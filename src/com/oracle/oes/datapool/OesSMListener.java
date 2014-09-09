/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oracle.oes.datapool;

import java.net.*;
import java.io.*;
import java.util.*;

import com.bea.security.*;
import weblogic.security.principal.*;
import javax.security.auth.*;
import java.security.*;
import oracle.security.jps.openaz.pep.*;
import org.openliberty.openaz.azapi.pep.*;

/**
 * A simple socket server
 * @author faheem
 *
 */
public class OesSMListener implements Runnable {

    private Socket connection;
    private String TimeStamp;
    private int ID;

    public static void main(String[] args) {
        int port = 19999;
        int count = 0;
        try {
            ServerSocket socket1 = new ServerSocket(port);
            System.out.println("OesSMListener Initialized");
            while (true) {
                Socket connection = socket1.accept();
                Runnable runnable = new OesSMListener(connection, ++count);
                Thread thread = new Thread(runnable);
                thread.start();
            }
        } catch (Exception e) {
        }
    }

    OesSMListener(Socket s, int i) {
        this.connection = s;
        this.ID = i;
    }

    public void run() {
        try {
            BufferedInputStream is =
                new BufferedInputStream(connection.getInputStream());
            InputStreamReader isr = new InputStreamReader(is);
            int character;
            StringBuffer process = new StringBuffer();
            while ((character = isr.read()) != 13) {
                process.append((char)character);
            }
            System.out.println(process);
            //need to wait 10 seconds to pretend that we're processing something
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            TimeStamp = new java.util.Date().toString();
            String returnCode =
                "OesSMListener repsonded at " + TimeStamp + (char)13;
            BufferedOutputStream os =
                new BufferedOutputStream(connection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
            System.out.println(returnCode);
            
            osw.write(returnCode);
            osw.flush();
            
            //calling OES SM
            String result=runSM("principal1", "/resource1", "action1");

            
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
            }
        }
    }
    
    String runSM(String principal, String resource, String Action){
        Principal p = new WLSUserImpl(principal);
        
        Subject user = new Subject();
        user.getPrincipals().add(p);     
        
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
                                resource,
                                null).decide();

                System.out.println("***** Request: {" + p + ", " + action + ", "
                        + resource
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
}
