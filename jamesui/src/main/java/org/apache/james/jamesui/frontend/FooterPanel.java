
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
import java.io.IOException;
import java.util.Properties;

/**
 * Create the upper bar of the application (the one with James logo)
 *
 */
public class FooterPanel extends HorizontalLayout {	
	
    private static final long serialVersionUID = 1L;
    private final static Logger LOG = LoggerFactory.getLogger(FooterPanel.class);
    private Label versionLabel;

    /**
     * Constructor
     * 
     */
    public FooterPanel(Client jamesClient) {		

        setHeight("16px");
        setWidth("100%");
        
        String version = "unknown";
        HorizontalLayout footerLayout = new HorizontalLayout();

        try {
            Properties productDetailsConfig = new Properties();			
            productDetailsConfig.load(this.getClass().getResourceAsStream("/product-details.properties"));
            version = productDetailsConfig.getProperty("jamesui.version");
        } catch(IOException e) {
            LOG.error("Can't open product-details.properties: " + e);
        }

        Label versionLabel = new Label("Version: " + version, ContentMode.HTML);
        footerLayout.addComponent(versionLabel);

        addComponent(footerLayout);  
    }
	
}
