package edu.colorado.phet.unfuddle;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Created by: Sam
 * Feb 21, 2008 at 7:30:51 AM
 */
public class RecentActivity {
    public static void main( String[] args ) throws IOException, SAXException, ParserConfigurationException {
        IUnfuddleAccount p = new UnfuddleAccount( new File( "C:\\reid\\phet\\svn\\trunk\\team\\reids\\unfuddle\\data\\phet.unfuddled.20080221150731.xml" ) );
//        DateFormat dateFormat = new SimpleDateFormat( "yyyy/M/d" );
//        String startDate = dateFormat.format( new Date( System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2 ) );
//        String endDate = dateFormat.format( new Date() );

//        String s = curl.readString( "activity.xml" );
//        System.out.println( "s = " + s );

//        String recent = curl.readString( "activity.xml?start_date=" + startDate + "&end_date=" + endDate );

//        UnfuddleCurl curl = new UnfuddleCurl( args[0], args[1], UnfuddleCurl.PHET_PROJECT_ID );
//        String recent = curl.readString( "activity.xml?limit=40" );
//        System.out.println( "recent = " + recent );

        final String recent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
                              "    <record-id type=\"integer\">145915</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Created</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\">12196</assignee-id>\n" +
                              "        <component-id type=\"integer\">13389</component-id>\n" +
                              "        <description>Is there anything on the website not in SVN?  Is there anything there that needs to be there but should not be in SVN?</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">145915</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">213</number>\n" +
                              "        <priority>4</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                              "        <resolution></resolution>\n" +
                              "        <resolution-description></resolution-description>\n" +
                              "        <severity-id type=\"integer\"></severity-id>\n" +
                              "        <status>new</status>\n" +
                              "        <summary>Make sure all relevant information on /web/htdocs/phet is checked into SVN</summary>\n" +
                              "        <version-id type=\"integer\"></version-id>\n" +
                              "        <created-at>2008-02-20T19:47:22-08:00</created-at>\n" +
                              "        <updated-at>2008-02-20T19:47:22-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-20T19:47:23-08:00</created-at>\n" +
                              "  </audit-trail>\n" +
                              "  <audit-trail>\n" +
                              "    <description></description>\n" +
                              "    <event>create</event>\n" +
                              "    <person-id type=\"integer\">12197</person-id>\n" +
                              "    <record-id type=\"integer\">145914</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Created</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\">12196</assignee-id>\n" +
                              "        <component-id type=\"integer\">19955</component-id>\n" +
                              "        <description>There is an unfuddle notebook started about this, but it is very high-level and doesn't contain any information about the cron job.</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">145914</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">212</number>\n" +
                              "        <priority>3</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                              "        <resolution></resolution>\n" +
                              "        <resolution-description></resolution-description>\n" +
                              "        <severity-id type=\"integer\"></severity-id>\n" +
                              "        <status>new</status>\n" +
                              "        <summary>Document the installer builder/cron job.</summary>\n" +
                              "        <version-id type=\"integer\"></version-id>\n" +
                              "        <created-at>2008-02-20T19:46:49-08:00</created-at>\n" +
                              "        <updated-at>2008-02-20T19:46:49-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-20T19:46:51-08:00</created-at>\n" +
                              "  </audit-trail>\n" +
                              "  <audit-trail>\n" +
                              "    <description></description>\n" +
                              "    <event>create</event>\n" +
                              "    <person-id type=\"integer\">12197</person-id>\n" +
                              "    <record-id type=\"integer\">145913</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Created</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\">12196</assignee-id>\n" +
                              "        <component-id type=\"integer\">13389</component-id>\n" +
                              "        <description>Make sure permissions are correct throughout /web/htdocs/phet.  Perhaps one of the reasons we couldn't run the installer builder ourselves is that we couldn't get a file lock on a new file because we don't have permissions in the directory.</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">145913</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">211</number>\n" +
                              "        <priority>4</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                              "        <resolution></resolution>\n" +
                              "        <resolution-description></resolution-description>\n" +
                              "        <severity-id type=\"integer\"></severity-id>\n" +
                              "        <status>new</status>\n" +
                              "        <summary>Fix permissions</summary>\n" +
                              "        <version-id type=\"integer\"></version-id>\n" +
                              "        <created-at>2008-02-20T19:45:48-08:00</created-at>\n" +
                              "        <updated-at>2008-02-20T19:45:53-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-20T19:45:49-08:00</created-at>\n" +
                              "  </audit-trail>\n" +
                              "  <audit-trail>\n" +
                              "    <description></description>\n" +
                              "    <event>create</event>\n" +
                              "    <person-id type=\"integer\">12197</person-id>\n" +
                              "    <record-id type=\"integer\">145912</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Created</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\">12196</assignee-id>\n" +
                              "        <component-id type=\"integer\">13389</component-id>\n" +
                              "        <description>It is difficult for us to know what is necessary and unnecessary there.</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">145912</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">210</number>\n" +
                              "        <priority>4</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                              "        <resolution></resolution>\n" +
                              "        <resolution-description></resolution-description>\n" +
                              "        <severity-id type=\"integer\"></severity-id>\n" +
                              "        <status>new</status>\n" +
                              "        <summary>Clean up /web/htdocs/phet?</summary>\n" +
                              "        <version-id type=\"integer\"></version-id>\n" +
                              "        <created-at>2008-02-20T19:45:11-08:00</created-at>\n" +
                              "        <updated-at>2008-02-20T19:46:22-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-20T19:45:12-08:00</created-at>\n" +
                              "  </audit-trail>\n" +
                              "  <audit-trail>\n" +
                              "    <description></description>\n" +
                              "    <event>create</event>\n" +
                              "    <person-id type=\"integer\">12197</person-id>\n" +
                              "    <record-id type=\"integer\">145911</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Created</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\"></assignee-id>\n" +
                              "        <component-id type=\"integer\">19955</component-id>\n" +
                              "        <description>Would it be quick and easy to add an error checking feature. Possibly a check to see if the build is at least 50MB or maybe compare to the previous days build?\n" +
                              "\n" +
                              "Sometimes the installer builds, but produces a small installer that includes Java but no simulations.</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">145911</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">209</number>\n" +
                              "        <priority>3</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                              "        <resolution></resolution>\n" +
                              "        <resolution-description></resolution-description>\n" +
                              "        <severity-id type=\"integer\"></severity-id>\n" +
                              "        <status>new</status>\n" +
                              "        <summary>Error checking for installer builder</summary>\n" +
                              "        <version-id type=\"integer\"></version-id>\n" +
                              "        <created-at>2008-02-20T19:43:07-08:00</created-at>\n" +
                              "        <updated-at>2008-02-20T19:43:07-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-20T19:43:08-08:00</created-at>\n" +
                              "  </audit-trail>\n" +
                              "  <audit-trail>\n" +
                              "    <description></description>\n" +
                              "    <event>create</event>\n" +
                              "    <person-id type=\"integer\">12198</person-id>\n" +
                              "    <record-id type=\"integer\">145639</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Created</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\"></assignee-id>\n" +
                              "        <component-id type=\"integer\">13436</component-id>\n" +
                              "        <description>When running the buildfiles, ProGuard is currently so verbose that the output is meaningless, and it would hide any real problems that might occur.  Change the ProGuard configuration to make the output less verbose.</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">145639</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">208</number>\n" +
                              "        <priority>3</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12198</reporter-id>\n" +
                              "        <resolution></resolution>\n" +
                              "        <resolution-description></resolution-description>\n" +
                              "        <severity-id type=\"integer\">15851</severity-id>\n" +
                              "        <status>new</status>\n" +
                              "        <summary>modify build process to make ProGuard less verbose</summary>\n" +
                              "        <version-id type=\"integer\"></version-id>\n" +
                              "        <created-at>2008-02-20T13:20:25-08:00</created-at>\n" +
                              "        <updated-at>2008-02-20T13:20:25-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-20T13:20:26-08:00</created-at>\n" +
                              "  </audit-trail>\n" +
                              "  <audit-trail>\n" +
                              "    <description></description>\n" +
                              "    <event>create</event>\n" +
                              "    <person-id type=\"integer\">12197</person-id>\n" +
                              "    <record-id type=\"integer\">145401</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Created</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\"></assignee-id>\n" +
                              "        <component-id type=\"integer\">13419</component-id>\n" +
                              "        <description>Pavel B&#246;hm wrote:\n" +
                              "&gt; Hi Sam,\n" +
                              "&gt;\n" +
                              "&gt; We have problem with translating word 'Current' in Photoelectric applet. I have searched the source code downloaded from the SourceForge, but I'm not sure about it's up-to-dateness. There is no string 'GraphLabel.Current' in java files, but we have found this in our localization file photoelectric-strings.properties. Changing of this parameter doesn't work whereas all other changes work perfectly. Same problems is with text 'File' or 'Help' in some menus, but I think you already know this.\n" +
                              "&gt;\n" +
                              "&gt; Please help mi fix this 'current problem' :o).\n" +
                              "&gt;\n" +
                              "&gt; Regards\n" +
                              "&gt; Pavel \n" +
                              "</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">145401</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">207</number>\n" +
                              "        <priority>3</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                              "        <resolution></resolution>\n" +
                              "        <resolution-description></resolution-description>\n" +
                              "        <severity-id type=\"integer\"></severity-id>\n" +
                              "        <status>new</status>\n" +
                              "        <summary>many strings are not translatable in photoelectric effect</summary>\n" +
                              "        <version-id type=\"integer\"></version-id>\n" +
                              "        <created-at>2008-02-20T08:38:47-08:00</created-at>\n" +
                              "        <updated-at>2008-02-20T08:38:47-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-20T08:38:48-08:00</created-at>\n" +
                              "  </audit-trail>\n" +
                              "  <audit-trail>\n" +
                              "    <description></description>\n" +
                              "    <event>close</event>\n" +
                              "    <person-id type=\"integer\">12197</person-id>\n" +
                              "    <record-id type=\"integer\">96327</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Closed</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\">12196</assignee-id>\n" +
                              "        <component-id type=\"integer\">13389</component-id>\n" +
                              "        <description>John Doe appears on a number of contributions; this only appears when no name has been specified, which should not be possible if the user is running JavaScript. The problem needs to be diagnosed and a fix applied.</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">96327</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">48</number>\n" +
                              "        <priority>3</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12196</reporter-id>\n" +
                              "        <resolution>fixed</resolution>\n" +
                              "        <resolution-description>duplicate</resolution-description>\n" +
                              "        <severity-id type=\"integer\">15852</severity-id>\n" +
                              "        <status>closed</status>\n" +
                              "        <summary>solve problem of 'John Doe' creating contributions</summary>\n" +
                              "        <version-id type=\"integer\">6651</version-id>\n" +
                              "        <created-at>2007-12-17T07:16:09-08:00</created-at>\n" +
                              "        <updated-at>2008-02-19T09:43:30-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-19T09:43:31-08:00</created-at>\n" +
                              "  </audit-trail>\n" +
                              "  <audit-trail>\n" +
                              "    <description></description>\n" +
                              "    <event>close</event>\n" +
                              "    <person-id type=\"integer\">12197</person-id>\n" +
                              "    <record-id type=\"integer\">132654</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Closed</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\">12197</assignee-id>\n" +
                              "        <component-id type=\"integer\">13389</component-id>\n" +
                              "        <description>Collaborate with John on this.  Will also impact deploy process/website.</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">132654</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">165</number>\n" +
                              "        <priority>3</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                              "        <resolution>fixed</resolution>\n" +
                              "        <resolution-description>duplicate</resolution-description>\n" +
                              "        <severity-id type=\"integer\"></severity-id>\n" +
                              "        <status>closed</status>\n" +
                              "        <summary>Move offline jars and SWF to one location to make offline usage stats easier.</summary>\n" +
                              "        <version-id type=\"integer\"></version-id>\n" +
                              "        <created-at>2008-02-05T08:53:31-08:00</created-at>\n" +
                              "        <updated-at>2008-02-19T09:43:13-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-19T09:43:15-08:00</created-at>\n" +
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
                              "    <description>duplicate</description>\n" +
                              "    <event>resolve</event>\n" +
                              "    <person-id type=\"integer\">12197</person-id>\n" +
                              "    <record-id type=\"integer\">132654</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Resolved as Fixed</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\">12197</assignee-id>\n" +
                              "        <component-id type=\"integer\">13389</component-id>\n" +
                              "        <description>Collaborate with John on this.  Will also impact deploy process/website.</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">132654</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">165</number>\n" +
                              "        <priority>3</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12197</reporter-id>\n" +
                              "        <resolution>fixed</resolution>\n" +
                              "        <resolution-description>duplicate</resolution-description>\n" +
                              "        <severity-id type=\"integer\"></severity-id>\n" +
                              "        <status>closed</status>\n" +
                              "        <summary>Move offline jars and SWF to one location to make offline usage stats easier.</summary>\n" +
                              "        <version-id type=\"integer\"></version-id>\n" +
                              "        <created-at>2008-02-05T08:53:31-08:00</created-at>\n" +
                              "        <updated-at>2008-02-19T09:43:13-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-19T09:42:04-08:00</created-at>\n" +
                              "  </audit-trail>\n" +
                              "  <audit-trail>\n" +
                              "    <description>duplicate</description>\n" +
                              "    <event>resolve</event>\n" +
                              "    <person-id type=\"integer\">12197</person-id>\n" +
                              "    <record-id type=\"integer\">96327</record-id>\n" +
                              "    <record-type>Ticket</record-type>\n" +
                              "    <summary>Ticket Resolved as Fixed</summary>\n" +
                              "    <project-id>9404</project-id>\n" +
                              "    <record>\n" +
                              "      <ticket>\n" +
                              "        <assignee-id type=\"integer\">12196</assignee-id>\n" +
                              "        <component-id type=\"integer\">13389</component-id>\n" +
                              "        <description>John Doe appears on a number of contributions; this only appears when no name has been specified, which should not be possible if the user is running JavaScript. The problem needs to be diagnosed and a fix applied.</description>\n" +
                              "        <due-on type=\"date\"></due-on>\n" +
                              "        <hours-estimate-current type=\"float\">0.0</hours-estimate-current>\n" +
                              "        <hours-estimate-initial type=\"float\">0.0</hours-estimate-initial>\n" +
                              "        <id type=\"integer\">96327</id>\n" +
                              "        <milestone-id type=\"integer\"></milestone-id>\n" +
                              "        <number type=\"integer\">48</number>\n" +
                              "        <priority>3</priority>\n" +
                              "        <project-id type=\"integer\">9404</project-id>\n" +
                              "        <reporter-id type=\"integer\">12196</reporter-id>\n" +
                              "        <resolution>fixed</resolution>\n" +
                              "        <resolution-description>duplicate</resolution-description>\n" +
                              "        <severity-id type=\"integer\">15852</severity-id>\n" +
                              "        <status>closed</status>\n" +
                              "        <summary>solve problem of 'John Doe' creating contributions</summary>\n" +
                              "        <version-id type=\"integer\">6651</version-id>\n" +
                              "        <created-at>2007-12-17T07:16:09-08:00</created-at>\n" +
                              "        <updated-at>2008-02-19T09:43:30-08:00</updated-at>\n" +
                              "      </ticket>\n" +
                              "    </record>\n" +
                              "    <created-at>2008-02-19T09:40:13-08:00</created-at>\n" +
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
        XMLObject events = new XMLObject( recent );
//        System.out.println( "r = " + recent );
        int e = events.getNodeCount( "audit-trail" );
        System.out.println( "num events=" + e );
        for ( int i = 0; i < e; i++ ) {
            XMLObject auditTrail = events.getNode( i, "audit-trail" );
            if ( auditTrail.getTextContent( "summary" ).equals( "Ticket Created" ) ) {
                XMLObject record = auditTrail.getNode( "record" );
                XMLObject ticket = record.getNode( "ticket" );
                if ( ticket != null ) {
                    handleTicketCreated( p, ticket );
                }
            }
            //
//                XMLObject comment = record.getNode( "comment" );
//                if ( comment != null ) {
//                    handleComment( p, comment );
//                }
        }
    }

    private static void handleComment( IUnfuddleAccount p, XMLObject comment ) {
        System.out.println( "Found comment: " + comment.getTextContent( "id" ) + ", " + comment.getTextContent( "body" ) );

    }

    private static void handleTicketCreated( IUnfuddleAccount p, XMLObject ticket ) {
        String reported = p.getPersonForID( Integer.parseInt( ticket.getTextContent( "reporter-id" ) ) );
        String component = p.getComponentForID( Integer.parseInt( ticket.getTextContent( "component-id" ) ) );
//        System.out.println( "Ticket Created: " + ticket.getTextContent( "number" ) + ", component=" + component + ", reporter: " + reported + ", " + ticket.getTextContent( "summary" ) + ": Description=" + ticket.getTextContent( "description" ) );

        NewTicketMessage newTicketMessage = new NewTicketMessage( ticket, p );
        System.out.println( newTicketMessage );
    }
}
