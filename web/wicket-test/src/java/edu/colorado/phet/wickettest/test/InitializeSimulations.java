package edu.colorado.phet.wickettest.test;

import java.io.File;
import java.util.*;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.data.Project;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.util.HibernateUtils;

public class InitializeSimulations {
    private static File trunk;

    private static Set<String> ignoreProjects = new HashSet<String>();

    public static void main( String[] args ) {
        trunk = new File( args[0] );
        BuildLocalProperties.initRelativeToTrunk( trunk );

        ignoreProjects.add( "acid-base-solutions" );
        ignoreProjects.add( "charges-and-fields-scala" );
        ignoreProjects.add( "density" );
        ignoreProjects.add( "force-law-lab" );
        ignoreProjects.add( "java-common-strings" );
        ignoreProjects.add( "ladybug-motion-2d" );
        ignoreProjects.add( "mvc-example" );
        ignoreProjects.add( "natural-selection" );
        ignoreProjects.add( "nuclear-physics" );
        ignoreProjects.add( "phetgraphics-demo" );
        ignoreProjects.add( "sim-template" );
        ignoreProjects.add( "states-of-matter" );
        ignoreProjects.add( "the-ramp" );
        ignoreProjects.add( "titration" );
        ignoreProjects.add( "calculus-grapher" );
        ignoreProjects.add( "flash-common-strings" );
        ignoreProjects.add( "test-flash-project" );


        List<PhetProject> projects = new LinkedList<PhetProject>();
        projects.addAll( Arrays.asList( JavaProject.getJavaSimulations( trunk ) ) );
        projects.addAll( Arrays.asList( FlashSimulationProject.getFlashSimulations( trunk ) ) );

        Transaction tx = null;
        Session session = HibernateUtils.getInstance().getCurrentSession();
        try {
            tx = session.beginTransaction();

            for ( PhetProject project : projects ) {

                if ( ignoreProjects.contains( project.getName() ) ) {
                    continue;
                }

                Project oProject = new Project();
                oProject.setName( project.getName() );
                PhetVersion version = project.getVersion();
                oProject.setVersionMajor( version.getMajorAsInt() );
                oProject.setVersionMinor( version.getMinorAsInt() );
                oProject.setVersionDev( version.getDevAsInt() );
                oProject.setVersionRevision( version.getRevisionAsInt() );
                oProject.setVersionTimestamp( version.getTimestampSeconds() );
                session.save( oProject );

                System.out.println( "Project: " + project.getName() );
                Locale[] locales = project.getLocales();
                for ( String simName : project.getSimulationNames() ) {

                    Simulation oSim = new Simulation();
                    oSim.setName( simName );
                    oSim.setProject( oProject );
                    oSim.setType( project instanceof JavaSimulationProject ? 0 : 1 );
                    session.save( oSim );

                    System.out.println( "   : " + simName );
                    for ( Locale locale : locales ) {
                        edu.colorado.phet.buildtools.Simulation sim = project.getSimulation( simName, locale );

                        LocalizedSimulation oLocalSim = new LocalizedSimulation();
                        oLocalSim.setLocale( locale );
                        oLocalSim.setSimulation( oSim );

                        // TODO: refactor so that these actions are handled in one place to ensure consistency
                        oSim.getLocalizedSimulations().add( oLocalSim );
                        oLocalSim.setTitle( sim.getTitle() );

                        oLocalSim.setDescription( sim.getDescription() );
                        session.save( oLocalSim );

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
