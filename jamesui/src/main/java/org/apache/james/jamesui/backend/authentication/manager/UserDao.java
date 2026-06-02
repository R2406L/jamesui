
package org.apache.james.jamesui.backend.authentication.manager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.james.jamesui.backend.configuration.bean.JamesuiLoginUser;
import org.apache.james.jamesui.backend.configuration.manager.EnvironmentConfigurationReader;
import org.apache.james.jamesui.frontend.JamesUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Dao that load user e password from a source (DB, properties file....). This implementation load them from properties file.
 * 
 * The current logic is: 
 * - try to load user/pw from a properties file named users.properties placed under TOMCAT_HOME/conf folder.
 *  
 * Future versions of JamesUI could load User information from a different source (eg: a Database).
 * In that case change only the implementation logic of the one methods findByUsername
 * 
 * @author fulvio
 *
 */
public class UserDao {

    private final static Logger LOG = LoggerFactory.getLogger(UserDao.class);

    private static final String USERS_FILE_NAME = "users.properties";

    /**
     * Constructor
     */
    public UserDao() {

    }	

    /**
     * 1) Look for TOMCAT_CONF/user.properties (the user defined one)
     * The pattern is: user=user01,ROLE_USER
     * Load the User informations
     * @param username The login name of the user
     * @return A JamesUIuser
     */
    public JamesuiLoginUser findByUsername(String userName) {				

        ArrayList<GrantedAuthority> ga = new ArrayList<GrantedAuthority>();	    

        String username = null;
        String password = null;	

        JamesuiLoginUser jamesuiLoginUser = null;

        Properties usersConfig = null;

        try {			
            String path = EnvironmentConfigurationReader.getConfigFolder()+File.separator+"conf"+File.separator+"/"+USERS_FILE_NAME;

            usersConfig = new Properties();
            usersConfig.load(new FileReader(new File(path)));

            String fullRow = (String) usersConfig.get(userName);
            String[] tokens = fullRow.split(",");

            username = userName;
            password = tokens[0];
            LOG.error(username + " " + tokens[0] + " " + tokens[1]);

            ga.add(new SimpleGrantedAuthority(tokens[1]));				
            jamesuiLoginUser = new JamesuiLoginUser(username,password,ga);			
        } catch (IOException e) {
            LOG.info("File "+USERS_FILE_NAME+" NOT found under Tomcat/conf folder"); 
        }  

        return jamesuiLoginUser;	
    }

}
