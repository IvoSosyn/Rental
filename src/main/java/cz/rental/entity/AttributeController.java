/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sosyn
 */
@Stateless
public class AttributeController  extends JpaController {

    @PersistenceContext(unitName = "PostgreSQLNajem")
    private EntityManager em;
    private Query query = null;
    private Typentity typentity = null;

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

}
