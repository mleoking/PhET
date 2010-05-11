package edu.colorado.phet.website.content;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.SearchUtils;
import edu.colorado.phet.website.util.StringUtils;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class SearchResultsPage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( SearchResultsPage.class.getName() );

    public SearchResultsPage( PageParameters parameters ) {
        super( parameters );

        // TODO: redo localization

        String query = parameters.getString( "q" );

        initializeLocation( getNavMenu().getLocationByKey( "search.results" ) );

        List<LocalizedSimulation> lsims = new LinkedList<LocalizedSimulation>();
        List<Contribution> contributions = new LinkedList<Contribution>();

        PhetLocalizer localizer = (PhetLocalizer) getLocalizer();
        // TODO: display query

        if ( query != null ) {
            lsims = SearchUtils.simulationSearch( getHibernateSession(), query, getPageContext().getLocale() );
            contributions = SearchUtils.contributionSearch( getHibernateSession(), query, getPageContext().getLocale() );
        }

        //add( new SimulationDisplayPanel( "search-results-panel", getPageContext(), lsims ) );
        add( new ListView( "sims", lsims ) {
            protected void populateItem( ListItem item ) {
                LocalizedSimulation lsim = (LocalizedSimulation) item.getModel().getObject();
                Link link = SimulationPage.getLinker( lsim ).getLink( "sim-link", getPageContext(), getPhetCycle() );
                item.add( link );
                link.add( new Label( "sim-title", lsim.getTitle() ) );
            }
        } );
        add( new ListView( "contribs", contributions ) {
            protected void populateItem( ListItem item ) {
                Contribution contribution = (Contribution) item.getModel().getObject();
                Link link = ContributionPage.getLinker( contribution ).getLink( "contrib-link", getPageContext(), getPhetCycle() );
                item.add( link );
                link.add( new Label( "contrib-title", contribution.getTitle() ) );
            }
        } );


        if ( query != null ) {
            addTitle( StringUtils.messageFormat( localizer.getString( "search.title", this ), query ) );
            add( new LocalizedText( "search-query", "search.query", new Object[]{query} ) );
        }
        else {
            addTitle( StringUtils.messageFormat( localizer.getString( "search.title", this ), "-" ) );
            add( new InvisibleComponent( "search-query" ) );
        }

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        // TODO: fix subtle ugliness. is query parameter included in map => q, or does wicket auto-put-it into the parameters?
        mapper.addMap( "^search(\\?q=(.+))?$", SearchResultsPage.class, new String[]{null, "q"} );
    }

    public static RawLinkable getLinker( final String query ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                if ( query != null ) {
                    try {
                        return "search?q=" + URLEncoder.encode( query, "UTF-8" );
                    }
                    catch( UnsupportedEncodingException e ) {
                        e.printStackTrace();
                        logger.error( e );
                        return "";
                    }
                }
                else {
                    return "search";
                }
            }
        };
    }

}