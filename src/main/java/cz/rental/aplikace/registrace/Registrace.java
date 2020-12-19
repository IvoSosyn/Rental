/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.aplikace.registrace;

import cz.rental.aplikace.Ucet;
import cz.rental.aplikace.Uzivatel;
import cz.rental.entity.Typentity;
import cz.rental.utils.Aplikace;
import cz.rental.utils.SHA512;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.PrimeFacesContext;

/**
 *
 * @author ivo
 */
@Named(value = "registrace")
@SessionScoped
public class Registrace implements Serializable {

    static final long serialVersionUID = 42L;

    @EJB
    cz.rental.entity.TypentityController typentityController;

    @Inject
    private Ucet ucet;

    private static final String XHTML_REGISTRACE_FILE = "/aplikace/registrace/regStep";
    private int selectedStep = 0;

    private String accountsRootDir = Ucet.ACCOUNT_ROOT_DIR;
    private String accountDir = null;

    private ArrayList<Typentity> models = new ArrayList<>(10);
//    private Typentity selectedModel = null;

    @PostConstruct
    public void init() {
//        try {
//            uzivatel = (Uzivatel) InitialContext.doLookup("java:module/User!cz.rental.aplikace.Uzivatel");
//        } catch (NamingException ex) {
//            Logger.getLogger(Ucet.class.getName()).log(Level.SEVERE, null, ex);
//        }
        // Nacist root dir pro soubory uctu, pokud je zadan ve WEB.XML  napr. na Linuxu
        //  <context-param>
        //      <param-name>cz.rental.accounts.root.dir</param-name>
        //      <param-value>/home/Rental/Accounts</param-value>
        //  </context-param>
        if (FacesContext.getCurrentInstance().getExternalContext().getInitParameter("cz.rental.accounts.root.dir") != null) {
            accountsRootDir = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("cz.rental.accounts.root.dir");
        }
        accountDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/account");
        //this.setCustomerPasswordSHA512(SHA512.getSHA512(""));
//        Set<String> setRes=FacesContext.getCurrentInstance().getExternalContext().getResourcePaths("/account");
//        System.out.println(" Ucet.accountDir="+accountDir);
//        for (String setRe : setRes) {
//            System.out.println(" /account: resources="+setRe);
//            
//        }
        this.ucet.getAccount().setId(UUID.randomUUID());
        this.ucet.getAccount().setPin(9020);
        this.ucet.getAccount().setFullname("Ing.Ivo Sosýn");
        this.ucet.getAccount().setEmail("sosyn@seznam.cz");
        this.ucet.getAccount().setStreet1("Fűgnerova 51/14");
        this.ucet.getAccount().setStreet2("Pod Cvilínem");
        this.ucet.getAccount().setCity("Krnov");
        this.ucet.getAccount().setPostcode("794 01");
        this.ucet.getAccount().setTelnumber("736667337");
        this.ucet.getAccount().setPasswordsha512(SHA512.getSHA512("daniela"));
        this.ucet.getAccount().setPasswordhelp("d.a.n.i.e.l.a.");
        this.ucet.getAccount().setPlatiod(Aplikace.getPlatiOd());
        this.ucet.getAccount().setPlatido(Aplikace.getPlatiDo());
        // Vygenerovat novy PIN
        // TO-OD: pozor na praci v siti, melo by to nasledovat pred ulozenim do DB
        this.ucet.changePin(null);
        // Sablona - model
//        Typentity typentity = new Typentity();
//        typentity.setId(UUID.fromString("cac1b920-6b4f-4d2c-8308-86fc3fef5ec3"));
//        this.ucet.getAccount().setIdmodel((Typentity) this.ucet.getAccController().findEntita(typentity));
        // Nacist vsechny sablony, ktere jsou k dispozici ro vyber
        this.readModels();
        // Novy uzivatel - stejny jako registrator
        this.ucet.getUzivatel().getUser().setIdaccount(this.ucet.getAccount());
        this.ucet.getUzivatel().getUser().setFullname(this.ucet.getAccount().getFullname());
        this.ucet.getUzivatel().getUser().setEmail(this.ucet.getAccount().getEmail());
        this.ucet.getUzivatel().getUser().setPasswordsha512(this.ucet.getAccount().getPasswordsha512());
        this.ucet.getUzivatel().getUser().setPasswordhelp(this.ucet.getAccount().getPasswordhelp());
        this.ucet.getUzivatel().getUser().setTelnumber(this.ucet.getAccount().getTelnumber());
    }

