package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.hibernate.Session;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.admin.AdminPage;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.panels.ComponentThreadStatusPanel;
import edu.colorado.phet.website.panels.LoggerComponentThread;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class DeployResourcePage extends AdminPage {

    private static final Logger logger = Logger.getLogger( DeployResourcePage.class.getName() );
    private static final Set<File> usedDirs = new HashSet<File>();
    private static final HashMap<File, LoggerComponentThread> serverMap = new HashMap<File, LoggerComponentThread>();
    private static final HashMap<File, List<String>> projectsMap = new HashMap<File, List<String>>();
    private static final Object usedLock = new Object();

    public DeployResourcePage( PageParameters parameters ) {
        super( parameters );

        add( new Label( "dir", parameters.getString( "dir" ) ) );

        final File resourceTmpDir = new File( parameters.getString( "dir" ) );

        logger.info( "Loading resource deployment page for " + resourceTmpDir );

        try {
            if ( !resourceTmpDir.getCanonicalPath().startsWith( PhetWicketApplication.get().getStagingRoot().getCanonicalPath() ) ) {
                logger.error( "unacceptable resource dir path: " + resourceTmpDir.getCanonicalPath() );
                logger.error( "not under " + PhetWicketApplication.get().getStagingRoot().getCanonicalPath() );
                add( new Label( "response", "PATH error, see logger" ) );
                return;
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        if ( !resourceTmpDir.exists() ) {
            add( new Label( "response", "PATH error, directory does not exist" ) );
            return;
        }

        if ( new File( resourceTmpDir, "finished.txt" ).exists() ) {
//            add( new WebsiteResourceFinishedPanel(
//                    "response",
//                    getPageContext(),
//                    "Ready for testing",
//                    resourceTmpDir,
//                    PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot()
//            ) );
            LoggerComponentThread thread = serverMap.get( resourceTmpDir );
            addThreadStatus( "response", thread );
        }
        else if ( new File( resourceTmpDir, "error.txt" ).exists() ) {
            //add( new Label( "response", "There was an error encountered in the processing" ) );
            LoggerComponentThread thread = serverMap.get( resourceTmpDir );
            addThreadStatus( "response", thread );
        }
        else {
            boolean ok;
            synchronized( usedLock ) {
                ok = !usedDirs.contains( resourceTmpDir );
                if ( ok ) {
                    usedDirs.add( resourceTmpDir );
                }
            }
            if ( ok ) {
                startServer( resourceTmpDir );
            }
            else {
//                add( new Label( "response", "This directory is being processed or another error has occurred. " +
//                                            "Please refer to the original window." ) );
                LoggerComponentThread thread = serverMap.get( resourceTmpDir );
                addThreadStatus( "response", thread );
            }
        }
    }

    private void startServer( final File resourceTmpDir ) {
        final String jarPath = PhetWicketApplication.get().getWebsiteProperties().getPathToJarUtility();
        final File docRoot = PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot();
        final File liveSimsDir = PhetWicketApplication.get().getSimulationsRoot();

        final List<Logger> loggers = new LinkedList<Logger>();

        loggers.add( Logger.getRootLogger() );
        //loggers.add( Logger.getLogger( WebsiteResourceDeployServer.class ) );
        //loggers.add( Logger.getLogger( Project.class ) );
        //loggers.add( Logger.getLogger( "edu.colorado.phet.buildtools" ) );
        //loggers.add( Logger.getLogger( "edu.colorado.phet.phetcommon" ) );

        LoggerComponentThread thread = new LoggerComponentThread( loggers ) {

            private WebsiteResourceDeployServer runner;

            @Override
            public boolean process() throws IOException, InterruptedException {
                logger.info( "process() for the resource server deployment thread" );
                runner = new WebsiteResourceDeployServer(
                        jarPath,
                        resourceTmpDir
                );

                runner.process( liveSimsDir );

                FileUtils.writeString( new File( resourceTmpDir, "finished.txt" ), "finished at " + new Date() );
                return true;
            }

            @Override
            public Component getFinishedComponent( String id, String rawText ) {
                //return new TranslationDeployServerFinishedPanel( id, getPageContext(), rawText, resourceTmpDir, PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot() );
                if ( didError() ) {
                    return new RawLabel( id, rawText );
                }
                else {
                    return new WebsiteResourceFinishedPanel( id, PageContext.getNewDefaultContext(), rawText, resourceTmpDir, PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot() );
                }
            }

            @Override
            public void onError() {
                try {
                    FileUtils.writeString( new File( resourceTmpDir, "error.txt" ), "errored at " + new Date() );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }

        };

        serverMap.put( resourceTmpDir, thread );

        addThreadStatus( "response", thread );

        thread.start();
    }

    private void addThreadStatus( String id, LoggerComponentThread thread ) {
        add( new ComponentThreadStatusPanel( id, getPageContext(), thread, 1000 ) );
    }

    private static class ServerThread extends LoggerComponentThread {
        private final String jarPath;
        private final File docRoot;
        private final File resourceTmpDir;

        protected ServerThread( String jarPath, File docRoot, File resourceTmpDir ) {
            super( Logger.getRootLogger() );
            this.jarPath = jarPath;
            this.docRoot = docRoot;
            this.resourceTmpDir = resourceTmpDir;
        }

        private WebsiteResourceDeployServer runner;

        @Override
        public boolean process() throws IOException, InterruptedException {
            runner = new WebsiteResourceDeployServer(
                    jarPath,
                    resourceTmpDir
            );

            projectsMap.put( resourceTmpDir, runner.getExistingProjects() );

            runner.process( new File( docRoot, "sims" ) );

//                Session session = HibernateUtils.getInstance().openSession();
//                for ( String projectName : runner.getExistingProjects() ) {
//                    Project.backupProject( docRoot, projectName );
//                }
//                session.close();

            FileUtils.writeString( new File( resourceTmpDir, "finished.txt" ), "finished at " + new Date() );
            return true;
        }

        @Override
        public Component getFinishedComponent( String id, String rawText ) {
            //return new TranslationDeployServerFinishedPanel( id, getPageContext(), rawText, resourceTmpDir, PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot() );
            //return new RawLabel( id, rawText );
            return new WebsiteResourceFinishedPanel( id, PageContext.getNewDefaultContext(), rawText, resourceTmpDir, docRoot );
        }

        @Override
        public void onError() {
            try {
                FileUtils.writeString( new File( resourceTmpDir, "error.txt" ), "errored at " + new Date() );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public static RawLinkable getLinker( final String dir ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "admin/deploy-resource?dir=" + dir;
            }
        };
    }

}