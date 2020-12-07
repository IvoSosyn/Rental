/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.aplikace.Ucet;
import cz.rental.utils.Aplikace;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import static javax.ejb.TransactionManagementType.CONTAINER;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author sosyn
 */
@Stateless
@TransactionManagement(CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class TypentityController extends JpaController {

    @EJB
    AttributeController attrControler;

    @Inject
    Ucet ucet;

    private Query query = null;

    @PostConstruct
    public void init() {
//        try {
//            ucet = (Ucet) InitialContext.doLookup("java:module/Account!cz.rental.aplikace.registrace.Account");
//            user = (Uzivatel) InitialContext.doLookup("java:module/User!cz.rental.aplikace.User");
//        } catch (NamingException ex) {
//            Logger.getLogger(Ucet.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /**
     * Najde nejvyssi-"root" prvek (Typentity.idparent==null) celeho stromu
     * zavislosti Typentity
     *
     * @return pole s prave 1 nejvyssim "root" prvkem
     */
    public ArrayList<Typentity> getRootTypEntity() {
        this.setQuery(this.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent IS NULL"));
        ArrayList<Typentity> list = new ArrayList(this.getQuery().getResultList());
        return list;
    }

    /**
     * Najde vsechny potomky katalogu tj. vsechny modely(sablony)
     *
     * @return
     */
    public ArrayList<Typentity> getCatalogTypEntity() {
        Typentity modelTypentity;
        this.setQuery(this.getEm().createQuery("SELECT t FROM Typentity t WHERE t.typentity='CATALOG'"));
        ArrayList<Typentity> list = new ArrayList(this.getQuery().getResultList());
        if (!list.isEmpty()) {
            modelTypentity = list.get(0);
            this.setQuery(this.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent=:idParent "));
            this.getQuery().setParameter("idParent", modelTypentity.getId());
            list = new ArrayList(this.getQuery().getResultList());
        }
        return list;
    }

    /**
     * Metoda vygeneruje "Tree" zavisloti a naplni jednotlive "TreeNode" datovou
     * hodnotou "Typentity", provaze je mezi sebou=parent<>child na zaklade
     * definice z DB
     *
     * @param typEntityRoot - nejvyssi vybrany Typentity, od ktereho se zacne
     * plnit "Tree", nemusi to byt nutne "Root", muze to zacit z kterekoliv
     * vetve
     * @return strom slozeny z "TreeNode" s hodnotami "TypEntity"
     */
    public TreeNode fillTreeNodes(Typentity typEntityRoot) {
        TreeNode root = new DefaultTreeNode("Root", null);
        this.setQuery(this.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent= :idParent"));
        this.getQuery().setParameter("idParent", typEntityRoot.getId());
        List<Typentity> list = this.getQuery().getResultList();
        if (!list.isEmpty()) {
            for (Typentity typEntity : list) {
                TreeNode top = new DefaultTreeNode(typEntity, root);
                addNode(top, typEntity);
            }
        } else {
        }
        // WildFly si dela management spojeni sam - vyhodi chybu
        // this.em.close();
        return root;
    }

    private void addNode(TreeNode parent, Typentity typEntityParent) {
        // Zmenim obsah cyklickeho dotazu
        this.setQuery(this.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent= :idParent AND (t.platiod IS NULL OR t.platiod <= :PlatiDO) AND (t.platido IS NULL OR t.platido >= :PlatiOD)"));
        this.getQuery().setParameter("idParent", typEntityParent.getId());
        this.getQuery().setParameter("PlatiOD", Aplikace.getPlatiOd());
        this.getQuery().setParameter("PlatiDO", Aplikace.getPlatiDo());
        List list = this.getQuery().getResultList();
        if (!list.isEmpty()) {
            for (Object typEntity : list) {
                TreeNode top = new DefaultTreeNode(typEntity, parent);
                addNode(top, (Typentity) typEntity);
            }
        }
    }

    public Typentity cloneTypentity(Typentity typentity) throws CloneNotSupportedException {
        Typentity typentityNew = (Typentity) typentity.clone();
        typentityNew.setId(UUID.randomUUID());
        typentityNew.setNewEntity(true);
        return typentityNew;
    }

    /**
     * Metoda najde zaznam v DB odpovidajici Typentity.id == id
     *
     * @param id ID hledaneho zazamu Typentity.id
     * @return nalezeny zanam Typentity nebo null
     */
    public Typentity getTypentity(UUID id) {
        Typentity typentity = null;
        this.setQuery(this.getEm().createQuery("SELECT t FROM Typentity t WHERE t.id= :id"));
        this.getQuery().setParameter("id", id);
        try {
            typentity = (Typentity) this.getQuery().getSingleResult();
        } catch (Exception e) {
            System.out.println(" TypentityContoller.getTypentity#e: " + e.getMessage());
        }
        return typentity;
    }

    /**
     * Metoda najde zaznam v DB odpovidajici Typentity.id == id
     *
     * @param id ID hledaneho zazamu Typentity.id
     * @return nalezeny zanam Typentity nebo null
     */
    public Typentity getTypentityForParentID(UUID id) {
        Typentity typentity = null;
        this.setQuery(this.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent= :id"));
        this.getQuery().setParameter("id", id);
        ArrayList<Typentity> typentities = new ArrayList<>(this.getQuery().getResultList());
        for (Typentity tp : typentities) {
            typentity = tp;
            break;
        }
        return typentity;
    }

    /**
     * Metoda vrati pole potomku(childs) pro parametr Typentity
     *
     * @param typentity
     * @return
     */
    public ArrayList<Typentity> getTypentityChilds(Typentity typentity) {
        this.setQuery(this.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent= :id"));
        this.getQuery().setParameter("id", typentity.getId());
        return new ArrayList<>(this.getQuery().getResultList());
    }

    /**
     * Metoda vytvori novy Tree pro konkretni zadane ID vlastnika (bude dosazeno
     * do Typentity.id) jako 'root' ID modelu do Typentity.idmodel bude dosazeno
     * 'model.id', aby se pozdeji mohlo testovat, z ceho to vzniklo a pripadne
     * provest UpDate
     *
     * @param source - Typentity modelu(sablony) od ktereho se zacne kopie smerem
     * dolu po vetvich
     * @param idAccount - ID uctu-vlastnika Treee
     * @throws CloneNotSupportedException
     * @throws Exception
     */
    public void copyTypentity(Typentity source, UUID idAccount) throws CloneNotSupportedException, Exception {
        if (source == null) {
            throw new Exception("Vzorový model(šablona) pro vytvoření modelu(šablony) účtu je prázdná.");
        }
        if (idAccount == null) {
            throw new Exception("Identifikátor účtu je prázdný, nelze pro něj vytvořit model(šablonu).");
        }
        Typentity target = (Typentity) source.clone();
        target.setId(idAccount);
        target.setIdmodel(source.getId());
        target.setTypentity("Ucet");
        target.setPopis("Model for account ID: " + idAccount.toString());
        if (!(this.findEntita(target) instanceof Typentity)) {
            this.create(target);
        } else {
            this.edit(target);
        }
        attrControler.copyAttr(source, target);
        copyTypentityChilds(source, idAccount);
    }

    /**
     * Pruchod vsemi polozkami 'children' pro Typentity.idparent a
     * zalozeni(aktualizaci) jejich kopii pro nove idParent
     *
     * @param source - polozka modelu(sablony)
     * @param idParent - id rodice, ktere bude dosazeno do noveho zaznamu
     */
    private void copyTypentityChilds(Typentity source, UUID idParent) throws CloneNotSupportedException, Exception {
        Typentity childrenNew;
        ArrayList<Typentity> childs = getTypentityChilds(source);
        for (Typentity children : childs) {
            this.setQuery(this.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent = :idParent AND t.idmodel = :idModel "));
            this.getQuery().setParameter("idParent", idParent);
            this.getQuery().setParameter("idModel", source.getId());
            try {
                childrenNew = (Typentity) this.getQuery().getSingleResult();
            } catch (NoResultException nEx) {
                childrenNew = (Typentity) children.clone();
                childrenNew.setId(UUID.randomUUID());
                childrenNew.setIdparent(idParent);
                childrenNew.setIdmodel(source.getId());
            }
            if (!(this.findEntita(childrenNew) instanceof Typentity)) {
                this.create(childrenNew);
            } else {
                this.edit(childrenNew);
            }
            attrControler.copyAttr(children, childrenNew);
            copyTypentityChilds(children, childrenNew.getId());
        }
    }
}
