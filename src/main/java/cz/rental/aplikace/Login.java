/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import cz.rental.aplikace.registrace.Registrace;
import cz.rental.entity.Account;
import cz.rental.entity.AccountController;
import cz.rental.entity.User;
import cz.rental.entity.UserController;
import cz.rental.utils.SHA512;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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
    @EJB
    private UserController userController;
    @Inject
    private Ucet ucet;
    @Inject
    private Registrace registrace;

    private Integer pin = 0;
    private String email = null;
    private String password = null;
    private String passwordHelp = null;

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
        User user = userController.getUserForPinAndEmail(this.pin, this.email);
        if (user instanceof User && user.getPasswordhelp() != null && !user.getPasswordhelp().isEmpty()) {
            facesMessageSummary = "Vaše pomůcka/nápověda pro heslo je: ";
            this.passwordHelp = user.getPasswordhelp();
        }
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(facesMessageSummary, this.passwordHelp));
        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessageSummary, this.passwordHelp));
    }

    /**
     * Metoda zkontroluje spravne heslo k PINu a E-mail identifikaci a pokud je
     * to OK, prejde na hlavni stranku evidence
     *
     * @return stránka, kam se má program presunout
     */
    public String actionLogin() {
        Account acc = accController.getAccountForPinAndEmail(this.pin, this.email);
        User usr = userController.getUserForAccount(acc, email, SHA512.getSHA512(this.password));
        if (acc instanceof Account && usr instanceof User) {
//            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Heslo je OK."));
            this.ucet.setAccount(acc);
            this.ucet.getUzivatel().initUzivatelByUser(usr);
            return "aplikace/evidence/evidence.xhtml";
        } else {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Špatné heslo", "Nelze pokračovat, opravte stěžejní bezpečnostní údaj."));
            return null;
        }
    }

    public void exitApp() {
        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Nenaprogramováno.", "Nenaprogramováno."));
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
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
