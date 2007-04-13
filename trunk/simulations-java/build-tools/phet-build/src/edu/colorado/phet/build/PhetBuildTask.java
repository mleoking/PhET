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

        public String getFullSourcePath() {
            String sourcePath = "";
            StringTokenizer stringTokenizer = new StringTokenizer( getSource(), ": " );
            while( stringTokenizer.hasMoreTokens() ) {
                String token = stringTokenizer.nextToken();
                File path = searchSourcePath( token );
                sourcePath = sourcePath + " " + path.getAbsolutePath();
                if( stringTokenizer.hasMoreTokens() ) {
                    sourcePath += " : ";
                }
            }
            return sourcePath;
        }

        private File searchSourcePath( String token ) {
            File path = new File( rootDir, token );
            if( path.exists() ) {
                return path;
            }
            File commonPath = new File( rootDir.getParentFile().getParentFile(), "common/" + token );
            if( commonPath.exists() ) {
                return commonPath;
            }
            throw new RuntimeException( "No path found for token=" + token + ", in project=" + this );
        }

        public void build() {
            Javac javac = new Javac();
            javac.setSource( "1.4" );
            javac.setSrcdir( new Path( getProject(), getFullSourcePath() ) );
            File destDir = new File( "C:/temp-phet-ant_output" );
            destDir.mkdirs();
            javac.setDestdir( destDir );
            javac.setClasspath( new Path( getProject(), getLib() ) );
            System.out.println( "System.getProperty( \"JAVA_HOME\") = " + System.getProperty( "JAVA_HOME" ) );
            runTask( javac );
        }
    }

    private void buildProject() throws IOException {
        output( "in build project" );
        File simulationsDir = new File( getProject().getBaseDir(), "simulations" );
        PhetProject phetProject = new PhetProject( simulationsDir, projectName );
        System.out.println( "phetProject = " + phetProject );
        System.out.println( "phetProject.getSource() = " + phetProject.getSource() );
        System.out.println( "phetProject.getFullSourcePath() = " + phetProject.getFullSourcePath() );

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
    */
    public static void main( String[] args ) {
        System.setProperty( "JAVA_HOME", "C:\\j2sdk1.4.2_13" );//todo: fix this
        //todo: fix build error requiring tools.jar:
        //see: http://jroller.com/page/vdbdavid?entry=ant_eclipse_problems
        //or: http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=next_topic&f=67&t=002030&go=older
        System.out.println( "System.getProperty( \"JAVA_HOME\") = " + System.getProperty( "JAVA_HOME" ) );
        File buildFile = new File( "build.xml" );
        Project p = new Project();
//        p.setBaseDir( new File( "C:\\phet\\subversion\\trunk\\simulations-java" ) );
        p.setUserProperty( "ant.file", buildFile.getAbsolutePath() );
        p.init();
        ProjectHelper.configureProject( p, buildFile );
        p.executeTarget( p.getDefaultTarget() );
    }
}