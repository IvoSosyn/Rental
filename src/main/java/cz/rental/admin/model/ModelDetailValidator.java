/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.entity.Attribute;
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

/**
 *
 * @author sosyn
 */
@FacesValidator("modelDetailValidator")
@SessionScoped
public class ModelDetailValidator implements Validator, Serializable {

    static final long serialVersionUID = 42L;
    //@Inject
    ModelDetail modelDetail;
    private Attribute attribute;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage msg = null;
        ValueExpression vex
                = context.getApplication().getExpressionFactory()
                        .createValueExpression(context.getELContext(),
                                "#{modelDetail}", ModelDetail.class);
        modelDetail = (ModelDetail) vex.getValue(context.getELContext());

        /**
         * *********
         * try { modelDetail = (ModelDetail)
         * InitialContext.doLookup("java:module/ModelDetail");
         * this.setAttribute(modelDetail.getAttribute()); //
         * System.out.println("*ModelDetailValidator.validate
         * modelDetail.getAttribute().getId()=" +
         * modelDetail.getAttribute().getId()); //
         * System.out.println("*ModelDetailValidator.validate
         * modelDetail.getAttribute().getPoradi()=" +
         * modelDetail.getAttribute().getPoradi()); //
         * System.out.println("*ModelDetailValidator.validate
         * modelDetail.getAttribute().getAttrname()=" +
         * modelDetail.getAttribute().getAttrname()); //
         * System.out.println("*ModelDetailValidator.validate
         * modelDetail.getAttribute().getPopis()=" +
         * modelDetail.getAttribute().getPopis()); //
         * System.out.println("*ModelDetailValidator.validate value=" + value);
         *
         * } catch (NamingException ex) { msg = new FacesMessage("System
         * failed", "Systémová chyba, nepodařilo se najít modul 'ModelDetail'.
         * "); } *************************
         */
        if (msg != null) {
            System.out.println("Chyba: " + msg.getSummary() + " " + msg.getDetail());
        } else if (component == null) {
            System.out.println(" Neznámá komponenta: ");
            msg = new FacesMessage("System failed", "Systémová chyba, neznámá komponenta. ");
        } else if (value == null) {
            // Nothing to control
        } else if (component.getClientId().contains("poradi")) {
            if ((Integer) value < 1) {
                msg = new FacesMessage("Validation failed", "Položka nesmí být menší než 1.");
            }
        } else if (component.getClientId().contains("attrname")) {
            if (value.toString().trim().length() < 2) {
                msg = new FacesMessage("Validation failed", "Položka nesmí být kratší než 2 znaky.");
            } else if (value.toString().contains("test")) {
                msg = new FacesMessage("Validation failed", "Položka nesmí mít hodnotu 'test'.");
            } else if (modelDetail.isValueUsed("a", "a.attrname='" + ((String) value).trim() + "'")) {
                msg = new FacesMessage("Validation failed", "Položka je již použita, musí být jedinečná.");
            }
        } else if (component.getClientId().contains("popis")) {
        } else if (component.getClientId().contains("attrtype")) {
        } else if (component.getClientId().contains("attrsize")) {
            if (((java.math.BigInteger) value).intValue() < 1) {
                msg = new FacesMessage("Validation failed", "Položka nesmí být menší než 1.");
            }
        } else if (component.getClientId().contains("attrdecimal")) {
            if (((java.math.BigInteger) value).intValue() < 0) {
                msg = new FacesMessage("Validation failed", "Položka nesmí být menší než 0.");
            }
        } else if (component.getClientId().contains("attrparser")) {
        } else if (component.getClientId().contains("attrmask")) {
        } else if (component.getClientId().contains("script")) {
        } else if (component.getClientId().contains("tables")) {
        } else if (component.getClientId().contains("platiod")) {
            vex = context.getApplication().getExpressionFactory()
                    .createValueExpression(context.getELContext(),
                            "#{modelDetail.attribute.platido}", Date.class);
            Date platiDo = (Date) vex.getValue(context.getELContext());
            if (platiDo != null && platiDo.before((Date) value)) {
                attribute.setPlatido((Date) value);
            }
        } else if (component.getClientId().contains("platido")) {
            vex = context.getApplication().getExpressionFactory()
                    .createValueExpression(context.getELContext(),
                            "#{modelDetail.attribute.platiod}", Date.class);
            Date platiOd = (Date) vex.getValue(context.getELContext());
            if (platiOd != null && platiOd.after((Date) value)) {
                attribute.setPlatiod((Date) value);
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
}
