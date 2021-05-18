/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.utils.Aplikace;
import cz.rental.aplikace.Uzivatel;
import cz.rental.aplikace.Ucet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import static javax.ejb.TransactionManagementType.CONTAINER;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

/**
 *
 * @author sosyn
 */
@Stateless
@TransactionManagement(CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AttrController extends JpaController {

    @Inject
    Ucet ucet;

    Query query = null;

    @PostConstruct
    public void init() {
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
                    this.edit(entSuperClass);
                    attribute = (Attribute) this.findEntita(attribute);
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
                    this.edit(entSuperClass);
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
            this.create(entSuperClass);
            attribute = (Attribute) this.findEntita(attribute);
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

    /**
     * Metoda naklonuje DB zaznamy Attribute z puvodni typEntity do nove
     * typEntityNew
     *
     * @param typEntity
     * @param typEntityNew
     */
    public void cloneAttribute(Typentity typEntity, Typentity typEntityNew) {
        Attribute attrNew;
        this.query = getEm().createNamedQuery("Attribute.findByIdTypEntity", Attribute.class);
        this.query.setParameter("idTypEntity", typEntity.getId());
        List<Attribute> attrs = this.query.getResultList();
        for (Attribute attr : attrs) {
            try {
                attrNew = (Attribute) attr.clone();
                attrNew.setId(UUID.randomUUID());
                attrNew.setIdtypentity(typEntityNew.getId());
                attrNew.setAttrsystem(false);
                this.create(attrNew);

            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(AttrController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(AttrController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Metoda vymaze z databaze vsechny Attribute, ktere maji
     * <CODE>Attribute.idtypentity=:idTypEntity</CODE>
     *
     * @param idTypEntity - ID Typentity, jehoz Attribute se maji smazat
     */
    public void deleteAllTypentityId(UUID idTypEntity) {
        this.query = getEm().createQuery("DELETE FROM Attribute a WHERE a.idtypentity=:idTypEntity" + (ucet.getUzivatel().getParam(Uzivatel.USER_PARAM_NAME.SUPERVISOR, false) ? "" : " AND NOT a.attrsystem"));
        this.query.setParameter("idTypEntity", idTypEntity);
        int deleted = this.query.executeUpdate();
        //System.out.println(" Delete from Attribute "+deleted);
    }

    /**
     * Metoda vyhleda Attribute podle zadanych parametru
     *
     * @param attrName - jmeno atribut Attribute.attrname
     * @param typentity - Typentity vlastnici Attribute.idtypentity
     * @param entita - Entita vlastnici Attribute.identita
     * @return hledany Attribute nebo null pokud neexistuje
     */
    public Attribute getAttribute(String attrName, Typentity typentity, Entita entita) throws Exception {
        HashMap<String, Object> params = new HashMap<>(3);
        StringBuilder sb = new StringBuilder("SELECT a FROM Attribute a WHERE (a.platiod IS NULL OR a.platiod <= :PlatiDO) AND (a.platido IS NULL OR a.platido >= :PlatiOD)");
        if (attrName instanceof String) {
            sb.append(" AND a.attrname= :AttrName");
            params.put("AttrName", attrName);
        }
        if (typentity instanceof Typentity) {
            sb.append(" AND a.idtypentity= :IDtypEntity");
            params.put("IDtypEntity", typentity.getId());
        } else {
            sb.append(" AND a.idtypentity IS NULL");
        }
        if (entita instanceof Entita) {
            sb.append(" AND a.identita= :IDEntita");
            params.put("IDEntita", entita.getId());
        } else {
            sb.append(" AND a.identita IS NULL");
        }
        sb.append(" ORDER BY a.platiod ASC NULLS FIRST, a.platido ASC NULLS LAST, a.timemodify ASC NULLS FIRST");
        params.put("PlatiOD", Aplikace.getPlatiOd());
        params.put("PlatiDO", Aplikace.getPlatiDo());

        this.query = getEm().createQuery(sb.toString());
        params.keySet().forEach(paramKey -> {
            this.query.setParameter(paramKey, params.get(paramKey));
        });

        Attribute attribute = null;
        try {
            attribute = (Attribute) this.query.getSingleResult();
        } catch (NonUniqueResultException nEx) {
            throw nEx;
        } catch (NoResultException nEx) {
            System.out.println(" AttrContrloler.getAttribute(attrName,typentity,entita) :" + nEx.getLocalizedMessage());
        }

        // getEm().close();
        return attribute;
    }

    /**
     * Metoda vyhleda Attribute podle zadaneho parametru (ID)
     *
     * @param attrId - ID hledaneho parametru
     * @return hledany Attribute nebo null
     */
    public Attribute getAttribute(UUID attrId) {
        if (attrId == null) {
            return null;
        }
        Attribute attribute = null;
        this.query = getEm().createQuery("SELECT a FROM Attribute a WHERE a.id= :AttrId");
        this.query.setParameter("AttrId", attrId);

        try {
            attribute = (Attribute) this.query.getSingleResult();
        } catch (Exception ex) {
            Logger.getLogger(AttrController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // getEm().close();
        return attribute;
    }
}
