/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

import cz.rental.aplikace.Ucet;
import cz.rental.entity.Attribute;
import cz.rental.entity.Typentity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author ivo
 */
@Named(value = "viewModelTree")
@SessionScoped
public class ViewModelTree implements Serializable {

    static final long serialVersionUID = 42L;

    @EJB
    cz.rental.entity.TypentityController typentityController;

    @EJB
    cz.rental.entity.AttributeController attributeController;

    @Inject
    private Ucet ucet;

    private TreeNode treeNode;
    private TreeNode selectedNode = null;

    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<Attribute> selectedAttrs = new ArrayList<>();

    @PostConstruct
    public void init() {
    }

    /**
     * Metoda zobrazi nahled na model pomoci TreeTable tridy
     *
     * @param typentity - vychozi uzel, pro ktery zobrazuji Tree+DataTable
     */
    public void showModel(Typentity typentity) {
        Map<String, Object> options = new HashMap<>();

        if (this.ucet.getAccount().getIdmodel() instanceof Typentity) {
            options.put("modal", false);
            options.put("minimizable", true);
            options.put("maximizable", true);
            options.put("draggable", true);
            options.put("resizable", true);
            options.put("width", 1024);
            options.put("height", 780);
            options.put("contentWidth", 1024);
            options.put("contentHeight", 780);
            options.put("closeOnEscape", true);
            options.put("fitViewport", true);
            options.put("responsive", true);

            this.setTreeNode(this.typentityController.fillTreeNodes(this.ucet.getAccount().getIdmodel()));
            PrimeFaces.current().dialog().openDynamic("/aplikace/registrace/viewModelTree.xhtml", options, null);
        } else {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Nebyl vybrán žádný model (šablona), není co zobrazit."));
        }
    }

    public void closeModelTreeTable() {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void onNodeSelect(NodeSelectEvent event) {
        Typentity typentity = (Typentity) event.getTreeNode().getData();
        this.setAttributes((typentity == null) ? null : attributeController.getAttributeForTypentity(typentity));
    }

    /**
     * @return the treeNode
     */
    public TreeNode getTreeNode() {
        return treeNode;
    }

    /**
     * @param treeNode the treeNode to set
     */
    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
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
