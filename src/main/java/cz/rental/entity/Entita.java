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
    @NamedQuery(name = "Entita.findAll", query = "SELECT e FROM Entita e"),
    @NamedQuery(name = "Entita.findByPlatiod", query = "SELECT e FROM Entita e WHERE e.platiod = :platiod"),
    @NamedQuery(name = "Entita.findByPlatido", query = "SELECT e FROM Entita e WHERE e.platido = :platido"),
    @NamedQuery(name = "Entita.findByPopis", query = "SELECT e FROM Entita e WHERE e.popis = :popis"),
    @NamedQuery(name = "Entita.findByTimeinsert", query = "SELECT e FROM Entita e WHERE e.timeinsert = :timeinsert"),
    @NamedQuery(name = "Entita.findByTimemodify", query = "SELECT e FROM Entita e WHERE e.timemodify = :timemodify"),
    @NamedQuery(name = "Entita.findByUsermodify", query = "SELECT e FROM Entita e WHERE e.usermodify = :usermodify")})
public class Entita extends EntitySuperClassNajem implements Serializable {

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @NotNull
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID idparent;

    @JoinColumn(name = "idtypentity", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Typentity idtypentity;

    public Entita() {
        super();
    }

    public UUID getIdparent() {
        return idparent;
    }

    public void setIdparent(UUID idparent) {
        this.idparent = idparent;
    }

    /**
     * @return the idtypentity
     */
    public Typentity getIdtypentity() {
        return idtypentity;
    }

    /**
     * @param idtypentity the idtypentity to set
     */
    public void setIdtypentity(Typentity idtypentity) {
        this.idtypentity = idtypentity;
    }

    @Override
    public String toString() {
        return "entity.Entita[ id=" + this.getId() + " ]";
    }

}
