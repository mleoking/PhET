package edu.colorado.phet.wickettest.panels;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.content.SimulationPage;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import static edu.colorado.phet.wickettest.util.HtmlUtils.encode;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.StaticImage;

public class SimulationDisplayPanel extends PhetPanel {

    public SimulationDisplayPanel( String id, final PageContext context ) {
        super( id, context );

        List<LocalizedSimulation> simulations = new LinkedList<LocalizedSimulation>();

        Session session = context.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            simulations = HibernateUtils.getAllSimulationsS( session, getMyLocale() );

            tx.commit();
        }
        catch( RuntimeException e ) {
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
        }

        IDataProvider simData = new SimulationDataProvider( simulations );
        GridView gridView = new GridView( "rows", simData ) {
            @Override
            protected void populateEmptyItem( Item item ) {
                item.setVisible( false );
            }

            @Override
            protected void populateItem( Item item ) {
                LocalizedSimulation simulation = (LocalizedSimulation) item.getModelObject();
                PhetLink link = SimulationPage.createLink( "simulation-link", getMyLocale(), simulation );
                link.add( new Label( "title", simulation.getTitle() ) );
                link.add( new StaticImage( "thumbnail", simulation.getSimulation().getThumbnailUrl(), MessageFormat.format( "Screenshot of the simulation {0}", encode( simulation.getTitle() ) ) ) );
                item.add( link );
            }
        };
        gridView.setColumns( 3 );
        add( gridView );

    }

    private static class SimulationDataProvider implements IDataProvider {
        private List<LocalizedSimulation> simulations;

        private SimulationDataProvider( List<LocalizedSimulation> simulations ) {
            this.simulations = simulations;
        }


        public Iterator iterator( int first, int count ) {
            int endIndex = first + count;
            if ( endIndex > simulations.size() ) {
                endIndex = simulations.size();
            }
            return simulations.subList( first, endIndex ).iterator();
        }

        public int size() {
            return simulations.size();
        }

        public IModel model( final Object o ) {
            return new IModel() {
                public Object getObject() {
                    return o;
                }

                public void setObject( Object o ) {

                }

                public void detach() {

                }
            };
        }

        public void detach() {

        }
    }

}