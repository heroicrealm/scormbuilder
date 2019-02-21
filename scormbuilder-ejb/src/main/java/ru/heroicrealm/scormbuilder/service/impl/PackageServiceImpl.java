package ru.heroicrealm.scormbuilder.service.impl;

import ru.heroicrealm.scormbuilder.entities.ScormPackage;
import ru.heroicrealm.scormbuilder.entities.ScormRef;
import ru.heroicrealm.scormbuilder.service.IPackageService;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by kuran on 26.01.2019.
 */
@Stateless
public class PackageServiceImpl implements IPackageService{
    @PersistenceContext(unitName="primary")
    private EntityManager entityManager;
    @Override
    public void createPackage(long id, String packageName, String user) {
        ScormPackage sp = new ScormPackage();
        sp.setId(id);
        entityManager.persist(sp);
    }

    @Override
    public ScormPackage load(long packageId) {

        return entityManager.find(ScormPackage.class,packageId);
    }

    @Override
    public void deleteRefs(List<ScormRef> deletedRefs) {
        deletedRefs.stream().forEach(ref -> entityManager.remove(entityManager.merge(ref)));
    }

    @Override
    public void save(ScormPackage pack) {
        pack.getComponents().stream().forEach(ref -> entityManager.merge(ref));
     entityManager.merge(pack);
    }
}
