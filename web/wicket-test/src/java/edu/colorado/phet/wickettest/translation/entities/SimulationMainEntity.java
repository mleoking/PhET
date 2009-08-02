package edu.colorado.phet.wickettest.translation.entities;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.panels.SimulationMainPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class SimulationMainEntity extends TranslationEntity {
    public SimulationMainEntity() {
        addString( "simulationMainPanel.translatedVersions" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                LocalizedSimulation simulation = null;
                Session session = requestCycle.getHibernateSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    Query query = session.createQuery( "select ls from LocalizedSimulation as ls, Simulation as s where (ls.simulation = s and s.name = 'pendulum-lab' and ls.locale = :locale)" );

                    simulation = (LocalizedSimulation) query.setLocale( "locale", LocaleUtils.stringToLocale( "en" ) ).uniqueResult();

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

                return new SimulationMainPanel( id, simulation, context );
            }
        }, "default" );
    }

    public String getDisplayName() {
        return "Simulation";
    }
}
