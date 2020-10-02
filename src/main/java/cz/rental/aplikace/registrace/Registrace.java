/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Init;
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

    @PostConstruct
    public void init() {

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
        boolean isEnable = true && !FacesContext.getCurrentInstance().isValidationFailed() && !this.account.getCustomerEmail().isEmpty();
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
        try {
            createAccountDir();
        } catch (IOException ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při zakládání souborů a adresářů k účtu. Opakujte později.", ex.getMessage()));
        }
        createAccountHTML();
        //return "/admin/model/model.xhtml";
        return (account.getCustomerDir()+File.separator+"index.xhtml").replace('\\', '/');
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

    private void createAccountDir() throws IOException {
        if (this.account.getCustomerID() == null) {
            return;
        }
        URL url = Registrace.class.getResource("");
        int web_inf = url.getPath().indexOf("WEB-INF");
        String rootDirName = "c:\\Program Files\\wildfly-20.0.0.Final\\standalone\\deployments\\Rental-Develop.war\\customers" + File.separator + this.account.getCustomerID().toString();
        account.setCustomerDir(rootDirName);

        File rootDir = new File(rootDirName);
        rootDir.mkdir();

//        rootDirName += File.separator + "html";
//        rootDir = new File(rootDirName);
//        rootDir.mkdir();
        rootDirName +=  File.separator+"index.xhtml";
        rootDir = new File(rootDirName);
        rootDir.createNewFile();

    }

    private void createAccountHTML() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(account.getCustomerDir() + File.separator+  "index.xhtml");
            pw.write("");
            pw.write("<?xml version='1.0' encoding='UTF-8' ?>");
            pw.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            pw.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
            pw.write("xmlns:ui=\"http://xmlns.jcp.org/jsf/facelets\"");
            pw.write("xmlns:h=\"http://xmlns.jcp.org/jsf/html\"");
            pw.write("xmlns:f=\"http://xmlns.jcp.org/jsf/core\"");
            pw.write("xmlns:p=\"http://primefaces.org/ui\">");
//            pw.write("<ui:composition>");
            pw.write("<h:form  id=\"idCustomer\" >");
            pw.write("<p:panelGrid columns=\"3\" >");
            pw.write("<f:facet name=\"header\">");
            pw.write("<p>Údaje o platbě</p>");
            pw.write("</f:facet>");
            pw.write("<div> Není zpřístupněno, po dobu testování je přístup zdarma.</div>");
            pw.write("</p:panelGrid>");
            pw.write("</h:form>");
//            pw.write("</ui:composition>	");

            pw.write("</html>");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (pw != null) {
            pw.close();
        }
    }

}
