package ru.heroicrealm.scormbuilder.exceptions;

/**
 * Created by kuran on 16.01.2019.
 */
public class FolderNotFoundException extends ScormbuilderException {
    long folderId;

    public FolderNotFoundException() {
        super();
    }

    public FolderNotFoundException(long folderId) {
        super();
        this.folderId = folderId;
    }

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }
}
