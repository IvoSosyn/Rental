/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

import cz.rental.aplikace.evidence.Evidence;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
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
@FacesValidator("ucetValidator")
@SessionScoped
public class UcetValidator implements Validator, Serializable {

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
            msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,"System failed", "Systémová chyba, neznámá komponenta. ");
        } else if (component.getClientId().contains("idAccEmail") || component.getClientId().contains("idUserEmail") ) {
            Pattern r = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
            Matcher m = r.matcher((String) value);
            if (!m.matches()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Nesprávný formát e-mailové adresy.", "E-mail identifikuje uživatele a je to povinný údaj.");
            }
        }
        // Vyhodit chybu, pokud je testovana polozka chybna
        if (msg != null) {
            // msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            // FacesContext.getCurrentInstance().addMessage(component == null ? null : component.getClientId(), msg);
            throw new ValidatorException(msg);
        }
    }
}
