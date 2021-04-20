/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import static javax.ejb.TransactionManagementType.CONTAINER;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 *
 * @author sosyn
 */
@Stateless
@TransactionManagement(CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class UserController extends JpaController {

    private Query query = null;

    @PostConstruct
    public void init() {
    }

    /**
     * Metoda vraci 'Uzivatel' DB zaznam pro pozadovany jedinecny 'Account' a
     * 'Email'
     *
     * @param account Nadrazeny "Account", ke kteremu je uzivatel prirazen
     * @param email Email uzivatele
     * @param passwordSHA512 SHA-512 hesla
     * @return Uzivatelsky zaznam nebo 'null', kdyz se uzivatel neexistuje
     */
    public User getUserForAccount(Account account, String email, String passwordSHA512) {
        User accUser = null;
        StringBuilder sb = new StringBuilder("SELECT u FROM User u WHERE u.idaccount=:account AND u.email=:email ");
        if (passwordSHA512 instanceof String) {
            sb.append(" AND u.passwordsha512=:passwordSHA512");
        }
        this.query = getEm().createQuery(sb.toString());
        this.query.setParameter("account", account);
        this.query.setParameter("email", email);
        if (passwordSHA512 instanceof String) {
            this.query.setParameter("passwordSHA512", passwordSHA512);
        }
        try {
            accUser = (User) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException | IllegalStateException | QueryTimeoutException e) {
            System.out.println(" UserController.getUserForAccount() vraci chybu" + e.getMessage());
        }
        return accUser;
    }

    /**
     * Metoda vytvori matici uzivatelu (<code>User</code>) pro zadany Ucet
     * (<code>Account</code>)
     *
     * @param account Ucet(<code>Account</code>), pro ktery hledam vsechny
     * uzivatele
     * @return Kolekce s uzivateli k uctu
     */
    public ArrayList<User> getUsersForAccount(Account account) {
        this.query = getEm().createQuery("SELECT u FROM User u WHERE u.idaccount=:account");
        this.query.setParameter("account", account);
        return new ArrayList<>(this.query.getResultList());
    }

    /**
     * Metoda ulozi polozku User do DB
     *
     * @param user User k ulozeni do DB
     * @throws Exception vyjimka v pripade, ze se nepodarilo ulozit User do DB
     */
    public void saveUser(User user) throws Exception {
        if (getEm().find(User.class, user.getId()) == null) {
            getEm().persist(user);
            user.setNewEntity(false);
        } else {
            getEm().merge(user);
        }
    }

    /**
     * Metoda nacte uzivatelsky parametr z DB nebo vrati
     * <code>defaultValue</code>
     *
     * @param user uzivatel, ke kteremu se poji parametr
     * @param paramName jemeno pozadovaneho parametru
     * @param defaultValue defaultni hodnota, kdyz se parametr nenajde v DB
     * @return nalezena hodnota v DB nebo <code>defaultValue</code>
     */
    public Object getUserParam(User user, String paramName, Object defaultValue) {
        Object value = defaultValue;
        Userparam userParam = null;
        if (user instanceof User && paramName instanceof String && defaultValue != null) {
            this.query = getEm().createQuery("SELECT p FROM Userparam p WHERE p.iduser=:user AND p.paramname=:paramName");
            this.query.setParameter("user", user);
            this.query.setParameter("paramName", paramName);
            try {
                userParam = (Userparam) this.query.getSingleResult();
            } catch (NoResultException | NonUniqueResultException | QueryTimeoutException e) {
                //System.out.println(" UserController.getUserParam("+paramName+",default:"+defaultValue+") chyba:" + e.getMessage());
            }
            if (userParam instanceof Userparam) {
                if (defaultValue instanceof Date) {
                    value = userParam.getDatevalue();
                } else if (defaultValue instanceof Double) {
                    value = userParam.getNumvalue();
                } else if (defaultValue instanceof String) {
                    value = userParam.getTextvalue();
                } else if (defaultValue instanceof Boolean) {
                    value = userParam.getNumvalue() != 0.0d;
                }
            }
        }
        return value;
    }

    /**
     * Metoda ulozi uzivatelsky parametr do DB
     *
     * @param user uzivatel, ke kteremu se poji parametr
     * @param paramName jemeno ukladaneho parametru
     * @param value hodnota parametru k ulozeni do DB
     * @return
     */
    public boolean setUserParam(User user, String paramName, Object value) {
        boolean lOk = user instanceof User && paramName instanceof String;
        Userparam userParam = null;
        if (lOk) {
            this.query = getEm().createQuery("SELECT p FROM Userparam p WHERE p.iduser=:user AND p.paramname=:paramName ");
            this.query.setParameter("user", user);
            this.query.setParameter("paramName", paramName);
            try {
                userParam = (Userparam) this.query.getSingleResult();
            } catch (NoResultException | NonUniqueResultException | QueryTimeoutException e) {
                // System.out.println(" UserController.setUserParam("+paramName+",value:"+value+") chyba:" + e.getMessage());
            }
            if (!(userParam instanceof Userparam)) {
                userParam = new Userparam();
                userParam.setId(UUID.randomUUID());
                userParam.setIduser(user);
                userParam.setParamname(paramName);
                userParam.setNewEntity(true);
                userParam.setPopis(user.getFullname().trim() + " : " + paramName.trim());
            }
            if (value instanceof Date) {
                userParam.setDatevalue((Date) value);
            } else if (value instanceof Double) {
                userParam.setNumvalue((Double) value);
            } else if (value instanceof String) {
                userParam.setTextvalue((String) value);
            } else if (value instanceof Boolean) {
                userParam.setNumvalue((Boolean) value ? 1.0d : 0.0d);
            } else {
                userParam.setTextvalue(value == null ? "" : value.toString());
            }
            try {
                if (getEm().find(Userparam.class, userParam.getId()) == null) {
                    getEm().persist(userParam);
                    userParam.setNewEntity(false);
                } else {
                    getEm().merge(userParam);
                }
            } catch (Exception e) {
                Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, e);
                for (ConstraintViolation cv : ((ConstraintViolationException) e).getConstraintViolations()) {
                    cv.getLeafBean();
                }

                lOk = false;
            }
        }
        return lOk;
    }
}
