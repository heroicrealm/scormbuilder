package ru.heroicrealm.scormbuilder.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuran on 19.01.2019.
 */


@Entity
@Table(name = "presentations")
public class Presentation  {

    @Id
    @Column(name = "oid")
    long id;

    @Column(name = "name")
    String name;


    @Column(name = "title",length = 100)
    String title;

    @OneToMany(mappedBy = "presentation",cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @OrderBy("pageNo")
    List<PresentationPage> pages = new ArrayList<>();

    @OneToMany(mappedBy = "presentation",cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @OrderBy("questionNo")
    List<QuizQuestion> questions = new ArrayList<>();



    public void addQuestion(QuizQuestion qq){
        qq.setPresentation(this);
        questions.add(qq);
    }
    public void addPage(PresentationPage pp){
        pp.setPresentation(this);
        pages.add(pp);
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestion> questions) {
        this.questions = questions;
    }

    public List<PresentationPage> getPages() {
        return pages;
    }

    public void setPages(List<PresentationPage> pages) {
        this.pages = pages;
    }
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
}
