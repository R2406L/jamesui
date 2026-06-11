
package org.apache.james.jamesui.frontend.administration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.james.jamesui.backend.api.Client;
import org.apache.james.jamesui.backend.configuration.bean.RecipientRewriteMapping;
import org.apache.james.jamesui.frontend.administration.mappings.AddressMapping;
import org.apache.james.jamesui.frontend.administration.mappings.DomainMapping;
import org.apache.james.jamesui.frontend.administration.mappings.RegexMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.Reindeer;

/**
 * Create the panel where the user can manage the Address mapping
 * See: {@link http://james.apache.org/server/3/manage-recipientrewrite.html}
 * 
 * @author fulvio
 *
 */
public class AddressMappingPanel extends VerticalLayout {
	
    private static final long serialVersionUID = 1L;

    private final static Logger LOG = LoggerFactory.getLogger(AddressMappingPanel.class);

    private final HorizontalSplitPanel horizontalSplitPanel;

    private Tree addressMappingTree;	
    private Button removeMappingButton;
    private Button compactMappingButton;
    private Button expandMappingButton;

    private HashSet<String> mappingToRemoveSet = new HashSet<String>();

    private Client jamesClient; 

    /**
     * Constructor
     */	
    public AddressMappingPanel(final Client jamesClient) {		

        setHeight("600px"); 

        this.setMargin(true); 
        this.setSpacing(true); 

        this.jamesClient = jamesClient;

        VerticalSplitPanel leftPanelLayout = new VerticalSplitPanel();
        leftPanelLayout.setStyleName(Reindeer.SPLITPANEL_SMALL);	
        leftPanelLayout.setSplitPosition(85, Sizeable.Unit.PERCENTAGE);
        this.removeMappingButton = new Button("Remove Mapping(s)");		
        this.removeMappingButton.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {	
                if(mappingToRemoveSet.isEmpty())
                   Notification.show("Please, select an Item !", Type.HUMANIZED_MESSAGE);
                else
                   showConfirmAndDelete(jamesClient);
            }
        });

        this.compactMappingButton = new Button("Compact Tree");
        this.compactMappingButton.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;
            public void buttonClick(ClickEvent event) {	
                for(Object itemId: addressMappingTree.getItemIds())
                   addressMappingTree.collapseItem(itemId);
            }
        });	

        this.expandMappingButton = new Button("Expand Tree");
        this.expandMappingButton.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;
            public void buttonClick(ClickEvent event) {	
                    for(Object itemId: addressMappingTree.getItemIds())
                       addressMappingTree.expandItem(itemId);
            }
        });	

        addressMappingTree = new Tree();
        addressMappingTree.setHeight(100, Unit.PERCENTAGE);
        addressMappingTree.addStyleName("checkboxed");		
        addressMappingTree.setSelectable(false);

        try {              	  
            updateMappingTreeData();        		
        } catch (Exception e) {
            LOG.error("Error retrieving mapping, cause: ", e);
        } 

        HorizontalLayout bottomPageLayout = new HorizontalLayout();
        bottomPageLayout.setMargin(true);
        bottomPageLayout.setSpacing(true);
        bottomPageLayout.addComponent(removeMappingButton);
        bottomPageLayout.addComponent(compactMappingButton);
        bottomPageLayout.addComponent(expandMappingButton);

        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.addComponent(new Label("<b>Mappings found:<b>",ContentMode.HTML));
        vl.addComponent(addressMappingTree);

        leftPanelLayout.setFirstComponent(vl);       
        leftPanelLayout.setSecondComponent(bottomPageLayout);

        TabSheet tabsheet = new TabSheet();
        tabsheet.addTab(new AddressMapping(jamesClient, this).getLayout(),"Address mapping");
        tabsheet.addTab(new DomainMapping(jamesClient, this).getLayout(),"Domain mapping");
        tabsheet.addTab(new RegexMapping(jamesClient, this).getLayout(),"Regex mapping");

        horizontalSplitPanel = new HorizontalSplitPanel();
        horizontalSplitPanel.setStyleName(Reindeer.SPLITPANEL_SMALL);		 
        horizontalSplitPanel.setFirstComponent(leftPanelLayout);
        horizontalSplitPanel.setSecondComponent(tabsheet);	
        horizontalSplitPanel.setLocked(true);

        addComponent(horizontalSplitPanel);		
    }

    /**
     * Show a window confirmation using Vaadin "confirm window" component and proceed with Mapping deletion if user confirm	
     * 
     */
    private void showConfirmAndDelete(final Client jamesClient) {	

        ConfirmDialog.show(this.getUI(), "Please Confirm:", "Remove "+mappingToRemoveSet.size()+ " Mapping(s) ?","Yes","No", new ConfirmDialog.Listener() {

            private static final long serialVersionUID = 1L;

            public void onClose(ConfirmDialog dialog) {

                if (dialog.isConfirmed()) {	
                    boolean errorFlag = false;

                    //the tree node that represents the mapping
                    String mappingParent;
                    String mappingToRemove;		                	
                    Iterator<String>  mappingToRemoveIterator = mappingToRemoveSet.iterator();

                    while(mappingToRemoveIterator.hasNext()) {
                        mappingToRemove = mappingToRemoveIterator.next();

                        //System.out.println("Item to Remove: "+mappingToRemove);			    				
                        mappingParent = (String) addressMappingTree.getParent(mappingToRemove);
                        //System.out.println("Mapping Parent: "+mappingParent);

                        //analyze node parent (ie: the address that own the mapping)
                        String[] token = mappingParent.split("@");
                        String ownerUser = token[0];
                        String ownerDomain = token[1];

                        try{			    				
                            if(mappingToRemove.startsWith("regex")) {
                                //regex mapping eg:  regex:theregex
                                LOG.debug("Calling removeRegexMapping("+ownerUser+","+ ownerDomain+","+ mappingToRemove.split(":")[1]+")");
                                jamesClient.removeRegexMapping(ownerUser, ownerDomain, mappingToRemove.split(":")[1]);
                            } else if(mappingToRemove.startsWith("domain")) {
                                //domain mapping eg: domain:thedomain
                                LOG.debug("Calling removeDomainMapping("+ownerDomain+","+mappingToRemove.split(":")[1]+")");
                                jamesClient.removeDomainMapping(ownerDomain, mappingToRemove.split(":")[1]);
                            } else {
                                //address mapping eg: theuser@thedomain
                                LOG.debug("Calling removeAddressMapping("+ownerUser+","+ ownerDomain+","+mappingToRemove+")");
                                jamesClient.removeAddressMapping(ownerUser, ownerDomain, mappingToRemove);
                            }	

                        }catch(Exception e){
                            LOG.error("Error removing Address Mapping, cause: ",e);			    					
                            errorFlag = true;
                            break;
                        }			    				
                    }

                    if(!errorFlag){	
                        Notification.show("Mapping(s) Removed Successfully !", Type.HUMANIZED_MESSAGE);
                        mappingToRemoveSet.clear();
                        updateMappingTreeData();
                    }else{
                        LOG.error("Error Removing Mapping");
//                        Notification.show("Error Removing Mapping(s) See log file !", Type.ERROR_MESSAGE);
                        mappingToRemoveSet.clear();
                        updateMappingTreeData();
                    }

                } else {
                       // User did not confirm		                	
                }
            }
        });
    }

    /**
     * Insert in the Mapping Tree the dataset provided in argument
     * @param dataSet
     */
    public void updateMappingTreeData(){
        final List<RecipientRewriteMapping> dataSet = jamesClient.listMappings();

        addressMappingTree.removeAllItems(); 
        // The tree root nodes (ie: user@doamin)
        final HashSet<String> usersAndDomainSet = new HashSet<String>();

        for (int i=0; i<dataSet.size(); i++) {       
            // The value show in the "user" column
            String userAndDomain = dataSet.get(i).getUserAndDomain();
            if(!userAndDomain.equalsIgnoreCase("*@*")) { 
                LOG.debug("Mapping panel, UserAndDomain: "+userAndDomain);	     	     
                addressMappingTree.addItem(userAndDomain); 

                usersAndDomainSet.add(userAndDomain);

                Object[] it = dataSet.get(i).getMappings().toArray();
                // mappingTable.setPageLength(it.length);

                for (int j=0; j<it.length; j++) {	         		        			
                    LOG.debug("Found Address Mapping: "+it[j]);       		
                    addressMappingTree.addItem(it[j]);
                    addressMappingTree.setParent(it[j], userAndDomain);
                }

                // Remember which nodes are checked (use a Set implementation to prevent duplicates)
                final HashSet<String> checked = new HashSet<String>();

                // Decide which css style apply returning a css suffix (se jamesuitheme.css) 
                Tree.ItemStyleGenerator itemStyleGenerator = new Tree.ItemStyleGenerator() {	    		  

                    private static final long serialVersionUID = 1L;

                    /*
                     * @param itemId Is the name shows in the Tree node (eg auser@adomanin.com )
                     */
                    @Override	
                    public String getStyle(Tree source, Object itemId) {

                        if(!usersAndDomainSet.contains(itemId)){
                            if (checked.contains(itemId))	    		        
                                return "checked";
                            else
                                return "unchecked";
                            }
                            return "normal";  // Use default style for root node (ie: user@domain)
                    }
                }; 

                // Allow the user to "check" and "uncheck" tree nodes  by clicking them
                addressMappingTree.addItemClickListener(new ItemClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void itemClick(ItemClickEvent event) {

                        if (checked.contains(event.getItemId())){
                            checked.remove(event.getItemId());
                            mappingToRemoveSet.remove(event.getItemId());
                        }else{
                            checked.add((String) event.getItemId());
                            mappingToRemoveSet.add((String) event.getItemId());
                        }

                        // Trigger running the item style generator which the return the class name to return
                        addressMappingTree.markAsDirty();						
                    }
                });	 	    		
                addressMappingTree.setItemStyleGenerator(itemStyleGenerator);	         	
            }	
        }
    }
}
