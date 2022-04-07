/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 *
 * @author ivo
 */
@Entity
@Table(catalog = "najem", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Typentity.findAll", query = "SELECT t FROM Typentity t"),
    @NamedQuery(name = "Typentity.findByAttrsystem", query = "SELECT t FROM Typentity t WHERE t.attrsystem = :attrsystem"),
    @NamedQuery(name = "Typentity.findByTypentity", query = "SELECT t FROM Typentity t WHERE t.typentity = :typentity"),
    @NamedQuery(name = "Typentity.findByPlatiod", query = "SELECT t FROM Typentity t WHERE t.platiod = :platiod"),
    @NamedQuery(name = "Typentity.findByPlatido", query = "SELECT t FROM Typentity t WHERE t.platido = :platido"),
    @NamedQuery(name = "Typentity.findByPopis", query = "SELECT t FROM Typentity t WHERE t.popis = :popis"),
    @NamedQuery(name = "Typentity.findByTimeinsert", query = "SELECT t FROM Typentity t WHERE t.timeinsert = :timeinsert"),
    @NamedQuery(name = "Typentity.findByTimemodify", query = "SELECT t FROM Typentity t WHERE t.timemodify = :timemodify"),
    @NamedQuery(name = "Typentity.findByUsermodify", query = "SELECT t FROM Typentity t WHERE t.usermodify = :usermodify")})
public class Typentity extends EntitySuperClassNajem implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @Column(nullable = true, columnDefinition = "UUID")
    private UUID idparent;

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @Column(nullable = true, columnDefinition = "UUID")
    private UUID idmodel;

    private Boolean attrsystem;

    @Size(max = 10)
    @Column(length = 10)
    private String typentity;

    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Character editor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idtypentity")
    private Collection<Entita> entitaCollection;

    @JoinColumn(name = "idaccount", referencedColumnName = "id")
    @ManyToOne
    private Account idaccount;

    @OneToMany(mappedBy = "idmodel")
    private Collection<Account> accountCollection;

    public Typentity() {
        super();
    }

    public UUID getIdparent() {
        return idparent;
    }

    public void setIdparent(UUID idparent) {
        this.idparent = idparent;
    }

    /**
     * @return the idmodel
     */
    public UUID getIdmodel() {
        return idmodel;
    }

    /**
     * @param idmodel the idmodel to set
     */
    public void setIdmodel(UUID idmodel) {
        this.idmodel = idmodel;
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

    @Override
    public String toString() {
        return this.typentity + "-" + this.getPopis();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the editor
     */
    public Character getEditor() {
        return editor;
    }

    /**
     * @param editor the editor to set
     */
    public void setEditor(Character editor) {
        this.editor = editor;
    }

    /**
     * @return the entitaCollection
     */
    public Collection<Entita> getEntitaCollection() {
        return entitaCollection;
    }

    /**
     * @param entitaCollection the entitaCollection to set
     */
    public void setEntitaCollection(Collection<Entita> entitaCollection) {
        this.entitaCollection = entitaCollection;
    }

    public Account getIdaccount() {
        return idaccount;
    }

    public void setIdaccount(Account idaccount) {
        this.idaccount = idaccount;
    }


    public Collection<Account> getAccountCollection() {
        return accountCollection;
    }

    public void setAccountCollection(Collection<Account> accountCollection) {
        this.accountCollection = accountCollection;
    }

}
