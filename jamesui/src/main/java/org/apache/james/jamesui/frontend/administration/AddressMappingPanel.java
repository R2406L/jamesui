
package org.apache.james.jamesui.frontend.administration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.james.jamesui.backend.api.Client;
import org.apache.james.jamesui.backend.configuration.bean.RecipientRewriteMappings;
import org.apache.james.jamesui.frontend.administration.mappings.AddressMapping;
import org.apache.james.jamesui.frontend.administration.mappings.DomainMapping;
import org.apache.james.jamesui.frontend.administration.mappings.RegexMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Item;
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
import org.apache.james.jamesui.backend.configuration.bean.RewriteMapping;

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

    private HashSet<String> mappingToRemoveSet = new HashSet<>();
    private List<String> displayed = new ArrayList<>();

    private Client jamesClient; 

    /**
     * Constructor
     */	
    public AddressMappingPanel(final Client jamesClient) {		

        setHeight("600px"); 

        this.setMargin(true); 
        this.setSpacing(true); 

        this.jamesClient = jamesClient;

        addressMappingTree = new Tree();
        addressMappingTree.setHeight(100, Unit.PERCENTAGE);
        addressMappingTree.addStyleName("checkboxed");		
        addressMappingTree.setSelectable(false);

        try {              	  
            updateMappingTreeData();        		
        } catch (Exception e) {
            LOG.error("Error retrieving mapping, cause: ", e);
        } 
        
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
                   showConfirmAndDelete();
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
    private void showConfirmAndDelete() {	

        ConfirmDialog.show(this.getUI(), "Please Confirm:", "Remove "+mappingToRemoveSet.size()+ " Mapping(s) ?","Yes","No", new ConfirmDialog.Listener() {

            private static final long serialVersionUID = 1L;

            public void onClose(ConfirmDialog dialog) {

                if (dialog.isConfirmed()) {	
                    boolean errorFlag = false;

                    for (String m : mappingToRemoveSet) {
                    
                        String current = m;
                        List<String> found = new ArrayList<>();
                        found.add(clearId(current));

                        while (!addressMappingTree.isRoot(current)) {
                            current = addressMappingTree.getParent(current).toString();
                            found.add(clearId(current));
                        }
                        
                        if (found.size() != 3) {
                            LOG.info("Incomplete matching selected");
                            continue;
                        }

                        try{			    				
                            switch (found.getLast()) {
                                case "Regex":
                                    LOG.debug("Calling removeRegexMapping("+found.getFirst()+","+found.get(1)+")");
                                    jamesClient.removeRegexMapping(found.get(1), found.getFirst());
                                    break;
                                case "DomainAlias":
                                    LOG.debug("Calling removeDomainMapping("+found.get(1)+","+found.getFirst()+")");
                                    jamesClient.removeDomainMapping(found.get(1), found.getFirst());
                                    break;
                                default:
                                    LOG.debug("Calling removeAddressMapping("+found.get(1)+","+found.getFirst()+")");
                                    jamesClient.removeAddressMapping(found.get(1), found.getFirst());
                                    break;
                            }
                        } catch(Exception e) {
                            LOG.error("Error removing Address Mapping, cause: ",e);			    					
                            errorFlag = true;
                            break;
                        }	
                    }

                    if(!errorFlag){	
                        Notification.show("Mapping(s) Removed Successfully !", Type.HUMANIZED_MESSAGE);
                        mappingToRemoveSet.clear();
                        updateMappingTreeData();
                    } else {
                        LOG.error("Error Removing Mapping");
                        mappingToRemoveSet.clear();
                        updateMappingTreeData();
                    }
                }
            }
        });
    }

    private String computeId(String s) {
        if (displayed.contains(s)) {
            int count = Collections.frequency(displayed, s);
            displayed.add(s);
            return s+" ("+count+")";
        } else {
            displayed.add(s);
            return s;
        }
    }
    
    private String clearId(String s) {
        if (s.contains(" (")) {
            return s.substring(0, s.lastIndexOf(" ("));
        } else {
            return s;
        }
    }
    
    /**
     * Insert in the Mapping Tree the dataset provided in argument
     * @param dataSet
     */
    public void updateMappingTreeData(){
        final List<RecipientRewriteMappings> dataSet = jamesClient.listMappings();

        addressMappingTree.removeAllItems();
        displayed.clear();

//        final HashSet<RewriteMapping> usersAndDomainSet = new HashSet<RewriteMapping>();

        addressMappingTree.addItem("DomainAlias");
        addressMappingTree.addItem("Address");
        addressMappingTree.addItem("Regex");
        
        for (int i = 0; i < dataSet.size(); i++) {

            RecipientRewriteMappings el = dataSet.get(i);
            String source = el.getUserAndDomain();
            String id = el.getId();
            
//            if(!userAndDomain.equalsIgnoreCase("*@*")) { 
//                LOG.debug("Mapping panel, UserAndDomain: "+userAndDomain);	     	     
//                addressMappingTree.addItem(userAndDomain); 

//                usersAndDomainSet.add(userAndDomain);

            RewriteMapping[] mappings = el.getMappings().toArray(new RewriteMapping[el.getMappings().size()]);
                // mappingTable.setPageLength(it.length);

            for (RewriteMapping map : mappings) {
                String src = computeId(source);
                String m = computeId(map.getMapping());
                
                addressMappingTree.addItem(src);
                addressMappingTree.setParent(src, map.getType());
                
                addressMappingTree.addItem(m);
                addressMappingTree.setParent(m, src);
            }

            final HashSet<String> checked = new HashSet<String>();

            // Decide which css style apply returning a css suffix (se jamesuitheme.css) 
            Tree.ItemStyleGenerator itemStyleGenerator = new Tree.ItemStyleGenerator() {	    		  

                private static final long serialVersionUID = 1L;

                /*
                 * @param itemId Is the name shows in the Tree node (eg auser@adomanin.com )
                 */
                @Override	
                public String getStyle(Tree source, Object itemId) {
                    if(!id.contains(itemId.toString())){
                        if (checked.contains(itemId.toString()))	    		        
                            return "checked";
                        else
                            return "unchecked";
                    }
                    return "normal";

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
