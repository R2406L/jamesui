/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.apache.james.jamesui.frontend.administration.mappings;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.james.jamesui.backend.api.Client;
import org.apache.james.jamesui.frontend.administration.AddressMappingPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author r2406
 */
public class AddressMapping {
    
    private final static Logger LOG = LoggerFactory.getLogger(AddressMapping.class);
    
    private Client jamesClient;
    private AddressMappingPanel mainLayout;
    
    private final Label addressMappingTitle;
    private final FormLayout addressMappingLayout;
    private final Button addAddressMappingButton;
    
    private ComboBox addressMappingUserCombo;
    private ComboBox addressMappingDomainCombo;
    private TextField addressMappingAddressText;

    /**
     * Constructor
     * @param client
     */
    public AddressMapping(final Client client, AddressMappingPanel layout) {
        this.jamesClient = client;
        this.mainLayout = layout;
        
        this.addressMappingUserCombo = new ComboBox("User:"); 
        this.addressMappingDomainCombo = new ComboBox("Domain:");	   
        this.addressMappingUserCombo.addFocusListener(new ComboRefreshListener());	   
        this.addressMappingDomainCombo.addFocusListener(new ComboRefreshListener());

        this.addressMappingAddressText = new TextField("Address:");	    

        this.addressMappingLayout = new FormLayout();	    
        this.addressMappingLayout.setMargin(true);
        this.addressMappingTitle = new Label("<b>Address Mapping</b>",ContentMode.HTML);
        this.addAddressMappingButton = new Button("Add");
        this.addAddressMappingButton.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                LOG.debug("Adding Address Mapping between user: "+addressMappingUserCombo.getValue()+ ", Domain: "+addressMappingDomainCombo.getValue()+", Address: "+addressMappingAddressText.getValue());				
                try {
                    jamesClient.addAddressMapping((String)addressMappingUserCombo.getValue(), (String)addressMappingDomainCombo.getValue(), (String)addressMappingAddressText.getValue());
                    mainLayout.updateMappingTreeData(); 
                    Notification.show("Operation Executed Successfully !", Type.HUMANIZED_MESSAGE);
                } catch (Exception e) {
                    LOG.error("Error Adding Address Mapping, cause: ",e);
                    Notification.show("Error Adding Address Mapping: "+e.getMessage(), Type.ERROR_MESSAGE);
                }
            }
        });	 	    

        HorizontalLayout addressButtonLayout = new HorizontalLayout();
        addressButtonLayout.setSpacing(true);
        addressButtonLayout.addComponent(addAddressMappingButton);

        this.addressMappingLayout.addComponent(addressMappingTitle);
        this.addressMappingLayout.addComponent(addressMappingUserCombo);
        this.addressMappingLayout.addComponent(addressMappingDomainCombo);
        this.addressMappingLayout.addComponent(addressMappingAddressText);
        this.addressMappingLayout.addComponent(addressButtonLayout);
    }
    
    public FormLayout getLayout() {
        return this.addressMappingLayout;
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

        this.addressMappingUserCombo.addItems(usersWithoutDomain);
        this.addressMappingDomainCombo.addItems(Arrays.asList(domains));	    
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
    
    /**
     * Utility that remove alla items in all the combo
     */
    private void emptyAllCombo(){
        addressMappingUserCombo.removeAllItems();
        addressMappingDomainCombo.removeAllItems();
    }
}
