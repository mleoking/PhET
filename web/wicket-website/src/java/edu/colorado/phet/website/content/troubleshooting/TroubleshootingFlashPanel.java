package edu.colorado.phet.website.content.troubleshooting;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class TroubleshootingFlashPanel extends PhetPanel {
    public TroubleshootingFlashPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/troubleshooting-v1.css" ) );

        add( new LocalizedText( "troubleshooting-flash-intro", "troubleshooting.flash.intro", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu\"><span class=\"red\">phethelp@colorado.edu</span></a>"
        } ) );

        add( new LocalizedText( "troubleshooting-flash-q1-answer", "troubleshooting.flash.q1.answer" ) );

    }

    public static String getKey() {
        return "troubleshooting.flash";
    }

    public static String getUrl() {
        return "troubleshooting/flash";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}