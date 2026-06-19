package org.apache.james.jamesui.backend.configuration.bean;

import java.util.Set;
import java.util.UUID;

/**
 * Bean that represents a Recipient Rewrite mappings used in mapping Administration panel
 * 
 * @author fulvio
 *
 */
public class RecipientRewriteMappings {

    private final String id;
    private String userAndDomain;
    private Set<RewriteMapping> mappings;

    /**
     * Constructor
     */
    public RecipientRewriteMappings() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Constructor
     * Creates the mapping with the user and domain received and all the mappings
     *
     * @param userAndDomain
     * @param mappings
     */
    public RecipientRewriteMappings(String userAndDomain, Set<RewriteMapping> mappings) {
        this.id = UUID.randomUUID().toString();
        this.userAndDomain = userAndDomain;
        this.mappings = mappings;
    }

    public String getId() {
        return id;
    }

    public String getUserAndDomain() {
        return userAndDomain;
    }

    public void setUserAndDomain(String userAndDomain) {
        this.userAndDomain = userAndDomain;
    }

    public Set<RewriteMapping> getMappings() {
        return mappings;
    }

    public void setMappings(Set<RewriteMapping> mappings) {
        this.mappings = mappings;
    }
}
