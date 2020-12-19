/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.entity.Attrdate;
import cz.rental.entity.Attribute;
import cz.rental.entity.Attrnumeric;
import cz.rental.entity.Attrtext;
import cz.rental.entity.Entita;
import cz.rental.entity.EntitySuperClassNajem;
import cz.rental.utils.Aplikace;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author ivo
 */
public class EviAttrValue {

    cz.rental.entity.AttributeController attrController;

    private Entita entita = null;
    private Attribute attribute = null;
    private Date platiOd = Aplikace.getPlatiOd();
    private Date platiDo = Aplikace.getPlatiDo();
    private ArrayList<EntitySuperClassNajem> attrValues = new ArrayList<>();
    private Object value = null;
    private Date valuePlatiOd = null;
    private Date valuePlatiDo = null;

    /**
     * Konstruktor s povinnymi parametry
     *
     * @param attrController controller pro DB operace
     * @param entita pro kterou hledame hodnoty attribute nesmi byt null
     * @param attribute pro kterou hledame hodnoty nesmi byt null
     * @param platiOd omezeni platnosti cele instance OD data, muze byt null
     * @param platiDo omezeni platnosti cele instance DO data, muze byt null
     */
    public EviAttrValue(cz.rental.entity.AttributeController attrController, Entita entita, Attribute attribute, Date platiOd, Date platiDo) {
        this.attrController = attrController;
        this.entita = entita;
        this.attribute = attribute;
        this.platiOd = platiOd;
        this.platiDo = platiDo;
        this.readAllValues();
    }

