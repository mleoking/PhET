package edu.colorado.phet.wickettest.content.troubleshooting;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

public class TroubleshootingFlashPanel extends PhetPanel {
    public TroubleshootingFlashPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/troubleshooting-v1.css" ) );

        add( new LocalizedText( "troubleshooting-flash-intro", "troubleshooting.flash.intro", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu\"><span class=\"red\">phethelp@colorado.edu</span></a>"
        } ) );

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