package ru.heroicrealm.scormbuilder.service;

import javax.ejb.Local;

/**
 * Created by kuran on 05.02.2019.
 */
@Local
public interface IConfigService {
    String FS_BASE_PATH="FS.BASE.PATH";

    String getProperty(String name);
    void setProperty(String name, String value);
}
