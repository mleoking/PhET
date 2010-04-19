package edu.colorado.phet.website.content.simulations;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.StringResourceModel;

import edu.colorado.phet.website.cache.SimplePanelCacheEntry;
import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.content.NotFoundPage;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.simulation.SimulationListViewPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class SimulationDisplay extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( SimulationDisplay.class.getName() );

    public SimulationDisplay( final PageParameters parameters ) {
        super( parameters );

        boolean showIndex = false;

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
        PhetPanel viewPanel = new SimplePanelCacheEntry( SimulationListViewPanel.class, this.getClass(), getPageContext().getLocale(), getMyPath() ) {
            public PhetPanel constructPanel( String id, PageContext context ) {
                return new SimulationListViewPanel(
                        id,
                        SimulationDisplay.this.getMyPath(),
                        parameters.containsKey( "categories" ) ? parameters.getString( "categories" ) : null,
                        index,
                        context
                );
            }
        }.instantiate( "view-panel", getPageContext() );
        add( viewPanel );

        NavLocation location = (NavLocation) viewPanel.getCacheParameter( "location" );

        initializeLocation( location );

        addTitle( new StringResourceModel( "simulationDisplay.title", this, null, new Object[]{new StringResourceModel( location.getLocalizationKey(), this, null )} ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        //mapper.addMap( "^simulations$", SimulationDisplay.class );
        mapper.addMap( "^simulations(/index)?$", SimulationDisplay.class, new String[]{"query-string"} );
        mapper.addMap( "^simulations/category/(.+?)(/index)?$", SimulationDisplay.class, new String[]{"categories", "query-string"} );
    }

    public static PhetLink createLink( String id, PageContext context ) {
        String str = context.getPrefix() + "simulations/category/featured";
        return new PhetLink( id, str );
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "simulations/category/featured";
            }
        };
    }

}