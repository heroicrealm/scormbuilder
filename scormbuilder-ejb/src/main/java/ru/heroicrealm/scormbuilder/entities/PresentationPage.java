package ru.heroicrealm.scormbuilder.entities;

import javax.persistence.*;

/**
 * Created by kuran on 19.01.2019.
 */
@Entity
@Table(name = "pres_page")
public class PresentationPage implements Comparable<PresentationPage>{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "pages_seq_gen" )
    @SequenceGenerator(name = "pages_seq_gen",sequenceName = "page_seq",allocationSize = 50)
    @Column(name = "pageid")
    long pageId;

    @ManyToOne
    Presentation presentation;

    @Column(name = "pg_number")
    int pageNo;

    @Column(name="title")
    String title;
    @Lob
    @Column(name = "pg_content")
    String content;

    @Transient
    boolean selected;

    @Override
    public int compareTo(PresentationPage o) {
        return this.pageNo - o.pageNo;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public void setPresentation(Presentation presentation) {
        this.presentation = presentation;
    }

    public int getPageNo() {
        return pageNo;
    }

    public String getPageNos() {
        return String.valueOf(pageNo);
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
    public void setPageNos(String pageNo) {
        this.pageNo = Integer.parseInt(pageNo);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
