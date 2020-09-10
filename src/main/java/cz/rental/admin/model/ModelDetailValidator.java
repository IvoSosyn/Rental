/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.entity.Attribute;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author sosyn
 */
@FacesValidator("_modelDetailValidator")

public class ModelDetailValidator implements Validator {

//    @EJB
    ModelDetail modelDetail;
    Attribute attribute;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage msg = null;

        try {
            modelDetail = (ModelDetail) InitialContext.doLookup("java:module/ModelDetail");
        } catch (NamingException ex) {
            msg = new FacesMessage("System failed", "Systémová chyba, nepodařilo se najít modul 'ModelDetail'. ");
        }

        this.attribute = modelDetail.getAttribute();
        if (msg != null) {
            System.out.println("Chyba: " + msg.getSummary() + " " + msg.getDetail());
        } else if (component == null) {
            System.out.println(" Neznámá komponenta: ");
            msg = new FacesMessage("System failed", "Systémová chyba, neznámá komponenta. ");
        } else if (component.getClientId().contains("poradi")) {
            if (value == null || (Integer) value < 1) {
                msg = new FacesMessage("Validation failed", "Položka nesmí být menší než 1.");
            }
        } else if (component.getClientId().contains("attrname")) {
            if (value == null || value.toString().trim().length() < 2) {
                msg = new FacesMessage("Validation failed", "Položka nesmí být kratší než 2 znaky.");
            } else if (value.toString().contains("test")) {
                msg = new FacesMessage("Validation failed", "Položka nesmí mít hodnotu 'test'.");
            } else if (modelDetail.isValueUsed("a", "a.attrname='" + ((String) value).trim() + "'")) {
                msg = new FacesMessage("Validation failed", "Položka je již použita, musí být jedinečná.");
            }
        } else if (component.getClientId().contains("popis")) {
        } else if (component.getClientId().contains("attrtype")) {
            System.out.println(" attrtype=" + value);
        } else if (component.getClientId().contains("attrsize")) {
            if (value == null || ((java.math.BigInteger) value).doubleValue() < 1) {
                msg = new FacesMessage("Validation failed", "Položka nesmí být menší než 1.");
            }
        } else if (component.getClientId().contains("attrdecimal")) {
            if (value == null || ((java.math.BigInteger) value).doubleValue() < 0) {
                msg = new FacesMessage("Validation failed", "Položka nesmí být menší než 0.");
            }
        } else if (component.getClientId().contains("attrparser")) {
        } else if (component.getClientId().contains("attrmask")) {
        } else if (component.getClientId().contains("script")) {
        } else if (component.getClientId().contains("tables")) {
        } else if (component.getClientId().contains("platiod")) {
        } else if (component.getClientId().contains("platido")) {
        }
        // Vyhodit chybu, pokud je testovana polozka chybna
        if (msg != null) {
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(component == null ? null : component.getClientId(), msg);
            throw new ValidatorException(msg);
        }
    }
}
