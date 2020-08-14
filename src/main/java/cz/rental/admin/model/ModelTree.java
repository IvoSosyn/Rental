/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.entity.Typentity;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TreeDragDropEvent;
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
    private TreeNode copyNode = null;
    private Typentity typentity = null;

    int poradi = 0;

    @EJB
    cz.rental.entity.TypentityController controller;
    @EJB
    cz.rental.admin.model.ModelTable modelTable;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
        setRoot(controller.fillTreeNodes());
    }

    public void onNodeSelect(NodeSelectEvent event) {
        setTypentity((Typentity) event.getTreeNode().getData());
        modelTable.getAttributeForTypentity(this.getTypentity());
        // System.out.println(" onNodeSelect(NodeSelectEvent event): typentity.getTypentity(): " + typentity.getTypentity());
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
        if (selectedNode == null) {
            return;
        }
        selectedNode.getChildren().clear();
        selectedNode.getParent().getChildren().remove(selectedNode);
        selectedNode.setParent(null);

        selectedNode = null;
        setTypentity(null);
    }

    public void addTypentity() {
        TreeNode parent = selectedNode;
        if (parent == null) {
            return;
        }
        try {
            this.typentity = new Typentity();
            this.typentity.setIdparent(((Typentity) parent.getData()).getId());
            this.typentity.setTypentity("Add " + (poradi++));
            this.typentity.setPopis("Nový evidenční uzel " + poradi);
            this.typentity.setEditor('F');
            this.typentity.setAttrsystem(false);
            // Ulozit do DB
            controller.create(this.typentity);
            TreeNode trn = new DefaultTreeNode(this.typentity);
            trn.setSelected(true);
            trn.setParent(parent);
            parent.getChildren().add(trn);
            this.setSelectedNode(trn);
            openAllParent(trn);
        } catch (Exception ex) {
            Logger.getLogger(ModelTree.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Záznam NEpřidán !", "Přidání záznamu bylo NEúspěšné ! Postup opakujte později.");
            FacesContext.getCurrentInstance().addMessage(null, message);
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
        try {
            // Ulozit do DB
            controller.edit(this.typentity);
        } catch (Exception ex) {
            Logger.getLogger(ModelTree.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Záznam NEuložen !", "Uložení změny záznamu " + e + " bylo NEúspěšné ! Postup opakujte později.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

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

    /**
     * TO-DO: Zavesit na prava uzivatele
     *
     * @return typentity je editovatelny
     */
    public boolean isEditable() {
        return (this.typentity != null && this.typentity.getAttrsystem() != null && !this.typentity.getAttrsystem());

    }

    /**
     * Premisteni nebo kopie TreeNode cestou Drag&Drop
     *
     * @param event - udalost, nesouci objekty DragAndDrop
     * @throws Exception
     */
    public void onDragDrop(TreeDragDropEvent event) throws Exception {
        TreeNode dragNode = event.getDragNode();
        TreeNode dropNode = event.getDropNode();
        int dropIndex = event.getDropIndex();
        Typentity drag = (Typentity) event.getDragNode().getData();
        Typentity drop = (Typentity) event.getDropNode().getData();
        this.copyNode = dragNode;
        if (event.isDroppedNodeCopy()) {
            this.setSelectedNode(pasteNode(dragNode, dropNode, dropIndex));
        } else {
            drag.setIdparent(drop.getId());
            // Ulozit do DB
            controller.edit(drag);
            this.setSelectedNode(dragNode);
        }
        this.getSelectedNode().setSelected(true);
        openAllParent(this.getSelectedNode());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Dragged " + dragNode.getData(), "Dropped on " + dropNode.getData() + " at " + dropIndex);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /*
                                    <!--
                                <p:ajax process="idTypentity" event="valueChange" listener="#{modelTree.valueChange(modelTree.typentity.typentity)}" />
                                <p:ajax   process="idTypentity" event="valueChange" listener="#{modelTree.valueChange}" />
                                -->
     */
    public void copyNodeFrom() {
        if (selectedNode == null) {
            return;
        }
        this.copyNode = this.selectedNode;
    }

    public void pasteNodeTo() {
        if (selectedNode == null) {
            return;
        }
        if (copyNode == null) {
            return;
        }
        // Naklonovat TreeNode ze zasobniku do vybraneho TreeNode
        try {
            this.setSelectedNode(pasteNode(copyNode, selectedNode, -1));
            openAllParent(this.getSelectedNode());

        } catch (Exception ex) {
            Logger.getLogger(ModelTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Klonuje do nove Typentity a TreeNode hodnoty z materskeho node a pripoji
     * je k nodeParent TO-DO: Dodelat kopie atriribute
     */
    private TreeNode pasteNode(TreeNode node, TreeNode nodeParent, int parentIndex) throws Exception {
        Typentity typEntityNew = controller.cloneTypentity((Typentity) node.getData());
        typEntityNew.setIdparent(((Typentity) nodeParent.getData()).getId());
        controller.create(typEntityNew);

        TreeNode treeNodeNew = new DefaultTreeNode(typEntityNew);
        treeNodeNew.setParent(nodeParent);

        for (TreeNode treeNodeChild : node.getChildren()) {
            treeNodeNew.getChildren().add(pasteNode(treeNodeChild, treeNodeNew, -1));
        }
        if (parentIndex < 0) {
            nodeParent.getChildren().add(treeNodeNew);
        } else {
            nodeParent.getChildren().set(parentIndex, treeNodeNew);
        }

        return treeNodeNew;
    }

    private void openAllParent(TreeNode parent) {
        while (parent.getParent() != null) {
            parent = parent.getParent();
            parent.setSelected(false);
            parent.setExpanded(true);
        }
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

    /**
     * @return the pasteNode
     */
    public TreeNode getCopyNode() {
        return copyNode;
    }

    /**
     * @param copyNode the pasteNode to set
     */
    public void setCopyNode(TreeNode copyNode) {
        this.copyNode = copyNode;
    }

}
