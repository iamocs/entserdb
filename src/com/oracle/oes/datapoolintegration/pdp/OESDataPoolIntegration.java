/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.oes.datapoolintegration.pdp;

import java.util.*;
import com.bea.security.*;
import weblogic.security.principal.*;
import javax.security.auth.*;
import java.security.*;
import oracle.security.jps.openaz.pep.*;
import org.openliberty.openaz.azapi.pep.*;

/**
 *
 * @author OCS
 */
public class OESDataPoolIntegration {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // user initiating Authorization request
        Principal p = new WLSUserImpl("weblogic");

        Subject user = new Subject();
        user.getPrincipals().add(p);

        // Resource being accessed AppName/ResourceType/ResouceName
        String resourceString = "HelloOESworld/MyResourceType/MyResource";

        // Action initiated by the user
        String action = "write";

        // Environmental/Context attributes
        while (true) {
            try {
                // get Authorization response from OES
                PepResponse response
                        = PepRequestFactoryImpl.getPepRequestFactory()
                        .newPepRequest(
                                user,
                                action,
                                resourceString,
                                null).decide();

                System.out.println("Request: {weblogic, " + action + ", "
                        + resourceString
                        + "} \nResult: " + response.allowed());

            } catch (PepException e) {
                System.out.println("***** Caught exception: "
                        + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println("sleeping 5 sec. Hit Ctrl-C to quit\n");

            try {
                Thread.currentThread().sleep(5000);
            } catch (Exception e) {
            }
        }
    }
;

}
