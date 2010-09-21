package edu.colorado.phet.website.panels;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;

import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.util.HtmlUtils;
import edu.colorado.phet.website.util.PageContext;

/**
 * A ComponentThread, but captures output from an object toString and displays that as the default component.
 */
public abstract class ObjectComponentThread extends ComponentThread {

    private boolean error = false;
    private Object obj;

    private static final Logger logger = Logger.getLogger( ObjectComponentThread.class );

    protected ObjectComponentThread() {
    }

    protected ObjectComponentThread( Object obj ) {
        this.obj = obj;
    }

    protected void setObject( Object obj ) {
        this.obj = obj;
    }

    /**
     * Process what needs to be processed in this thread.
     *
     * @return True on success, false on failure
     * @throws java.io.IOException  Possibly
     * @throws InterruptedException Possibly
     */
    public abstract boolean process() throws IOException, InterruptedException;

    public abstract void startListening();

    public abstract void stopListening();

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
        String output = obj.toString();
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
        startListening();
        
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

        stopListening();
    }

    public boolean didError() {
        return error;
    }
}