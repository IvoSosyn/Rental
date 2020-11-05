/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import cz.rental.entity.Account;
import cz.rental.entity.AccountController;
import cz.rental.utils.SHA512;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;

/**
 *
 * @author ivo
 */
@Named(value = "login")
@SessionScoped
public class Login implements Serializable {

    static final long serialVersionUID = 42L;

    @EJB
    private AccountController accController;
    @Inject
    private Ucet ucet;

    private Integer pin = 0;
    private String password = "";
    private String passwordHelp = "";
    private String email = "";

    public void handleLogin(ValueChangeEvent event) {
        // Po validaci hodnot formulare si kazdou novou hodnotu ulozim do prislusne promenne (takovy lokalni COMMIT)
        if (event.getComponent().getClientId().equalsIgnoreCase("formLogin:loginPIN")) {
            this.pin = (Integer) event.getNewValue();
        } else if (event.getComponent().getClientId().equalsIgnoreCase("formLogin:loginEmail")) {
            this.email = (String) event.getNewValue();
        } else if (event.getComponent().getClientId().equalsIgnoreCase("formLogin:loginPassword")) {
            this.password = (String) event.getNewValue();
        }
    }

    /**
     * Metoda najde DB zaznam pro PIN a E-mail a zobrazi passwordHELP - napovedu
     * k heslu
     */
    public void actionPassHelp() {
        String facesMessageSummary = "Nemohu pomoci.";
        this.passwordHelp = "Nemáte uloženu žádnou pomůcku/nápovědu pro Vaše heslo.";
// Nenacitaji se aktualne vlozene hodnoty         
//        Object loginPIN=((InputNumber)FacesContext.getCurrentInstance().getViewRoot().findComponent("formLogin:loginPIN")).getValue();
//        Object loginEmail=((InputText)FacesContext.getCurrentInstance().getViewRoot().findComponent("formLogin:loginEmail")).getValue();                
        Account acc = accController.getAccountForPinAndEmail(this.pin, this.email);
        if (acc instanceof Account && acc.getPasswordhelp() != null && !acc.getPasswordhelp().isEmpty()) {
            facesMessageSummary = "Vaše pomůcka/nápověda pro heslo je: ";
            this.passwordHelp = acc.getPasswordhelp();
        }
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(facesMessageSummary, this.passwordHelp));
        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(facesMessageSummary, this.passwordHelp));
    }

    /**
     * Metoda zkontroluje spravne heslo k PINu a E-mail identifikaci a pokud je
     * to OK, prejde na hlavni stranku evidence
     *
     * @return stránka, kam se má program presunout
     */
    public String actionLogin() {
        Account acc = accController.getAccountForPinAndEmail(this.pin, this.email);
        if (acc instanceof Account && acc.getPasswordsha512() != null && SHA512.getSHA512(this.password).equals(acc.getPasswordsha512())) {
//            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Heslo je OK."));
            return "aplikace/evidence/evidence.xhtml";
        } else {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Špatné heslo", "Nelze pokračovat, opravte stěžejní bezpečnostní údaj."));
            return null;
        }
    }

    public String actionRegistrace() {
        return "aplikace/registrace/regucet.xhtml";
    }

    /**
     * @return the pin
     */
    public Integer getPin() {
        return pin;
    }

    /**
     * @param pin the pin to set
     */
    public void setPin(Integer pin) {
        this.pin = pin;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the passwordHelp
     */
    public String getPasswordHelp() {
        return passwordHelp;
    }

    /**
     * @param passwordHelp the passwordHelp to set
     */
    public void setPasswordHelp(String passwordHelp) {
        this.passwordHelp = passwordHelp;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
