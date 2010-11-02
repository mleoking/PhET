package edu.colorado.phet.website.templates;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.cache.SimplePanelCacheEntry;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.constants.SocialBookmarkService;
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
    private boolean showSocialBookmarkButtons = true;
    private Collection<NavLocation> navLocations;

    private static final Logger logger = Logger.getLogger( PhetMenuPage.class.getName() );

    public PhetMenuPage( PageParameters parameters ) {
        super( parameters );

        add( new SimplePanelCacheEntry( SponsorsPanel.class, null, getPageContext().getLocale(), "tester", getPhetCycle() ) {
            public PhetPanel constructPanel( String id, PageContext context ) {
                return new SponsorsPanel( id, context );
            }
        }.instantiate( "sponsors-panel", getPageContext(), getPhetCycle() ) );

        if ( DistributionHandler.displayTranslationLinksPanel( (PhetRequestCycle) getRequestCycle() ) ) {
            add( new TranslationLinksPanel( "translation-links", getPageContext() ) );
        }
        else {
            add( new InvisibleComponent( "translation-links" ) );
        }
        add( HeaderContributor.forCss( CSS.MENU_PAGE ) );

        add( AboutLicensingPanel.getLinker().getLink( "some-rights-link", getPageContext(), getPhetCycle() ) );

        checkNavLocationParameters( parameters );

        // we initialize the links in the onBeforeRender so we can grab the title

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

    public void hideSocialBookmarkButtons() {
        showSocialBookmarkButtons = false;
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
            int extraSocialButtonPadding = showSocialBookmarkButtons ? 55 : 0;
            return "width: " + ( getContentWidth() + extraSocialButtonPadding ) + "px;"; // adding 55 for size of right column
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

    /**
     * @return The title string to be passed into social bookmarking tools. Added so we can strip out excess keywords or
     *         other parts of regular titles that wouldn't be proper there.
     */
    protected String getSocialBookmarkTitle() {
        return getTitle();
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if ( !initializedLocations ) {
            initializeDefaultLocations();
        }
        if ( !hasTitle() && getSocialBookmarkTitle() == null ) { // also check social bookmark override, in case we do funky magic later
            throw new RuntimeException( "title was not set before onBeforeRender for " + this.getClass().getCanonicalName() );
        }
        if ( showSocialBookmarkButtons ) {
            add( new ListView<SocialBookmarkService>( "social-list", SocialBookmarkService.SERVICES ) {
                @Override
                protected void populateItem( ListItem<SocialBookmarkService> item ) {
                    SocialBookmarkService mark = item.getModelObject();
                    Link link = mark.getLinker( getFullPath(), getSocialBookmarkTitle() ).getLink( "link", getPageContext(), getPhetCycle() );
                    link.add( new AttributeModifier( "title", true, new ResourceModel( mark.getTooltipLocalizationKey() ) ) ); // tooltip
                    item.add( link );
                    link.add( new StaticImage( "icon", mark.getIconPath(), null ) ); // for now, don't replace the alt attribute
                }
            } );
        }
        else {
            add( new InvisibleComponent( "social-list" ) );
        }

    }
}
