/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import cz.rental.entity.AccountController;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
@FacesValidator("loginValidator")
public class LoginValidator implements Validator, Serializable {

    static final long serialVersionUID = 42L;

    @EJB
    private AccountController accController;

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
        } else if (component.getClientId().contains("loginPIN")) {
            
            if ((Integer) value < 100 || (Integer) value > 9999) {
                msg = new FacesMessage("Nesprávný PIN tj. identifikátor účtu.", "Přidělený PIN musí být v rozmezí 100-9999.");
            }else if(accController.getAccountForPIN((Integer) value)==null){
                msg = new FacesMessage("PIN neexistuj", String.format("Zadaný PIN: %1$d NEEXISTUJE.",value));
            }
            
        } else if (component.getClientId().contains("loginEmail")) {
            
            Pattern r = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
            Matcher m = r.matcher((String) value);
            if (!m.matches()) {
                msg = new FacesMessage("Nesprávný formát e-mailové adresy.", "E-mail identifikuje uživatele a je to povinný údaj.");
            }
        } else if (component.getClientId().contains("loginPassword")) {
            
            if (value == null || ((String) value).isEmpty()) {
                msg = new FacesMessage("Heslo nesmí být prázdné.", "Heslo je povinné.");
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
