/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.aplikace.User;
import cz.rental.aplikace.Ucet;
import cz.rental.utils.Aplikace;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import static javax.ejb.TransactionManagementType.CONTAINER;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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

    private Query query = null;

    Ucet account;
    User user;

    @PostConstruct
    public void init() {
        try {
            account = (Ucet) InitialContext.doLookup("java:module/Account!cz.rental.aplikace.registrace.Account");
            user = (User) InitialContext.doLookup("java:module/User!cz.rental.aplikace.User");
        } catch (NamingException ex) {
            Logger.getLogger(Ucet.class.getName()).log(Level.SEVERE, null, ex);
        }
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

}
