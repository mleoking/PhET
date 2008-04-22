package edu.colorado.phet.unfuddle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

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
        unfuddleAccount = new UnfuddleAccount( new File( args[7] ) );
        unfuddleCurl = new UnfuddleCurl( args[0], args[1], UnfuddleNotifierConstants.PHET_ACCOUNT_ID );
        final boolean sendMail = Boolean.parseBoolean( args[8] );

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
                processChangesDisplayExceptions( sendMail );
            }
        } );
        running.setContentPane( contentPanel );


        timer = new Timer( getTimerDelay(), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( jCheckBox.isSelected() ) {
                    processChangesDisplayExceptions( sendMail );
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


    private void start() {
        timer.start();
        running.setVisible( true );
    }

    private void processChangesDisplayExceptions( boolean sendMail ) {
        try {
            processChanges( sendMail );
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

    private void processChanges( boolean sendMail ) throws IOException, SAXException, ParserConfigurationException {
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
//        h.addMessageHandler( new EmailHandler( args[2], args[3], args[4], args[5], new ReadEmailList( unfuddleAccount, unfuddleCurl ), true ) );
        h.addMessageHandler( new EmailHandler( args[2], args[3], args[4], args[5], new ReadEmailList( unfuddleAccount, unfuddleCurl ), sendMail ) );
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
                        mh.handleMessage( new TicketCommentMessage( comment, unfuddleAccount, unfuddleCurl ) );
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
                        mh.handleMessage( new TicketNewMessage( ticket, unfuddleAccount ) );
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


    // args example:
    // @unfuddle-id@ @unfuddle-password@ phetmail@comcast.net smtp.comcast.net phetmail @phet-mail-password@ C:/phet/svn C:/phet/unfuddled.xml true
    public static void main( String[] args ) throws IOException, SAXException, ParserConfigurationException {
        if ( args.length == 1 && new File( args[0] ).exists() ) {
            String text = FileUtils.loadFileAsString( new File( args[0] ) );
            StringTokenizer st = new StringTokenizer( text, " " );
            args = new String[9];
            for ( int i = 0; i < args.length; i++ ) {
                args[i] = st.nextToken();
            }
        }
        else if ( args.length == 0 ) {
            args = new String[9];
            args[0] = JOptionPane.showInputDialog( "Unfuddle ID" );
            args[1] = JOptionPane.showInputDialog( "Unfuddle Password" );
            args[2] = "phetmail@comcast.net";
            args[3] = "smtp.comcast.net";
            args[4] = "phetmail";
            args[5] = JOptionPane.showInputDialog( "Phetmail password" );
            args[6] = JOptionPane.showInputDialog( "SVN-trunk dir, e.g. C:/phet/svn/trunk" );
            args[7] = JOptionPane.showInputDialog( "Path to phet-unfuddled.xml, e.g. C:/phet-unfuddled.xml" );
            args[8] = JOptionPane.showInputDialog( "Send mail? true=yes false=test only", "true" );
        }
        final String[] out = args;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    runMain( out );
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
}
