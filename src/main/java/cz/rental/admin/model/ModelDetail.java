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
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.spinner.Spinner;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 * Trida je back-end tabulky "Attribute" pro zadany "Typetnity"
 *
 * @author sosyn
 */
@Named("modelDetail")
@SessionScoped
public class ModelDetail implements Serializable {

    static final long serialVersionUID = 42L;

    private Attribute attribute = new Attribute();
    private Attribute beforeEditAttr = new Attribute();
    ArrayList<Character> editabelAttrsize;
    ArrayList<Character> editabelAttrdecimal;

    @EJB
    cz.rental.entity.AttributeController controller;

    @Inject
    Ucet ucet;

    ArrayList<String> uiiComponents = new ArrayList<>();

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

        /**
         * Nacist ID editacnich komponent formulare s detailem Attribute k
         * pozdejsi validaci a resetu techto polozek
         */
        UIComponent uiGrid = FacesContext.getCurrentInstance().getViewRoot().findComponent("formModelDetail:gridModelDetail");
        for (UIComponent uic : uiGrid.getChildren()) {
            if (uic instanceof UIInput) {
                this.uiiComponents.add(uic.getClientId());
            }
        }

        editabelAttrsize = new ArrayList<>();
        editabelAttrsize.add('C');
        editabelAttrsize.add('N');
        editabelAttrsize.add('I');
        editabelAttrdecimal = new ArrayList<>();
        editabelAttrdecimal.add('N');
        attribute = new Attribute();
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
        try {
            this.attribute = (Attribute) attrLocal.clone();
            this.beforeEditAttr = (Attribute) attrLocal.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ModelDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onRowUnselect(UnselectEvent event) {
        System.out.println("ModelDetail.onRowUnselect  event.getObject()=" + event.getObject());
        this.attribute = new Attribute();
    }

    /**
     * Test na moznost editace polozek foemulare detailu Attribute
     *
     * @return je|neni mozne polozku editovat
     */
    public Boolean isEditable() {
        UIComponent uic = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        boolean isEditable = ucet.getUzivatel().getParam(Uzivatel.SUPERVISOR, false) || ucet.getUzivatel().getParam(cz.rental.aplikace.Uzivatel.MODEL_EDIT, false);
        if (!(this.attribute instanceof Attribute)) {
            isEditable = false;
        }
        if (isEditable && uic.getId().equals("attrsize")) {
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

    /**
     * Test povoleni ulozit detail Attribute do DB
     *
     * @return je|neni mozne Attribute ulozit do DB
     */
    public Boolean isButtonSaveEnable() {
        boolean isEnable = !FacesContext.getCurrentInstance().isValidationFailed();
        for (String idUIcomponent : this.uiiComponents) {
            UIInput uii = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent(idUIcomponent);
            if (!(isEnable = uii.isValid())) {
                break;
            }
        }
        return isEnable;
    }

    /**
     * Vrati puvodni hodnoty Attribute a provede reset vstupnich polozek
     *
     * @param event
     */
    public void resetAttributes(ActionEvent event) {
        this.attribute = this.beforeEditAttr;
        FacesContext.getCurrentInstance().getViewRoot().resetValues(FacesContext.getCurrentInstance(), uiiComponents);
    }

    public void attrtypeChange() {
        if (!(this.attribute instanceof Attribute)) {

        } else if (this.attribute.getAttrtype().compareTo('C') == 0) {
            this.attribute.setAttrdecimal(BigInteger.valueOf(0));
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

    public void changeValue(javax.faces.event.AjaxBehaviorEvent e) {
        UIComponent uic= PrimeFacesContext.getCurrentInstance().getViewRoot().findComponent("formModelDetail:attrsize");
        System.out.println(" e.getSource().getClass(): "+e.getSource().getClass() );
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        if (!(this.attribute instanceof Attribute)) {
            this.attribute = new Attribute();
        }
        return this.attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
}
