/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.utils;

import cz.rental.entity.Attribute;
import cz.rental.entity.EntitySuperClassNajem;
import cz.rental.entity.Typentity;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.json.Json;
import javax.json.stream.JsonParser;
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

    Query query = null;
    Date platiOd = Aplikace.getPlatiOd();
    Date platiDo = Aplikace.getPlatiDo();

    private UploadedFile uploadFile=null;
    private boolean createNewModel = false;
    private boolean importTypentityID = false;
    private boolean importAttributeID = false;
    InputStream is = null;

    Object value = null;
    HashSet<Field> hashSetSuperFields = new HashSet<>(Arrays.asList(EntitySuperClassNajem.class.getDeclaredFields()));
    HashSet<Method> hashSetSuperMethods = new HashSet<>(Arrays.asList(EntitySuperClassNajem.class.getDeclaredMethods()));
    HashSet<Field> hashSetTypentityFields = new HashSet<>(Arrays.asList(Typentity.class.getDeclaredFields()));
    HashSet<Method> hashSetTypentityMethods = new HashSet<>(Arrays.asList(Typentity.class.getDeclaredMethods()));
    HashSet<Field> hashSetAttributeFields = new HashSet<>(Arrays.asList(Attribute.class.getDeclaredFields()));
    HashSet<Method> hashSetAttributeMethods = new HashSet<>(Arrays.asList(Attribute.class.getDeclaredMethods()));

    public void importModel() {
        HashMap<String, Object> options = new HashMap<>();

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
        PrimeFaces.current().dialog().openDynamic("/admin/model/importModel.xhtml", options, null);
    }

    public void importFromJsonToModel(ActionEvent actionEvent) {
        if (uploadFile == null) {
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
        JsonParser jsp = Json.createParser(is);
        // START_OBJECT, END_OBJECT, START_ARRAY, END_ARRAY, KEY_NAME, VALUE_STRING, VALUE_NUMBER, VALUE_TRUE, VALUE_FALSE, and VALUE_NULL
        while (jsp.hasNext()) {
            JsonParser.Event event = jsp.next();
            switch (event) {
                case START_OBJECT: {
                    System.out.println(" " + event.name());
                    break;
                }
                case END_OBJECT: {
                    System.out.println(" " + event.name());
                    break;
                }
                case START_ARRAY: {
                    System.out.println(" " + event.name());
                    break;
                }
                case END_ARRAY: {
                    System.out.println(" " + event.name());
                    break;
                }
                case KEY_NAME: {
                    System.out.println(" " + jsp.getString());
                    break;
                }
                case VALUE_STRING: {
                    System.out.println(" " + jsp.getString());

                    break;
                }
                case VALUE_NUMBER: {
                    System.out.println(" " + jsp.getLong());

                    break;
                }
                case VALUE_TRUE: {
                    System.out.println(" " + jsp.getValue());
                    break;
                }
                case VALUE_FALSE: {
                    System.out.println(" " + jsp.getValue());
                    break;
                }
                case VALUE_NULL: {
                    System.out.println(" " + jsp.getValue());
                    break;
                }
            }
        }
        if (is != null) {
            try {
                uploadFile.getInputStream().close();
            } catch (IOException ex) {
                Logger.getLogger(JSONImport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        closeModelDialog(actionEvent);
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
