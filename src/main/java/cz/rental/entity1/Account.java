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
    @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a")})
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(nullable = false)
    private Object id;
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
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(nullable = false, length = 2147483647)
    private String passwordsha512;
    @Size(max = 2147483647)
    @Column(length = 2147483647)
    private String passwordhelp;
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
    @OneToMany(mappedBy = "idaccount")
    private Collection<Typentity> typentityCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idaccount")
    private Collection<User> userCollection;
    @JoinColumn(name = "idmodel", referencedColumnName = "id")
    @ManyToOne
    private Typentity idmodel;

    public Account() {
    }

    public Account(Object id) {
        this.id = id;
    }

    public Account(Object id, int pin, String email, String passwordsha512, Date timeinsert, Date timemodify) {
        this.id = id;
        this.pin = pin;
        this.email = email;
        this.passwordsha512 = passwordsha512;
        this.timeinsert = timeinsert;
        this.timemodify = timemodify;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
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
    public Collection<Typentity> getTypentityCollection() {
        return typentityCollection;
    }

    public void setTypentityCollection(Collection<Typentity> typentityCollection) {
        this.typentityCollection = typentityCollection;
    }

    @XmlTransient
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
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.rental.entity1.Account[ id=" + id + " ]";
    }
    
}
