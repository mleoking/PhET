/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.model.BaseModel;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

/**
 * User: Sam Reid
 * Date: Sep 15, 2003
 * Time: 2:53:11 AM
 * Copyright (c) Sep 15, 2003 by Sam Reid
 */
public class ReadManifest {

    public static void showManifestData(PhetApplication app) throws IOException {
//        VersionInfo vi = readVersionInfo(app);
        String str=readManifestString(app);
        JOptionPane.showMessageDialog(app.getApplicationView().getPhetFrame(), str);
    }

    public static String readManifestString(PhetApplication app) throws IOException {

        ClassLoader cl = app.getClass().getClassLoader();
        InputStream resource1=cl.getResourceAsStream("META-INF/Manifest.mf");
//        InputStream resource1=cl.getResourceAsStream("build.number");
        System.out.println("resource1 = " + resource1);
        Enumeration res=cl.getResources("meta-inf/Manifest.mf");
        boolean more=res.hasMoreElements();
        String all="";
        System.out.println("res = " + res);
        while (res.hasMoreElements()) {
            URL resource=(URL) res.nextElement();
            Manifest mf=new Manifest(resource.openStream());
            all+=mf.toString();
            System.out.println("resource = " + resource);
            System.out.println("mf = " + mf);
        }
        return all;
//        URL buildNumberURL = cl.getResource("build.number");
//        System.out.println("buildNumberURL = " + buildNumberURL);
//        int buildNum = -1;
//        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(buildNumberURL.openStream()));
//            String line = br.readLine();
//            while (line != null) {
//                if (line.toLowerCase().startsWith("build.number=")) {
//                    String number = line.substring("build.number=".length());
//                    buildNum = Integer.parseInt(number);
//                }
//                line = br.readLine();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//        }
//
//        InputStream buildTimeURL = cl.getResourceAsStream("build.time.stamp.txt");
//        String buildTimeStr = "-1";
//        try {
//            buildTimeStr = new BufferedReader(new InputStreamReader(buildTimeURL)).readLine();
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//        }
//        return new VersionInfo(buildNum, buildTimeStr);
    }
    public static void main(String[] args) throws IOException {
        PhetApplication pa=new PhetApplication(new ApplicationDescriptor("","","",10,10), new MyModule(""),new SwingTimerClock(0,0,true));
        readManifestString(pa);
    }
    static class MyModule extends Module{
        public MyModule(String name) {
            super(name);
            setApparatusPanel(new ApparatusPanel());
            setModel(new BaseModel(new SwingTimerClock(0,0,true)));
        }
    }
}
