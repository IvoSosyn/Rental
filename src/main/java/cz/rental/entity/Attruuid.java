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
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 *
 * @author ivo
 */
@Entity
@Table(catalog = "najem", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Attruuid.findAll", query = "SELECT a FROM Attruuid a")})
public class Attruuid extends EntitySuperClassNajem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @NotNull
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID identita;

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @Column(nullable = true, columnDefinition = "UUID")
    private UUID idtable;

    private Integer edit;

    @JoinColumn(name = "idattribute", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Attribute idattribute;

    public Attruuid() {
        super();
    }

    public Object getIdentita() {
        return identita;
    }

    public void setIdentita(UUID identita) {
        this.identita = identita;
    }

    public UUID getIdtable() {
        return idtable;
    }

    public void setIdtable(UUID idtable) {
        this.idtable = idtable;
    }

    public Integer getEdit() {
        return edit;
    }

    public void setEdit(Integer edit) {
        this.edit = edit;
    }

    /**
     * @return the idattribute
     */
    public Attribute getIdattribute() {
        return idattribute;
    }

    /**
     * @param idattribute the idattribute to set
     */
    public void setIdattribute(Attribute idattribute) {
        this.idattribute = idattribute;
    }

    @Override
    public String toString() {
        return "entity.Attruuid[ id=" + this.getId() + " ]";
    }

}
