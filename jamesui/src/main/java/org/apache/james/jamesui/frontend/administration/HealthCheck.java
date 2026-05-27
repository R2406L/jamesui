/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.apache.james.jamesui.frontend.administration;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.james.jamesui.backend.api.Client;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.vaadin.dialogs.ConfirmDialog;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.data.Item;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;


/**
 *
 * @author r2406
 */
public class HealthCheck extends VerticalLayout {
    
    private Client jamesClient;

    private Label totalUsers;
    private PagedTable statusTable;	
    private TextField newUserTextField;
    private PasswordField newUserPasswordTextField;
    private VerticalLayout leftPanelLayout;	
    private HorizontalSplitPanel horizontalSplitPanel;
    private FormLayout addUserFormLayout;
    private FormLayout editUserFormLayout;	
    private VerticalLayout rightPanelLayout;
    private Button addUserButton;

    private TextField selectedUserText;	
    private PasswordField newUserPasswordText;	
    private PasswordField retypeNewUserPasswordText; 		
    private Button changePasswordButton;

    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor
     */
    public HealthCheck(final Client client) {	

        setMargin(true);
        setSizeFull();
        
        this.jamesClient = client;

        this.leftPanelLayout = new VerticalLayout();			    

        this.statusTable = new PagedTable("");	   
        this.statusTable.setSelectable( true );
        this.statusTable.addContainerProperty("Name", String.class, null);	   
        this.statusTable.addContainerProperty("Status", String.class, null);
        this.statusTable.addContainerProperty("Cause", String.class, null);
        this.statusTable.addStyleName(Reindeer.TABLE_STRONG);		
        this.statusTable.setWidth(100, Unit.PERCENTAGE);
        this.statusTable.setPageLength(15);

        try {
            this.updateUsersTable();
        } catch(JSONException e) {
            
        }

        this.leftPanelLayout.addComponent(statusTable);
        this.leftPanelLayout.addComponent(statusTable.createControls());

        this.addComponent(leftPanelLayout);			
    }
    
    
    /**
     * Utility method to Insert/Update the Users table with the dataset provided in argument
     */
    private void updateUsersTable() throws JSONException {
        
        JSONObject dataSet = this.jamesClient.getHealthCheck();		
        
        Object newItemId = null;
        Item row = null;

        statusTable.removeAllItems();
        
        JSONArray checksList = dataSet.getJSONArray("checks");

        for (int i = 0; i < checksList.length(); i++) {        	
            JSONObject elem = checksList.getJSONObject(i);
            newItemId = statusTable.addItem();
            row = statusTable.getItem(newItemId);
            row.getItemProperty("Name").setValue(elem.getString("componentName"));
            row.getItemProperty("Status").setValue(elem.getString("status"));
            row.getItemProperty("Cause").setValue(elem.getString("cause"));
        }
        statusTable.refreshRowCache();        
    }
    
}

