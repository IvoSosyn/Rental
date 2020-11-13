/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.utils.Aplikace;
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
@FacesValidator("obdobiValidator")
@SessionScoped
public class ObdobiValidator implements Validator, Serializable {

    static final long serialVersionUID = 42L;

    Evidence evidence;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {

    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage msg = null;

        if (msg != null) {
            System.out.println("Chyba: " + msg.getSummary() + " " + msg.getDetail());
        } else if (component == null) {
            System.out.println(" Neznámá komponenta: ");
            msg = new FacesMessage("System failed", "Systémová chyba, neznámá komponenta. ");
        } else if (component.getClientId().contains("ucetPlatiOD")) {
            ValueExpression vex
                    = context.getApplication().getExpressionFactory()
                            .createValueExpression(context.getELContext(),
                                    "#{evidence.platiDo}", Date.class);
            Date platiDo = (Date) vex.getValue(context.getELContext());
            if (value != null && platiDo != null && platiDo.before((Date) value)) {
                vex.setValue(context.getELContext(), value);
            }
        } else if (component.getClientId().contains("ucetPlatiDO")) {
            ValueExpression vex
                    = context.getApplication().getExpressionFactory()
                            .createValueExpression(context.getELContext(),
                                    "#{evidence.platiOd}", Date.class);
            Date platiOd = (Date) vex.getValue(context.getELContext());
            if (value != null && platiOd != null && platiOd.after((Date) value)) {
                msg = new FacesMessage("Chyba rozsahu platnosti", "Datum konce rozsahu DO  musí být větší než : " + Aplikace.getSimpleDateFormat().format(platiOd));
                // vex.setValue(context.getELContext(), value);
            }
        } else if (component.getClientId().contains("ucetZmenaOD")) {
            ValueExpression vex
                    = context.getApplication().getExpressionFactory()
                            .createValueExpression(context.getELContext(),
                                    "#{evidence.platiOd}", Date.class);
            Date platiOd = (Date) vex.getValue(context.getELContext());

            vex = context.getApplication().getExpressionFactory()
                    .createValueExpression(context.getELContext(),
                            "#{evidence.platiDo}", Date.class);
            Date platiDo = (Date) vex.getValue(context.getELContext());
            if (value != null && ((platiOd != null && platiOd.after((Date) value)) || (platiDo != null && platiDo.before((Date) value)))) {
                msg = new FacesMessage("Chyba rozsahu platnosti", "Datum zpracovani musi být v rozsahu OD - DO: " + Aplikace.getSimpleDateFormat().format(platiOd) + " - " + Aplikace.getSimpleDateFormat().format(platiDo));
                // vex.setValue(context.getELContext(), value);
            }
        }
        // Vyhodit chybu, pokud je testovana polozka chybna
        if (msg != null) {
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            // FacesContext.getCurrentInstance().addMessage(component == null ? null : component.getClientId(), msg);
            throw new ValidatorException(msg);
        }
    }
}
