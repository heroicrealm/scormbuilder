package ru.heroicrealm.scormbuilder.entities;

import javax.persistence.*;

/**
 * Created by kuran on 26.01.2019.
 */
@Entity
@Table(name = "package_refs")
public class ScormRef implements Comparable<ScormRef> {
    @Id
    @GeneratedValue
    @Column(name = "refid")
    long refid;

    @Column(name = "seqnr")
    int seqnr;

    @Column(name = "refName")
    String name;

    @Column(name = "targetType")
    ObjType targetType;

    @Column(name = "targetId")
    long targetId;

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public long getRefid() {
        return refid;
    }

    public void setRefid(long refid) {
        this.refid = refid;
    }

    public int getSeqnr() {
        return seqnr;
    }

    public void setSeqnr(int seqnr) {
        this.seqnr = seqnr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjType getTargetType() {
        return targetType;
    }

    public void setTargetType(ObjType targetType) {
        this.targetType = targetType;
    }

    @Override
    public int compareTo(ScormRef o) {
        return this.seqnr - o.seqnr;
    }
}
