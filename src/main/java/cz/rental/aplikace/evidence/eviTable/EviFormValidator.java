/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence.eviTable;

import cz.rental.aplikace.evidence.EviForm;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

/**
 *
 * @author sosyn
 */
@FacesValidator("eviFormValidator")
@SessionScoped
public class EviFormValidator implements Validator, Serializable {

    static final long serialVersionUID = 42L;
    @Inject
    EviForm eviForm;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage msg = null;
        String label = null;

        if (msg != null) {
            System.out.println("Chyba: " + msg.getSummary() + " " + msg.getDetail());
        } else if (eviForm == null) {
            System.out.println(" @Inject eviForm=null ");
            msg = new FacesMessage("System failed", "Nositel dat EviForm.java je prázdný. @Inject 'EviForm'=null.");
        } else if (component == null) {
            System.out.println(" Neznámá komponenta: ");
            msg = new FacesMessage("System failed", "Systémová chyba, neznámá komponenta. ");
        } else {
            if (HtmlInputText.class.isAssignableFrom(component.getClass())) {
                label = ((HtmlInputText) component).getLabel();
            } else if (HtmlSelectBooleanCheckbox.class.isAssignableFrom(component.getClass())) {
                label = ((HtmlSelectBooleanCheckbox) component).getLabel();
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Neznámá položka ke kontrole", component.getClientId(context));
            }

            if (label != null) {
                Throwable validateErr = eviForm.getScriptTools().validate(label, value, eviForm.getValues());
                if (validateErr != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Chyba hodnoty.", validateErr.getMessage());
                }
            }
        }
        // Vyhodit chybu, pokud je testovana polozka chybna
        if (msg != null) {
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(component == null ? null : component.getClientId(), msg);
            throw new ValidatorException(msg);
        }
    }
}
