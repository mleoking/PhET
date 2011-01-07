// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.translationutility.test;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
This was a feasibility test to try to identify whether Java automatically converts strings to unicode escape when entered
 from something other ISO 8859-1 character encoding.

 To run this test, change fileLocation to something that works on your platform.

 Run the test to put some non-ISO code in the text area.  Then you can copy paste this to another text editing application (TA).
 Then paste back from the text from (TA) to the text area (possibly multiple times, so a change will be evident).
 This copy-out/copy-back is recommended in order to try to simulate using non ISO characters directly
 (to try to destry any unicode information that may have come along with the text genereated from Java).  This may be unnecessary.

 Then press return to save the file to a properties file specified in fileLocation.

 If the properties file comes out with unicode, that's good.  (One test I ran had this result).
 Sam Reid
 11-9-2007
 */
public class TestUnicodeEscapes {
    final static File fileLocation = new File( "C:/phet-out-tmp.txt" );
    public static void main( String[] args ) throws IOException {
        String text = "\u0637\u0642\u0645 \u0627\u062F\u0648\u0627\u062A";
        System.out.println( "text = " + text );
//        JOptionPane.showInputDialog( text );
        final JTextArea te = new JTextArea( text );
        JFrame f = new JFrame();
        f.setContentPane( te );
        f.pack();
        f.setVisible( true );

        final Properties properties = new Properties();
        properties.put( "key", text );

        te.addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    System.out.println( "TestUnicodeEscapes.keyPressed" );
                    try {
                        properties.put( "from.textfield", te.getText() );
                        properties.store( new FileOutputStream( fileLocation, false ), null );
                    }
                    catch( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );


        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
