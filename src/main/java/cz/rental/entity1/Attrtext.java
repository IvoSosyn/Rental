/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity1;

import java.io.Serializable;
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
    @NamedQuery(name = "Attrtext.findAll", query = "SELECT a FROM Attrtext a")})
public class Attrtext implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(nullable = false)
    private Object id;
    @Lob
    private Object identita;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String text;
    private Integer edit;
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
    @JoinColumn(name = "idattribute", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Attribute idattribute;

    public Attrtext() {
    }

    public Attrtext(Object id) {
        this.id = id;
    }

    public Attrtext(Object id, Date timeinsert, Date timemodify) {
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

    public Object getIdentita() {
        return identita;
    }

    public void setIdentita(Object identita) {
        this.identita = identita;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getEdit() {
        return edit;
    }

    public void setEdit(Integer edit) {
        this.edit = edit;
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

    public Attribute getIdattribute() {
        return idattribute;
    }

    public void setIdattribute(Attribute idattribute) {
        this.idattribute = idattribute;
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
        if (!(object instanceof Attrtext)) {
            return false;
        }
        Attrtext other = (Attrtext) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.rental.entity1.Attrtext[ id=" + id + " ]";
    }
    
}
