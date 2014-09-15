/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oracle.util;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author ruben
 */
public class ReadPropertiesOESDaemon extends ReadProperties{
    String connPort;
    String action;
    String principal;
    String resource;
    
    String att1;
    String att2;
    String att3;
    
    String mode;

    public ReadPropertiesOESDaemon() {
        super();
        
        this.connPort = prop.getProperty("local.connection.port");
        
        if (Boolean.getBoolean(prop.getProperty("test.mode")))
        {
            this.mode="test";
            this.action = prop.getProperty("test.case.action");
            this.principal = prop.getProperty("test.case.principal");
            this.resource = prop.getProperty("test.case.resource");
            this.att1 = prop.getProperty("test.case.attribute.1");
            this.att2 = prop.getProperty("test.case.attribute.2");
            this.att3 = prop.getProperty("test.case.attribute.3");    
        }
        else
        {
            this.mode="test";
        }
        
    }

    public String getConnPort() {
        return connPort;
    }

    public String getAction() {
        return action;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getResource() {
        return resource;
    }

    public String getAtt1() {
        return att1;
    }

    public String getAtt2() {
        return att2;
    }

    public String getAtt3() {
        return att3;
    }

    public String getMode() {
        return mode;
    }
}



