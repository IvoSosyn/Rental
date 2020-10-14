/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.aplikace.registrace.Account;
import cz.rental.entity.Attribute;
import cz.rental.entity.Entita;
import cz.rental.entity.Typentity;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.PrimeFaces.Dialog;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.model.DualListModel;

/**
 *
 * @author ivo
 */
@Named(value = "eviEntita")
@Stateless
public class EviEntita implements Serializable {

    static final long serialVersionUID = 42L;

    static final int COUNT_ENTITA_NEW = 1;

    @EJB
    cz.rental.entity.EntitaController entitaController;
    @EJB
    cz.rental.entity.AttributeController attrController;

    @Inject
    Account account;

    private Entita parentEntita = null;
    private Typentity typentity = null;
    private ArrayList<Entita> entities = new ArrayList<>();
    private Entita selectedEntita = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<String> columns = new ArrayList<>();

    private Dialog dialog;
    private ArrayList<String> columnsSource = new ArrayList<>();
    private DualListModel<String> columnsDualList;

    @PostConstruct
    public void init() {
//        try {
//            account = (Account) InitialContext.doLookup("java:module/Account!cz.rental.aplikace.registrace.Account");
//            user = (User) InitialContext.doLookup("java:module/User!cz.rental.aplikace.User");
//        } catch (NamingException ex) {
//            Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
//        }
        this.columns.add("Entita.Popis");
        this.columns.add("Typentity.Popis");
        this.columns.add("Attribute.DIC");
        this.columns.add("Entita.Platiod");
        this.columns.add("Entita.Platido");

        for (String column : this.columns) {
            getColumnsSource().add(column);
        }
        setColumnsDualList(new DualListModel<>(this.columnsSource, this.columns));

        this.typentity = new Typentity();
        this.typentity.setId(UUID.fromString("945889e4-4383-480e-9d77-dafe665fd475"));
        this.typentity.setPopis("945889e4-4383-480e-9d77-dafe665fd475");
        //
//        account.setCustomerID(UUID.fromString("34416c9f-26f2-44d8-b01d-6be4d6868dba"));
//        account.setCustomerModel(this.typentity);
        //
        this.parentEntita = new Entita();
//        this.parentEntita.setId(UUID.fromString("22ec3d58-67ce-4e6a-a692-c6d167f47528"));
        this.parentEntita.setId(null);
        this.parentEntita.setIdtypentity(this.typentity);
        loadEntities(this.parentEntita, this.typentity);
    }

    /**
     * Metoda 1. nacte vsechny instance "Entita" pro Entita.idparent==parent.id,
     * pole Attribute pro parametr "typentity" 2. prida nove instance "Entita"
     * do pole "entities"
     *
     *
     * @param parent
     * @param typentity
     */
    public void loadEntities(Entita parent, Typentity typentity) {
        if (typentity == null || parent == null) {
            return;
        }
        this.parentEntita = parent;
        this.typentity = typentity;
        this.entities = entitaController.getEntities(this.parentEntita);
        this.attributes = attrController.getAttributeForTypentity(typentity);
        for (int i = 0; i < EviEntita.COUNT_ENTITA_NEW; i++) {
            // Nova Entita
            Entita newEntita = new Entita();
            newEntita.setId(UUID.randomUUID());
            newEntita.setIdparent(parent.getId());
            newEntita.setIdtypentity(this.getTypentity());
            newEntita.setPopis(this.getTypentity().getPopis());
            newEntita.setNewEntity(true);
            this.entities.add(newEntita);
        }
    }

