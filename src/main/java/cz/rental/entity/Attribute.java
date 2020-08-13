/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
    @NamedQuery(name = "Attribute.findAll", query = "SELECT a FROM Attribute a"),
    @NamedQuery(name = "Attribute.findByAttrtype", query = "SELECT a FROM Attribute a WHERE a.attrtype = :attrtype"),
    @NamedQuery(name = "Attribute.findByAttrsize", query = "SELECT a FROM Attribute a WHERE a.attrsize = :attrsize"),
    @NamedQuery(name = "Attribute.findByAttrdecimal", query = "SELECT a FROM Attribute a WHERE a.attrdecimal = :attrdecimal"),
    @NamedQuery(name = "Attribute.findByAttrsystem", query = "SELECT a FROM Attribute a WHERE a.attrsystem = :attrsystem"),
    @NamedQuery(name = "Attribute.findByPoradi", query = "SELECT a FROM Attribute a WHERE a.poradi = :poradi"),
    @NamedQuery(name = "Attribute.findByPlatiod", query = "SELECT a FROM Attribute a WHERE a.platiod = :platiod"),
    @NamedQuery(name = "Attribute.findByPlatido", query = "SELECT a FROM Attribute a WHERE a.platido = :platido"),
    @NamedQuery(name = "Attribute.findByPopis", query = "SELECT a FROM Attribute a WHERE a.popis = :popis"),
    @NamedQuery(name = "Attribute.findByTimeinsert", query = "SELECT a FROM Attribute a WHERE a.timeinsert = :timeinsert"),
    @NamedQuery(name = "Attribute.findByTimemodify", query = "SELECT a FROM Attribute a WHERE a.timemodify = :timemodify"),
    @NamedQuery(name = "Attribute.findByUsermodify", query = "SELECT a FROM Attribute a WHERE a.usermodify = :usermodify")})
public class Attribute extends EntitySuperClassNajem implements Serializable, Cloneable {

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @NotNull
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID idtypentity;

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @NotNull
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID identita;

    @Basic(optional = true)
    @NotNull
    @Column(nullable = false)
    private String attrname;

    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Character attrtype;

    private BigInteger attrsize;

    @Basic(optional = false)
    @Column(nullable = true)
    private BigInteger attrdecimal;

    private Boolean attrsystem;

    @Basic(optional = true)
    @Column(nullable = true)
    private String attrparser;

    @Basic(optional = true)
    @Column(nullable = true)
    private String attrmask;

    @Basic(optional = true)
    @Column(nullable = true)
    private String tables;

    @Basic(optional = true)
    @Column(nullable = true)
    private String script;

    private Integer poradi;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idattribute")
    private Collection<Attrdate> attrdateCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idattribute")
    private Collection<Attrnumeric> attrnumericCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idattribute")
    private Collection<Attrtext> attrtextCollection;

    public Attribute() {
        super();
    }

    public UUID getIdtypentity() {
        return idtypentity;
    }

    public void setIdtypentity(UUID idtypentity) {
        this.idtypentity = idtypentity;
    }

    public UUID getIdentita() {
        return identita;
    }

    public void setIdentita(UUID identita) {
        this.identita = identita;
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

    public Boolean getAttrsystem() {
        return attrsystem;
    }

    public void setAttrsystem(Boolean attrsystem) {
        this.attrsystem = attrsystem;
    }

    public Integer getPoradi() {
        return poradi;
    }

    public void setPoradi(Integer poradi) {
        this.poradi = poradi;
    }

    public Collection<Attrdate> getAttrdateCollection() {
        return attrdateCollection;
    }

    public void setAttrdateCollection(Collection<Attrdate> attrdateCollection) {
        this.attrdateCollection = attrdateCollection;
    }

    public Collection<Attrnumeric> getAttrnumericCollection() {
        return attrnumericCollection;
    }

    public void setAttrnumericCollection(Collection<Attrnumeric> attrnumericCollection) {
        this.attrnumericCollection = attrnumericCollection;
    }

    public Collection<Attrtext> getAttrtextCollection() {
        return attrtextCollection;
    }

    public void setAttrtextCollection(Collection<Attrtext> attrtextCollection) {
        this.attrtextCollection = attrtextCollection;
    }

    @Override
    public String toString() {
        return "entity.Attribute[ id=" + this.getId() + " ]";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the attrname
     */
    public String getAttrname() {
        return attrname;
    }

    /**
     * @param attrname the attrname to set
     */
    public void setAttrname(String attrname) {
        this.attrname = attrname;
    }

    /**
     * @return the attrmask
     */
    public String getAttrmask() {
        return attrmask;
    }

    /**
     * @param attrmask the attrmask to set
     */
    public void setAttrmask(String attrmask) {
        this.attrmask = attrmask;
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @param script the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @return the attrparser
     */
    public String getAttrparser() {
        return attrparser;
    }

    /**
     * @param attrparser the attrparser to set
     */
    public void setAttrparser(String attrparser) {
        this.attrparser = attrparser;
    }

    /**
     * @return the tables
     */
    public String getTables() {
        return tables;
    }

    /**
     * @param tables the tables to set
     */
    public void setTables(String tables) {
        this.tables = tables;
    }

}
