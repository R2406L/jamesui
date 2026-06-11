/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.apache.james.jamesui.frontend.administration.mappings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.james.jamesui.backend.api.Client;
import org.apache.james.jamesui.frontend.administration.AddressMappingPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.validator.StringLengthValidator;
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
import com.vaadin.ui.TextField;


/**
 *
 * @author r2406
 */
public class RegexMapping {
    
    private final static Logger LOG = LoggerFactory.getLogger(RegexMapping.class);
    
    private Client jamesClient;
    private AddressMappingPanel mainLayout;
    
    private ComboBox regexMappingUserCombo;
    private ComboBox regexMappingDomainCombo;
    private TextField regexMappingRegex;
    private final Label regexMappingTitle;
    private final FormLayout regexMappingLayout;
    private final Button addRegexpMappingButton;

    /**
     * Constructor
     * @param client
     */
    public RegexMapping(final Client client, AddressMappingPanel layout) {
        this.jamesClient = client;
        this.mainLayout = layout;
    
        this.regexMappingUserCombo = new ComboBox("User:");
        this.regexMappingDomainCombo = new ComboBox("Domain:");
        this.regexMappingUserCombo.addFocusListener(new ComboRefreshListener());	   
        this.regexMappingDomainCombo.addFocusListener(new ComboRefreshListener());

        this.regexMappingRegex = new TextField("Regex:");
        this.regexMappingRegex.addValidator(new StringLengthValidator("Must not be empty", 1, 100, false));	

        this.regexMappingLayout = new FormLayout();
        this.regexMappingLayout.setMargin(true); 
        this.regexMappingTitle = new Label("<b>Regex Mapping</b>",ContentMode.HTML);
        this.addRegexpMappingButton = new Button("Add");
        this.addRegexpMappingButton.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                LOG.debug("Adding Regex Mapping between user: "+regexMappingUserCombo.getValue()+ ", Domain: "+regexMappingDomainCombo.getValue()+", Regex: "+regexMappingRegex.getValue());				
                try {
                    regexMappingRegex.validate();
                    jamesClient.addRegexMapping((String)regexMappingUserCombo.getValue(), (String)regexMappingDomainCombo.getValue(), (String)regexMappingRegex.getValue());
                    layout.updateMappingTreeData(); 
                    Notification.show("Operation Executed Successfully !", Type.HUMANIZED_MESSAGE);
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
            }
        });	

        HorizontalLayout regexButtonLayout = new HorizontalLayout();
        regexButtonLayout.setSpacing(true);
        regexButtonLayout.addComponent(addRegexpMappingButton);

        this.regexMappingLayout.addComponent(regexMappingTitle);
        this.regexMappingLayout.addComponent(regexMappingUserCombo);
        this.regexMappingLayout.addComponent(regexMappingDomainCombo);
        this.regexMappingLayout.addComponent(regexMappingRegex);	    
        this.regexMappingLayout.addComponent(regexButtonLayout);
    }
    
    public FormLayout getLayout() {
        return this.regexMappingLayout;
    }
    
    /**
     * Utility method that fill the Users, addresses, domains Combo boxes
     */
    private void insertComboData(){

        String[] users = this.jamesClient.getAllusers();
        String[] domains = this.jamesClient.getDomains();

        List<String> usersWithoutDomain = new ArrayList<>(); 
        for (String user : users) {
            usersWithoutDomain.add(user.split("@")[0]);
        }

        this.regexMappingUserCombo.addItems(usersWithoutDomain);
        this.regexMappingDomainCombo.addItems(Arrays.asList(domains));
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
