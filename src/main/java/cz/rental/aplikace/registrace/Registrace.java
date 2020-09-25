/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.PrimeFacesContext;

/**
 *
 * @author ivo
 */
@Named(value = "registrace")
@Stateless
public class Registrace {


    @Inject
    private Account account;

    private static final String XHTML_REGISTRACE_FILE = "/aplikace/registrace/regStep";
    private int selectedStep = 0;

    /**
     * Metoda rozhoduje, zda-li je nebo neni mozne vybrat "<p:MenuItem>" ze
     * "<p:Step>" menu POZOR - rozhoduje o tom samostatne "<p:Step>" menu
     *
     * @param seletedStep hodnoceny "<p:MenuItem>" ze "<p:Step>" menu
     * registracniho formulare
     * @return vzdy "true", protoze o tom rozhoduje samostatne "<p:Step>" menu
     */
    public boolean isRegistracniPanelEnable(int seletedStep) {
        boolean isEnable = true;
        return isEnable;
    }

    /**
     * Metoda nastavi-prepne na panel z parametru <code>seletedStep</code>
     *
     * @param seletedStep vybrany "<p:MenuItem>" ze "<p:Step>" menu
     * registracniho formulare
     */
    public void changeRegistracniPanel(int seletedStep) {
        this.setSelectedStep(seletedStep);
    }

    public String regPanel() {
        return XHTML_REGISTRACE_FILE + getSelectedStep() + ".xhtml";
    }

    public boolean isBackEnable() {
        boolean isEnable = this.getSelectedStep() > 0 && !FacesContext.getCurrentInstance().isValidationFailed();
        FacesContext.getCurrentInstance().isValidationFailed();
        return isEnable;
    }

    public boolean isBackRendered() {
        boolean isEnable = this.getSelectedStep() > 0;
        return isEnable;
    }

    public void backRegPanel() {
        boolean nextRegPanel = true;
        if (nextRegPanel && this.getSelectedStep() > 0) {
            this.setSelectedStep(this.getSelectedStep() - 1);
        }
    }

    public boolean isNextEnable() {
        boolean isEnable = true && !FacesContext.getCurrentInstance().isValidationFailed();;
        FacesContext.getCurrentInstance().isValidationFailed();
        return isEnable;
    }

    public boolean isNextRendered() {
        boolean isEnable = this.getSelectedStep() < 3;
        return isEnable;
    }

    public void nextRegPanel() {
        boolean nextRegPanel = true;
        if (nextRegPanel && this.getSelectedStep() < 3) {
            this.setSelectedStep(this.getSelectedStep() + 1);
        }
    }

    public String createAccount() {
        try {
            account.saveAccount();
        } catch (Exception ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při ukládání účtu. Opakujte později.", ex.getMessage()));
        }
        createAccountDir();
        createAccountHTML();
        return "/admin/model/model.xhtml";
    }

    /**
     * @return the selectedStep
     */
    public int getSelectedStep() {
        return selectedStep;
    }

    /**
     * @param selectedStep the selectedStep to set
     */
    public void setSelectedStep(int selectedStep) {
        this.selectedStep = selectedStep;
    }

    /**
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    private void saveAccount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void createAccountDir() {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void createAccountHTML() {
      //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

}
