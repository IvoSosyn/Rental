/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.admin.model;

import cz.rental.aplikace.Aplikace;
import cz.rental.entity.Typentity;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author sosyn
 */
@Named("modelTree")
@Stateless
public class ModelTree {

    private TreeNode root = null;
    private TreeNode selectedNode = null;

    @PersistenceContext(unitName = "PostgreSQLNajem")
    private EntityManager em;
    Query query = null;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void init() {
        getTreeNodes();
    }

    private void getTreeNodes() {
        root = new DefaultTreeNode("Root", null);
        this.query = em.createQuery("SELECT t FROM Typentity t WHERE t.idparent IS NULL");
        List<Typentity> list = query.getResultList();
        if (!list.isEmpty()) {
            for (Typentity typEntity : list) {
                addNode(root, typEntity);
            }
        } else {
        }
        this.em.close();
    }

    private void addNode(TreeNode parent, Typentity typEntityParent) {
        // Zmenim obsah cyklickeho dotazu
        this.query = this.em.createQuery("SELECT t FROM Typentity t WHERE t.idparent= :idParent AND (t.platiod IS NULL OR t.platiod <= :PlatiDO) AND (t.platido IS NULL OR t.platido >= :PlatiOD)");
        this.query.setParameter("idParent", typEntityParent.getId());
        query.setParameter("PlatiOD", Aplikace.getPlatiOd());
        query.setParameter("PlatiDO", Aplikace.getPlatiDo());
        List list = this.query.getResultList();
        if (!list.isEmpty()) {
            for (Object typEntity : list) {
                TreeNode top = new DefaultTreeNode(typEntity, parent);
                addNode(top, (Typentity) typEntity);
            }
        }
    }

    /**
     * @return the root
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(TreeNode root) {
        this.root = root;
    }

    /**
     * @return the selectedNode
     */
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    /**
     * @param selectedNode the selectedNode to set
     */
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
}
