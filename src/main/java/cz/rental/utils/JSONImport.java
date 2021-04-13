/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import cz.rental.entity.Attribute;
import cz.rental.entity.Typentity;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.persistence.Query;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author sosyn
 */
@Named(value = "jsonImport")
@SessionScoped
public class JSONImport implements Serializable {

    private static final long serialVersionUID = 42L;

    @EJB
    cz.rental.entity.TypentityController typEntitycontroller;
    @EJB
    cz.rental.entity.AttrController attrController;

    Typentity typentityRoot = null;
    boolean succes = false;
    Query query = null;
    Date platiOd = Aplikace.getPlatiOd();
    Date platiDo = Aplikace.getPlatiDo();

    private UploadedFile uploadFile = null;
    private ArrayList<String[]> howToImportModels=null;
    private String howToImportModel = "AktJsonId";
    private ArrayList<String[]> howToImportTypentities=null;
    private String howToImportTypentity = "TypEntityName";
    private ArrayList<String[]> howToImportAttributes = null;
    private String howToImportAttribute = "AttrName";

    private boolean aktSelectedModel = true;
    private boolean aktJsonModel = false;
    private boolean createNewModel = false;
    private boolean importTypentityID = false;
    private boolean importAttributeID = false;

    InputStream is = null;
    // "Insert Code > Add Business Method")

    public JSONImport() {
        this.howToImportModels = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        // Rezim importu z JSON souboru
        setHowToImportModels(new ArrayList<>());
        getHowToImportModels().add(new String[]{"Aktuálně vybraný model","AktSelectedId"});
        getHowToImportModels().add(new String[]{"Model uvedený v JSON souboru","AktJsonId"});
        getHowToImportModels().add(new String[]{"Vytvořit zcela nový model","CreateNewId"});
        // Rezim importu uzlu Typentity
        setHowToImportTypentities(new ArrayList<>());
        getHowToImportTypentities().add(new String[]{"Názvu typu entity (doporučeno)","TypEntityName"});
        getHowToImportTypentities().add(new String[]{"ID typu entity","TypEntityId"});
        // Rezim importu Attributes
        setHowToImportAttributes(new ArrayList<>());
        getHowToImportAttributes().add(new String[]{"Názvu atributu (doporučeno)","AttrName"});
        getHowToImportAttributes().add(new String[]{"ID atributu","AttrId"});
    }

    public void importModel(Typentity typentityRoot) {

        this.succes = false;
        this.typentityRoot = typentityRoot;

        HashMap<String, Object> options = new HashMap<>();
        HashMap<String, List<String>> params = new HashMap<>();

        options.put("modal", false);
        options.put("minimizable", true);
        options.put("maximizable", true);
        options.put("draggable", false);
        options.put("resizable", true);
        options.put("width", 800);
        options.put("height", 600);
        options.put("contentWidth", 800);
        options.put("contentHeight", 600);
        options.put("closeOnEscape", true);
        options.put("fitViewport", true);
        options.put("responsive", true);

        ArrayList list = new ArrayList();
        list.add(typentityRoot.getId().toString());
        params.put("typentityRoot", list);

        PrimeFaces.current().dialog().openDynamic("/admin/model/importModel.xhtml", options, params);
    }

    /**
     * Metoda uzavre dialog a preda data volajicimu objektu (uspech nebo
     * neuspech importu)
     *
     * @param actionEvent - udalost uzavirajici dialog - source=commandButton
     */
    public void closeModelDialog(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(this.succes);
    }

