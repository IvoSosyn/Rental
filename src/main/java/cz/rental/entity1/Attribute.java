/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity1;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ivo
 */
@Entity
@Table(catalog = "najem", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Attribute.findAll", query = "SELECT a FROM Attribute a")})
public class Attribute implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(nullable = false)
    private Object id;
    @Lob
    private Object idtypentity;
    @Lob
    private Object identita;
    private Integer poradi;
    private Boolean attrsystem;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(nullable = false, length = 2147483647)
    private String attrname;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Character attrtype;
    private BigInteger attrsize;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private BigInteger attrdecimal;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String attrparser;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String attrmask;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String script;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String tables;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idattribute")
    private Collection<Attrdate> attrdateCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idattribute")
    private Collection<Attrnumeric> attrnumericCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idattribute")
    private Collection<Attruuid> attruuidCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idattribute")
    private Collection<Attrtext> attrtextCollection;

    public Attribute() {
    }

    public Attribute(Object id) {
        this.id = id;
    }

    public Attribute(Object id, String attrname, Character attrtype, BigInteger attrdecimal, Date timeinsert, Date timemodify) {
        this.id = id;
        this.attrname = attrname;
        this.attrtype = attrtype;
        this.attrdecimal = attrdecimal;
        this.timeinsert = timeinsert;
        this.timemodify = timemodify;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getIdtypentity() {
        return idtypentity;
    }

    public void setIdtypentity(Object idtypentity) {
        this.idtypentity = idtypentity;
    }

    public Object getIdentita() {
        return identita;
    }

    public void setIdentita(Object identita) {
        this.identita = identita;
    }

    public Integer getPoradi() {
        return poradi;
    }

    public void setPoradi(Integer poradi) {
        this.poradi = poradi;
    }

    public Boolean getAttrsystem() {
        return attrsystem;
    }

    public void setAttrsystem(Boolean attrsystem) {
        this.attrsystem = attrsystem;
    }

    public String getAttrname() {
        return attrname;
    }

    public void setAttrname(String attrname) {
        this.attrname = attrname;
    }

    public Character getAttrtype() {
        return attrtype;
    }

    public void setAttrtype(Character attrtype) {
        this.attrtype = attrtype;
    }

    public BigInteger getAttrsize() {
        return attrsize;
    }

    public void setAttrsize(BigInteger attrsize) {
        this.attrsize = attrsize;
    }

    public BigInteger getAttrdecimal() {
        return attrdecimal;
    }

    public void setAttrdecimal(BigInteger attrdecimal) {
        this.attrdecimal = attrdecimal;
    }

    public String getAttrparser() {
        return attrparser;
    }

    public void setAttrparser(String attrparser) {
        this.attrparser = attrparser;
    }

    public String getAttrmask() {
        return attrmask;
    }

    public void setAttrmask(String attrmask) {
        this.attrmask = attrmask;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables;
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

    @XmlTransient
    public Collection<Attrdate> getAttrdateCollection() {
        return attrdateCollection;
    }

    public void setAttrdateCollection(Collection<Attrdate> attrdateCollection) {
        this.attrdateCollection = attrdateCollection;
    }

    @XmlTransient
    public Collection<Attrnumeric> getAttrnumericCollection() {
        return attrnumericCollection;
    }

    public void setAttrnumericCollection(Collection<Attrnumeric> attrnumericCollection) {
        this.attrnumericCollection = attrnumericCollection;
    }

    @XmlTransient
    public Collection<Attruuid> getAttruuidCollection() {
        return attruuidCollection;
    }

    public void setAttruuidCollection(Collection<Attruuid> attruuidCollection) {
        this.attruuidCollection = attruuidCollection;
    }

    @XmlTransient
    public Collection<Attrtext> getAttrtextCollection() {
        return attrtextCollection;
    }

    public void setAttrtextCollection(Collection<Attrtext> attrtextCollection) {
        this.attrtextCollection = attrtextCollection;
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
        if (!(object instanceof Attribute)) {
            return false;
        }
        Attribute other = (Attribute) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.rental.entity1.Attribute[ id=" + id + " ]";
    }
    
}
