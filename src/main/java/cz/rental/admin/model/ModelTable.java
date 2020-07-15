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
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;

/**
 * Trida je back-end tabulky "Attribute" pro zadany "Typetnity"
 * @author sosyn
 */
@Named("modelTable")
@Stateless
public class ModelTable {

    private Typentity typentity = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();

    @EJB
    cz.rental.entity.AttributeController controller;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    /**
     * Metoda nacte pro zadany "typentity" pole "attribute" a ulozi do pole 
     * @param typentity
     */
    public void getAttributeForTypentity(Typentity typentity) {
        this.setTypentity(typentity);
        setAttributes(controller.getAttributeForTypentity(this.getTypentity()));
    }

    public void onNodeExpand(NodeExpandEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Expanded", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Collapsed", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onNodeSelect(NodeSelectEvent event) {
        System.out.println(" "+event.toString());
        getAttributeForTypentity((Typentity) event.getTreeNode().getData() );
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected Node", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onNodeUnselect(NodeUnselectEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unselected", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
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

}
