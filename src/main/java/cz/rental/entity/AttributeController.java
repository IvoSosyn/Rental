/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author sosyn
 */
@Stateless
public class AttributeController {

    @PersistenceContext(unitName = "PostgreSQLNajem")
    private EntityManager em;
    private Query query = null;
    private Typentity typentity = null;

    public ArrayList<Attribute> getAttributeForTypentity(Typentity typentity) {
        this.typentity = typentity;
        if (this.typentity==null) {
            return new ArrayList<Attribute>();
        }
        this.query = getEm().createQuery("SELECT a FROM Attribute a WHERE a.idtypentity= :idTypEntity AND a.identita IS NULL ORDER BY a.poradi");
        this.query.setParameter("idTypEntity", this.typentity.getId());
//        this.query.setParameter("PlatiOD", platiOd);
//        this.query.setParameter("PlatiDO", platiDo);
        List<Attribute> list = query.getResultList();
        return new ArrayList<>(list);
    }

    /**
     * @return the em
     */
    public EntityManager getEm() {
        return em;
    }

    /**
     * @param em the em to set
     */
    public void setEm(EntityManager em) {
        this.em = em;
    }
}