    /**
     * Metoda rozhoduje, zda-li je nebo neni mozne vybrat "<p:MenuItem>" ze
     * "<p:Step>" menu POZOR - rozhoduje o tom samostatne "<p:Step>" menu
     *
     * @param seletedStep hodnoceny "<p:MenuItem>" ze "<p:Step>" menu
     * registracniho formulare
     * @return vzdy "true", protoze o tom rozhoduje samostatne "<p:Step>" menu
     */
    public boolean isRegistracniPanelEnable(int seletedStep) {
        boolean isEnable = true;
        return isEnable;
    }

    /**
     * Metoda nastavi-prepne na panel z parametru <code>seletedStep</code>
     *
     * @param seletedStep vybrany "<p:MenuItem>" ze "<p:Step>" menu
     * registracniho formulare
     */
    public void changeRegistracniPanel(int seletedStep) {
        this.setSelectedStep(seletedStep);
    }

    public String includeRegPanel() {
        return XHTML_REGISTRACE_FILE + getSelectedStep() + ".xhtml";
    }

    public boolean isBackEnable() {
        boolean isEnable = this.getSelectedStep() > 0 && !FacesContext.getCurrentInstance().isValidationFailed();
        return isEnable;
    }

    public boolean isBackRendered() {
        boolean isEnable = this.getSelectedStep() > 0;
        return isEnable;
    }

    public void backRegPanel() {
        boolean nextRegPanel = true;
        if (nextRegPanel && this.getSelectedStep() > 0) {
            this.setSelectedStep(this.getSelectedStep() - 1);
        }
    }

    public boolean isNextEnable() {
        boolean isEnable = true && !FacesContext.getCurrentInstance().isValidationFailed() && !this.ucet.getAccount().getEmail().isEmpty();
        return isEnable;
    }

    public boolean isNextRendered() {
        boolean isEnable = this.getSelectedStep() < 3;
        return isEnable;
    }

    public void nextRegPanel() {
        boolean nextRegPanel = true;
        if (nextRegPanel && this.getSelectedStep() < 3) {
            this.setSelectedStep(this.getSelectedStep() + 1);
        }
    }

    public Boolean isEditable() {
        boolean isEditable = this.ucet.getUzivatel().getParam(Uzivatel.SUPERVISOR, false) || this.ucet.getUzivatel().getParam(Uzivatel.ACCOUNT_EDIT, false);
        UIComponent uic = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        return isEditable;
    }

    public boolean isButtonEnable() {
        return !FacesContext.getCurrentInstance().isValidationFailed();
    }

    /**
     * Metoda nacte vsechny modely(sablony) kde je Typentity.Idparen=null a
     * nastavi vybrany Typentity 1. '0'-tou vetu v poli Models, pokud jeste neni
     * zalozen v Accout 2. Typentity, ktery je vybran v Account
     *
     */
    private void readModels() {
        this.models = typentityController.getRootTypEntity();
        if (this.ucet.getAccount().getIdmodel() instanceof Typentity) {
//            this.selectedModel = (Typentity) this.typentityController.findEntita(this.ucet.getAccount().getIdmodel());
        } else if (!this.models.isEmpty()) {
//            this.selectedModel = this.models.get(0);
            this.ucet.getAccount().setIdmodel(this.models.get(0));
        }
    }

    public void changeModel(ValueChangeEvent valueChangeEvents) {
//        this.selectedModel = (Typentity) valueChangeEvents.getNewValue();
//        this.ucet.getAccount().setIdmodel(selectedModel);
        //PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage(" Není dosud implementováno.","Není dosud implementováno."));
    }