    /**
     * *
     * Metoda načte všechny hodnoty pro Entita a Attributes a do promenne
     * "value" vlozi platnou, vyhovujici casovemu omezeni cele instance platiOd,
     * platiDo
     */
    private void readAllValues() {
        this.attrValues = attrController.getAllAttrValues(entita, attribute);
        for (EntitySuperClassNajem attrValue : attrValues) {
            if ((this.platiOd == null || attrValue.getPlatido() == null || attrValue.getPlatido().after(this.platiOd))
                    && (this.platiDo == null || attrValue.getPlatiod() == null || attrValue.getPlatiod().before(this.platiDo))) {
                this.valuePlatiOd = attrValue.getPlatiod();
                this.valuePlatiDo = attrValue.getPlatido();
                switch (attribute.getAttrtype()) {
                    case 'T': {
                    }
                    case 'C': {
                        value = ((Attrtext) attrValue).getText();
                        break;
                    }
                    case 'L': {
                        value = (((Attrnumeric) attrValue).getCislo())!=0;
                        break;
                    }
                    case 'N': {
                    }
                    case 'I': {
                        value = ((Attrnumeric) attrValue).getCislo();
                        break;
                    }
                    case 'D': {
                        value = ((Attrdate) attrValue).getDatumcas();
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }
    }

    void save(Date zmenaOd) throws Exception {
        EntitySuperClassNajem attrValueNew = null;
        Date attrValueplatiOd = zmenaOd;
        Date attrValueplatiDo = null;
        // TO-DO: Ukončit předchozí platnosti a vložit novou hodnotu
        for (EntitySuperClassNajem attrValue : attrValues) {
            if (attrValue.getPlatiod() == null && attrValue.getPlatido().before(zmenaOd)) {

            }
            if (attrValue.getPlatiod() == null && attrValue.getPlatiod().after(zmenaOd)) {

            }
            if (attrValue.getPlatiod() != null && attrValue.getPlatiod().after(zmenaOd)) {

            }
            if (attrValue.getPlatido() != null && attrValue.getPlatido().before(zmenaOd)) {

            }
        }
        switch (this.attribute.getAttrtype()) {
            case 'T': {
            }
            case 'C': {
                attrValueNew = new Attrtext();
                ((Attrtext) attrValueNew).setIdentita(this.entita.getId());
                ((Attrtext) attrValueNew).setIdattribute(this.attribute);
                ((Attrtext) attrValueNew).setText((String) this.value);
                break;
            }
            case 'L': {
                attrValueNew = new Attrnumeric();
                ((Attrnumeric) attrValueNew).setIdentita(this.entita.getId());
                ((Attrnumeric) attrValueNew).setIdattribute(this.attribute);
                ((Attrnumeric) attrValueNew).setCislo((Boolean) this.value ? 1.0 : 0.0);
                break;
            }
            case 'N': {
            }
            case 'I': {
                attrValueNew = new Attrnumeric();
                ((Attrnumeric) attrValueNew).setIdentita(this.entita.getId());
                ((Attrnumeric) attrValueNew).setIdattribute(this.attribute);
                ((Attrnumeric) attrValueNew).setCislo((this.value instanceof String)?Double.parseDouble((String) this.value):(double) this.value);
                break;
            }
            case 'D': {
                attrValueNew = new Attrdate();
                ((Attrdate) attrValueNew).setIdentita(this.entita.getId());
                ((Attrdate) attrValueNew).setIdattribute(this.attribute);
                ((Attrdate) attrValueNew).setDatumcas((Date) this.value);
                break;
            }
            default: {
                break;
            }
        }
        if (attrValueNew != null) {
            attrValueNew.setPopis(this.attribute.getPopis());
            attrValueNew.setPlatiod(attrValueplatiOd);
            attrValueNew.setPlatido(attrValueplatiDo);
            attrValueNew.setNewEntity(true);
            if (this.attrController.findEntita(attrValueNew) == null) {
                this.attrController.create(attrValueNew);
                attrValueNew.setNewEntity(false);
            } else {
                this.attrController.edit(attrValueNew);
            }

        }
    }

    /**
     * *
     * Metoda vraci velikost hodnoty pole AttrXXXXX
     *
     * @return pocet zobrazenych znaku
     */
    public Integer getAttrSize() {
        int size = 10;
        System.out.println("getAttrSize() Attribute: " + attribute.getPopis() + "/" + attribute.getAttrname() + "/" + attribute.getAttrtype() + "/" + attribute.getAttrsize() + "/" + attribute.getAttrdecimal() + "/" + value);
        if (this.attribute.getAttrsize() instanceof BigInteger) {
            size = this.attribute.getAttrsize().intValue();
        } else {
            switch (attribute.getAttrtype()) {
                case 'T': {
                    size = 30;
                    break;
                }
                case 'C': {
                    size = 150;
                    break;
                }
                case 'L': {
                    size = 1;
                    break;
                }
                case 'N': {
                }
                case 'I': {
                    size = 10;
                    break;
                }
                case 'D': {
                    size = 10;
                    break;
                }
                default: {
                    break;
                }
            }
        }
        System.out.println("   getAttrSize() size: " + size);
        return size;
    }

    /**
     * *
     * Metoda vraci pocet desetinnych mist pole AttrXXXXX
     *
     * @return pocet desetinnych mist
     */
    public Integer getAttrDecimal() {
        int decimal = 2;
        if (this.attribute.getAttrdecimal() instanceof BigInteger) {
            decimal = this.attribute.getAttrdecimal().intValue();
        } else {
            switch (attribute.getAttrtype()) {
                case 'T': {
                    decimal = 0;
                    break;
                }
                case 'C': {
                    decimal = 0;
                    break;
                }
                case 'L': {
                    decimal = 0;
                    break;
                }
                case 'N': {
                    decimal = 2;
                    break;
                }
                case 'I': {
                    decimal = 0;
                    break;
                }
                case 'D': {
                    decimal = 0;
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return decimal;
    }

    public boolean isRenderedAttr(String attrType) {
        return this.attribute.getAttrtype().equals(attrType.charAt(0));
    }

    public String valueAsDate() {
        return ((this.value instanceof Date) ? Aplikace.getSimpleDateFormat().format(this.value) : "");
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
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the platiOd
     */
    public Date getPlatiOd() {
        return platiOd;
    }

    /**
     * @param platiOd the platiOd to set
     */
    public void setPlatiOd(Date platiOd) {
        this.platiOd = platiOd;
    }

    /**
     * @return the platiDo
     */
    public Date getPlatiDo() {
        return platiDo;
    }

    /**
     * @param platiDo the platiDo to set
     */
    public void setPlatiDo(Date platiDo) {
        this.platiDo = platiDo;
    }

    /**
     * @return the valuePlatiOd
     */
    public Date getValuePlatiOd() {
        return valuePlatiOd;
    }

    /**
     * @param valuePlatiOd the valuePlatiOd to set
     */
    public void setValuePlatiOd(Date valuePlatiOd) {
        this.valuePlatiOd = valuePlatiOd;
    }

    /**
     * @return the valuePlatiDo
     */
    public Date getValuePlatiDo() {
        return valuePlatiDo;
    }

    /**
     * @param valuePlatiDo the valuePlatiDo to set
     */
    public void setValuePlatiDo(Date valuePlatiDo) {
        this.valuePlatiDo = valuePlatiDo;
    }
}
