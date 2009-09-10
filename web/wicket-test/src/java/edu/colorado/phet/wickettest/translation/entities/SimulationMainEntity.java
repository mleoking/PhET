package edu.colorado.phet.wickettest.translation.entities;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.panels.SimulationMainPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class SimulationMainEntity extends TranslationEntity {
    public SimulationMainEntity() {
        addString( "simulationMainPanel.translatedVersions" );
        addString( "simulationMainPanel.screenshot.alt", "{0} will by replaced by the title of the simulation" );
        addString( "simulationMainPanel.version", "{0} will be replaced by the version number" );
        addString( "simulationMainPanel.kilobytes", "{0} will by replaced by the size of the simulation download (number of kilobytes)" );
        addString( "simulationMainPanel.runOffline" );
        addString( "simulationMainPanel.runOnline" );
        addString( "simulationMainPanel.topics" );
        addString( "simulationMainPanel.mainTopics" );
        addString( "simulationMainPanel.keywords" );
        addString( "simulationMainPanel.softwareRequirements" );
        addString( "simulationMainPanel.sampleLearningGoals" );
        addString( "simulationMainPanel.credits" );
        addString( "simulationMainPanel.language" );
        addString( "simulationMainPanel.languageTranslated" );
        addString( "simulationMainPanel.simulationTitleTranslated" );
        addString( "simulationMainPanel.designTeam" );
        addString( "simulationMainPanel.thirdPartyLibraries" );
        addString( "simulationMainPanel.thanksTo" );
        addString( "simulationMainPanel.untranslatedMessage" );
        addString( "macwarning.title" );
        addString( "macwarning.problem" );
        addString( "macwarning.solution" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {

                Session session = requestCycle.getHibernateSession();
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

                return new SimulationMainPanel( id, simulation, context );
            }
        }, "Main Simulation Page" );
    }

    public String getDisplayName() {
        return "Simulation";
    }
}
