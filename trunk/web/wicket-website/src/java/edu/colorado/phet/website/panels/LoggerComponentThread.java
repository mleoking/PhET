package edu.colorado.phet.website.panels;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.wicket.Component;

import edu.colorado.phet.website.admin.deploy.WebsiteTranslationDeployServer;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.util.HtmlUtils;
import edu.colorado.phet.website.util.PageContext;

/**
 * A ComponentThread, but captures output on a logger and displays that as the default component.
 */
public abstract class LoggerComponentThread extends ComponentThread {

    private StringWriter writer = new StringWriter();
    private boolean error = false;
    private final Logger logger;

    protected LoggerComponentThread( Logger logger ) {
        this.logger = logger;
    }

    /**
     * Process what needs to be processed in this thread.
     *
     * @return True on success, false on failure
     * @throws IOException          Possibly
     * @throws InterruptedException Possibly
     */
    public abstract boolean process() throws IOException, InterruptedException;

    public Component getProgressComponent( String id, String rawText ) {
        return new RawLabel( id, rawText );
    }

    public Component getFinishedComponent( String id, String rawText ) {
        return new RawLabel( id, rawText );
    }

    public void onError() {

    }

    @Override
    public Component getComponent( String id, PageContext context ) {
        String output = writer.toString();
        output = HtmlUtils.encode( output ).replace( "\n", "<br/>" );

        String header;

        if ( isDone() ) {
            if ( error ) {
                header = "<strong style=\"color: #FF0000;\">Errors encountered</strong><br/>";
            }
            else {
                header = "<strong style=\"color: #008800;\">Completed without errors</strong><br/>";
            }
        }
        else {
            header = "<strong style=\"color: #0000FF;\">Processing, please wait.</strong><br/>updated";
        }

        header += " at " + ( new Date() ).toString() + "<br/>";

        String text = header + "<br/>" + output;

        if ( isDone() ) {
            return getFinishedComponent( id, text );
        }
        else {
            return getProgressComponent( id, text );
        }
    }

    @Override
    public void run() {
        // add an appender so we can capture the info
        WriterAppender appender = new WriterAppender( new PatternLayout(), writer );
        logger.addAppender( appender );

        try {
            error = !process();
        }
        catch( IOException e ) {
            e.printStackTrace();
            logger.error( "IOException", e );
            error = true;
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
            logger.error( "InterruptedException", e );
            error = true;
        }
        catch( RuntimeException e ) {
            e.printStackTrace();
            logger.error( "RuntimeException", e );
            error = true;
        }

        if ( error ) {
            onError();
        }

        finish();

        logger.info( "Finishing" );
        logger.removeAppender( appender );
    }
}
