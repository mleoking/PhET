package edu.colorado.phet.website.content.simulations;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.StringResourceModel;

import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ByGradeLevelPage extends PhetRegularPage {

    private static final Logger logger = Logger.getLogger( ByGradeLevelPage.class.getName() );

    public ByGradeLevelPage( final PageParameters parameters ) {
        super( parameters );

        add( new ByGradeLevelPanel( "by-grade-level-panel", getPageContext() ) );

        NavLocation location = getNavMenu().getLocationByKey( "by-level" );

        initializeLocation( location );

        addTitle( new StringResourceModel( "simulationDisplay.title", this, null, new Object[]{new StringResourceModel( location.getLocalizationKey(), this, null )} ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        // WARNING: don't change without also changing the old URL redirection
        mapper.addMap( "^simulations/category/by-level$", ByGradeLevelPage.class );
    }

    public static RawLinkable getLinker() {
        // WARNING: don't change without also changing the old URL redirection
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "simulations/category/by-level";
            }
        };
    }

}