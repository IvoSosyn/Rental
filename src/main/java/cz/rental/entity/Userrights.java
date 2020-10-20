/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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
@NamedQueries({
    @NamedQuery(name = "Userrights.findAll", query = "SELECT u FROM Userrights u")})
public class Userrights extends EntitySuperClassNajem implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @Size(max = 10)
    @Column(length = 10)
    private String rightname;

    @Size(max = 10)
    @Column(length = 10)
    private String tablename;

    @Basic(optional = false)
    @Converter(name = "uuidConverter", converterClass = UUIDConverter.class)
    @Convert("uuidConverter")
    @Column(nullable = true, columnDefinition = "UUID")
    private UUID idtable;

    @Size(max = 300)
    @Column(length = 300)
    private String textvalue;

    private BigInteger numbervalue;

    private Boolean supervisor;

    @JoinColumn(name = "iduser", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private User iduser;

    public Userrights() {
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

    public BigInteger getNumbervalue() {
        return numbervalue;
    }

    public void setNumbervalue(BigInteger numbervalue) {
        this.numbervalue = numbervalue;
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
    public String toString() {
        return "entity.Userrights[ id=" + this.getId() + " ]";
    }
}
