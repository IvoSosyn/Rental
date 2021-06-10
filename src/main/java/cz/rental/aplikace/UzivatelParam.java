/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

/**
 * Trida k uchovani uzivatelskych prav, jejich popisu, hodnot a urovne pristupu
 * k nastaveni prava
 *
 * @author sosyn
 */
public class UzivatelParam {

    /**
     * BASIC - obecny (default) urovne pristupu SUPERVISOR - pravo muze zmenit
     * pouze supervisor ADMINISTRATOR - pravo muze zmenit pouze autor programu
     */
    public static enum SECURITY_LEVEL {
        BASIC, SUPERVISOR, ADMINISTRATOR
    };

    private Uzivatel.USER_PARAM_NAME paramName;
    private String popis = "";
    private Object value = null;
    private Object defaultValue = null;
    private SECURITY_LEVEL securityLevel = SECURITY_LEVEL.BASIC;

    public UzivatelParam() {
    }

    /**
     * Konstruktor vytvori parametricky popis uzivatelskeho prava
     *
     * @param paramName jmeno uzivatelskeho prava
     * @param popis popis prava
     * @param defaultValue defaultni hodnota
     * @param securityLevel uroven pristupu k editaci obsahu prava tj. jaka je nezbytna uroven prava editora<BR>
     * BASIC - obecna(default) uroven pristupu<BR>
     * SUPERVISOR - pravo muze zmenit pouze supervisor <BR>
     * ADMINISTRATOR - pravo muze zmenit pouze autor programu
     */
    public UzivatelParam(Uzivatel.USER_PARAM_NAME paramName, String popis, Object defaultValue, SECURITY_LEVEL securityLevel) {
        this.paramName = paramName;
        this.popis = popis;
        this.defaultValue = defaultValue;
        this.securityLevel = securityLevel;
    }

    public UzivatelParam(Uzivatel.USER_PARAM_NAME paramName, String popis, Object defaultValue) {
        this(paramName, popis, defaultValue, SECURITY_LEVEL.BASIC);
    }

    /**
     * @return the popis
     */
    public String getPopis() {
        return popis;
    }

    /**
     * @param popis the popis to set
     */
    public void setPopis(String popis) {
        this.popis = popis;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the defaultValue
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the paramName
     */
    public Uzivatel.USER_PARAM_NAME getParamName() {
        return paramName;
    }

    /**
     * @param paramName the paramName to set
     */
    public void setParamName(Uzivatel.USER_PARAM_NAME paramName) {
        this.paramName = paramName;
    }

    /**
     * @return the securityLevel
     */
    public SECURITY_LEVEL getSecurityLevel() {
        return securityLevel;
    }

    /**
     * @param securityLevel the securityLevel to set
     */
    public void setSecurityLevel(SECURITY_LEVEL securityLevel) {
        this.securityLevel = securityLevel;
    }

}
