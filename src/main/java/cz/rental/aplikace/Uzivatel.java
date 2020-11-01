/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import cz.rental.entity.Account;
import cz.rental.entity.User;
import cz.rental.entity.UserController;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

/**
 *
 * @author ivo
 */
@Named(value = "uzivatel")
@Stateful
public class Uzivatel {

    public static final String SUPERVISOR = "Supervisor";
    public static final String MODEL_EDIT = "ModelEdit";
    public static final String ACCOUNT_EDIT = "AccounEdit";

    @Inject
    private ServletContext context;
    @EJB
    private UserController userController;

//    @Inject
//    private HttpServletRequest httpRequest;
    boolean debugApp = false;
    private cz.rental.entity.User user = null;

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

    }

    /**
     * Metoda vzhleda v DB 'User' pro 'Ucet' a 'email' a v pripade ze souhlasi
     * heslo, naplni promennou <code>user</code>
     *
     * @param acc ucet registrace
     * @param email email uzivatele
     * @param passwordSHA512 HASH - 512 hesla
     * @return
     */
    public boolean getUserForAccount(Account acc, String email, String passwordSHA512) {
        if (userController instanceof UserController) {
            this.user = userController.getUserForAccount(acc, email, passwordSHA512);
        }
        return (this.user instanceof User);
    }

    /**
     * Metoda zalozi nebo aktualizuje zaznam User v DB
     *
     * @param changedUser - uzivatel k vytvoreni|aktualizaci do DB
     * @throws Exception vyjimka, pokud ulození do DB nebude uspesne
     */
    public void saveUser(User changedUser) throws Exception {
        if (this.userController.findEntita(changedUser) instanceof User) {
            this.userController.edit(changedUser);
        } else {
            this.userController.create(changedUser);
        }
    }

    /**
     * Metoda vrati logickou hodnotu pojmenovaneho parametru nabo defaultni
     * hodnotu, pokud neexistuje v DB nebo neni jeste nastaven
     *
     * TO-DO: Dodelat vazbu naprava v DB
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
            booleanValue = (boolean) userController.getUserParam(user, paramName.toUpperCase(), true);
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
        return (String) userController.getUserParam(this.user, paramName, defaultValue);
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
        return (Date) userController.getUserParam(this.user, paramName, defaultValue);
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
        return userController.setUserParam(this.user, paramName, value);
    }

    /**
     * @return the user
     */
    public cz.rental.entity.User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(cz.rental.entity.User user) {
        this.user = user;
    }

}
