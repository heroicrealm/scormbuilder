package ru.heroicrealm.scormbuilder.service;

import ru.heroicrealm.scormbuilder.entities.ScormPackage;
import ru.heroicrealm.scormbuilder.entities.ScormRef;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by kuran on 26.01.2019.
 */
@Local
public interface IPackageService {
    void createPackage(long id, String packageName, String user);

    ScormPackage load(long packageId);

    void deleteRefs(List<ScormRef> deletedRefs);

    void save(ScormPackage pack);
}
