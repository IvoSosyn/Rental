/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.aplikace.Uzivatel;
import cz.rental.aplikace.Ucet;
import cz.rental.entity.Typentity;
import cz.rental.utils.JSONExport;
import cz.rental.utils.JSONImport;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author sosyn
 */
@Named("modelTree")
@SessionScoped
public class ModelTree implements Serializable {

    static final long serialVersionUID = 42L;

    // ID z katalogu modelu(sablon)
    private Typentity typentityRoot = null;
    private ArrayList<Typentity> models = null;
    private Typentity typentity = null;
    private TreeNode rootNode = null;
    private TreeNode selectedNode = null;
    private TreeNode copyNode = null;

    int poradi = 0;

    @EJB
    cz.rental.entity.TypentityController typEntitycontroller;
    @EJB
    cz.rental.entity.AttrController attrController;
    @EJB
    JSONExport jsonExport;
    
    @Inject
    JSONImport jsonImport;

    @Inject
    ModelTable modelTable;

    @Inject
    Ucet ucet;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
//        try {
//            ucet = (Ucet) InitialContext.doLookup("java:module/Account!cz.rental.aplikace.registrace.Account");
//            user = (Uzivatel) InitialContext.doLookup("java:module/User!cz.rental.aplikace.User");
//        } catch (NamingException ex) {
//            Logger.getLogger(Ucet.class.getName()).log(Level.SEVERE, null, ex);
//        }
        fillTreeNodes();
    }

    /**
     * Metoda je volana pri udalosti "select" v SelectOneMenu komponente TO-DO:
     * zalozeni nove sablony-modelu doresit
     *
     * @param event
     */
    public void onModelSelect(SelectEvent event) {
        this.typentityRoot = (Typentity) event.getObject();
        fillTreeNodes();
    }

    /**
     * Hlavni metoda plnici "Tree" jednotlivými "TreeNode" V TreeNode.getData()
     * je ulozena konkretni instance "Typentity" na zaklade rodicovskeho UUID
     * "Typentity.idparent"
     */
    public void fillTreeNodes() {
        if (this.typentityRoot == null) {
            this.models = new ArrayList<>();
            this.typentityRoot = typEntitycontroller.getTypentity(ucet.getAccount().getId());
            if (this.typentityRoot != null) {
                this.models.add(this.typentityRoot);
            }
            if (ucet.getUzivatel().getParam(Uzivatel.SUPERVISOR, false)) {
                boolean added = this.models.addAll(typEntitycontroller.getRootTypEntity());
            }
            if (this.typentityRoot == null && !models.isEmpty()) {
                this.typentityRoot = models.get(0);
            }
        }
        this.typentity = new Typentity();
        this.rootNode = typEntitycontroller.fillTreeNodes(this.typentityRoot);
        modelTable.onNodeUnselect(null);
    }

    public void onNodeSelect(NodeSelectEvent event) {
        this.typentity = (Typentity) event.getTreeNode().getData();
        modelTable.loadAttributesForTypentity(this.getTypentity());
        // System.out.println(" onNodeSelect(NodeSelectEvent event): typentity.getTypentity(): " + typentity.getTypentity());
    }

    /**
     * Vymaze <CODE>selectedNode</CODE> z DB a z TreeNode + vymaz vsech potomku
     * a jejich Typentity (ulozeno v TreeNode.getData() ) a Attribute + vymaz
     * Attribute prislusneho Typentity z tohoto a podrizenych node
     */
    public void deleteNode() {
        System.out.println("deleteNode");
        if (selectedNode == null) {
            return;
        }
        // Smaze Typentity a Attribute z DB
        this.deleteFromDb(selectedNode);
        selectedNode.getParent().getChildren().remove(selectedNode);
        selectedNode = null;
        setTypentity(null);
    }

    /**
     * Smaze Typentity a Attribute z DB
     */
    private void deleteFromDb(TreeNode selectedNode) {
        try {
            Typentity typEntityDel = (Typentity) selectedNode.getData();
            if (typEntityDel.getAttrsystem() != null && typEntityDel.getAttrsystem() && !ucet.getUzivatel().getParam(Uzivatel.SUPERVISOR, false)) {
                return;
            }
            this.attrController.deleteAllTypentityId(typEntityDel.getId());
            this.typEntitycontroller.destroy(typEntityDel);
            for (TreeNode treeNode : selectedNode.getChildren()) {
                deleteFromDb(treeNode);
            }
        } catch (Exception ex) {
            Logger.getLogger(ModelTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addTypentity() {
        this.typentity = new Typentity();
        this.typentity.setIdparent(((Typentity) this.selectedNode.getData()).getId());
        this.typentity.setTypentity("Add " + poradi);
        this.typentity.setPopis("Nový evidenční uzel " + (poradi++));
        this.typentity.setEditor('F');
        this.typentity.setAttrsystem(false);
        this.typentity.setNewEntity(true);
        modelTable.loadAttributesForTypentity(this.typentity);
        openAllParent(this.selectedNode);
    }

    public void saveTypentity() {
        if (!(this.typentity instanceof Typentity)) {
            return;
        }
        try {
            if (this.typentity.isNewEntity()) {
                // Ulozit do DB
                typEntitycontroller.create(this.typentity);
                this.typentity = (Typentity) typEntitycontroller.findEntita(this.typentity);
                this.typentity.setNewEntity(false);
                TreeNode trn = new DefaultTreeNode(this.typentity);
                trn.setSelected(true);
                trn.setParent(this.selectedNode);
                this.selectedNode.getChildren().add(trn);
                this.setSelectedNode(trn);
                modelTable.loadAttributesForTypentity(this.getTypentity());
            } else {
                typEntitycontroller.edit(this.typentity);
            }
            openAllParent(this.selectedNode);
        } catch (Exception ex) {
            Logger.getLogger(ModelTree.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Záznam NEuložen !", "Uložení záznamu bylo NEúspěšné ! Postup opakujte později.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

    }

    public void editorChange(Object event) {
        System.out.println("ModelTree.editorChange event" + event);
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
            typEntitycontroller.edit(this.typentity);
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
     * Metoda testuje, zda-li ma uzivatel pravo vybrat model-sablonu SUPERVISOR
     * nevybira, tam ma k dispozici vsechny uzly od nejvyssiho, kde je
     * Typentity.idparent==null
     *
     * @return true|false uzivatel muze vybrat model-sablonu
     */
    public boolean isSelectableModel() {
        boolean isSelectableModel = true;
        return isSelectableModel;
    }

    /**
     * Metoda testuje, zda-li ma uzivatel prava pridavat zaznam
     *
     * @return uzivatel muze pridavat zaznamy
     */
    public boolean isAppendable() {
        boolean isAppendable = ucet.getUzivatel().getParam(Uzivatel.SUPERVISOR, false) || ucet.getUzivatel().getParam(cz.rental.aplikace.Uzivatel.MODEL_EDIT, false);
        return (isAppendable);
    }

    /**
     * Metoda testuje, zda-li je zaznam editovatelny a uzivatel ma pravo
     * editovat zaznam
     *
     * @return Typentity je editovatelny
     */
    public boolean isEditable() {
        boolean isEditable = (this.typentity != null);
        if (isEditable) {
            isEditable = ucet.getUzivatel().getParam(Uzivatel.SUPERVISOR, false);
            if (!isEditable) {
                isEditable = this.typentity.getAttrsystem() != null && !this.typentity.getAttrsystem() && ucet.getUzivatel().getParam(cz.rental.aplikace.Uzivatel.MODEL_EDIT, false);
            }
        }
        return (isEditable);
    }

    /**
     * Metoda testuje, zda-li je zaznam smazatelny a uzivatel ma pravo mazat
     *
     * @return Typentity je smazatelny
     */
    public boolean isRemovable() {
        boolean isRemoveable = (this.typentity != null);
        if (isRemoveable) {
            isRemoveable = ucet.getUzivatel().getParam(Uzivatel.SUPERVISOR, false);
            if (!isRemoveable) {
                isRemoveable = this.typentity.getAttrsystem() != null && !this.typentity.getAttrsystem() && ucet.getUzivatel().getParam(cz.rental.aplikace.Uzivatel.MODEL_EDIT, false);
            }
        }
        return (isRemoveable);
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
            dragNode.setSelected(false);
            this.setSelectedNode(pasteNode(dragNode, dropNode, dropIndex));
        } else {
            drag.setIdparent(drop.getId());
            // Ulozit do DB
            typEntitycontroller.edit(drag);
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
            selectedNode.setSelected(false);
            this.setSelectedNode(pasteNode(copyNode, selectedNode, -1));
            openAllParent(this.getSelectedNode());

        } catch (Exception ex) {
            Logger.getLogger(ModelTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Klonuje do nove Typentity a TreeNode hodnoty z materskeho
     * <CODE>node</CODE> a pripoji je k <CODE>nodeParent</CODE> TO-DO: Dodelat
     * kopie atriribute
     */
    private TreeNode pasteNode(TreeNode node, TreeNode nodeParent, int parentIndex) throws Exception {
        Typentity typEntityNew = typEntitycontroller.cloneTypentity((Typentity) node.getData());
        typEntityNew.setIdparent(((Typentity) nodeParent.getData()).getId());
        typEntityNew.setAttrsystem(false);
        typEntitycontroller.create(typEntityNew);
        typEntityNew.setNewEntity(false);
        attrController.cloneAttribute((Typentity) node.getData(), typEntityNew);

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

    public String getCopyNodeName() {
        return this.copyNode == null || this.copyNode.getData() == null || ((Typentity) this.copyNode.getData()) == null || ((Typentity) this.copyNode.getData()).getTypentity() == null ? "" : ((Typentity) this.copyNode.getData()).getTypentity();
    }

    /**
     * Metoda vyvola dialog pro prideleni sestav k danemu Typentity
     */
    public void editReports() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sestavy-Reports ... TO-DO", "Editace seznamu přidělených sestav ještě není hotova .. TO-DO");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /**
     * Metoda vyexportuje cely strom Typentity i s Attributes
     *
     * @return
     */
    public StreamedContent exportToJSON() throws FileNotFoundException {
        String jsonFileName = null;
        StreamedContent file = null;
        try {
            // Export "this.typentityRoot" to JSONExport
            jsonFileName = jsonExport.exportModel(this.typentityRoot);
        } catch (IOException ex) {
            Logger.getLogger(ModelTree.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Systémová chyba, opakujte později a chybu nahlaste zřizovateli. ", ex.getLocalizedMessage()));
        } catch (Exception e) {
            Logger.getLogger(ModelTree.class.getName()).log(Level.SEVERE, null, e);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Systémová chyba, opakujte později a chybu nahlaste zřizovateli. ", e.getLocalizedMessage()));
        }
        PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Export souboru byl úspěšný, jméno vyexportovaního souboru: ", jsonFileName));
        final String is = jsonFileName;
        file = DefaultStreamedContent.builder()
                .contentType("application/json")
                .name("Model.json")
                .stream(() -> {
                    try {
                        return new FileInputStream(is);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ModelTree.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return null;
                })
                .build();

        return file;
    }

    /**
     * Metoda načte data pro Model (provede Update nových položek nebo Insert)
     */
    public void importFromJSON() {
        jsonImport.importModel();
    }

    /**
     * @return the rootNode
     */
    public TreeNode getRootNode() {
        return rootNode;
    }

    /**
     * @param rootNode the rootNode to set
     */
    public void setRootNode(TreeNode rootNode) {
        this.rootNode = rootNode;
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

    /**
     * @return the typentityRoot
     */
    public Typentity getTypentityRoot() {
        return typentityRoot;
    }

    /**
     * @param typentityRoot the typentityRoot to set
     */
    public void setTypentityRoot(Typentity typentityRoot) {
        this.typentityRoot = typentityRoot;
    }

    /**
     * @return the models
     */
    public ArrayList<Typentity> getModels() {
        return models;
    }

    /**
     * @param models the models to set
     */
    public void setModels(ArrayList<Typentity> models) {
        this.models = models;
    }
}
