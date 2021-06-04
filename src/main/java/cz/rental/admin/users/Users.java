/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.users;

import cz.rental.aplikace.*;
import cz.rental.entity.User;
import cz.rental.entity.UserController;
import cz.rental.utils.Aplikace;
import cz.rental.utils.Password;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import javax.ejb.EJB;
import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datepicker.DatePicker;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.toggleswitch.ToggleSwitch;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.LangUtils;

/**
 *
 * @author ivo
 */
@Named(value = "users")
@SessionScoped
public class Users implements Serializable {

    static final long serialVersionUID = 42L;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private UserController userController;
    @Inject
    private Ucet ucet;
    @Inject
    private Password password;
    private Uzivatel uzivatel;
    private ArrayList<User> users = new ArrayList();
    private ArrayList<User> filteredUsers = new ArrayList();
    private User selectedUser;

    @PostConstruct
    public void init() {
        try {
            getUsersForAccount();
            this.setUzivatel(getUcet().getUzivatel().clone());
            this.selectedUser = this.getUzivatel().getUser();
            this.getUzivatel().initUzivatelByUser(this.selectedUser);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // -------------------------
    // Prace s uzivateli
    // ------------------------    
    /**
     * Metoda vraci matici uzivatelu(<code>User</code>) pro tento
     * ucet(<code>Account</code>)
     *
     * @return matici uzivatelu nebo prazdnou matici
     */
    public ArrayList<User> getUsersForAccount() {
        if (userController instanceof UserController) {
            this.users = userController.getUsersForAccount(this.getUcet().getAccount());
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setIdaccount(this.getUcet().getAccount());
        user.setNewEntity(true);
        this.users.add(user);
        System.out.println(" getUsersForAccount():" + this.users.size());
        filteredUsers = new ArrayList(users);
        return this.users;
    }

    /**
     * Metoda znovu nacte matici Users pro definovany Account
     *
     * @return vraci vzdy null, aby se nezmenila stranka s tabulkou uzivatelu
     */
    public String refreshUsers() {
        getUsersForAccount();
        return null;
    }

    public void onRowSelect(SelectEvent event) {
        this.getUzivatel().setUser(this.selectedUser);
        clearFormUserDetail();
        this.getUzivatel().initUzivatelByUser(this.selectedUser);
    }

    public void onRowUnselect(UnselectEvent event) {
        this.getUzivatel().setUser(this.selectedUser);
        clearFormUserDetail();
        this.getUzivatel().initUzivatelByUser(this.selectedUser);
    }

    public String addUser() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formUserTable:tableUserTable");
        // dataTable.reset();
        this.selectedUser = users.get(users.size() - 1);
        HashSet<String> selectedRowKeys = new HashSet<>(1);
        selectedRowKeys.add(this.selectedUser.getId().toString());
        dataTable.setSelectedRowKeys(selectedRowKeys);
        onRowSelect(null);

//        for (User user : users) {
//            if (user.isNewEntity()) {
//                this.selectedUser = user;
//                this.uzivatel.setUser(selectedUser);
//                this.uzivatel.initUzivatelByUser(selectedUser);
//                break;
//            }
//        }
        return null;
    }

    public void delUser() {
        if (this.selectedUser != null && !this.selectedUser.isNewEntity()) {
            try {
                userController.destroy(this.selectedUser);
            } catch (Exception ex) {
                Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, ex);
                PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Vymazání uživatele: " + this.selectedUser.getFullname() + " z registru nebylo úspěšné.", "Po chvíli zkuste znovu."));
            }
        } else {
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage(this.selectedUser == null ? "Není vybrán žádný uživatel k vymazání." : "Nový neuložený uživatel se nedá vymazat.", "Vyberte správného uživatele k vymazání"));
        }
        getUsersForAccount();
    }

