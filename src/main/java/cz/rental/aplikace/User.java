/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

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

    @Inject
    private ServletContext context;

//    @Inject
//    private HttpServletRequest httpRequest;
    
    boolean debugApp = false;

    UUID user=null;
    
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
     *  TO-DO: Dodelat vazbu naprava v DB
     * 
     * @param paramName - jmeno pozadovaneho parametru
     * @param defaultValue - defaultni hodnota k navraceni, pokud parametr neexituje nebo neni nastaven
     * @return true|false hodnota pozadovaneho parametru
     */
    public boolean getParam(String paramName, boolean defaultValue) {
        boolean booleanValue = defaultValue;
        // Apliace je v DEBUG rezimu true|false
        if (paramName.toUpperCase().contains("DEBUG")) {
            booleanValue = debugApp;
        }
        // Uzivatel ma prava administratora
        if (paramName.toUpperCase().contains("SUPERVISOR")) {
            // Dohledat v DB
            booleanValue = true;
        }
        return booleanValue;
    }

    /**
     * Metoda vrati retezcovou hodnotu  pojmenovaneho parametru nabo defaultni
     * hodnotu, pokud neexistuje v DB nebo neni jeste nastaven
     *
     * @param paramName - jmeno pozadovaneho parametru
     * @param defaultValue - defaultni hodnota k navraceni, pokud parametr neexituje nebo neni nastaven
     * @return hodnota pozadovaneho parametru nebo default
     */
    public String getParam(String paramName, String defaultValue) {
        String stringValue = defaultValue;
        return stringValue;
    }
}
