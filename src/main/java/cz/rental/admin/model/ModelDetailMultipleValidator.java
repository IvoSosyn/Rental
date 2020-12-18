/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.omnifaces.validator.MultiFieldValidator;

/**
 *
 * @author sosyn
 */
@Named(value = "modelDetailMultipleValidator")
@SessionScoped
public class ModelDetailMultipleValidator implements MultiFieldValidator, Serializable {

    static final long serialVersionUID = 42L;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    @Override
    public boolean validateValues(FacesContext context, List<UIInput> components, List<Object> values) {
        boolean isValidate = true;
        FacesMessage msg = null;
        for (UIInput component : components) {
            if (!component.isValid()) {
                msg=new FacesMessage("Chyba položky:",component.getValidatorMessage());
                break;
            }
        }
        if (msg != null) {
            System.out.println("Chyba: " + msg.getSummary() + " " + msg.getDetail());
        }
        return isValidate;
    }
}
