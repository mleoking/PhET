package edu.colorado.phet.website.content.simulations;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;
import org.hibernate.event.PostUpdateEvent;

import edu.colorado.phet.website.cache.CacheableUrlStaticPanel;
import edu.colorado.phet.website.cache.EventDependency;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.TranslatedString;
import edu.colorado.phet.website.data.util.AbstractChangeListener;
import edu.colorado.phet.website.data.util.HibernateEventListener;
import edu.colorado.phet.website.data.util.IChangeListener;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.*;

public class TranslationLocaleListPanel extends PhetPanel implements CacheableUrlStaticPanel {

    private static final Logger logger = Logger.getLogger( TranslationLocaleListPanel.class.getName() );

    public TranslationLocaleListPanel( String id, final PageContext context ) {
        super( id, context );

        // TODO: remove unnecessary steps

        add( HeaderContributor.forCss( CSS.TRANSLATED_SIMS ) );

        final List<LocalizedSimulation> localizedSimulations = new LinkedList<LocalizedSimulation>();
        final Map<Locale, List<LocalizedSimulation>> localeMap = new HashMap<Locale, List<LocalizedSimulation>>();
        final List<Locale> locales = new LinkedList<Locale>();
        final Map<Locale, String> localeNames = new HashMap<Locale, String>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {

                List lsims = session.createQuery( "select ls from LocalizedSimulation as ls" ).list();

                for ( Object lsim : lsims ) {
                    LocalizedSimulation localizedSimulation = (LocalizedSimulation) lsim;

                    if ( !localizedSimulation.getSimulation().isVisible() ) {
                        continue;
                    }

                    localizedSimulations.add( localizedSimulation );

                    List<LocalizedSimulation> localeSimList = localeMap.get( localizedSimulation.getLocale() );
                    if ( localeSimList == null ) {
                        localeSimList = new LinkedList<LocalizedSimulation>();
                        localeMap.put( localizedSimulation.getLocale(), localeSimList );
                    }

                    localeSimList.add( localizedSimulation );
                }

                return true;
            }
        } );

        for ( Locale locale : localeMap.keySet() ) {
            locales.add( locale );
            localeNames.put( locale, StringUtils.getLocaleTitle( locale, context.getLocale(), (PhetLocalizer) getLocalizer() ) );
        }

        Collections.sort( locales, new Comparator<Locale>() {
            public int compare( Locale a, Locale b ) {
                return localeNames.get( a ).compareTo( localeNames.get( b ) );
            }
        } );

        for ( List<LocalizedSimulation> simulationList : localeMap.values() ) {
            HibernateUtils.orderSimulations( simulationList, context.getLocale() );
        }

        add( new ListView( "locale-list", locales ) {
            protected void populateItem( ListItem item ) {
                Locale locale = (Locale) item.getModel().getObject();

                // TODO: override with language.name when possible
                item.add( new Label( "locale-title-translated", locale.getDisplayName( locale ) ) );

                //RawLink link = new RawLink( "locale-link", "#" + LocaleUtils.localeToString( locale ) );
                Link link = TranslatedSimsPage.getLinker( locale ).getLink( "locale-link", context, getPhetCycle() );
                link.add( new Label( "locale-title", localeNames.get( locale ) ) );
                item.add( link );

                item.add( new Label( "number-of-translations", String.valueOf( localeMap.get( locale ).size() ) ) );

                WicketUtils.highlightListItem( item );

            }
        } );

        // TODO: refine dependencies
        addDependency( new EventDependency() {

            private IChangeListener projectListener;
            private IChangeListener stringListener;

            @Override
            protected void addListeners() {
                projectListener = new AbstractChangeListener() {
                    public void onUpdate( Object object, PostUpdateEvent event ) {
                        if ( HibernateEventListener.getSafeHasChanged( event, "visible" ) ) {
                            invalidate();
                        }
                    }
                };
                stringListener = createTranslationChangeInvalidator( context.getLocale() );
                HibernateEventListener.addListener( Project.class, projectListener );
                HibernateEventListener.addListener( TranslatedString.class, stringListener );
                HibernateEventListener.addListener( Simulation.class, getAnyChangeInvalidator() );
                HibernateEventListener.addListener( LocalizedSimulation.class, getAnyChangeInvalidator() );
            }

            @Override
            protected void removeListeners() {
                HibernateEventListener.removeListener( Project.class, projectListener );
                HibernateEventListener.removeListener( TranslatedString.class, stringListener );
                HibernateEventListener.removeListener( Simulation.class, getAnyChangeInvalidator() );
                HibernateEventListener.removeListener( LocalizedSimulation.class, getAnyChangeInvalidator() );
            }
        } );

    }

}