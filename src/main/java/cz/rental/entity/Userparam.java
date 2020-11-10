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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
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
public class Userparam extends EntitySuperClassNajem implements Serializable, Cloneable  {

    private static final long serialVersionUID = 1L;

    
    @Size(max = 100)
    @Column(length = 30)
    private String paramname;
    
    @Size(max = 100)
    @Column(length = 30)
    private String tablename;    

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @Column(nullable = true)
    private UUID idtable;

    @Size(max = 4096)
    @Column(length = 1024)
    private String textvalue;
    
    private Double numvalue;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date datevalue;
    
    
    @JoinColumn(name = "iduser", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private User iduser;

    public Userparam() {
    }

    public String getParamname() {
        return paramname;
    }

    public void setParamname(String paramname) {
        this.paramname = paramname;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public UUID getIdtable() {
        return idtable;
    }

    public void setIdtable(UUID idtable) {
        this.idtable = idtable;
    }

    public String getTextvalue() {
        return textvalue;
    }

    public void setTextvalue(String textvalue) {
        this.textvalue = textvalue;
    }

    public Double getNumvalue() {
        return numvalue;
    }

    public void setNumvalue(Double numvalue) {
        this.numvalue = numvalue;
    }

    public Date getDatevalue() {
        return datevalue;
    }

    public void setDatevalue(Date datevalue) {
        this.datevalue = datevalue;
    }

    public User getIduser() {
        return iduser;
    }

    public void setIduser(User iduser) {
        this.iduser = iduser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Userparam)) {
            return false;
        }
        Userparam other = (Userparam) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.rental.entity1.Userparam[ id=" + getId() + " ]";
    }

}
