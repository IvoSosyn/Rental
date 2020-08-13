/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import entity.exceptions.NonexistentEntityException;
import entity.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ivo
 */
public class JpaController implements Serializable {

    @PersistenceContext(unitName = "PostgreSQLNajem")
    private EntityManager em;

    private Query query = null;

    /**
     * Metoda vytvori novy zaznam do DB
     *
     * @param entita dedicove EntitySuperClassNajem (skoro vsechny tabulky)
     * @throws PreexistingEntityException vyjimka, pokud entita jiz existuje
     * @throws Exception obecna vyjimka z transakce
     */
    public void create(EntitySuperClassNajem entita) throws PreexistingEntityException, Exception {
        try {
            getEm().persist(entita);
            getEm().flush();
        } catch (Exception ex) {
            if (findEntita(entita) != null) {
                throw new PreexistingEntityException("Entita " + entita + " již existuje v databázi.", ex);
            }
            throw ex;
        } finally {
        }
    }

    /**
     * Metoda zapise zmeny do DB, parametr entita musi jiz v DB existovat
     *
     * @param entita dedicove EntitySuperClassNajem (skoro vsechny tabulky)
     * @throws PreexistingEntityException vyjimka, pokud entita jeste NEexistuje v DB
     * @throws Exception obecna vyjimka z transakce
     */
    public void edit(EntitySuperClassNajem entita) throws NonexistentEntityException, Exception {
        try {
            entita = getEm().merge(entita);
            getEm().flush();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                if (findEntita(entita) == null) {
                    throw new NonexistentEntityException("Entita " + entita + " NEexistuje v databázi.");
                }
            }
            throw ex;
        } finally {
        }
    }

    /**
     * Metoda smaze zaznam z DB
     *
     * @param entita dedicove EntitySuperClassNajem (skoro vsechny tabulky)
     * @throws NonexistentEntityException vyjimka, pokud entita ke smazani
     * neexistuje
     */
    public void destroy(EntitySuperClassNajem entita) throws NonexistentEntityException, Exception {
        try {
            if (findEntita(entita) == null) {
                throw new NonexistentEntityException("Entita " + entita + " NEexistuje v databázi.");
            } else {
                try {
                    getEm().remove(getEm().merge(entita));
                    getEm().flush();
                } catch (Exception ex) {
                    Logger.getLogger(JpaController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally {
        }
    }

    /**
     * Metoda se pokusi najit zaznam v DB
     *
     * @param entita dedicove EntitySuperClassNajem (skoro vsechny tabulky)
     * @return instance EntitySuperClassNajem nebo null pokud entita neexistuje
     */
    public EntitySuperClassNajem findEntita(EntitySuperClassNajem entita) {
        try {
            return (EntitySuperClassNajem) getEm().find((Class) entita.getClass(), entita.getId());
        } finally {
        }
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

    /**
     * @return the query
     */
    public Query getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(Query query) {
        this.query = query;
    }
}
