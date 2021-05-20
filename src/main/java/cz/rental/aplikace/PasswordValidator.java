/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import cz.rental.entity.AccountController;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.primefaces.component.password.Password;

/**
 *
 * @author sosyn
 */
@FacesValidator("cz.rental.aplikace.passwordValidator")
@SessionScoped
public class PasswordValidator implements Validator, Serializable {

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
        } else if (component.getClientId().contains("idPassword")) {
            if (value == null || ((String) value).isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Heslo je povinný údaj.", "Doplňte hodnotu prosím - minimálně 4 znaky.");
            } else if (((String) value).trim().length() < 4) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Heslo musí mít minimálně 4 znaky.", "Doplňte hodnotu prosím - minimálně 4 znaky.");
            }
        } else if (component.getClientId().contains("idPassConfirm")) {
            Password passUIC = findPassUIC(FacesContext.getCurrentInstance().getViewRoot().getChildren(), "formPass:idPassword");
            if (passUIC == null  || passUIC.getValue()==null) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Heslo je povinný údaj.", "Doplňte hodnotu prosím - minimálně 4 znaky.");
            } else if (value == null || !((String) value).equals(passUIC.getValue()) ) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hesla se neshodují.", "Opravte je prosím.");
            }

        } else if (component.getClientId().contains("idPassHelp")) {
            if (value == null || ((String) value).isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Neudali jste žádnou nápovědu k heslu.", "Nápověda k heslu by Vám v budoucnu mohla pomoci.");
            }
        }
        // Vyhodit chybu, pokud je testovana polozka chybna
        if (msg != null && msg.getSeverity()!=FacesMessage.SEVERITY_INFO && msg.getSeverity()!=FacesMessage.SEVERITY_WARN) {
            // FacesContext.getCurrentInstance().addMessage(component == null ? null : component.getClientId(), msg);
            throw new ValidatorException(msg);
        }
    }

    private Password findPassUIC(List<UIComponent> children, String formPassidPassword) {
        Password uICpass = null;
        for (UIComponent uIComponent : children) {
            if (uIComponent.getClientId().equalsIgnoreCase(formPassidPassword)) {
                uICpass = (Password) uIComponent;
                break;
            } else {
                if ((uICpass = findPassUIC(uIComponent.getChildren(), formPassidPassword)) != null) {
                    break;
                }
            }
        }
        return uICpass;
    }

}
