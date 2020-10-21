/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.aplikace.registrace.Ucet;
import cz.rental.entity.Attribute;
import cz.rental.entity.Entita;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces.Dialog;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author ivo
 */
@Named(value = "eviAttr")
@Stateless
public class EviAttribute implements Serializable {

    private static long serialVersionUID = 42L;

    private static int COUNT_ENTITA_NEW = 1;

    @EJB
    private cz.rental.entity.AttributeController attrController;

    @Inject
    private Ucet account;

    private Entita entita = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<EviValue> values = new ArrayList<>();

    private Dialog dialog;

    @PostConstruct
    public void init() {
    }

    /**
     * Metoda nacte vsechny Attribute pro Entita a naplni pole "values"
     *
     * @param entita
     */
    public void loadAttributes(Entita entita) {
        if (entita == null) {
            attributes = new ArrayList<>();
            return;
        }
        this.entita = entita;
        /**
         * TO-DO: Zohlednit PlatiOd a PlatiDo sablony ve vyberu dat Attribute,
         * aby se vybraly pouze platne v danem obdobi
         */
        this.attributes = getAttrController().getAttributeForTypentity(entita.getIdtypentity());
        this.values = new ArrayList<>(this.attributes.size());
        for (Attribute attr : this.getAttributes()) {
            // Vybrat uplne vsechno, bez ohledu na platnost, aby se dalo podivat do cele historie
            boolean add = this.values.add(new EviValue(this.attrController, this.entita, attr, null, null));
        }
    }

    /**
     * @return the attributes
     */
    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the values
     */
    public ArrayList<EviValue> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(ArrayList<EviValue> values) {
        this.values = values;
    }

    /**
     * @return the entita
     */
    public Entita getEntita() {
        return entita;
    }

    /**
     * @param entita the entita to set
     */
    public void setEntita(Entita entita) {
        this.entita = entita;
    }

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @param aSerialVersionUID the serialVersionUID to set
     */
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }

    /**
     * @return the COUNT_ENTITA_NEW
     */
    public static int getCOUNT_ENTITA_NEW() {
        return COUNT_ENTITA_NEW;
    }

    /**
     * @param aCOUNT_ENTITA_NEW the COUNT_ENTITA_NEW to set
     */
    public static void setCOUNT_ENTITA_NEW(int aCOUNT_ENTITA_NEW) {
        COUNT_ENTITA_NEW = aCOUNT_ENTITA_NEW;
    }

    /**
     * @return the attrController
     */
    public cz.rental.entity.AttributeController getAttrController() {
        return attrController;
    }

    /**
     * @param attrController the attrController to set
     */
    public void setAttrController(cz.rental.entity.AttributeController attrController) {
        this.attrController = attrController;
    }

    /**
     * @return the account
     */
    public Ucet getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(Ucet account) {
        this.account = account;
    }

    /**
     * @return the dialog
     */
    public Dialog getDialog() {
        return dialog;
    }

    /**
     * @param dialog the dialog to set
     */
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

}
