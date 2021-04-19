/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace;

import java.util.Date;

/**
 *
 * @author sosyn
 */
public class UzivatelParam {

    private String paramName = "";
    private String popis = "";
    private Object value = null;
    private Object defaultValue = null;

    public UzivatelParam() {
    }

    public UzivatelParam(String paramName, String popis, Object defaultValue) {
        this.paramName = paramName;
        this.popis = popis;
        this.defaultValue = defaultValue;
    }

    /**
     * @return the paramName
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * @param paramName the paramName to set
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
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
}
