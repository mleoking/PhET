package edu.colorado.phet.build;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.build.util.*;

/**
 * Author: Sam Reid
 * Apr 14, 2007, 2:40:56 PM
 */
public class PhetProject {

    private static final String WEBROOT = "http://phet.colorado.edu/sims/";

    private final String name;
    private final File projectDir;
    private final ProjectPropertiesFile projectPropertiesFile;
    private final BuildPropertiesFile buildPropertiesFile;

    public PhetProject( File projectRoot ) throws IOException {
        this( projectRoot.getParentFile(), projectRoot.getName() );
    }

    public PhetProject( File parentDir, String name ) throws IOException {
        this.name = name;
        this.projectDir = new File( parentDir, name );
        this.projectPropertiesFile = new ProjectPropertiesFile( this );
        this.buildPropertiesFile = new BuildPropertiesFile( this );
    }

    public File getDeployDir() {
        File file = new File( getProjectDir(), "deploy/" );

        file.mkdirs();

        return file;
    }

    public File getDefaultDeployJar() {
        return new File( getDeployDir(), getName() + "_all.jar" );
    }

    public File getDefaultDeploySimulationJar( String simulation ) {
        return new File( getDeployDir(), simulation + "_all.jar" );
    }

    public File getProjectDir() {
        return projectDir;
    }

    public boolean equals( Object obj ) {
        if ( obj instanceof PhetProject ) {
            PhetProject phetProject = (PhetProject) obj;
            return phetProject.projectDir.equals( projectDir );
        }
        else {
            return false;
        }
    }

    public File getAntBaseDir() {
        return new File( "." );
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "project=" + name + ", root=" + projectDir.getAbsolutePath() + ", buildProperties=" + buildPropertiesFile.toString();
    }

    private String getSource() {
        return buildPropertiesFile.getSource();
    }

    private String getScalaSource() {
        return buildPropertiesFile.getScalaSource();
    }

    private String getLib() {
        return buildPropertiesFile.getLib();
    }

    /**
     * return "" instead of null if no data directory specified
     *
     * @return
     */
    public String getData() {
        String s = buildPropertiesFile.getData();
        return s == null ? "" : s;
    }

    public String[] getKeepMains() {
        return buildPropertiesFile.getKeepMains();
    }

    private File[] getDataDirectories() {
        return expandPath( getData() );
    }

    public File[] getJarFiles() {
        ArrayList all = new ArrayList( Arrays.asList( expandPath( getLib() ) ) );
        for ( int i = 0; i < all.size(); i++ ) {
            File file = (File) all.get( i );
            if ( isProject( file ) ) {
                all.remove( i );
                i--;
            }
        }
        return (File[]) all.toArray( new File[0] );
    }


    private File[] getScalaSourceRoots() {
        return expandPath( getScalaSource() );
    }

    public File[] getSourceRoots() {
        return expandPath( getSource() );
    }

    /**
     * Retrieves a list of this project's dependencies (the project is
     * considered to be itself a dependency, so this list will always contain
     * at least one element).
     *
     * @return A list of this project's dependencies.
     */
    public PhetProject[] getAllDependencies() {
        ArrayList toSearch = new ArrayList();
        ArrayList found = new ArrayList();
        toSearch.addAll( Arrays.asList( getDependencies() ) );
        getAllDependencies( toSearch, found );
        found.add( this );
        return (PhetProject[]) found.toArray( new PhetProject[0] );
    }

    private void getAllDependencies( ArrayList toSearch, ArrayList found ) {
        toSearch.addAll( Arrays.asList( getDependencies() ) );
        while ( toSearch.size() > 0 ) {
            PhetProject project = (PhetProject) toSearch.remove( 0 );
            if ( !found.contains( project ) ) {
                found.add( project );
                PhetProject[] dependencies = project.getDependencies();
                for ( int i = 0; i < dependencies.length; i++ ) {
                    PhetProject dependency = dependencies[i];
                    if ( !found.contains( dependency ) && !toSearch.contains( dependency ) ) {
                        toSearch.add( dependency );
                    }
                }
            }
        }
    }

