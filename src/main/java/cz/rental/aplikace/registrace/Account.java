/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

import cz.rental.aplikace.User;
import cz.rental.entity.AccountController;
import cz.rental.entity.Typentity;
import cz.rental.utils.SHA512;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.PostConstruct;

/**
 *
 * @author ivo
 */
@Named(value = "account")
@Stateful
public class Account {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private AccountController controller;
    @Inject
    private cz.rental.aplikace.User user;

    final static String ACCOUNT_ROOT_DIR = File.separator + "Rental" + File.separator + "Accounts";

    private UUID customerID = null;
    private String customerName = "";
    private String customerEmail = "";
    private String customerTel = "";
    private String customerPassword = "";
    private String customerPasswordControl = "";
    private String customerPasswordHelp = "";
    private String customerPasswordSHA512 = SHA512.hash512("");
    private int customerPin = 0;
    private String customerAddress = "";
    private Typentity customerModel = null;
    private String accountsRootDir = Account.ACCOUNT_ROOT_DIR;
    private String accountDir = null;

    @PostConstruct
    public void init() {
        // Nacist root dir pro soubory uctu, pokud je zadan ve WEB.XML  napr. na Linuxu
        //  <context-param>
        //      <param-name>cz.rental.accounts.root.dir</param-name>
        //      <param-value>/home/Rental/Accounts</param-value>
        //  </context-param>
        if (FacesContext.getCurrentInstance().getExternalContext().getInitParameter("cz.rental.accounts.root.dir") != null) {
            this.accountsRootDir = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("cz.rental.accounts.root.dir");
        }
        accountDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/account");
//        Set<String> setRes=FacesContext.getCurrentInstance().getExternalContext().getResourcePaths("/account");
//        System.out.println(" Account.accountDir="+accountDir);
//        for (String setRe : setRes) {
//            System.out.println(" /account: resources="+setRe);
//            
//        }
    }

    public Account() {
    }

    public Boolean isEditable() {
        boolean isEditable = getUser().getParam(User.SUPERVISOR, false) || getUser().getParam(cz.rental.aplikace.User.ACCOUNT_EDIT, false);
        UIComponent uic = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());

