/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.apache.james.jamesui.backend.configuration.bean;

/**
 *
 * @author r2406l
 */
public class RewriteMapping {
    
    private final String type;
    private final String mapping;
    
    /**
    * Constructor
    * Creates the mapping
    *
    * @param type
    * @param mapping
    */
    public RewriteMapping(String type, String mapping) {
        this.type = type;
        this.mapping = mapping;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getMapping() {
        return this.mapping;
    }
    
}
