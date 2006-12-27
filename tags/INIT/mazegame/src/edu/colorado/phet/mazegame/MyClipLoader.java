package edu.colorado.phet.mazegame;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Nov 14, 2002
 * Time: 10:54:22 PM
 * To change this template use Options | File Templates.
 */
//package phet.utils;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

public class MyClipLoader {
    ClassLoader loader;
    Applet applet;

    public MyClipLoader(ClassLoader loader, Applet applet) {
        this.loader = loader;
        this.applet = applet;
    }

    public AudioClip loadAudioClip(String name) {
        URL loc = loader.getResource(name);
        // new JFrame("URL="+loc.toString()).setVisible(true);
        AudioClip clip = Applet.newAudioClip(loc);
        //new JFrame("Clip="+clip).setVisible(true);;
        return clip;
        //return null;
    }
}