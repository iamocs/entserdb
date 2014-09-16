/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.oes.datapool;

import com.bea.security.*;
import com.oracle.util.ReadProperties;
import com.oracle.util.ReadPropertiesOESDaemon;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.security.auth.*;
import oracle.security.jps.openaz.pep.*;
import org.openliberty.openaz.azapi.pep.*;
import weblogic.security.principal.*;

/**
 * A simple socket server
 *
 * @author faheem
 *
 */
public class OesSMListener implements Runnable {

    private Socket connection;
    private String TimeStamp;
    private int ID;
    private ReadProperties properties;

    public static void main(String[] args) {
        ReadPropertiesOESDaemon prop = new ReadPropertiesOESDaemon();
        int port = 19999;
        int count = 0;

        if (Boolean.getBoolean(prop.getMode())) {
            System.out.println("# Inicio del servicio OES Listener en modo Prueba (sin levantar el listener)...");
            OesSMListener oesListener = new OesSMListener();
            String OESresult = oesListener.runSM(prop.getPrincipal(), prop.getResource(), prop.getAction());
            System.out.println("Respuesta de la evaluación: " + OESresult);
        } else {

            port = Integer.getInteger(prop.getConnPort());
            count = 0;
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
    }

    OesSMListener(Socket s, int i) {
        this.connection = s;
        this.ID = i;
    }

    OesSMListener() {
    }

    public void run() {
        String userId = null;
        String database = null;
        String databaseSchema = null;
        String databaseTable = null;
        String action = null;
        String resourceType = null;
        String recurso = null;
        boolean inputParameterError = false;
        String returnCode;

        try {
            BufferedInputStream is
                    = new BufferedInputStream(connection.getInputStream());
            InputStreamReader isr = new InputStreamReader(is);
            int character;
            StringBuffer process = new StringBuffer();
            while ((character = isr.read()) != 13) {
                process.append((char) character);
            }
            System.out.println("DAEMON TRACE: Process = " + process);
            String[] inputData = process.toString().split("#");

            if ((inputData == null)
                    || (inputData.length != 6)) {
                inputParameterError = true;
            } else {
                userId = inputData[0];
                database = inputData[1];
                resourceType = inputData[2];
                databaseSchema = inputData[3];
                databaseTable = inputData[4];
                action = inputData[5];

                if (database.compareToIgnoreCase("empty") == 0) {
                    inputParameterError = true;
                } else if (resourceType.compareToIgnoreCase("empty") == 0) {
                    inputParameterError = true;
                } else if (databaseSchema.compareToIgnoreCase("empty") == 0) {
                    inputParameterError = true;
                } else if (action.compareToIgnoreCase("empty") == 0) {
                    inputParameterError = true;
                } else if (databaseTable.compareToIgnoreCase("empty") == 0) {
                    recurso = database + "/" + resourceType + "/" + databaseSchema;
                } else {
                    recurso = database + "/" + resourceType + "/" + databaseSchema + "/" + databaseTable;
                }
            }

            TimeStamp = new java.util.Date().toString();

            //calling OES SM
            String OESresult = Boolean.toString(false);

            if (!inputParameterError) {
                OESresult = runSM(userId, recurso, action);
            }

            /*
            if (process.toString().equalsIgnoreCase("trueuser")) {
                OESresult = "true";
            } else {
                OESresult = "false";
            }
            */
            
            returnCode = OESresult + (char) 13;

            BufferedOutputStream os
                    = new BufferedOutputStream(connection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
            System.out.println("* DAEMON TRACE: returnCode = " + returnCode);

            osw.write(returnCode);
            osw.flush();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
            }
        }
    }

    String runSM(String principal, String resource, String action) {
        String res = "false";
        Principal p = new WLSUserImpl(principal);

        Subject user = new Subject();
        user.getPrincipals().add(p);

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

                res = "" + response.allowed();

            } catch (PepException e) {
                System.out.println("***** Caught exception: "
                        + e.getMessage());
                e.printStackTrace();
                return "false";
            }
            return res;
        }
    }
}
