/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.entity.Attribute;
import cz.rental.entity.Entita;
import cz.rental.entity.Typentity;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

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
    cz.rental.aplikace.registrace.Account account;
    @Inject
    cz.rental.aplikace.User user;

    private Entita parentEntita = null;
    private Typentity typentity = null;
    private ArrayList<Entita> entities = new ArrayList<>();
    private Entita selectedEntita = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<ColumnModel> columns = new ArrayList<>();

    @PostConstruct
    public void init() {
        /**
         * <ui:include src="/aplikace/evidence/evientita.xhtml"/>
         *
         *
         * < f:facet name="header">
         * <h:outputText value="#{eviEntita.getColumnHeader(entita, entitaColumn)}" />
         * </f:facet>
         * <h:outputText value="#{eviEntita.getColumnValue(entita, entitaColumn)}" />
         *
         */

//        this.entitaColumns.add("Entita.Popis");
//        this.entitaColumns.add("Entita.Platiod");
//        this.entitaColumns.add("Entita.Platido");
        this.columns.add(new ColumnModel("Entita.Popis", "Entita.Popis"));
        this.columns.add(new ColumnModel("Entita.Platiod", "Entita.Platiod"));
        this.columns.add(new ColumnModel("Entita.Platiod", "Entita.Platiod"));

        this.typentity = new Typentity();
        this.typentity.setId(UUID.fromString("cac1b920-6b4f-4d2c-8308-86fc3fef5ec3"));
        //
        account.setCustomerID(UUID.fromString("34416c9f-26f2-44d8-b01d-6be4d6868dba"));
        account.setCustomerModel(this.typentity);
        //
        this.parentEntita = new Entita();
        this.parentEntita.setId(account.getCustomerID());
        this.parentEntita.setIdtypentity(account.getCustomerModel());
        loadEntities(this.parentEntita, account.getCustomerModel());
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
        this.setTypentity(typentity);
        this.setEntities(entitaController.getEntities(this.parentEntita));
        this.setAttributes(attrController.getAttributeForTypentity(typentity));
        for (int i = 0; i < EviEntita.COUNT_ENTITA_NEW; i++) {
            // Nova Entita
            Entita newEntita = new Entita();
            newEntita.setId(UUID.randomUUID());
            newEntita.setIdparent(parent.getId());
            newEntita.setIdtypentity(this.getTypentity());
            newEntita.setPopis(this.getTypentity().getPopis());
            newEntita.setNewEntity(true);
        }
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
        tableField = column.split(".");
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
                    value = attr.getPopis();
                    break;
                }
            }
        }
        // Pokud se jedna o tabulku "Typentity" metoda vrati hodnotu pole z  instance Entita
        if (table.equalsIgnoreCase("Typentity")) {
            value = field;
        }
        // Pokud se jedna o tabulku "Entita" metoda vrati hodnotu pole z  instance Entita
        if (table.equalsIgnoreCase("Entita")) {
            value = field;
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
        tableField = column.split(".");
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
                    value = attr.getPopis();
                    value = attrController.getAttrValue(entita, attr, (Date) null, (Date) null).toString();
                    ///
                    break;
                }
            }
        }
        // Pokud se jedna o tabulku "Typentity" metoda vrati hodnotu pole z  instance Entita
        if (table.equalsIgnoreCase("Typentity")) {
            Method method;
            try {
                method = tpe.getClass().getMethod("get" + field, (Class) null);
                value = method.invoke(tpe, (Object) null).toString();
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(EviEntita.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Pokud se jedna o tabulku "Entita" metoda vrati hodnotu pole z  instance Entita
        if (table.equalsIgnoreCase("Entita")) {
            Method method;
            try {
                method = entita.getClass().getMethod("get" + field, (Class) null);
                value = method.invoke(entita, (Object) null).toString();
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(EviEntita.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return value;
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
    public ArrayList<ColumnModel> getColumns() {
        return this.columns;
    }

    /**
     * @param columns the entitaColumns to set
     */
    public void setColumns(ArrayList<ColumnModel> columns) {
        this.columns = columns;
    }

    static public class ColumnModel implements Serializable {

        private String header;
        private String property;

        public ColumnModel(String header, String property) {
            this.header = header;
            this.property = property;
        }

        public String getHeader() {
            return header;
        }

        public String getProperty() {
            return property;
        }
    }

}
