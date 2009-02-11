package edu.colorado.phet.buildtools.flash;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jan 29, 2009
 * Time: 11:22:23 AM
 * <p/>
 * //phetProjects.addAll(Arrays.asList( PhetFlashProject.getFlashProjects(baseDir ) ));
 */
public class PhetFlashProject extends PhetProject {
    public PhetFlashProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public PhetFlashProject( File parentDir, String name ) throws IOException {
        super( parentDir, name );
    }

    public static PhetProject[] getFlashProjects( File trunk ) {
//        File flashSimDir=new File(baseDir.getParentFile(),"team/jolson/simulations");
        File flashSimDir = new File( trunk, "simulations-flash/simulations" );
        File[] files = flashSimDir.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isDirectory() && !pathname.getName().startsWith( "." );
            }
        } );
        ArrayList projects = new ArrayList();
        for ( int i = 0; ( files != null ) && ( i < files.length ); i++ ) {
            File file = files[i];
            try {
                projects.add( new PhetFlashProject( file ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return (PhetFlashProject[]) projects.toArray( new PhetFlashProject[0] );
    }

    public boolean build() throws Exception {
        //super.build();
        System.out.println( "Building flash sim." );
        buildSWF();
        buildHTMLs();
        return true;
    }

    private void buildSWF() throws Exception {

        // TODO: if deploying to dev / production, don't allow the user to skip the build
        // we don't want inaccurate SWFs deployed
        
        // if the user has decided not to auto-build the SWF, don't do anything else
        if( !Boolean.parseBoolean(getConfigValue( "autobuild-swf", "true" ) ) ) {
            return;
        }
        
        String def = "C:\\Program Files\\Macromedia\\Flash 8\\Flash.exe";
        String property = "flash.exe";
        String exe = getConfigValue( property, def );

        boolean useWine = Boolean.parseBoolean( getConfigValue( "wine", "false" ) );

        File trunk = getProjectDir().getParentFile() // simulations
                       .getParentFile() // simulations-flash
                       .getParentFile(); // trunk
        FlashBuildCommand.build( exe, getName(), trunk, useWine );
        JOptionPane.showMessageDialog( null, "Building the Flash SWF, press OK when finished." );
    }

    private void buildHTMLs() {
        Locale[] locales = getLocales();
        for ( int i = 0; i < locales.length; i++ ) {
            Locale locale = locales[i];
            PhetVersion version = super.getVersion();
            try {
                String bgColor = getProjectProperties().getProperty( "bgcolor" );
                String simDev = "true"; // TODO: handle what will be sent as the sim_dev field for statistics
                String countryCode = locale.getCountry();

                // TODO: get FlashHTML to handle this part
                if (countryCode==null || countryCode.trim().length()==0){
                    countryCode="null";
                }

                // TODO: use country code as well
                File HTMLFile = new File( getDeployDir(), getName() + "_" + locale.toString() + ".html" );

                // TODO: why is version.formatTimestamp() returning bad things?
                String html = FlashHTML.generateHTML( getName(), locale.getLanguage(), countryCode,
                                                      "phet-production-website", GenerateHTML.distribution_tag_dummy,
                                                      GenerateHTML.installation_timestamp_dummy,
                                                      GenerateHTML.installer_creation_timestamp_dummy,
                                                      version.getMajor(), version.getMinor(), version.getDev(),
                                                      version.getRevision(), version.formatTimestamp(), simDev, bgColor,
                                                      FlashHTML.encodeXMLFile( getTranslationFile( locale ) ),
                                                      FlashHTML.encodeXMLFile( getCommonTranslationFile( locale ) ), "8",
                                                      getFlashHTMLTemplate().getAbsolutePath() );
                
                FileUtils.writeString( HTMLFile, html );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

        // copy over other extra files needed by the main HTML file
        
        File[] extras = {
            new File( getProjectDir().getParentFile().getParentFile(), "build-tools/flash-build/data/get_flash.jpg" )
        };

        for( int i = 0; i < extras.length; i++ ) {
            File source = extras[i];
            File destination = new File( getDeployDir(), source.getName() );

            try {
                FileUtils.copyTo(source, destination);
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }

    }

    private File getFlashHTMLTemplate() {
        return new File( getProjectDir().getParentFile().getParentFile(), "build-tools/flash-build/data/flash-template.html" );
    }

    private File getCommonTranslationFile( Locale locale ) {
        String lang = "_" + locale.getLanguage();
        File file = new File( getProjectDir().getParentFile().getParentFile(), "common/data" + File.separator + "localization" + File.separator + "common-strings" + lang + ".xml" );
        if ( file.exists() ) {
            return file;
        }
        else {
            // TODO: compare locale to new Locale( "en" ), if equal return file, and IOException will be thrown later
            return getCommonTranslationFile( new Locale( "en" ) );//this code will throw stack overflow exception if this fails too
        }
    }

    private File getFlashBuildConfigFile() {
        File configFile = new File( getProjectDir().getParentFile().getParentFile(), "flash-build.properties" );
        if( !configFile.exists() ) {
            String defaultFlashProperties = "build-tools" + File.separator + "flash-build" + File.separator + "default-flash-build.properties";
            File defaultConfigFile = new File( getProjectDir().getParentFile().getParentFile(), defaultFlashProperties );
            try {
                FileUtils.copyTo( defaultConfigFile, configFile );
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return configFile;
    }

    private String getConfigValue( String property, String defaultValue ) {
        Properties properties = new Properties();
        try {
            properties.load( new FileInputStream( getFlashBuildConfigFile() ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return defaultValue;
        }
        
        return properties.getProperty( property, defaultValue );
    }

    public String getListDisplayName() {
        return "Flash: " + getName();
    }

    public void runSim( Locale locale, String simulationName ) {
        System.out.println( "Running the flash sim: " + simulationName );
        String exe = getConfigValue( "browser.exe", "C:\\Users\\Sam\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe" );
        try {
            String command = exe + " " + getHTMLFile( locale ).getAbsolutePath();
            System.out.println( "command = " + command );
            Process p = Runtime.getRuntime().exec( command );

        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private File getHTMLFile( Locale locale ) {
        return new File( getProjectDir(), "deploy/" + getName() + "_" + locale + ".html" );
    }

    private File getSWFFile() {
        return new File( getProjectDir(), "deploy/" + getName() + ".swf" );
    }

    public Simulation getSimulation( String simulationName, String locale ) {
        return new Simulation( simulationName, "title", "description", "mainclass", new String[0], new File( "screenshot.gif" ) );
    }

    public Locale[] getLocales() {
        return getLocalesImpl( ".xml" );
    }

    public File getTranslationFile( Locale locale ) {
        String lang = "_" + locale.getLanguage();
        return new File( getProjectDir(), "data" + File.separator + getName() + File.separator + "localization" + File.separator + getName() + "-strings" + lang + ".xml" );
    }

    public void buildLaunchFiles( String URL, boolean dev ) {
//        super.buildLaunchFiles( URL, dev );
        System.out.println( "What to do for building Flash launch files?  Are these HTML?" );
    }
}
