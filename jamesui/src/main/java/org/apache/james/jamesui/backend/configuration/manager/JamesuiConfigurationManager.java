
package org.apache.james.jamesui.backend.configuration.manager;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.james.jamesui.backend.configuration.bean.JamesuiConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager for the configuration of JamesUi product.
 * It offer methods to manage the configuration stored in jamesui.config file that
 * the user must place under TOMCAT/conf folder with his custom values about James server installation folder
 * and  Database storage folder.
 * 
 * @author fulvio
 *
 */
public class JamesuiConfigurationManager {
	
    private final static Logger LOG = LoggerFactory.getLogger(JamesuiConfigurationManager.class);

    /* config filename that must be placed by the user in TOMCAT/conf folder */
    private static final String CONFIG_FILE_NAME = "jamesui.config";

    /* config filename placed inside the war file used for Eclipse development */
    private static final String DEVEL_CONFIG_FILE_NAME = "jamesui-devel.config";

    /*
     * Constructor
     */
    public JamesuiConfigurationManager(){

    }
	
    /**
     * Return the JamesUI configuration to use, chosen between:
     * 
     * 1) TOMCAT_HOME/conf/jamesui.config --> if jamesui running inside Tomcat
     * 2) src/main/resources/jamesui-devel.config --> if jamesui is running inside Eclipse
     *  
     * @return A JamesuiConfiguration object containing the configuration to use. return null if no configuration file was found anywhere.
     */
    public JamesuiConfiguration loadConfiguration() {		
        JamesuiConfiguration jamesuiConfiguration = new JamesuiConfiguration();

        try {   	
            File jamesuiConfigFile = new File(EnvironmentConfigurationReader.getConfigFolder()+File.separator+"conf"+File.separator+CONFIG_FILE_NAME);	

            if(!jamesuiConfigFile.exists()) throw new ConfigurationException();

            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(jamesuiConfigFile);	

            jamesuiConfiguration.setJamesApiUrl(propertiesConfiguration.getString("james_url_api"));
            jamesuiConfiguration.setJamesApiLogin(propertiesConfiguration.getString("james_login"));
            jamesuiConfiguration.setJamesApiPassword(propertiesConfiguration.getString("james_password"));

        } catch (ConfigurationException e) {
            LOG.trace("Configuration file "+CONFIG_FILE_NAME+" not found in Tomcat/conf folder");
        }

        return jamesuiConfiguration;		
    }

}
