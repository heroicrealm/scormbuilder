package ru.heroicrealm.scormbuilder.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


/**
 * Created by kuran on 03.02.2019.
 */
@Entity
@Table(name = "tasks")
public class Task {
    public enum Status{
        STARTED,FINISHED,ABORTED
    }
    public enum  TaskType{
        PACKAGE
    }

    @Id
    @Column(name = "tid")
    String guid;

    @Column(name = "name")
    String name;
    @Column(name = "dstart")
    Date start;

    @Column(name = "dend")
    Date end;

    @Column(name = "owner")
    String owner;

    @Column(name = "status")
    Status status;

    @Column(name = "taskType")
    TaskType taskType;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
}
