/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import entity.exceptions.NonexistentEntityException;
import entity.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.EntityManager;

/**
 *
 * @author ivo
 */
public class JpaController implements Serializable {

    public EntityManager getEntityManager() {
        return ManagerFactory.getManager();
    }

    /**
     * Metoda vytvori novy zaznam do DB
     *
     * @param entita dedicove EntitySuperClassNajem (skoro vsechny tabulky)
     * @throws PreexistingEntityException vyjimka, pokud entita jiz existuje
     * @throws Exception obecna vyjimka z transakce
     */
    public void create(EntitySuperClassNajem entita) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entita);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEntita(entita) != null) {
                throw new PreexistingEntityException("Entita " + entita + " již existuje v databázi.", ex);
            }
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Metoda zapise zmeny do DB
     *
     * @param entita dedicove EntitySuperClassNajem (skoro vsechny tabulky)
     * @throws PreexistingEntityException vyjimka, pokud entita jiz existuje
     * @throws Exception obecna vyjimka z transakce
     */
    public void edit(EntitySuperClassNajem entita) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            entita = em.merge(entita);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                if (findEntita(entita) == null) {
                    throw new NonexistentEntityException("Entita " + entita + " NEexistuje v databázi.");
                }
            }
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Metoda smaze zaznam z DB
     *
     * @param entita dedicove EntitySuperClassNajem (skoro vsechny tabulky)
     * @throws NonexistentEntityException vyjimka, pokud entita ke smazani neexistuje
     */
    public void destroy(EntitySuperClassNajem entita) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            if (findEntita(entita) == null) {
                throw new NonexistentEntityException("Entita " + entita + " NEexistuje v databázi.");
            } else {
                em = getEntityManager();
                em.getTransaction().begin();
                em.remove(em.merge(entita));
                em.getTransaction().commit();
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Metoda se pokusi najit zaznam v DB
     *
     * @param entita dedicove EntitySuperClassNajem (skoro vsechny tabulky)
     * @return instance EntitySuperClassNajem nebo null pokud entita neexistuje
     */
    public EntitySuperClassNajem findEntita(EntitySuperClassNajem entita) {
        EntityManager em = getEntityManager();
        try {
            return (EntitySuperClassNajem) em.find((Class) entita.getClass(), entita.getId());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}