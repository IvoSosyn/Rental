/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ivo
 */
@Named(value = "password")
@SessionScoped
public class Password implements Serializable {

    static final long serialVersionUID = 42L;
    private double passwordID = 0.0;

    private String password = null;
    private String passwordControl = null;
    private String passwordHelp = "";

    /**
     * Inicializace matice prav uzivatele
     *
     */
    @PostConstruct
    public void init() {
        this.passwordID = Math.random() * 1000;
        System.out.println(" init() Password.passwordID: " + this.passwordID);
    }

    /**
     * Metoda zavola dialog (samostatny .XHTML soubor) pro zadani hesla
     *
     * @param password
     * @param passwordControl
     * @param passwordHelp
     */
    public void editPassword(String password, String passwordControl, String passwordHelp) {
        this.password = password;
        this.passwordControl = passwordControl;
        this.passwordHelp = passwordHelp;

        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("width", 1000);
        options.put("height", 450);
        options.put("contentWidth", 1000);
        options.put("contentHeight", 450);
        options.put("closeOnEscape", true);
        // options.put("includeViewParams", true);
        PrimeFaces.current().dialog().openDynamic("/aplikace/password.xhtml", options, null);
    }

    /**
     * Metoda uzavre dialog se zadanim hesla - je volana ze samostatneho XHTML
     * souboru, ve kterem je dialog vzdy vraci null - nepresmerovava na jinou
     * strnku
     */
    public void savePassword() {
        if (this.passwordHelp == null || this.passwordHelp.isEmpty()) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN,"Neudali jste žádnou nápovědu k heslu.", "Nápověda k heslu by Vám v budoucnu mohla pomoci."));
        }
        PrimeFaces.current().dialog().closeDynamic(this);
    }


    /**
     * Metoda provede validaci hesla proti kontrolnimu heslu
     *
     * @param event Udalost s hodnotami
     */
    public void validatePassword(ValueChangeEvent event) {
        String newConfirmPass = (String) event.getNewValue();
        if (newConfirmPass == null || newConfirmPass.isEmpty() || this.password == null || !this.password.equals(newConfirmPass)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hesla se neshodují, opravte je prosím.", "");
            FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(), msg);
        }
    }

    public void passFromDialog(SelectEvent event) {
        SHA512.getSHA512(this.getPassword());
        String passwordHelp1 = this.passwordHelp;
    }

    /**
     * @return the Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the Password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the passwordControl
     */
    public String getPasswordControl() {
        return passwordControl;
    }

    /**
     * @param passwordControl the passwordControl to set
     */
    public void setPasswordControl(String passwordControl) {
        this.passwordControl = passwordControl;
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
     * @return the passwordID
     */
    public double getPasswordID() {
        return passwordID;
    }

    /**
     * @param passwordID the passwordID to set
     */
    public void setPasswordID(double passwordID) {
        this.passwordID = passwordID;
    }
}
