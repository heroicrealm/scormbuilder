package ru.heroicrealm.scormbuilder.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kuran on 12.02.2019.
 */
@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {
    public enum QuestionType {
        SINGLE,MULTI,TEXT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "questions_seq_gen" )
    @SequenceGenerator(name = "questions_seq_gen",sequenceName = "qq_seq",allocationSize = 50)
    long qqid;

    @Column(name = "col")
    String title;


    @Column(name ="qtype")
    QuestionType qtype;

    @Column(name = "q_number")
    int questionNo;

    @ManyToOne
    Presentation presentation;

    @Column(name = "txt")
    String text;

    @OneToMany(mappedBy = "question",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @OrderBy("answerNo")
    Set<QuizAnswer> answers = new HashSet<>();

    public void addAnswer(QuizAnswer qa){
        qa.setQuestion(this);
        answers.add(qa);
    }
    public int getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public void setPresentation(Presentation presentation) {
        this.presentation = presentation;
    }

    public Set<QuizAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<QuizAnswer> answers) {
        this.answers = answers;
    }

    public long getQqid() {
        return qqid;
    }

    public void setQqid(long qqid) {
        this.qqid = qqid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getQtype() {
        return qtype;
    }

    public void setQtype(QuestionType qtype) {
        this.qtype = qtype;
    }
}
