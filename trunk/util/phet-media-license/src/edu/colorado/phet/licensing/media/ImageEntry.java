package edu.colorado.phet.licensing.media;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

/**
 * User: Sam Reid
 * Date: Aug 17, 2006
 * Time: 12:37:02 AM
 * Copyright (c) Aug 17, 2006 by Sam Reid
 */
public class ImageEntry {
    private String imageName;
    private boolean nonPhet = false;
    private String source = "";
    private String notes = "";
    private boolean done = false;

    public ImageEntry( String imageName ) {
        this.imageName = imageName;
//        System.out.println( "imageName = " + imageName );
    }

    public ImageEntry( Properties prop, String imageName ) {
        parseProperties( prop, imageName );
//        System.out.println( "imageName = " + imageName );
    }

    public String getImageName() {
        return imageName;
    }

    public BufferedImage toImage() {
        try {
//            System.out.println( "imageName = " + imageName );
            File file = new File( "annotated-data", imageName );
//            System.out.println( "file = " + file.getAbsolutePath() );
            return ImageLoader.loadBufferedImage( file.toURL() );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public boolean isNonPhet() {
        return nonPhet;
    }

    public boolean needsAttention() {
        return done;
    }

    public void setNonPhet( boolean nonPhet ) {
        if ( nonPhet != this.nonPhet ) {
            this.nonPhet = nonPhet;
            System.out.println( "nonPhet = " + nonPhet );
            notifyListeners();
        }
    }

    public String getSimulationName() {
        return "";
    }

    private ArrayList listeners = new ArrayList();

    public String getSource() {
        return source;
    }

    public String getNotes() {
        return notes;
    }

    public Properties toProperties() {
        Properties prop = new Properties();
        prop.setProperty( "nonphet", isNonPhet() + "" );
        prop.setProperty( "source", getSource() );
        prop.setProperty( "notes", getNotes() );
        prop.setProperty( "done", done + "" );
        return prop;
    }

    public void parseProperties( Properties prop, String imageName ) {
        this.imageName = imageName;
        this.nonPhet = Boolean.valueOf( prop.getProperty( "nonphet" ) ).booleanValue();
        this.source = prop.getProperty( "source" );
        this.notes = prop.getProperty( "notes" );
        this.done = Boolean.valueOf( prop.getProperty( "done" ) ).booleanValue();
    }

    public static ImageEntry createNewEntry( String imageName ) {
        ImageEntry imageEntry = new ImageEntry( imageName );
        imageEntry.nonPhet = false;
        imageEntry.source = "?";
        imageEntry.notes = "?";
        imageEntry.done = false;
        return imageEntry;
    }

    public String toString() {
        return toProperties().toString();
    }

    public static interface Listener {
        void nonPhetChanged();

        void sourceChanged();

        void notesChanged();

        void doneChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.nonPhetChanged();
        }
    }

    public void setSource( String source ) {
        if ( source.equals( "" ) ) {
            System.out.println( "ImageEntry.setSource" );
        }
        if ( !this.source.equals( source ) ) {
            this.source = "" + source;
            System.out.println( "set source to: this.source = " + this.source );
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.sourceChanged();
            }
        }
    }

    public void setNotes( String notes ) {
        if ( !this.notes.equals( notes ) ) {
            this.notes = "" + notes;
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.notesChanged();
            }
        }
    }

    public boolean isDone() {
        return done;
    }

    public void setDone( boolean done ) {
        if ( this.done != done ) {
            this.done = done;
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.doneChanged();
            }
        }
    }
}
