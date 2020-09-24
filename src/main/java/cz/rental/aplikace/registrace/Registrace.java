/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;

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
        boolean isEnable = this.getSelectedStep() > 0;
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
        boolean isEnable = true;
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
    
    
    
    
    
    
    public void changePassword() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        PrimeFaces.current().dialog().openDynamic("/aplikace/registrace/password.xhtml", options, null);
        System.out.println("PrimeFaces.current().dialog().openDynamic");
        
    }

    public void closePassword(String password) {
        PrimeFaces.current().dialog().closeDynamic(password);
        System.out.println("PrimeFaces.current().dialog().closeDynamic");
    }

    public void newPassword(Object event) {
        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "What we do in life", "Echoes in eternity."));
    }

    
    
}
