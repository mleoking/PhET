/* Copyright 2008, University of Colorado */
package edu.colorado.phet.common.phetcommon.util.logging;

import javax.swing.*;

public class JFrameLogger implements ILogger {
    private JFrame frame;
    private JTextArea jTextArea;

    public JFrameLogger( String title ) {
        frame = new JFrame( title );
        jTextArea = new JTextArea( 30, 60 );
        frame.setContentPane( new JScrollPane( jTextArea ) );
        frame.pack();
    }

    public void log( String text ) {
        jTextArea.append( text + "\n" );
        jTextArea.setCaretPosition( jTextArea.getText().length() );
    }

    public void setVisible( boolean visible ) {
        frame.setVisible( visible );
    }

    public static void main( String[] args ) {
        JFrameLogger logger = new JFrameLogger( "test log" );
        logger.setVisible( true );
        logger.log( "testing" );
    }
}
