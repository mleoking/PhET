package edu.colorado.phet.unfuddle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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

    public ProcessRecentChanges( String[] args ) throws IOException, SAXException, ParserConfigurationException {
        this.args = args;
        unfuddleAccount = new UnfuddleAccount( new File( "C:\\reid\\phet\\svn\\trunk\\team\\reids\\unfuddle\\data\\phet.unfuddled.20080221150731.xml" ) );
        unfuddleCurl = new UnfuddleCurl( args[0], args[1], UnfuddleCurl.PHET_PROJECT_ID );

    }

    public static void main( String[] args ) throws IOException, SAXException, ParserConfigurationException {
        JFrame running = new JFrame();
        final JCheckBox jCheckBox = new JCheckBox( "running", true );
        running.setContentPane( jCheckBox );
        final ProcessRecentChanges recentChanges = new ProcessRecentChanges( args );
        Timer timer = new Timer( 30 * 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( jCheckBox.isSelected() ) {
                    try {
                        recentChanges.processChanges();
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
            }
        } );
        timer.setInitialDelay( 0 );
        timer.start();
        running.pack();
        running.setVisible( true );
    }

    private void processChanges() throws IOException, SAXException, ParserConfigurationException {
        String recent = unfuddleCurl.readString( "activity.xml?limit=10" );
//        String recent = STORED_XML;

        XMLObject events = new XMLObject( recent );
        int e = events.getNodeCount( "audit-trail" );
        System.out.println( "num events=" + e );

        CompositeMessageHandler h = new CompositeMessageHandler();
        h.addMessageHandler( new PrintMessageHandler() );
        h.addMessageHandler( new EmailHandler( args[2], args[3], new ReadEmailList( unfuddleAccount, unfuddleCurl ), true ) );
        MessageHandler mh = new IgnoreDuplicatesMessageHandler( h, new File( "C:\\reid\\phet\\svn\\trunk\\team\\reids\\unfuddle\\data\\handled.txt" ) );
//        MessageHandler mh = h;

        for ( int i = 0; i < e; i++ ) {
            XMLObject auditTrail = events.getNode( i, "audit-trail" );
            XMLObject record = auditTrail.getNode( "record" );

            XMLObject comment = record.getNode( "comment" );
            if ( comment != null ) {
                if ( comment.getTextContent( "parent-type" ).equals( "Ticket" ) ) {
                    final NewCommentMessage message = new NewCommentMessage( comment, unfuddleAccount, unfuddleCurl );
                    System.out.println( "message = " + message );
                    mh.handleMessage( message );
                }
                else {
                    System.out.println( "Skipping unknown parent type: " + comment.getTextContent( "parent-type" ) );
                }
            }
            else if ( auditTrail.getTextContent( "summary" ).equals( "Ticket Created" ) ) {
                XMLObject ticket = record.getNode( "ticket" );
                if ( ticket != null ) {
                    mh.handleMessage( new NewTicketMessage( ticket, unfuddleAccount ) );
                }
            }
            else {
                System.out.println( "Skipping unknown type: " + auditTrail.getTextContent( "summary" ) );
            }
        }
    }

    private static final String STORED_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                             "<events type=\"array\">\n" +
                                             "  <audit-trail>\n" +
                                             "    <description></description>\n" +
                                             "    <event>create</event>\n" +
                                             "    <person-id type=\"integer\">12197</person-id>\n" +
                                             "    <record-id type=\"integer\">145970</record-id>\n" +
                                             "    <record-type>Ticket</record-type>\n" +
                                             "    <summary>Ticket Created</summary>\n" +
                                             "    <project-id>9404</project-id>\n" +
                                             "    <record>\n" +
                                             "      <ticket>\n" +
                                             "        <assignee-id type=\"integer\"></assignee-id>\n" +
                                             "        <component-id type=\"integer\">21855</component-id>\n" +
                                             "        <description>Could add functionality that would allow e.g. closing unfuddle tickets from SVN commit message.</description>\n" +
                                             "        <due-on type=\"date\"></due-on>\n" +
                                             "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                                             "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                                             "        <id type=\"integer\">145970</id>\n" +
                                             "        <milestone-id type=\"integer\"></milestone-id>\n" +
                                             "        <number type=\"integer\">216</number>\n" +
                                             "        <priority>2</priority>\n" +
                                             "        <project-id type=\"integer\">9404</project-id>\n" +
                                             "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                                             "        <resolution></resolution>\n" +
                                             "        <resolution-description></resolution-description>\n" +
                                             "        <severity-id type=\"integer\"></severity-id>\n" +
                                             "        <status>new</status>\n" +
                                             "        <summary>Add cron job to process SVN messages for unfuddle</summary>\n" +
                                             "        <version-id type=\"integer\"></version-id>\n" +
                                             "        <created-at>2008-02-20T21:16:00-08:00</created-at>\n" +
                                             "        <updated-at>2008-02-20T21:16:00-08:00</updated-at>\n" +
                                             "      </ticket>\n" +
                                             "    </record>\n" +
                                             "    <created-at>2008-02-20T21:16:04-08:00</created-at>\n" +
                                             "  </audit-trail>\n" +
                                             "  <audit-trail>\n" +
                                             "    <description></description>\n" +
                                             "    <event>create</event>\n" +
                                             "    <person-id type=\"integer\">12197</person-id>\n" +
                                             "    <record-id type=\"integer\">145967</record-id>\n" +
                                             "    <record-type>Ticket</record-type>\n" +
                                             "    <summary>Ticket Created</summary>\n" +
                                             "    <project-id>9404</project-id>\n" +
                                             "    <record>\n" +
                                             "      <ticket>\n" +
                                             "        <assignee-id type=\"integer\">12197</assignee-id>\n" +
                                             "        <component-id type=\"integer\">13435</component-id>\n" +
                                             "        <description>We discussed adding the Walls to Sound and Light tabs in Wave Interference, but I can't remember whether we decided to go ahead with this.</description>\n" +
                                             "        <due-on type=\"date\"></due-on>\n" +
                                             "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                                             "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                                             "        <id type=\"integer\">145967</id>\n" +
                                             "        <milestone-id type=\"integer\"></milestone-id>\n" +
                                             "        <number type=\"integer\">215</number>\n" +
                                             "        <priority>3</priority>\n" +
                                             "        <project-id type=\"integer\">9404</project-id>\n" +
                                             "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                                             "        <resolution></resolution>\n" +
                                             "        <resolution-description></resolution-description>\n" +
                                             "        <severity-id type=\"integer\"></severity-id>\n" +
                                             "        <status>new</status>\n" +
                                             "        <summary>add wall barriers to all tabs?</summary>\n" +
                                             "        <version-id type=\"integer\"></version-id>\n" +
                                             "        <created-at>2008-02-20T21:12:56-08:00</created-at>\n" +
                                             "        <updated-at>2008-02-20T21:12:56-08:00</updated-at>\n" +
                                             "      </ticket>\n" +
                                             "    </record>\n" +
                                             "    <created-at>2008-02-20T21:13:04-08:00</created-at>\n" +
                                             "  </audit-trail>\n" +
                                             "  <audit-trail>\n" +
                                             "    <description></description>\n" +
                                             "    <event>close</event>\n" +
                                             "    <person-id type=\"integer\">12197</person-id>\n" +
                                             "    <record-id type=\"integer\">144289</record-id>\n" +
                                             "    <record-type>Ticket</record-type>\n" +
                                             "    <summary>Ticket Closed</summary>\n" +
                                             "    <project-id>9404</project-id>\n" +
                                             "    <record>\n" +
                                             "      <ticket>\n" +
                                             "        <assignee-id type=\"integer\">12197</assignee-id>\n" +
                                             "        <component-id type=\"integer\">13435</component-id>\n" +
                                             "        <description></description>\n" +
                                             "        <due-on type=\"date\"></due-on>\n" +
                                             "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                                             "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                                             "        <id type=\"integer\">144289</id>\n" +
                                             "        <milestone-id type=\"integer\"></milestone-id>\n" +
                                             "        <number type=\"integer\">203</number>\n" +
                                             "        <priority>5</priority>\n" +
                                             "        <project-id type=\"integer\">9404</project-id>\n" +
                                             "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                                             "        <resolution>fixed</resolution>\n" +
                                             "        <resolution-description>walls disappear in side view</resolution-description>\n" +
                                             "        <severity-id type=\"integer\"></severity-id>\n" +
                                             "        <status>closed</status>\n" +
                                             "        <summary>barrier should disappear in side-view for all tabs</summary>\n" +
                                             "        <version-id type=\"integer\"></version-id>\n" +
                                             "        <created-at>2008-02-19T07:34:25-08:00</created-at>\n" +
                                             "        <updated-at>2008-02-20T21:11:35-08:00</updated-at>\n" +
                                             "      </ticket>\n" +
                                             "    </record>\n" +
                                             "    <created-at>2008-02-20T21:11:36-08:00</created-at>\n" +
                                             "  </audit-trail>\n" +
                                             "  <audit-trail>\n" +
                                             "    <description>walls disappear in side view</description>\n" +
                                             "    <event>resolve</event>\n" +
                                             "    <person-id type=\"integer\">12197</person-id>\n" +
                                             "    <record-id type=\"integer\">144289</record-id>\n" +
                                             "    <record-type>Ticket</record-type>\n" +
                                             "    <summary>Ticket Resolved as Fixed</summary>\n" +
                                             "    <project-id>9404</project-id>\n" +
                                             "    <record>\n" +
                                             "      <ticket>\n" +
                                             "        <assignee-id type=\"integer\">12197</assignee-id>\n" +
                                             "        <component-id type=\"integer\">13435</component-id>\n" +
                                             "        <description></description>\n" +
                                             "        <due-on type=\"date\"></due-on>\n" +
                                             "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                                             "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                                             "        <id type=\"integer\">144289</id>\n" +
                                             "        <milestone-id type=\"integer\"></milestone-id>\n" +
                                             "        <number type=\"integer\">203</number>\n" +
                                             "        <priority>5</priority>\n" +
                                             "        <project-id type=\"integer\">9404</project-id>\n" +
                                             "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                                             "        <resolution>fixed</resolution>\n" +
                                             "        <resolution-description>walls disappear in side view</resolution-description>\n" +
                                             "        <severity-id type=\"integer\"></severity-id>\n" +
                                             "        <status>closed</status>\n" +
                                             "        <summary>barrier should disappear in side-view for all tabs</summary>\n" +
                                             "        <version-id type=\"integer\"></version-id>\n" +
                                             "        <created-at>2008-02-19T07:34:25-08:00</created-at>\n" +
                                             "        <updated-at>2008-02-20T21:11:35-08:00</updated-at>\n" +
                                             "      </ticket>\n" +
                                             "    </record>\n" +
                                             "    <created-at>2008-02-20T21:11:35-08:00</created-at>\n" +
                                             "  </audit-trail>\n" +
                                             "  <audit-trail>\n" +
                                             "    <description></description>\n" +
                                             "    <event>create</event>\n" +
                                             "    <person-id type=\"integer\">12197</person-id>\n" +
                                             "    <record-id type=\"integer\">111779</record-id>\n" +
                                             "    <record-type>Comment</record-type>\n" +
                                             "    <summary>Comment Created</summary>\n" +
                                             "    <project-id>9404</project-id>\n" +
                                             "    <record>\n" +
                                             "      <comment>\n" +
                                             "        <author-id type=\"integer\">12197</author-id>\n" +
                                             "        <body>Also it would be nice to understand what causes this problem in the first place.</body>\n" +
                                             "        <id type=\"integer\">111779</id>\n" +
                                             "        <parent-id type=\"integer\">145911</parent-id>\n" +
                                             "        <parent-type>Ticket</parent-type>\n" +
                                             "        <created-at>2008-02-20T19:48:46-08:00</created-at>\n" +
                                             "        <updated-at>2008-02-20T19:48:46-08:00</updated-at>\n" +
                                             "      </comment>\n" +
                                             "    </record>\n" +
                                             "    <created-at>2008-02-20T19:48:46-08:00</created-at>\n" +
                                             "  </audit-trail>\n" +
                                             "  <audit-trail>\n" +
                                             "    <description></description>\n" +
                                             "    <event>create</event>\n" +
                                             "    <person-id type=\"integer\">12197</person-id>\n" +
                                             "    <record-id type=\"integer\">145916</record-id>\n" +
                                             "    <record-type>Ticket</record-type>\n" +
                                             "    <summary>Ticket Created</summary>\n" +
                                             "    <project-id>9404</project-id>\n" +
                                             "    <record>\n" +
                                             "      <ticket>\n" +
                                             "        <assignee-id type=\"integer\">12196</assignee-id>\n" +
                                             "        <component-id type=\"integer\">19955</component-id>\n" +
                                             "        <description>Add log file output that would indicate the kind of error we are seeing now.</description>\n" +
                                             "        <due-on type=\"date\"></due-on>\n" +
                                             "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                                             "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                                             "        <id type=\"integer\">145916</id>\n" +
                                             "        <milestone-id type=\"integer\"></milestone-id>\n" +
                                             "        <number type=\"integer\">214</number>\n" +
                                             "        <priority>3</priority>\n" +
                                             "        <project-id type=\"integer\">9404</project-id>\n" +
                                             "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                                             "        <resolution></resolution>\n" +
                                             "        <resolution-description></resolution-description>\n" +
                                             "        <severity-id type=\"integer\"></severity-id>\n" +
                                             "        <status>new</status>\n" +
                                             "        <summary>Add log file output for installer builder process to help identify problems</summary>\n" +
                                             "        <version-id type=\"integer\"></version-id>\n" +
                                             "        <created-at>2008-02-20T19:48:06-08:00</created-at>\n" +
                                             "        <updated-at>2008-02-20T19:48:06-08:00</updated-at>\n" +
                                             "      </ticket>\n" +
                                             "    </record>\n" +
                                             "    <created-at>2008-02-20T19:48:08-08:00</created-at>\n" +
                                             "  </audit-trail>\n" +
                                             "  <audit-trail>\n" +
                                             "    <description></description>\n" +
                                             "    <event>create</event>\n" +
                                             "    <person-id type=\"integer\">12197</person-id>\n" +
                                             "    <record-id type=\"integer\">110797</record-id>\n" +
                                             "    <record-type>Comment</record-type>\n" +
                                             "    <summary>Comment Created</summary>\n" +
                                             "    <project-id>9404</project-id>\n" +
                                             "    <record>\n" +
                                             "      <comment>\n" +
                                             "        <author-id type=\"integer\">12197</author-id>\n" +
                                             "        <body>This should address SWF files as well as JAR files.</body>\n" +
                                             "        <id type=\"integer\">110797</id>\n" +
                                             "        <parent-id type=\"integer\">144376</parent-id>\n" +
                                             "        <parent-type>Ticket</parent-type>\n" +
                                             "        <created-at>2008-02-19T09:42:43-08:00</created-at>\n" +
                                             "        <updated-at>2008-02-19T09:42:43-08:00</updated-at>\n" +
                                             "      </comment>\n" +
                                             "    </record>\n" +
                                             "    <created-at>2008-02-19T09:42:43-08:00</created-at>\n" +
                                             "  </audit-trail>\n" +
                                             "  <audit-trail>\n" +
                                             "    <description></description>\n" +
                                             "    <event>create</event>\n" +
                                             "    <person-id type=\"integer\">12197</person-id>\n" +
                                             "    <record-id type=\"integer\">110793</record-id>\n" +
                                             "    <record-type>Comment</record-type>\n" +
                                             "    <summary>Comment Created</summary>\n" +
                                             "    <project-id>9404</project-id>\n" +
                                             "    <record>\n" +
                                             "      <comment>\n" +
                                             "        <author-id type=\"integer\">12197</author-id>\n" +
                                             "        <body>This change will also require a change to the deploy process; I'll make that change to create JNLP files that launch the english translation only.  The filename will follow the convention as other languages do, with the english code \"en\".</body>\n" +
                                             "        <id type=\"integer\">110793</id>\n" +
                                             "        <parent-id type=\"integer\">143824</parent-id>\n" +
                                             "        <parent-type>Ticket</parent-type>\n" +
                                             "        <created-at>2008-02-19T09:38:07-08:00</created-at>\n" +
                                             "        <updated-at>2008-02-19T09:38:07-08:00</updated-at>\n" +
                                             "      </comment>\n" +
                                             "    </record>\n" +
                                             "    <created-at>2008-02-19T09:38:07-08:00</created-at>\n" +
                                             "  </audit-trail>\n" +
                                             "</events>";
}
