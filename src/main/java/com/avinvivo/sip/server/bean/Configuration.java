package com.avinvivo.sip.server.bean;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "CONFIGURATION")
@EntityListeners(PreventAnyUpdate.class)
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long configuration;
    
    @Basic(optional = false)
    @Column(nullable = false, length = 128)
    @Size(min = 1, max = 128)
    private String name;
    
    @Column     
    @NotNull        
    @Size(min = 0, max = 2147483647)
    private String value;
    
    protected Configuration(){	
    }
    
    public Configuration(final String name, final String value) {
    	this.value = value;
        this.name = name;
    }
   
    
    public String getName() {
        return name;
    }
    
    public Long getConfiguration() {
        return configuration;
    }

    public String getValue() {
        return value;
    }
}
