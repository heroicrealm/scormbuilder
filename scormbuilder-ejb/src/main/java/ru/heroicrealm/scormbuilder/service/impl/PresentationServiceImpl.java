package ru.heroicrealm.scormbuilder.service.impl;

import ru.heroicrealm.scormbuilder.entities.Presentation;
import ru.heroicrealm.scormbuilder.entities.PresentationPage;
import ru.heroicrealm.scormbuilder.service.IPresentationService;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by kuran on 20.01.2019.
 */
@Stateless
public class PresentationServiceImpl implements IPresentationService {

    @PersistenceContext(unitName="primary")
    private EntityManager entityManager;

    @Override
    public Presentation createPresentation(long id, String presentationName, String user) {
        Presentation presentation = new Presentation();
        presentation.setId(id);
        presentation.setTitle(presentationName);
        presentation.setName(presentationName);
        PresentationPage pp = new PresentationPage();
        pp.setPageNo(1);
        pp.setPresentation(presentation);
        presentation.getPages().add(pp);
        entityManager.persist(presentation);
        return presentation;
    }

    @Override
    public Presentation load(long id) {
        return  entityManager.find(Presentation.class,id);

    }

    @Override
    public void savePresentation(Presentation presentation) {
        System.out.println("total:pages:"+presentation.getPages());
        presentation.getPages().stream().forEach(presentationPage -> entityManager.merge(presentationPage));
        presentation.getQuestions().stream().forEach(quizQuestion -> entityManager.merge(quizQuestion));
      //  entityManager.merge(presentation);

        //entityManager.merge(presentation);
    }

    @Override
    public void deletePages(List<PresentationPage> deletedPages) {
        deletedPages.stream().forEach(presentationPage -> entityManager.remove(entityManager.merge(presentationPage)));
    }
}
