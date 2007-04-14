package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;

public class PhetBuildTask extends Task {
    private String projectName;

    // The method executing the task
    public void execute() throws BuildException {
        output( "Executing: " + projectName );
        try {
            buildProject();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public class PhetProject {
        private File rootDir;
        private Properties properties;
        private String name;

        public PhetProject( File parentDir, String name ) throws IOException {
            this.name = name;
            this.rootDir = new File( parentDir, name );
            this.properties = new Properties();
            File propertyFile = new File( rootDir, name + ".properties" );
            this.properties.load( new BufferedInputStream( new FileInputStream( propertyFile ) ) );
        }

        public boolean equals( Object obj ) {
            if( obj instanceof PhetProject ) {
                PhetProject phetProject = (PhetProject)obj;
                return phetProject.rootDir.equals( rootDir );
            }
            else {
                return false;
            }
        }

        public PhetProject( File projectRoot ) throws IOException {
            this( projectRoot.getParentFile(), projectRoot.getName() );
        }

        public String toString() {
            return "project=" + name + ", root=" + rootDir.getAbsolutePath() + ", properties=" + properties;
        }

        public String getSource() {
            return properties.getProperty( "project.depends.source" );
        }

        public String getLib() {
            return properties.getProperty( "project.depends.lib" );
        }

        public String getData() {
            return properties.getProperty( "project.depends.data" );
        }

        public String getExpandedLibPath() {
            return toString( expandPath( getLib() ) );
        }

        public String getExpandedSourcePath() {
            return toString( expandPath( getSource() ) );
        }

        public PhetProject[] getAllDependencies() {
            ArrayList toSearch = new ArrayList();
            ArrayList found = new ArrayList();
            toSearch.addAll( Arrays.asList( getDependencies() ) );
            getAllDependencies( toSearch, found );
            return (PhetProject[])found.toArray( new PhetProject[0] );
        }

        private void getAllDependencies( ArrayList toSearch, ArrayList found ) {
            toSearch.addAll( Arrays.asList( getDependencies() ) );
            while( toSearch.size() > 0 ) {
                PhetProject project = (PhetProject)toSearch.remove( 0 );
                if( !found.contains( project ) ) {
                    found.add( project );
                    PhetProject[] dependencies = project.getDependencies();
                    for( int i = 0; i < dependencies.length; i++ ) {
                        PhetProject dependency = dependencies[i];
                        if( !found.contains( dependency ) && !toSearch.contains( dependency ) ) {
                            toSearch.add( dependency );
                        }
                    }
                }
            }
        }

        private String toString( File[] files ) {
            String string = "";
            for( int i = 0; i < files.length; i++ ) {
                File file = files[i];
                string += file.getAbsolutePath();
                if( i < files.length - 1 ) {
                    string += " : ";
                }
            }
            return string;
        }

        /**
         * Returns the immediate dependencies for this PhetProject
         *
         * @return
         */
        public PhetProject[] getDependencies() {
            ArrayList projects = new ArrayList();
            File[] path = expandPath( getLib() );
            for( int i = 0; i < path.length; i++ ) {
                File file = path[i];
                if( file.exists() && isProject( file ) ) {
                    try {
                        projects.add( new PhetProject( file ) );
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                }
            }
            return (PhetProject[])projects.toArray( new PhetProject[0] );
        }

        private boolean isProject( File file ) {
            if( !file.isDirectory() ) {
                return false;
            }
            if( new File( file, file.getName() + ".properties" ).exists() ) {
                return true;
            }
            else {
                return false;
            }
        }

        private File[] expandPath( String lib ) {
            ArrayList files = new ArrayList();
            StringTokenizer stringTokenizer = new StringTokenizer( lib, ": " );
            while( stringTokenizer.hasMoreTokens() ) {
                String token = stringTokenizer.nextToken();
                File path = searchPath( token );
                files.add( path );
            }
            return (File[])files.toArray( new File[0] );
        }

        private File searchPath( String token ) {
            File path = new File( rootDir, token );
            if( path.exists() ) {
                return path;
            }
            File commonPath = new File( rootDir.getParentFile().getParentFile(), "common/" + token );
            if( commonPath.exists() ) {
                return commonPath;
            }
            File contribPath = new File( rootDir.getParentFile().getParentFile(), "contrib/" + token );
            if( contribPath.exists() ) {
                return contribPath;
            }
            throw new RuntimeException( "No path found for token=" + token + ", in project=" + this );
        }

        public void build() {
            PhetProject[] dependencies = getDependencies();
            for( int i = 0; i < dependencies.length; i++ ) {
                PhetProject dependency = dependencies[i];
                System.out.println( "dependency [" + i + "]= " + dependency );
            }

            Javac javac = new Javac();
            javac.setSource( "1.4" );
            javac.setSrcdir( new Path( getProject(), getExpandedSourcePath() ) );
            File destDir = new File( getProject().getBaseDir(), "ant_output/projects/" + projectName );
            destDir.mkdirs();
            javac.setDestdir( destDir );
            javac.setClasspath( new Path( getProject(), getExpandedLibPath() ) );
            runTask( javac );
        }
    }

    private void buildProject() throws IOException {
        output( "in build project" );
        File simulationsDir = new File( getProject().getBaseDir(), "simulations" );
        PhetProject phetProject = new PhetProject( simulationsDir, projectName );
        System.out.println( "phetProject = " + phetProject );
        System.out.println( "phetProject.getSource() = " + phetProject.getSource() );
        System.out.println( "phetProject.getExpandedSourcePath() = " + phetProject.getExpandedSourcePath() );
        System.out.println( "phetProject.getAllDependencies( ) = ["+phetProject.getAllDependencies( ).length+" total dependencies]" + Arrays.asList( phetProject.getAllDependencies()) );

        phetProject.build();
    }

    public void output( String string ) {
        System.out.println( string );
        echo( string );
    }

    public void runTask( Task child ) {
        child.setProject( getProject() );
        child.setLocation( getLocation() );
        child.setOwningTarget( getOwningTarget() );
        child.init();
        child.execute();
    }

    private void echo( String message ) {
        Echo echo = new Echo();
        echo.setMessage( message );
        runTask( echo );
    }

    public void setProject( String projectName ) {
        this.projectName = projectName;
    }

    /*
   http://www-128.ibm.com/developerworks/websphere/library/techarticles/0502_gawor/0502_gawor.html

   See here for a discussion of how to have an ant script use this task immediately after building it:
   http://ant.apache.org/manual/develop.html
    */
    public static void main( String[] args ) {
        File buildFile = new File( "build.xml" );
        Project p = new Project();
        p.setUserProperty( "ant.file", buildFile.getAbsolutePath() );
        p.init();
        ProjectHelper.configureProject( p, buildFile );
        p.executeTarget( p.getDefaultTarget() );
    }
}