    public void columnsSelect() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Dosud neimplementovano", "Dosud neimplementovano"));
    }

    /**
     * Metoda pro nadpis sloupcu tabulky prehledu Entit pro nasledny vyber
     * detailu
     *
     * @param entita instance Entity, pro kterou hledam nadpis z Attributa.popis
     * tabulky z nazvu pole Typentity nebo nazvu pole Entita
     * @param column ve tvaru "Tabulka.pole" napr. "Entita.poznamka" nebo
     * "Attribute.prijemni"
     * @return hodnotu bud z relacnich tabulek pro Attribute.popis nebo z
     * Entita.metoda nebo z pole Typentity.metoda
     */
    public String getColumnHeader(Entita entita, String column) {
        String table = "Entita";
        String field = "popis";
        String value = column;
        String[] tableField;
        if (column == null || column.isEmpty()) {
            column = "Entita.popis";
        }
        tableField = column.split("\\.");
        switch (tableField.length) {
            case 0: {
                break;
            }
            case 1: {
                field = tableField[0];
                break;
            }
            case 2: {
                table = tableField[0];
                field = tableField[1];
                break;
            }
            default:
        }
        // Pokud se jedna o hodnotu Attribute je potreba dohledaat v DB
        if (table.equalsIgnoreCase("Attribute")) {
            value = column;
            if (this.getAttributes() != null) {
                for (Attribute attr : this.getAttributes()) {
                    if (attr.getAttrname().trim().equalsIgnoreCase(field.trim())) {
                        value = attr.getPopis();
                        break;
                    }
                }
            }
        }
        // Pokud se jedna o tabulku "Typentity" metoda vrati hodnotu pole z  instance Entita
        if (table.equalsIgnoreCase("Typentity")) {
            value = column;
        }
        // Pokud se jedna o tabulku "Entita" metoda vrati hodnotu pole z  instance Entita
        if (table.equalsIgnoreCase("Entita")) {
            value = column;
        }
        return value;
    }

    /**
     * Metoda pro hodnoty do tabulky prehledu Entit pro nasledny vyber detailu
     *
     * @param entita instance Entity, pro kterou hledam hodnotu bud z jejiho
     * pole nebo z Attributa tabulky a jejich relacnich hodnot nebo z pole
     * Typentity
     * @param column ve tvaru "Tabulka.pole" napr. Entita.poznamka nebo
     * Attribute.prijemni
     * @return hodnotu bud z relacnich tabulek pro Attribute nebo z
     * Entita.get[metoda] nebo z pole Typentity.get[metoda]
     */
    public String getColumnValue(Entita entita, String column) {
        String table = "Attribute";
        String field = "popis";
        String value = "";
        Typentity tpe = null;
        String[] tableField;
        if (entita == null || entita.getIdtypentity() == null) {
            return "";
        }
        tpe = entita.getIdtypentity();
        if (column == null || column.isEmpty()) {
            column = "Attribute.popis";
        }
        tableField = column.split("\\.");
        switch (tableField.length) {
            case 0: {
                break;
            }
            case 1: {
                field = tableField[0];
                break;
            }
            case 2: {
                table = tableField[0];
                field = tableField[1];
                break;
            }
            default:
        }
        // Pokud se jedna o hodnotu Attribute je potreba dohledaat v DB
        if (table.equalsIgnoreCase("Attribute") && this.getAttributes() != null) {
            for (Attribute attr : this.getAttributes()) {
                if (attr.getAttrname().trim().equalsIgnoreCase(field.trim())) {
                    //TO-DO: najit hodnotu v DB pro vybranou Attribute a Typentity
                    Object obj = attr.getPopis();
                    obj = attrController.getAttrValue(entita, attr, (Date) null, (Date) null);
                    if (obj != null) {
                        value = obj.toString();
                    }
                    ///
                    break;
                }
            }
        }
        // Pokud se jedna o tabulku "Typentity" metoda vrati hodnotu pole z  instance Entita
        if (table.equalsIgnoreCase("Typentity")) {
            Method method;
            try {
                method = tpe.getClass().getMethod("get" + field, new Class[]{});
                value = method.invoke(tpe, new Object[]{}).toString();
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(EviEntita.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Pokud se jedna o tabulku "Entita" metoda vrati hodnotu pole z  instance Entita
        if (table.equalsIgnoreCase("Entita")) {
            try {
                Method method = tpe.getClass().getMethod("get" + field, new Class[]{});
                value = (String) method.invoke(entita, new Object[]{});
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
                Logger.getLogger(EviEntita.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return value;
    }

    public String viewColumns() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("draggable", false);
        options.put("resizable", false);
        options.put("contentHeight", 320);

        this.dialog = PrimeFaces.current().dialog();
        this.dialog.openDynamic("/aplikace/evidence/evientitacolumn", options, null);
        return "";
    }

    public void configureColumns() {
        this.columns = new ArrayList(this.columnsDualList.getTarget());
//        this.dialog.closeDynamic(this.columnsDualList.getTarget());
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
     * @return the entities
     */
    public ArrayList<Entita> getEntities() {
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(ArrayList<Entita> entities) {
        this.entities = entities;
    }

    /**
     * @return the selectedEntita
     */
    public Entita getSelectedEntita() {
        return selectedEntita;
    }

    /**
     * @param selectedEntita the selectedEntita to set
     */
    public void setSelectedEntita(Entita selectedEntita) {
        this.selectedEntita = selectedEntita;
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
     * @return the entitaColumns
     */
    public ArrayList<String> getColumns() {
        return this.columns;
    }

    /**
     * @param columns the entitaColumns to set
     */
    public void setColumns(ArrayList<String> columns) {
        this.columns = columns;
    }

    /**
     * @return the columnsSource
     */
    public ArrayList<String> getColumnsSource() {
        return columnsSource;
    }

    /**
     * @param columnsSource the columnsSource to set
     */
    public void setColumnsSource(ArrayList<String> columnsSource) {
        this.columnsSource = columnsSource;
    }

    /**
     * @return the columnsDualList
     */
    public DualListModel<String> getColumnsDualList() {
        return columnsDualList;
    }

    /**
     * @param columnsDualList the columnsDualList to set
     */
    public void setColumnsDualList(DualListModel<String> columnsDualList) {
        this.columnsDualList = columnsDualList;
    }

}
