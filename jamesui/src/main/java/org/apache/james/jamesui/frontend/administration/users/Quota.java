/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.apache.james.jamesui.frontend.administration.users;

import org.apache.james.jamesui.backend.api.Client;


import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import java.lang.Integer;

/**
 *
 * @author R2406
 */
public class Quota {
    
    private static final int KB = 1024;
    private static final int MB = 1024 * 1024;
    private static final int GB = 1024 * 1024 * 1024;
    private static final int TB = 1024 * 1024 * 1024 * 1024;
    
    private Client client;
    
    public VerticalLayout quotasLayout;
    
    private String user;
    
    private FormLayout setQuotaFormLayout;
    
    private Label formLabel;
    private TextField setQuotaSizeField;
    private TextField setQuotaCountField;
    
    private Label sizeLabel;
    private Label countLabel;
    private ProgressBar sizeBar;
    private ProgressBar countBar;
    
    private Button setQuotaButton;
    
    /**
     * Constructor
     */
    public Quota(final Client client) {
        this.client = client;
        
        this.quotasLayout = new VerticalLayout();
        this.quotasLayout.setMargin(true);

        this.setQuotaFormLayout = new FormLayout();	
        this.setQuotaFormLayout.setSpacing(true);
        this.setQuotaFormLayout.setMargin(new MarginInfo(false, false, false, true));		

        this.setQuotaSizeField = new TextField("Size:");
        this.setQuotaSizeField.setWidth("300px");
        this.setQuotaSizeField.setRequired(true);        
        this.setQuotaSizeField.setRequiredError("Required field");
        this.setQuotaSizeField.setImmediate(true);	
        this.setQuotaSizeField.addValidator(new StringLengthValidator("Must not be empty", 1, 100, false));
        
        this.setQuotaCountField = new TextField("Count:");
        this.setQuotaCountField.setWidth("300px");
        this.setQuotaCountField.setRequired(true);        
        this.setQuotaCountField.setRequiredError("Required field");
        this.setQuotaCountField.setImmediate(true);	
        this.setQuotaCountField.addValidator(new StringLengthValidator("Must not be empty", 1, 100, false));
        
        this.setQuotaButton = new Button("Set quota");
        this.setQuotaButton.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                boolean resp = client.setUserQuota(user, Integer.parseInt(setQuotaSizeField.getValue()), Integer.parseInt(setQuotaCountField.getValue()));
                if (resp) {
                    Notification.show("Quota set successfully", Type.HUMANIZED_MESSAGE);
                    setUser(user);
                } else {
                    Notification.show("Quota set error", Type.HUMANIZED_MESSAGE);
                }
            }
        });
        
        this.formLabel = new Label("Please select user:", ContentMode.HTML);

        this.setQuotaFormLayout.addComponent(formLabel);
        this.setQuotaFormLayout.addComponent(setQuotaSizeField);
        this.setQuotaFormLayout.addComponent(setQuotaCountField);
        this.setQuotaFormLayout.addComponent(setQuotaButton);
        
        // User quotas
        VerticalLayout quotaStatusLayout = new VerticalLayout();
        this.sizeLabel = new Label("User size quota is unknown", ContentMode.HTML);
        
        this.sizeBar = generateBar();
        this.sizeBar.setValue((float) 0.0);

        this.countLabel = new Label("User count quota is unknown", ContentMode.HTML);
        
        this.countBar = generateBar();
        this.countBar.setValue((float) 0.0);
        
        quotaStatusLayout.addComponent(sizeLabel);
        quotaStatusLayout.addComponent(sizeBar);
        quotaStatusLayout.addComponent(countLabel);
        quotaStatusLayout.addComponent(countBar);
        
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setStyleName(Reindeer.SPLITPANEL_SMALL);
        splitPanel.setFirstComponent(quotaStatusLayout);	  
        splitPanel.setSecondComponent(setQuotaFormLayout);
        splitPanel.setLocked(true);
        splitPanel.setHeight("250px");

        this.quotasLayout.addComponent(splitPanel);
    }
    
    private static ProgressBar generateBar(){

        ProgressBar bar = new ProgressBar();
        bar.setIndeterminate(true);
        bar.setWidth("300px");
        bar.setEnabled(true);
        bar.setVisible(true);
        bar.setIndeterminate​(false);
        
        return bar;
    }
    
    public VerticalLayout getQuotasLayout() {
        return this.quotasLayout;
    }
    
    public void setUser(String user) {
        int[][] quotas = this.client.getUserQuota(user);
        
        this.user = user;
        this.formLabel.setValue("Set user <b>"+this.user+"</b> quota:");
        String sizeQuota = (quotas[0][0] > 0) ? getHumanSize(quotas[0][0]) : "Unlimited";
        this.sizeLabel.setValue("User size quota is <b>"+sizeQuota+" / "+getHumanSize(quotas[0][1])+"</b>");
        this.sizeBar.setValue((quotas[0][2] / 100 > 1) ? 1 : (float) quotas[0][2] / 100);
        String countQuota = (quotas[1][0] > 0) ? String.valueOf(quotas[1][0]) : "Unlimited";
        this.countLabel.setValue("User count quota is <b>"+countQuota+" / "+quotas[1][1]+"</b>");
        this.countBar.setValue((quotas[1][2] / 100 > 1) ? 1 : (float) quotas[1][2] / 100);
    };
    
    private String getHumanSize(int size) {
        if (size < KB) return size + "B";
        else if (size < MB) return (size / KB) + "KB";
        else if (size < GB) return (size / MB) + "MB";
        else if (size < TB) return (size / GB) + "GB";
        else return (size / TB) + "TB";
    }
}