    /**
     * Metoda nacte z JSON souboru data definujici model(sablonu) a aktualizuje
     * bud aktualni model(sablonu) nebo vytvori novy
     *
     * @param actionEvent - button event z dialogu
     */
    public void importFromJsonToModel(ActionEvent actionEvent) {
        if (uploadFile == null || uploadFile.getFileName() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Není vybrán správný soubor s daty!!", "Nelze provést načtení do databáze !!"));
            return;
        }
        try {
            is = uploadFile.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(JSONImport.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nelze načíst soubor " + uploadFile.getFileName() + " s daty!!", "Nelze provést načtení do databáze !!"));
            return;
        }

        JsonReader jsr = Json.createReader(is);
        JsonObject jso = jsr.readObject().getJsonObject("MODEL");
        if (jso == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Soubor " + uploadFile.getFileName() + " nemá správný formát!!", "Nelze provést načtení do databáze !!"));
            return;
        }

        // Start rekurzivniho volani nad JSON objekty
        Typentity typentity = null;
        UUID typEntityId = jso.isNull("id") ? null : UUID.fromString(jso.asJsonObject().getString("id"));
        String typEntityTyp = jso.isNull("typentity") ? null : jso.getString("typentity");

        if (!this.howToImportModel.equals("CreateNewId")) {
            // Aktualizovat model z JSON souboru
            if (this.howToImportModel.equals("AktJsonId")) {
                typentity = this.typEntitycontroller.getTypentity(typEntityId);
            }
            // Aktualizovat aktualne vybrany model
            if (this.howToImportModel.equals("AktSelectedId")) {
                typentity = this.typentityRoot;
            }
        }

        if (typentity == null) {
            typentity = new Typentity();
            typentity.setId(UUID.randomUUID());
            typentity.setIdparent(null);
        }
        try {
            processJsonTypentityObject(jso, typentity);
        } catch (Exception ex) {
            Logger.getLogger(JSONImport.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Import souboru " + uploadFile.getFileName() + " NEbyl úspěšný.Data se nepodřilo uložit do databáze, opravte JSON soubor a postup opakujte.", ex.getLocalizedMessage()));
        }

        if (is != null) {
            try {
                uploadFile.getInputStream().close();
                this.succes = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Import souboru " + uploadFile.getFileName() + " byl úspěšný.", "Model(šablona) byl načten do databáze."));
            } catch (IOException ex) {
                Logger.getLogger(JSONImport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        closeModelDialog(actionEvent);
    }

    /**
     * Metoda zpracuje JsonObject definujici Typentity nebo Attribute rozebere
     * .json soubor na jdnotlive cleny a ty dale zpracuje do DB jako nove nebo
     * jako aktualizaci
     *
     * @param jso objekt JSON ke zpracovani
     * @param typentity aktualni Typentity k naplneni hodnotami z JSON objektu a
     * dale jako parent parametr do rekurzivniho volani
     * @throws java.lang.Exception
     */
    public void processJsonTypentityObject(JsonObject jso, Typentity typentity) throws Exception {
        // Naplnit objekt Typentity
        fillTypentity(jso, typentity);

        // Zpracovat pole s Attribute pro typentity
        JsonArray jsonAttrArray = jso.getJsonArray(JSONExport.ATTRIBUTES_ARRAY_NAME);
        for (JsonValue jsonValue : jsonAttrArray) {
            createAttribute(jsonValue.asJsonObject(), typentity);
        }
        // Zpracovat pole s podrizenymi Typentities pro parent typentity
        JsonArray jsonTypentityArray = jso.getJsonArray(JSONExport.TYPENTITIES_ARRAY_NAME);
        UUID typEntityId;
        for (JsonValue jsonValue : jsonTypentityArray) {
            Typentity newTypentity = null;
            typEntityId = jsonValue.asJsonObject().isNull("id") ? null : UUID.fromString(jsonValue.asJsonObject().getString("id"));
            if (this.importTypentityID && typEntityId != null) {
                newTypentity = this.typEntitycontroller.getTypentity(typEntityId);
            }
            if (newTypentity == null) {
                newTypentity = new Typentity();
                newTypentity.setId(UUID.randomUUID());
                newTypentity.setIdparent(typentity.getId());
            }
            processJsonTypentityObject(jsonValue.asJsonObject(), newTypentity);
        }
    }

    private void fillTypentity(JsonObject jso, Typentity typentity) throws Exception {

        typentity.setTypentity(jso.getString("typentity", "???"));
        typentity.setEditor(jso.getString("editor", "F").charAt(0));
        typentity.setIdmodel(!jso.isNull("idmodel") ? UUID.fromString(jso.getString("idmodel")) : null);

        typentity.setAttrsystem(jso.getBoolean("attrsystem", false));
        typentity.setPopis(jso.getString("popis", " "));

        // Ulozit do databaze
        if (typentity.isNewEntity()) {
            this.attrController.create(typentity);
            typentity.setNewEntity(false);
        } else {
            this.attrController.edit(typentity);
        }
    }

    private void createAttribute(JsonObject jso, Typentity typentity) throws Exception {
        UUID attrId = jso.isNull("id") ? null : UUID.fromString(jso.getString("id"));
        String attrName = jso.getString("attrname");

        Attribute attribute = null;
        if (this.importAttributeID) {
            attribute = this.attrController.getAttribute(attrId);

        } else {
            attribute = this.attrController.getAttribute(attrName, typentity, null);
        }
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setId(UUID.randomUUID());
            attribute.setIdtypentity(typentity.getId());
            attribute.setNewEntity(true);
        }

        attribute.setAttrname(attrName);
        attribute.setAttrtype(jso.getString("attrtype", "C").charAt(0));
        attribute.setAttrsize(new BigInteger(jso.getString("attrsize", "10")));
        attribute.setAttrdecimal(new BigInteger(jso.getString("attrdecimal", "0")));
        attribute.setAttrparser(jso.getString("attrparser", ""));
        attribute.setAttrmask(jso.getString("attrmask", ""));
        attribute.setTables(jso.getString("tables", ""));
        attribute.setScript(jso.getString("sript", ""));
        attribute.setPoradi(jso.getInt("poradi", 100));

        attribute.setAttrsystem(jso.getBoolean("attrsystem", false));
        attribute.setPopis(jso.getString("popis", ""));

        // Ulozit do databaze
        if (attribute.isNewEntity()) {
            this.attrController.create(attribute);
            attribute.setNewEntity(false);
        } else {
            this.attrController.edit(attribute);
        }

    }

    /**
     * @return the uploadFile
     */
    public UploadedFile getUploadFile() {
        return uploadFile;
    }

    /**
     * @param uploadFile the uploadFile to set
     */
    public void setUploadFile(UploadedFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    /**
     * @return the importTypentityID
     */
    public boolean isImportTypentityID() {
        return importTypentityID;
    }

    /**
     * @param importTypentityID the importTypentityID to set
     */
    public void setImportTypentityID(boolean importTypentityID) {
        this.importTypentityID = importTypentityID;
    }

    /**
     * @return the importAttributeID
     */
    public boolean isImportAttributeID() {
        return importAttributeID;
    }

    /**
     * @param importAttributeID the importAttributeID to set
     */
    public void setImportAttributeID(boolean importAttributeID) {
        this.importAttributeID = importAttributeID;
    }

    /**
     * @return the createNewModel
     */
    public boolean isCreateNewModel() {
        return createNewModel;
    }

    /**
     * @param createNewModel the createNewModel to set
     */
    public void setCreateNewModel(boolean createNewModel) {
        this.createNewModel = createNewModel;
    }

    /**
     * @return the aktSelectedModel
     */
    public boolean isAktSelectedModel() {
        return aktSelectedModel;
    }

    /**
     * @param aktSelectedModel the aktSelectedModel to set
     */
    public void setAktSelectedModel(boolean aktSelectedModel) {
        this.aktSelectedModel = aktSelectedModel;
    }

    /**
     * @return the aktJsonModel
     */
    public boolean isAktJsonModel() {
        return aktJsonModel;
    }

    /**
     * @param aktJsonModel the aktJsonModel to set
     */
    public void setAktJsonModel(boolean aktJsonModel) {
        this.aktJsonModel = aktJsonModel;
    }

    /**
     * @return the howToImportModel
     */
    public String getHowToImportModel() {
        return howToImportModel;
    }

    /**
     * @param howToImportModel the howToImportModel to set
     */
    public void setHowToImportModel(String howToImportModel) {
        this.howToImportModel = howToImportModel;
    }

    /**
     * @return the howToImportTypentity
     */
    public String getHowToImportTypentity() {
        return howToImportTypentity;
    }

    /**
     * @param howToImportTypentity the howToImportTypentity to set
     */
    public void setHowToImportTypentity(String howToImportTypentity) {
        this.howToImportTypentity = howToImportTypentity;
    }

    /**
     * @return the howToImportAttribute
     */
    public String getHowToImportAttribute() {
        return howToImportAttribute;
    }

    /**
     * @param howToImportAttribute the howToImportAttribute to set
     */
    public void setHowToImportAttribute(String howToImportAttribute) {
        this.howToImportAttribute = howToImportAttribute;
    }

    /**
     * @return the howToImportModels
     */
    public ArrayList<String[]> getHowToImportModels() {
        return howToImportModels;
    }

    /**
     * @param howToImportModels the howToImportModels to set
     */
    public void setHowToImportModels(ArrayList<String[]> howToImportModels) {
        this.howToImportModels = howToImportModels;
    }

    /**
     * @return the howToImportTypentities
     */
    public ArrayList<String[]> getHowToImportTypentities() {
        return howToImportTypentities;
    }

    /**
     * @param howToImportTypentities the howToImportTypentities to set
     */
    public void setHowToImportTypentities(ArrayList<String[]> howToImportTypentities) {
        this.howToImportTypentities = howToImportTypentities;
    }

    /**
     * @return the howToImportAttributes
     */
    public ArrayList<String[]> getHowToImportAttributes() {
        return howToImportAttributes;
    }

    /**
     * @param howToImportAttributes the howToImportAttributes to set
     */
    public void setHowToImportAttributes(ArrayList<String[]> howToImportAttributes) {
        this.howToImportAttributes = howToImportAttributes;
    }


}
