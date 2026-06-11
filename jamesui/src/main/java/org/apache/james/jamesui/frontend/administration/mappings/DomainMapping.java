/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.apache.james.jamesui.frontend.administration.mappings;

import java.util.Arrays;

import org.apache.james.jamesui.backend.api.Client;
import org.apache.james.jamesui.frontend.administration.AddressMappingPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;



/**
 *
 * @author r2406
 */
public class DomainMapping {
    
    private final static Logger LOG = LoggerFactory.getLogger(AddressMapping.class);
    
    private Client jamesClient;
    private AddressMappingPanel mainLayout;
    
    private ComboBox domainMappingDomainCombo;
    private ComboBox domainMappingTargetDomainCombo;
    private final Label domainMappingTitle;
    private final FormLayout domainMappingLayout;
    private final Button addDomainMappingButton;
    
    /**
     * Constructor
     */
    public DomainMapping(final Client client, AddressMappingPanel layout) {
        this.jamesClient = client;
        this.mainLayout = layout;
    
        this.domainMappingDomainCombo = new ComboBox("Domain:");
        this.domainMappingTargetDomainCombo = new ComboBox("Target Domain:");
        this.domainMappingDomainCombo.addFocusListener(new ComboRefreshListener());	   
        this.domainMappingTargetDomainCombo.addFocusListener(new ComboRefreshListener());

        this.domainMappingLayout = new FormLayout();
        this.domainMappingLayout.setMargin(true);
        this.domainMappingTitle = new Label("<b>Domain Mapping</b>",ContentMode.HTML);
        this.addDomainMappingButton = new Button("Add");
        this.addDomainMappingButton.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;
            public void buttonClick(ClickEvent event) {
                LOG.debug("Adding Domain Mapping between Domain: "+domainMappingDomainCombo.getValue()+", Target Domain: "+domainMappingTargetDomainCombo.getValue());				
                try {
                    jamesClient.addDomainMapping((String)domainMappingDomainCombo.getValue(), (String)domainMappingTargetDomainCombo.getValue());
                    layout.updateMappingTreeData(); 
                    Notification.show("Operation Executed Successfully !", Type.HUMANIZED_MESSAGE);
                } catch (Exception e) {
                    LOG.error("Error starting IMAP server, cause: ",e);
                    Notification.show("Error Adding Domain Mapping: "+e.getMessage(), Type.ERROR_MESSAGE);
                }
            }
        });

        HorizontalLayout domainButtonLayout = new HorizontalLayout();
        domainButtonLayout.setSpacing(true);
        domainButtonLayout.addComponent(addDomainMappingButton);

        this.domainMappingLayout.addComponent(domainMappingTitle);	   
        this.domainMappingLayout.addComponent(domainMappingDomainCombo);
        this.domainMappingLayout.addComponent(domainMappingTargetDomainCombo);
        this.domainMappingLayout.addComponent(domainButtonLayout);
    }
    
    public FormLayout getLayout() {
        return this.domainMappingLayout;
    }
    
    /**
     * Utility method that fill the Users, addresses, domains Combo boxes
     */
    private void insertComboData(){

        String[] domains = this.jamesClient.getDomains();

        this.domainMappingDomainCombo.addItems(Arrays.asList(domains)); 
        this.domainMappingTargetDomainCombo.addItems(Arrays.asList(domains)); 
    }
    
    /*
     * Listener attached at the combo that reload the combo valueswhen the combo is opened, to have the last values
     * of users and domains
     */
    private class ComboRefreshListener implements FocusListener{
        private static final long serialVersionUID = 1L;

        @Override
        public void focus(FocusEvent event) {
            insertComboData();
        }
    }
}
