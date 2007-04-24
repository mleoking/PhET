package edu.colorado.phet.energyskatepark.model.physics;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 2, 2007
 * Time: 3:06:31 PM
 *
 */

public class DebugFrame extends JFrame {
    private JTextArea jta;

    public DebugFrame( String title ) throws HeadlessException {
        super( title );
        jta = new JTextArea( 10, 40 );
        setContentPane( new JScrollPane( jta ) );
        pack();
        setDefaultCloseOperation( EXIT_ON_CLOSE );
    }

    public void appendLine( String text ) {
        jta.append( text + System.getProperty( "line.separator" ) );
        jta.setCaretPosition( jta.getDocument().getLength() );
    }
}
