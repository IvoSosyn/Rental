/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.aplikace.Ucet;
import cz.rental.entity.Attribute;
import cz.rental.entity.Entita;
import cz.rental.utils.Aplikace;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces.Dialog;

/**
 *
 * @author ivo
 */
@Named(value = "eviAttr")
@SessionScoped
public class EviAttribute implements Serializable {

    private static long serialVersionUID = 42L;

    private static int COUNT_ENTITA_NEW = 1;

    @EJB
    private cz.rental.entity.AttributeController attrController;
    @EJB
    private cz.rental.entity.EntitaController entitaController;

    @Inject
    private Ucet ucet;

    private EviEntita source = null;
    private Entita entita = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<EviAttrValue> values = new ArrayList<>();

    private Dialog dialog;

    @PostConstruct
    public void init() {
    }

    /**
     * Metoda nacte vsechny Attribute pro Entita a naplni pole "values"
     *
     * @param entita zaznam, pro ktery nacitam Attribute
     * @param source zaznam, jehoz matice Entita se ma aktualizovat
     */
    public void loadAttributes(Entita entita, EviEntita source) {
        this.source = source;
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
            boolean add = this.values.add(new EviAttrValue(this.attrController, this.entita, attr, null, null));
        }
    }

    /**
     * Metoda ulozi vsechny hodnty do DB
     *
     * @param event udalost, ktera vyvolala ulozeni
     */
    public void saveAttributes(ActionEvent event) {

        Date zmenaOd = new Date();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String param = params.get("zmenaOD");
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Metoda saveAttrValues(ActionEvent event) neni zatim immplementovana", "ActionEvent event" + event.getSource()));
        //PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Metoda saveAttrValues(ActionEvent event) neni zatim immplementovana", "ActionEvent event" + event.getSource()));
        try {
            // Nejdriv ulozim zmeny do DB vety Entita
            if (entitaController.findEntita(this.entita) == null) {
                entitaController.create(this.entita);
                this.entita.setNewEntity(false);

            } else {
                entitaController.edit(this.entita);
            }
            if (this.source != null) {
                this.source.reLoadEntities();
            }
            zmenaOd = Aplikace.getSimpleDateFormat().parse(param);
            for (EviAttrValue eviAttrValue : this.values) {
                eviAttrValue.save(zmenaOd);
            }
        } catch (Exception ex) {
            Logger.getLogger(EviAttribute.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Hodnoty NEbyly uloženy.", "Chyba" + ex.getMessage()));
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
    public ArrayList<EviAttrValue> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(ArrayList<EviAttrValue> values) {
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
     * @return the ucet
     */
    public Ucet getUcet() {
        return ucet;
    }

    /**
     * @param account the ucet to set
     */
    public void setUcet(Ucet account) {
        this.ucet = account;
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
