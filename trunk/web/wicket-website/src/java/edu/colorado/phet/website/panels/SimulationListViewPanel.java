package edu.colorado.phet.website.panels;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.cache.EventDependency;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.content.NotFoundPage;
import edu.colorado.phet.website.data.*;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class SimulationListViewPanel extends PhetPanel {

    private static Logger logger = Logger.getLogger( SimulationListViewPanel.class.getName() );
    private Category category;

    public SimulationListViewPanel( String id, String pagePath, final String categories, boolean showIndex, final PageContext context ) {
        super( id, context );

        final List<LocalizedSimulation> simulations = new LinkedList<LocalizedSimulation>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                if ( categories != null ) {
                    category = Category.getCategoryFromPath( getHibernateSession(), categories );
                    if ( category != null ) {
                        addSimulationsFromCategory( simulations, getMyLocale(), category );
                    }
                }
                else {
                    HibernateUtils.addPreferredFullSimulationList( simulations, getHibernateSession(), context.getLocale() );
                    HibernateUtils.orderSimulations( simulations, context.getLocale() );
                }
                return true;
            }
        } );

        if ( category == null && categories != null ) {
            // didn't find the category
            // TODO: write log message?
            throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
        }

        if ( showIndex ) {
            HibernateUtils.orderSimulations( simulations, context.getLocale() );
            SimulationIndexPanel indexPanel = new SimulationIndexPanel( "simulation-display-panel", context, simulations );
            add( indexPanel );

            add( new InvisibleComponent( "to-index-view" ) );
            add( new PhetLink( "to-thumbnail-view", context.getPrefix() + pagePath.substring( 0, pagePath.length() - 6 ) ) );
            add( new IndexLetterLinks( "letter-links", context, indexPanel.getLetters() ) );
        }
        else {
            add( new SimulationDisplayPanel( "simulation-display-panel", context, simulations ) );

            add( new PhetLink( "to-index-view", context.getPrefix() + pagePath + "/index" ) );
            add( new InvisibleComponent( "to-thumbnail-view" ) );
            add( new InvisibleComponent( "letter-links" ) );
        }

        addDependency( new EventDependency() {

            private IChangeListener stringListener;

            @Override
            protected void addListeners() {
                stringListener = createTranslationChangeInvalidator( context.getLocale() );
                HibernateEventListener.addListener( Project.class, getAnyChangeInvalidator() );
                HibernateEventListener.addListener( TranslatedString.class, stringListener );
                HibernateEventListener.addListener( Simulation.class, getAnyChangeInvalidator() );
                HibernateEventListener.addListener( LocalizedSimulation.class, getAnyChangeInvalidator() );
            }

            @Override
            protected void removeListeners() {
                HibernateEventListener.removeListener( Project.class, getAnyChangeInvalidator() );
                HibernateEventListener.removeListener( TranslatedString.class, stringListener );
                HibernateEventListener.removeListener( Simulation.class, getAnyChangeInvalidator() );
                HibernateEventListener.removeListener( LocalizedSimulation.class, getAnyChangeInvalidator() );
            }
        } );

        NavLocation location;

        if ( category == null ) {
            location = getNavMenu().getLocationByKey( "all" );
        }
        else {
            location = category.getNavLocation( getNavMenu() );
        }

        addCacheParameter( "location", location );
    }

    // NOTE: must be in a transaction
    private static void addSimulationsFromCategory( List<LocalizedSimulation> simulations, Locale locale, Category category ) {
        addSimulationsFromCategory( simulations, locale, category, new HashSet<Integer>() );
    }

    private static void addSimulationsFromCategory( List<LocalizedSimulation> simulations, Locale locale, Category category, Set<Integer> used ) {
        for ( Object o : category.getSimulations() ) {
            Simulation sim = (Simulation) o;
            if ( !sim.isVisible() ) {
                continue;
            }
            LocalizedSimulation englishSim = null;
            boolean added = false;
            for ( Object p : sim.getLocalizedSimulations() ) {
                LocalizedSimulation lsim = (LocalizedSimulation) p;
                if ( lsim.getLocale().equals( locale ) ) {
                    added = true;
                    if ( !used.contains( lsim.getId() ) ) {
                        simulations.add( lsim );
                        used.add( lsim.getId() );
                    }

                    break;
                }
                else if ( lsim.getLocale().equals( PhetWicketApplication.getDefaultLocale() ) ) {
                    englishSim = lsim;
                }
            }
            if ( !added && englishSim != null ) {
                simulations.add( englishSim );
                used.add( englishSim.getId() );
            }
        }
        if ( category.isAuto() ) {
            for ( Object o : category.getSubcategories() ) {
                Category subcategory = (Category) o;
                addSimulationsFromCategory( simulations, locale, subcategory, used );
            }
        }
    }

    public Category getCategory() {
        return category;
    }
}
