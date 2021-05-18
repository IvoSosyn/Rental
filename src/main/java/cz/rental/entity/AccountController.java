/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
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
public class AccountController extends JpaController {

    private Query query = null;

    @PostConstruct
    public void init() {
    }

    /**
     * Metoda vraci 'Account' DB zaznam pro pozadovany jedinecny PIN
     *
     * @param pin jedinecny PIN uctu
     * @return pozadovany Account nebo 'null', kdyz ucet neexistuje
     */
    public Account getAccountForPIN(int pin) {
        Account account = null;
        this.query = getEm().createQuery("SELECT a FROM Account a WHERE a.pin=:pin ");
        this.query.setParameter("pin", pin);
        ArrayList<Account> accounts = new ArrayList<>(query.getResultList());
        if (accounts.size() > 0) {
            account = accounts.get(0);
        }
        return account;
    }

    /**
     * Metoda vraci ucet z DB na zaklade povinnych parametru PIN a EMAIL
     *
     * @param pin jednoznacny PIN vlastnika uctu
     * @param email vstupni email vlastnika uctu, bude duplikovan v uzivateli
     * @return ucet nebo null, kdyz ucet neexistuje
     */
    public Account getAccountForPinAndEmail(int pin, String email) {
        Account account = null;
        this.query = getEm().createQuery("SELECT a FROM User u INNER JOIN u.idaccount a WHERE a.pin=:pin AND u.email=:email ");
        this.query.setParameter("pin", pin);
        this.query.setParameter("email", email);
        ArrayList<Account> accounts = new ArrayList<>(query.getResultList());
        if (accounts.size() > 0) {
            account = accounts.get(0);
        }
        return account;
    }

    /**
     * Metoda ulozi polozku ucet do DB
     *
     * @param account ucet k ulozeni do DB
     * @throws Exception vyjimka v pripade, ze se nepodarilo ulozit ucet do DB
     */
    public void saveAccount(Account account) throws Exception {
        Account findAccount = (Account) this.findEntita(account);
        if (findAccount instanceof Account) {
            getEm().merge(account);
        } else {
            getEm().persist(account);
            account.setNewEntity(false);
        }
        account = (Account) this.findEntita(account);
    }
}
