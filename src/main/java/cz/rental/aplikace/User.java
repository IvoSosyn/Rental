/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import java.util.Date;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

/**
 *
 * @author ivo
 */
@Named(value = "user")
@Stateful
public class User {

    public static final String SUPERVISOR = "Supervisor";
    public static final String MODEL_EDIT = "ModelEdit";
    public static final String ACCOUNT_EDIT = "AccounEdit";
    @Inject
    private ServletContext context;

//    @Inject
//    private HttpServletRequest httpRequest;
    boolean debugApp = false;

    private UUID userId = null;

    /**
     * Inicializace matice prav uzivatele
     */
    @PostConstruct

    public void init() {
        String param;
        if (context == null) {
            return;
        }
        param = context.getInitParameter("javax.faces.PROJECT_STAGE");
        if (param != null && param.toUpperCase().contains("DEBUG")) {
            debugApp = true;
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
        // Apliace je v DEBUG rezimu true|false
        if (paramName.toUpperCase().contains("DEBUG")) {
            booleanValue = debugApp;
        }
        // Uzivatel ma prava administratora
        if (paramName.toUpperCase().contains(User.SUPERVISOR)) {
            // Dohledat v DB
            booleanValue = true;
        }
        // Uzivatel ma prava editovat "model" - sablony
        if (paramName.toUpperCase().contains(User.MODEL_EDIT)) {
            // Dohledat v DB
            booleanValue = false;
        }
        if (paramName.toUpperCase().contains(User.ACCOUNT_EDIT)) {
            // Dohledat v DB
            booleanValue = false;
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
        String stringValue = defaultValue;
        return stringValue;
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
        double doubleValue = defaultValue;
        return doubleValue;
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
        Date dateValue = defaultValue;
        return dateValue;
    }

    /**
     * @return the userId
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

}
