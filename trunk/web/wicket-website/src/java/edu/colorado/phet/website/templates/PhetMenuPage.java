package edu.colorado.phet.website.templates;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.cache.SimplePanelCacheEntry;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.constants.CSS;
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

    private int contentWidth = 765;
    private boolean initializedLocations = false;
    private Collection<NavLocation> navLocations;

    private static final Logger logger = Logger.getLogger( PhetMenuPage.class.getName() );

    static { logger.setLevel( Level.DEBUG ); }

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
        add( HeaderContributor.forCss( CSS.MENU_PAGE ) );

        add( AboutLicensingPanel.getLinker().getLink( "some-rights-link", getPageContext(), getPhetCycle() ) );

        checkNavLocationParameters( parameters );

    }

    /**
     * If the user is not signed in, redirect them to the sign-in page but keep the correct navigation locations
     */
    protected void verifySignedIn() {
        if ( initializedLocations ) {
            AuthenticatedPage.checkSignedIn( navLocations );
        }
        else {
            super.verifySignedIn();
        }
    }

    public void initializeLocation( NavLocation currentLocation ) {
        HashSet<NavLocation> currentLocations = new HashSet<NavLocation>();
        currentLocations.add( currentLocation );
        initializeLocationWithSet( currentLocations );
    }

    public void initializeLocationWithSet( Collection<NavLocation> currentLocations ) {
        if ( initializedLocations ) {
            throw new RuntimeException( "Initialized locations twice!" );
        }
        navLocations = currentLocations;
        initializedLocations = true;
        add( new SideNavMenu( "side-navigation", getPageContext(), currentLocations ) );
    }

    public int getContentWidth() {
        return contentWidth;
    }

    public void setContentWidth( int contentWidth ) {
        this.contentWidth = contentWidth;
    }

    @Override
    public String getStyle( String key ) {
        // be able to override the width so we can increase it for specific pages
        if ( key.equals( "style.menu-page-content" ) ) {
            return "width: " + getContentWidth() + "px;";
        }

        return super.getStyle( key );
    }

    /**
     * If nav locations were passed in through the page parameters, we use those instead. Useful for things like the
     * sign-in page which ideally would have the menu continue.
     *
     * @param parameters Initial page parameters
     */
    private void checkNavLocationParameters( PageParameters parameters ) {
        Object locations = parameters.get( "navLocations" );
        if ( locations != null ) {
            Collection<NavLocation> defaultLocations = (Collection<NavLocation>) locations;
            if ( logger.isDebugEnabled() ) {
                logger.debug( "initializing with default locations:" );
                for ( NavLocation navLocation : defaultLocations ) {
                    logger.debug( navLocation.getKey() );
                }
            }
            initializeLocationWithSet( defaultLocations );
        }
    }

    private void initializeDefaultLocations() {
        initializeLocationWithSet( new HashSet<NavLocation>() );
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if ( !initializedLocations ) {
            initializeDefaultLocations();
        }
    }
}