        return isEditable;
    }

    public boolean isButtonEnable() {
        return !FacesContext.getCurrentInstance().isValidationFailed();
    }

    public void changePassword() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        PrimeFaces.current().dialog().openDynamic("/aplikace/registrace/password.xhtml", options, null);
        //System.out.println("PrimeFaces.current().dialog().openDynamic");
    }

    public void closePassword(String password) {
        PrimeFaces.current().dialog().closeDynamic(password);
        //System.out.println("PrimeFaces.current().dialog().closeDynamic");
    }

    public void newPassword() {
        this.setCustomerPasswordSHA512(SHA512.hash512(this.customerPassword));
        //System.out.println("Account.newPassword " + this.customerPassword);
        //PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, this.getCustomerPassword(), this.getCustomerPasswordSHA512()));
    }

    public void changePin() {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 9999 + 1);
        this.setCustomerPin(randomNum);
    }

    /**
     * Ulozi data Account do DB
     *
     * @throws Exception
     */
    public void saveAccount() throws Exception {
        if (this.customerID == null) {
            this.customerID = UUID.randomUUID();
        }
        controller.saveAccount(this);
    }

    /**
     * Vytvori sadu adresaru pro ucet v preddefinovanem ulozisti a hlavni soubor
     * uctu 'index.xhtml'
     *
     * @throws IOException
     * @throws Exception
     */
    public void createAccountDir() throws IOException, Exception {
        if (this.getCustomerID() == null) {
            return;
        }
        // Konstrukce cety k uctu uzivatele
        File rootFile = new File(getRootAccountDirFor(null));
        if (!rootFile.exists() && !rootFile.mkdirs()) {
            throw new SQLException("Založení adresáře: '" + this.accountsRootDir + "' NEBYLO úspěšné, vytvořte ručně.");
        }
        // Vstupni soubor uctu
        rootFile = new File(getRootAccountDirFor("index.xhtml"));
        if (!rootFile.exists() && !rootFile.createNewFile()) {
            throw new SQLException("Založení vstupního souboru: '" + getRootAccountDirFor("index.xhtml") + "' NEBYLO úspěšné, vytvořte ručně.");
        }
        // Data uctu
        rootFile = new File(getRootAccountDirFor("data"));
        if (!rootFile.exists() && !rootFile.mkdirs()) {
            throw new SQLException("Založení adresáře: '" + getRootAccountDirFor("data") + "' NEBYLO úspěšné, vytvořte ručně.");
        }
        rootFile = new File(getRootAccountDirFor("data", "data1", null, "data3"));
        if (!rootFile.exists() && !rootFile.mkdirs()) {
            throw new SQLException("Založení adresáře: '" + getRootAccountDirFor("data") + "' NEBYLO úspěšné, vytvořte ručně.");
        }
    }

    /**
     * Naplni hlavni soubor uctu 'index.xhtml' daty TO-DO: Melo by to jit do
     * samostatneho souboru s osetrenim, co bylo vytvoreno a v jake verzi
     *
     * @throws FileNotFoundException
     */
    public void createAccountHTML() throws FileNotFoundException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(getRootAccountDirFor("index.xhtml"));
            pw.println("");
            pw.println("<?xml version='1.0' encoding='UTF-8' ?>");
            pw.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            pw.println("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
            pw.println("xmlns:ui=\"http://xmlns.jcp.org/jsf/facelets\"");
            pw.println("xmlns:h=\"http://xmlns.jcp.org/jsf/html\"");
            pw.println("xmlns:f=\"http://xmlns.jcp.org/jsf/core\"");
            pw.println("xmlns:p=\"http://primefaces.org/ui\">");
//            pw.println("<ui:composition>");
            pw.println("<h:form  id=\"idCustomer\" >");
            pw.println("<p:panelGrid columns=\"3\" >");
            pw.println("<f:facet name=\"header\">");
            pw.println("<p>Účet ID=" + this.getCustomerID() + "</p>");
            pw.println("</f:facet>");
            pw.println("<div> Konfigurace dat účtu </div>");
            pw.println("</p:panelGrid>");
            pw.println("</h:form>");
//            pw.println("</ui:composition>	");
            pw.println("</html>");

        } catch (FileNotFoundException ex) {
            if (pw != null) {
                pw.close();
            }
            throw ex;
        }
        if (pw != null) {
            pw.close();
        }
    }

    /**
     *   Metoda vytvori nazev adresare a podaresare pozadovaneho uctu napr.:
     *  "\Rental\Account\<UUID>\data"; "\Rental\Account\<UUID>\index.xhtml"
     * 
     * @param fileNames - matice nazvu podadresarui a souboru, ktere metoda prevede na retezec s oddelovacem <code>Files.separator</code>
     * @return
     */
    public String getRootAccountDirFor(String... fileNames) {
        StringBuilder sb = new StringBuilder(this.accountsRootDir).append(File.separator).append(this.getCustomerID());
        if (fileNames != null && fileNames.length > 0) {
            for (String fn : fileNames) {
                if (fn != null) {
                    sb.append(File.separator).append(fn.trim());
                }
            }
        }
        return sb.toString();
    }

    /**
     * @return the customerID
     */
    public UUID getCustomerID() {
        return customerID;
    }

    /**
     * @param customerID the customerID to set
     */
    public void setCustomerID(UUID customerID) {
        this.customerID = customerID;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return the customerEmail
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * @param customerEmail the customerEmail to set
     */
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    /**
     * @return the customerPassword
     */
    public String getCustomerPassword() {
        return customerPassword;
    }

    /**
     * @param customerPassword the customerPassword to set
     */
    public void setCustomerPassword(String customerPassword) {
        this.customerPassword = customerPassword;
    }

    /**
     * @return the customerPasswordSHA512
     */
    public String getCustomerPasswordSHA512() {
        return customerPasswordSHA512;
    }

    /**
     * @param customerPasswordSHA512 the customerPasswordSHA512 to set
     */
    public void setCustomerPasswordSHA512(String customerPasswordSHA512) {
        this.customerPasswordSHA512 = customerPasswordSHA512;
    }

    /**
     * @return the customerPin
     */
    public int getCustomerPin() {
        return customerPin;
    }

    /**
     * @param customerPin the customerPin to set
     */
    public void setCustomerPin(int customerPin) {
        this.customerPin = customerPin;
    }

    /**
     * @return the customerAddress
     */
    public String getCustomerAddress() {
        return customerAddress;
    }

    /**
     * @param customerAddress the customerAddress to set
     */
    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    /**
     * @return the customerModel
     */
    public Typentity getCustomerModel() {
        return customerModel;
    }

    /**
     * @param customerModel the customerModel to set
     */
    public void setCustomerModel(Typentity customerModel) {
        this.customerModel = customerModel;
    }

    /**
     * @return the customerTel
     */
    public String getCustomerTel() {
        return customerTel;
    }

    /**
     * @param customerTel the customerTel to set
     */
    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }

    /**
     * @return the controller
     */
    public cz.rental.entity.AccountController getController() {
        return controller;
    }

    /**
     * @param controller the controller to set
     */
    public void setController(AccountController controller) {
        this.controller = controller;
    }

    /**
     * @return the user
     */
    public cz.rental.aplikace.User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(cz.rental.aplikace.User user) {
        this.user = user;
    }

    /**
     * @return the customerPasswordControl
     */
    public String getCustomerPasswordControl() {
        return customerPasswordControl;
    }

    /**
     * @param customerPasswordControl the customerPasswordControl to set
     */
    public void setCustomerPasswordControl(String customerPasswordControl) {
        this.customerPasswordControl = customerPasswordControl;
    }

    /**
     * @return the customerPasswordHelp
     */
    public String getCustomerPasswordHelp() {
        return customerPasswordHelp;
    }

    /**
     * @param customerPasswordHelp the customerPasswordHelp to set
     */
    public void setCustomerPasswordHelp(String customerPasswordHelp) {
        this.customerPasswordHelp = customerPasswordHelp;
    }

    /**
     * @return the accountDir
     */
    public String getAccountDir() {
        return accountDir;
    }

    /**
     * @param accountDir the accountDir to set
     */
    public void setAccountDir(String accountDir) {
        this.accountDir = accountDir;
    }

    /**
     * @return the accountsRootDir
     */
    public String getAccountsRootDir() {
        return accountsRootDir;
    }

    /**
     * @param accountsRootDir the accountsRootDir to set
     */
    public void setAccountsRootDir(String accountsRootDir) {
        this.accountsRootDir = accountsRootDir;
    }
}
