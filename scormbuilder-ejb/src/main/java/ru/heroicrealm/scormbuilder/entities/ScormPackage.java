package ru.heroicrealm.scormbuilder.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kuran on 26.01.2019.
 */
@Entity
@Table(name = "ScormPackage")
public class ScormPackage {
    @Id
    @Column(name = "pid")
    private Long id;


    @Column(name = "begda")
    Date begda;

    @Column(name = "endda")
    Date endda;

    @Column(name = "descr")
    String comment;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "scormComponents")
    @OrderBy("seqnr")
    List<ScormRef> components = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getBegda() {
        return begda;
    }

    public void setBegda(Date begda) {
        this.begda = begda;
    }

    public Date getEndda() {
        return endda;
    }

    public void setEndda(Date endda) {
        this.endda = endda;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ScormRef> getComponents() {
        return components;
    }

    public void setComponents(List<ScormRef> components) {
        this.components = components;
    }
}
