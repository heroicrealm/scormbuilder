package ru.heroicrealm.scormbuilder;

import ru.heroicrealm.scormbuilder.entities.*;
import ru.heroicrealm.scormbuilder.service.ICatalogService;
import ru.heroicrealm.scormbuilder.service.IPresentationService;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * Created by kuran on 16.01.2019.
 */
@WebServlet("/hello")
public class HelloServlet extends javax.servlet.http.HttpServlet {

    @EJB
    ICatalogService ics;
    @EJB
    IPresentationService ips;

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {


        CatalogEntry ce = ics.createPresentation(2, "TestPres", "USER");
        Presentation presentation = ips.load(ce.getId());
        PresentationPage pp = new PresentationPage();
        pp.setPageNo(1);
        pp.setContent("Page1");
        presentation.addPage(pp);

        pp = new PresentationPage();
        pp.setPageNo(2);
        pp.setContent("Page2");
        presentation.addPage(pp);

        pp = new PresentationPage();
        pp.setPageNo(3);
        pp.setContent("Page3");
        presentation.addPage(pp);

        QuizQuestion qq = new QuizQuestion();
        qq.setText("В каком году основан Петербург");
        qq.setQuestionNo(1);
        qq.setQtype(QuizQuestion.QuestionType.SINGLE);
        qq.addAnswer(new QuizAnswer(1,"1812",false));
        qq.addAnswer(new QuizAnswer(2,"1703",true));
        qq.addAnswer(new QuizAnswer(3,"1912",false));
        qq.addAnswer(new QuizAnswer(4,"1945",false));

        presentation.addQuestion(qq);

        qq = new QuizQuestion();
        qq.setText("Выберите четные числа");
        qq.setQuestionNo(2);
        qq.setQtype(QuizQuestion.QuestionType.MULTI);
        qq.addAnswer(new QuizAnswer(1,"1",false));
        qq.addAnswer(new QuizAnswer(2,"2",true));
        qq.addAnswer(new QuizAnswer(3,"3",false));
        qq.addAnswer(new QuizAnswer(4,"4",true));
        qq.addAnswer(new QuizAnswer(5,"5",false));

        presentation.addQuestion(qq);

        qq = new QuizQuestion();
        qq.setQtype(QuizQuestion.QuestionType.TEXT);
        qq.setQuestionNo(3);
        qq.setText("Столица России?");
        qq.addAnswer(new QuizAnswer(1,"Москва",true));
        presentation.addQuestion(qq);

        ips.savePresentation(presentation);








    }
}
