/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.el.ValueExpression;
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
@FacesValidator("accountValidator")
@Stateless
public class AccountValidator implements Validator, Serializable {

    static final long serialVersionUID = 42L;

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
        } else if (component.getClientId().contains("idAccName")) {
        } else if (component.getClientId().contains("idAccAddress")) {
        } else if (component.getClientId().contains("platiod")) {
            ValueExpression vex
                    = context.getApplication().getExpressionFactory()
                            .createValueExpression(context.getELContext(),
                                    "#{modelDetail.attribute.platido}", Date.class);
            Date platiDo = (Date) vex.getValue(context.getELContext());

        } else if (component.getClientId().contains("platido")) {
            ValueExpression vex
                    = context.getApplication().getExpressionFactory()
                            .createValueExpression(context.getELContext(),
                                    "#{modelDetail.attribute.platiod}", Date.class);
            Date platiOd = (Date) vex.getValue(context.getELContext());
        }
        // Vyhodit chybu, pokud je testovana polozka chybna
        if (msg != null) {
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(component == null ? null : component.getClientId(), msg);
            throw new ValidatorException(msg);
        }
    }

}
