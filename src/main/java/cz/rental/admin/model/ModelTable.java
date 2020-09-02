/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.entity.Attribute;
import cz.rental.entity.Typentity;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
    private ArrayList<Attribute> selectedAttrs = null;
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
    public void loadAttributeForTypentity(Typentity typentity) {
        this.setTypentity(typentity);
        setAttributes(controller.getAttributeForTypentity(this.getTypentity()));
        int poradi = ModelTable.COUNT_ATTRIBUTE_NEW + (this.getAttributes().isEmpty() ? 0 : this.getAttributes().get(this.getAttributes().size() - 1).getPoradi());

        // Vzdy pridat jednu prazdnou vetu
        Attribute attrNew = new Attribute();
        attrNew.setIdtypentity(typentity.getId());
        attrNew.setPoradi(poradi);
        attrNew.setNewEntity(true);
        this.getAttributes().add(attrNew);

        // Inicialisovat detail
        this.selectedAttr = null;
        modelDetail.setAttribute(this.selectedAttr);
    }

    public void onNodeSelect(NodeSelectEvent event) {
        loadAttributeForTypentity((Typentity) event.getTreeNode().getData());
    }

    public void onNodeUnselect(NodeUnselectEvent event) {
        loadAttributeForTypentity(null);
    }
    public void addAttr() {
    }
    public void deleteAttr() {
    }
    public boolean isEditable() {
        return ((this.selectedAttr!=null && !this.selectedAttr.getAttrsystem()) || (this.selectedAttrs!=null && this.selectedAttrs.size()>0));
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

}
