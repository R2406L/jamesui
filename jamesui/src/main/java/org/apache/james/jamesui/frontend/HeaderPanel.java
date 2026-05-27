
package org.apache.james.jamesui.frontend;


import java.io.File;

import org.apache.james.jamesui.backend.configuration.bean.JamesuiLoginUser;
import org.apache.james.jamesui.backend.configuration.manager.EnvironmentConfigurationReader;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

/**
 * Create the upper bar of the application (the one with James logo)
 *
 */
public class HeaderPanel extends HorizontalLayout {	
	
    private static final long serialVersionUID = 1L;

    private final static Logger LOG = LoggerFactory.getLogger(HeaderPanel.class);

    private Label welcomeMsgLabel;
    private Image jamesLogoImage;
    private Image logoutImage;
    private JamesuiLoginUser loggedUser;

    private Scheduler scheduler;

    private static final String JAMESUI_TMP_FILE_NAME = "jamesui.tmp";	

    /**
     * Constructor
     * 
     */
    public HeaderPanel() {		

        setSizeFull();		 
//        setHeight(10, Unit.PERCENTAGE);

        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath(); 

        this.logoutImage = new Image("Logout", new FileResource(new File(basepath +"/WEB-INF/images/logout.png")));       
        this.logoutImage.setAlternateText("Logout");
        this.logoutImage.setHeight("16px");
        this.logoutImage.addClickListener(new MouseEvents.ClickListener() {		
            private static final long serialVersionUID = 1L;

            @Override
            public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
                getUI().getPage().setLocation("/jamesui/j_spring_security_logout");
                getUI().getSession().close();
            }
        });
	   
        this.loggedUser = (JamesuiLoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();       
        this.welcomeMsgLabel = new Label("Welcome: "+loggedUser.getUsername());      

        this.jamesLogoImage = new Image();
        this.jamesLogoImage.setHeight("30px");
        this.jamesLogoImage.setSource(new FileResource(new File(basepath +"/WEB-INF/images/james-logo.png")));

        addComponent(jamesLogoImage);
        addComponent(welcomeMsgLabel);
        addComponent(logoutImage);  
    }
	
}
