/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

import cz.rental.aplikace.User;
import cz.rental.entity.AccountController;
import cz.rental.entity.Typentity;
import cz.rental.utils.SHA512;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import java.util.concurrent.ThreadLocalRandom;
import javax.faces.application.FacesMessage;

/**
 *
 * @author ivo
 */
@Named(value = "account")
@Stateful
public class Account {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private AccountController controller;
    @Inject
    private cz.rental.aplikace.User user;

    private UUID customerID = null;
    private String customerName = "";
    private String customerEmail = "";
    private String customerTel = "";
    private String customerPassword = "";
    private String customerPasswordControl = "";
    private String customerPasswordHelp = "";
    private String customerPasswordSHA512 = SHA512.hash512("");
    private int customerPin = 0;
    private String customerAddress = "";
    private Typentity customerModel = null;

    public Account() {
    }

    public Boolean isEditable() {
        boolean isEditable = getUser().getParam(User.SUPERVISOR, false) || getUser().getParam(cz.rental.aplikace.User.ACCOUNT_EDIT, false);
        UIComponent uic = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());

        return isEditable;
    }

    public boolean isButtonEnable() {
        return !FacesContext.getCurrentInstance().isValidationFailed();
    }

    public void changePassword() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        PrimeFaces.current().dialog().openDynamic("/aplikace/registrace/password.xhtml", options, null);
        //System.out.println("PrimeFaces.current().dialog().openDynamic");
    }

    public void closePassword(String password) {
        PrimeFaces.current().dialog().closeDynamic(password);
        //System.out.println("PrimeFaces.current().dialog().closeDynamic");
    }

    public void newPassword() {
        this.setCustomerPasswordSHA512(SHA512.hash512(this.customerPassword));
        //System.out.println("Account.newPassword " + this.customerPassword);
        //PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, this.getCustomerPassword(), this.getCustomerPasswordSHA512()));
    }

    public void changePin() {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 9999 + 1);
        this.setCustomerPin(randomNum);
    }

    public void saveAccount() throws Exception {
        if (this.customerID==null) {
            this.customerID=UUID.randomUUID();
        }
        controller.saveAccount(this);
    }

    /**
     * @return the customerID
     */
    public UUID getCustomerID() {
        return customerID;
    }

    /**
     * @param customerID the customerID to set
     */
    public void setCustomerID(UUID customerID) {
        this.customerID = customerID;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return the customerEmail
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * @param customerEmail the customerEmail to set
     */
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    /**
     * @return the customerPassword
     */
    public String getCustomerPassword() {
        return customerPassword;
    }

    /**
     * @param customerPassword the customerPassword to set
     */
    public void setCustomerPassword(String customerPassword) {
        this.customerPassword = customerPassword;
    }

    /**
     * @return the customerPasswordSHA512
     */
    public String getCustomerPasswordSHA512() {
        return customerPasswordSHA512;
    }

    /**
     * @param customerPasswordSHA512 the customerPasswordSHA512 to set
     */
    public void setCustomerPasswordSHA512(String customerPasswordSHA512) {
        this.customerPasswordSHA512 = customerPasswordSHA512;
    }

    /**
     * @return the customerPin
     */
    public int getCustomerPin() {
        return customerPin;
    }

    /**
     * @param customerPin the customerPin to set
     */
    public void setCustomerPin(int customerPin) {
        this.customerPin = customerPin;
    }

    /**
     * @return the customerAddress
     */
    public String getCustomerAddress() {
        return customerAddress;
    }

    /**
     * @param customerAddress the customerAddress to set
     */
    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    /**
     * @return the customerModel
     */
    public Typentity getCustomerModel() {
        return customerModel;
    }

    /**
     * @param customerModel the customerModel to set
     */
    public void setCustomerModel(Typentity customerModel) {
        this.customerModel = customerModel;
    }

    /**
     * @return the customerTel
     */
    public String getCustomerTel() {
        return customerTel;
    }

    /**
     * @param customerTel the customerTel to set
     */
    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }

    /**
     * @return the controller
     */
    public cz.rental.entity.AccountController getController() {
        return controller;
    }

    /**
     * @param controller the controller to set
     */
    public void setController(AccountController controller) {
        this.controller = controller;
    }

    /**
     * @return the user
     */
    public cz.rental.aplikace.User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(cz.rental.aplikace.User user) {
        this.user = user;
    }

    /**
     * @return the customerPasswordControl
     */
    public String getCustomerPasswordControl() {
        return customerPasswordControl;
    }

    /**
     * @param customerPasswordControl the customerPasswordControl to set
     */
    public void setCustomerPasswordControl(String customerPasswordControl) {
        this.customerPasswordControl = customerPasswordControl;
    }

    /**
     * @return the customerPasswordHelp
     */
    public String getCustomerPasswordHelp() {
        return customerPasswordHelp;
    }

    /**
     * @param customerPasswordHelp the customerPasswordHelp to set
     */
    public void setCustomerPasswordHelp(String customerPasswordHelp) {
        this.customerPasswordHelp = customerPasswordHelp;
    }
}
