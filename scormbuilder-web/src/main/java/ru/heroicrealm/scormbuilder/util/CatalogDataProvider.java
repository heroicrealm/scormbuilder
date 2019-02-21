package ru.heroicrealm.scormbuilder.util;

import com.vaadin.data.provider.AbstractHierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import ru.heroicrealm.scormbuilder.entities.CatalogEntry;
import ru.heroicrealm.scormbuilder.exceptions.FolderNotFoundException;
import ru.heroicrealm.scormbuilder.service.ICatalogService;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by kuran on 01.02.2019.
 */
public class CatalogDataProvider extends AbstractHierarchicalDataProvider<CatalogEntry,Void> {
    private ICatalogService ics;
    private CatalogEntry root;
    public CatalogDataProvider(ICatalogService ics) {
        this.ics = ics;
        ics.load(1L);
    }

    public ICatalogService getIcs() {
        return ics;
    }

    public void setIcs(ICatalogService ics) {
        this.ics = ics;
    }

    @Override
    public int getChildCount(HierarchicalQuery<CatalogEntry, Void> hierarchicalQuery) {
        Stream<CatalogEntry> catalogEntryStream = fetchChildren(hierarchicalQuery);
        if(catalogEntryStream !=null){
            return (int) catalogEntryStream.count();
        }

        return 0;
    }

    @Override
    public Stream<CatalogEntry> fetchChildren(HierarchicalQuery<CatalogEntry, Void> hierarchicalQuery) {
        Optional<CatalogEntry> oParent = hierarchicalQuery.getParentOptional();
        if(oParent.isPresent()){
            try {
                return ics.list(oParent.get().getId()).stream();
            } catch (FolderNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            try {
                return ics.list(1L).stream();
            } catch (FolderNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean hasChildren(CatalogEntry catalogEntry) {
        return ics.hasChildren(catalogEntry);
    }

    @Override
    public boolean isInMemory() {
        return false;
    }
}
