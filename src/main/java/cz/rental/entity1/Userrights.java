/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity1;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ivo
 */
@Entity
@Table(catalog = "najem", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Userrights.findAll", query = "SELECT u FROM Userrights u")})
public class Userrights implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(nullable = false)
    private Object id;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String rightname;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String tablename;
    @Lob
    private Object idtable;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String textvalue;
    private BigInteger numbervalue;
    @Temporal(TemporalType.TIMESTAMP)
    private Date platiod;
    @Temporal(TemporalType.TIMESTAMP)
    private Date platido;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String popis;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeinsert;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodify;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String usermodify;
    private Boolean supervisor;
    @JoinColumn(name = "iduser", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private User iduser;

    public Userrights() {
    }

    public Userrights(Object id) {
        this.id = id;
    }

    public Userrights(Object id, Date timeinsert, Date timemodify) {
        this.id = id;
        this.timeinsert = timeinsert;
        this.timemodify = timemodify;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getRightname() {
        return rightname;
    }

    public void setRightname(String rightname) {
        this.rightname = rightname;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public Object getIdtable() {
        return idtable;
    }

    public void setIdtable(Object idtable) {
        this.idtable = idtable;
    }

    public String getTextvalue() {
        return textvalue;
    }

    public void setTextvalue(String textvalue) {
        this.textvalue = textvalue;
    }

    public BigInteger getNumbervalue() {
        return numbervalue;
    }

    public void setNumbervalue(BigInteger numbervalue) {
        this.numbervalue = numbervalue;
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

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
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

    public Boolean getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Boolean supervisor) {
        this.supervisor = supervisor;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Userrights)) {
            return false;
        }
        Userrights other = (Userrights) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.rental.entity1.Userrights[ id=" + id + " ]";
    }
    
}
