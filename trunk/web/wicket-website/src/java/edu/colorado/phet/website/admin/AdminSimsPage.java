package edu.colorado.phet.website.admin;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class AdminSimsPage extends AdminPage {

    private static final Logger logger = Logger.getLogger( AdminSimsPage.class.getName() );

    public AdminSimsPage( PageParameters parameters ) {
        super( parameters );

        Session session = getHibernateSession();
        final List<Simulation> simulations = new LinkedList<Simulation>();
        final Map<Simulation, LocalizedSimulation> englishSims = new HashMap<Simulation, LocalizedSimulation>();

        Locale english = LocaleUtils.stringToLocale( "en" );

        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            List sims = session.createQuery( "select s from Simulation as s" ).list();

            for ( Object sim : sims ) {
                Simulation simulation = (Simulation) sim;
                simulations.add( simulation );
                for ( Object o : simulation.getLocalizedSimulations() ) {
                    LocalizedSimulation lsim = (LocalizedSimulation) o;
                    if ( lsim.getLocale().equals( english ) ) {
                        englishSims.put( simulation, lsim );
                        break;
                    }
                }
            }

            tx.commit();
        }
        catch( RuntimeException e ) {
            logger.warn( e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    logger.error( "ERROR: Error rolling back transaction", e1 );
                }
                throw e;
            }
        }

        Collections.sort( simulations, new Comparator<Simulation>() {
            public int compare( Simulation a, Simulation b ) {
                return a.getName().compareTo( b.getName() );
            }
        } );

        ListView simulationList = new ListView( "simulation-list", simulations ) {
            protected void populateItem( ListItem item ) {
                final Simulation simulation = (Simulation) item.getModel().getObject();
                LocalizedSimulation lsim = englishSims.get( simulation );
                Link simLink = new Link( "simulation-link" ) {
                    public void onClick() {
                        PageParameters params = new PageParameters();
                        params.put( "simulationId", simulation.getId() );
                        setResponsePage( AdminSimPage.class, params );
                    }
                };
                simLink.add( new Label( "simulation-name", simulation.getName() ) );
                item.add( simLink );
                Link projectLink = new Link( "project-link" ) {
                    public void onClick() {
                        PageParameters params = new PageParameters();
                        params.put( "projectId", simulation.getProject().getId() );
                        setResponsePage( AdminProjectPage.class, params );
                    }
                };
                projectLink.add( new Label( "project-name", simulation.getProject().getName() ) );
                item.add( projectLink );
                if ( lsim == null ) {
                    item.add( new Label( "simulation-title", "?" ) );
                }
                else {
                    item.add( new Label( "simulation-title", lsim.getTitle() ) );
                }
                Link removeLink = new Link( "remove-link" ) {
                    public void onClick() {
                        try {
                            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                                public boolean run( Session session ) {
                                    Simulation s = (Simulation) session.load( Simulation.class, simulation.getId() );
                                    // TODO: add checks and error messages for ways this can fail
                                    session.delete( s );
                                    return true;
                                }
                            } );
                            if ( success ) {
                                simulations.remove( simulation );
                            }
                        }
                        catch( RuntimeException e ) {
                            e.printStackTrace();
                        }
                    }
                };
                item.add( removeLink );
            }
        };

        add( simulationList );

    }
}
