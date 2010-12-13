package edu.colorado.phet.website.panels;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

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
        init();
    }

    protected LoggerComponentThread( List<Logger> loggers ) {
        this.loggers = loggers;
        setObject( writer );
        init();
    }

    private void init() {
        setObject( writer );
        appender.addFilter( new ThreadFilter() );
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

    private class ThreadFilter extends Filter {
        @Override
        public int decide( LoggingEvent event ) {
            if ( Thread.currentThread().getName().equals( event.getThreadName() ) ) {
                return Filter.NEUTRAL;
            }
            else {
                return Filter.DENY;
            }
        }
    }
}
