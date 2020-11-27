/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 *
 * @author ivo
 */
@Entity
@Table(catalog = "najem", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Attrdate.findAll", query = "SELECT a FROM Attrdate a"),
    @NamedQuery(name = "Attrdate.findByDatumcas", query = "SELECT a FROM Attrdate a WHERE a.datumcas = :datumcas"),
    @NamedQuery(name = "Attrdate.findByPlatiod", query = "SELECT a FROM Attrdate a WHERE a.platiod = :platiod"),
    @NamedQuery(name = "Attrdate.findByPlatido", query = "SELECT a FROM Attrdate a WHERE a.platido = :platido"),
    @NamedQuery(name = "Attrdate.findByPopis", query = "SELECT a FROM Attrdate a WHERE a.popis = :popis"),
    @NamedQuery(name = "Attrdate.findByTimeinsert", query = "SELECT a FROM Attrdate a WHERE a.timeinsert = :timeinsert"),
    @NamedQuery(name = "Attrdate.findByTimemodify", query = "SELECT a FROM Attrdate a WHERE a.timemodify = :timemodify"),
    @NamedQuery(name = "Attrdate.findByUsermodify", query = "SELECT a FROM Attrdate a WHERE a.usermodify = :usermodify")})
public class Attrdate extends EntitySuperClassNajem implements Serializable {
    
    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @NotNull
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID identita;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumcas;

    private Integer edit;
    
    @JoinColumn(name = "idattribute", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Attribute idattribute;

    public Attrdate() {
        super();
    }

    public UUID getIdentita() {
        return identita;
    }

    public void setIdentita(UUID identita) {
        this.identita = identita;
    }

    public Date getDatumcas() {
        return datumcas;
    }

    public void setDatumcas(Date datumcas) {
        this.datumcas = datumcas;
    }

    public Attribute getIdattribute() {
        return idattribute;
    }

    public void setIdattribute(Attribute idattribute) {
        this.idattribute = idattribute;
    }

    /**
     * @return the edit
     */
    public Integer getEdit() {
        return edit;
    }

    /**
     * @param edit the edit to set
     */
    public void setEdit(Integer edit) {
        this.edit = edit;
    }
    @Override
    public String toString() {
        return "entity.Attrdate[ id=" + this.getId() + " ]";
    }

}
