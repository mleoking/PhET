package edu.colorado.phet.website.content.simulations;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.cache.SimplePanelCacheEntry;
import edu.colorado.phet.website.content.NotFoundPage;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.simulation.SimulationListViewPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.StringUtils;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Displays thumbnails of simulations within a single category, or an alphabetical list of simulations, depending on
 * whether in index mode.
 */
public class CategoryPage extends PhetRegularPage {

    private static final Logger logger = Logger.getLogger( CategoryPage.class.getName() );

    public CategoryPage( final PageParameters parameters ) {
        super( parameters );

        boolean showIndex = false;

        // we need to check the "query string" part to see if we will show in "index" mode
        if ( parameters.containsKey( "query-string" ) ) {
            logger.debug( "Query string: " + parameters.getString( "query-string" ) );
            if ( parameters.getString( "query-string" ).equals( "/index" ) ) {
                showIndex = true;
            }
            else {
                setResponsePage( NotFoundPage.class );
            }
        }
        else {
            logger.debug( "No query string" );
        }

        final boolean index = showIndex;

        // create a cacheable panel for the simulation list
        PhetPanel viewPanel = new SimplePanelCacheEntry( SimulationListViewPanel.class, this.getClass(), getPageContext().getLocale(), getMyPath() ) {
            public PhetPanel constructPanel( String id, PageContext context ) {
                return new SimulationListViewPanel(
                        id,
                        CategoryPage.this.getMyPath(),
                        parameters.containsKey( "categories" ) ? parameters.getString( "categories" ) : null,
                        index,
                        context
                );
            }
        }.instantiate( "view-panel", getPageContext() );
        add( viewPanel );

        NavLocation location = (NavLocation) viewPanel.getCacheParameter( "location" );
        initializeLocation( location );

        addTitle( StringUtils.messageFormat( getPhetLocalizer().getString( "simulationDisplay.title", this ), new Object[]{
                getPhetLocalizer().getString( location.getLocalizationKey(), this )
        } ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        // WARNING: don't change without also changing the old URL redirection
        mapper.addMap( "^simulations(/index)?$", CategoryPage.class, new String[]{"query-string"} );
        mapper.addMap( "^simulations/category/(.+?)(/index)?$", CategoryPage.class, new String[]{"categories", "query-string"} );
    }

    public static RawLinkable getLinker() {
        // WARNING: don't change without also changing the old URL redirection
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "simulations/category/" + Category.getDefaultCategoryKey();
            }
        };
    }

}