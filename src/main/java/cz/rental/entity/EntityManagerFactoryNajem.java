/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author ivo
 */
public class EntityManagerFactoryNajem implements Serializable {

    private static EntityManagerFactory entityManagerFactory = null;
    private static EntityManager entityManager = null;
    private static boolean valid = false;

    static {
        setEntityManagerFactory(Persistence.createEntityManagerFactory("PostgreSQLNajem"));
        setEntityManager(getEntityManagerFactory().createEntityManager());
        setValid(true);
    }

    /**
     * @return the entityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            setEntityManagerFactory(Persistence.createEntityManagerFactory("PostgreSQLNajem"));
        }
        return entityManagerFactory;
    }

    /**
     * @param aEmf the entityManagerFactory to set
     */
    public static void setEntityManagerFactory(EntityManagerFactory aEmf) {
        if (aEmf instanceof EntityManagerFactory) {
            entityManagerFactory = aEmf;
        } else {
            entityManagerFactory = Persistence.createEntityManagerFactory("PostgreSQLNajem");
        }
        setEntityManager(null);
    }

    /**
     * @return the entityManager
     */
    public static EntityManager getEntityManager() {
        if (entityManager == null || !entityManager.isOpen()) {

            entityManager = getEntityManagerFactory().createEntityManager();
        }
        return entityManager;
    }

    /**
     * @param aEm the entityManager to set
     */
    public static void setEntityManager(EntityManager aEm) {
        if (aEm instanceof EntityManager) {
            entityManager = aEm;
        } else {
            entityManager = getEntityManagerFactory().createEntityManager();
        }
    }

    /**
     * @return the valid
     */
    public static boolean isValid() {
        return valid;
    }

    /**
     * @param aValid the valid to set
     */
    public static void setValid(boolean aValid) {
        valid = aValid;
    }

    public static void closeAll() {
        if (entityManager != null) {
            entityManager.close();
            entityManager = null;
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
        closeAll();
    }
}
