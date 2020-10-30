/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.aplikace.Uzivatel;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import static javax.ejb.TransactionManagementType.CONTAINER;
import javax.inject.Inject;
import javax.persistence.Query;

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
     * @return Uzivatelsky zaznam nebo 'null', kdyz se uzivatel neexistuje
     */
    public User getUserForAccount(Account account, String email) {
        this.query = getEm().createQuery("SELECT u FROM User u WHERE u.idaccount=:account AND u.email=:email ");
        this.query.setParameter("account", account);
        this.query.setParameter("email", email);
        User accUser = (User) query.getSingleResult();
        return accUser;
    }


    /**
     * Metoda ulozi polozku User do DB
     *
     * @param user User k ulozeni do DB
     * @throws Exception vyjimka v pripade, ze se nepodarilo ulozit User do DB
     */
    public void saveUser(User user) throws Exception {
        if (user.isNewEntity()) {
            getEm().persist(user);

        } else {
            getEm().merge(user);
        }
    }
}
