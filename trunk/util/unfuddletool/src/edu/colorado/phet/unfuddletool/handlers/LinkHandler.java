package edu.colorado.phet.unfuddletool.handlers;

import java.awt.*;
import java.io.IOException;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class LinkHandler implements HyperlinkListener {
    public void hyperlinkUpdate( HyperlinkEvent hyperlinkEvent ) {

        if ( hyperlinkEvent.getEventType() != HyperlinkEvent.EventType.ACTIVATED ) {
            return;
        }

        String link = hyperlinkEvent.getDescription();

        if ( link.startsWith( "update:" ) ) {
            int ticketId = Integer.valueOf( link.substring( 7 ) );

            System.out.println( "Manually attempting to update ticket " + ticketId );

            TicketHandler.getTicketHandler().requestTicketUpdate( ticketId, new Date() );

            return;
        }

        System.out.println( "Opening " + link );
        showInBrowser( hyperlinkEvent.getDescription(), null );
    }

    // from http://forums.sun.com/thread.jspa?threadID=679673
    static boolean showInBrowser( String url, Frame frame ) {
        //minimizes the app
        if ( frame != null ) {
            frame.setExtendedState( JFrame.ICONIFIED );
        }

        String os = System.getProperty( "os.name" ).toLowerCase();
        Runtime rt = Runtime.getRuntime();
        try {
            if ( os.indexOf( "win" ) >= 0 ) {
                String[] cmd = new String[4];
                cmd[0] = "cmd.exe";
                cmd[1] = "/C";
                cmd[2] = "start";
                cmd[3] = url;
                rt.exec( cmd );
            }
            else if ( os.indexOf( "mac" ) >= 0 ) {
                rt.exec( "open " + url );
            }
            else {
                //prioritized 'guess' of users' preference
                String[] browsers = {"firefox", "epiphany", "mozilla", "konqueror",
                        "netscape", "opera", "links", "lynx"};

                StringBuffer cmd = new StringBuffer();
                for ( int i = 0; i < browsers.length; i++ ) {
                    cmd.append( ( i == 0 ? "" : " || " ) + browsers[i] + " \"" + url + "\" " );
                }

                rt.exec( new String[]{"sh", "-c", cmd.toString()} );
                //rt.exec("firefox http://www.google.com");
                //System.out.println(cmd.toString());

            }
        }
        catch( IOException e ) {
            e.printStackTrace();
            JOptionPane.showMessageDialog( frame,
                                           "\n\n The system failed to invoke your default web browser while attempting to access: \n\n " + url + "\n\n",
                                           "Browser Error",
                                           JOptionPane.WARNING_MESSAGE );

            return false;
        }
        return true;
    }

}
