/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.aplikace.Ucet;
import cz.rental.entity.Attribute;
import cz.rental.entity.Entita;
import cz.rental.utils.Aplikace;
import cz.rental.utils.ScriptTools;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.script.ScriptException;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datepicker.DatePicker;
import org.primefaces.component.inputnumber.InputNumber;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.PrimeFacesContext;

/**
 *
 * @author ivo
 */
@Named(value = "eviForm")
@SessionScoped
public class EviForm implements Serializable {

    private static final long serialVersionUID = 42L;

    private static final int COUNT_ENTITA_NEW = 1;

    @EJB
    private cz.rental.entity.AttributeController attrController;
    @EJB
    private cz.rental.entity.EntitaController entitaController;

    @Inject
    private Ucet ucet;

    private EviEntita source = null;
    private Entita entita = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private ArrayList<EviAttrValue> values = new ArrayList<>();
    private HashMap<String, EviAttrValue> valuesMap = new HashMap<>();
    private ScriptTools scriptTools = null;

    @PostConstruct
    public void init() {
    }

    /**
     * Metoda nacte vsechny Attribute pro Entita a naplni pole "values"
     *
     * @param entita zaznam, pro ktery nacitam Attribute
     */
    public void loadAttrValue(Entita entita) {
        if (entita == null || entita.getIdtypentity() == null) {
            attributes = new ArrayList<>();
            values = new ArrayList<>();
            return;
        }
        /**
         * TO-DO: Zohlednit PlatiOd a PlatiDo sablony ve vyberu dat Attribute,
         * aby se vybraly pouze platne v danem obdobi
         */
        if (this.attributes.isEmpty() || this.entita == null || !this.entita.getIdtypentity().equals(entita.getIdtypentity())) {
            this.attributes = getAttrController().getAttributeForTypentity(entita.getIdtypentity());
        }
        this.entita = entita;
        this.values = new ArrayList<>(this.attributes.size());
        this.scriptTools = new ScriptTools(this);
        EviAttrValue eav;
        for (Attribute attr : this.getAttributes()) {
            // Vybrat uplne vsechno, bez ohledu na platnost, aby se dalo podivat do cele historie
            eav = new EviAttrValue(this.attrController, this.entita, attr, null, null);
            boolean add = this.values.add(eav);
            valuesMap.put(attr.getAttrname(), eav);
        }
    }

    /**
     * Metoda nacte vsechny Attribute pro Entita a naplni pole "values"
     *
     * @param entita zaznam, pro ktery nacitam Attribute
     * @param source zaznam, jehoz matice Entita se ma aktualizovat
     */
    public void loadForm(Entita entita, EviEntita source) {
        this.source = source;
        loadAttrValue(entita);
    }

