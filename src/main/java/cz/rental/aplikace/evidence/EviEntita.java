/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.entity.Attribute;
import cz.rental.entity.Entita;
import cz.rental.entity.Typentity;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author ivo
 */
@Named(value = "eviEntita")
@Stateless
public class EviEntita implements Serializable {

    static final long serialVersionUID = 42L;

    static final int COUNT_ENTITA_NEW = 5;

    private Typentity typentity = null;
    private Entita entita = null;
    private ArrayList<Typentity> typenties = new ArrayList<>();
    private ArrayList<Entita> entities = new ArrayList<>();
    private ArrayList<Attribute> attributes = new ArrayList<>();

    @EJB
    cz.rental.entity.AttributeController controller;
    @EJB
    cz.rental.entity.TypentityController typEntityController;
    @Inject
    cz.rental.aplikace.User user;

    @PostConstruct
    public void init() {
    }

    /**
     * Metoda nacte pro zadany "typentity" pole "attribute" a ulozi do pole
     * attributes
     *
     * @param typentity
     */
    public void loadEntitaForTypentity(Entita entita) {
        if (entita == null) {
            return;
        }
        this.entita = entita;
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
