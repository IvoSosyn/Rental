/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.aplikace.Aplikace;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author sosyn
 */
public class AttrController {

    JpaController jpaController = new JpaController();
    private EntityManager em = jpaController.getEntityManager();
    Query query = null;

    public AttrController() {
    }

    /**
     * @return the em
     */
    public EntityManager getEm() {
        if (!em.isOpen()) {
            this.setEm(jpaController.getEntityManager());
        }
        return em;
    }

    /**
     * @param em the em to set
     */
    public void setEm(EntityManager em) {
        this.em = em;
    }

    /**
     * Metoda vrati ArrayList s hodnotami Attr{text|date|numeric} pro zadany
     * parametr <attribute>
     *
     * @param attribute - Attribute ke kteremu hledam hodnotu
     * @return
     *
     */
    public ArrayList<EntitySuperClassNajem> getAttrValueList(Attribute attribute) {
        if (attribute == null) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder("SELECT a FROM ");
        switch (attribute.getAttrtype()) {
            case 'C': {
                sb.append("Attrtext");
                break;
            }
            case 'L': {
            }
            case 'N': {
            }
            case 'I': {
                sb.append("Attrnumeric");
                break;
            }
            case 'D': {
                sb.append("Attrdate");
                break;
            }
            default: {
                break;
            }
        }
        sb.append(" a WHERE a.idattribute=:attribute ORDER BY a.platiod ASC NULLS FIRST, a.platido ASC NULLS LAST, a.timemodify ASC NULLS FIRST");
        query = getEm().createQuery(sb.toString());
        query.setParameter("attribute", attribute);
        List list = query.getResultList();
        getEm().close();
        return new ArrayList(list);

    }

