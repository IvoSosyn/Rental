/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.evidence;

import cz.rental.aplikace.Ucet;
import cz.rental.utils.Aplikace;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.event.SelectEvent;

/**
 * Trida poskytuje metody pro obsluhu evidence Entita a jejich Attributes -
 * poskytuje instancni promenne cestou getXXX a setXXX metod s odkazy na 'Ucet'
 * a jeho clena tridy 'Uzivatel'
 *
 *
 * @author ivo
 */
@Named(value = "evidence")
@SessionScoped
public class Evidence implements Serializable {

    static final long serialVersionUID = 42L;

    @Inject
    private Ucet ucet;

    @PostConstruct
    public void init() {
    }

    public void handleDateSelect(SelectEvent event) {
        Date date = (Date) event.getObject();
        //Add facesmessage
        PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Metoda evidence.handleDateSelect() není implementována.", "Předávaná hodnota: " + Aplikace.getSimpleDateFormat().format(date)));
    }

    public void handleDateChange(ValueChangeEvent e) {
        System.out.println("valueChangeListener invoked on " + e.getComponent().getClientId(FacesContext.getCurrentInstance()) + ":"
                + " OLD: " + e.getOldValue()
                + " NEW: " + e.getNewValue());
        //  System.out.println("Evidence.platiOd: "+Aplikace.getSimpleDateFormat().format(this.platiOd)+ " / Evidence.platiDo: "+Aplikace.getSimpleDateFormat().format(this.platiDo) );
        // PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getComponent().getClientId(FacesContext.getCurrentInstance()),"Evidence.platiOd: "+Aplikace.getSimpleDateFormat().format(this.platiOd)+ " / Evidence.platiDo: "+Aplikace.getSimpleDateFormat().format(this.platiDo)));
    }

    /**
     * @return the ucet
     */
    public Ucet getUcet() {
        return ucet;
    }

    /**
     * @param ucet the ucet to set
     */
    public void setUcet(Ucet ucet) {
        this.ucet = ucet;
    }

    /**
     * @return the platiOd
     */
    public Date getPlatiOd() {
        return ucet.getUzivatel().getParam("ObdobiOD", Aplikace.getPlatiOd());
    }

    /**
     * @param platiOd the platiOd to set
     */
    public void setPlatiOd(Date platiOd) {
        ucet.getUzivatel().setParam("ObdobiOD", platiOd);
        if (!ucet.getUzivatel().setParam("ObdobiOD", platiOd)) {
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uložení parametru 'ObdobiOD' NEBYLA úspěšná.", "Předávaná hodnota: " + Aplikace.getSimpleDateFormat().format(platiOd)));
        }
    }

    /**
     * @return the platiDo
     */
    public Date getPlatiDo() {
        return ucet.getUzivatel().getParam("ObdobiDO", Aplikace.getPlatiDo());
    }

    /**
     * @param platiDo the platiDo to set
     */
    public void setPlatiDo(Date platiDo) {
        if (!ucet.getUzivatel().setParam("ObdobiDO", platiDo)) {
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uložení parametru 'ObdobiDO' NEBYLA úspěšná.", "Předávaná hodnota: " + Aplikace.getSimpleDateFormat().format(platiDo)));
        }
    }

    /**
     * @return the datumZmeny
     */
    public Date getDatumZmeny() {
        return ucet.getUzivatel().getParam("ZmenaOD", Aplikace.getPlatiDo());
    }

    /**
     * @param datumZmeny the datumZmeny to set
     */
    public void setDatumZmeny(Date datumZmeny) {
        if (!ucet.getUzivatel().setParam("ZmenaOD", datumZmeny)) {
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uložení parametru 'ZmenaOD' NEBYLA úspěšná.", "Předávaná hodnota: " + Aplikace.getSimpleDateFormat().format(datumZmeny)));
        }
    }

    /**
     * @return the datumZmeny
     */
    public String datumZmenyAsString() {
        return Aplikace.getSimpleDateFormat().format(ucet.getUzivatel().getParam("ZmenaOD", Aplikace.getZmenaOd()));
    }

}
