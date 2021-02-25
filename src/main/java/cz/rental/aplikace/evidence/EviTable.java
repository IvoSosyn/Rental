/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.aplikace.Ucet;
import cz.rental.entity.Attribute;
import cz.rental.entity.Entita;
import cz.rental.entity.Typentity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces.Dialog;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author ivo
 */
@Named(value = "eviTable")
@SessionScoped
public class EviTable implements Serializable {

    static final long serialVersionUID = 42L;

    static final int COUNT_ENTITA_NEW = 1;

    @EJB
    cz.rental.entity.TypentityController typentityController;
    @EJB
    cz.rental.entity.EntitaController entitaController;
    @EJB
    cz.rental.entity.AttributeController attrController;

    @Inject
    private Ucet ucet;

    @Inject
    private EviForm eviAttribute;

    private Entita parentEntita = null;
    private Typentity typentity = null;
    private ArrayList<Entita> entities = new ArrayList<>();
    private Entita selectedEntita = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<String> columns = new ArrayList<>();
    private ArrayList<Typentity> typentityChilds = new ArrayList<>();

    private Dialog dialog;
    private ArrayList<String> columnsSource = new ArrayList<>();
    private DualListModel<String> columnsDualList;

    @PostConstruct
    public void init() {
    }

    /**
     * Metoda 1. nacte vsechny instance "Entita" pro Entita.idparent==parent.id,
     * pole Attribute pro parametr "typentity" 2. prida nove instance "Entita"
     * do pole "entities" - vytvori matici tlacitek k dalsimu pouziti v
     * 'eviButtons.xml'
     *
     *
     * @param parent - Entita(uzel), pro kerou se vybere matice nejblizsich
     * podrizenych entit
     * @param typentity
     */
    public void loadTable(Entita parent, Typentity typentity) {
        if (parent == null || typentity == null) {
            return;
        }
        if (this.attributes.isEmpty() || !this.typentity.equals(typentity)) {
            this.attributes = attrController.getAttributeForTypentity(typentity);
        }
        this.parentEntita = parent;
        this.typentity = typentity;
        this.entities = entitaController.getEntities(parent);
        // Pridani radku nove zaznamu Entita
        for (int i = 0; i < EviTable.COUNT_ENTITA_NEW; i++) {
            // Nova Entita
            Entita newEntita = new Entita();
            newEntita.setId(UUID.randomUUID());
            newEntita.setIdparent(parent.getId());
            newEntita.setIdtypentity(this.getTypentity());
            newEntita.setPopis("Nový záznam");
            newEntita.setNewEntity(true);
            this.entities.add(newEntita);
        }
        // Naselectuji 0-tou Entita, pokud neni jiz preddefinovana
        if (this.selectedEntita == null) {
            this.selectedEntita = this.entities.get(0);
        }
        // Nacist hodnoty Attribute 
        eviAttribute.loadAttributes(this.selectedEntita);
        // Child Typentity pro tlacitkovou listu
        this.setTypentityChilds(typentityController.getTypentityChilds(this.typentity));
    }

    /**
     * Metoda pro opakovane nacteni matice Entita slouzi k aktualizaci
     */
    public void reLoadEntities() {
        loadTable(this.parentEntita, this.typentity);
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
     * @param attribute Polozka sloupce
     * @return hodnotu bud z relacnich tabulek pro Attribute.popis nebo z
     * Entita.metoda nebo z pole Typentity.metoda
     */
    public String getColumnHeader(Entita entita, Attribute attribute) {
        return (attribute instanceof Attribute ? attribute.getPopis() : "?");
    }

    /**
     * Metoda pro hodnoty do tabulky prehledu Entit pro nasledny vyber detailu
     *
     * @param entita instance Entity, pro kterou hledam hodnotu bud z jejiho
     * pole nebo z Attributa tabulky a jejich relacnich hodnot nebo z pole
     * Typentity
     * @param attribute Attribute pro ktery hledame hodnotu
     * @return hodnotu bud z relacnich tabulek pro Attribute nebo z
     * Entita.get[metoda] nebo z pole Typentity.get[metoda]
     */
    public String getColumnValue(Entita entita, Attribute attribute) {
        String value = "";
        if (entita == null || entita.getIdtypentity() == null || attribute == null) {
            return value;
        }

        Object obj = attrController.getAttrValue(entita, attribute, (Date) null, (Date) null);
        if (obj != null) {
            value = obj.toString();
        }
        return value;
    }

    public void onRowSelect(SelectEvent event) {
//        System.out.println("EviEntita.onRowSelect  event.getObject()=" + event.getObject());
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("EviEntita.onRowSelect - dosud neimplementováno", ((Entita)event.getObject()).getPopis()));
        eviAttribute.loadAttributes(((Entita) event.getObject()));
    }

    public void onRowUnselect(UnselectEvent event) {
//        System.out.println("EviEntita.onRowUnselect  event.getObject()=" + event.getObject());
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("EviEntita.onRowUnselect - dosud neimplementováno", ((Entita)event.getObject()).getPopis()));
        eviAttribute.loadAttributes(null);

    }

    public void gotoNewEntita() {
        for (Entita entita : entities) {
            if (entita.isNewEntity()) {
                this.selectedEntita = entita;
                eviAttribute.loadAttributes(this.selectedEntita);
                break;
            }
        }
    }

    /**
     * @return the ucet
     */
    public Ucet getUcet() {
        return ucet;
    }

    /**
     * @param ucet the ucet to set
     */
    public void setUcet(Ucet ucet) {
        this.ucet = ucet;
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

    /**
     * @return the eviAttribute
     */
    public EviForm getEviAttribute() {
        return eviAttribute;
    }

    /**
     * @param eviAttribute the eviAttribute to set
     */
    public void setEviAttribute(EviForm eviAttribute) {
        this.eviAttribute = eviAttribute;
    }

    /**
     * @return the typentityChilds
     */
    public ArrayList<Typentity> getTypentityChilds() {
        return typentityChilds;
    }

    /**
     * @param typentityChilds the typentityChilds to set
     */
    public void setTypentityChilds(ArrayList<Typentity> typentityChilds) {
        this.typentityChilds = typentityChilds;
    }

}
