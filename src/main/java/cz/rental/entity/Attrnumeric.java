/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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
    @NamedQuery(name = "Attrnumeric.findAll", query = "SELECT a FROM Attrnumeric a"),
    @NamedQuery(name = "Attrnumeric.findByCislo", query = "SELECT a FROM Attrnumeric a WHERE a.cislo = :cislo"),
    @NamedQuery(name = "Attrnumeric.findByPlatiod", query = "SELECT a FROM Attrnumeric a WHERE a.platiod = :platiod"),
    @NamedQuery(name = "Attrnumeric.findByPlatido", query = "SELECT a FROM Attrnumeric a WHERE a.platido = :platido"),
    @NamedQuery(name = "Attrnumeric.findByPopis", query = "SELECT a FROM Attrnumeric a WHERE a.popis = :popis"),
    @NamedQuery(name = "Attrnumeric.findByTimeinsert", query = "SELECT a FROM Attrnumeric a WHERE a.timeinsert = :timeinsert"),
    @NamedQuery(name = "Attrnumeric.findByTimemodify", query = "SELECT a FROM Attrnumeric a WHERE a.timemodify = :timemodify"),
    @NamedQuery(name = "Attrnumeric.findByUsermodify", query = "SELECT a FROM Attrnumeric a WHERE a.usermodify = :usermodify")})
public class Attrnumeric extends EntitySuperClassNajem implements Serializable {

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @NotNull
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID identita;

    private Double cislo;
    
    private Integer edit;
    
    @JoinColumn(name = "idattribute", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Attribute idattribute;

    public Attrnumeric() {
        super();
    }

    public UUID getIdentita() {
        return identita;
    }

    public void setIdentita(UUID identita) {
        this.identita = identita;
    }

    public Double getCislo() {
        return cislo;
    }

    public void setCislo(Double cislo) {
        this.cislo = cislo;
    }

    
    public Attribute getIdattribute() {
        return idattribute;
    }

    public void setIdattribute(Attribute idattribute) {
        this.idattribute = idattribute;
    }


    @Override
    public String toString() {
        return "entity.Attrnumeric[ id=" + this.getId() + " ]";
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

}
