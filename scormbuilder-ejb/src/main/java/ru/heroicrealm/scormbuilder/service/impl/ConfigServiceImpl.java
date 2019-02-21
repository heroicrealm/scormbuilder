package ru.heroicrealm.scormbuilder.service.impl;

import ru.heroicrealm.scormbuilder.entities.Configuration;
import ru.heroicrealm.scormbuilder.service.IConfigService;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by kuran on 05.02.2019.
 */
@Stateless
public class ConfigServiceImpl implements IConfigService{
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public String getProperty(String name) {
        Configuration c = entityManager.find(Configuration.class,name);
        if(c!=null){
            return c.getValue();
        }
        return null;
    }

    @Override
    public void setProperty(String name, String value) {
        Configuration c = new Configuration();
        c.setProp(name);
        c.setValue(value);
        entityManager.merge(c);
    }
}
