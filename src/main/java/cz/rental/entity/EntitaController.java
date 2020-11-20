/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.utils.Aplikace;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import static javax.ejb.TransactionManagementType.CONTAINER;
import javax.persistence.Query;

/**
 *
 * @author sosyn
 */
@Stateless
@TransactionManagement(CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class EntitaController extends JpaController {

    private Query query = null;

    /**
     * Metoda vyhleda v DB vsechny zaznamy Entita pro rodicovske parent.id
     *
     * @param parent nadrizena Entita pro kterou hledam vsechny vetve
     * podrizenych entit
     * @return pole vsech Entita, ktere maji Entita.idparent==parent.id
     */
    public ArrayList<Entita> getEntities(Entita parent) {
        if (parent == null) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder("SELECT e FROM Entita e WHERE e.idparent");
        if (parent.getId() != null) {
            sb.append("=:idParent");
        } else {
            sb.append(" IS NULL");
        }
        sb.append(" AND (e.platiod IS NULL OR e.platiod <= :PlatiDO) AND (e.platido IS NULL OR e.platido >= :PlatiOD)");
        this.query = this.getEm().createQuery(sb.toString());
        if (parent.getId() != null) {
            this.query.setParameter("idParent", parent.getId());
        }
        this.query.setParameter("PlatiOD", Aplikace.getPlatiOd());
        this.query.setParameter("PlatiDO", Aplikace.getPlatiDo());
        List<Entita> list = this.query.getResultList();
        return new ArrayList<>(list);
    }

    /**
     * Metoda najde zaznam Entita v DB nebo vraci 'null'
     *
     * @param id identifikator zaznamu
     * @return zaznam Entita nebo null
     */
    public Entita getEntita(UUID id) {
        Entita entita = null;
        StringBuilder sb = new StringBuilder("SELECT e FROM Entita e WHERE ");
        if (id != null) {
            sb.append("e.id=:id");
        } else {
            sb.append("e.id IS NULL");
        }
        this.query = this.getEm().createQuery(sb.toString());
        if (id != null) {
            this.getQuery().setParameter("id", id);
        }
        try {
            entita = (Entita) this.getQuery().getSingleResult();
        } catch (Exception e) {
            System.out.println(" EntitaContoller.getEntita#e: " + e.getMessage());
        }
        return entita;
    }
}
