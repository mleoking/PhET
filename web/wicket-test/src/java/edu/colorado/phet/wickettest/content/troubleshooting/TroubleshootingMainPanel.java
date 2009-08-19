package edu.colorado.phet.wickettest.content.troubleshooting;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;

public class TroubleshootingMainPanel extends PhetPanel {
    public TroubleshootingMainPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "troubleshooting-main-intro", "troubleshooting.main.intro", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu?Subject=Help\"><span class=\"red\">phethelp@colorado.edu</span></a>",
                ""
        } ) );

        add( new LocalizedText( "troubleshooting-main-q1-answer", "troubleshooting.main.q1.answer", new Object[]{
                "href=\"../tech_support/support-java.php\"",
                ""
        } ) );

        add( new LocalizedText( "troubleshooting-main-q2-answer", "troubleshooting.main.q2.answer", new Object[]{
                "href=\"../get_phet/full_install.php\""
        } ) );

        add( new LocalizedText( "troubleshooting-main-q4-answer", "troubleshooting.main.q4.answer" ) );

        add( new LocalizedText( "troubleshooting-main-q5-answer", "troubleshooting.main.q5.answer", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu?Subject=Flash%20Simulations\">phethelp@colorado.edu</a>"
        } ) );

        add( new LocalizedText( "troubleshooting-main-q6-answer", "troubleshooting.main.q6.answer" ) );

        add( new LocalizedText( "troubleshooting-main-q7-answer", "troubleshooting.main.q7.answer", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu?Subject=Windows%202000%20Issues\">phethelp@colorado.edu</a>"
        } ) );

        add( new LocalizedText( "troubleshooting-main-q8-answer", "troubleshooting.main.q8.answer", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu?Subject=Laptop%20Performance%20Issues\">phethelp@colorado.edu</a>"
        } ) );

        add( new LocalizedText( "troubleshooting-main-q9-answer", "troubleshooting.main.q9.answer", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu?Subject=Sound%20Issues\">phethelp@colorado.edu</a>"
        } ) );

        add( new LocalizedText( "troubleshooting-main-q10-answer", "troubleshooting.main.q10.answer", new Object[]{
                "href=\"../contribute/index.php\""
        } ) );

        add( new LocalizedText( "troubleshooting-main-q11-answer", "troubleshooting.main.q11.answer", new Object[]{
                "href=\"../get_phet/full_install.php\""
        } ) );

        add( new LocalizedText( "troubleshooting-main-q12-answer", "troubleshooting.main.q12.answer" ) );

        add( new LocalizedText( "troubleshooting-main-q13-answer", "troubleshooting.main.q13.answer" ) );

        add( HeaderContributor.forCss( "/css/troubleshooting-v1.css" ) );

    }

    public static String getKey() {
        return "troubleshooting.main";
    }

    public static String getUrl() {
        return "troubleshooting";
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + getUrl() );
            }
        };
    }
}