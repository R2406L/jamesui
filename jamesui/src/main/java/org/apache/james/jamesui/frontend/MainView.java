package org.apache.james.jamesui.frontend;


import org.apache.james.jamesui.backend.api.Client;
import org.apache.james.jamesui.backend.configuration.bean.JamesuiConfiguration;
import org.apache.james.jamesui.frontend.administration.AddressMappingPanel;
import org.apache.james.jamesui.frontend.administration.DomainsPanel;
import org.apache.james.jamesui.frontend.administration.HealthCheck;
import org.apache.james.jamesui.frontend.administration.ProductInfoPanel;
import org.apache.james.jamesui.frontend.administration.UsersPanel;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;


/**
 * Build the main page (ie: the home page) 
 * @author fulvio
 *
 */
@Component
@Scope("prototype")
@Secured("ROLE_USER")
public class MainView extends VerticalLayout implements View {
		
    private static final long serialVersionUID = 1L;

    private HeaderPanel headerPanel;		
    private TabSheet bodyTabsheet;		

    /**
     * Constructor
     * @throws Exception 
     */
    public MainView(Client jamesClient, int heigth, JamesuiConfiguration jamesuiConfiguration) {		

        setSizeFull();
        setSpacing(true);
        setMargin(new MarginInfo(true, true, true, true)); 

        this.headerPanel = new HeaderPanel(jamesClient);

        this.bodyTabsheet = new TabSheet();
        this.bodyTabsheet.addTab(new HealthCheck(jamesClient),"Health Check");
        this.bodyTabsheet.addTab(new DomainsPanel(jamesClient),"Domains");
        this.bodyTabsheet.addTab(new UsersPanel(jamesClient),"Users"); 
        this.bodyTabsheet.addTab(new AddressMappingPanel(jamesClient,heigth),"Mapping");		
        this.bodyTabsheet.addTab(new ProductInfoPanel(jamesuiConfiguration),"Produtc Info");	

        addComponent(headerPanel);
        addComponent(bodyTabsheet);

        setExpandRatio(headerPanel, 1);
        setExpandRatio(bodyTabsheet, 24);	
    }

    @Override
    public void enter(ViewChangeEvent event) {	

    }

}
