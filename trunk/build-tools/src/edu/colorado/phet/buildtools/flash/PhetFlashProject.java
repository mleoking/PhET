package edu.colorado.phet.buildtools.flash;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;

import edu.colorado.phet.buildtools.MyAntTaskRunner;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.java.projects.FlashLauncherProject;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.flashlauncher.FlashHTML;
import edu.colorado.phet.flashlauncher.FlashLauncher;

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
        System.out.println( "Building flash sim." );

        boolean success;

        cleanDeploy();

        buildHTMLs();

        copyProperties();

        success = buildSWF();

        if ( !success ) { return false; }


        buildOfflineJARs();

        //TODO: check for success
        return true;
    }

    private void copyProperties() throws IOException {
        FileUtils.copyToDir( new File( getDataDirectory(), getName() + ".properties" ), getDeployDir() );
    }

    private void cleanSWF() {
        File swf = getSWFFile();
        if ( swf.exists() ) {
            System.out.println( "Cleaning " + swf.getName() );
            swf.delete();
        }
    }

    private void cleanHTML() {
        Locale[] locales = getLocales();

        for ( int i = 0; i < locales.length; i++ ) {
            File html = getHTMLFile( locales[i] );

            if ( html.exists() ) {
                System.out.println( "Cleaning " + html.getName() );
                html.delete();
            }
        }
    }

    private void cleanDeploy() {
        if ( shouldRebuildSWF() ) {
            cleanSWF();
        }
        cleanHTML();
    }

    private void buildOfflineJARs() {
        if ( !shouldRebuildJARs() ) {
            return;
        }
        Locale[] locales = getLocales();
        for ( int i = 0; i < locales.length; i++ ) {
            buildOfflineJAR( locales[i] );
        }
    }

    private void buildOfflineJAR( Locale locale ) {
        System.out.println( "Working in " + getOfflineJARContentsDir().getAbsolutePath() );

        FileUtils.delete( getOfflineJARContentsDir(), true );
        getOfflineJARContentsDir().mkdirs();
        try {
            //copy class files for FlashLauncher
            FlashLauncherProject launcherProject = new FlashLauncherProject( getTrunk() );
            launcherProject.build();
            FileUtils.unzip( launcherProject.getDefaultDeployJar(), getOfflineJARContentsDir() );

            //copy SWF File
            FileUtils.copyToDir( getSWFFile(), getOfflineJARContentsDir() );

            //copy sim XML localization Files
            copyLocalizationFiles( new File( getDataDirectory(), "localization" ) );

            // copy common XML to en for flash-common-strings
            if( getName().equals( "flash-common-strings" ) ) {
                FileUtils.copyTo( new File( getCommonLocalizationDir(), "common-strings_en.xml" ), new File( getOfflineJARContentsDir(), "flash-common-strings-strings_en.xml" ) );
            }

            //copy common XML localization Files
            copyLocalizationFiles( getCommonLocalizationDir() );

            //copy HTML template
            FileUtils.copyToDir( new File( getTrunk(), "simulations-flash/build-tools/flash-build/data/flash-template.html" ), getOfflineJARContentsDir() );

            // copy agreement text
            FileUtils.copyToDir( getAgreementFile(), getOfflineJARContentsDir() );

            //create args file
            FileUtils.writeString( new File( getOfflineJARContentsDir(), "flash-launcher-args.txt" ), getName() + " " + locale.getLanguage() + " " + locale.getCountry() );

            //copy properties file
            FileUtils.copyToDir( new File( getDataDirectory(), getName() + ".properties" ), getOfflineJARContentsDir() );

            Jar jar = new Jar();
            jar.setBasedir( getOfflineJARContentsDir() );
            jar.setDestFile( new File( getDeployDir(), getName() + "_" + locale + ".jar" ) );
            Manifest manifest = new Manifest();

            Manifest.Attribute attribute = new Manifest.Attribute();
            attribute.setName( "Main-Class" );
            attribute.setValue( FlashLauncher.class.getName() );

//            jar.addFileset( toFileSetFile( createJARLauncherPropertiesFile() ) );

            manifest.addConfiguredAttribute( attribute );
            jar.addConfiguredManifest( manifest );

            new MyAntTaskRunner().runTask( jar );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private File getCommonLocalizationDir() {
        return new File( getTrunk(), "simulations-flash/common/data/localization" );
    }

    private void copyLocalizationFiles( File localizationDir ) throws IOException {
        File[] localizationFiles = localizationDir.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.getName().indexOf( "-strings_" ) >= 0;
            }
        } );
        for ( int i = 0; i < localizationFiles.length; i++ ) {
            FileUtils.copyToDir( localizationFiles[i], getOfflineJARContentsDir() );
        }
    }

    private File getOfflineJARContentsDir() {
        return new File( getAntOutputDir(), "jardata" );
    }

    private boolean shouldRebuildSWF() {
        // TODO: check whether testing. (override for deploy dev / deploy dev & production)
        return getConfigValue( "autobuild-swf", "true" ).equals( "true" );
    }

    private boolean shouldRebuildJARs() {
        // TODO: check whether testing. (override for deploy dev / deploy dev & production)
        return getConfigValue( "autobuild-jars", "true" ).equals( "true" );
    }

    private boolean buildSWF() throws Exception {

        // if the user has decided not to auto-build the SWF, don't do anything else
        if ( !shouldRebuildSWF() ) {
            return true;
        }

        String def = "C:\\Program Files\\Macromedia\\Flash 8\\Flash.exe";
        String property = "flash.exe";
        String exe = getConfigValue( property, def );

        boolean useWine = getConfigValue( "wine", "false" ).equals( "true" );

        File trunk = getProjectDir().getParentFile() // simulations
                .getParentFile() // simulations-flash
                .getParentFile(); // trunk

        boolean success = FlashBuildCommand.build( exe, getName(), trunk, useWine );

        return success;
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

                // TODO: maybe version.formatTimestamp() will work sometime in the future?
                String versionTimestamp = getProjectProperties().getProperty( "version.timestamp" );

                // TODO: get FlashHTML to handle this part
                if ( countryCode == null || countryCode.trim().length() == 0 ) {
                    countryCode = "null";
                }

                Properties agreementProperties = getAgreementProperties();

                String agreementVersion = agreementProperties.getProperty( "version" );
                String agreementContent = agreementProperties.getProperty( "content" );

                String encodedAgreement = FlashHTML.encodeXML( agreementContent );

                // TODO: use country code as well
                File HTMLFile = new File( getDeployDir(), getName() + "_" + locale.toString() + ".html" );

                System.out.println( "Generating " + HTMLFile.getName() );

                // TODO: why is version.formatTimestamp() returning bad things?
                String html = FlashHTML.generateHTML( getName(), locale.getLanguage(), countryCode,
                                                      "phet-production-website", FlashHTML.distribution_tag_dummy,
                                                      FlashHTML.installation_timestamp_dummy,
                                                      FlashHTML.installer_creation_timestamp_dummy,
                                                      version.getMajor(), version.getMinor(), version.getDev(),
                                                      version.getRevision(), versionTimestamp, simDev, bgColor,
                                                      FlashHTML.encodeXMLFile( getTranslationFile( locale ) ),
                                                      FlashHTML.encodeXMLFile( getCommonTranslationFile( locale ) ), "8",
                                                      getFlashHTMLTemplate().getAbsolutePath(),
                                                      agreementVersion, encodedAgreement );

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

        for ( int i = 0; i < extras.length; i++ ) {
            File source = extras[i];
            File destination = new File( getDeployDir(), source.getName() );

            try {
                FileUtils.copyTo( source, destination );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

    }

    private File getAgreementFile() {
        return new File( getTrunkAbsolute(), "simulations-common/data/software-agreement.properties" );
    }

    private Properties getAgreementProperties() {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream( getAgreementFile() );
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        Properties properties = new Properties();
        try {
            properties.load( inStream );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return properties;
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
        File configFile=new File( getTrunk(), "build-tools/build-local.properties");
//        File configFile = new File( getProjectDir().getParentFile().getParentFile(), "flash-build.properties" );
        if ( !configFile.exists() ) {
            String defaultFlashProperties = "build-tools" + File.separator + "flash-build" + File.separator + "default-flash-build.properties";
            File defaultConfigFile = new File( getProjectDir().getParentFile().getParentFile(), defaultFlashProperties );
            try {
                FileUtils.copyTo( defaultConfigFile, configFile );
            }
            catch( IOException e ) {
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

    public File getTrunkAbsolute() {
        return new File( getProjectDir(), "../../.." );
    }

    public String getAlternateMainClass() {
        return null;//use JARLauncher
    }

    public String getProdServerDeployPath() {
        return null;//deploy to sims
    }

    public void buildLaunchFiles( String URL, boolean dev ) {
//        super.buildLaunchFiles( URL, dev );
        System.out.println( "What to do for building Flash launch files?  Are these HTML?" );
    }

    public PhetProject[] getDependencies() {
        File commonRoot = new File( getProjectDir().getParentFile().getParentFile(), "common" );

        PhetProject commonProject;

        try {
            commonProject = new FlashCommonProject( commonRoot );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return new PhetProject[]{};
        }

        return new PhetProject[]{commonProject};
    }
}
