/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.aplikace.User;
import cz.rental.utils.Aplikace;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import static javax.ejb.TransactionManagementType.CONTAINER;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sosyn
 */
@Stateless
@TransactionManagement(CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AttributeController extends JpaController {

    @PersistenceContext(unitName = "PostgreSQLNajem")
    private EntityManager em;
    private Query query = null;
    private Typentity typentity = null;

    @Inject
    User user;

    public ArrayList<Attribute> getAttributeForTypentity(Typentity typentity) {
        this.typentity = typentity;
        if (this.typentity == null) {
            return new ArrayList<>();
        }
        this.query = getEm().createQuery("SELECT a FROM Attribute a WHERE a.idtypentity= :idTypEntity AND a.identita IS NULL ORDER BY a.poradi");
        this.query.setParameter("idTypEntity", this.typentity.getId());
//        this.query.setParameter("PlatiOD", platiOd);
//        this.query.setParameter("PlatiDO", platiDo);
        List<Attribute> list = query.getResultList();
        return new ArrayList<>(list);
    }

    public ArrayList<Attribute> getAttributeWhere(UUID id, String alias, String whereCondition) {
        if (whereCondition == null) {
            return null;
        }
        StringBuffer sbQuery = new StringBuffer("SELECT ").append(alias)
                .append(" FROM Attribute ").append(alias)
                .append(" WHERE ").append(whereCondition).append(" AND ").append(alias).append(".idtypentity= :idTypEntity AND ").append(alias).append(".identita IS NULL")
                .append(" ORDER BY ").append(alias).append(".poradi");

        // String strQuery = "SELECT " + alias + " FROM Attribute " + alias + " WHERE " + whereCondition + " AND " + alias + ".idtypentity= :idTypEntity AND " + alias + ".identita IS NULL ORDER BY " + alias + ".poradi";
        this.query = getEm().createQuery(sbQuery.toString());
        this.query.setParameter("idTypEntity", id);
        List<Attribute> list = query.getResultList();
        return new ArrayList<>(list);
    }

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
            if (!selectedAttr.isNewEntity() && (user.getParam(User.SUPERVISOR, false) ? true : selectedAttr.getAttrsystem() != null && !selectedAttr.getAttrsystem())) {
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
}
