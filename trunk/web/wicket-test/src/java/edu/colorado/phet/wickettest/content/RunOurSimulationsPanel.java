package edu.colorado.phet.wickettest.content;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;

public class RunOurSimulationsPanel extends PhetPanel {
    public RunOurSimulationsPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/run-our-simulations-v1.css" ) );

        add( SimulationDisplay.createLink( "online-link", context ) );
        add( FullInstallPanel.getLinker().getLink( "install-link", context ) );
        add( OneAtATimePanel.getLinker().getLink( "offline-link", context ) );

        add( new LocalizedText( "get-phet-install-header", "get-phet.install.header" ) );
        add( new LocalizedText( "get-phet-offline-header", "get-phet.offline.header" ) );

        add( new LocalizedText( "get-phet-install-howToGet", "get-phet.install.howToGet", new Object[]{
                "href=\"" + context.getPrefix() + FullInstallPanel.getUrl() + "\""
        } ) );

        add( new LocalizedText( "get-phet-offline-howToGet", "get-phet.offline.howToGet", new Object[]{
                "href=\"" + context.getPrefix() + OneAtATimePanel.getUrl() + "\""
        } ) );

    }

    public static String getKey() {
        return "get-phet";
    }

    public static String getUrl() {
        return "get-phet";
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + getUrl() );
            }
        };
    }
}