package edu.colorado.phet.wickettest.panels;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.test.BasicLocalizedSimulation;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PhetLink;

public class SimulationListPanel extends PhetPanel {

    public SimulationListPanel( String id, final Locale myLocale ) {
        super( id, myLocale );

        /*
        List<SimulationModel> models = SqlUtils.getOrderedSimulationModels( getContext(), myLocale );

        ListView simulationList = new ListView( "simulation-list", models ) {
            protected void populateItem( ListItem item ) {
                WebSimulation simulation = (WebSimulation) ( ( (SimulationModel) ( item.getModel().getObject() ) ).getObject() );

                Label title = new Label( "title", simulation.getTitle() );
                PhetLink link = SimulationPage.createLink( "simulation-link", getMyLocale(), simulation );
                link.add( title );
                item.add( link );
            }
        };
        add( simulationList );
        */

        List<BasicLocalizedSimulation> tsims = new LinkedList<BasicLocalizedSimulation>();

        Session session = HibernateUtils.getInstance().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            /*
            List<BasicSimulation> simulations = HibernateUtils.getAllSimulationsT();
            for ( BasicSimulation simulation : simulations ) {
                tsims.add( simulation.getBestLocalizedSimulation( getMyLocale() ) );
            }
            */
            tsims = HibernateUtils.getAllSimulationsTX( getMyLocale() );

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

        ListView simulationList = new ListView( "simulation-list", tsims ) {
            protected void populateItem( ListItem item ) {
                System.out.println( "item " + item );
                System.out.println( "item.getModel() " + item.getModel().getClass().getCanonicalName() );
                System.out.println( "item.getModel().getObject() " + item.getModel().getObject().getClass().getCanonicalName() );
                //System.out.println( "item.getModel().getObject().getObject() " + item.getModel().getObject().getObject().getClass().getCanonicalName() );
                BasicLocalizedSimulation simulation = (BasicLocalizedSimulation) item.getModel().getObject();

                Label title = new Label( "title", simulation.getTitle() );
                PhetLink link = new PhetLink( "simulation-link", "#" );
                link.add( title );
                item.add( link );
            }
        };
        add( simulationList );
    }

}