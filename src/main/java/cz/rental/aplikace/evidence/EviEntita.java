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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.PrimeFaces.Dialog;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author ivo
 */
@Named(value = "eviEntita")
@SessionScoped
public class EviEntita implements Serializable {

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
    private EviAttribute eviAttribute;

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
//        try {
//            ucet = (Ucet) InitialContext.doLookup("java:module/Account!cz.rental.aplikace.registrace.Ucet");
//            user = (User) InitialContext.doLookup("java:module/User!cz.rental.aplikace.User");
//        } catch (NamingException ex) {
//            Logger.getLogger(Ucet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
        this.parentEntita = new Entita();
        this.parentEntita.setId(ucet.getAccount().getId());
        // 
        // Dohledat model Typentity s jeho definovanymi Attribute pro matici Entita
        this.typentity = ucet.getAccount().getIdmodel();
        if (this.typentity != null && this.typentity.getIdparent() == null) {
            // V pripade vrcholove Typentity najdu nejblizsi dalsi
            this.typentity = typentityController.getTypentityForParentID(this.typentity.getId());
        }
        if (this.typentity == null) {
            this.typentity = new Typentity();
            this.typentity.setId(UUID.fromString("945889e4-4383-480e-9d77-dafe665fd475"));
            this.typentity.setPopis("Nejvyšší entita");
        }
        this.parentEntita.setIdtypentity(this.typentity);

        // Nacist matici Entita pro Typentita a null jako parent entitu-nema predka  
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
        this.attributes = attrController.getAttributeForTypentity(this.typentity);
        // Definice sloupcu tabulky Entity
        // TO-DO: nacist z konfigurace uzivatele pro dany Typentity        
        this.columns = new ArrayList<>();
        String userColumns = this.ucet.getUzivatel().getParam(this.typentity.getId().toString(), "");
        if (!userColumns.isEmpty()) {
            this.columns.addAll(Arrays.asList(userColumns.split(";")));
        }
        // Doplnim pole pouzitelnych sloupcu o promenne z Entita
        for (String entitaColumns : new String[]{"Entita.Popis", "Entita.Platiod", "Entita.Platido"}) {
            if (!columns.contains(entitaColumns)) {
                getColumnsSource().add(entitaColumns);
            }
        }
        // Doplnim pole pouzitelnych sloupcu o Attribute
        for (Attribute attr : this.attributes) {
            String column = "Attribute." + attr.getAttrname().trim();
            if (!columns.contains(column)) {
                getColumnsSource().add(column);
            }
        }
        setColumnsDualList(new DualListModel<>(this.columnsSource, this.columns));

        for (int i = 0; i < EviEntita.COUNT_ENTITA_NEW; i++) {
            // Nova Entita
            Entita newEntita = new Entita();
            newEntita.setId(UUID.randomUUID());
            newEntita.setIdparent(parent.getId());
            newEntita.setIdtypentity(this.getTypentity());
            newEntita.setPopis("...");
            newEntita.setNewEntity(true);
            this.entities.add(newEntita);
        }

        this.setTypentityChilds(typentityController.getTypentityChilds(this.typentity));
    }

    /**
     * Metoda pro opakovane nacteni matice Entita slouzi k aktualizaci
     */
    public void reLoadEntities() {
        loadEntities(this.parentEntita, this.typentity);
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

    /**
     * Metoda vola dynamic Dialog pro vyber sloupcu ke zobrazeni v tabulce
     * Evientita
     *
     * @return prazdny retezec
     */
    public String viewColumns() {
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("draggable", false);
        options.put("resizable", false);
        options.put("contentHeight", 320);

        this.dialog = PrimeFaces.current().dialog();
        this.dialog.openDynamic("/aplikace/evidence/evientitacolumn", options, null);
        return "";
    }

    public void saveUserColumns() {
        this.columns = new ArrayList(this.columnsDualList.getTarget());
        StringBuilder sb = new StringBuilder("");
        columns.forEach((String column) -> {
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(column);
        });
        if (sb.length() > 0) {
            this.ucet.getUzivatel().setParam(this.typentity.getId().toString(), sb.toString());
        }
        //        this.dialog.closeDynamic(this.columnsDualList.getTarget());
    }

    public void onRowSelect(SelectEvent event) {
//        System.out.println("EviEntita.onRowSelect  event.getObject()=" + event.getObject());
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("EviEntita.onRowSelect - dosud neimplementováno", ((Entita)event.getObject()).getPopis()));
        eviAttribute.loadAttributes(((Entita) event.getObject()), this);
    }

    public void onRowUnselect(UnselectEvent event) {
//        System.out.println("EviEntita.onRowUnselect  event.getObject()=" + event.getObject());
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("EviEntita.onRowUnselect - dosud neimplementováno", ((Entita)event.getObject()).getPopis()));
        eviAttribute.loadAttributes(null, this);

    }

    public void changeTypentity(Typentity typentity) {
        loadEntities(selectedEntita, typentity);
    }

    public void changeTypentityBack() {
        Entita parentEntitaofParent = entitaController.getEntita(this.parentEntita.getIdparent());
        if (parentEntitaofParent instanceof Entita) {
            parentEntitaofParent = new Entita();
            parentEntitaofParent.setId(null);
            parentEntitaofParent.setIdtypentity(this.typentity);
        }
        this.selectedEntita = this.parentEntita;
        loadEntities(parentEntitaofParent, parentEntitaofParent.getIdtypentity());
    }

    public void gotoNewEntita() {
        for (Entita entita : entities) {
            if (entita.isNewEntity()) {
                this.selectedEntita=entita;
                eviAttribute.loadAttributes(this.selectedEntita, this);
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
    public EviAttribute getEviAttribute() {
        return eviAttribute;
    }

    /**
     * @param eviAttribute the eviAttribute to set
     */
    public void setEviAttribute(EviAttribute eviAttribute) {
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
