package edu.colorado.phet.buildtools.flash;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;

import edu.colorado.phet.buildtools.*;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.PhetJarSigner;
import edu.colorado.phet.common.phetcommon.application.JARLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.flashlauncher.FlashHTML;
import edu.colorado.phet.flashlauncher.FlashLauncher;

/**
 * Represents a Flash simulation project. Each project contains one simulation of the same name.
 */
public class FlashSimulationProject extends PhetProject {

    public FlashSimulationProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public FlashSimulationProject( File parentDir, String name ) throws IOException {
        super( parentDir, name );
    }

    public void updateProjectFiles() {
        super.updateProjectFiles();
        copySoftwareAgreement();
    }

    private void copySoftwareAgreement() {
        FlashCommonProject.generateFlashSoftwareAgreement( getTrunk() );
    }

    public static PhetProject[] getFlashSimulations( File trunk ) {
        File flashSimDir = new File( trunk, BuildToolsPaths.FLASH_SIMULATIONS_DIR );
        File[] files = flashSimDir.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isDirectory() && !pathname.getName().startsWith( "." );
            }
        } );
        Collection projects = new ArrayList();
        for ( int i = 0; ( files != null ) && ( i < files.length ); i++ ) {
            File file = files[i];
            try {
                projects.add( new FlashSimulationProject( file ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        projects = PhetProject.sort( new ArrayList( projects ) );
        return (FlashSimulationProject[]) projects.toArray( new FlashSimulationProject[projects.size()] );
    }

    public boolean build() throws Exception {
        System.out.println( "Building flash sim." );

        cleanDeploy();

        // write descriptions and titles to <project-name>.xml
        writeMetaXML();

        buildHTMLs();

        copyProperties();

        boolean success = buildSWFs();
        if ( success ) {
            buildOfflineJARs();
        }


        //TODO: check for success
        return success;
    }

    /**
     * Build function meant to be used ONLY for testing purposes, but allows optimizations which will save developers
     * time. If the HTML is already generated, and all that is needed is the SWF to be updated, just re-publish within
     * the IDE instead of re-running the test
     *
     * @param simulationName The name of the simulation to build
     * @param clean          Whether to clean the deploy directory beforehand. Will only clean the parts that will be regenerated,
     *                       as specified by the later arguments
     * @param html           Whether to (re)generate HTML for each locale
     * @param swf            Whether to (re)publish the SWF from the FLA. Time consuming, especially if IDE is not open.
     * @param jars           Whether to (re)generate the JARs for each locale. Time consuming!
     * @return Success or failure
     * @throws Exception
     */
    public boolean testBuild( String simulationName, boolean clean, boolean html, boolean swf, boolean jars ) throws Exception {
        if ( clean ) {
            // note: if not rebuilding JARs, they will not be available!
            if ( html && swf ) {
                cleanEntireDeployDir();
            }
            else {
                if ( html ) {
                    cleanHTMLs();
                }
                if ( swf ) {
                    cleanSWFs();
                }
            }
        }

        // write descriptions and titles to <project-name>.xml
        writeMetaXML();

        if ( html ) {
            buildHTMLs();
        }
        copyProperties();
        boolean success = true;
        if ( swf ) {
            success = buildSWFs();
        }
        if ( success && jars ) {
            buildOfflineJARs();
        }


        return success;
    }

    private void copyProperties() throws IOException {
        FileUtils.copyToDir( new File( getDataDirectory(), getName() + ".properties" ), getDeployDir() );
    }

    private void cleanSWFs() {
        for ( String simulationName : getSimulationNames() ) {
            File swf = getSWFFile( simulationName );
            if ( swf.exists() ) {
                System.out.println( "Cleaning " + swf.getName() );
                swf.delete();
            }
        }
    }

    private void cleanHTMLs() {
        Locale[] locales = getLocales();

        for ( String simulationName : getSimulationNames() ) {
            for ( Locale locale : locales ) {
                File html = getHTMLFile( simulationName, locale );

                if ( html.exists() ) {
                    System.out.println( "Cleaning " + html.getName() );
                    html.delete();
                }
            }
        }
    }

    private void cleanEntireDeployDir() {
        for ( File file : getDeployDir().listFiles() ) {
            if ( !file.isDirectory() ) {
                System.out.println( "Cleaning " + file.getName() );
                file.delete();
            }
        }
    }

    private void cleanDeploy() {
        cleanSWFs();
        cleanHTMLs();

        // added so that miscellaneous files are not deployed to tigercat when they should not be!
        cleanEntireDeployDir();
    }

    private void buildOfflineJARs() {
        Locale[] locales = getLocales();
        try {
            // now we only build flash launcher once. -JO
            FlashLauncherProject launcherProject = new FlashLauncherProject( getTrunk() );
            launcherProject.build();
            for ( String simulationName : getSimulationNames() ) {
                for ( Locale locale : locales ) {
                    buildOfflineJAR( simulationName, locale, launcherProject );
                }
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

    }

    private void buildOfflineJAR( String simulationName, Locale locale, FlashLauncherProject launcherProject ) {
        System.out.println( "Working in " + getOfflineJARContentsDir().getAbsolutePath() );

        FileUtils.delete( getOfflineJARContentsDir(), true );
        getOfflineJARContentsDir().mkdirs();
        try {
            // copy class files for FlashLauncher
            FileUtils.unzip( launcherProject.getDefaultDeployJar(), getOfflineJARContentsDir() );

            // The FlashLauncherProject came with a jar-launcher.properties, which should be deleted for
            // embedding in this new project, see #1292
            File jarLauncherPropertiesFile = new File( getOfflineJARContentsDir(), JARLauncher.PROPERTIES_FILE_NAME );
            boolean deleted = jarLauncherPropertiesFile.delete();
            System.out.println( "Attempt to delete file, deleted=" + deleted + ": " + jarLauncherPropertiesFile.getAbsolutePath() );

            // copy SWF File
            FileUtils.copyToDir( getSWFFile( simulationName ), getOfflineJARContentsDir() );

            // copy sim XML localization Files
            copyLocalizationFiles( new File( getDataDirectory(), "localization" ) );

            // copy common XML to en for flash-common-strings
            if ( getName().equals( "flash-common-strings" ) ) {
                FileUtils.copyTo( new File( getCommonLocalizationDir(), "common-strings_en.xml" ), new File( getOfflineJARContentsDir(), "flash-common-strings-strings_en.xml" ) );
            }

            // copy common XML localization Files
            copyLocalizationFiles( getCommonLocalizationDir() );

            // copy HTML template
            FileUtils.copyToDir( new File( getTrunk(), BuildToolsPaths.FLASH_HTML_TEMPLATE ), getOfflineJARContentsDir() );

            // copy HTML extras like get_flash.jpg
            copyExtrasTo( getOfflineJARContentsDir() );

            // copy agreement properties
            FileUtils.copyToDir( getAgreementPropertiesFile(), getOfflineJARContentsDir() );

            // copy agreement text so there is an HTML copy at the top level of the JAR, adds about 20kb to JAR
            // see similar code in JavaBuildCommand
            File src = new File( getTrunk(), BuildToolsPaths.SOFTWARE_AGREEMENT_PATH );
            try {
                FileUtils.copyRecursive( src, getOfflineJARContentsDir() );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }

            // copy credits file
            FileUtils.copyToDir( getCreditsFile(), getOfflineJARContentsDir() );

            //create args file
            FileUtils.writeString( new File( getOfflineJARContentsDir(), "flash-launcher-args.txt" ), simulationName + " " + locale.getLanguage() + " " + locale.getCountry() );

            //copy properties file
            FileUtils.copyToDir( new File( getDataDirectory(), getName() + ".properties" ), getOfflineJARContentsDir() );

            Jar jar = new Jar();
            jar.setBasedir( getOfflineJARContentsDir() );
            File destFile = new File( getDeployDir(), simulationName + "_" + locale + ".jar" );
            jar.setDestFile( destFile );
            Manifest manifest = new Manifest();

            Manifest.Attribute attribute = new Manifest.Attribute();
            attribute.setName( "Main-Class" );
            attribute.setValue( FlashLauncher.class.getName() );

            manifest.addConfiguredAttribute( attribute );
            jar.addConfiguredManifest( manifest );

            new MyAntTaskRunner().runTask( jar );

            if ( BuildLocalProperties.getInstance().isJarsignerCredentialsSpecified() ) {
                signJAR( destFile );
            }
            else {
                System.out.println( "Jarsigner credentials not specified in build file, skipping jar signing." );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

    }

    private void signJAR( File outputJar ) {
        PhetJarSigner signer = new PhetJarSigner( BuildLocalProperties.getInstance() );
        // Sign the JAR.
        if ( signer.signJar( outputJar ) != true ) {
            // Signing failed.  Throw an exception in order to force the build process to stop.
            throw new BuildException( "Signing of JAR file failed." );
        }
    }

    private File getCreditsFile() {
        return new File( getDataDirectory(), "credits.txt" );
    }

    private File getCommonLocalizationDir() {
        return new File( getTrunk(), BuildToolsPaths.FLASH_COMMON_LOCALIZATION );
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

    /**
     * Builds all of the SWFs in the project
     *
     * @return Success
     * @throws Exception
     */
    protected boolean buildSWFs() throws Exception {
        for ( String simulationName : getSimulationNames() ) {
            boolean success = buildSWF( simulationName );
            if ( !success ) {
                return success;
            }
        }
        return true;
    }

    protected boolean buildSWF( String simulationName ) throws Exception {
        //copy software agreement HTM to AS for compilation into SWF
        copySoftwareAgreement();

        File trunk = getProjectDir().getParentFile() // simulations
                .getParentFile() // simulations-flash
                .getParentFile(); // trunk

        return FlashBuildCommand.build( simulationName, trunk );
    }

    /**
     * Build HTML for a specific locale and version. Automatically sets the dev flag if the dev version is not zero
     *
     * @param simulationName The name of the simulation
     * @param locale         The locale to use (common and sim strings)
     * @param version        The version to include
     * @throws IOException
     */
    public void buildHTML( String simulationName, Locale locale, PhetVersion version ) throws IOException {
        String bgColor = getProjectProperties().getProperty( "bgcolor" );

        String simDev = "false";

        if ( !version.getDev().equals( "0" ) && !version.getDev().equals( "00" ) ) {
            simDev = "true";
        }

        String countryCode = locale.getCountry();

        String versionTimestamp = getProjectProperties().getProperty( "version.timestamp" );

        // TODO: get FlashHTML to handle this part
        if ( countryCode == null || countryCode.trim().length() == 0 ) {
            countryCode = "null";
        }

        Properties agreementProperties = getAgreementProperties();

        String agreementVersion = agreementProperties.getProperty( "version" );
        String agreementContent = FileUtils.loadFileAsString( getAgreementHTMLFile() );

        String creditsString = FileUtils.loadFileAsString( getCreditsFile() );

        String encodedAgreement = FlashHTML.encodeXML( agreementContent );

        File HTMLFile = getHTMLFile( simulationName, locale );

        System.out.println( "Generating " + HTMLFile.getName() );

        String titleString = FlashHTML.extractTitleFromXML( simulationName, getTranslationFile( locale ) );
        if ( titleString == null ) {
            titleString = FlashHTML.extractTitleFromXML( simulationName, getDefaultTranslationFile() );
            if ( titleString == null ) {
                titleString = getName();
            }
        }

        String html = FlashHTML.generateHTML( simulationName, locale.getLanguage(), countryCode,
                                              "phet-production-website", FlashHTML.distribution_tag_dummy,
                                              FlashHTML.installation_timestamp_dummy,
                                              FlashHTML.installer_creation_timestamp_dummy,
                                              version.getMajor(), version.getMinor(), version.getDev(),
                                              version.getRevision(), versionTimestamp, simDev, bgColor,
                                              FlashHTML.encodeXMLFile( getTranslationFile( locale ) ),
                                              FlashHTML.encodeXMLFile( getCommonTranslationFile( locale ) ), "8",
                                              getFlashHTMLTemplate().getAbsolutePath(),
                                              agreementVersion, encodedAgreement, creditsString, titleString );

        FileUtils.writeString( HTMLFile, html );
    }

    private void buildHTMLs() {
        Locale[] locales = getLocales();
        for ( String simulationName : getSimulationNames() ) {
            for ( Locale locale : locales ) {
                PhetVersion version = super.getVersion();
                try {
                    buildHTML( simulationName, locale, version );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        }

        // copy over other extra files needed by the main HTML file
        // currently this is usually the "get flash" image
        copyExtrasTo( getDeployDir() );

    }

    private File[] getExtras() {
        return new File[]{
                new File( getTrunkAbsolute(), BuildToolsPaths.FLASH_GET_FLASH_IMAGE )
        };
    }

    private void copyExtrasTo( File destinationDir ) {
        File[] extras = getExtras();

        for ( int i = 0; i < extras.length; i++ ) {
            File source = extras[i];
            File destination = new File( destinationDir, source.getName() );

            try {
                FileUtils.copyTo( source, destination );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private File getAgreementPropertiesFile() {
        File f = new File( getTrunkAbsolute(), BuildToolsPaths.SOFTWARE_AGREEMENT_PROPERTIES );
        if ( !f.exists() ) {
            System.out.println( f.getName() + " does not exist" );
        }
        return f;
    }

    private File getAgreementHTMLFile() {
        return new File( getTrunkAbsolute(), BuildToolsPaths.SOFTWARE_AGREEMENT_HTML );
    }

    private Properties getAgreementProperties() {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream( getAgreementPropertiesFile() );
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
        return new File( getTrunkAbsolute(), BuildToolsPaths.FLASH_HTML_TEMPLATE );
    }

    private File getCommonTranslationFile( Locale locale ) {
        String localeString = LocaleUtils.localeToString( locale );
        File file = new File( getTrunkAbsolute(), BuildToolsPaths.FLASH_COMMON_LOCALIZATION + "/common-strings_" + localeString + ".xml" );
        if ( file.exists() ) {
            return file;
        }
        else {
            // TODO: compare locale to new Locale( "en" ), if equal return file, and IOException will be thrown later
            return getCommonTranslationFile( new Locale( "en" ) );//this code will throw stack overflow exception if this fails too
        }
    }

    public String getListDisplayName() {
        return "Flash: " + getName();
    }

    public void runSim( Locale locale, String simulationName ) {
        System.out.println( "Running the flash sim: " + simulationName );
        PhetWebsite.openBrowser( getHTMLFile( simulationName, locale ).getAbsolutePath() );
    }

    private File getHTMLFile( String simulationName, Locale locale ) {
        return new File( getDeployDir(), simulationName + "_" + LocaleUtils.localeToString( locale ) + ".html" );
    }

    public File getSWFFile( String simulationName ) {
        return new File( getProjectDir(), "deploy/" + simulationName + ".swf" );
    }

    private String getSimulationTitle( String simulationName, Locale locale ) {
        File simXMLFile = getTranslationFile( locale );
        if ( !simXMLFile.exists() ) {
            simXMLFile = getTranslationFile( new Locale( "en" ) ); // TODO: standardize "en" locale somewhere? LocaleUtils?
            if ( !simXMLFile.exists() ) {
                System.out.println( "Warning: could not find sim XML for either locale or en" );
                return getName();
            }
        }
        String ret = FlashHTML.extractTitleFromXML( simulationName, simXMLFile );
        if ( ret == null ) {
            ret = getName();
        }
        return ret;
    }

    public Simulation getSimulation( String simulationName, Locale locale ) {
        return new Simulation( simulationName, getSimulationTitle( simulationName, locale ), "mainclass", new String[0] );
    }

    public Locale[] getLocales() {
        return getLocalesImpl( ".xml" );
    }

    public boolean hasLocale( Locale locale ) {
        Locale[] locales = getLocales();

        for ( int i = 0; i < locales.length; i++ ) {
            if ( LocaleUtils.localeToString( locales[i] ).equals( LocaleUtils.localeToString( locale ) ) ) {
                return true;
            }
        }

        return false;
    }

    public File getLocalizationFile( Locale locale ) {
        return new File( getLocalizationDir(), getName() + "-strings_" + LocaleUtils.localeToString( locale ) + ".xml" );
    }

    public File getDefaultTranslationFile() {
        return getTranslationFile( new Locale( "en" ) );
    }

    public File getTranslationFile( Locale locale ) {
        String localeString = LocaleUtils.localeToString( locale );
        return new File( getProjectDir(), "data/" + getName() + "/localization/" + getName() + "-strings_" + localeString + ".xml" );
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

    public String getLaunchFileSuffix() {
        return "html";
    }

    public void buildLaunchFiles( String URL, boolean dev ) {
//        super.buildLaunchFiles( URL, dev );
        System.out.println( "What to do for building Flash launch files?  Are these HTML?" );
    }

    public boolean isTestable() {
        return true;
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


    /**
     * Used solely for building the flash-launcher .class files that will be used in FlashSimulationProject.
     * This project is not intended to be used or deployed by itself.
     */
    public class FlashLauncherProject extends JavaProject {
        public FlashLauncherProject( File trunk ) throws IOException {
            super( new File( trunk, BuildToolsPaths.FLASH_LAUNCHER ) );
        }

        public File getTrunkAbsolute() {
            return new File( getProjectDir(), "../../" );
        }

        public String getAlternateMainClass() {
            return null;
        }

        public String getProdServerDeployPath() {
            return null;
        }

        public boolean isTestable() {
            return false;
        }

        public String getJavaTargetVersion() {
            return BuildToolsConstants.FLASH_LAUNCHER_JAVA_VERSION;
        }

        public String getJavaSourceVersion() {
            return BuildToolsConstants.FLASH_LAUNCHER_JAVA_VERSION;
        }
    }
}
