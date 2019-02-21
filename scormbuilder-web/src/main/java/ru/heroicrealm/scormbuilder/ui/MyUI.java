package ru.heroicrealm.scormbuilder.ui;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;


import javax.servlet.annotation.WebServlet;


/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@PreserveOnRefresh
public class MyUI extends UI implements View {


    Navigator navigator;



    @Override
    protected void init(VaadinRequest vaadinRequest) {


        navigator = new Navigator(this,this);
        navigator.addView("",new MainView());
        navigator.addView("presentation-editor",new PresentationEditor());
        navigator.addView("package-editor",new PackageEditor());
        navigator.addView("tasks",new TaskView());
        navigator.addView("config",new ConfigView());
    }



    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
