package edu.colorado.phet.unfuddle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.colorado.phet.unfuddle.process.BasicProcess;
import edu.colorado.phet.unfuddle.process.MyProcess;
import edu.colorado.phet.unfuddle.process.ThreadProcess;

/**
 * This is the main entry point for the Unfuddle Notifier.
 * <p/>
 * Created by: Sam
 * Feb 21, 2008 at 7:30:51 AM
 */
public class UnfuddleEmailNotifier {

    private final ProgramArgs args;
    private final UnfuddleAccount unfuddleAccount;
    private final UnfuddleCurl unfuddleCurl;
    private final Timer timer;
    private final JFrame running;
    private final JTextField minutes;
    private final boolean sendMail;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    public static interface Listener {
        void batchComplete();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyBatchComplete() {
        for ( Listener listener : listeners ) {
            listener.batchComplete();
        }
    }

    public UnfuddleEmailNotifier( ProgramArgs args )
            throws IOException, SAXException, ParserConfigurationException {

        this.args = args;
        this.sendMail = args.isSendMailEnabled(); // must be final for use in callbacks

        unfuddleAccount = new UnfuddleAccount( new File( args.getXmlDumpPath() ) );
        MyProcess myProcess = new ThreadProcess( new BasicProcess(), 1000 * 60 * 3 );
        unfuddleCurl = new UnfuddleCurl( myProcess, args.getUnfuddleUsername(), args.getUnfuddlePassword(), UnfuddleNotifierConstants.PHET_ACCOUNT_ID, args.getSvnTrunk() );

        running = new JFrame( "Process Unfuddle Changes" );
        running.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JPanel contentPanel = new JPanel();

        final JCheckBox jCheckBox = new JCheckBox( "running", true );
        contentPanel.add( jCheckBox );
//        final ProcessRecentChanges recentChanges = new ProcessRecentChanges( args );

        minutes = new JTextField( "4", 4 );
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
                processChangesDisplayExceptions( sendMail );
            }
        } );
        running.setContentPane( contentPanel );

        timer = new Timer( getTimerDelay(), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( jCheckBox.isSelected() ) {
                    processChangesDisplayExceptions( sendMail );
                }
            }
        } );

        timer.setInitialDelay( 0 );
        running.pack();
    }

    private int getTimerDelay() {
        final double v = Double.parseDouble( minutes.getText() );
//        System.out.println( "changed delay to = " + v + " minutes" );
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
        System.out.println( "Started update at " + new Date() );
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
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        } );
        t.start();
        long startTime = System.currentTimeMillis();
        long timeout = 1000 * 60 * 5;
        //TODO: better thread communication
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

        XMLObject events = new XMLObject( recent[0] );
        int e = events.getNodeCount( "event" );
        System.out.println( "Requested " + limit + " events, received: " + e );

        CompositeMessageHandler h = new CompositeMessageHandler();
        h.addMessageHandler( new PrintMessageHandler() );
        h.addMessageHandler( new EmailHandler( args.getEmailFromAddress(), args.getEmailServer(), args.getEmailUsername(), args.getEmailPassword(), new EmailList( unfuddleAccount, unfuddleCurl ), sendMail ) );
        IMessageHandler mh = new IgnoreDuplicatesMessageHandler( h, new File( args.getSvnTrunk() + "/util/unfuddle/data/handled.txt" ) ); //TODO separator is Windows specific
        ArrayList<String> results = new ArrayList<String>();
        for ( int i = e - 1; i >= 0; i-- ) {//reverse iterate to post notifications in chronological order
            XMLObject auditTrail = events.getNode( i, "event" );
            XMLObject record = auditTrail.getNode( "record" );
            XMLObject comment = record.getNode( "comment" );
            if ( comment != null ) {
                if ( comment.getTextContent( "parent-type" ).equals( "Ticket" ) ) {
                    try {
                        String result = mh.handleMessage( new TicketCommentMessage( comment, unfuddleAccount, unfuddleCurl ) );
                        results.add( result );
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
                        String result = mh.handleMessage( new TicketNewMessage( ticket, unfuddleAccount ) );
                        results.add( result );
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
                        UnfuddlePerson resolvedPerson = unfuddleAccount.getPersonForID( auditTrail.getTextContentAsInt( "person-id" ) );
                        int recordID = auditTrail.getTextContentAsInt( "record-id" );
                        String result = mh.handleMessage( new TicketResolvedMessage( ticket, unfuddleAccount, resolvedPerson, recordID ) );
                        results.add( result );
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

        for ( int i = 0; i < results.size(); i++ ) {
            System.out.println( "Result[" + i + "]: " + results.get( i ) );
        }
        System.out.println( "Finished update at " + new Date() + ", number of messages handled=" + results.size() + ", elapsed time=" + ( System.currentTimeMillis() - startTime ) / 1000.0 + " sec.  See results above." );
        notifyBatchComplete();
    }

    public static void main( final String[] args ) throws IOException {
        final ProgramArgs programArgs = new ProgramArgs( args );

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    UnfuddleEmailNotifier emailNotifier = new UnfuddleEmailNotifier( programArgs );
                    emailNotifier.start();
                    new UnfuddleCrashWorkaround( args, emailNotifier ).start();
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

    private static class UnfuddleCrashWorkaround {
        private String[] args;
        private UnfuddleEmailNotifier emailNotifier;
        private long lastBatchCompleteTime = System.currentTimeMillis();

        public UnfuddleCrashWorkaround( String[] args, final UnfuddleEmailNotifier emailNotifier ) {
            this.args = args;
            this.emailNotifier = emailNotifier;
            emailNotifier.addListener( new Listener() {
                public void batchComplete() {
                    lastBatchCompleteTime = System.currentTimeMillis();
                }
            } );
            Thread thread = new Thread( new Runnable() {
                public void run() {
                    while ( true ) {
                        try {
                            Thread.sleep( 10000 );
                        }
                        catch( InterruptedException e ) {
                            e.printStackTrace();
                        }
                        long timeSinceLastBatch = System.currentTimeMillis() - lastBatchCompleteTime;
                        System.out.println( "Minutes since last batch complete: " + timeSinceLastBatch/1000.0/60.0 );
                        if ( timeSinceLastBatch > emailNotifier.getTimerDelay() * 2.5 ) {//missed 2 or so
                            System.out.println( "It's been a long time since the email notifier finished a batch; perhaps it has halted" );
                            System.out.println( "Shutting down the process" );
                            System.exit( 0 );
                        }
                    }
                }
            } );
            thread.start();
        }

        public void start() {
            System.out.println( "Started Unfuddle Crash Workaround" );
        }
    }
}
