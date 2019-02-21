package ru.heroicrealm.scormbuilder.service.impl;


import ru.heroicrealm.scormbuilder.entities.CatalogEntry;
import ru.heroicrealm.scormbuilder.exceptions.FolderNotFoundException;
import ru.heroicrealm.scormbuilder.service.ICatalogService;
import ru.heroicrealm.scormbuilder.service.IPackageService;
import ru.heroicrealm.scormbuilder.service.IPresentationService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kuran on 16.01.2019.
 */
@Stateless
public class CatalogServiceImpl implements ICatalogService {
    @EJB
    IPresentationService presentationService;

    @EJB
    IPackageService packageService;

    @PersistenceContext(unitName="primary")
    private EntityManager entityManager;

    @Override
    public CatalogEntry createFolder(long parentId, String name, String author) throws FolderNotFoundException {
        CatalogEntry root = entityManager.find(CatalogEntry.class, parentId);
        if (root==null){
            throw new FolderNotFoundException(parentId);
        }
        CatalogEntry ce = new CatalogEntry();
        ce.setName(name);
        ce.setAuthor(author);
        ce.setParent(root);
        ce.setCreatedDate(new Date());
        ce.setOtype(CatalogEntry.FOLDER);
        root.getChildren().add(ce);
        entityManager.persist(root);
        return ce;
    }

    @Override
    public List<CatalogEntry> list(long parentId) throws FolderNotFoundException {
        CatalogEntry root = entityManager.find(CatalogEntry.class, parentId);
        if (root==null){
            throw new FolderNotFoundException(parentId);
        }
        LinkedList<CatalogEntry> res = new LinkedList<>();
        for (CatalogEntry ce:root.getChildren()) {
            res.add(ce);
        }
        return res;
    }

    @Override
    public CatalogEntry createPresentation(long folderId, String presentationName, String user) {
        CatalogEntry ce = insertCatalogEntry(CatalogEntry.PRESENTATION, folderId, presentationName, user);
        presentationService.createPresentation(ce.getId(),presentationName,user);

        return ce;
    }

    @Override
    public void save(CatalogEntry ce) {
        entityManager.merge(ce);
    }

    @Override
    public CatalogEntry load(long cid) {
        return entityManager.find(CatalogEntry.class,cid);
    }

    @Override
    public CatalogEntry createPackage(long folderId, String packageName, String user) {
        CatalogEntry ce = insertCatalogEntry(CatalogEntry.PACKAGE,folderId, packageName, user);
        packageService.createPackage(ce.getId(),packageName,user);
        return ce;
    }

    @Override
    public boolean hasChildren(CatalogEntry catalogEntry) {
        TypedQuery<CatalogEntry> query = entityManager.createQuery("from CatalogEntry where parent.id=:p", CatalogEntry.class);
        query.setParameter("p",catalogEntry.getId());
        List<CatalogEntry> resultList = query.getResultList();
        return !resultList.isEmpty();
    }

    private CatalogEntry insertCatalogEntry(int type,long folderId, String presentationName, String user) {
        CatalogEntry root = entityManager.find(CatalogEntry.class,folderId);
        CatalogEntry ce = new CatalogEntry();
        ce.setName(presentationName);
        ce.setAuthor(user);
        ce.setOtype(type);
        ce.setParent(root);
        ce.setCreatedDate(new Date());
        entityManager.persist(ce);
        return ce;
    }


}
