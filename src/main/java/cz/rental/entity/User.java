/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import java.io.Serializable;
import java.util.Collection;
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
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")})
public class User extends EntitySuperClassNajem implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @Size(max = 150)
    @Column(length = 150)
    private String fullname;

    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    private String telnumber;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 512)
    @Column(nullable = false, length = 512)
    private String passwordsha512;

    @Size(max = 150)
    @Column(length = 150)
    private String passwordhelp;

    @JoinColumn(name = "idaccount", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Account idaccount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iduser")
    private Collection<Userparam> userparamCollection;

    public User() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelnumber() {
        return telnumber;
    }

    public void setTelnumber(String telnumber) {
        this.telnumber = telnumber;
    }

    public String getPasswordsha512() {
        return passwordsha512;
    }

    public void setPasswordsha512(String passwordsha512) {
        this.passwordsha512 = passwordsha512;
    }

    public String getPasswordhelp() {
        return passwordhelp;
    }

    public void setPasswordhelp(String passwordhelp) {
        this.passwordhelp = passwordhelp;
    }

    public Account getIdaccount() {
        return idaccount;
    }

    public void setIdaccount(Account idaccount) {
        this.idaccount = idaccount;
    }
    @XmlTransient
    public Collection<Userparam> getUserparamCollection() {
        return userparamCollection;
    }

    public void setUserparamCollection(Collection<Userparam> userparamCollection) {
        this.userparamCollection = userparamCollection;
    }


    @Override
    public String toString() {
        return "entity.User[ id=" + this.getId() + " ]";
    }

}