    /**
     * Metoda ulozi hodnotu <value> do prislusne polozky Attr{text|date|numeric}
     * do DB
     *
     * @param attribute - Attribute ke kteremu ukladam hodnotu
     * @param value
     */
    public void saveAttr(Attribute attribute, Object value) {
        ArrayList<EntitySuperClassNajem> aAttrValue = getAttrValueList(attribute);
        EntitySuperClassNajem entSuperClass = null;
        for (EntitySuperClassNajem escn : aAttrValue) {
            // Aktualizovat posledni polozku
            if (Aplikace.isUpdateLast()) {
                entSuperClass = escn;
            } else {
                // Zaznam je nutne rozpulit
                if ((escn.getPlatiod() == null || escn.getPlatiod().before(Aplikace.getZmenaOd()))
                        && (escn.getPlatido() == null || escn.getPlatido().after(Aplikace.getZmenaOd()))) {
                    entSuperClass = escn;
                }
            }
        }
        // TODO:            
        // Vyresit platnost zaznamu - kde vzit datum ukonceni
        // Osetrit situaci, kdy platiDo < platiOd
        // ?????
        // Od kdy plati vkladana hodnota z parametru 'value'
        Date datumZmenyDo = null;
        // Ukoncit predchozi hodnotu
        if (entSuperClass != null) {
            // Nutno posoudit, zda se zmenila hodnota a je nutno neco delat
            switch (attribute.getAttrtype()) {
                case 'C': {
                    if (value != null && ((Attrtext) entSuperClass).getPopis() != null && ((String) value).trim().equals(((Attrtext) entSuperClass).getPopis().trim())) {
                        return;
                    }
                    break;
                }
                case 'L': {
                }
                case 'N': {
                }
                case 'I': {
                    if (value != null && ((Double) value).equals((((Attrnumeric) entSuperClass).getCislo()))) {
                        return;
                    }
                    break;
                }
                case 'D': {
                    if (value != null && ((Date) value).equals((((Attrdate) entSuperClass).getDatumcas()))) {
                        return;
                    }
                    break;
                }
                default: {
                    break;
                }
            }
            // Pokud je pouze UpGrade posledni hodnoty tak ji ulozim a konec
            if (Aplikace.isUpdateLast()) {
                switch (attribute.getAttrtype()) {
                    case 'C': {
                        ((Attrtext) entSuperClass).setPopis(value == null ? null : (String) value);
                        break;
                    }
                    case 'L': {
                    }
                    case 'N': {
                    }
                    case 'I': {
                        ((Attrnumeric) entSuperClass).setCislo(value == null ? null : (Double) value);
                        break;
                    }
                    case 'D': {
                        ((Attrdate) entSuperClass).setDatumcas(value == null ? null : (Date) value);
                        break;
                    }
                    default: {
                        break;
                    }
                }
                try {
                    jpaController.edit(entSuperClass);
                    attribute = (Attribute) jpaController.findEntita(attribute);
                } catch (Exception ex) {
                    // Osetrit, co kdyz se to neulozi
                    Logger.getLogger(AttrController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                return;
            } else {
                // Zapamatovat si do kdy predchozi hodnota platila 
                datumZmenyDo = entSuperClass.getPlatido();
                // Puvodni zaznam ukoncim k prechozimu dni            
                Calendar cal = Aplikace.getCalendar();
                cal.setTime(datumZmenyDo instanceof Date ? datumZmenyDo : Aplikace.getZmenaOd());
                cal.add(Calendar.DAY_OF_MONTH, -1);
                entSuperClass.setPlatido(cal.getTime());
                try {
                    // Pokus o ulozeni ukonceneho zaznamu do databaze
                    jpaController.edit(entSuperClass);
                } catch (Exception ex) {
                    Logger.getLogger(AttrController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
        // Zapis nove hodnoty
        // null hodnoty se nezapisuji, pokud neexistuje zadny predchozi zaznam
        if (value == null && aAttrValue.isEmpty()) {
            return;
        }

        // TODO:
        // Osetrit vyjimky pri ukladani dat do databaze
        entSuperClass = null;
        switch (attribute.getAttrtype()) {
            case 'C': {
                entSuperClass = new Attrtext();
                ((Attrtext) entSuperClass).setIdattribute(attribute);
                ((Attrtext) entSuperClass).setIdentita(attribute.getIdentita());
                entSuperClass.setPopis(value == null ? null : (String) value);
                break;
            }
            case 'N': {
            }
            case 'I': {
            }
            case 'L': {
                entSuperClass = new Attrnumeric();
                ((Attrnumeric) entSuperClass).setIdattribute(attribute);
                ((Attrnumeric) entSuperClass).setIdentita(attribute.getIdentita());
                ((Attrnumeric) entSuperClass).setCislo(value == null ? null : (Double) value);
                ((Attrnumeric) entSuperClass).setPopis(attribute.getPopis());
                break;
            }
            case 'D': {
                entSuperClass = new Attrdate();
                ((Attrdate) entSuperClass).setIdattribute(attribute);
                ((Attrdate) entSuperClass).setIdentita(attribute.getIdentita());
                ((Attrdate) entSuperClass).setDatumcas(value == null ? null : (Date) value);
                ((Attrdate) entSuperClass).setPopis(attribute.getPopis());
                break;
            }
            default: {
                return;
            }
        }
        entSuperClass.setPlatiod(aAttrValue.isEmpty() ? null : Aplikace.getZmenaOd());
        entSuperClass.setPlatido(aAttrValue.isEmpty() ? null : datumZmenyDo);
        try {
            jpaController.create(entSuperClass);
            attribute = (Attribute) jpaController.findEntita(attribute);
        } catch (Exception ex) {
            // Osetrit, co kdyz se to neulozi
            Logger.getLogger(AttrController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Nacteni posledni hodnoty Attr{text|date|numeric} pro zadany parametr
     * attribute
     *
     * @param attribute
     * @return hodnota Attr{text|date|numeric}
     */
    public EntitySuperClassNajem getLastValue(Attribute attribute) {
        // Zjisteni posledni hodnoty
        EntitySuperClassNajem entSuperClass = null;
        ArrayList<EntitySuperClassNajem> aAttrValue = getAttrValueList(attribute);
        for (EntitySuperClassNajem escn : aAttrValue) {
            // Najit posledni zaznam, ktery plati v ramci Aplikace.getPlatiOd()...Aplikace.getPlatiDo()
            if ((escn.getPlatiod() == null || escn.getPlatiod().before(Aplikace.getPlatiDo()))
                    && (escn.getPlatido() == null || escn.getPlatido().after(Aplikace.getPlatiOd()))) {
                entSuperClass = escn;
            }
        }
        return entSuperClass;
    }
}