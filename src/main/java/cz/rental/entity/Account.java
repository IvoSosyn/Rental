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

/**
 *
 * @author ivo
 */
@Entity
@Table(catalog = "najem", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a")})
public class Account extends EntitySuperClassNajem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int pin;

    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String fullname;

    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String street1;

    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String street2;

    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String city;

    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String postcode;

    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(nullable = false, length = 2147483647)
    private String email;

    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String telnumber;

    @Basic(optional = false)
    @Size(min = 1, max = 2147483647)
    @Column(nullable = false, length = 2147483647)
    private String passwordsha512;

    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String passwordhelp;

    @OneToMany(mappedBy = "idaccount")
    private Collection<Typentity> typentityCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idaccount")
    private Collection<User> userCollection;

    @JoinColumn(columnDefinition ="UUID" ,name = "idmodel", referencedColumnName = "id")
    @ManyToOne
    private Typentity idmodel;

    public Account() {
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
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


    public Collection<Typentity> getTypentityCollection() {
        return typentityCollection;
    }

    public void setTypentityCollection(Collection<Typentity> typentityCollection) {
        this.typentityCollection = typentityCollection;
    }

    public Collection<User> getUserCollection() {
        return userCollection;
    }

    public void setUserCollection(Collection<User> userCollection) {
        this.userCollection = userCollection;
    }

    public Typentity getIdmodel() {
        return idmodel;
    }

    public void setIdmodel(Typentity idmodel) {
        this.idmodel = idmodel;
    }

    @Override
    public String toString() {
        return "entity.Account[ id=" + this.getId() + " ]";
    }

}
