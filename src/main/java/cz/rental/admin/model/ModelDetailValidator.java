/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author sosyn
 */
@FacesValidator("modelDetailValidator")
public class ModelDetailValidator implements Validator {

    @EJB
    ModelDetail modelDetail;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage msg = null;
        if (component == null) {
        } else if (component.getClientId().contains("poradi")) {
            if (value == null || (Integer) value < 1) {
                msg = new FacesMessage("Validation failed", "Položka nesmí být menší než 1.");
            }
        } else if (component.getClientId().contains("attrname")) {
            if (value == null || value.toString().trim().length() < 3) {
                msg = new FacesMessage("Validation failed", "Položka nesmí být kratší než 3 znaky.");
            }
            if (value.toString().contains("test")) {
                msg = new FacesMessage("Validation failed", "Položka nesmí mít hodnotu 'test'.");
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
            FacesContext.getCurrentInstance().addMessage(component.getClientId(), msg);
            throw new ValidatorException(msg);
        }
    }
}
