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
public class ManagerFactory implements Serializable {

    private static EntityManagerFactory managerFactory = null;
    private static EntityManager manager = null;
    private static boolean valid = false;

    static {
        setManagerFactory(Persistence.createEntityManagerFactory("PostgreSQLNajem"));
        setEntityManager(getManagerFactory().createEntityManager());
        setValid(true);
    }

    /**
     * @return the managerFactory
     */
    public static EntityManagerFactory getManagerFactory() {
        if (managerFactory == null) {
            setManagerFactory(Persistence.createEntityManagerFactory("PostgreSQLNajem"));
        }
        return managerFactory;
    }

    /**
     * @param aEmf the managerFactory to set
     */
    public static void setManagerFactory(EntityManagerFactory aEmf) {
        if (aEmf instanceof EntityManagerFactory) {
            managerFactory = aEmf;
        } else {
            managerFactory = Persistence.createEntityManagerFactory("PostgreSQLNajem");
        }
        setEntityManager(null);
    }

    /**
     * @return the manager
     */
    public static EntityManager getManager() {
        if (manager == null || !manager.isOpen()) {

            manager = getManagerFactory().createEntityManager();
        }
        return manager;
    }

    /**
     * @param aEm the manager to set
     */
    public static void setEntityManager(EntityManager aEm) {
        if (aEm instanceof EntityManager) {
            manager = aEm;
        } else {
            manager = getManagerFactory().createEntityManager();
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
        if (manager != null) {
            manager.close();
            manager = null;
        }
        if (managerFactory != null) {
            managerFactory.close();
            managerFactory = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
        closeAll();
    }
}
