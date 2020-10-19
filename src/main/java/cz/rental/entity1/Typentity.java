/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity1;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
    @NamedQuery(name = "Typentity.findAll", query = "SELECT t FROM Typentity t")})
public class Typentity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(nullable = false)
    private Object id;
    @Lob
    private Object idparent;
    private Boolean attrsystem;
    @Size(max = 10)
    @Column(length = 10)
    private String typentity;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Character editor;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idtypentity")
    private Collection<Entita> entitaCollection;
    @JoinColumn(name = "idaccount", referencedColumnName = "id")
    @ManyToOne
    private Account idaccount;
    @OneToMany(mappedBy = "idmodel")
    private Collection<Account> accountCollection;

    public Typentity() {
    }

    public Typentity(Object id) {
        this.id = id;
    }

    public Typentity(Object id, Character editor, Date timeinsert, Date timemodify) {
        this.id = id;
        this.editor = editor;
        this.timeinsert = timeinsert;
        this.timemodify = timemodify;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getIdparent() {
        return idparent;
    }

    public void setIdparent(Object idparent) {
        this.idparent = idparent;
    }

    public Boolean getAttrsystem() {
        return attrsystem;
    }

    public void setAttrsystem(Boolean attrsystem) {
        this.attrsystem = attrsystem;
    }

    public String getTypentity() {
        return typentity;
    }

    public void setTypentity(String typentity) {
        this.typentity = typentity;
    }

    public Character getEditor() {
        return editor;
    }

    public void setEditor(Character editor) {
        this.editor = editor;
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
    public Collection<Entita> getEntitaCollection() {
        return entitaCollection;
    }

    public void setEntitaCollection(Collection<Entita> entitaCollection) {
        this.entitaCollection = entitaCollection;
    }

    public Account getIdaccount() {
        return idaccount;
    }

    public void setIdaccount(Account idaccount) {
        this.idaccount = idaccount;
    }

    @XmlTransient
    public Collection<Account> getAccountCollection() {
        return accountCollection;
    }

    public void setAccountCollection(Collection<Account> accountCollection) {
        this.accountCollection = accountCollection;
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
        if (!(object instanceof Typentity)) {
            return false;
        }
        Typentity other = (Typentity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.rental.entity1.Typentity[ id=" + id + " ]";
    }
    
}
