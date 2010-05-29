package edu.colorado.phet.website.panels;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

/**
 * A ComponentThread, but captures output from multiple loggers and displays that as the default component.
 */
public abstract class LoggerComponentThread extends ObjectComponentThread {

    private StringWriter writer = new StringWriter();
    private WriterAppender appender = new WriterAppender( new PatternLayout( "%d{DATE} %5p %25c{1} - %m%n" ), writer );
    private final List<Logger> loggers;

    protected LoggerComponentThread( Logger logger ) {
        loggers = new LinkedList<Logger>();
        loggers.add( logger );
        setObject( writer );
    }

    protected LoggerComponentThread( List<Logger> loggers ) {
        this.loggers = loggers;
        setObject( writer );
    }

    @Override
    public void startListening() {
        for ( Logger logger : loggers ) {
            logger.addAppender( appender );
        }
    }

    @Override
    public void stopListening() {
        for ( Logger logger : loggers ) {
            logger.removeAppender( appender );
        }
    }
}
