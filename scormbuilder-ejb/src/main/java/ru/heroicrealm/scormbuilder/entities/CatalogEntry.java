package ru.heroicrealm.scormbuilder.entities;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kuran on 16.01.2019.
 */
@Entity
@Table(name = "Catalog")
public class CatalogEntry {

    @Transient
    public static final int FOLDER =1;
    @Transient
    public static final int PRESENTATION =2;
    @Transient
    public static final int PACKAGE =3;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "cid")
    long id;

    @Column(name = "name",length = 100)
    String name;

    @Column(name = "author",length = 50)
    String author;

    @Column(name = "createdDate")
    Date createdDate;

    @Column(name = "changed_by",length = 50)
    String changedBy;

    @Column(name = "changedDate")
    Date changedDate;




    @Column(name = "obj_type")
    int otype;

    @ManyToOne(targetEntity = CatalogEntry.class)
    CatalogEntry parent;

    @OneToMany(targetEntity = CatalogEntry.class,
            mappedBy = "parent",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    List<CatalogEntry> children =new LinkedList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(Date changedDate) {
        this.changedDate = changedDate;
    }

    public int getOtype() {
        return otype;
    }

    public void setOtype(int otype) {
        this.otype = otype;
    }

    public CatalogEntry getParent() {
        return parent;
    }

    public void setParent(CatalogEntry parent) {
        this.parent = parent;
    }

    public List<CatalogEntry> getChildren() {
        return children;
    }

    public void setChildren(List<CatalogEntry> children) {
        this.children = children;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String change) {
        this.changedBy = change;
    }

    public CatalogEntry() {
    }

    @Override
    public String toString() {
        if(this.otype == CatalogEntry.FOLDER) {
            return "["+name+"]";
        }else{
            return name;
        }

    }
}
