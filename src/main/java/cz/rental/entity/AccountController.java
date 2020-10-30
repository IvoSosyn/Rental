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
public class AccountController extends JpaController {

    private Query query = null;

    @Inject
    Uzivatel user;

    @PostConstruct
    public void init() {
//        try {
//
//            user = (Uzivatel) InitialContext.doLookup("java:module/User!cz.rental.aplikace.Uzivatel");
//        } catch (NamingException ex) {
//            Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /**
     * Metoda vraci 'Account' DB zaznam pro pozadovany jedinecny PIN
     *
     * @param pin jedinecny PIN uctu
     * @return pozadovany Account nebo 'null', kdyz ucet neexistuje
     */
    public Account getAccountForPIN(String pin) {
        this.query = getEm().createQuery("SELECT a FROM Account a WHERE a.pin=:pin ");
        this.query.setParameter("pin", pin);
        Account account = (Account) query.getSingleResult();
        return account;
    }

    /**
     * Metoda vraci ucet z DB na zaklade povinnych parametru PIN a EMAIL
     *
     * @param pin jednoznacny PIN vlastnika uctu
     * @param email vstupni email vlastnika uctu, bude duplikovan v uzivateli
     * @return ucet nebo null, kdyz ucet neexistuje
     */
    public Account getAccount(String pin, String email) {
        this.query = getEm().createQuery("SELECT a FROM Account a WHERE a.pin=:pin AND a.email=:email ");
        this.query.setParameter("pin", pin);
        this.query.setParameter("email", email);
        Account account = (Account) query.getSingleResult();
        return account;
    }

    /**
     * Metoda ulozi polozku ucet do DB
     *
     * @param account ucet k ulozeni do DB
     * @throws Exception vyjimka v pripade, ze se nepodarilo ulozit ucet do DB
     */
    public void saveAccount(Account account) throws Exception {
        if (account.isNewEntity()) {
            getEm().persist(account);

        } else {
            getEm().merge(account);
        }
    }
}
