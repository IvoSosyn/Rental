/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import cz.rental.entity.Account;
import cz.rental.entity.AccountController;
import cz.rental.entity.User;
import cz.rental.utils.Aplikace;
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
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.PostConstruct;

/**
 *
 * @author ivo
 */
@Named(value = "ucet")
@Stateful
public class Ucet {

    private static String ACCOUNT_ROOT_DIR = File.separator + "Rental" + File.separator + "Accounts";

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private AccountController accController;

    private Account account;
    private Uzivatel uzivatel;
    private String password = "";
    private String passwordControl = "";
    private String passwordHelp = "";
    private String accountsRootDir = Ucet.ACCOUNT_ROOT_DIR;
    private String accountDir = null;

    @PostConstruct
    public void init() {
//        try {
//            uzivatel = (Uzivatel) InitialContext.doLookup("java:module/User!cz.rental.aplikace.Uzivatel");
//        } catch (NamingException ex) {
//            Logger.getLogger(Ucet.class.getName()).log(Level.SEVERE, null, ex);
//        }
        // Nacist root dir pro soubory uctu, pokud je zadan ve WEB.XML  napr. na Linuxu
        //  <context-param>
        //      <param-name>cz.rental.accounts.root.dir</param-name>
        //      <param-value>/home/Rental/Accounts</param-value>
        //  </context-param>
        if (FacesContext.getCurrentInstance().getExternalContext().getInitParameter("cz.rental.accounts.root.dir") != null) {
            this.accountsRootDir = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("cz.rental.accounts.root.dir");
        }
        this.accountDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/account");
        //this.setCustomerPasswordSHA512(SHA512.hash512(""));
//        Set<String> setRes=FacesContext.getCurrentInstance().getExternalContext().getResourcePaths("/account");
//        System.out.println(" Ucet.accountDir="+accountDir);
//        for (String setRe : setRes) {
//            System.out.println(" /account: resources="+setRe);
//            
//        }
        this.account = new Account();
        this.account.setNewEntity(true);
        this.account.setId(UUID.randomUUID());
        this.account.setPin(9020);
        this.account.setFullname("Ing.Ivo Sosýn");
        this.account.setEmail("sosyn@seznam.cz");
        this.account.setStreet1("Fűgnerova 51/14");
        this.account.setStreet2("Pod Cvilínem");
        this.account.setCity("Krnov");
        this.account.setPostcode("794 01");
        this.account.setPlatiod(Aplikace.getPlatiOd());
        this.account.setPlatido(Aplikace.getPlatiDo());
        
        this.uzivatel = new Uzivatel();
    }

    /**
     * Metoda vyhleda ucet v DB a porovna SHA-512 hesla s heslem v DB. Pokud je
     * shoda, naplni instancni promennou <code>account</code> udaji z DB
     *
     * @param pin Jedinecny PIN uctu
     * @param email Email majitele uctu
     * @param passwordSHA512 Hash SHA-512 hesla
     * @return Uccet existuje True|False
     * @throws Exception
     */
    public Boolean getUcetAndUzivatel(String pin, String email, String passwordSHA512) throws Exception {
        boolean ucetExist = false;
        Account acc = accController.getAccountForPIN(pin);
        if (!(acc instanceof Account)) {
            throw new Exception("Účet pro PIN:" + pin + " NEEXISTUJE.");
        }
        User accUser = accController.getUserForAccount(acc, email);
        if (accUser instanceof User && accUser.getPasswordsha512().equals(passwordSHA512)) {
            this.account = acc;
            this.uzivatel.setUser(accUser);
        } else {
            throw new Exception("Účet pro PIN:" + pin + " a eMail: " + email + " NEEXISTUJE nebo máte chybné heslo.");
        }
        return ucetExist;
    }

    public Boolean isEditable() {
        boolean isEditable = this.uzivatel.getParam(Uzivatel.SUPERVISOR, false) || this.uzivatel.getParam(Uzivatel.ACCOUNT_EDIT, false);
        UIComponent uic = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        return isEditable;
    }

    public boolean isButtonEnable() {
        return !FacesContext.getCurrentInstance().isValidationFailed();
    }

    /**
     * NEFUNGUJE PrimeFaces.current().dialog().openDynamic(...)
     */
    public void changePassword() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        PrimeFaces.current().dialog().openDynamic("/aplikace/registrace/password.xhtml", options, null);
        //System.out.println("PrimeFaces.current().dialog().openDynamic");
    }

    /**
     * NEFUNGUJE PrimeFaces.current().dialog().closeDynamic(...)
     *
     * @param password
     */
    public void closePassword(String password) {
        PrimeFaces.current().dialog().closeDynamic(password);
        //System.out.println("PrimeFaces.current().dialog().closeDynamic");
    }

    public void newPassword() {
        this.account.setPasswordsha512(SHA512.hash512(this.password));
        this.account.setPasswordhelp(this.passwordHelp);
        //System.out.println("Ucet.newPassword " + this.customerPassword);
        //PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, this.getCustomerPassword(), this.getCustomerPasswordSHA512()));
    }

    public void changePin() {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 9999 + 1);
        this.account.setPin(randomNum);
    }

    /**
     * Ulozi data Ucet do DB
     *
     * @throws Exception
     */
    public void saveAccount() throws Exception {
        getAccController().saveAccount(this.account);
    }

    /**
     * Vytvori sadu adresaru pro ucet v preddefinovanem ulozisti a hlavni soubor
     * uctu 'index.xhtml'
     *
     * @throws IOException
     * @throws Exception
     */
    public void createAccountDir() throws IOException, Exception {
        // Konstrukce cety k uctu uzivatele
        File rootFile = new File(getRootAccountDirFor((String) null));
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
            pw.println("<p>Účet ID=" + this.account.getId() + "</p>");
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
     * Metoda vytvori nazev adresare a podaresare pozadovaneho uctu napr.:
     * "\Rental\Ucet\[UUID]\data"; "\Rental\Ucet\[UUID]\index.xhtml"
     *
     * @param fileNames - matice nazvu podadresarui a souboru, ktere metoda
     * prevede na retezec s oddelovacem <code>Files.separator</code>
     * @return
     */
    public String getRootAccountDirFor(String... fileNames) {
        StringBuilder sb = new StringBuilder(this.accountsRootDir).append(File.separator).append(this.account.getId());
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
     * @return the uzivatel
     */
    public Uzivatel getUzivatel() {
        return uzivatel;
    }

    /**
     * @param uzivatel the uzivatel to set
     */
    public void setUzivatel(Uzivatel uzivatel) {
        this.uzivatel = uzivatel;
    }

    /**
     * @return the accController
     */
    public AccountController getAccController() {
        return accController;
    }

    /**
     * @param accController the accController to set
     */
    public void setAccController(AccountController accController) {
        this.accController = accController;
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
}
