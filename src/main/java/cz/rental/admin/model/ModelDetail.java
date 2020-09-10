/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.entity.Attribute;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.naming.InitialContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 * Trida je back-end tabulky "Attribute" pro zadany "Typetnity"
 *
 * @author sosyn
 */
@Stateless
@Named("modelDetail")
public class ModelDetail implements Validator {

    private Attribute attribute = null;

    @EJB
    cz.rental.entity.AttributeController controller;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    public void onRowSelect(SelectEvent event) {
        setAttribute((Attribute) event.getObject());

    }

    public void onRowUnselectSelect(UnselectEvent event) {
        setAttribute(null);
    }

    public Boolean isEditable() {
        return (this.attribute instanceof Attribute);
    }

    public boolean isValueUsed(String alias, String whereCondition) {
        boolean ret = false;
//        System.out.println(" Selected this.attribute.getId()="+this.attribute.getId());
//        System.out.println(" Selected this.attribute.getIdtypentity()="+this.attribute.getIdtypentity());
        ArrayList<Attribute> attrs = controller.getAttributeWhere(this.attribute.getIdtypentity(), alias, whereCondition);
        for (Attribute attr : attrs) {
            if (!attr.getId().equals(this.attribute.getId())) {
//                System.out.println("  attr.getId()="+attr.getId());
                ret = true;
                break;
            }
        }
        return ret;
    }

    /**
     * Metoda ulozi aktualni Attrtibute do DB
     *
     */
    public void saveAttribute() {
        try {
            if (this.attribute.isNewEntity()) {
                this.controller.create(this.attribute);
            } else {
                this.controller.edit(this.attribute);
            }
            this.attribute = (Attribute) this.controller.findEntita(this.attribute);
            ModelTable modelTable = (ModelTable) InitialContext.doLookup("java:module/ModelTable");
            modelTable.loadAttributeForTypentity(modelTable.getTypentity());
        } catch (Exception ex) {
            Logger.getLogger(ModelDetail.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessage msg = new FacesMessage("System failed", "Systémová chyba, nepodařilo se uložit položku do databáze, zkuste později. ");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage msg = null;

        this.attribute = this.getAttribute();
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
            } else if (this.isValueUsed("a", "a.attrname='" + ((String) value).trim() + "'")) {
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
