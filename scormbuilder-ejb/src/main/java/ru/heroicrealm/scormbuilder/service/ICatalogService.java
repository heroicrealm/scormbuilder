package ru.heroicrealm.scormbuilder.service;

import ru.heroicrealm.scormbuilder.entities.CatalogEntry;
import ru.heroicrealm.scormbuilder.exceptions.FolderNotFoundException;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by kuran on 16.01.2019.
 */

@Local
public interface ICatalogService {
    CatalogEntry createFolder(long parentId, String name, String author) throws FolderNotFoundException;


    List<CatalogEntry> list(long parentId) throws FolderNotFoundException;


    CatalogEntry createPresentation(long folderId, String presentationName, String user);
    void save(CatalogEntry ce);

    CatalogEntry load(long cid);

    CatalogEntry createPackage(long id, String fldrname, String user);

    boolean hasChildren(CatalogEntry catalogEntry);
}
