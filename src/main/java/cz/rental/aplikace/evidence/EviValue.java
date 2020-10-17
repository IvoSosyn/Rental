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
import java.util.ArrayList;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ivo
 */
public class EviValue {

    cz.rental.entity.AttributeController attrController;

    private Entita entita = null;
    private Attribute attribute = null;
    private Date platiOd = Aplikace.getPlatiOd();
    private Date platiDo = Aplikace.getPlatiDo();
    private ArrayList<EntitySuperClassNajem> attrValues = new ArrayList<>();
    private Object value = null;
    private Date valuePlatiOd = null;
    private Date valuePlatiDo = null;

    public EviValue() {
    }

    /**
     * Konstruktor s povinnzmi parametry
     *
     * @param attrController controller pro DB operace
     * @param entita pro kterou hledame hodnoty attribute nesmi byt null
     * @param attribute pro kterou hledame hodnoty nesmi byt null
     * @param platiOd omezeni platnosti cele instance OD data, muze byt null
     * @param platiDo omezeni platnosti cele instance DO data, muze byt null
     */
    public EviValue(cz.rental.entity.AttributeController attrController, Entita entita, Attribute attribute, Date platiOd, Date platiDo) {
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
