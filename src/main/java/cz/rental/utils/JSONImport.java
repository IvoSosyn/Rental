/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import cz.rental.entity.Typentity;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @EJB
    cz.rental.entity.TypentityController typEntitycontroller;
    @EJB
    cz.rental.entity.AttrController attrController;

    Typentity typentityRoot=null;
    Query query = null;
    Date platiOd = Aplikace.getPlatiOd();
    Date platiDo = Aplikace.getPlatiDo();

    private UploadedFile uploadFile = null;
    private boolean createNewModel = true;
    private boolean importTypentityID = false;
    private boolean importAttributeID = false;
    InputStream is = null;

    Object value = null;
//    HashSet<Field> hashSetSuperFields = new HashSet<>(Arrays.asList(EntitySuperClassNajem.class.getDeclaredFields()));
//    HashSet<Method> hashSetSuperMethods = new HashSet<>(Arrays.asList(EntitySuperClassNajem.class.getDeclaredMethods()));
//    HashSet<Field> hashSetTypentityFields = new HashSet<>(Arrays.asList(Typentity.class.getDeclaredFields()));
//    HashSet<Method> hashSetTypentityMethods = new HashSet<>(Arrays.asList(Typentity.class.getDeclaredMethods()));
//    HashSet<Field> hashSetAttributeFields = new HashSet<>(Arrays.asList(Attribute.class.getDeclaredFields()));
//    HashSet<Method> hashSetAttributeMethods = new HashSet<>(Arrays.asList(Attribute.class.getDeclaredMethods()));

    public void importModel(Typentity typentityRoot) {
        this.typentityRoot = typentityRoot;

        HashMap<String, Object> options = new HashMap<>();

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
        PrimeFaces.current().dialog().openDynamic("/admin/model/importModel.xhtml", options, null);
    }

    /**
     * Metoda nacte z JSON souboru data definujici model(sablonu) a aktualizuje bud aktualni model(sablonu) nebo vytvori novy
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
        processJsonObject(jso);

        if (is != null) {
            try {
                uploadFile.getInputStream().close();
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
     * @param jsonObject objekt ke zpracovani
     */
    private void processJsonObject(JsonObject jsonObject) {
        for (Map.Entry<String, JsonValue> jse : jsonObject.entrySet()) {
            System.out.println(" jse.getKey():" + jse.getKey() + " jse.getValueType():" + jse.getValue().getValueType() + " jse.getValue():" + jse.getValue());
            switch (jse.getValue().getValueType()) {
                case OBJECT: {
                    processJsonObject(jsonObject);
                    break;
                }
                case ARRAY: {
                    JsonArray jsa = jse.getValue().asJsonArray();
                    for (JsonValue jsonValue : jsa) {
                        System.out.println(" jsonValue.getValueType():" + jsonValue.getValueType());
                        switch (jsonValue.getValueType()) {
                            case OBJECT: {
                                processJsonObject(jsonValue.asJsonObject());
                                break;
                            }
                        }
                    }
                    break;
                }
                case STRING: {
                    break;
                }
                case NUMBER: {
                    break;
                }
                case TRUE: {
                    break;
                }
                case FALSE: {
                    break;
                }
                case NULL: {
                    break;
                }
            }
        }
    }

    public void closeModelDialog(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
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

}
