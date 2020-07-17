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
import javax.inject.Named;
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
        this.selectedNode = new DefaultTreeNode(this.typentity, parent);
        parent.setExpanded(true);
        this.selectedNode.setExpanded(true);
    }

    public void valueChange(javax.faces.event.AjaxBehaviorEvent e) {
        System.out.println(" "+FacesContext.getCurrentInstance());
        System.out.println(" ValueChangeEvent e"+e.getComponent());
    }

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
