package edu.colorado.phet.common.phetcommon.util.logging;

import javax.swing.*;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A logging appender that opens up a JFrame. Use Logger.addAppender() to have the specified logger (and its children)
 * log to this appender.
 *
 * @author Jonathan Olson
 */
public class JFrameAppender extends AppenderSkeleton {

    private JFrame frame;
    private JTextArea jTextArea;

    public JFrameAppender( String title ) {
        frame = new JFrame( title );
        jTextArea = new JTextArea( 30, 60 );
        frame.setContentPane( new JScrollPane( jTextArea ) );
        frame.pack();
    }

    public void setVisible( boolean visible ) {
        frame.setVisible( visible );
    }

    public JFrame getFrame() {
        return frame;
    }

    /*---------------------------------------------------------------------------*
    * logging implementation
    *----------------------------------------------------------------------------*/

    @Override
    protected void append( LoggingEvent event ) {
        jTextArea.append( this.layout.format( event ) );
        jTextArea.setCaretPosition( jTextArea.getText().length() );
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return true;
    }

    /*---------------------------------------------------------------------------*
    * test example
    *----------------------------------------------------------------------------*/

    public static void main( String[] args ) {
        JFrameAppender appender = new JFrameAppender( "test log" );
        appender.setVisible( true );
        appender.setLayout( new PatternLayout( "%d{DATE} %5p %25c{1} - %m%n" ) );

        Logger mainLogger = Logger.getLogger( JFrameAppender.class );
        mainLogger.addAppender( appender );

        mainLogger.debug( "debug message" );
        mainLogger.info( "info message" );
        mainLogger.warn( "warn message" );
        mainLogger.error( "error message" );
        mainLogger.fatal( "fatal message" );

        /*
          This is a "child" logger, since it starts with the already appended logger's name.
          Logger.getLogger( "edu.colorado.phet" ).addAppender() will cause any logging message for any loggers under
          edu.colorado.phet to be logged to this appender (and displayed in the window)
          */
        Logger otherLogger = Logger.getLogger( JFrameAppender.class.getCanonicalName() + ".OtherLogger" );

        otherLogger.info( "This is another logger" );
    }

}
