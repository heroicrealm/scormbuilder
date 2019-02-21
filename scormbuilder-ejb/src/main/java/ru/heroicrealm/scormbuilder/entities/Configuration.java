package ru.heroicrealm.scormbuilder.entities;

import javax.persistence.*;

/**
 * Created by kuran on 05.02.2019.
 */
@Entity
@Table(name = "Configuration")
public class Configuration {

    @Id
    @Column(name = "property")
    String prop;

    @Column(name = "val")
    String value;

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
