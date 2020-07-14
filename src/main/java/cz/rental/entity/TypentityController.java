/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.aplikace.Aplikace;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author sosyn
 */
@Stateless
public class TypentityController {

    @PersistenceContext(unitName = "PostgreSQLNajem")
    private EntityManager em;
    private Query query = null;

    public TreeNode fillTreeNodes() {
        TreeNode root = new DefaultTreeNode("Root", null);
        this.query = em.createQuery("SELECT t FROM Typentity t WHERE t.idparent IS NULL");
        List<Typentity> list = query.getResultList();
        if (!list.isEmpty()) {
            for (Typentity typEntity : list) {
                addNode(root, typEntity);
            }
        } else {
        }
        // WildFly si dela management spojeni sam - vyhodi chybu
        // this.em.close();
        return root;
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
     * @return the em
     */
    public EntityManager getEm() {
        return em;
    }

    /**
     * @param em the em to set
     */
    public void setEm(EntityManager em) {
        this.em = em;
    }

}