    /**
     * Returns the immediate dependencies for this PhetProject
     *
     * @return the immediate dependencies declared for this project
     */
    public PhetProject[] getDependencies() {
        ArrayList projects = new ArrayList();
        File[] path = expandPath( getLib() );
        for ( int i = 0; i < path.length; i++ ) {
            File file = path[i];
            if ( file.exists() && isProject( file ) ) {
                try {
                    projects.add( new PhetProject( file ) );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
        return (PhetProject[]) projects.toArray( new PhetProject[0] );
    }

    private boolean isProject( File file ) {
        if ( !file.exists() || !file.isDirectory() ) {
            return false;
        }
        return new File( file, file.getName() + "-build.properties" ).exists();
    }

    private File[] expandPath( String lib ) {
        if ( lib == null || lib.trim().length() == 0 ) {
            return new File[0];
        }
        ArrayList files = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer( lib, ": " );
        while ( stringTokenizer.hasMoreTokens() ) {
            String token = stringTokenizer.nextToken();
            File path = searchPath( token );
            files.add( path );
        }
        return (File[]) files.toArray( new File[0] );
    }

    private File searchPath( String token ) {
        File commonProject = new File( getAntBaseDir(), "common/" + token );
        if ( commonProject.exists() && isProject( commonProject ) ) {
            return commonProject;
        }
        File contribPath = new File( getAntBaseDir(), "contrib/" + token );
        if ( contribPath.exists() ) {
            return contribPath;
        }
        File path = new File( projectDir, token );
        if ( path.exists() ) {
            return path;
        }
        File commonPathNonProject = new File( getAntBaseDir(), "common/" + token );
        if ( commonPathNonProject.exists() ) {
            return commonPathNonProject;
        }
        File simProject = new File( getAntBaseDir(), "simulations/" + token );
        if ( simProject.exists() && isProject( simProject ) ) {
            return simProject;
        }
        throw new RuntimeException( "No path found for token=" + token + ", antBaseDir=" + getAntBaseDir().getAbsolutePath() + ", in project=" + this );
    }


    //copied from getAllJavaSourceRoots, should use interface for different call
    public File[] getAllScalaSourceRoots() {
        PhetProject[] dependencies = getAllDependencies();
        ArrayList srcDirs = new ArrayList();
        for ( int i = 0; i < dependencies.length; i++ ) {
            PhetProject dependency = dependencies[i];
            File[] jf = dependency.getScalaSourceRoots();
            for ( int j = 0; j < jf.length; j++ ) {
                File file = jf[j];
                if ( !srcDirs.contains( file ) ) {
                    srcDirs.add( file );
                }
            }
        }
        return (File[]) srcDirs.toArray( new File[0] );
    }

    public File[] getAllJavaSourceRoots() {
        PhetProject[] dependencies = getAllDependencies();
        ArrayList srcDirs = new ArrayList();
        for ( int i = 0; i < dependencies.length; i++ ) {
            PhetProject dependency = dependencies[i];
            File[] jf = dependency.getSourceRoots();
            for ( int j = 0; j < jf.length; j++ ) {
                File file = jf[j];
                if ( !srcDirs.contains( file ) ) {
                    srcDirs.add( file );
                }
            }
        }
        return (File[]) srcDirs.toArray( new File[0] );
    }

    public File[] getAllDataDirectories() {
        PhetProject[] all = getAllDependencies();
        ArrayList jarFiles = new ArrayList();

        for ( int i = 0; i < all.length; i++ ) {
            PhetProject phetProject = all[i];
            File[] jf = phetProject.getDataDirectories();
            for ( int j = 0; j < jf.length; j++ ) {
                File file = jf[j];
                if ( !jarFiles.contains( file ) ) {
                    jarFiles.add( file );
                }
            }
        }
        return (File[]) jarFiles.toArray( new File[0] );
    }

    public File[] getAllJarFiles() {
        PhetProject[] all = getAllDependencies();
        ArrayList jarFiles = new ArrayList();

        for ( int i = 0; i < all.length; i++ ) {
            PhetProject phetProject = all[i];
            File[] jf = phetProject.getJarFiles();
            for ( int j = 0; j < jf.length; j++ ) {
                File file = jf[j];
                if ( !jarFiles.contains( file ) ) {
                    jarFiles.add( file );
                }
            }
        }
        return (File[]) jarFiles.toArray( new File[0] );
    }

    public File getClassesDirectory() {
        File file = new File( getAntOutputDir(), "classes" );
        file.mkdirs();
        return file;
    }

    public File getJarFile() {
        File file = new File( getAntOutputDir(), "jars/" + name + "_all.jar" );
        file.getParentFile().mkdirs();
        return file;
    }

    public File getAntOutputDir() {
        File destDir = new File( getAntBaseDir(), "ant_output/projects/" + name );
        destDir.mkdirs();
        return destDir;
    }

    private String getMainClassDefault() {
        return buildPropertiesFile.getMainClassDefault();
    }

    public String[] getAllMainClasses() {
        HashSet mainClasses = new HashSet();
        if ( getMainClassDefault() != null ) {
            mainClasses.add( getMainClassDefault() );
        }

        mainClasses.addAll( Arrays.asList( getKeepMains() ) );
        mainClasses.addAll( Arrays.asList( getAllSimulationMainClasses() ) );
        return (String[]) mainClasses.toArray( new String[0] );
    }

    /**
     * Returns the key values [simulation], not the titles for all simulations declared in this project.
     * If no simulations are declared, the project name is returned as the sole simulation.
     *
     * @return
     */
    public String[] getSimulationNames() {
        ArrayList simulationNames = new ArrayList();
        Enumeration e = buildPropertiesFile.getPropertyNames();
        while ( e.hasMoreElements() ) {
            String s = (String) e.nextElement();
            String prefix = "project.flavor.";
            if ( s.startsWith( prefix ) ) {
                String lastPart = s.substring( prefix.length() );
                int lastIndex = lastPart.indexOf( '.' );
                String simulationName = lastPart.substring( 0, lastIndex );
                if ( !simulationNames.contains( simulationName ) ) {
                    simulationNames.add( simulationName );
                }
            }
        }
        if ( simulationNames.size() == 0 ) {
            simulationNames.add( getName() );
        }

        return (String[]) simulationNames.toArray( new String[0] );
    }

    /**
     * Return an array of all declared simulations for this project.
     *
     * @return
     */
    public Simulation[] getSimulations() {//todo: separate locale-specific from locale dependent?
        String[] simulationNames = getSimulationNames();
        Simulation[] simulations = new Simulation[simulationNames.length];
        for ( int i = 0; i < simulationNames.length; i++ ) {
            simulations[i] = getSimulation( simulationNames[i] );
        }
        return simulations;
    }

    private String[] getAllSimulationMainClasses() {
        ArrayList mainClasses = new ArrayList();
        Simulation[] simulations = getSimulations();//see todo: in getSimulations(String)
        for ( int i = 0; i < simulations.length; i++ ) {
            Simulation simulation = simulations[i];
            if ( !mainClasses.contains( simulation.getMainclass() ) ) {
                mainClasses.add( simulation.getMainclass() );
            }
        }
        return (String[]) mainClasses.toArray( new String[0] );
    }

    public Simulation getSimulation( String simulationName ) {
        return getSimulation( simulationName, "en" );
    }

    /**
     * Load the simulation for associated with this project for the specified name and locale.
     * todo: better error handling for missing attributes (for projects that don't support simulations yet)
     *
     * @param simulationName
     * @return
     */
    public Simulation getSimulation( String simulationName, String locale ) {
        String mainclass = buildPropertiesFile.getMainClass( simulationName );
        if ( mainclass == null ) {
            mainclass = buildPropertiesFile.getMainClassDefault();
        }
        if ( mainclass == null ) {
            throw new RuntimeException( "Mainclass was null for project=" + name + ", simulation=" + simulationName );
        }
        String[] args = buildPropertiesFile.getArgs( simulationName );
        String screenshotPathname = buildPropertiesFile.getScreenshot( simulationName );
        File screenshot = new File( screenshotPathname == null ? "screenshot.gif" : screenshotPathname );

        //If we reuse PhetResources class, we should move Proguard usage out, so GPL doesn't virus over
        Properties localizedProperties = new Properties();
        try {
            File localizationFile = getLocalizationFile( locale );
            String title = null;
            String description = null;
            if ( localizationFile.exists() ) {
                localizedProperties.load( new FileInputStream( localizationFile ) );//todo: handle locale (graceful support for missing strings in locale)
                String titleKey = simulationName + ".name";
                title = localizedProperties.getProperty( titleKey );
                if ( title == null ) {
                    Properties englishProperties = new Properties();
                    englishProperties.load( new FileInputStream( getLocalizationFile( "en" ) ) );
                    title = englishProperties.getProperty( titleKey );
                    System.out.println( "PhetProject.getSimulation: missing title for simulation: key=" + titleKey + ", locale=" + locale + ", using English" );
                    if ( title == null ) {
                        title = simulationName;
                    }
                }
                String descriptionKey = simulationName + ".description";
                description = localizedProperties.getProperty( descriptionKey );
                if ( description == null ) {
                    Properties englishProperties = new Properties();
                    englishProperties.load( new FileInputStream( getLocalizationFile( "en" ) ) );
                    description = englishProperties.getProperty( descriptionKey );
                    System.out.println( "PhetProject.getSimulation: missing description for simulation: key=" + descriptionKey + ", locale=" + locale + ", using English" );
                    if ( description == null ) {
                        description = descriptionKey;
                    }
                }
            }
            else {
                System.out.println( "PhetProject.getSimulation: localization file doesn't exist: " + localizationFile.getAbsolutePath() );
                title = buildPropertiesFile.getTitleDefault();
                description = buildPropertiesFile.getDescriptionDefault();
                if ( title == null ) {
                    System.out.println( "PhetProject.getSimulation: project.name not found, using: " + name );
                    title = name;
                }
                if ( description == null ) {
                    System.out.println( "PhetProject.getSimulation: project.description not found, using empty string" );
                    description = "";
                }
            }

            return new Simulation( simulationName, title, description, mainclass, args, screenshot );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    /*
     * Returns an array of the 2-character locale codes supported by this application.
     */
    public Locale[] getLocales() {
        File localeDir = getLocalizationDir();
        File[] children = localeDir.listFiles();
        ArrayList locales = new ArrayList();
        for ( int i = 0; i < children.length; i++ ) {
            File child = children[i];
            String filename = child.getName();
            String prefix = getName() + "-strings_";
            String suffix = ".properties";
            if ( child.isFile() && filename.startsWith( prefix ) && filename.endsWith( suffix ) ) {
                String languageCode = filename.substring( prefix.length(), filename.length() - suffix.length() );
                locales.add( new Locale( languageCode ) );
            }
        }
        locales.add( new Locale( "en" ) );
        return (Locale[]) locales.toArray( new Locale[0] );
    }

    public File getLocalizationDir() {
        return new File( getProjectDir(), "data/" + getName() + "/localization" );
    }

    public File getLocalizationFile( String locale ) {
        String suffix = locale.equals( "en" ) || locale.equals( "" ) ? "" : "_" + locale;
        return new File( getLocalizationDir(), getName() + "-strings" + suffix + ".properties" );
    }

    public String getDevDirectoryBasename() {
        return projectPropertiesFile.getMajorVersionString() + "." + projectPropertiesFile.getMinorVersionString() + "." + projectPropertiesFile.getDevVersionString();
    }

    public String getFullVersionString() {
        return projectPropertiesFile.getFullVersionString();
    }

    public int getMajorVersion() {
        return projectPropertiesFile.getMajorVersion();
    }

    public int getMinorVersion() {
        return projectPropertiesFile.getMinorVersion();
    }

    public int getDevVersion() {
        return projectPropertiesFile.getDevVersion();
    }

    public int getVersionProperty( String property ) {
        Properties prop = new Properties();
        try {
            prop.load( new FileInputStream( getVersionFile() ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return Integer.parseInt( prop.getProperty( "version." + property ) );
    }

    public static String[] getSimNames( File basedir ) {
        File[] simulations = new File( basedir, "simulations" ).listFiles();
        ArrayList sims = new ArrayList();
        for ( int i = 0; i < simulations.length; i++ ) {
            File simulation = simulations[i];
            if ( isSimulation( simulation ) ) {
                sims.add( simulation.getName() );
            }
        }
        return (String[]) sims.toArray( new String[0] );
    }


    private static boolean isSimulation( File simulation ) {
        return simulation.isDirectory() && !simulation.getName().equalsIgnoreCase( "all-sims" ) && !simulation.getName().equalsIgnoreCase( ".svn" ) && new File( simulation, simulation.getName() + "-build.properties" ).exists();
    }

    public static PhetProject[] getAllProjects( File baseDir ) {
        List phetProjects = new ArrayList();
        String[] sims = getSimNames( baseDir );
        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];
            File projectDir = PhetBuildUtils.resolveProject( baseDir, sim );
            try {
                PhetProject phetProject = new PhetProject( projectDir, sim );
                phetProjects.add( phetProject );
            }
            catch( IOException e ) {
                throw new BuildException( e );
            }
        }
        return (PhetProject[]) phetProjects.toArray( new PhetProject[0] );
    }

    public String getDeployedSimulationJarURL() {
        return WEBROOT + getName() + "/" + getName() + "_all.jar";
    }

    public File getTranslationFile( Locale locale ) {
        String lang = locale.getLanguage().equals( "en" ) ? "" : "_" + locale.getLanguage();
        return new File( projectDir, "data" + File.separator + getName() + File.separator + "localization" + File.separator + getName() + "-strings" + lang + ".properties" );
    }

    public File getBuildPropertiesFile() {
        return new File( getProjectDir(), getName() + "-build.properties" );
    }

    public File getDataDirectory() {
        return new File( getProjectDir(), "data/" + getName() );
    }

    public String getPackageName() {
        String name = getName();//remove hyphens
        return name.replaceAll( "-", "" );
    }

    public void setMajorVersion( int value ) {
        projectPropertiesFile.setMajorVersion( value );
    }

    public void setMinorVersion( int value ) {
        projectPropertiesFile.setMinorVersion( value );
    }

    public void setDevVersion( int value ) {
        projectPropertiesFile.setDevVersion( value );
    }

    public void setSVNVersion( int value ) {
        projectPropertiesFile.setSVNVersion( value );
    }

    public void setVersionTimestamp( int value ) {
        projectPropertiesFile.setVersionTimestamp( value );
    }

    /*
     * ***********
     * Licensing Information
     * ***********
     */
    public LicenseInfo[] getAllLicenseInfo() {
        PhetProject[] p = getAllDependencies();
        ArrayList infos = new ArrayList();
        for ( int i = 0; i < p.length; i++ ) {
            infos.addAll( Arrays.asList( p[i].getLicenseInfo() ) );
        }

        //todo: also need to get license info for data/ directories and jar/ files, such as cck: nanoxml
        return (LicenseInfo[]) infos.toArray( new LicenseInfo[infos.size()] );
    }

    /**
     * Returns the LicenseInfo for this sim only, not for dependencies
     *
     * @return
     */
    public LicenseInfo[] getLicenseInfo() {
        return LicenseInfo.getAll( new File( getProjectDir(), "license-info.txt" ) );
    }

    //Returns media info for this project and all dependencies
    public DataResource[] getAllMediaInfo() {
        ArrayList mediaInfo = new ArrayList();
        PhetProject[] dependencies = getAllDependencies();
        for ( int i = 0; i < dependencies.length; i++ ) {
            mediaInfo.addAll( Arrays.asList( dependencies[i].getMediaInfo() ) );
        }
        return (DataResource[]) mediaInfo.toArray( new DataResource[mediaInfo.size()] );
    }

    //Returns media info for this project only (not dependencies)
    private DataResource[] getMediaInfo() {
        File data = getDataDirectory();
        if ( data.isDirectory() && data.exists() ) {
            File[] f = listFilesRecursive( data );
            //for each data file, track down annotation data, if it exists
            DataResource[] m = new DataResource[f.length];
            for ( int i = 0; i < m.length; i++ ) {
                m[i] = new DataResource( f[i] );
            }
            return m;
        }
        else {
            return new DataResource[0];
        }
    }

    private File[] listFilesRecursive( File data ) {
        ArrayList files = new ArrayList();
        File[] ch = data.listFiles();
        for ( int i = 0; i < ch.length; i++ ) {
            File file = ch[i];
            if ( file.isDirectory() ) {
                if ( !isIgnoreDirectory( file ) ) {
                    files.addAll( Arrays.asList( listFilesRecursive( file ) ) );
                }
            }
            else {
                files.add( file );
            }
        }
        return (File[]) files.toArray( new File[files.size()] );
    }

    private boolean isIgnoreDirectory( File file ) {
        return file.getName().startsWith( ".svn" ) || file.getName().startsWith( "localization" );
    }

    public File getVersionFile() {
        return new File( getProjectDir(), "data/" + getName() + "/" + getName() + ".properties" );
    }

    public File getChangesFile() {
        return new File( getDeployDir(), "changes.txt" );
    }

    public String getChangesText() {
        File changesFile = getChangesFile();
        if ( !changesFile.exists() ) {
            return "";
        }
        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader( changesFile ) );
            StringBuffer s = new StringBuffer();
            String line = bufferedReader.readLine();
            while ( line != null ) {
                s.append( line );
                line = bufferedReader.readLine();
                if ( line != null ) {
                    s.append( "\n" );
                }
            }
            return s.toString();
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public void prependChangesText( String message ) {
        String changes = message + "\n" + getChangesText();
        try {
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( getChangesFile() ) );
            bufferedWriter.write( changes );
            bufferedWriter.close();
            notifyChangesTextChanged();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private ArrayList listeners = new ArrayList();

    public boolean containsScalaSource() {
        return getAllScalaSourceRoots().length > 0;
    }

    public File getDeployHeaderFile() {
        return new File( getDeployDir(), "HEADER" );
    }

    public void copyLicenseInfo() {
        File contribLicensesDir = new File( getDataDirectory(), "contrib-licenses" );
        File file = new File( contribLicensesDir, "license-info.txt" );
        System.out.println( "file.getAbsolute = " + file.getAbsolutePath() );
        contribLicensesDir.mkdirs();
        try {
            LicenseInfo[] licenseInfo = getAllLicenseInfo();

            //add top-level file
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( file ) );
            bufferedWriter.write( "#This file identifies licenses of contibuted libraries\n" );
            for ( int i = 0; i < licenseInfo.length; i++ ) {
                bufferedWriter.write( licenseInfo[i].toString() + "\n" );
            }
            bufferedWriter.close();

            //copy licenses
            for ( int i = 0; i < licenseInfo.length; i++ ) {
                LicenseInfo info = licenseInfo[i];
                File licenseFile = info.getLicenseFile();
                if ( licenseFile != null && licenseFile.exists() ) {
                    FileUtils.copyTo( licenseFile, new File( contribLicensesDir, "" + info.getID() + "-" + licenseFile.getName() ) );
                }
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public String[] getCreditsKeys() {
        ArrayList strings = new ArrayList();
        try {
            File creditsFile = new File( getDataDirectory(), "credits.txt" );
            if ( !creditsFile.exists() ) {
                System.out.println( getName() + " missing credits.txt" );
            }
            else {
                String text = FileUtils.loadFileAsString( creditsFile );
                AnnotationParser.Annotation[] a = AnnotationParser.getAnnotations( text );
                for ( int i = 0; i < a.length; i++ ) {
                    AnnotationParser.Annotation annotation = a[i];
                    strings.addAll( annotation.getMap().keySet() );
                }
                return (String[]) strings.toArray( new String[strings.size()] );
            }
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public static interface Listener {
        public void changesTextChanged();
    }

    public void notifyChangesTextChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).changesTextChanged();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }
}
