package edu.colorado.phet.wickettest.test;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.util.HibernateUtils;

public class TestSchema {
    private static File trunk;

    public static void main( String[] args ) {
        trunk = new File( args[0] );
        BuildLocalProperties.initRelativeToTrunk( trunk );

        List<PhetProject> projects = new LinkedList<PhetProject>();
        projects.addAll( Arrays.asList( JavaProject.getJavaSimulations( trunk ) ) );
        projects.addAll( Arrays.asList( FlashSimulationProject.getFlashSimulations( trunk ) ) );

        Transaction tx = null;
        Session session = HibernateUtils.getInstance().getCurrentSession();
        try {
            tx = session.beginTransaction();

            for ( PhetProject project : projects ) {

                BasicProject basicProject = new BasicProject();
                basicProject.setName( project.getName() );
                session.save( basicProject );

                System.out.println( "Project: " + project.getName() );
                Locale[] locales = project.getLocales();
                for ( String simName : project.getSimulationNames() ) {

                    BasicSimulation bs = new BasicSimulation();
                    bs.setName( simName );
                    bs.setProject( basicProject );
                    bs.setType( project instanceof JavaSimulationProject ? 0 : 1 );
                    session.save( bs );

                    System.out.println( "   : " + simName );
                    for ( Locale locale : locales ) {
                        Simulation sim = project.getSimulation( simName, locale );

                        BasicLocalizedSimulation bls = new BasicLocalizedSimulation();
                        bls.setLocale( locale );
                        bls.setSimulation( bs );
                        bls.setTitle( sim.getTitle() );
                        bls.setDescription( sim.getDescription() );
                        session.save( bls );

                        System.out.println( "       : " + LocaleUtils.localeToString( locale ) );
                    }
                }
            }

            tx.commit();
        }
        catch( RuntimeException e ) {
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "Error rolling back transaction" );
                }
                throw e;
            }
        }


    }
}
