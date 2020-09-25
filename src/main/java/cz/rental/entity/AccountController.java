/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.aplikace.User;
import cz.rental.aplikace.registrace.Account;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author sosyn
 */
@Stateless
public class AccountController extends JpaController {

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

    public void saveAccount(Account account) throws Exception {
        Entita entita = null;
        if (account == null) {
            throw new Exception("Chybí (objekt) zadání účtu.");
        }
        if (account.getCustomerID() == null) {
            throw new Exception("Chybí identifikátor (ID) účtu (jva.util.UUID).");
        }
        // Najit nebo zalozit instanci Entita pro ucet
        this.query = getEm().createQuery("SELECT e FROM Entita e WHERE e.id= :idEntita");
        this.query.setParameter("idEntita", account.getCustomerID());
        ArrayList<Entita> listEntity = new ArrayList<>(query.getResultList());
        if (listEntity.isEmpty()) {
            this.query = getEm().createQuery("SELECT t FROM Typentity t WHERE t.typentity='ACCOUNT'");
            ArrayList<Typentity> listTypentity = new ArrayList<>(query.getResultList());
            // Zalozit novou instanci Entita
            entita = new Entita();
            entita.setId(account.getCustomerID());
            entita.setNewEntity(true);
            if (listTypentity.isEmpty()) {
                throw new Exception("Chybí model(šalona) pro Typentity.typentit='ACCOUNT'.");
            } else {
                entita.setIdtypentity(listTypentity.get(0));
            }
        } else {
            entita = listEntity.get(0);
        }
        entita.setPopis("Account for " + account.getCustomerName());
        // Ulozit nebo vytvorit Entita pro konkretni Accout
        if (entita.isNewEntity()) {
            this.create(entita);
            entita = (Entita) this.findEntita(entita);
            entita.setNewEntity(false);
        } else {
            this.edit(entita);
        }
        // Ulozit hodnoty polozek uctu Account
        saveAcountAttr(entita, account.getCustomerName(), "accountName");
        saveAcountAttr(entita, account.getCustomerAddress(), "acountAddress");
        saveAcountAttr(entita, account.getCustomerEmail(), "acountEmail");
        saveAcountAttr(entita, account.getCustomerTel(), "accountTel");
        saveAcountAttr(entita, account.getCustomerPasswordSHA512(), "accountPassword");
        if (account.getCustomerModel() instanceof Typentity) {
            saveAcountAttr(entita, account.getCustomerModel().getId().toString(), "accountModel");
        }
        saveAcountAttr(entita, (double) account.getCustomerPin(), "accountPIN");

    }

    /**
     * Ulozit hodnoty do DB
     */
    private void saveAcountAttr(Entita entita, Object value, String attrname) throws Exception {
        Attribute attribute = null;
        // Najit nebo zalozit instanci Attribute pro Typentity uctu
        Query queryAttr = getEm().createQuery("SELECT a FROM Attribute a WHERE a.attrname= :attrname AND a.idtypentity= :idTypEntity AND a.identita IS NULL");
        queryAttr.setParameter("attrname", attrname);
        queryAttr.setParameter("idTypEntity", entita.getIdtypentity().getId());
        ArrayList<Attribute> listAttr = new ArrayList<>(queryAttr.getResultList());
        if (listAttr.isEmpty()) {
            attribute = new Attribute();
            attribute.setId(UUID.randomUUID());
            attribute.setPoradi(5);
            attribute.setIdtypentity(entita.getIdtypentity().getId());
            attribute.setAttrname(attrname);
            attribute.setAttrtype('N');
            if (value instanceof String) {
                attribute.setAttrtype('C');
            }
            if (value instanceof Date) {
                attribute.setAttrtype('D');
            }
            attribute.setAttrsize(BigInteger.valueOf(36));
            attribute.setAttrdecimal(BigInteger.valueOf(0));
            attribute.setAttrsystem(true);
            attribute.setPopis(attrname);
            this.create(attribute);
            attribute = (Attribute) this.findEntita(attribute);
        } else {
            attribute = listAttr.get(0);
        }
        // Ulozit hodnoty uctu  
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
        sb.append(" v WHERE v.idattribute=:attribute ORDER BY v.platiod ASC NULLS FIRST, v.platido ASC NULLS LAST, v.timemodify ASC NULLS FIRST");
        Query queryValue = getEm().createQuery(sb.toString());
        queryValue.setParameter("attribute", attribute);
        // queryValue.setParameter("idEntita", entita.getId());
        ArrayList<EntitySuperClassNajem> listValue = new ArrayList<>(queryValue.getResultList());
        EntitySuperClassNajem esc = null;
        if (!listValue.isEmpty()) {
            esc = listValue.get(0);
            switch (attribute.getAttrtype()) {
                case 'T': {
                }
                case 'C': {
                    ((Attrtext) esc).setText((String) value);
                    break;
                }
                case 'L': {
                }
                case 'N': {
                }
                case 'I': {
                    ((Attrnumeric) esc).setCislo((double) value);
                    break;
                }
                case 'D': {
                    ((Attrdate) esc).setDatumcas((Date) value);
                    break;
                }
                default: {
                    break;
                }
            }

        } else {
            switch (attribute.getAttrtype()) {
                case 'T': {
                }
                case 'C': {
                    esc = new Attrtext();
                    esc.setId(UUID.randomUUID());
                    esc.setPopis(attrname);
                    esc.setNewEntity(true);
                    ((Attrtext) esc).setIdattribute(attribute);
                    ((Attrtext) esc).setIdentita(entita.getId());
                    ((Attrtext) esc).setText((String) value);
                    ((Attrtext) esc).setEdit(0);
                    break;
                }
                case 'L': {
                }
                case 'N': {
                }
                case 'I': {
                    esc = new Attrnumeric();
                    esc.setId(UUID.randomUUID());
                    esc.setPopis(attrname);
                    esc.setNewEntity(true);
                    ((Attrnumeric) esc).setIdattribute(attribute);
                    ((Attrnumeric) esc).setIdentita(entita.getId());
                    ((Attrnumeric) esc).setCislo((double) value);
                    ((Attrnumeric) esc).setEdit(0);
                    break;
                }
                case 'D': {
                    esc = new Attrdate();
                    esc.setId(UUID.randomUUID());
                    esc.setPopis(attrname);
                    esc.setNewEntity(true);
                    ((Attrdate) esc).setIdattribute(attribute);
                    ((Attrdate) esc).setIdentita(entita.getId());
                    ((Attrdate) esc).setDatumcas((Date) value);
                    ((Attrdate) esc).setEdit(0);
                    break;
                }
                default: {
                    break;
                }
            }
        }
        // Ulozit hodnotu
        if (esc.isNewEntity()) {
            this.create(esc);
            esc.setNewEntity(false);

        } else {
            this.edit(esc);
        }
    }
}
