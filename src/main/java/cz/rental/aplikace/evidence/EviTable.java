/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.aplikace.Ucet;
import cz.rental.aplikace.evidence.eviTable.EviHeader;
import cz.rental.entity.Attribute;
import cz.rental.entity.Entita;
import cz.rental.entity.Typentity;
import cz.rental.utils.Aplikace;
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
import javax.faces.event.ActionEvent;
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
    private EviForm eviForm;

    private Entita parentEntita = null;
    private Typentity typentity = null;
    private ArrayList<Entita> entities = new ArrayList<>();
    private Entita selectedEntita = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<String> columns = new ArrayList<>();
    private ArrayList<EviHeader> eviHeaders = new ArrayList<>();
    private ArrayList<Typentity> typentityChilds = new ArrayList<>();
    private int loadRows = 0;

    private Dialog dialog;
    private ArrayList<String> columnsSource = new ArrayList<>();
    private DualListModel<String> columnsDualList;

    @PostConstruct
    public void init() {
    }

    /**
     * Metoda 
     * 1. nacte vsechny instance "Entita" pro Entita.idparent==parent.id a Entita.typentity=typentity
     * 2. nacte pole Attribute pro parametr "typentity" 
     * 3. prida nove instance "Entita" do pole "entities" 
     * 4. vytvori matici tlacitek k dalsimu pouziti v 'eviButtons.xml'
     * 5. naplni sloupce EviHeaders pro tabulku
     * 6. naplni matici hodnot pro kazdou "Entita" z matice "this.entities" do pole hodnot "EviHeaders.values" v matici sloupcu
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
        if (this.attributes.isEmpty() || this.typentity == null || !this.typentity.equals(typentity)) {
            this.attributes = attrController.getAttributeForTypentity(typentity);
            this.loadRows = 1;
            this.columnsForEntitaTable(typentity);
        }
        this.parentEntita = parent;
        this.typentity = typentity;
        this.entities = entitaController.getEntities(parent,typentity);
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
        // Nacist hodnoty sloupcu z this.eviHeaders
        //eviForm.loadAttrValue(this.selectedEntita);
        this.loadRows();

        // Child Typentity pro tlacitkovou listu
        this.setTypentityChilds(typentityController.getTypentityChilds(this.typentity));
    }

    /**
     * Metoda nacte hodnoty pro klic Entita do poli "values" sloupcu EviHeaders
     */
    private void loadRows() {
        this.loadRows = 0;
        Runnable loadHeadersValues = new Runnable() {
            Object value;
            Method method;
            HashMap<Entita, Object> tempHashMap;

            @Override
            public void run() {
                for (EviHeader header : eviHeaders) {
                    if (loadRows != 0) {
                        break;
                    }
                    // Nachystat novou prazdnou matici pro hodnoty
                    tempHashMap = new HashMap<>(entities.size());
                    for (Entita entita : entities) {
                        if (loadRows != 0) {
                            break;
                        }
                        value = null;
                        if (header.getAttribute() instanceof Attribute) {
                            value = attrController.getAttrValue(entita, header.getAttribute(), null, null);
                        } else if (header.getTable().equalsIgnoreCase("Entita")) {
                            try {
                                method = entita.getClass().getMethod("get" + header.getField(), new Class[]{});
                                value = method.invoke(entita, new Object[]{});
                            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                Logger.getLogger(EviEntita.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (header.getTable().equalsIgnoreCase("Typentity")) {
                            try {
                                method = entita.getIdtypentity().getClass().getMethod("get" + header.getField(), new Class[]{});
                                value = method.invoke(entita, new Object[]{});
                            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                Logger.getLogger(EviEntita.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        // Ulozit do zasobniku k pozdejsimu vlozeni do sloupce
                        tempHashMap.put(entita, value);
                    }
                    if (loadRows == 0) {
                        // Ulozit hodnoty do sloupce
                        header.setEntitaValues(tempHashMap);
                    }
                }
            }
        };
        loadHeadersValues.run();
    }

    /**
     * Metoda pro opakovane nacteni matice Entita slouzi k aktualizaci
     */
    public void reLoadEntities() {
        // Zastavit nacitani radku
        this.loadRows = 1;
        loadTable(this.parentEntita, this.typentity);
    }

    public void columnsSelect() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Dosud neimplementovano", "Dosud neimplementovano"));
    }

    /**
     * Metoda naplni matici pouzitelnych sloupcu pro tabulku "eviTable.xml" z
     * poli Typentity a jeste z jeho prvku Attribute pro pozdejsi vyuziti v
     * dialogu vyberu zobrazovanych slopcu
     */
    private void columnsForEntitaTable(Typentity typentity) {
        // Definice sloupcu tabulky Entity
        this.columns = new ArrayList<>();
        this.columnsSource = new ArrayList<>();
        this.eviHeaders = new ArrayList<>();
        // Nacteni predchoziho uzivatelskeho nastaveni
        String userColumns = this.ucet.getUzivatel().getParam(typentity.getId().toString(), "");
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
            } else {
                this.eviHeaders.add(new EviHeader(entitaColumns, entitaColumns, null));
            }
        }
        // Doplnim pole pouzitelnych sloupcu o Attribute
        for (Attribute attr : this.attributes) {
            String column = "Attribute." + attr.getAttrname().trim();
            if (!columns.contains(column)) {
                this.columnsSource.add(column);
            } else {
                this.eviHeaders.add(new EviHeader(column, attr.getPopis(), attr));
            }
        }
        // Vytvoreni nabidky sloupcu k vyberu
        this.columnsDualList = new DualListModel<>(this.columnsSource, this.columns);
    }

    /**
     * Metoda ulozi vybrane sloupce do uzivatelskeho nastaveni do DB - nacte
     * znovu sloupce matice <code>this.eviHeaders</code> a provede znovu nacteni
     * dat <code>this.loadRows()</code>
     */
    public void saveUserColumns() {
        this.loadRows = 1;
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
        this.eviHeaders = new ArrayList<>();
        for (String column : this.columns) {
            if (column.contains("Attribute.")) {
                for (Attribute attr : this.attributes) {
                    if (column.contains("Attribute." + attr.getAttrname().trim())) {
                        this.eviHeaders.add(new EviHeader(column, attr.getPopis(), null));
                        break;
                    }
                }
            } else {
                this.eviHeaders.add(new EviHeader(column, column, null));
            }
        }
        //        reLoadEntities();
        loadRows();
        //        this.dialog.closeDynamic(this.columnsDualList.getTarget());
    }

    /**
     * Metoda vybere z matice hodnot Entit odpovidajici hodnotu pro zadany
     * sloupec
     *
     * @param eviHeader - sloupec, pro ktery pozaduji hodnotu
     * @param entita - Entita pro kterou hledam hodnotu
     * @return
     */
    public Object getColumnValue(EviHeader eviHeader, Entita entita) {
        // Object value=row.get(header.getColumn());
        Object value = eviHeader.getValue(entita);
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
    public Object getColumnValue(Entita entita, String column) {
        String table = "Attribute";
        String field = "popis";
        Object value = "";
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
                        //value = obj.toString();
                        value = obj;
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
                //value = method.invoke(tpe, new Object[]{}).toString();
                value = method.invoke(tpe, new Object[]{});
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(EviEntita.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Pokud se jedna o tabulku "Entita" metoda vrati hodnotu pole z  instance Entita
        if (table.equalsIgnoreCase("Entita")) {
            try {
                Method method = tpe.getClass().getMethod("get" + field, new Class[]{});
                // value = (String) method.invoke(entita, new Object[]{});
                value = method.invoke(entita, new Object[]{});
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
                Logger.getLogger(EviEntita.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return value;
    }

    public void onRowSelect(SelectEvent event) {
//        System.out.println("EviEntita.onRowSelect  event.getObject()=" + event.getObject());
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("EviEntita.onRowSelect - dosud neimplementováno", ((Entita)event.getObject()).getPopis()));
        this.selectedEntita = (Entita) event.getObject();
        eviForm.loadAttrValue(this.selectedEntita);
    }

    public void onRowUnselect(UnselectEvent event) {
//        System.out.println("EviEntita.onRowUnselect  event.getObject()=" + event.getObject());
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("EviEntita.onRowUnselect - dosud neimplementováno", ((Entita)event.getObject()).getPopis()));
        this.selectedEntita = null;
        eviForm.loadAttrValue(this.selectedEntita);

    }

    public void gotoNewEntita() {
        for (Entita entita : entities) {
            if (entita.isNewEntity()) {
                this.selectedEntita = entita;
                eviForm.loadAttrValue(this.selectedEntita);
                break;
            }
        }
        if (this.selectedEntita instanceof Entita && this.selectedEntita.isNewEntity()) {
            editEntita(this.selectedEntita);
        }
    }

    public void editEntita(Entita entita) {
        if (entita == null) {
            return;
        }
        this.selectedEntita = entita;
        eviForm.loadAttrValue(entita);

        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("draggable", false);
        options.put("resizable", true);
        options.put("contentHeight", 800);

        this.dialog = PrimeFaces.current().dialog();
        this.dialog.openDynamic("/aplikace/evidence/eviTableEdit.xhtml", options, null);
    }

    /**
     * Metody vymaze vsechny hodnoty Attribute parametru "entita" a pak vymaze i
     * samotnou Entita danou parametem "entita"
     *
     * @param entita Entita ke smazani
     */
    public void removeEntita(Entita entita) {
        if (entita == null || entita.isNewEntity()) {
            return;
        }
        for (EviAttrValue attrValue : this.eviForm.getValues()) {
            try {
                // Vymazat uplne vsechno s ohledem na plati OD-DO
                attrValue.removeAllValues();
            } catch (Exception ex) {
                Logger.getLogger(EviTable.class
                        .getName()).log(Level.SEVERE, null, ex);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Hodnoty NEbyly vymazány.", "Chyba" + ex.getMessage()));
                return;
            }
        }
        try {
            entitaController.destroy(this.selectedEntita);
        } catch (Exception ex) {
            Logger.getLogger(EviTable.class
                    .getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Hodnoty NEbyly vymazány.", "Chyba" + ex.getMessage()));
            return;
        }
        reLoadEntities();
    }

    /**
     * Metoda ulozi vsechny hodnoty do DB
     *
     * @param event udalost, ktera vyvolala ulozeni
     */
    public void saveAttributes(ActionEvent event) throws Exception {
        Date zmenaOd = new Date();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String param = params.get("zmenaOD");
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Metoda saveAttrValues(ActionEvent event) neni zatim immplementovana", "ActionEvent event" + event.getSource()));
        //PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Metoda saveAttrValues(ActionEvent event) neni zatim immplementovana", "ActionEvent event" + event.getSource()));
        try {
            // Nejdriv ulozim zmeny do DB vety Entita
            if (entitaController.findEntita(this.selectedEntita) == null) {
                entitaController.create(this.selectedEntita);
                this.selectedEntita.setNewEntity(false);

            } else {
                entitaController.edit(this.selectedEntita);
            }
            zmenaOd = Aplikace.getSimpleDateFormat().parse(param);
            for (EviAttrValue eviAttrValue : this.eviForm.getValues()) {
                eviAttrValue.saveAllValues(zmenaOd);
            }
            reLoadEntities();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Data byla úspěšně uložena."));
        } catch (Exception ex) {
            Logger.getLogger(EviForm.class
                    .getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Hodnoty NEbyly uloženy.", "Chyba" + ex.getMessage()));
            throw ex;
        }
    }

    /**
     * Metoda ulozi vsechny hodnoty do DB a vytvori novy zaznam, ktery predlozi
     * k editaci
     *
     * @param event udalost, ktera vyvolala ulozeni
     */
    public void saveAttrAndAdd(ActionEvent event) {
        try {
            saveAttributes(event);
            for (Entita entita : entities) {
                if (entita.isNewEntity()) {
                    this.selectedEntita = entita;
                    eviForm.loadAttrValue(entita);
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(EviTable.class
                    .getName()).log(Level.SEVERE, null, ex);
            this.dialog.closeDynamic(null);
        }
    }

    /**
     * Metoda ulozi vsechny hodnoty do DB a ukonci DynamicDialog
     *
     * @param event udalost, ktera vyvolala ulozeni
     */
    public void saveAttrAndEnd(ActionEvent event) {
        try {
            saveAttributes(event);
        } catch (Exception ex) {
            Logger.getLogger(EviTable.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        this.dialog.closeDynamic(null);
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
        return eviForm;
    }

    /**
     * @param eviForm the eviForm to set
     */
    public void setEviForm(EviForm eviForm) {
        this.eviForm = eviForm;
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
     * @return the tempHashMap
     */
    public ArrayList<EviAttrValue> getValues() {
        return eviForm.getValues();
    }

    /**
     * @param values the tempHashMap to set
     */
    public void setValues(ArrayList<EviAttrValue> values) {
        this.eviForm.setValues(values);
    }

    /**
     * @return the eviHeaders
     */
    public ArrayList<EviHeader> getEviHeaders() {
        return eviHeaders;
    }

    /**
     * @param eviHeaders the eviHeaders to set
     */
    public void setEviHeaders(ArrayList<EviHeader> eviHeaders) {
        this.eviHeaders = eviHeaders;
    }
}
