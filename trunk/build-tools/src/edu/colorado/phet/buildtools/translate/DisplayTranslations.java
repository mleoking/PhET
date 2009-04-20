package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.*;

/**
 * This utility displays all translations given in the .properties file specificed as args[0].
 * It is useful for visualizing translations specificed in unicode escapes.
 */
public class DisplayTranslations {
    public static void main( final String[] args ) throws IOException {
        Properties p = new Properties();
        p.load( new FileReader( new File( args[0] ) ) );

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
