/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.entity.Attribute;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 * Trida je back-end tabulky "Attribute" pro zadany "Typetnity"
 *
 * @author sosyn
 */
@Named("modelDetail")
@Stateless
public class ModelDetail{

    private Attribute attribute = null;

    @EJB
    cz.rental.entity.AttributeController controller;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
    }

    public void onRowSelect(SelectEvent event) {
        setAttribute((Attribute) event.getObject());

    }

    public void onRowUnselectSelect(UnselectEvent event) {
        setAttribute(null);
    }
    
    public void saveAttribute() {
        System.out.println("saveAttribute");
    }
    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

}
