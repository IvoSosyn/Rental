/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.rental.entity;

import cz.rental.aplikace.Aplikace;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import static javax.ejb.TransactionManagementType.CONTAINER;
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
@TransactionManagement(CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class TypentityController extends JpaController {

    @PersistenceContext(unitName = "PostgreSQLNajem")
    private EntityManager em;
    private Query query = null;

    public TreeNode fillTreeNodes() {
        TreeNode root = new DefaultTreeNode("Root", null);
        this.setQuery(this.getEm().createQuery("SELECT t FROM Typentity t WHERE t.idparent IS NULL"));
        List<Typentity> list = this.getQuery().getResultList();
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
