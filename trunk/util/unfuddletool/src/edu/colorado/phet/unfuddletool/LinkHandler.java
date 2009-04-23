package edu.colorado.phet.unfuddletool;

import java.io.IOException;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class LinkHandler implements HyperlinkListener {
    public void hyperlinkUpdate( HyperlinkEvent hyperlinkEvent ) {

        if ( hyperlinkEvent.getEventType() != HyperlinkEvent.EventType.ACTIVATED ) {
            return;
        }

        System.out.println( "Opening " + hyperlinkEvent.getDescription() );

        try {
            Runtime.getRuntime().exec( new String[]{ Configuration.browser() , hyperlinkEvent.getDescription()} );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
