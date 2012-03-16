package edu.colorado.phet.buildtools.translate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This utility displays all translations given in the .properties file specificed as args[0].
 * It is useful for visualizing translations specificed in unicode escapes.
 */
public class DisplayTranslations {
    public static void main( final String[] args ) throws IOException {
        if ( args.length != 1 ) {
            System.out.println( DisplayTranslations.class.getName() + ": missing command line arg" );
            System.exit( 1 );
        }
        Properties p = new Properties();
        p.load( new FileInputStream( args[0] ) );

        String s = "";
        Set set = p.keySet();
        for ( Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            Object o = iterator.next();
            s += p.get( o ) + "\n";
        }
        JTextArea jTextArea = new JTextArea( s );
        JFrame frame = new JFrame();
        frame.setContentPane( new JScrollPane( jTextArea ) );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }
}
