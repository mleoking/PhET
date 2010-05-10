package edu.colorado.phet.buildtools.java;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;

import edu.colorado.phet.buildtools.AntTaskRunner;
import edu.colorado.phet.buildtools.java.projects.WebsiteProject;
import edu.colorado.phet.buildtools.util.FileUtils;

/**
 * Build process for the wicket website. Constructs a WAR file that has an intrinsically different structure
 */
public class WebsiteBuildCommand extends JavaBuildCommand {

    private final WebsiteProject project;
    private final AntTaskRunner antTaskRunner;

    public WebsiteBuildCommand( JavaProject project, AntTaskRunner taskRunner, boolean shrink, File outputJar ) {
        super( project, taskRunner, shrink, outputJar );

        this.project = (WebsiteProject) project;
        this.antTaskRunner = taskRunner;
    }

    @Override
    public void execute() throws Exception {
        super.execute();

        // copy the finished WAR into the deployment directory
        FileUtils.copyToDir( project.getJarFile(), project.getDeployDir() );
    }

    @Override
    public void jar() throws ManifestException {
        try {
            File baseDir = new File( project.getClassesDirectory().getParentFile(), "jarbuild" );
            baseDir.mkdirs();
            File classesDir = new File( baseDir, "WEB-INF/classes" );
            classesDir.mkdirs();
            File libDir = new File( baseDir, "WEB-INF/lib" );
            libDir.mkdirs();

            // copy the classes over
            FileUtils.copyRecursive( project.getClassesDirectory(), classesDir );

            // copy all of the data we need over
            for ( File file : project.getAllDataDirectories() ) {
                if ( file.getName().equals( "root" ) && file.getParentFile().getName().equals( "wicket-website" ) ) {
                    // root directory, dump it in the root
                    FileUtils.copyRecursive( file, baseDir );
                }
                else {
                    FileUtils.copyRecursive( file, classesDir );
                }
            }

            for ( File file : project.getAllJarFiles() ) {
                if ( file.getName().equals( "scala-compiler.jar" ) || file.getName().equals( "scala-library.jar" ) ) {
                    System.out.println( "skipping " + file.getAbsolutePath() + " for file size." );
                    continue;
                }
                System.out.println( "Adding (or overwriting) lib with: " + file.getAbsolutePath() );
                FileUtils.copyToDir( file, libDir );
            }

            // we then need to copy the other files in the source root that are depended on.

            Copy copy = new Copy();

            FileSet otherFiles = new FileSet();
            otherFiles.setDir( project.getSourceRoots()[0] );
            otherFiles.setIncludes( "**/*.html" );
            otherFiles.setIncludes( "**/*.xml" );
            otherFiles.setIncludes( "**/*.properties" );

            copy.addFileset( otherFiles );
            copy.setTodir( classesDir );

            antTaskRunner.runTask( copy );

            // then we need to JAR everything up into the WAR file

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
        // don't proguard anything right now.
        // reflection and no main class would cause many issues
    }

}
