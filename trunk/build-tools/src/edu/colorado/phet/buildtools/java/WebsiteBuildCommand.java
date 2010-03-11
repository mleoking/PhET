package edu.colorado.phet.buildtools.java;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;

import edu.colorado.phet.buildtools.AntTaskRunner;
import edu.colorado.phet.buildtools.java.projects.WebsiteProject;
import edu.colorado.phet.buildtools.util.FileUtils;

public class WebsiteBuildCommand extends JavaBuildCommand {

    private final WebsiteProject project;
    private final AntTaskRunner antTaskRunner;

    public WebsiteBuildCommand( JavaProject project, AntTaskRunner taskRunner, boolean shrink, File outputJar ) {
        super( project, taskRunner, shrink, outputJar );

        this.project = (WebsiteProject) project;
        this.antTaskRunner = taskRunner;
    }

    @Override
    public void jar() throws ManifestException {
        try {
            System.out.println( "Classes at " + project.getClassesDirectory() );

            File baseDir = new File( project.getClassesDirectory().getParentFile(), "jarbuild" );
            baseDir.mkdirs();
            File classesDir = new File( baseDir, "WEB-INF/classes" );
            classesDir.mkdirs();
            FileUtils.copyRecursive( project.getClassesDirectory(), classesDir );

            for ( File file : project.getAllDataDirectories() ) {
                if( file.getName().equals( "root") && file.getParentFile().getName().equals( "wicket-website")) {
                    // root directory, dump it in the root
                    FileUtils.copyRecursive( file, baseDir );
                } else {
                    FileUtils.copyRecursive( file, classesDir );
                }
            }

            Jar jar = new Jar();
            
            jar.setBasedir( baseDir );
            jar.setDestFile( project.getJarFile() );
            
            antTaskRunner.runTask( jar );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void proguard() {

    }

}
