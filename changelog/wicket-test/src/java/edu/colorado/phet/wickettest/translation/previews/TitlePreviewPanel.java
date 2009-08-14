package edu.colorado.phet.wickettest.translation.previews;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.menu.NavLocation;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;

public class TitlePreviewPanel extends PhetPanel {
    public TitlePreviewPanel( String id, PageContext context ) {
        super( id, context );

        Session session = getHibernateSession();
        LocalizedSimulation simulation = null;
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            simulation = HibernateUtils.getExampleSimulation( session, context.getLocale() );

            tx.commit();
        }
        catch( RuntimeException e ) {
            System.out.println( "Exception: " + e );
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

        NavLocation location = getNavMenu().getLocationByKey( "motion" );

        add( new Label( "home", new ResourceModel( "home.title" ) ) );
        add( new Label( "simulationPage", new StringResourceModel( "simulationPage.title", this, null, new String[]{simulation.getTitle(), simulation.getSimulation().getProject().getVersionString()} ) ) );
        add( new Label( "simulationDisplay", new StringResourceModel( "simulationDisplay.title", this, null, new Object[]{new StringResourceModel( location.getLocalizationKey(), this, null )} ) ) );
    }
}
