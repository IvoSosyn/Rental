/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.entity.Attribute;
import cz.rental.entity.Typentity;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;

/**
 * Trida je back-end tabulky "Attribute" pro zadany "Typetnity"
 *
 * @author sosyn
 */
@Named("modelTable")
@Stateless
public class ModelTable {

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

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    /**
     * Metoda nacte pro zadany "typentity" pole "attribute" a ulozi do pole
     *
     * @param typentity
     */
    public void loadAttributesForTypentity(Typentity typentity) {
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
        newAttr.setNewEntity(true);
        this.getAttributes().add(newAttr);
//        selectedAttrs = new ArrayList<>();
//        modelDetail.setAttribute(this.selectedAttr);
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
     * Je konkretni polozka Attribute editovatelna ?
     *
     * @return ano|ne
     */
    public boolean isEditable() {
        return ((this.selectedAttrs != null && !this.selectedAttrs.isEmpty()) || (this.selectedAttr != null && this.selectedAttr.getAttrsystem() != null && !this.selectedAttr.getAttrsystem()));
    }

    /**
     * Je vybrana v tabulce Attribute alespon 1 polozka ?
     *
     * @return Ano = alespon 1 | Ne = 0 nebo null
     */
    public boolean isSelectedAttr() {
        return ((this.selectedAttrs != null && !this.selectedAttrs.isEmpty()));
    }

    /**
     * Je vybrana v pameti alespon 1 polozka Attribute, kterou lze vlozit do DB
     * ?
     *
     * @return Ano = alespon 1 | Ne = 0 nebo null
     */
    public boolean isCopyAttr() {
        return ((this.getCopyAttrs() != null && !this.copyAttrs.isEmpty()));
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
