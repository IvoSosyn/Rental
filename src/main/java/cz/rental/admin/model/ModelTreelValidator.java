/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.entity.Typentity;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

/**
 *
 * @author sosyn
 */
@FacesValidator("modelTreeValidator")
@SessionScoped
public class ModelTreelValidator implements Validator, Serializable {

    static final long serialVersionUID = 42L;
    @Inject
    ModelTree modelTree;
    private Typentity typentity;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage msg = null;

//        try {
//            modelTree = (ModelTree) InitialContext.doLookup("java:module/ModelTree");
//            this.setTypentity(modelTree.getTypentity());
//            System.out.println("*ModelTreeValidator.validate  modelTree.getAttribute().getId()=" + modelTree.getTypentity().getId());
//            System.out.println("*ModelTreeValidator.validate  modelTree.getAttribute().getAttrname()=" + modelTree.getTypentity().getTypentity());
//            System.out.println("*ModelTreeValidator.validate  modelTree.getAttribute().getPopis()=" + modelTree.getTypentity().getPopis());
//            System.out.println("*ModelTreeValidator.validate                                  value=" + value);
//
//        } catch (NamingException ex) {
//            msg = new FacesMessage("System failed", "Systémová chyba, nepodařilo se najít modul 'ModelTree'. ");
//        }

        if (msg != null) {
            System.out.println("Chyba: " + msg.getSummary() + " " + msg.getDetail());
        } else if (component == null) {
            System.out.println(" Neznámá komponenta: ");
            msg = new FacesMessage("System failed", "Systémová chyba, neznámá komponenta. ");
        } else if (component.getClientId().contains("idTypentity")) {
        } else if (component.getClientId().contains("idPopis")) {
        } else if (component.getClientId().contains("idEditor")) {
        } else if (component.getClientId().contains("platiOd")) {
            ValueExpression vex
                    = context.getApplication().getExpressionFactory()
                            .createValueExpression(context.getELContext(),
                                    "#{modelTree.typentity.platido}", Date.class);
            Date platiDo = (Date) vex.getValue(context.getELContext());
            if (value != null && platiDo != null && platiDo.before((Date) value)) {
                typentity.setPlatido((Date) value);
            }
        } else if (component.getClientId().contains("platiDo")) {
            ValueExpression vex
                    = context.getApplication().getExpressionFactory()
                            .createValueExpression(context.getELContext(),
                                    "#{modelTree.typentity.platiod}", Date.class);
            Date platiOd = (Date) vex.getValue(context.getELContext());
            if (value != null && platiOd != null && platiOd.after((Date) value)) {
                typentity.setPlatiod((Date) value);
            }
        }
        // Vyhodit chybu, pokud je testovana polozka chybna
        if (msg != null) {
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(component == null ? null : component.getClientId(), msg);
            throw new ValidatorException(msg);
        }
    }

    /**
     * @return the typentity
     */
    public Typentity getTypentity() {
        return typentity;
    }

    /**
     * @param typentity the typentity to set
     */
    public void setTypentity(Typentity typentity) {
        this.typentity = typentity;
    }
}
