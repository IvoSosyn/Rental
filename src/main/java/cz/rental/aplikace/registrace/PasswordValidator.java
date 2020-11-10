/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

import java.io.Serializable;
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
@FacesValidator("passwordValidator")
@SessionScoped
public class PasswordValidator implements Validator, Serializable {

    static final long serialVersionUID = 42L;

    static final int MINIMAL_PASSWORD_LENGTH = 4;

    /// @Inject
    Registrace registrace;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage msg = null;

//        try {
//            registrace = (Registrace) InitialContext.doLookup("java:module/Registrace");
////            System.out.println("*ModelDetailValidator.validate  modelDetail.getAttribute().getId()=" + modelDetail.getAttribute().getId());
////            System.out.println("*ModelDetailValidator.validate  modelDetail.getAttribute().getPoradi()=" + modelDetail.getAttribute().getPoradi());
////            System.out.println("*ModelDetailValidator.validate  modelDetail.getAttribute().getAttrname()=" + modelDetail.getAttribute().getAttrname());
////            System.out.println("*ModelDetailValidator.validate  modelDetail.getAttribute().getPopis()=" + modelDetail.getAttribute().getPopis());
////            System.out.println("*ModelDetailValidator.validate                                  value=" + value);
//
//        } catch (NamingException ex) {
//            msg = new FacesMessage("System failed", "Systémová chyba, nepodařilo se najít modul 'Registrace'. ");
//        }
        if (msg != null) {
            System.out.println("Chyba: " + msg.getSummary() + " " + msg.getDetail());
        } else if (component == null) {
            msg = new FacesMessage("System failed", "Systémová chyba, neznámá komponenta.");
        } else if (component.getId().equals("password")) {
            if (value == null || ((String) value).isEmpty() || ((String) value).trim().length() < MINIMAL_PASSWORD_LENGTH) {
                msg = new FacesMessage("Chybné heslo.", "Heslo musí mít minimálně " + MINIMAL_PASSWORD_LENGTH + " znaky.");
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