    /**
     * Metoda vraci kratkou infomaci o naplneni Account.passwordsha512
     *
     * @return hlaseni
     */
    public String passwordMessage() {
        if (this.getUcet().getAccount().getPasswordsha512() instanceof String && !this.getUcet().getAccount().getPasswordsha512().isEmpty()) {
            if (this.getUcet().getAccount().getPasswordhelp() instanceof String && !this.getUcet().getAccount().getPasswordhelp().isEmpty()) {
                return "Nápověda hesla: " + this.getUcet().getAccount().getPasswordhelp().trim();
            } else {
                return "Máte uložené heslo.";
            }
        }
        return "Heslo NENÍ definované";
    }

    /**
     * Metoda zalozi ucet a pro nej model (sablonu) a pracovni adresare
     *
     * @return webova stranka nasledujici po uspesne nebo neuspesne registraci
     */
    public String createAccount() {
        boolean isOk = true;
        // Uloz ucet do DB
        try {
//            this.ucet.getAccount().setIdmodel(this.selectedModel);
            this.ucet.saveAccount();
        } catch (Exception ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při ukládání účtu. Opakujte později.", ex.getMessage()));
            isOk = false;
        }
        try {
            // Novy uzivatel do DB - stejny jako registrator uctu
            this.ucet.getUzivatel().getUser().setIdaccount(this.ucet.getAccount());
            this.ucet.getUzivatel().getUser().setFullname(this.ucet.getAccount().getFullname());
            this.ucet.getUzivatel().getUser().setEmail(this.ucet.getAccount().getEmail());
            this.ucet.getUzivatel().getUser().setPasswordsha512(this.ucet.getAccount().getPasswordsha512());
            this.ucet.getUzivatel().getUser().setPasswordhelp(this.ucet.getAccount().getPasswordhelp());
            this.ucet.getUzivatel().getUser().setTelnumber(this.ucet.getAccount().getTelnumber());
            this.ucet.getUzivatel().saveUser();
        } catch (Exception ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při aktualizaci záznamu o uživateli. Opakujte později.", ex.getMessage()));
            isOk = false;
        }
        // Kopie modelu(sablony)
        try {
            this.typentityController.copyTypentity(this.ucet.getAccount().getIdmodel(), this.ucet.getAccount());
        } catch (Exception ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při zakládání modelu(šablony) účtu. Opakujte později.", ex.getMessage()));
            isOk = false;
        }
        // Zaloz adresare k uctu a vstupni 'index.xhtml'
        try {
            this.createAccountDir();
        } catch (Exception ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při zakládání souborů a adresářů k účtu. Opakujte později.", ex.getMessage()));
            isOk = false;
        }
        // Napln XHTML soubory daty ze sablony
        try {
            this.createAccountHTML();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Registrace.class.getName()).log(Level.SEVERE, null, ex);
            PrimeFacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Chyba při plnění a konfiguraci souborů k účtu. Opakujte později.", ex.getMessage()));
            isOk = false;
        }
        if (isOk) {
            return "/aplikace/evidence/evidence.xhtml";
        } else {
            // Pri neuspechu vratit na zcatek registrace
            return "/aplikace/registrace/registrace.xhtml";
        }
    }

    /**
     * Vytvori sadu adresaru pro ucet v preddefinovanem ulozisti a hlavni soubor
     * uctu 'index.xhtml'
     *
     * @throws IOException
     * @throws Exception
     */
    public void createAccountDir() throws IOException, Exception {
        // Konstrukce cety k uctu uzivatele
        File rootFile = new File(getRootAccountDirFor((String) null));
        if (!rootFile.exists() && !rootFile.mkdirs()) {
            throw new SQLException("Založení adresáře: '" + accountsRootDir + "' NEBYLO úspěšné, vytvořte ručně."
            );
        }
        // Vstupni soubor uctu
        rootFile = new File(getRootAccountDirFor("index.xhtml"));
        if (!rootFile.exists() && !rootFile.createNewFile()) {
            throw new SQLException("Založení vstupního souboru: '" + getRootAccountDirFor("index.xhtml") + "' NEBYLO úspěšné, vytvořte ručně.");
        }
        // Data uctu
        rootFile = new File(getRootAccountDirFor("data"));
        if (!rootFile.exists() && !rootFile.mkdirs()) {
            throw new SQLException("Založení adresáře: '" + getRootAccountDirFor("data") + "' NEBYLO úspěšné, vytvořte ručně.");
        }
        rootFile = new File(getRootAccountDirFor("data", "data1", null, "data3"));
        if (!rootFile.exists() && !rootFile.mkdirs()) {
            throw new SQLException("Založení adresáře: '" + getRootAccountDirFor("data") + "' NEBYLO úspěšné, vytvořte ručně.");
        }
    }

    /**
     * Naplni hlavni soubor uctu 'index.xhtml' daty TO-DO: Melo by to jit do
     * samostatneho souboru s osetrenim, co bylo vytvoreno a v jake verzi
     *
     * @throws FileNotFoundException
     */
    public void createAccountHTML() throws FileNotFoundException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(getRootAccountDirFor("index.xhtml"));
            pw.println("");
            pw.println("<?xml version='1.0' encoding='UTF-8' ?>");
            pw.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            pw.println("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
            pw.println("xmlns:ui=\"http://xmlns.jcp.org/jsf/facelets\"");
            pw.println("xmlns:h=\"http://xmlns.jcp.org/jsf/html\"");
            pw.println("xmlns:f=\"http://xmlns.jcp.org/jsf/core\"");
            pw.println("xmlns:p=\"http://primefaces.org/ui\">");
//            pw.println("<ui:composition>");
            pw.println("<h:form  id=\"idCustomer\" >");
            pw.println("<p:panelGrid columns=\"3\" >");
            pw.println("<f:facet name=\"header\">");
            pw.println("<p>Účet ID=" + this.ucet.getAccount().getId() + "</p>");
            pw.println("</f:facet>");
            pw.println("<div> Konfigurace dat účtu </div>");
            pw.println("</p:panelGrid>");
            pw.println("</h:form>");
//            pw.println("</ui:composition>	");
            pw.println("</html>");

        } catch (FileNotFoundException ex) {
            if (pw != null) {
                pw.close();
            }
            throw ex;
        }
        if (pw != null) {
            pw.close();
        }
    }

    /**
     * Metoda vytvori nazev adresare a podaresare pozadovaneho uctu napr.:
     * "\Rental\Ucet\[UUID]\data"; "\Rental\Ucet\[UUID]\index.xhtml"
     *
     * @param fileNames - matice nazvu podadresarui a souboru, ktere metoda
     * prevede na retezec s oddelovacem <code>Files.separator</code>
     * @return
     */
    public String getRootAccountDirFor(String... fileNames) {
        StringBuilder sb = new StringBuilder(accountsRootDir).append(File.separator).append(this.ucet.getAccount().getId());
        if (fileNames != null && fileNames.length > 0) {
            for (String fn : fileNames) {
                if (fn != null) {
                    sb.append(File.separator).append(fn.trim());
                }
            }
        }
        return sb.toString();
    }

    /**
     * @return the selectedStep
     */
    public int getSelectedStep() {
        return selectedStep;
    }

    /**
     * @param selectedStep the selectedStep to set
     */
    public void setSelectedStep(int selectedStep) {
        this.selectedStep = selectedStep;
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
     * @return the models
     */
    public ArrayList<Typentity> getModels() {
        return models;
    }

    /**
     * @param models the models to set
     */
    public void setModels(ArrayList<Typentity> models) {
        this.models = models;
    }

//    /**
//     * @return the selectedModel
//     */
//    public Typentity getSelectedModel() {
//        return selectedModel;
//    }
//
//    /**
//     * @param selectedModel the selectedModel to set
//     */
//    public void setSelectedModel(Typentity selectedModel) {
//        this.selectedModel = selectedModel;
//    }
}
