/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import cz.rental.entity.Account;
import cz.rental.entity.User;
import cz.rental.entity.UserController;
import cz.rental.utils.SHA512;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import org.primefaces.PrimeFaces;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ivo
 */
@Named(value = "uzivatel")
@SessionScoped
public class Uzivatel implements Serializable {

    static final long serialVersionUID = 42L;

    public static enum USER_PARAM_NAME {
        SUPERVISOR, MODEL, MODEL_EDIT, ACCOUNT, ACCOUNT_EDIT, UZIVATEL, UZIVATEL_EDIT, EVIDENCE, EVIDENCE_EDIT
    };

    @Inject
    private ServletContext context;
    @EJB
    private UserController userController;

//    @Inject
//    private HttpServletRequest httpRequest;
    boolean debugApp = false;
    private User user = null;
    private ArrayList<UzivatelParam> userParams = new ArrayList<>();

    private String password = null;
    private String passwordControl = null;
    private String passwordHelp = "";

    /**
     * Inicializace matice prav uzivatele
     */
    @PostConstruct
    public void init() {
        String param;
        if (!(userController instanceof UserController)) {
            try {
                userController = (UserController) InitialContext.doLookup("java:module/UserController!cz.rental.entity.UserController");
            } catch (NamingException ex) {
                Logger.getLogger(Ucet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (context == null || userController == null) {
            return;
        }
        param = context.getInitParameter("javax.faces.PROJECT_STAGE");
        if (param != null && param.toUpperCase().contains("DEBUG")) {
            debugApp = true;
        }
        this.user = new User();
        this.user.setNewEntity(true);
        initUserParams();
    }

    public Uzivatel() {
        init();
    }

    /**
     * Metoda vytvori matici parametru s default hodnotami a nasledne ji naplni
     * hodnotami pro konkretniho User z DB
     *
     * @param user - konkretni uzivatel, pro ktereho se nachysta matice s
     * parametry
     * @return true|false - jak vytvoreni a naplneni probehlo
     */
    public boolean initUzivatelByUser(User user) {
        this.user = user;
        initUserParams();
        fillUserParamsByUser();
        return false;
    }

    /**
     * Metoda vzhleda v DB 'User' pro 'Ucet' a 'email' a v pripade ze souhlasi
     * heslo, naplni promennou <code>user</code>
     *
     * @param account ucet registrace
     * @param email email uzivatele
     * @param passwordSHA512 HASH - 512 hesla
     * @return
     */
    public boolean getUserForAccount(Account account, String email, String passwordSHA512) {
        if (userController instanceof UserController) {
            this.user = userController.getUserForAccount(account, email, passwordSHA512);
            fillUserParamsByUser();
        }
        return (this.user instanceof User);
    }

    /**
     * Metoda zalozi nebo aktualizuje zaznam User v DB
     *
     * @throws Exception vyjimka, pokud ulození do DB nebude uspesne
     */
    public void saveUser() throws Exception {
        if (!(this.user instanceof User)) {
            return;
        }
        if (this.userController.findEntita(this.user) instanceof User) {
            this.userController.edit(this.user);
        } else {
            this.userController.create(this.user);
            this.user.setNewEntity(false);
        }
        this.userController.findEntita(this.user);
    }

    /**
     * Metoda vrati logickou hodnotu pojmenovaneho parametru nabo defaultni
     * hodnotu, pokud neexistuje v DB nebo neni jeste nastaven
     *
     * @param paramName - jmeno pozadovaneho parametru
     * @param defaultValue - defaultni hodnota k navraceni, pokud parametr
     * neexituje nebo neni nastaven
     * @return true|false hodnota pozadovaneho parametru
     */
    public boolean getParam(String paramName, boolean defaultValue) {
        boolean booleanValue = defaultValue;
        if (!(userController instanceof UserController)) {
            return defaultValue;
        }
        // Apliace je v DEBUG rezimu true|false
        if (paramName.toUpperCase().contains("DEBUG")) {
            booleanValue = debugApp;
        } else {
            booleanValue = (boolean) userController.getUserParam(user, paramName.toUpperCase(), defaultValue);
        }
        return booleanValue;
    }

    /**
     * Metoda vrati logickou hodnotu pojmenovaneho parametru nabo defaultni
     * hodnotu, pokud neexistuje v DB nebo neni jeste nastaven
     *
     * @param paramName - jmeno pozadovaneho parametru
     * @param defaultValue - defaultni hodnota k navraceni, pokud parametr
     * neexituje nebo neni nastaven
     * @return true|false hodnota pozadovaneho parametru
     */
    public boolean getParam(Uzivatel.USER_PARAM_NAME paramName, boolean defaultValue) {
        boolean booleanValue = defaultValue;
        if (!(userController instanceof UserController)) {
            return defaultValue;
        }
        // Apliace je v DEBUG rezimu true|false
        if (paramName.name().toUpperCase().contains("DEBUG")) {
            booleanValue = debugApp;
        } else {
            booleanValue = (boolean) userController.getUserParam(this.user, paramName.name().toUpperCase(), defaultValue);
        }
        return booleanValue;
    }

    /**
     * Metoda vrati retezcovou hodnotu pojmenovaneho parametru nabo defaultni
     * hodnotu, pokud neexistuje v DB nebo neni jeste nastaven
     *
     * @param paramName - jmeno pozadovaneho parametru
     * @param defaultValue - defaultni hodnota k navraceni, pokud parametr
     * neexituje nebo neni nastaven
     * @return hodnota pozadovaneho parametru nebo default
     */
    public String getParam(String paramName, String defaultValue) {
        if (!(userController instanceof UserController)) {
            return defaultValue;
        }
        return (String) userController.getUserParam(this.user, paramName.toUpperCase(), defaultValue);
    }

    /**
     * Metoda vrati retezcovou hodnotu pojmenovaneho parametru nabo defaultni
     * hodnotu, pokud neexistuje v DB nebo neni jeste nastaven
     *
     * @param paramName - jmeno pozadovaneho parametru
     * @param defaultValue - defaultni hodnota k navraceni, pokud parametr
     * neexituje nebo neni nastaven
     * @return hodnota pozadovaneho parametru nebo default
     */
    public String getParam(Uzivatel.USER_PARAM_NAME paramName, String defaultValue) {
        if (!(userController instanceof UserController)) {
            return defaultValue;
        }
        return (String) userController.getUserParam(this.user, paramName.name().toUpperCase(), defaultValue);
    }

    /**
     * Metoda vrati numerickou=double hodnotu pojmenovaneho parametru nabo
     * defaultni hodnotu, pokud neexistuje v DB nebo neni jeste nastaven
     *
     * @param paramName - jmeno pozadovaneho parametru
     * @param defaultValue - defaultni hodnota k navraceni, pokud parametr
     * neexituje nebo neni nastaven
     * @return hodnota pozadovaneho parametru nebo default
     */
    public double getParam(String paramName, double defaultValue) {
        if (!(userController instanceof UserController)) {
            return defaultValue;
        }
        return (double) userController.getUserParam(this.user, paramName, defaultValue);
    }

    /**
     * Metoda vrati numerickou=double hodnotu pojmenovaneho parametru nabo
     * defaultni hodnotu, pokud neexistuje v DB nebo neni jeste nastaven
     *
     * @param paramName - jmeno pozadovaneho parametru
     * @param defaultValue - defaultni hodnota k navraceni, pokud parametr
     * neexituje nebo neni nastaven
     * @return hodnota pozadovaneho parametru nebo default
     */
    public double getParam(Uzivatel.USER_PARAM_NAME paramName, double defaultValue) {
        if (!(userController instanceof UserController)) {
            return defaultValue;
        }
        return (double) userController.getUserParam(this.user, paramName.toString().toUpperCase(), defaultValue);
    }

    /**
     * Metoda vrati datum pojmenovaneho parametru nabo defaultni hodnotu, pokud
     * neexistuje v DB nebo neni jeste nastaven
     *
     * @param paramName - jmeno pozadovaneho parametru
     * @param defaultValue - defaultni hodnota k navraceni, pokud parametr
     * neexituje nebo neni nastaven
     * @return datum pozadovaneho parametru nebo default
     */
    public Date getParam(String paramName, Date defaultValue) {
        if (!(userController instanceof UserController)) {
            return defaultValue;
        }
        return (Date) userController.getUserParam(this.user, paramName.toUpperCase(), defaultValue);
    }

    /**
     * Metoda vrati datum pojmenovaneho parametru nabo defaultni hodnotu, pokud
     * neexistuje v DB nebo neni jeste nastaven
     *
     * @param paramName - jmeno pozadovaneho parametru
     * @param defaultValue - defaultni hodnota k navraceni, pokud parametr
     * neexituje nebo neni nastaven
     * @return datum pozadovaneho parametru nebo default
     */
    public Date getParam(Uzivatel.USER_PARAM_NAME paramName, Date defaultValue) {
        if (!(userController instanceof UserController)) {
            return defaultValue;
        }
        return (Date) userController.getUserParam(this.user, paramName.name().toUpperCase(), defaultValue);
    }

    /**
     * Ulozeni uzivatelskeho parametru do DB
     *
     * @param paramName - jmeno ukladaneho parametru
     * @param value - hodnota ukleadaneho parametru (String, Boolean, Double,
     * Date)
     * @return ulozeni do DB bylo uspesne true|false
     */
    public boolean setParam(String paramName, Object value) {
        if (!(userController instanceof UserController)) {
            return false;
        }
        return userController.setUserParam(this.user, paramName.toUpperCase(), value);
    }

    /**
     * Ulozeni uzivatelskeho parametru do DB
     *
     * @param paramName - jmeno ukladaneho parametru
     * @param value - hodnota ukleadaneho parametru (String, Boolean, Double,
     * Date)
     * @return ulozeni do DB bylo uspesne true|false
     */
    public boolean setParam(Uzivatel.USER_PARAM_NAME paramName, Object value) {
        if (!(userController instanceof UserController)) {
            return false;
        }
        return userController.setUserParam(this.user, paramName.name().toUpperCase(), value);
    }

    private void initUserParams() {
        this.userParams = new ArrayList<>();
        this.userParams.add(new UzivatelParam(Uzivatel.USER_PARAM_NAME.SUPERVISOR, "Neomezená práva správce", false));
        this.userParams.add(new UzivatelParam(Uzivatel.USER_PARAM_NAME.ACCOUNT, "Může vidět položky účtu", false));
        this.userParams.add(new UzivatelParam(Uzivatel.USER_PARAM_NAME.ACCOUNT_EDIT, "Může editovat účet", false));
        this.userParams.add(new UzivatelParam(Uzivatel.USER_PARAM_NAME.UZIVATEL, "Může vidět seznam uživatelů", true));
        this.userParams.add(new UzivatelParam(Uzivatel.USER_PARAM_NAME.UZIVATEL_EDIT, "Může editovat seznam uživatelů", false));
        this.userParams.add(new UzivatelParam(Uzivatel.USER_PARAM_NAME.MODEL, "Může vidět model(šablonu)", true));
        this.userParams.add(new UzivatelParam(Uzivatel.USER_PARAM_NAME.MODEL_EDIT, "Může editovat model(šablonu)", false));
        this.userParams.add(new UzivatelParam(Uzivatel.USER_PARAM_NAME.EVIDENCE, "Může vidět evidenci", false));
        this.userParams.add(new UzivatelParam(Uzivatel.USER_PARAM_NAME.EVIDENCE_EDIT, "Může editovat editovat evidenci", false));
    }

    private void fillUserParamsByUser() {
        for (UzivatelParam up : this.userParams) {

            if (up.getDefaultValue() instanceof Boolean) {
                up.setValue(this.getParam(up.getParamName(), ((Boolean) up.getDefaultValue())));
            }
            if (up.getDefaultValue() instanceof Date) {
                up.setValue(this.getParam(up.getParamName(), ((Date) up.getDefaultValue())));
            }
            if (up.getDefaultValue() instanceof String) {
                up.setValue(this.getParam(up.getParamName(), ((String) up.getDefaultValue())));
            }
            if (up.getDefaultValue() instanceof Double) {
                up.setValue(this.getParam(up.getParamName(), ((Double) up.getDefaultValue())));
            }
        }
    }

    /**
     * Metoda ulozi hodnoty z matice UserParams pro uzivatele User do DB
     *
     * @return true|false uspech ulozeni vsech parametru do DB
     */
    public boolean saveUserParams() {
        for (UzivatelParam userParam : this.userParams) {
            boolean setParam = setParam(userParam.getParamName(), userParam.getValue());
            if (!setParam) {
                return false;
            }
        }
        return true;
    }

    public UzivatelParam getUserParm(String paramName) {
        for (UzivatelParam userParam : userParams) {
            if (userParam.getParamName().name().equalsIgnoreCase(paramName)) {
                return userParam;
            }
        }
        return null;
    }

    public void userParamChanged(ValueChangeEvent event) {
        PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Není zatím implementováno", "userParamChanged()"));
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
        options.put("height", 400);
        options.put("contentWidth", 1000);
        options.put("contentHeight", 400);
        options.put("closeOnEscape", true);
        // options.put("includeViewParams", true);
        PrimeFaces.current().dialog().openDynamic("/aplikace/password.xhtml", options, null);
    }

    /**
     * Metoda uzavre dialog se zadanim hesla - je volana ze samostatneho XHTML
     * souboru, ve kterem je dialog
     */
    public void savePassword() {
        PrimeFaces.current().dialog().closeDynamic(this.password);
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

    /**
     * Metoda provede validaci hesla proti kontrolnimu heslu
     * @param event Udalost s hodnotami 
     */
    public void validatePassword(ValueChangeEvent event) {
        String newConfirmPass = (String) event.getNewValue();
        if (newConfirmPass == null || newConfirmPass.isEmpty() || !this.password.equals(newConfirmPass)) {
            FacesMessage msg = new FacesMessage("Hesla se neshodují, opravte je prosím.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(), msg);
        }
    }

    public void passFromDialog(SelectEvent event) {
        this.user.setPasswordsha512(SHA512.getSHA512(this.getPassword()));
        this.user.setPasswordsha512(this.getPasswordHelp());
        // PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Heslo:", event.getObject().toString()));
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(cz.rental.entity.User user) {
        this.user = user;
    }

    /**
     * @return the userParams
     */
    public ArrayList<UzivatelParam> getUserParams() {
        return userParams;
    }

    /**
     * @param userParams the userParams to set
     */
    public void setUserParams(ArrayList<UzivatelParam> userParams) {
        this.userParams = userParams;
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
