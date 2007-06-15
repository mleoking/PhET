package edu.colorado.phet.mm;


import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 17, 2006
 * Time: 12:37:02 AM
 * Copyright (c) Aug 17, 2006 by Sam Reid
 */
public class ImageEntry {
    private File file;
    private boolean nonPhet = false;
    private String source = "";
    private String notes = "";
    private boolean done = false;
    private String path;

    public ImageEntry( String path ) {
        this.path = path;
        this.file = new File( path );
    }

    public String getName() {
        return file.getName();
    }

    public boolean equals( Object obj ) {
        if( obj instanceof ImageEntry ) {
            ImageEntry imageEntry = (ImageEntry)obj;
            return imageEntry.getFile().getName().equals( file.getName() );// && imageEntry.getFile().length() == file.length();
        }
        return false;
    }

    public BufferedImage toImage() {
        try {
            return ImageLoader.loadBufferedImage( file.toURL() );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public void setFile( File file ) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public boolean isNonPhet() {
        return nonPhet;
    }

    public boolean needsAttention() {
        return done;
    }

    public void setNonPhet( boolean nonPhet ) {
        if( nonPhet != this.nonPhet ) {
            this.nonPhet = nonPhet;
            System.out.println( "nonPhet = " + nonPhet );
            notifyListeners();
        }
    }

    public String getSimulationName() {
        String key = "phet-mm-temp\\";
        String a = file.getAbsolutePath().substring( file.getAbsolutePath().lastIndexOf( key ) + key.length() );
        return a.substring( 0, a.indexOf( '\\' ) );
    }

    private ArrayList listeners = new ArrayList();

    public String getSource() {
        return source;
    }

    public String getNotes() {
        return notes;
    }

    public String getPath() {
        return path;
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
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.nonPhetChanged();
        }
    }

    public void setSource( String source ) {
        if( source.equals( "" ) ) {
            System.out.println( "ImageEntry.setSource" );
        }
        if( !this.source.equals( source ) ) {
            this.source = "" + source;
            System.out.println( "set source to: this.source = " + this.source );
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.sourceChanged();
            }
        }
    }

    public void setNotes( String notes ) {
        if( !this.notes.equals( notes ) ) {
            this.notes = "" + notes;
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.notesChanged();
            }
        }
    }

    public boolean isDone() {
        return done;
    }

    public void setDone( boolean done ) {
        if( this.done != done ) {
            this.done = done;
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.doneChanged();
            }
        }
    }
}
