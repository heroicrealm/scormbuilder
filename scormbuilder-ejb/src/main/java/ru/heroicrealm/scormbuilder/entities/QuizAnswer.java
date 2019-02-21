package ru.heroicrealm.scormbuilder.entities;

import javax.persistence.*;

@Entity
@Table(name = "quiz_answers")
public class QuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "qans_seq_gen" )
    @SequenceGenerator(name = "qans_seq_gen",sequenceName = "qans_seq",allocationSize = 50)
    long aid;

    @ManyToOne
    QuizQuestion question;

    @Column(name = "ansNo")
    int answerNo;

    @Column(name ="atext")
    String text;

    @Column(name = "correct")
    boolean correct;

    public QuizAnswer(int answerNo, String text, boolean correct) {
        this.answerNo = answerNo;
        this.text = text;
        this.correct = correct;
    }

    public QuizAnswer() {
    }

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

    public QuizQuestion getQuestion() {
        return question;
    }

    public void setQuestion(QuizQuestion question) {
        this.question = question;
    }

    public int getAnswerNo() {
        return answerNo;
    }

    public void setAnswerNo(int answerNo) {
        this.answerNo = answerNo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
