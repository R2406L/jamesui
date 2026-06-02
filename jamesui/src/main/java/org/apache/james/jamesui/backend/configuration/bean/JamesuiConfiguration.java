
package org.apache.james.jamesui.backend.configuration.bean;

/**
 * bean that represents the configuration stored in TOMCAT_HOME/conf/jamesui.config file (or jamesui-devel.config if Jamesui is running inside Eclipse)
 * (a user defined file with some configuration option)
 * 
 * @author fulvio
 *
 */
public class JamesuiConfiguration {
	
    /* the configuration files names used as template and the used configuration file created using the associated template
      Note that james beta4 version uses template files with .conf extension, insted beta5 uses .xml extension */


    private String jamesApiUrl;
    private String jamesApiLogin;
    private String jamesApiPassword;

    /**
     * Constructor
     */
    public JamesuiConfiguration() {

    }

    public String getJamesApiUrl() {
            return jamesApiUrl;
    }

    public void setJamesApiUrl(String jamesApiUrl) {
            this.jamesApiUrl = jamesApiUrl;
    }

    public String getJamesApiLogin() {
            return jamesApiLogin;
    }

    public void setJamesApiLogin(String jamesApiLogin) {
            this.jamesApiLogin = jamesApiLogin;
    }
    
    public String getJamesApiPassword() {
            return jamesApiPassword;
    }

    public void setJamesApiPassword(String jamesApiPassword) {
            this.jamesApiPassword = jamesApiPassword;
    }


    @Override
    public String toString() {
        return "JamesuiConfiguration ["
            + "jamesApiUrl=" + jamesApiUrl 
            + ", jamesApiLogin=" + jamesApiLogin + "]";
    }
}