    /**
     * Muze uzivatel editovat uzivatele tj.ma pravo UZIVATEL_EDIT nebo
     * SUPERVISOR - pro smazani vybrany zaznam neni 'NewEntity'=true
     *
     * @param mod
     * @return true|false
     */
    public boolean disabledUser(String mod) {
        boolean lDisable = false;

        if (this.getUcet().getUzivatel() == null) {
            System.out.println(" this.ucet.getUzivatel(): ==null");
            lDisable = true;
        } else if (this.getUcet().getUzivatel().getUser() == null) {
            System.out.println(" this.ucet.getUzivatel(): ==null");
            lDisable = true;
        } else if (mod.equalsIgnoreCase("UserAdd")) {
            lDisable = !(this.ucet.getUzivatel().getParam(Uzivatel.USER_PARAM_NAME.UZIVATEL_EDIT, false) || this.ucet.getUzivatel().getParam(Uzivatel.USER_PARAM_NAME.SUPERVISOR, false));
        } else if (mod.equalsIgnoreCase("UserDel")) {
            lDisable = !(this.ucet.getUzivatel().getParam(Uzivatel.USER_PARAM_NAME.UZIVATEL_EDIT, false) || this.ucet.getUzivatel().getParam(Uzivatel.USER_PARAM_NAME.SUPERVISOR, false))
                    || this.selectedUser == null
                    || this.selectedUser.isNewEntity();
        } else if (mod.equalsIgnoreCase("UserEdit")) {
            lDisable = !(this.ucet.getUzivatel().getParam(Uzivatel.USER_PARAM_NAME.UZIVATEL_EDIT, false) || this.ucet.getUzivatel().getParam(Uzivatel.USER_PARAM_NAME.SUPERVISOR, false));
        } else if (mod.equalsIgnoreCase("UserSupervisor")) {
            lDisable = !(this.ucet.getUzivatel().getUser().getEmail() != null && this.ucet.getUzivatel().getUser().getEmail().equalsIgnoreCase("sosyn@seznam.cz"));
        }
        return lDisable;
    }

    public void clearFormUserDetail() {
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
//        ArrayList<String> resetIds = new ArrayList<>();
//        resetIds.add("formUserDetail");
//        root.resetValues(FacesContext.getCurrentInstance(), resetIds);
        UIComponent formUserDetail = root.findComponent("formUserDetail");
        // for JSF 2 getFacetsAndChildren instead of only JSF 1 getChildren
        Iterator<UIComponent> children = formUserDetail.getFacetsAndChildren();
        clearAllComponentInChilds(children);
    }

    private void clearAllComponentInChilds(Iterator<UIComponent> childrenIt) {
        while (childrenIt.hasNext()) {
            UIComponent component = childrenIt.next();
            //System.out.println("handling component " + component.getId());
            if (component instanceof InputText) {
                InputText com = (InputText) component;
                com.resetValue();
            }
            if (component instanceof DatePicker) {
                DatePicker com = (DatePicker) component;
                com.resetValue();
            }
            if (component instanceof ToggleSwitch) {
                ToggleSwitch com = (ToggleSwitch) component;
                com.resetValue();
            }
//            if (component instanceof OutputLabel) {
//                OutputLabel com = (OutputLabel) component;
//                com.resetValue();
//            }
            clearAllComponentInChilds(component.getFacetsAndChildren());
        }

    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        return true;
    }

