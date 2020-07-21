/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.entity.Typentity;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author sosyn
 */
@Named("modelTree")
@Stateless
public class ModelTree {

    private TreeNode root = null;
    private TreeNode selectedNode = null;
    private Typentity typentity = null;

    int poradi = 0;

    @EJB
    cz.rental.entity.TypentityController controller;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
        setRoot(controller.fillTreeNodes());
    }

    public void onNodeSelect(NodeSelectEvent event) {
        setTypentity((Typentity) event.getTreeNode().getData());
        System.out.println("typentity.getTypentity(): " + typentity.getTypentity());
    }

    public void displaySelectedSingle() {
        System.out.println("VIEW-Selected");
        if (selectedNode != null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "VIEW-Selected", selectedNode.getData().toString());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void deleteNode() {
        System.out.println("deleteNode");
        selectedNode.getChildren().clear();
        selectedNode.getParent().getChildren().remove(selectedNode);
        selectedNode.setParent(null);

        selectedNode = null;
    }

    public void addTypentity() {
        TreeNode parent = selectedNode;
        if (parent == null) {
            return;
        }
        this.typentity = new Typentity();
        this.typentity.setTypentity("Add " + (poradi++));
        TreeNode trn = new DefaultTreeNode(this.typentity);
        trn.setSelected(true);
        this.setSelectedNode(trn);
        parent.getChildren().add(this.selectedNode);
        parent.setSelected(false);
        parent.setExpanded(true);
        while (parent.getParent() != null) {
            parent = parent.getParent();
            parent.setSelected(false);
            parent.setExpanded(true);
        }

    }

    public void valueChange() {
        System.out.println(" get 'fieldName' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fieldName"));
        System.out.println(" get 'fieldValue' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fieldValue"));
        System.out.println(" get 'typEntity.id' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("typEntity.id"));
    }

    public void valueChange(String e) {
        System.out.println(" " + e);
        System.out.println(" get 'fieldName' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fieldName"));
        System.out.println(" get 'fieldValue' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fieldValue"));
        System.out.println(" get 'typEntity.id' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("typEntity.id"));
    }

    public void valueChange(ValueChangeEvent e) {
        System.out.println(" " + e);
        System.out.println(" get 'fieldName' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fieldName"));
        System.out.println(" get 'fieldValue' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fieldValue"));
        System.out.println(" get 'typEntity.id' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("typEntity.id"));

    }

    public void valueChange(javax.faces.event.AjaxBehaviorEvent e) {
        System.out.println(" ((InputText)e.getSource()).getValue() " + ((InputText) e.getSource()).getLocalValue());
        System.out.println(" ValueChangeEvent e.getComponent() " + e.getComponent());
        System.out.println(" ValueChangeEvent e.getSource() " + e.getSource());
        System.out.println(" get 'fieldName' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fieldName"));
        System.out.println(" get 'fieldValue' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fieldValue"));
        System.out.println(" get 'typEntity' " + FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("typEntity"));

    }

    /*
                                    <!--
                                <p:ajax process="idTypentity" event="valueChange" listener="#{modelTree.valueChange(modelTree.typentity.typentity)}" />
                                <p:ajax   process="idTypentity" event="valueChange" listener="#{modelTree.valueChange}" />
                                -->
     */
    /**
     * @return the root
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(TreeNode root) {
        this.root = root;
    }

    /**
     * @return the selectedNode
     */
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    /**
     * @param selectedNode the selectedNode to set
     */
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
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

}
