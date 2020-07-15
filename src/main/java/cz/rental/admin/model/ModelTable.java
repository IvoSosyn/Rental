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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 * Trida je back-end tabulky "Attribute" pro zadany "Typetnity"
 *
 * @author sosyn
 */
@Named("modelTable")
@Stateless
public class ModelTable {

    private Typentity typentity = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<Attribute> selectedAttrs = null;
    Attribute selectedAttr = null;

    @EJB
    cz.rental.entity.AttributeController controller;

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
    public void getAttributeForTypentity(Typentity typentity) {
        this.setTypentity(typentity);
        setAttributes(controller.getAttributeForTypentity(this.getTypentity()));
    }

    public void onNodeSelect(NodeSelectEvent event) {
        getAttributeForTypentity((Typentity) event.getTreeNode().getData());
    }

    public void onNodeUnselect(NodeUnselectEvent event) {
        getAttributeForTypentity(null);
    }

    public void onRowSelect(SelectEvent event) {
        this.selectedAttr = (Attribute) event.getObject();
        FacesMessage msg = new FacesMessage("Attribute selected", event.getObject().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowUnselectSelect(UnselectEvent event) {
        this.selectedAttr = null;
    }

    public void displaySelectedSingle() {
        System.out.println("VIEW-Selected");
        if (true) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "VIEW-Selected", "Test");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
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
