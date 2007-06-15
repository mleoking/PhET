package edu.colorado.phet.media;

import edu.colorado.phet.common.phetcommon.view.util.PhetAudioClip;
import edu.colorado.phet.media.util.FileUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 3:38:43 AM
 */
public class MediaSoundApplication {
    private static JList list;

    public static void main( String[] args ) {
        File[] soundFiles = MediaFinder.getSoundFiles();
        ArrayList singleCopies = new ArrayList();
        for( int i = 0; i < soundFiles.length; i++ ) {
            File soundFile = soundFiles[i];
            if( !contains( singleCopies, soundFile ) ) {
                singleCopies.add( soundFile );
            }
        }
        System.out.println( "original = " + soundFiles.length );
        System.out.println( "without duplicates = " + singleCopies.size() );
        list = new JList( new Vector( singleCopies ) );
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        JFrame frame = new JFrame();
        JPanel panel = new JPanel( new BorderLayout() );
        frame.setContentPane( panel );
        panel.add( list, BorderLayout.CENTER );
        list.addMouseListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                if( e.getClickCount() >= 2 ) {
                    playSelected();
                }
            }
        } );
        JButton play = new JButton( new AbstractAction( "Play" ) {
            public void actionPerformed( ActionEvent e ) {
                playSelected();
            }
        } );
        panel.add( play, BorderLayout.SOUTH );
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.show();
    }

    private static void playSelected() {
        File file = (File)list.getSelectedValue();
        if( file.getAbsolutePath().toLowerCase().endsWith( ".au" ) ) {
            try {
                Applet.newAudioClip( file.toURL() ).play();
            }
            catch( MalformedURLException e ) {
                e.printStackTrace();
            }
        }
        else {
            try {
                PhetAudioClip phetAudioClip = new PhetAudioClip( file.toURL() );
                phetAudioClip.play();
            }
            catch( MalformedURLException e1 ) {
                e1.printStackTrace();
            }
        }
    }

    private static boolean contains( ArrayList fileList, File b ) {
        for( int i = 0; i < fileList.size(); i++ ) {
            File a = (File)fileList.get( i );
            try {
                if( FileUtils.contentEquals( a, b ) ) {
                    return true;
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
