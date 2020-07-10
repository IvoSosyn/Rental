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
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 * Spolecna trida vsech entit
 *
 * @author Ivo
 * 
 */
@MappedSuperclass
public class EntitySuperClassNajem implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @NotNull
    @Column(nullable = false,columnDefinition = "UUID")
    private UUID id;

    @Size(max = 2048)
    @Column(length = 2048)
    private String popis;

    @Temporal(TemporalType.TIMESTAMP)
    private Date platiod;

    @Temporal(TemporalType.TIMESTAMP)
    private Date platido;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeinsert;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodify;

    @Size(max = 1024)
    @Column(length = 1024)
    private String usermodify;

    @Transient
    private boolean newEntity = false;

    @Transient
    private boolean delEntity = false;

    public EntitySuperClassNajem() {
        this.id = UUID.randomUUID() ;
        this.timeinsert = new Date();
        this.timemodify = new Date();
        this.usermodify = java.lang.System.getenv("username");
    }

    public EntitySuperClassNajem(UUID id) {
        this.id = id;
    }

    public EntitySuperClassNajem(UUID id, Date platiod, Date platido, Date timeinsert, Date timemodify) {
        this.id = id;
        this.platiod = platiod;
        this.platido = platido;
        this.timeinsert = timeinsert;
        this.timemodify = timemodify;

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public Date getPlatiod() {
        return platiod;
    }

    public void setPlatiod(Date platiod) {
        this.platiod = platiod;
    }

    public Date getPlatido() {
        return platido;
    }

    public void setPlatido(Date platido) {
        this.platido = platido;
    }

    public Date getTimeinsert() {
        return timeinsert;
    }

    public void setTimeinsert(Date timeinsert) {
        this.timeinsert = timeinsert;
    }

    public Date getTimemodify() {
        return timemodify;
    }

    public void setTimemodify(Date timemodify) {
        this.timemodify = timemodify;
    }

    public String getUsermodify() {
        return usermodify;
    }

    public void setUsermodify(String usermodify) {
        this.usermodify = usermodify;
    }

    /**
     * @return the newEntity
     */
    public boolean isNewEntity() {
        return newEntity;
    }

    /**
     * @param newEntity the newEntity to set
     */
    public void setNewEntity(boolean newEntity) {
        this.newEntity = newEntity;
    }

    /**
     * @return the delEntity
     */
    public boolean isDelEntity() {
        return delEntity;
    }

    /**
     * @param delEntity the delEntity to set
     */
    public void setDelEntity(boolean delEntity) {
        this.delEntity = delEntity;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EntitySuperClassNajem)) {
            return false;
        }
        EntitySuperClassNajem other = (EntitySuperClassNajem) object;
        return (this.id != null && other.id != null && this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "entity." + this.getClass().getName() + "[ id=" + id + " ]";
    }

}
