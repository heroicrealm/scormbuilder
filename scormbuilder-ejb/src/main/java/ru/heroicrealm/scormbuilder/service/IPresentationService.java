package ru.heroicrealm.scormbuilder.service;

import ru.heroicrealm.scormbuilder.entities.Presentation;
import ru.heroicrealm.scormbuilder.entities.PresentationPage;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by kuran on 20.01.2019.
 */
@Local
public interface IPresentationService {
    Presentation createPresentation(long id, String presentationName, String user);
    Presentation load(long id);

    void savePresentation(Presentation presentation);

    void deletePages(List<PresentationPage> deletedPages);
}
