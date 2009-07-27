package edu.colorado.phet.wickettest.panels;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetLink;

public class SimulationListPanel extends PhetPanel {

    public SimulationListPanel( String id, PageContext context ) {
        super( id, context );

        List<LocalizedSimulation> tsims = new LinkedList<LocalizedSimulation>();

        Session session = context.getSession();
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
                LocalizedSimulation simulation = (LocalizedSimulation) item.getModel().getObject();
                Label title = new Label( "title", simulation.getTitle() );
                PhetLink link = new PhetLink( "simulation-link", "#" );
                link.add( title );
                item.add( link );
            }
        };
        add( simulationList );
    }

}