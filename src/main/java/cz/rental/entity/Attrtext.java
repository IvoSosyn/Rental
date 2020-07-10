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
    @NamedQuery(name = "Attrtext.findAll", query = "SELECT a FROM Attrtext a"),
    @NamedQuery(name = "Attrtext.findByPlatiod", query = "SELECT a FROM Attrtext a WHERE a.platiod = :platiod"),
    @NamedQuery(name = "Attrtext.findByPlatido", query = "SELECT a FROM Attrtext a WHERE a.platido = :platido"),
    @NamedQuery(name = "Attrtext.findByPopis", query = "SELECT a FROM Attrtext a WHERE a.popis = :popis"),
    @NamedQuery(name = "Attrtext.findByTimeinsert", query = "SELECT a FROM Attrtext a WHERE a.timeinsert = :timeinsert"),
    @NamedQuery(name = "Attrtext.findByTimemodify", query = "SELECT a FROM Attrtext a WHERE a.timemodify = :timemodify"),
    @NamedQuery(name = "Attrtext.findByUsermodify", query = "SELECT a FROM Attrtext a WHERE a.usermodify = :usermodify")})
public class Attrtext extends EntitySuperClassNajem implements Serializable {

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @NotNull
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID identita;

    @JoinColumn(name = "idattribute", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Attribute idattribute;

    private Integer edit;

    public Attrtext() {
        super();
    }

    public UUID getIdentita() {
        return identita;
    }

    public void setIdentita(UUID identita) {
        this.identita = identita;
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
        return "entity.Attrtext[ id=" + this.getId() + " ]";
    }

}