    public boolean dateFilter(Object value, Object filter, Locale locale) {
        System.out.println(" dateFilter(Object value, Object filter, Locale locale):" + value + "," + filter + "," + locale);
        boolean lOk = true;

        if (value == null || filter == null) {
            lOk = true;
        } else if (filter instanceof Date) {
            lOk = ((Date) value).equals(filter);
        } else if (filter instanceof String) {
            String datums = ((String) filter).replaceAll(" ", "").replaceAll(",", ".");
            if (datums.length() == 0) {
                lOk = true;
            } else if (datums.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}[-_>;]\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}")) {
                String[] splitOdDo = datums.split("[-_>;]");
                try {
                    Date odData = Aplikace.getSimpleDateFormat().parse(splitOdDo[0]);
                    Date doData = Aplikace.getSimpleDateFormat().parse(splitOdDo[1]);
                    if (splitOdDo.length == 1) {
                        lOk = (((Date) value).equals(odData) || ((Date) value).after(odData));
                    }
                    if (splitOdDo.length == 2) {
                        lOk = (((Date) value).equals(odData) || ((Date) value).after(odData)) && (((Date) value).equals(doData) || ((Date) value).before(doData));
                    }

                } catch (ParseException ex) {
                    Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            } else if (datums.matches("\\d{1,2}/\\d{2,4}[-_>;]\\d{1,2}/\\d{2,4}")) {
                String[] splitOdDo = datums.split("[-_>;]");
                Calendar calOd = Calendar.getInstance(Aplikace.getLocaleCZ());
                Calendar calDo = Calendar.getInstance(Aplikace.getLocaleCZ());
                calOd.set(Calendar.HOUR_OF_DAY, 0);
                calOd.set(Calendar.MINUTE, 0);
                calOd.set(Calendar.SECOND, 0);
                calOd.set(Calendar.MILLISECOND, 0);
                calOd.set(Calendar.DAY_OF_MONTH, 1);
                calOd.set(Calendar.MONTH, Integer.parseInt(splitOdDo[0].split("/")[0]) - 1);
                calOd.set(Calendar.YEAR, Integer.parseInt(splitOdDo[0].split("/")[1]));
                
                calDo.set(Calendar.HOUR_OF_DAY, 0);
                calDo.set(Calendar.MINUTE, 0);
                calDo.set(Calendar.SECOND, 0);
                calDo.set(Calendar.MILLISECOND, 0);
                calDo.set(Calendar.DAY_OF_MONTH, 1);
                calDo.set(Calendar.MONTH, Integer.parseInt(splitOdDo[1].split("/")[0]));
                calDo.set(Calendar.YEAR, Integer.parseInt(splitOdDo[1].split("/")[1]));
                calDo.add(Calendar.HOUR_OF_DAY, -1);
                Date odData = calOd.getTime();
                Date doData = calDo.getTime();
                lOk = (((Date) value).equals(odData) || ((Date) value).after(odData)) && (((Date) value).equals(doData) || ((Date) value).before(doData));

            } else if (datums.length() <= 10) {
                String[] datumA = datums.split("\\.");
                int[] datumI = new int[]{0, 0, 0};
                for (int i = 0; i < Math.min(3, datumA.length); i++) {
                    datumI[i] = Integer.parseInt(datumA[i]);
                }
                Calendar cal = Calendar.getInstance(Aplikace.getLocaleCZ());
                cal.setTime(((Date) value));
                if (datumI[0] > 0) {
                    lOk &= cal.get(Calendar.DAY_OF_MONTH) == datumI[0];
                }
                if (datumI[1] > 0) {
                    lOk &= cal.get(Calendar.MONTH) == datumI[1] - 1;
                }
                if (datumI[2] > 0) {
                    lOk &= cal.get(Calendar.YEAR) == datumI[2];
                }
            }
        } else if (filter instanceof ArrayList) {
            try {
                Date odData = Aplikace.getSimpleDateFormat().parse(((LocalDate) ((ArrayList) filter).get(0)).format(DateTimeFormatter.ofPattern("dd.MM.uuuu", Aplikace.getLocaleCZ())));
                Date doData = Aplikace.getSimpleDateFormat().parse(((LocalDate) ((ArrayList) filter).get(1)).format(DateTimeFormatter.ofPattern("dd.MM.uuuu", Aplikace.getLocaleCZ())));

                lOk = (((Date) value).equals(odData) || ((Date) value).after(odData)) && (((Date) value).equals(doData) || ((Date) value).before(doData));

            } catch (ParseException ex) {
                Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return lOk;
    }

    /**
     * Metoda predplni "passwodHelp" do formulare a zavola dynamicky formular
     * pro editaci hesla
     */
    public void editPassword() {
        password.editPassword("", "", this.selectedUser.getPasswordhelp());
    }

    /**
     * Getter and Setter methods
     */
    /**
     * @return the uzivatel
     */
    public Uzivatel getUzivatel() {
        return uzivatel;
    }

    /**
     * @param uzivatel the uzivatel to set
     */
    public void setUzivatel(Uzivatel uzivatel) {
        this.uzivatel = uzivatel;
    }

    /**
     * @return the selectedUser
     */
    public User getSelectedUser() {
        return selectedUser;
    }

    /**
     * @param selectedUser the selectedUser to set
     */
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * @return the users
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    /**
     * @return the ucet
     */
    public Ucet getUcet() {
        return ucet;
    }

    /**
     * @param ucet the ucet to set
     */
    public void setUcet(Ucet ucet) {
        this.ucet = ucet;
    }

    /**
     * @return the filteredUsers
     */
    public ArrayList<User> getFilteredUsers() {
        return filteredUsers;
    }

    /**
     * @param filteredUsers the filteredUsers to set
     */
    public void setFilteredUsers(ArrayList<User> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }
}
