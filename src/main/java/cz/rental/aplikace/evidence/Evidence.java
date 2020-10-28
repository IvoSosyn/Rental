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
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.event.SelectEvent;

@Named(value = "evidence")
@Stateless
public class Evidence implements Serializable {

    static final long serialVersionUID = 42L;

    @Inject
    private Ucet ucet;

    private Date platiOd;
    private Date platiDo;
    private Date datumZmeny;

    @PostConstruct
    public void init() {
        platiOd=ucet.getAccount().getPlatiod();
        platiDo=ucet.getAccount().getPlatido();
        datumZmeny=ucet.getUzivatel().getDatumZmeny();
    }

    public void handleDateSelect(SelectEvent event) {
        Date date = (Date) event.getObject();
        //Add facesmessage
        PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Metoda evidence.handleDateSelect() není implementována.", "Předávaná hodnota: " + Aplikace.getSimpleDateFormat().format(date)));
    }

    public void handleDateChange(ValueChangeEvent e) {
          System.out.println("valueChangeListener invoked on "+e.getComponent().getClientId(FacesContext.getCurrentInstance())+":" 
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
        return platiOd;
    }

    /**
     * @param platiOd the platiOd to set
     */
    public void setPlatiOd(Date platiOd) {
        this.platiOd = platiOd;
    }

    /**
     * @return the platiDo
     */
    public Date getPlatiDo() {
        return platiDo;
    }

    /**
     * @param platiDo the platiDo to set
     */
    public void setPlatiDo(Date platiDo) {
        this.platiDo = platiDo;
    }

    /**
     * @return the datumZmeny
     */
    public Date getDatumZmeny() {
        return datumZmeny;
    }

    /**
     * @param datumZmeny the datumZmeny to set
     */
    public void setDatumZmeny(Date datumZmeny) {
        this.datumZmeny = datumZmeny;
    }
}
