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
import java.util.UUID;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

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
    private ArrayList<User> users = new ArrayList();
    private User selectedUser;
    private Uzivatel uzivatelEdit;

    @PostConstruct
    public void init() {
        this.account = new Account();
        this.account.setNewEntity(true);
        if (!(uzivatel instanceof Uzivatel)) {
            this.uzivatel = new Uzivatel();
        }
        uzivatelEdit = new Uzivatel();
        uzivatelEdit.initUzivatelByUser(new User());
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
            this.setUsers(userController.getUsersForAccount(this.account));
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setNewEntity(true);
        user.setPopis("Nový záznam.");
        user.setPasswordsha512(SHA512.getSHA512(""));
        this.getUsers().add(user);
        System.out.println(" getUsersForAccount():" + this.getUsers().size());
        return this.getUsers();
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

    public void onRowSelect(SelectEvent event) {
        this.uzivatelEdit.setUser(selectedUser);
        this.uzivatelEdit.initUzivatelByUser(selectedUser);
    }

    public void onRowUnselect(UnselectEvent event) {        
        this.uzivatelEdit.setUser(selectedUser);
        this.uzivatelEdit.initUzivatelByUser(selectedUser);
    }

    public void addUser() {
        for (User user : users) {
            if (user.isNewEntity()) {
                this.selectedUser = user;
                this.uzivatelEdit.setUser(selectedUser);
                this.uzivatelEdit.initUzivatelByUser(selectedUser);
                break;
            }
        }
    }

    public void delUser() {
        if (this.selectedUser != null && !this.selectedUser.isNewEntity()) {
            try {
                userController.destroy(this.selectedUser);
            } catch (Exception ex) {
                Logger.getLogger(Ucet.class.getName()).log(Level.SEVERE, null, ex);
                PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Vymazání uživatele: " + this.selectedUser.getFullname() + " z registru nebylo úspěšné.", "Po chvíli zkuste znovu."));
            }
        } else {
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage(this.selectedUser == null ? "Není vybrán žádný uživatel k vymazání." : "Nový neuložený uživatel se nedá vymazat.", "Vyberte správného uživatele k vymazání"));
        }
    }

    /**
     * Metoda ulozi data z detailu editace uzivatele do DB vcetne jeho prav
     */
    public void saveUser() {
        if (this.uzivatelEdit != null && this.uzivatelEdit.getUser() != null) {
            try {
                if (this.uzivatelEdit.getUser().getIdaccount() == null) {
                    this.uzivatelEdit.getUser().setIdaccount(this.account);
                }
                if (this.uzivatelEdit.getUser().getPasswordsha512().isEmpty()) {
                    this.uzivatelEdit.getUser().setPasswordsha512(SHA512.getSHA512(""));
                }
                this.uzivatelEdit.saveUser();
                boolean saveUserParams = this.uzivatelEdit.saveUserParams();
                if (!saveUserParams) {
                    PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uložení hodnot práv uživatele: " + this.uzivatelEdit.getUser().getFullname() + " do databáze nebylo úspěšné.", "Opakujte pokus za chvíli."));
                }
            } catch (Exception ex) {
                Logger.getLogger(Ucet.class.getName()).log(Level.SEVERE, null, ex);
                PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uložení uživatele: " + this.uzivatelEdit.getUser().getFullname() + " do databáze nebylo úspěšné.", "Opakujte pokus za chvíli."));
            }
        } else {
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Není vybrán žádný uživatel k uložení dat.", "Vyberte správného uživatele k uložení dat."));
        }
    }

    /**
     * Muze uzivatel pridavat nove uzivatele tj.ma pravo UZIVATEL_EDIT
     *
     * @return true|false
     */
    public boolean appendable() {
        return this.uzivatel != null && this.uzivatel.getParam(Uzivatel.USER_PARAM_NAME.UZIVATEL_EDIT, true);
    }

    /**
     * Muze uizivatel smazat zaznam o uzivateli tj.ma pravo UZIVATEL_EDIT a
     * vybrany zaznam neni 'NewEntity'
     *
     * @return true|false
     */
    public boolean removable() {
        return this.uzivatel != null && this.uzivatel.getParam(Uzivatel.USER_PARAM_NAME.UZIVATEL_EDIT, true) && this.selectedUser != null && !this.selectedUser.isNewEntity();
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

    /**
     * @return the users
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    /**
     * @return the uzivatelEdit
     */
    public Uzivatel getUzivatelEdit() {
        return uzivatelEdit;
    }

    /**
     * @param uzivatelEdit the uzivatelEdit to set
     */
    public void setUzivatelEdit(Uzivatel uzivatelEdit) {
        this.uzivatelEdit = uzivatelEdit;
    }
}
