package edu.colorado.phet.website.panels.contribution;

import java.text.DateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.cache.EventDependency;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionLevel;
import edu.colorado.phet.website.data.contribution.ContributionType;
import edu.colorado.phet.website.data.util.HibernateEventListener;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.WicketUtils;
import edu.colorado.phet.website.constants.Images;
import edu.colorado.phet.website.constants.CSS;

public class ContributionBrowsePanel extends PhetPanel {

    private final List<Contribution> newContributions;

    private static final Logger logger = Logger.getLogger( ContributionBrowsePanel.class.getName() );

    /**
     * NOTE: ALWAYS preload the levels, types, simulations, and those simulations' localizedSimulations. They are all
     * lazy fields, and will throw errors if that isn't done.
     *
     * @param id            Wicket id
     * @param context       Page context
     * @param contributions List of contributions
     */
    public ContributionBrowsePanel( String id, final PageContext context, final List<Contribution> contributions ) {
        this( id, context, contributions, true );
    }

    /**
     * NOTE: ALWAYS preload the levels, types, simulations, and those simulations' localizedSimulations. They are all
     * lazy fields, and will throw errors if that isn't done.
     *
     * @param id                      Wicket id
     * @param context                 Page context
     * @param contributions           List of contributions
     * @param simulationColumnVisible Whether the simulations column should be displayed
     */
    public ContributionBrowsePanel( String id, final PageContext context, final List<Contribution> contributions, final boolean simulationColumnVisible ) {
        super( id, context );

        logger.debug( System.currentTimeMillis() + " start" );

        add( HeaderContributor.forCss( CSS.CONTRIBUTION_MAIN ) );

        newContributions = new LinkedList<Contribution>();

        logger.debug( System.currentTimeMillis() + " A" );

        // TODO: sort things like level and type, and maybe simulations (if not done already)
        // TODO: add sortability of columns
        // TODO: add locale (language) for main page

        final DateFormat format = DateFormat.getDateInstance( DateFormat.SHORT, getLocale() );

        Collections.sort( contributions, new Comparator<Contribution>() {
            public int compare( Contribution a, Contribution b ) {
                return a.displayCompareTo( b, getLocale() );
            }
        } );

        logger.debug( System.currentTimeMillis() + " B" );

        if ( contributions.isEmpty() ) {
            add( new InvisibleComponent( "contributions" ) );
        }
        else {

            if ( simulationColumnVisible ) {
                add( new Label( "sim-col-1", "" ) );
            }
            else {
                add( new InvisibleComponent( "sim-col-1" ) );
            }

            add( new ListView( "contributions", contributions ) {
                private HashMap<Simulation, LocalizedSimulation> mapCache = new HashMap<Simulation, LocalizedSimulation>();

                protected void populateItem( ListItem item ) {
                    //logger.debug( "start item" );
                    Contribution contribution = (Contribution) item.getModel().getObject();
                    Link link = ContributionPage.getLinker( contribution ).getLink( "contribution-link", context, getPhetCycle() );
                    link.add( new Label( "contribution-title", contribution.getTitle() ) );
                    item.add( link );
                    item.add( new Label( "contribution-authors", contribution.getAuthors() ) );

                    item.add( new RawLabel( "contribution-level", getLevelString( contribution ) ) );

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
                        item.add( new StaticImage( "phet-contribution", Images.PHET_LOGO_ICON_SMALL, "Contribution by the PhET team" ) );
                    }
                    else {
                        item.add( new InvisibleComponent( "phet-contribution" ) );
                    }

                    if ( contribution.isGoldStar() ) {
                        // TODO: localize alt
                        // TODO: add title?
                        item.add( new StaticImage( "gold-star-contribution", Images.GOLD_STAR_SMALL, "Gold Star Contribution" ) );
                    }
                    else {
                        item.add( new InvisibleComponent( "gold-star-contribution" ) );
                    }

                    // add the list in
                    if ( simulationColumnVisible ) {
                        WebMarkupContainer container = new WebMarkupContainer( "contribution-simulation-list" );
                        item.add( container );
                        container.add( new ListView( "contribution-simulations", lsims ) {
                            protected void populateItem( ListItem item ) {
                                LocalizedSimulation lsim = (LocalizedSimulation) item.getModel().getObject();
                                Link link = SimulationPage.getLinker( lsim ).getLink( "simulation-link", context, getPhetCycle() );
                                link.add( new Label( "simulation-title", lsim.getTitle() ) );
                                item.add( link );
                            }
                        } );
                    }
                    else {
                        item.add( new InvisibleComponent( "contribution-simulation-list" ) );
                    }

                    item.add( new Label( "contribution-updated", format.format( contribution.getDateUpdated() ) ) );
                    //logger.debug( "finish item" );

                    WicketUtils.highlightListItem( item );
                }
            } );

        }

        addDependency( new EventDependency() {

            @Override
            protected void addListeners() {
                HibernateEventListener.addListener( Contribution.class, getAnyChangeInvalidator() );
            }

            @Override
            protected void removeListeners() {
                HibernateEventListener.removeListener( Contribution.class, getAnyChangeInvalidator() );
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