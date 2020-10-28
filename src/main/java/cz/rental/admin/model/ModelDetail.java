/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.aplikace.Uzivatel;
import cz.rental.aplikace.Ucet;
import cz.rental.entity.Attribute;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 * Trida je back-end tabulky "Attribute" pro zadany "Typetnity"
 *
 * @author sosyn
 */
@Named("modelDetail")
@Stateless
public class ModelDetail implements Serializable {

    static final long serialVersionUID = 42L;

    private Attribute attribute = null;
    ArrayList<Character> editabelAttrsize;
    ArrayList<Character> editabelAttrdecimal;

    @EJB
    cz.rental.entity.AttributeController controller;
    
    @Inject
    Ucet ucet;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
//        try {
//            ucet = (Ucet) InitialContext.doLookup("java:module/Account!cz.rental.aplikace.registrace.Account");
//            user = (Uzivatel) InitialContext.doLookup("java:module/User!cz.rental.aplikace.User");
//        } catch (NamingException ex) {
//            Logger.getLogger(Ucet.class.getName()).log(Level.SEVERE, null, ex);
//        }        
        
        editabelAttrsize = new ArrayList<>();
        editabelAttrsize.add('C');
        editabelAttrsize.add('N');
        editabelAttrsize.add('I');
        editabelAttrdecimal = new ArrayList<>();
        editabelAttrdecimal.add('N');
        System.out.println(" ModelDetail.init()");
    }

    public void onRowSelect(SelectEvent event) {
        Attribute attrLocal = (Attribute) event.getObject();
//        System.out.println(" ModelDetail.onRowSelect  attrLocal.getId()="+attrLocal.getId());
//        System.out.println(" ModelDetail.onRowSelect  attrLocal.getPoaradi()="+attrLocal.getPoradi());
//        System.out.println(" ModelDetail.onRowSelect  attrLocal.getAttrname()="+attrLocal.getAttrname());
//        System.out.println(" ModelDetail.onRowSelect  attrLocal.getPopis()="+attrLocal.getPopis());
//        try {
//            Object obj=InitialContext.doLookup("java:module/ModelDetailValidator");
//            cz.rental.admin.model.ModelDetailValidator mdv = (cz.rental.admin.model.ModelDetailValidator) obj ;
//            mdv.setAttribute(attrLocal);
//            System.out.println("ModelDetail.onRowSelect mdv.getAttribute().getId()=" + mdv.getAttribute().getId());
//            System.out.println("ModelDetail.onRowSelect mdv.getAttribute().getPoradi()=" + mdv.getAttribute().getPoradi());
//            System.out.println("ModelDetail.onRowSelect mdv.getAttribute().getAttrname()=" + mdv.getAttribute().getAttrname());
//            System.out.println("ModelDetail.onRowSelect mdv.getAttribute().getPopis()=" + mdv.getAttribute().getPopis());
//        } catch (NamingException ex) {
//        }
        setAttribute(attrLocal);
    }

    public void onRowUnselect(UnselectEvent event) {
        System.out.println("ModelDetail.onRowUnselect  event.getObject()=" + event.getObject());
        setAttribute(null);
    }

    public Boolean isEditable() {
        boolean isEditable = ucet.getUzivatel().getParam(Uzivatel.SUPERVISOR, false) || ucet.getUzivatel().getParam(cz.rental.aplikace.Uzivatel.MODEL_EDIT, false);
        UIComponent uic = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        if (!(this.attribute instanceof Attribute)) {
            isEditable = false;
        }
        if (isEditable && uic.getId().equals("attrsize")) {
            // isEditable=this.attribute.getAttrtype().compareTo('N')==0 || this.attribute.getAttrtype().compareTo('C')==0;
            isEditable = this.editabelAttrsize.contains(this.attribute.getAttrtype());
        }
        if (isEditable && uic.getId().equals("attrdecimal")) {
            isEditable = this.editabelAttrdecimal.contains(this.attribute.getAttrtype());
        }
        if (isEditable && uic.getId().equals("attrsystem")) {
            isEditable = ucet.getUzivatel().getParam(Uzivatel.SUPERVISOR, false);
        }
        // Systemove polozky muze editovat pouze SUPERVISOR
        if (isEditable && this.attribute.getAttrsystem() != null && this.attribute.getAttrsystem()) {
            isEditable = ucet.getUzivatel().getParam(Uzivatel.SUPERVISOR, false);
        }

        return isEditable;
    }

    public void attrtypeChange() {
        if (!(this.attribute instanceof Attribute)) {

        } else if (this.attribute.getAttrtype().compareTo('D') == 0) {
            this.attribute.setAttrsize(BigInteger.valueOf(10));
            this.attribute.setAttrdecimal(BigInteger.valueOf(0));
        } else if (this.attribute.getAttrtype().compareTo('L') == 0) {
            this.attribute.setAttrsize(BigInteger.valueOf(1));
            this.attribute.setAttrdecimal(BigInteger.valueOf(0));
        } else if (this.attribute.getAttrtype().compareTo('T') == 0) {
            this.attribute.setAttrsize(BigInteger.valueOf(16));
            this.attribute.setAttrdecimal(BigInteger.valueOf(0));
        } else if (this.attribute.getAttrtype().compareTo('I') == 0) {
            this.attribute.setAttrdecimal(BigInteger.valueOf(0));
        }
    }

    public boolean isValueUsed(String alias, String whereCondition) {
        boolean ret = false;
//        System.out.println(" Selected this.attribute.getId()="+this.attribute.getId());
//        System.out.println(" Selected this.attribute.getIdtypentity()="+this.attribute.getIdtypentity());
        ArrayList<Attribute> attrs = controller.getAttributeWhere(this.attribute.getIdtypentity(), alias, whereCondition);
        for (Attribute attr : attrs) {
            if (!attr.getId().equals(this.attribute.getId())) {
//                System.out.println("  attr.getId()="+attr.getId());
                ret = true;
                break;
            }
        }
        return ret;
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return this.attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
}
