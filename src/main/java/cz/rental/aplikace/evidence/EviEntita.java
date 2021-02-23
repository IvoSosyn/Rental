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
import java.util.Stack;
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
    private static final String XHTML_EVIATTR_FILE = "/aplikace/evidence/";

    @EJB
    cz.rental.entity.TypentityController typentityController;
    @EJB
    cz.rental.entity.EntitaController entitaController;
    @EJB
    cz.rental.entity.AttributeController attrController;

    @Inject
    private Ucet ucet;

    @Inject
    private EviForm eviForm;
    @Inject
    private EviTable eviTable;

    private Stack<Entita> stackEntities = new Stack();
    private Stack<StackEvi> stackEvi = new Stack();
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
        // Dohledat vrcholovy model(sablonu) Typentity( s jeho definovanymi Attribute) pro pole Entita uctu
        this.typentity = typentityController.getTypentity(ucet.getAccount().getId());
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

        // Zahajovaci nastaveni:
        // Nacist matici Entita pro nevyssi entitu, ktera je umele vytvorena z identifikace uctu a jako parent entitu ma hodnotu 'null' - nema predka   
        // Typentita je prevzata z vybraneho modelu uctu
        this.stackEntities.push(this.parentEntita);
        this.getStackEvi().push(new StackEvi(parentEntita, null, typentity, 'T', parentEntita, null, typentity, typentity.getEditor()));
        loadEntities();
    }

    /**
     * Metoda 1. nacte vsechny instance "Entita" pro Entita.idparent==parent.id,
     * pole Attribute pro parametr "typentity" 2. prida nove instance "Entita"
     * do pole "entities" - vytvori matici tlacitek k dalsimu pouziti v
     * 'eviButtons.xml'
     *
     *
     */
    public void loadEntities() {
        this.parentEntita = getStackEvi().lastElement().getParentEntita();
        this.typentity = getStackEvi().lastElement().getParentTypEntity();
        this.entities = entitaController.getEntities(this.parentEntita, this.typentity);
        // Pridani radku nove zaznamu Entita
        for (int i = 0; i < EviEntita.COUNT_ENTITA_NEW; i++) {
            // Nova Entita
            Entita newEntita = new Entita();
            newEntita.setId(UUID.randomUUID());
            newEntita.setIdparent(this.parentEntita.getId());
            newEntita.setIdtypentity(this.getTypentity());
            newEntita.setPopis("Nový záznam");
            newEntita.setNewEntity(true);
            this.entities.add(newEntita);
        }
        // Naselectuji 0-tou Entita, pokud neni jiz preddefinovana
        if (this.selectedEntita == null) {
            this.selectedEntita = this.entities.get(0);
        }
        getStackEvi().lastElement().setParentSelectedEntita(this.selectedEntita);
        // Naplnit pole Attribute pro zadany typ entity
        this.attributes = attrController.getAttributeForTypentity(this.typentity);
        // Definice sloupcu tabulky Entity
        this.columnsForEntitaTable();
        // Child Typentity pro tlacitkovou listu
        this.setTypentityChilds(typentityController.getTypentityChilds(this.typentity));
        // Nacist stredovy panel, bud formular nebo tabulku
        if (getStackEvi().lastElement().getChildEditMode() == 'T') {
            this.eviTable.loadTable(getStackEvi().lastElement().getChildEntita(), getStackEvi().lastElement().getChildTypEntity());
            if (this.eviTable.getEntities() instanceof ArrayList && !this.eviTable.getEntities().isEmpty()) {
                getStackEvi().lastElement().setChildSelectedEntita(this.eviTable.getEntities().get(0));
            }
        } else {
            getStackEvi().lastElement().setChildEntita(this.selectedEntita);
            getStackEvi().lastElement().setChildSelectedEntita(this.selectedEntita);
            // Nacist hodnoty Attribute 
            this.eviForm.loadForm(this.selectedEntita, this);
        }

    }

    /**
     * Metoda pro opakovane nacteni matice Entita slouzi k aktualizaci
     */
    public void reLoadEntities() {
        loadEntities();
    }

    public void onRowSelect(SelectEvent event) {
//        System.out.println("EviEntita.onRowSelect  event.getObject()=" + event.getObject());
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("EviEntita.onRowSelect - dosud neimplementováno", ((Entita)event.getObject()).getPopis()));
        Entita selectedEnti = (Entita) event.getObject();

        getStackEvi().lastElement().setParentSelectedEntita(selectedEnti);
        getStackEvi().lastElement().setChildEntita(selectedEnti);
        // Nacist stredovy panel, bud formular nebo tabulku
        if (getStackEvi().lastElement().getChildEditMode() == 'T') {
            this.eviTable.loadTable(getStackEvi().lastElement().getChildEntita(), getStackEvi().lastElement().getChildTypEntity());
            if (this.eviTable.getEntities() instanceof ArrayList && !this.eviTable.getEntities().isEmpty()) {
                getStackEvi().lastElement().setChildSelectedEntita(this.eviTable.getEntities().get(0));
            }
        } else {
            getStackEvi().lastElement().setChildSelectedEntita(selectedEnti);
            this.eviForm.loadForm(selectedEnti, this);
        }

    }

    public void onRowUnselect(UnselectEvent event) {
//        System.out.println("EviEntita.onRowUnselect  event.getObject()=" + event.getObject());
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("EviEntita.onRowUnselect - dosud neimplementováno", ((Entita)event.getObject()).getPopis()));
        //       eviForm.loadForm(null, this);
    }

    /**
     * Metoda naplni naplni tento beans novymi hodnotami pro novy Typentity
     *
     * @param typentity - pozadovany Typentity, pro ktery se naplni tento beans
     * (matice a promenne)
     */
    public void changeTypentity(Typentity typentity) {
        this.getStackEvi().push(new StackEvi(this.getStackEvi().lastElement().getChildEntita(), this.getStackEvi().lastElement().getChildSelectedEntita(), this.getStackEvi().lastElement().getChildTypEntity(), this.getStackEvi().lastElement().getChildEditMode(), this.getStackEvi().lastElement().getChildSelectedEntita(), null, typentity, typentity.getEditor()));
        loadEntities();
    }

    /**
     * Metoda provede krok zpet a naplni tento beans hodnotami vyssi urovne
     * Typentity ze zasobniku Entit
     */
    public void changeTypentityBack() {
        if (!this.stackEvi.empty() && this.getStackEvi().size() > 1) {
            // Odeberu aktuální záznam
            StackEvi stackEviLast = this.getStackEvi().pop();
        }
        loadEntities();
    }

    public void gotoNewEntita() {
        for (Entita entita : entities) {
            if (entita.isNewEntity()) {
                this.getStackEvi().lastElement().setParentSelectedEntita(entita);
                if (this.getStackEvi().lastElement().getChildEditMode() == 'F') {
                    this.getStackEvi().lastElement().setChildSelectedEntita(entita);
                }
                loadEntities();
                break;
            }
        }
    }

    public String includeEviPanel() {
        String includePath;
        if (this.getTypentity() != null && this.getTypentity().getEditor() == 'T') {
            includePath = XHTML_EVIATTR_FILE + "eviTable.xhtml";
        } else {
            includePath = XHTML_EVIATTR_FILE + "eviForm.xhtml";
        }
        return includePath;
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
     * Metoda naplni matici pouzitelnych sloupcu pro tabulku "evientita.xml" z
     * poli Typentity a jeste z jeho prvku Attribute pro pozdejsi vyuziti v
     * dialogu vyberu zobrazovanych slopcu
     */
    private void columnsForEntitaTable() {
        // Definice sloupcu tabulky Entity
        this.columns = new ArrayList<>();
        this.columnsSource = new ArrayList<>();
        // Nacteni predchozihoi uzivatelskeho nastaveni
        String userColumns = this.ucet.getUzivatel().getParam(this.typentity.getId().toString(), "");
        if (!userColumns.isEmpty()) {
            this.columns.addAll(Arrays.asList(userColumns.split(";")));
        }
        boolean emptyColumns = this.columns.isEmpty();
        // Doplnim pole pouzitelnych sloupcu o promenne z Entita
        for (String entitaColumns : new String[]{"Entita.Popis", "Entita.Platiod", "Entita.Platido"}) {
            if (emptyColumns) {
                this.columns.add(entitaColumns);
            }
            if (!columns.contains(entitaColumns)) {
                this.columnsSource.add(entitaColumns);
            }
        }
        // Doplnim pole pouzitelnych sloupcu o Attribute
        for (Attribute attr : this.attributes) {
            String column = "Attribute." + attr.getAttrname().trim();
            if (!columns.contains(column)) {
                this.columnsSource.add(column);
            }
        }
        // Vytvoreni nabidky sloupcu k vyberu
        this.columnsDualList = new DualListModel<>(this.columnsSource, this.columns);
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
        this.dialog.openDynamic(XHTML_EVIATTR_FILE + "evientitacolumn", options, null);
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
     * @return the eviForm
     */
    public EviForm getEviForm() {
        return this.eviForm;
    }

    /**
     * @param eviForm the eviForm to set
     */
    public void setEviForm(EviForm eviForm) {
        this.eviForm = eviForm;
    }

    /**
     * @return the eviTable
     */
    public EviTable getEviTable() {
        return this.eviTable;
    }

    /**
     * @param eviTable the eviTable to set
     */
    public void setEviTable(EviTable eviTable) {
        this.eviTable = eviTable;
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

    /**
     * @return the stackEntities
     */
    public Stack<Entita> getStackEntities() {
        return this.stackEntities;
    }

    /**
     * @param stackEntities the stackEntities to set
     */
    public void setStackEntities(Stack<Entita> stackEntities) {
        this.stackEntities = stackEntities;
    }

    /**
     * =========================================================================
     * Interni trida pro ukladani urovni vnoreni do stromu Entita
     *
     */
    class StackEvi {

        private Entita parentEntita = null;
        private Entita parentSelectedEntita = null;
        private Typentity parentTypEntity = null;
        private Character parentEditMode = 'T';
        private Entita childEntita = null;
        private Entita childSelectedEntita = null;
        private Typentity childTypEntity = null;
        private Character childEditMode = 'F';

        public StackEvi(Entita parentEntita, Entita parentSelectedEntita, Typentity parentTypEntity, Character parentEditMode, Entita childEntita, Entita childSelectedEntita, Typentity childTypEntity, Character childEditMode) {
            this.parentEntita = parentEntita;
            this.parentSelectedEntita = parentSelectedEntita;
            this.parentTypEntity = parentTypEntity;
            this.parentEditMode = parentEditMode;
            this.childEntita = childEntita;
            this.childSelectedEntita = childSelectedEntita;
            this.childTypEntity = childTypEntity;
            this.childEditMode = childEditMode;
        }

        /**
         * @return the parentEntita
         */
        public Entita getParentEntita() {
            return parentEntita;
        }

        /**
         * @param parentEntita the parentEntita to set
         */
        public void setParentEntita(Entita parentEntita) {
            this.parentEntita = parentEntita;
        }

        /**
         * @return the parentTypEntity
         */
        public Typentity getParentTypEntity() {
            return parentTypEntity;
        }

        /**
         * @param parentTypEntity the parentTypEntity to set
         */
        public void setParentTypEntity(Typentity parentTypEntity) {
            this.parentTypEntity = parentTypEntity;
        }

        /**
         * @return the parentEditMode
         */
        public Character getParentEditMode() {
            return parentEditMode;
        }

        /**
         * @param parentEditMode the parentEditMode to set
         */
        public void setParentEditMode(Character parentEditMode) {
            this.parentEditMode = parentEditMode;
        }

        /**
         * @return the childEntita
         */
        public Entita getChildEntita() {
            return childEntita;
        }

        /**
         * @param childEntita the childEntita to set
         */
        public void setChildEntita(Entita childEntita) {
            this.childEntita = childEntita;
        }

        /**
         * @return the childTypEntity
         */
        public Typentity getChildTypEntity() {
            return childTypEntity;
        }

        /**
         * @param childTypEntity the childTypEntity to set
         */
        public void setChildTypEntity(Typentity childTypEntity) {
            this.childTypEntity = childTypEntity;
        }

        /**
         * @return the childEditMode
         */
        public Character getChildEditMode() {
            return childEditMode;
        }

        /**
         * @param childEditMode the childEditMode to set
         */
        public void setChildEditMode(Character childEditMode) {
            this.childEditMode = childEditMode;
        }

        /**
         * @return the parentSelectedEntita
         */
        public Entita getParentSelectedEntita() {
            return parentSelectedEntita;
        }

        /**
         * @param parentSelectedEntita the parentSelectedEntita to set
         */
        public void setParentSelectedEntita(Entita parentSelectedEntita) {
            this.parentSelectedEntita = parentSelectedEntita;
        }

        /**
         * @return the childSelectedEntita
         */
        public Entita getChildSelectedEntita() {
            return childSelectedEntita;
        }

        /**
         * @param childSelectedEntita the childSelectedEntita to set
         */
        public void setChildSelectedEntita(Entita childSelectedEntita) {
            this.childSelectedEntita = childSelectedEntita;
        }

    }

    /**
     * @return the stackEvi
     */
    public Stack<StackEvi> getStackEvi() {
        return stackEvi;
    }

    /**
     * @param stackEvi the stackEvi to set
     */
    public void setStackEvi(Stack<StackEvi> stackEvi) {
        this.stackEvi = stackEvi;
    }
}
