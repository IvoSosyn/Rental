/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.aplikace.Uzivatel;
import cz.rental.aplikace.Ucet;
import cz.rental.utils.Aplikace;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import static javax.ejb.TransactionManagementType.CONTAINER;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author sosyn
 */
@Stateless
@TransactionManagement(CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AttributeController extends JpaController {

    private Query query = null;
    private Typentity typentity = null;

    Ucet ucet;

    @PostConstruct
    public void init() {
    }

    /**
     * Metoda vrací pole(matici) s Attribute pro zadany parametr
     * <code>typentity</code>
     *
     * @param typentity zaznam sablony, pro ktery hledam pole Attribute
     * @return
     */
    public ArrayList<Attribute> getAttributeForTypentity(Typentity typentity) {
        this.typentity = typentity;
        if (this.typentity == null) {
            return new ArrayList<>();
        }
        this.query = getEm().createQuery("SELECT a FROM Attribute a WHERE a.idtypentity=:idTypEntity ORDER BY a.poradi");
        this.query.setParameter("idTypEntity", this.typentity.getId());
//        this.query.setParameter("PlatiOD", platiOd);
//        this.query.setParameter("PlatiDO", platiDo);
        List<Attribute> list = query.getResultList();
        return new ArrayList<>(list);
    }

    /**
     * Metoda vraci pole(matici) Attribute pro Typentity se zadanou podminkou
     * <code>whereCondition</code>
     *
     * @param idtypentity ID typu entity(sablony), pro ktery hledam pole
     * Attribute
     * @param alias alias pro entitu, vetsinou 'a'
     * @param whereCondition pridana podminka vyberu
     * @return matice Attribute pro dany Typentiy
     */
    public ArrayList<Attribute> getAttributeWhere(UUID idtypentity, String alias, String whereCondition) {
        if (whereCondition == null) {
            return null;
        }
        StringBuffer sbQuery = new StringBuffer("SELECT ").append(alias)
                .append(" FROM Attribute ").append(alias)
                .append(" WHERE ").append(whereCondition).append(" AND ").append(alias).append(".idtypentity= :idTypEntity AND ").append(alias).append(".identita IS NULL")
                .append(" ORDER BY ").append(alias).append(".poradi");

        // String strQuery = "SELECT " + alias + " FROM Attribute " + alias + " WHERE " + whereCondition + " AND " + alias + ".idtypentity= :idTypEntity AND " + alias + ".identita IS NULL ORDER BY " + alias + ".poradi";
        this.query = getEm().createQuery(sbQuery.toString());
        this.query.setParameter("idTypEntity", idtypentity);
        List<Attribute> list = query.getResultList();
        return new ArrayList<>(list);
    }

    /**
     * Metoda nakopiruje pole Attribute <code>copyAttrs</code> do DB s novym
     * <code>id</code> "idtypentity" BEZ duplicit (false)
     *
     * @param copyAttrs pole Attribute k ulozeni do DB POZOR, musi se
     * vygenerovat nove Attiribute.id
     * @param id nove "Attribute.idtypentity"
     *
     * @throws Exception
     */
    public void pasteAttrs(ArrayList<Attribute> copyAttrs, UUID id) throws Exception {
        pasteAttrs(copyAttrs, id, false);
    }

    /**
     * Metoda nakopiruje pole Attribute <code>copyAttrs</code> do DB s novym
     * <code>id</code> "idtypentity"
     *
     * @param copyAttrs pole Attribute k ulozeni do DB POZOR, musi se
     * vygenerovat nove Attiribute.id
     * @param id nove "Attribute.idtypentity"
     * @param duplicities jsou povoleny duplicity Ano|Ne
     * @throws Exception
     */
    public void pasteAttrs(ArrayList<Attribute> copyAttrs, UUID id, boolean duplicities) throws Exception {
        for (Attribute copyAttr : copyAttrs) {
            // Kontrola na duplicitu idtypentity,nazev
            if (!duplicities) {
                this.query = getEm().createQuery("SELECT a FROM Attribute a WHERE a.attrname= :attrname AND a.idtypentity= :idTypEntity AND a.identita IS NULL");
                this.query.setParameter("idTypEntity", id);
                this.query.setParameter("attrname", copyAttr.getAttrname());
                List<Attribute> list = query.getResultList();
                if (list != null && !list.isEmpty()) {
                    continue;
                }
            }
            copyAttr.setId(UUID.randomUUID());
            copyAttr.setIdtypentity(id);
            copyAttr.setIdentita(null);
            copyAttr.setAttrsystem(false);
            // Novou polozku Attribute ulozit do DB
            this.create(copyAttr);
        }
    }

    public void deleteAttrs(ArrayList<Attribute> selectedAttrs) throws Exception {
        for (Attribute selectedAttr : selectedAttrs) {
            if (!selectedAttr.isNewEntity() && (ucet.getUzivatel().getParam(Uzivatel.USER_PARAM_NAME.SUPERVISOR, false) ? true : selectedAttr.getAttrsystem() != null && !selectedAttr.getAttrsystem())) {
                this.destroy(selectedAttr);
            }
        }
    }

    public Object getAttrValue(Entita entita, Attribute attr, Date platiOd, Date platiDo) {
        Object obj = null;
        StringBuilder sb = new StringBuilder("SELECT v FROM ");
        switch (attr.getAttrtype()) {
            case 'T': {
            }
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
        sb.append(" v WHERE v.identita=:idEntita AND v.idattribute=:Attribute AND (v.platiod IS NULL OR v.platiod<=:platiDo) AND (v.platido IS NULL OR v.platido>=:platiOd) ");
        sb.append(" ORDER BY v.platiod ASC NULLS FIRST, v.platido ASC NULLS LAST, v.timemodify ASC NULLS FIRST");
        Query queryValue = getEm().createQuery(sb.toString());
        queryValue.setParameter("idEntita", entita.getId());
        queryValue.setParameter("Attribute", attr);
        queryValue.setParameter("platiOd", platiOd == null ? Aplikace.getPlatiOd() : platiOd);
        queryValue.setParameter("platiDo", platiDo == null ? Aplikace.getPlatiDo() : platiDo);
        ArrayList<EntitySuperClassNajem> listValue = new ArrayList<>(queryValue.getResultList());
        EntitySuperClassNajem esc = null;
        if (!listValue.isEmpty()) {
            switch (attr.getAttrtype()) {
                case 'T': {
                }
                case 'C': {
                    obj = ((Attrtext) listValue.get(listValue.size() - 1)).getText();
                    break;
                }
                case 'L': {
                }
                case 'N': {
                }
                case 'I': {
                    obj = ((Attrnumeric) listValue.get(listValue.size() - 1)).getCislo();
                    break;
                }
                case 'D': {
                    sb.append("Attrdate");
                    obj = ((Attrdate) listValue.get(listValue.size() - 1)).getDatumcas();
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return obj;
    }

    /**
     * Metoda nacte z DB vsechny hodnoty pro Entita a jeji Attribute
     *
     * @param entita
     * @param attribute
     * @return
     */
    public ArrayList<EntitySuperClassNajem> getAllAttrValues(Entita entita, Attribute attribute) {
        StringBuilder sb = new StringBuilder("SELECT v FROM ");
        switch (attribute.getAttrtype()) {
            case 'T': {
            }
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
        sb.append(" v WHERE ");
        sb.append(entita != null ? " v.identita=:idEntita " : " v.identita IS NULL ");
        sb.append(" AND ");
        sb.append(attribute != null ? " v.idattribute=:Attribute " : " v.idattribute IS NULL ");
        sb.append(" ORDER BY v.platiod ASC NULLS FIRST, v.platido ASC NULLS LAST, v.timemodify ASC NULLS FIRST");
        Query queryValue = getEm().createQuery(sb.toString());
        if (entita != null) {
            queryValue.setParameter("idEntita", entita.getId());
        }
        if (attribute != null) {
            queryValue.setParameter("Attribute", attribute);
        }
        return new ArrayList<>(queryValue.getResultList());
    }

    /**
     * Metoda vytvori|aktualizuje údaje Attribute modelu(sablony) 'target' podle
     * vzoru 'source'
     *
     * @param source
     * @param target
     * @throws CloneNotSupportedException
     * @throws Exception
     */
    public void copyAttr(Typentity source, Typentity target) throws CloneNotSupportedException, Exception {
        if (source == null) {
            throw new Exception("Vzorový model(šablona) pro vytvoření atributů modelu(šablony) účtu je prázdná.");
        }
        if (target == null) {
            throw new Exception("Model(šablona) účtu je prázdná, nemohu vytvořit kopii atributů uzlu.");
        }
        Attribute attrNew;
        // Nacte pole pro vsechny Attribute source modelu
        this.query = getEm().createQuery("SELECT a FROM Attribute a WHERE a.idtypentity= :idTypEntitySource AND a.identita IS NULL");
        this.query.setParameter("idTypEntitySource", source.getId());
        List<Attribute> attributes = query.getResultList();
        for (Attribute attr : attributes) {
            // Najit pro konkretni Attribute ze source odpovidajici kopii Attribute v target 
            this.query = getEm().createQuery("SELECT a FROM Attribute a WHERE a.idtypentity= :idTypEntityTarget AND a.idmodel= :idAttributeSource AND a.identita IS NULL");
            this.query.setParameter("idTypEntityTarget", target.getId());
            this.query.setParameter("idAttributeSource", attr.getId());
            try {
                attrNew = (Attribute) this.query.getSingleResult();
            } catch (NoResultException en) {
                attrNew = (Attribute) attr.clone();
                attrNew.setId(UUID.randomUUID());
                attrNew.setIdtypentity(target.getId());
                attrNew.setIdmodel(attr.getId());
            }
            if (this.findEntita(attrNew) instanceof Attribute) {
                this.edit(attrNew);
            } else {
                this.create(attrNew);
            }
        }

    }
}