    /**
     * Metoda ulozi vsechny hodnoty do DB
     *
     * @param event udalost, ktera vyvolala ulozeni - zatim se nevyuziva
     */
    public void saveAttributes(ActionEvent event) {

        Date zmenaOd = new Date();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String param = params.get("zmenaOD");
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Metoda saveAttrValues(ActionEvent event) neni zatim immplementovana", "ActionEvent event" + event.getSource()));
        //PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Metoda saveAttrValues(ActionEvent event) neni zatim immplementovana", "ActionEvent event" + event.getSource()));
        try {
            // Nejdriv ulozim zmeny do DB vety Entita
            if (entitaController.findEntita(this.entita) == null) {
                entitaController.create(this.entita);
                this.entita.setNewEntity(false);

            } else {
                entitaController.edit(this.entita);
            }
            zmenaOd = Aplikace.getSimpleDateFormat().parse(param);
            for (EviAttrValue eviAttrValue : this.values) {
                eviAttrValue.saveAllValues(zmenaOd);
            }
            if (this.source != null) {
                this.source.reLoadEntities();
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Data byla úspěšně uložena."));
        } catch (Exception ex) {
            Logger.getLogger(EviForm.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Hodnoty NEbyly uloženy.", "Chyba" + ex.getMessage()));
        }
    }

    /**
     * Metoda provede JavaScript, pokud je pro menene pole definovany v
     * sablone/modelu.
     *
     * @param e udalost nad menenym polem se starou a novou hodnotou
     */
    public void valueChangeListener(ValueChangeEvent e) {
        String label = null;
        String script = null;
        UIInput uii = (UIInput) e.getSource();
        if (e.getSource() instanceof InputText) {
            InputText inputText = (InputText) e.getSource();
            label = inputText.getLabel();
        }
        if (e.getSource() instanceof InputNumber) {
            InputNumber inputNumber = (InputNumber) e.getSource();
            label = inputNumber.getLabel();
        }
        if (e.getSource() instanceof SelectBooleanCheckbox) {
            SelectBooleanCheckbox selectBooleanCheckbox = (SelectBooleanCheckbox) e.getSource();
            label = selectBooleanCheckbox.getLabel();
        }
        if (e.getSource() instanceof DatePicker) {
            DatePicker datePicker = (DatePicker) e.getSource();
            label = datePicker.getLabel();
        }
        if (label == null) {
            return;
        }
        for (EviAttrValue value : values) {
            if (value.getAttribute().getAttrname().equalsIgnoreCase(label)) {
                script = value.getAttribute().getScript();
                break;
            }
        }
        if (script == null) {
            return;
        }
        Throwable scrErr = this.scriptTools.run(script);
        if (scrErr != null) {
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage(script, scrErr.getMessage()));
        }
    }

    /**
     * Metoda dosadi hodnotu 'value' do formularove promenne definovane hodnotou
     * 'klic'
     * Metoda provadi automatickou konverzi z retezce 'String' na cislo 'Double'
     * 
     * @param klic pole formulare, kam dosadit hodnotu 'value'
     * @param value hodonta k dosazeni
     * @throws java.text.ParseException - chyba v pripaded konverze retecove hodnoty 'value' na datum
     */
    public void setFromScrtipt(Object klic, Object value) throws ParseException {
        if (klic instanceof String) {
            String[] attrName = ((String) klic).split(".");
            for (EviAttrValue eviAttrValue : values) {
                if (eviAttrValue.getAttribute().getAttrname().equalsIgnoreCase(attrName[attrName.length - 1])) {
                    if ((eviAttrValue.getAttribute().getAttrtype() == 'I' || eviAttrValue.getAttribute().getAttrtype() == 'N') && value instanceof String) {
                        eviAttrValue.setValue(Double.valueOf((String) value));
                    } else if (eviAttrValue.getAttribute().getAttrtype() == 'D' && value instanceof String) {
                        eviAttrValue.setValue(Aplikace.getSimpleDateFormat().parse((String) value));
                    } else {
                        eviAttrValue.setValue(value);
                    }
                    break;
                }
            }
        }
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
     * @return the values
     */
    public ArrayList<EviAttrValue> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(ArrayList<EviAttrValue> values) {
        this.values = values;
    }

    /**
     * @return the entita
     */
    public Entita getEntita() {
        return entita;
    }

    /**
     * @param entita the entita to set
     */
    public void setEntita(Entita entita) {
        this.entita = entita;
    }

    /**
     * @return the attrController
     */
    public cz.rental.entity.AttributeController getAttrController() {
        return attrController;
    }

    /**
     * @param attrController the attrController to set
     */
    public void setAttrController(cz.rental.entity.AttributeController attrController) {
        this.attrController = attrController;
    }

    /**
     * @return the ucet
     */
    public Ucet getUcet() {
        return ucet;
    }

    /**
     * @param account the ucet to set
     */
    public void setUcet(Ucet account) {
        this.ucet = account;
    }

    /**
     * @return the valuesMap
     */
    public HashMap<String, EviAttrValue> getValuesMap() {
        return valuesMap;
    }

    /**
     * @param valuesMap the valuesMap to set
     */
    public void setValuesMap(HashMap<String, EviAttrValue> valuesMap) {
        this.valuesMap = valuesMap;
    }

}
