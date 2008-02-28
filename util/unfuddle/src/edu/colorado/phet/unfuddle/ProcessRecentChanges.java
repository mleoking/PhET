package edu.colorado.phet.unfuddle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Created by: Sam
 * Feb 21, 2008 at 7:30:51 AM
 */
public class ProcessRecentChanges {
    private String[] args;
    private UnfuddleAccount unfuddleAccount;
    private UnfuddleCurl unfuddleCurl;
    private Timer timer;
    private JFrame running;
    private JTextField minutes;
    public static String SVN_TRUNK;

    public ProcessRecentChanges( String[] args ) throws IOException, SAXException, ParserConfigurationException {
        this.args = args;
        unfuddleAccount = new UnfuddleAccount( new File( SVN_TRUNK + "\\util\\unfuddle\\data\\phet.unfuddled.20080221150731.xml" ) );
        unfuddleCurl = new UnfuddleCurl( args[0], args[1], UnfuddleCurl.PHET_PROJECT_ID );

        running = new JFrame( "Process Unfuddle Changes" );
        running.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JPanel contentPanel = new JPanel();

        final JCheckBox jCheckBox = new JCheckBox( "running", true );
        contentPanel.add( jCheckBox );
//        final ProcessRecentChanges recentChanges = new ProcessRecentChanges( args );

        minutes = new JTextField( "10", 4 );
        minutes.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timer.setDelay( getTimerDelay() );
            }
        } );
        JLabel minutesLabel = new JLabel( "minutes" );
        contentPanel.add( minutes );
        contentPanel.add( minutesLabel );

        JButton updateNow = new JButton( "Update Now" );
        contentPanel.add( updateNow );
        updateNow.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                processRecentChanges( ProcessRecentChanges.this );
                processChangesDisplayExceptions();
            }
        } );
        running.setContentPane( contentPanel );


        timer = new Timer( getTimerDelay(), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( jCheckBox.isSelected() ) {
                    processChangesDisplayExceptions();
//                    processRecentChanges( ProcessRecentChanges.this );
                }
            }
        } );

        timer.setInitialDelay( 0 );
        running.pack();
    }

    private int getTimerDelay() {
        final double v = Double.parseDouble( minutes.getText() );
        System.out.println( "changed delay to = " + v + " minutes" );
        return (int) ( 60 * 1000 * v );
    }

    public static void main( final String[] args ) throws IOException, SAXException, ParserConfigurationException {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    runMain( args );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                catch( SAXException e ) {
                    e.printStackTrace();
                }
                catch( ParserConfigurationException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    private static void runMain( String[] args ) throws IOException, SAXException, ParserConfigurationException {
        SVN_TRUNK = args[6];
        new ProcessRecentChanges( args ).start();
    }

    private void start() {
        timer.start();
        running.setVisible( true );
    }

    private void processChangesDisplayExceptions() {
        try {
            processChanges();
        }
        catch( IOException e1 ) {
            e1.printStackTrace();
        }
        catch( SAXException e1 ) {
            e1.printStackTrace();
        }
        catch( ParserConfigurationException e1 ) {
            e1.printStackTrace();
        }
    }

    private void processChanges() throws IOException, SAXException, ParserConfigurationException {
        final int limit = 20;
        final String[] recent = new String[]{null};
        Thread t = new Thread( new Runnable() {
            public void run() {
                try {
                    recent[0] = unfuddleCurl.readString( "activity.xml?limit=" + limit );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
        t.start();
        long startTime = System.currentTimeMillis();
        long timeout = 1000 * 60 * 5;
        //todo: better thread communication
        while ( recent[0] == null && System.currentTimeMillis() - startTime < timeout ) {
            try {
                Thread.sleep( 5000 );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        final long time = System.currentTimeMillis() - startTime;
        if ( time >= timeout ) {
            System.out.println( "Timeout: (" + time + " ms)" );
            return;
        }

//        String recent = STORED_XML;

        XMLObject events = new XMLObject( recent[0] );
        int e = events.getNodeCount( "event" );
        System.out.println( "Requested " + limit + " events, received: " + e );

        CompositeMessageHandler h = new CompositeMessageHandler();
        h.addMessageHandler( new PrintMessageHandler() );
        h.addMessageHandler( new EmailHandler( args[2], args[3], args[4], args[5], new ReadEmailList( unfuddleAccount, unfuddleCurl ), true ) );
        MessageHandler mh = new IgnoreDuplicatesMessageHandler( h, new File( SVN_TRUNK + "\\util\\unfuddle\\data\\handled.txt" ) );
//        MessageHandler mh = h;
        int handled = 0;
        for ( int i = e - 1; i >= 0; i-- ) {//reverse iterate to post notifications in chronological order
            XMLObject auditTrail = events.getNode( i, "event" );
            XMLObject record = auditTrail.getNode( "record" );

            XMLObject comment = record.getNode( "comment" );
            if ( comment != null ) {
                if ( comment.getTextContent( "parent-type" ).equals( "Ticket" ) ) {
                    try {
                        mh.handleMessage( new NewCommentMessage( comment, unfuddleAccount, unfuddleCurl ) );
                        handled++;
                    }
                    catch( MessagingException e1 ) {
                        e1.printStackTrace();
                    }
                }
                else {
                    System.out.println( "Skipping unknown parent type: " + comment.getTextContent( "parent-type" ) );
                }
            }
            else if ( auditTrail.getTextContent( "summary" ).equals( "Ticket Created" ) ) {
                XMLObject ticket = record.getNode( "ticket" );
                if ( ticket != null ) {
                    try {
                        mh.handleMessage( new NewTicketMessage( ticket, unfuddleAccount ) );
                        handled++;
                    }
                    catch( MessagingException e1 ) {
                        e1.printStackTrace();
                    }
                }
            }
            else if ( auditTrail.getTextContent( "summary" ).toLowerCase().startsWith( "ticket resolved" ) ) {
                XMLObject ticket = record.getNode( "ticket" );
                if ( ticket != null ) {
                    try {
                        String resolvedBy = unfuddleAccount.getPersonForID( auditTrail.getTextContentAsInt( "person-id" ) );
                        int recordID = auditTrail.getTextContentAsInt( "record-id" );
                        mh.handleMessage( new TicketResolvedMessage( ticket, unfuddleAccount, resolvedBy, recordID ) );
                        handled++;
                    }
                    catch( MessagingException e1 ) {
                        e1.printStackTrace();
                    }
                }
            }
            else {
                System.out.println( "Skipping unknown type: " + auditTrail.getTextContent( "summary" ) );
            }
        }
        System.out.println( "Finished update, number of messages handled=" + handled );
    }

}
