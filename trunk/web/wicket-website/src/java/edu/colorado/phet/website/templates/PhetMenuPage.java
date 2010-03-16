package edu.colorado.phet.website.templates;

import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.cache.SimplePanelCacheEntry;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.content.about.AboutLicensingPanel;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.SideNavMenu;
import edu.colorado.phet.website.panels.SponsorsPanel;
import edu.colorado.phet.website.panels.TranslationLinksPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * This is a page with a menu on the left, and the PhET header and footer
 */
public abstract class PhetMenuPage extends PhetPage {
    public PhetMenuPage( PageParameters parameters ) {
        super( parameters, true );

        add( new SimplePanelCacheEntry( SponsorsPanel.class, null, getPageContext().getLocale(), "tester" ) {
            public PhetPanel constructPanel( String id, PageContext context ) {
                return new SponsorsPanel( id, context );
            }
        }.instantiate( "sponsors-panel", getPageContext() ) );

        if ( DistributionHandler.displayTranslationLinksPanel( (PhetRequestCycle) getRequestCycle() ) ) {
            add( new TranslationLinksPanel( "translation-links", getPageContext() ) );
        }
        else {
            add( new InvisibleComponent( "translation-links" ) );
        }
        add( HeaderContributor.forCss( "/css/menu-page-v1.css" ) );

        add( AboutLicensingPanel.getLinker().getLink( "some-rights-link", getPageContext(), getPhetCycle() ) );

    }

    public void initializeLocation( NavLocation currentLocation ) {
        Set<NavLocation> currentLocations = new HashSet<NavLocation>();
        currentLocations.add( currentLocation );
        initializeLocationWithSet( currentLocations );
    }

    public void initializeLocationWithSet( Set<NavLocation> currentLocations ) {
        add( new SideNavMenu( "side-navigation", getPageContext(), currentLocations ) );
    }
}
