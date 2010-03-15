package edu.colorado.phet.website.panels;

import java.text.DateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.content.ContributionPage;
import edu.colorado.phet.website.content.SimulationPage;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionLevel;
import edu.colorado.phet.website.data.contribution.ContributionType;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class ContributionBrowsePanel extends PhetPanel {

    private final List<Contribution> newContributions;

    private static Logger logger = Logger.getLogger( ContributionBrowsePanel.class.getName() );

    /**
     * NOTE: ALWAYS preload the levels, types, simulations, and those simulations' localizedSimulations. They are all
     * lazy fields, and will throw errors if that isn't done.
     *
     * @param id
     * @param context
     * @param contributions List of contributions
     */
    public ContributionBrowsePanel( String id, final PageContext context, final List<Contribution> contributions ) {
        super( id, context );

        logger.debug( System.currentTimeMillis() + " start" );

        add( HeaderContributor.forCss( "/css/contribution-main-v1.css" ) );

        newContributions = new LinkedList<Contribution>();

        logger.debug( System.currentTimeMillis() + " A" );

        // TODO: localize

        final DateFormat format = DateFormat.getDateInstance( DateFormat.SHORT, getLocale() );

        Collections.sort( contributions, new Comparator<Contribution>() {
            public int compare( Contribution a, Contribution b ) {
                return a.displayCompareTo( b, getLocale() );
            }
        } );

        logger.debug( System.currentTimeMillis() + " B" );

        add( new ListView( "contributions", contributions ) {
            private HashMap<Simulation, LocalizedSimulation> mapCache = new HashMap<Simulation, LocalizedSimulation>();

            protected void populateItem( ListItem item ) {
                //logger.debug( "start item" );
                Contribution contribution = (Contribution) item.getModel().getObject();
                Link link = ContributionPage.getLinker( contribution ).getLink( "contribution-link", context, getPhetCycle() );
                link.add( new Label( "contribution-title", contribution.getTitle() ) );
                item.add( link );
                item.add( new Label( "contribution-authors", contribution.getAuthors() ) );

                Label levelLabel = new Label( "contribution-level", getLevelString( contribution ) );
                levelLabel.setEscapeModelStrings( false );
                item.add( levelLabel );

                Label typeLabel = new Label( "contribution-type", getTypeString( contribution ) );
                typeLabel.setEscapeModelStrings( false );
                item.add( typeLabel );

                // get a sorted (for the locale) list of simulations
                List<LocalizedSimulation> lsims = new LinkedList<LocalizedSimulation>();
                for ( Object o : contribution.getSimulations() ) {
                    Simulation sim = (Simulation) o;
                    LocalizedSimulation lsim;
                    if ( mapCache.containsKey( sim ) ) {
                        lsim = mapCache.get( sim );
                    }
                    else {
                        lsim = sim.getBestLocalizedSimulation( getLocale() );
                        mapCache.put( sim, lsim );
                    }
                    lsims.add( lsim );
                }
                HibernateUtils.orderSimulations( lsims, getLocale() );

                if ( contribution.isFromPhet() ) {
                    // TODO: localize alt
                    // TODO: add title?
                    item.add( new StaticImage( "phet-contribution", "/images/contributions/phet-logo-icon-small.jpg", "Contribution by the PhET team" ) );
                }
                else {
                    item.add( new InvisibleComponent( "phet-contribution" ) );
                }

                if ( contribution.isGoldStar() ) {
                    // TODO: localize alt
                    // TODO: add title?
                    item.add( new StaticImage( "gold-star-contribution", "/images/contributions/gold-star-small.jpg", "Gold Star Contribution" ) );
                }
                else {
                    item.add( new InvisibleComponent( "gold-star-contribution" ) );
                }

                // add the list in
                item.add( new ListView( "contribution-simulations", lsims ) {
                    protected void populateItem( ListItem item ) {
                        LocalizedSimulation lsim = (LocalizedSimulation) item.getModel().getObject();
                        Link link = SimulationPage.createLink( "simulation-link", context, lsim );
                        link.add( new Label( "simulation-title", lsim.getTitle() ) );
                        item.add( link );
                    }
                } );

                item.add( new Label( "contribution-updated", format.format( contribution.getDateUpdated() ) ) );
                //logger.debug( "finish item" );
            }
        } );

        logger.debug( System.currentTimeMillis() + " finish init" );

    }

    public String getLevelString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        String ret = "";
        boolean started = false;
        for ( Object o : contribution.getLevels() ) {
            if ( started ) {
                ret += "<br/>";
            }
            started = true;

            ContributionLevel level = (ContributionLevel) o;
            String key = level.getLevel().getAbbreviatedTranslationKey();
            ret += localizer.getString( key, this );
        }
        return ret;
    }

    public String getTypeString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        String ret = "";
        boolean started = false;
        for ( Object o : contribution.getTypes() ) {
            if ( started ) {
                ret += "<br/>";
            }
            started = true;

            ContributionType type = (ContributionType) o;
            String key = type.getType().getAbbreviatedTranslationKey();
            ret += localizer.getString( key, this );
        }
        return ret;
    }

    public String getSimulationString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        String ret = "";
        boolean started = false;
        for ( Object o : contribution.getSimulations() ) {
            if ( started ) {
                ret += "<br/>";
            }
            started = true;

            Simulation sim = (Simulation) o;
            LocalizedSimulation lsim = sim.getBestLocalizedSimulation( getLocale() );

            // translated title
            ret += lsim.getTitle();
        }
        return ret;
    }

}