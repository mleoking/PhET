package edu.colorado.phet.wickettest.panels;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.test.BasicLocalizedSimulation;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.PhetPage;

public class SimulationListPanel extends PhetPanel {

    public SimulationListPanel( String id, PhetPage page ) {
        super( id, page.getMyLocale() );

        List<BasicLocalizedSimulation> tsims = new LinkedList<BasicLocalizedSimulation>();

        Session session = page.getHibernateSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            tsims = HibernateUtils.getAllSimulationsS( session, getMyLocale() );

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