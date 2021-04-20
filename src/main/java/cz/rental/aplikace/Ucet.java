/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import cz.rental.entity.Account;
import cz.rental.entity.AccountController;
import cz.rental.entity.User;
import cz.rental.entity.UserController;
import cz.rental.utils.Aplikace;
import cz.rental.utils.SHA512;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ivo
 */
@Named(value = "ucet")
@SessionScoped
public class Ucet implements Serializable {

    static final long serialVersionUID = 42L;

    public static final String ACCOUNT_ROOT_DIR = File.separator + "Rental" + File.separator + "Accounts";
    public static final int ACCOUNT_MIN_PIN = 1000;
    public static final int ACCOUNT_MAX_PIN = 9999 + 1;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private AccountController accController;
    @EJB
    private UserController userController;
    @Inject
    private Uzivatel uzivatel;
    private Account account;
    private Integer pin = 0;
    private String email = null;
    private String password = null;
    private String passwordControl = null;
    private String passwordHelp = "";
    private String accountsRootDir = Ucet.ACCOUNT_ROOT_DIR;
    private String accountDir = null;
    private User selectedUser;

    @PostConstruct
    public void init() {
        this.account = new Account();
        this.account.setNewEntity(true);
        if (!(uzivatel instanceof Uzivatel)) {
            this.uzivatel = new Uzivatel();
        }
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
        Account acc = accController.getAccountForPIN(this.pin);
        if (!(acc instanceof Account)) {
            throw new Exception("Účet pro PIN:" + pin + " NEEXISTUJE.");
        }
        if (uzivatel.getUserForAccount(acc, email, passwordSHA512)) {
            this.account = acc;
        } else {
            throw new Exception("Účet pro PIN:" + pin + " a eMail: " + email + " NEEXISTUJE nebo máte chybné heslo.");
        }
        return ucetExist;
    }

    /**
     * Metoda vraci matici uzivatelu(<code>User</code>) pro tento
     * ucet(<code>Account</code>)
     *
     * @return matici uzivatelu nebo praydnou matici
     */
    public ArrayList<User> getUsersForAccount() {
        if (userController instanceof UserController) {
            return userController.getUsersForAccount(this.account);
        }
        return new ArrayList<>();
    }

    /**
     * Metoda zavola dialog (samostatny .XHTML soubor) pro zadani hesla
     */
    public void editPassword() {
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("width", 1000);
        options.put("height", 350);
        options.put("contentWidth", 1000);
        options.put("contentHeight", 350);
        options.put("closeOnEscape", true);
        // options.put("includeViewParams", true);
        PrimeFaces.current().dialog().openDynamic("/aplikace/registrace/password.xhtml", options, null);
    }

    /**
     * Metoda uzavre dialog se zadanim hesla - je volana ze samostatneho XHTML
     * souboru, ve kterem je dialog
     */
    public void savePassword() {
        PrimeFaces.current().dialog().closeDynamic(this.password);
    }

    public void passFromDialog(SelectEvent event) {
        this.account.setPasswordsha512(SHA512.getSHA512(this.password));
        this.account.setPasswordhelp(this.passwordHelp);
        // PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Heslo:", event.getObject().toString()));
    }

    /**
     *
     * @param event
     */
    public void validatePassword(ValueChangeEvent event) {

        String newConfirmPass = (String) event.getNewValue();

        if (newConfirmPass == null || newConfirmPass.isEmpty() || !this.password.equals(newConfirmPass)) {
            FacesMessage msg = new FacesMessage("Hesla se neshodují, opravte je prosím.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(), msg);
        }
    }

    /**
     * Metoda testuje, zda-li lze ulozit heslo
     *
     * @return
     */
    public boolean isPassBtnSaveEnabled() {
        boolean isEnable = !FacesContext.getCurrentInstance().isValidationFailed();
        if (!isEnable) {
            return false;
        }
//        String password = this.getUcet().getPassword();
//        String passwordControl = this.getUcet().getPasswordControl();;
//
        isEnable = this.getPassword() != null && !this.getPassword().isEmpty()
                && this.getPasswordControl() != null && !this.getPasswordControl().isEmpty()
                && this.getPassword().equals(this.getPasswordControl());

        return isEnable;

    }

    public void changePin(ActionEvent event) {
        //System.out.println("Ucet.changePin()");
        int randomPin = 0;
        do {
            randomPin = ThreadLocalRandom.current().nextInt(Ucet.ACCOUNT_MIN_PIN, Ucet.ACCOUNT_MAX_PIN);
        } while (this.accController.getAccountForPIN(randomPin) != null);
        this.account.setPin(randomPin);
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
        this.account = account;
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
     * @return the platiOd
     */
    public Date getPlatiOd() {
        return this.uzivatel.getParam("ObdobiOD", Aplikace.getPlatiOd());
    }

    /**
     * @param platiOd the platiOd to set
     */
    public void setPlatiOd(Date platiOd) {
        this.uzivatel.setParam("ObdobiOD", platiOd);
        if (!this.uzivatel.setParam("ObdobiOD", platiOd)) {
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uložení parametru 'ObdobiOD' NEBYLA úspěšná.", "Předávaná hodnota: " + Aplikace.getSimpleDateFormat().format(platiOd)));
        }
    }

    /**
     * @return the platiDo
     */
    public Date getPlatiDo() {
        return this.uzivatel.getParam("ObdobiDO", Aplikace.getPlatiDo());
    }

    /**
     * @param platiDo the platiDo to set
     */
    public void setPlatiDo(Date platiDo) {
        if (!this.uzivatel.setParam("ObdobiDO", platiDo)) {
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uložení parametru 'ObdobiDO' NEBYLA úspěšná.", "Předávaná hodnota: " + Aplikace.getSimpleDateFormat().format(platiDo)));
        }
    }

    /**
     * @return the datumZmeny
     */
    public Date getDatumZmeny() {
        return this.uzivatel.getParam("ZmenaOD", Aplikace.getPlatiDo());
    }

    /**
     * @param datumZmeny the datumZmeny to set
     */
    public void setDatumZmeny(Date datumZmeny) {
        if (!this.uzivatel.setParam("ZmenaOD", datumZmeny)) {
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uložení parametru 'ZmenaOD' NEBYLA úspěšná.", "Předávaná hodnota: " + Aplikace.getSimpleDateFormat().format(datumZmeny)));
        }
    }

    /**
     * @return the datumZmeny
     */
    public String datumZmenyAsString() {
        return Aplikace.getSimpleDateFormat().format(this.uzivatel.getParam("ZmenaOD", Aplikace.getZmenaOd()));
    }

    /**
     * @return the selectedUser
     */
    public User getSelectedUser() {
        return selectedUser;
    }

    /**
     * @param selectedUser the selectedUser to set
     */
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }
}
