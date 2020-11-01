/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

import cz.rental.aplikace.Ucet;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.PrimeFacesContext;

/**
 *
 * @author ivo
 */
@Named(value = "registrace")
@Stateless
public class Registrace {

    @EJB
    private Ucet ucet;

    private static final String XHTML_REGISTRACE_FILE = "/aplikace/registrace/regStep";
    private int selectedStep = 0;

    @PostConstruct
    public void init() {
        if (!(ucet instanceof Ucet)) {
            System.out.println(" Registrace.ucet==null");
        }
    }

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

    public String includeRegPanel() {
        return XHTML_REGISTRACE_FILE + getSelectedStep() + ".xhtml";
    }

    public boolean isBackEnable() {
        boolean isEnable = this.getSelectedStep() > 0 && !FacesContext.getCurrentInstance().isValidationFailed();
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
        boolean isEnable = true && !FacesContext.getCurrentInstance().isValidationFailed() && !this.ucet.getAccount().getEmail().isEmpty();
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
        boolean isOk = true;
        // Uloz registraci uctu
        try {
            this.ucet.saveAccount();
        } catch (Exception ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při ukládání účtu. Opakujte později.", ex.getMessage()));
            isOk = false;
        }
        try {
            this.ucet.saveUzivatel();
        } catch (Exception ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při aktualizaci záznamu o uživateli. Opakujte později.", ex.getMessage()));
            isOk = false;
        }
        // Zaloz adresare k uctu a vstupni 'index.xhtml'
        try {
            this.ucet.createAccountDir();
        } catch (Exception ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při zakládání souborů a adresářů k účtu. Opakujte později.", ex.getMessage()));
            isOk = false;
        }
        // Napln XHTML soubory daty ze sablony
        try {
            this.ucet.createAccountHTML();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při plnění a konfiguraci souborů k účtu. Opakujte později.", ex.getMessage()));
            isOk = false;
        }
        if (isOk) {
            return "/aplikace/evidence/evidence.xhtml";
        } else {
            // Pri neuspechu vratit na zcatek registrace
            return "/aplikace/registrace/registrace.xhtml";
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

    public boolean isPassBtnSaveEnabled() {
        boolean isEnable = !FacesContext.getCurrentInstance().isValidationFailed()
                && this.ucet.getPassword() != null && !this.ucet.getPassword().isEmpty()
                && this.ucet.getPasswordControl() != null && !this.ucet.getPasswordControl().isEmpty()
                && this.ucet.getPassword().equals(this.ucet.getPasswordControl());

        return isEnable;

    }

    /**
     * Metoda vraci kratkou infomaci o naplneni Account.passwordsha512
     *
     * @return hlaseni
     */
    public String passwordMessage() {
        if (this.ucet.getAccount().getPasswordsha512() instanceof String && !this.ucet.getAccount().getPasswordsha512().isEmpty()) {
            if (this.ucet.getAccount().getPasswordhelp() instanceof String && !this.ucet.getAccount().getPasswordhelp().isEmpty()) {
                return "Nápověda hesla: " + this.ucet.getAccount().getPasswordhelp().trim();
            } else {
                return "Máte uložené heslo.";
            }
        }
        return "Heslo NENÍ definované";
    }

    /**
     * @return the ucet
     */
    public Ucet getUcet() {
        return ucet;
    }

    /**
     * @param ucet the ucet to set
     */
    public void setUcet(Ucet ucet) {
        this.ucet = ucet;
    }
}
