/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.utils.Aplikace;
import cz.rental.aplikace.User;
import cz.rental.aplikace.registrace.Account;
import cz.rental.entity.Attribute;
import cz.rental.entity.Typentity;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;

/**
 * Trida je back-end tabulky "Attribute" pro zadany "Typetnity"
 *
 * @author sosyn
 */
@Named("modelTable")
@Stateless
public class ModelTable implements Serializable {

    static final long serialVersionUID = 42L;

    static final int COUNT_ATTRIBUTE_NEW = 5;

    private Typentity typentity = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<Attribute> selectedAttrs = new ArrayList<>();
    private ArrayList<Attribute> copyAttrs = new ArrayList<>();
    Attribute selectedAttr = null;

    @EJB
    cz.rental.entity.AttributeController controller;
    @EJB
    ModelDetail modelDetail;

    Account account;
    User user;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
        try {
            account = (Account) InitialContext.doLookup("java:module/Account!cz.rental.aplikace.registrace.Account");
            user = (User) InitialContext.doLookup("java:module/User!cz.rental.aplikace.User");
        } catch (NamingException ex) {
            Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Metoda nacte pro zadany "typentity" pole "attribute" a ulozi do pole
     *
     * @param typentity
     */
    public void loadAttributesForTypentity(Typentity typentity) {
        if (typentity == null) {
            return;
        }
        this.setTypentity(typentity);
        setAttributes(controller.getAttributeForTypentity(this.getTypentity()));
        int poradi = ModelTable.COUNT_ATTRIBUTE_NEW + (this.getAttributes().isEmpty() ? 0 : this.getAttributes().get(this.getAttributes().size() - 1).getPoradi());

        // Vzdy pridat jednu prazdnou vetu
        Attribute newAttr = new Attribute();
        newAttr.setIdtypentity(typentity.getId());
        newAttr.setPoradi(poradi);
        newAttr.setAttrtype('C');
        newAttr.setAttrsize(BigInteger.valueOf(50));
        newAttr.setAttrdecimal(BigInteger.valueOf(0));
        newAttr.setAttrsystem(false);
        newAttr.setNewEntity(true);
        this.getAttributes().add(newAttr);
        selectedAttrs = new ArrayList<>();
        modelDetail.setAttribute(this.selectedAttr);
    }

    public void onNodeSelect(NodeSelectEvent event) {
        loadAttributesForTypentity((Typentity) event.getTreeNode().getData());
    }

    public void onNodeUnselect(NodeUnselectEvent event) {
        loadAttributesForTypentity(null);
    }

    public void addAttr() {
        this.selectedAttrs = new ArrayList<>();
        for (Attribute attr : this.attributes) {
            if (attr.isNewEntity()) {
                this.selectedAttr = attr;
                this.selectedAttrs.add(attr);
                break;
            } else {
                this.selectedAttr = null;
            }
        }
        this.modelDetail.setAttribute(this.selectedAttr);
    }

    /**
     * Metoda vymaze vsechny vybrane (selected) Attribute polozky z DB
     */
    public void deleteAttr() {
        if (this.selectedAttrs == null || this.selectedAttrs.isEmpty()) {
            return;
        }
        try {
            controller.deleteAttrs(this.selectedAttrs);
            loadAttributesForTypentity(this.typentity);
        } catch (Exception ex) {
            Logger.getLogger(ModelTable.class.getName()).log(Level.SEVERE, null, ex);
            UIComponent uic = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
            FacesMessage msg = new FacesMessage("System selhal.", "Systémová chyba, nepodařilo se smazat všechny označené položky z databáze, zkuste později.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * Metoda naklonuje vsechny vybrane (selected) Attribute polozky do
     * zasobniku - <code>copyAttrs</code>
     *
     */
    public void copyAttrFrom() {
        this.setCopyAttrs(new ArrayList<>());
        // Nejsou vybrany Attributes pro kopii
        if (this.selectedAttrs == null || this.selectedAttrs.isEmpty()) {
            return;
        }
        for (Attribute selAttr : this.selectedAttrs) {
            try {
                // Novou, jeste nezkontrolovanou polozku Attribute NEbudeme klonovat zaznam 
                if (selAttr.isNewEntity()) {
                    continue;
                }
                this.copyAttrs.add((Attribute) selAttr.clone());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(ModelTable.class.getName()).log(Level.SEVERE, null, ex);
                UIComponent uic = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
                FacesMessage msg = new FacesMessage("System selhal.", "Systémová chyba, nepodařilo se nakopírovat položky do paměti, zkuste později.");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage(null, msg);
                this.setCopyAttrs(new ArrayList<>());
            }
        }
    }

    /**
     * Metoda uloyi vsechny Attribute polozky ZE zasobniku -
     * <code>copyAttrs</code> do DB
     *
     */
    public void pasteAttrTo() {
        // Nejsou Attributes pro vlozeni
        if (this.getCopyAttrs() == null || this.getCopyAttrs().isEmpty()) {
            return;
        }
        try {
            // Uloz nove Attributes do DB
            controller.pasteAttrs(this.getCopyAttrs(), this.typentity.getId(), false);
            // Nacti data z DB
            loadAttributesForTypentity(this.typentity);
        } catch (Exception ex) {
            Logger.getLogger(ModelTable.class.getName()).log(Level.SEVERE, null, ex);
            UIComponent uic = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
            FacesMessage msg = new FacesMessage("System selhal.", "Systémová chyba, nepodařilo se nakopírovat položky z paměti do databáze, zkuste později.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * Metoda ulozi aktualni Attrtibute do DB
     *
     */
    public void saveAttribute() {
        this.selectedAttr = modelDetail.getAttribute();
        try {
            if (this.selectedAttr.isNewEntity()) {
                this.controller.create(this.selectedAttr);
                this.selectedAttr.setNewEntity(false);
                this.selectedAttr = (Attribute) this.controller.findEntita(this.selectedAttr);
            } else {
                this.controller.edit(this.selectedAttr);
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelTable.class.getName()).log(Level.SEVERE, null, ex);
            UIComponent uic = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
            FacesMessage msg = new FacesMessage("System failed", "Systémová chyba, nepodařilo se uložit položku do databáze, zkuste později.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        loadAttributesForTypentity(this.typentity);
        selectedAttrs = new ArrayList<>();
        selectedAttrs.add(this.selectedAttr);
    }

    /**
     * Metoda testuje, zda-li ma uzivatel prava pridavat zaznam
     *
     * @return uzivatel muze pridavat zaznamy
     */
    public boolean isAppendable() {
        boolean isAppendable = user.getParam(User.SUPERVISOR, false) || user.getParam(cz.rental.aplikace.User.MODEL_EDIT, false);
        return (isAppendable);
    }

    /**
     * Metoda testuje, zda-li jsou polozky v zasobniku a uzivatel ma pravo
     * vkladat
     *
     * @return V zasobniku jsou polozky k vlozeni a yaroven uzivatel ma pravo
     * vkladat True|False
     */
    public boolean isPasteable() {
        boolean isPasteable = (this.copyAttrs != null && !this.copyAttrs.isEmpty());
        if (isPasteable) {
            isPasteable = user.getParam(User.SUPERVISOR, false) || user.getParam(cz.rental.aplikace.User.MODEL_EDIT, false);
        }
        return (isPasteable);
    }

    /**
     * Metoda testuje, zda-li jsou zaznamy smazatelne a uzivatel ma pravo mazat
     *
     * @return pole Attribute je smazatelne
     */
    public boolean isRemovable() {
        boolean isRemoveable = (this.selectedAttrs != null && !this.selectedAttrs.isEmpty());
        if (isRemoveable) {
            isRemoveable = user.getParam(User.SUPERVISOR, false) || user.getParam(cz.rental.aplikace.User.MODEL_EDIT, false);
        }
        return (isRemoveable);
    }

    public String convertData(Date datum) {
        return Aplikace.getSimpleDateFormat().format(datum);
    }

    /**
     * @return the typentity
     */
    public Typentity getTypentity() {
        return typentity;
    }

    /**
     * @param typentity the typentity to set
     */
    public void setTypentity(Typentity typentity) {
        this.typentity = typentity;
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
     * @return the selectedAttrs
     */
    public ArrayList<Attribute> getSelectedAttrs() {
        return selectedAttrs;
    }

    /**
     * @param selectedAttrs the selectedAttrs to set
     */
    public void setSelectedAttrs(ArrayList<Attribute> selectedAttrs) {
        this.selectedAttrs = selectedAttrs;
    }

    /**
     * @return the copyAttrs
     */
    public ArrayList<Attribute> getCopyAttrs() {
        return copyAttrs;
    }

    /**
     * @param copyAttrs the copyAttrs to set
     */
    public void setCopyAttrs(ArrayList<Attribute> copyAttrs) {
        this.copyAttrs = copyAttrs;
    }
}
