package edu.colorado.phet.buildtools.java;

import java.io.*;
import java.util.Properties;

import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;
import org.mozilla.javascript.tools.ToolErrorReporter;

import edu.colorado.phet.buildtools.AntTaskRunner;
import edu.colorado.phet.buildtools.java.projects.WebsiteProject;
import edu.colorado.phet.common.phetcommon.util.FileUtils;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

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
                if ( file.getName().equals( "root" ) && file.getParentFile().getName().equals( project.getName() ) ) {
                    // root directory, dump it in the root
                    FileUtils.copyRecursive( file, baseDir );
                }
                else {
                    FileUtils.copyRecursive( file, classesDir );
                }
            }

            for ( File file : project.getAllJarFiles() ) {
//                if ( file.getName().equals( "scala-compiler.jar" ) || file.getName().equals( "scala-library.jar" ) ) {
//                    System.out.println( "skipping " + file.getAbsolutePath() + " for file size." );
//                    continue;
//                }
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

            System.out.println( "website baseDir = " + baseDir );
            try { // compression. we pull minified filenames from the build properties, and create the files from JS and CSS
                File cssDir = new File( baseDir, "css" );
                File jsDir = new File( baseDir, "js" );

                Properties buildProperties = new Properties();
                String cssName;
                String jsName;

                BufferedInputStream propStream = new BufferedInputStream( new FileInputStream( project.getBuildPropertiesFile() ) );
                try {
                    buildProperties.load( propStream );
                    cssName = buildProperties.getProperty( "website.cssName" );
                    jsName = buildProperties.getProperty( "website.jsName" );
                }
                finally {
                    propStream.close();
                }

                /*---------------------------------------------------------------------------*
                * CSS
                *----------------------------------------------------------------------------*/

                StringBuilder cssBuilder = new StringBuilder();
                for ( File cssFile : cssDir.listFiles( new FilenameFilter() {
                    public boolean accept( File dir, String name ) {
                        // exclude preview and IE-only CSS
                        return !name.startsWith( "preview" ) && !name.startsWith( "ie" ) && name.endsWith( ".css" );
                    }
                } ) ) {
                    cssBuilder.append( FileUtils.loadFileAsString( cssFile ) ).append( "\n" );
                }

                System.out.println( "compressing css" );
                CssCompressor cssCompressor = new CssCompressor( new StringReader( cssBuilder.toString() ) );
                StringWriter cssWriter = new StringWriter();
                cssCompressor.compress( cssWriter, 500 );
                FileUtils.writeString( new File( cssDir, cssName ), cssWriter.toString() );
                System.out.println( "css finished" );

                /*---------------------------------------------------------------------------*
                * JS
                *----------------------------------------------------------------------------*/

                StringBuilder jsBuilder = new StringBuilder();
                jsBuilder.append( FileUtils.loadFileAsString( new File( jsDir, "autoTracking_phet.js" ) ) ).append( "\n" );
                jsBuilder.append( FileUtils.loadFileAsString( new File( jsDir, "jquery-1.4.4.min.js" ) ) ).append( "\n" );
                jsBuilder.append( FileUtils.loadFileAsString( new File( jsDir, "jquery.autocomplete.js" ) ) ).append( "\n" );
                jsBuilder.append( FileUtils.loadFileAsString( new File( jsDir, "contribution-browse.js" ) ) ).append( "\n" );
                jsBuilder.append( FileUtils.loadFileAsString( new File( jsDir, "phet-autocomplete.js" ) ) ).append( "\n" );
                jsBuilder.append( FileUtils.loadFileAsString( new File( jsDir, "phet-misc.js" ) ) ).append( "\n" );

                System.out.println( "compressing js" );
                ToolErrorReporter reporter = new ToolErrorReporter( false );
                JavaScriptCompressor jsCompressor = new JavaScriptCompressor( new StringReader( jsBuilder.toString() ), reporter );
                StringWriter jsWriter = new StringWriter();
                jsCompressor.compress( jsWriter, 500, true, false, false, false );
                FileUtils.writeString( new File( jsDir, jsName ), jsWriter.toString() );
                System.out.println( "js finished" );
            }
            catch( Exception e ) {
                System.out.println( "warning: compiling old website version." );
                e.printStackTrace();
            }

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
