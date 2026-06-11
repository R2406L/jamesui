
package org.apache.james.jamesui.frontend;


import java.io.File;

import org.apache.james.jamesui.backend.api.Client;
import org.apache.james.jamesui.backend.configuration.bean.JamesuiLoginUser;
import org.apache.james.jamesui.backend.configuration.manager.EnvironmentConfigurationReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.event.MouseEvents;
import com.vaadin.shared.ui.label.ContentMode;       
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
    private Label statusLabel;
    private Image jamesLogoImage;
    private Image logoutImage;
    private JamesuiLoginUser loggedUser;

    private Scheduler scheduler;

    private static final String JAMESUI_TMP_FILE_NAME = "jamesui.tmp";	

    /**
     * Constructor
     * 
     */
    public HeaderPanel(Client jamesClient) {		

        setSizeFull();
        JSONObject data = jamesClient.getHealthCheck();

        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath(); 

        // Logo
        this.jamesLogoImage = new Image();
        this.jamesLogoImage.setHeight("30px");
        this.jamesLogoImage.setSource(new FileResource(new File(basepath +"/WEB-INF/images/james-logo.png")));

        addComponent(jamesLogoImage);
        
        // User
        this.loggedUser = (JamesuiLoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();       
        this.welcomeMsgLabel = new Label("Welcome: "+loggedUser.getUsername());      
        
        addComponent(welcomeMsgLabel);
        
        // Status
        try {
            String status = data.getString("status");
            this.statusLabel = new Label(status);
            if ("healthy".equals(status)) {
                this.statusLabel.addStyleName("v-color-green");
            } else if ("unhealthy".equals(status)) {
                this.statusLabel.addStyleName("v-color-red");
            } else {
                this.statusLabel.addStyleName("v-color-orange");
            }
            
        } catch(JSONException e) {
            this.statusLabel = new Label("unknown");
        }
        HorizontalLayout statusLayout = new HorizontalLayout();
        statusLayout.addComponents(new Label("Status:&nbsp;", ContentMode.HTML), this.statusLabel);
        
        addComponent(statusLayout);
        
        // Logout
        HorizontalLayout logoutLayout = new HorizontalLayout();
        
        this.logoutImage = new Image();
        this.logoutImage.addStyleName("v-datefield-button");
        this.logoutImage.setSource(new FileResource(new File(basepath +"/WEB-INF/images/logout.png")));       
        this.logoutImage.setAlternateText("Logout");
        this.logoutImage.setHeight("16px");

        Label logoutLabel = new Label("&nbsp;Logout", ContentMode.HTML);
        logoutLabel.addStyleName("v-datefield-button");

        logoutLayout.addComponents(logoutImage, logoutLabel); 
        logoutLayout.addLayoutClickListener(event -> {
            getUI().getPage().setLocation("/jamesui/j_spring_security_logout");
            getUI().getSession().close();
        });
        
        addComponent(logoutLayout);  
    }
	
}
