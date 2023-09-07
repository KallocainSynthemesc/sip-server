/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.sip.Dialog;

/**
 *
 * @author Kilian
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SipUser {

    private Long id = 0L;
    
    private String user_name;
    
    private boolean active = false;
    
    private String sipadresse;
    
    private Long exp = 0L; //expire time from token perspective
    
    private Long expireClient = 0L; //expire time from client perspective
    
    private String client_id;

    private String[] scope;
    
    private Dialog dialog;
    
    private String subsriptionState;
    
    public SipUser(){
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSipadresse() {
        return sipadresse;
    }

    public void setSipadresse(String sipadresse) {
        this.sipadresse = sipadresse;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String[] getScope() {
        return scope;
    }

    public void setScope(String[] scope) {
        this.scope = scope;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }
    
    public Long getExpireClient() {
        return expireClient;
    }

    public void setExpireClient(Long expireClient) {
        this.expireClient = expireClient;
    }
    
    public String getSubsriptionState() {
        return subsriptionState;
    }

    public void setSubsriptionState(String subsriptionState) {
        this.subsriptionState = subsriptionState;
    }
}
