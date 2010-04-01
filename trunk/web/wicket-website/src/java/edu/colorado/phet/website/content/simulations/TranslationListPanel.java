package edu.colorado.phet.website.content.simulations;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.Session;
import org.hibernate.event.PostUpdateEvent;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.cache.CacheableUrlStaticPanel;
import edu.colorado.phet.website.cache.EventDependency;
import edu.colorado.phet.website.components.InvisibleComponent;
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

public class TranslationListPanel extends PhetPanel implements CacheableUrlStaticPanel {

    private static Logger logger = Logger.getLogger( TranslationListPanel.class.getName() );

    public TranslationListPanel( String id, final PageContext context, final Locale locale ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/translated-sims-v1.css" ) );

        final List<LocalizedSimulation> localizedSimulations = new LinkedList<LocalizedSimulation>();
        final Map<Simulation, String> simNameDefault = new HashMap<Simulation, String>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {

                List lsims = session.createQuery( "select ls from LocalizedSimulation as ls where ls.locale = :locale or ls.locale = :default" )
                        .setLocale( "locale", locale ).setLocale( "default", context.getLocale() ).list();

                for ( Object lsim : lsims ) {
                    LocalizedSimulation localizedSimulation = (LocalizedSimulation) lsim;

                    if ( localizedSimulation.getLocale().equals( context.getLocale() ) ) {
                        simNameDefault.put( localizedSimulation.getSimulation(), localizedSimulation.getTitle() );
                    }

                    if ( !localizedSimulation.getSimulation().isVisible() || !localizedSimulation.getLocale().equals( locale ) ) {
                        continue;
                    }

                    localizedSimulations.add( localizedSimulation );

                }

                return true;
            }
        } );

        HibernateUtils.orderSimulations( localizedSimulations, context.getLocale() );

        // TODO: localize order of translated title: {0} - {1} ex.
        add( new Label( "locale-header", locale.getDisplayName( locale ) + " " + StringUtils.getLocaleTitle( locale, context.getLocale(), (PhetLocalizer) getLocalizer() ) ) );
        add( new ListView( "translations", localizedSimulations ) {
            protected void populateItem( ListItem item ) {
                LocalizedSimulation lsim = (LocalizedSimulation) item.getModel().getObject();

                Link simLink = SimulationPage.createLink( "sim-link", context, lsim );
                simLink.add( new Label( "title-translated", lsim.getTitle() ) );
                item.add( simLink );

                String otherTitle = simNameDefault.get( lsim.getSimulation() );
                if ( otherTitle == null ) {
                    otherTitle = "-";
                }
                item.add( new Label( "title", otherTitle ) );
                item.add( lsim.getRunLink( "run-now-link" ) );

                if ( DistributionHandler.displayJARLink( (PhetRequestCycle) getRequestCycle(), lsim ) ) {
                    item.add( lsim.getDownloadLink( "download-link" ) );
                }
                else {
                    item.add( new InvisibleComponent( "download-link" ) );
                }

                if ( item.getIndex() % 2 == 0 ) {
                    item.add( new AttributeAppender( "class", new Model( "highlight-background" ), " " ) );
                }
            }
        } );

        // TODO: update dependencies to narrow
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