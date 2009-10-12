package edu.colorado.phet.website.templates;

import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.content.about.AboutLicensingPanel;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.SideNavMenu;
import edu.colorado.phet.website.panels.SponsorsPanel;
import edu.colorado.phet.website.panels.TranslationLinksPanel;

public abstract class PhetMenuPage extends PhetPage {
    public PhetMenuPage( PageParameters parameters ) {
        super( parameters, true );

        add( new SponsorsPanel( "sponsors-panel", getPageContext() ) );
        add( new TranslationLinksPanel( "translation-links", getPageContext() ) );
        add( HeaderContributor.forCss( "/css/menu-page-v1.css" ) );

        add( AboutLicensingPanel.getLinker().getLink( "some-rights-link", getPageContext() ) );

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
