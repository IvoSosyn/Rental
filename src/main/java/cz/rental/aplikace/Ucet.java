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
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import javax.ejb.EJB;
import javax.inject.Named;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datepicker.DatePicker;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.toggleswitch.ToggleSwitch;
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
    private Uzivatel uzivatel;
    private Account account;
    private Integer pin = 0;
    private String email = null;
    private String accountsRootDir = Ucet.ACCOUNT_ROOT_DIR;
    private String accountDir = null;
    private ArrayList<User> users = new ArrayList();
    private User selectedUser;

    @PostConstruct
    public void init() {
        this.account = new Account();
        this.account.setNewEntity(true);
        if (!(uzivatel instanceof Uzivatel)) {
            this.uzivatel = new Uzivatel();
        }
        uzivatel = new Uzivatel();
        uzivatel.initUzivatelByUser(new User());
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
        if (this.account instanceof Account) {
            getUsersForAccount();
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
            this.users = userController.getUsersForAccount(this.account);
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setIdaccount(this.account);
        user.setNewEntity(true);
        this.users.add(user);
        System.out.println(" getUsersForAccount():" + this.users.size());
        return this.users;
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
// -------------------------
// Prace s uzivateli
// ------------------------    

    public String loadUsers() {
        this.getUsersForAccount();
        return "/admin/users/users.xhtml";
    }

    public void onRowSelect(SelectEvent event) {
        this.uzivatel.setUser(selectedUser);
        clearFormUserDetail();
        this.uzivatel.initUzivatelByUser(selectedUser);
    }

    public void onRowUnselect(UnselectEvent event) {
        this.uzivatel.setUser(selectedUser);
        clearFormUserDetail();
        this.uzivatel.initUzivatelByUser(selectedUser);
    }

    public String addUser() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formUserTable:tableUserTable");
        // dataTable.reset();
        this.selectedUser = users.get(users.size() - 1);
        HashSet<String> selectedRowKeys = new HashSet<>(1);
        selectedRowKeys.add(this.selectedUser.getId().toString());
        dataTable.setSelectedRowKeys(selectedRowKeys);
        onRowSelect(null);

//        for (User user : users) {
//            if (user.isNewEntity()) {
//                this.selectedUser = user;
//                this.uzivatel.setUser(selectedUser);
//                this.uzivatel.initUzivatelByUser(selectedUser);
//                break;
//            }
//        }
        return null;
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
        getUsersForAccount();
    }

    /**
     * Metoda ulozi data z detailu editace uzivatele do DB vcetne jeho prav
     */
    public String refreshUsers() {
        getUsersForAccount();
        return null;
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

    public void clearFormUserDetail() {
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
//        ArrayList<String> resetIds = new ArrayList<>();
//        resetIds.add("formUserDetail");
//        root.resetValues(FacesContext.getCurrentInstance(), resetIds);
        UIComponent formUserDetail = root.findComponent("formUserDetail");
        // for JSF 2 getFacetsAndChildren instead of only JSF 1 getChildren
        Iterator<UIComponent> children = formUserDetail.getFacetsAndChildren();
        clearAllComponentInChilds(children);
    }

    private void clearAllComponentInChilds(Iterator<UIComponent> childrenIt) {

        while (childrenIt.hasNext()) {
            UIComponent component = childrenIt.next();
            System.out.println("handling component " + component.getId());
            if (component instanceof InputText) {
                InputText com = (InputText) component;
                com.resetValue();
            }
            if (component instanceof DatePicker) {
                DatePicker com = (DatePicker) component;
                com.resetValue();
            }
            if (component instanceof ToggleSwitch) {
                ToggleSwitch com = (ToggleSwitch) component;
                com.resetValue();
            }
//            if (component instanceof OutputLabel) {
//                OutputLabel com = (OutputLabel) component;
//                com.resetValue();
//            }
            clearAllComponentInChilds(component.getFacetsAndChildren());

        }

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

}
