package ru.heroicrealm.scormbuilder.util;

import com.vaadin.data.provider.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;

import java.io.File;
import java.io.FilenameFilter;
import java.util.stream.Stream;

/**
 * Created by kuran on 01.02.2019.
 */
public class FileSystemDataProvider  extends
        AbstractBackEndHierarchicalDataProvider<File, FilenameFilter> {

    private final File root;

    public FileSystemDataProvider(File root) {
        this.root = root;
    }

    @Override
    public int getChildCount(
            HierarchicalQuery<File, FilenameFilter> query) {
        return (int) fetchChildren(query).count();
    }

    @Override
    public Stream<File> fetchChildrenFromBackEnd(
            HierarchicalQuery<File, FilenameFilter> query) {
        final File parent = query.getParentOptional().orElse(root);
        return query.getFilter()
                .map(filter -> Stream.of(parent.listFiles(filter)))
                .orElse(Stream.of(parent.listFiles()))
                .skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public boolean hasChildren(File item) {
        return item.list() != null && item.list().length > 0;
    }
}
