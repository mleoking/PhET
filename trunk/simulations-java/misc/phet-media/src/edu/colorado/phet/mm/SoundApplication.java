package edu.colorado.phet.mm;

import edu.colorado.phet.common.phetcommon.view.util.PhetAudioClip;
import edu.colorado.phet.mm.util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 3:38:43 AM
 */
public class SoundApplication {
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
        final JList list = new JList( new Vector( singleCopies ) );
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        JFrame frame = new JFrame();
        JPanel panel = new JPanel( new BorderLayout() );
        frame.setContentPane( panel );
        panel.add( list, BorderLayout.CENTER );
        JButton play = new JButton( new AbstractAction( "Play" ) {
            public void actionPerformed( ActionEvent e ) {
                File file = (File)list.getSelectedValue();
                try {
                    PhetAudioClip phetAudioClip = new PhetAudioClip( file.toURL() );
                    phetAudioClip.play();
                }
                catch( MalformedURLException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        panel.add( play, BorderLayout.SOUTH );
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.show();
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
