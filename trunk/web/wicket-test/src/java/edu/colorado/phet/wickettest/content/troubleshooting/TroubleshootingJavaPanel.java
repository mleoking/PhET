package edu.colorado.phet.wickettest.content.troubleshooting;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;

public class TroubleshootingJavaPanel extends PhetPanel {
    public TroubleshootingJavaPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/troubleshooting-v1.css" ) );


        add( new LocalizedText( "intro", "troubleshooting.java.intro", new Object[]{
                "href=\"mailto:phethelp@colorado.edu\""
        } ) );

        add( new LocalizedText( "troubleshooting-java-q1-answer", "troubleshooting.java.q1.answer", new Object[]{
                "<img style=\"float: left;\" src=\"/images/unhappy-mac-jnlp-logo-small.jpg\" alt=\"Unhappy JNLP Mac Logo\"/>"
        } ) );

        add( new LocalizedText( "troubleshooting-java-q2-answer", "troubleshooting.java.q2.answer", new Object[]{
                "<a href=\"http://www.java.com/en/index.jsp\"><img src=\"/images/java-jump.gif\" alt=\"Java Jump\"/></a>"
        } ) );

        add( new LocalizedText( "troubleshooting-java-q3-answer", "troubleshooting.java.q3.answer", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>"
        } ) );

        add( new LocalizedText( "troubleshooting-java-q4-answer", "troubleshooting.java.q4.answer", new Object[]{
                "http://www.apple.com/java/",
                "href=\"#q1\""
        } ) );

        add( new LocalizedText( "troubleshooting-java-q5-answer", "troubleshooting.java.q5.answer" ) );

        add( new LocalizedText( "troubleshooting-java-q6-answer", "troubleshooting.java.q6.answer" ) );

        add( new LocalizedText( "troubleshooting-java-q7-answer", "troubleshooting.java.q7.answer" ) );

        add( new LocalizedText( "troubleshooting-java-q8-answer", "troubleshooting.java.q8.answer" ) );

    }

    public static String getKey() {
        return "troubleshooting.java";
    }

    public static String getUrl() {
        return "troubleshooting/java";
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + getUrl() );
            }
        };
    }
}