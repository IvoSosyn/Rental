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
@Table(catalog = "najem", schema="public")
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
public class Typentity extends EntitySuperClassNajem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @NotNull
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID idparent;

    private Boolean attrsystem;

    @Size(max = 6)
    @Column(length = 6)
    private String typentity;

    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Character editor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idtypentity")
    private Collection<Entita> entitaCollection;

    public Typentity() {
        super();
    }

    public UUID getIdparent() {
        return idparent;
    }

    public void setIdparent(UUID idparent) {
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

    @Override
    public String toString() {
        return typentity + "-" + this.getPopis();
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
}
