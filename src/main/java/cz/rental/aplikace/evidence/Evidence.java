/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.aplikace.User;
import cz.rental.aplikace.registrace.Account;
import cz.rental.entity.Attribute;
import cz.rental.entity.Typentity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author ivo
 */
@Named(value = "evidence")
@Stateless
public class Evidence implements Serializable {

    static final long serialVersionUID = 42L;

    static final int COUNT_ATTRIBUTE_NEW = 5;

    private Typentity typentity = null;
    private ArrayList<Attribute> attributes = new ArrayList<>();

    @EJB
    cz.rental.entity.AttributeController controller;
    @EJB
    cz.rental.entity.TypentityController typEntityController;

    Account account;
    User user;

    @PostConstruct
    public void init() {
        try {
            account = (Account) InitialContext.doLookup("java:module/Account!cz.rental.aplikace.registrace.Account");
            user = (User) InitialContext.doLookup("java:module/User!cz.rental.aplikace.User");
        } catch (NamingException ex) {
            Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.account != null && this.account.getUser() != null && this.account.getCustomerModel() != null) {
            loadAttributesForTypentity(this.account.getCustomerModel());
        } else {
            // Pouze pro testovani
            for (Typentity tp : this.typEntityController.getRootTypEntity()) {
                loadAttributesForTypentity(tp);
            }
        }

    }

    /**
     * Metoda nacte pro zadany "typentity" pole "attribute" a ulozi do pole
     * attributes
     *
     * @param typentity
     */
    public void loadAttributesForTypentity(Typentity typentity) {
        if (typentity == null) {
            return;
        }
        this.typentity = typentity;
        this.setAttributes(controller.getAttributeForTypentity(this.typentity));
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    /**
     * @return the attributes
     */
    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }
}
