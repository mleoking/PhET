/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 8:16:29 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class SimulationXMLEntry {
    private String url;
    private String abstractStr;
    private String thumb;
    private String title;

    public SimulationXMLEntry( String url, String abstractStr, String thumb, String title ) {
        this.url = url;
        this.abstractStr = abstractStr;
        this.thumb = thumb;
        this.title = title;
    }

    public String toString() {
        return "jnlp=" + url + ", abstract=" + abstractStr + ", thumb=" + thumb;
    }

    public URL getSimulationURL() {
        try {
            return new URL( getUrl() );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public URL getAbstractURL() {
        try {
            return new URL( getAbstract() );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public URL getThumbURL() {
        try {
            return new URL( getThumb() );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUrl() {
        return url;
    }

    public String getAbstract() {
        return abstractStr;
    }

    public String getThumb() {
        return thumb;
    }

    public boolean isJNLP() {
        return url.toLowerCase().endsWith( ".jnlp" );
    }

    public boolean isSWF() {
        return url.toLowerCase().endsWith( ".swf" );
    }

    public String getTitle() {
        return title;
    }
}
