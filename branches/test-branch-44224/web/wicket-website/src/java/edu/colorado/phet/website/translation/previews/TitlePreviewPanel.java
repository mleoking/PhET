package edu.colorado.phet.website.translation.previews;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class TitlePreviewPanel extends PhetPanel {

    private static final Logger logger = Logger.getLogger( TitlePreviewPanel.class.getName() );

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
            logger.warn( "Exception: " + e );
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

        NavLocation location = getNavMenu().getLocationByKey( "motion" );

        add( new Label( "home", new ResourceModel( "home.title" ) ) );
        add( new Label( "simulationPage", new StringResourceModel( "simulationPage.title", this, null, new String[]{simulation.getTitle(), "Electricity", "Magnetism", "Faraday's Law"} ) ) );
        add( new Label( "simulationDisplay", new StringResourceModel( "simulationDisplay.title", this, null, new Object[]{new StringResourceModel( location.getLocalizationKey(), this, null )} ) ) );
    }
}
