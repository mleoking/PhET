package edu.colorado.phet.buildtools;

import java.io.*;
import java.util.*;

import edu.colorado.phet.buildtools.flash.PhetFlashProject;
import edu.colorado.phet.buildtools.scripts.SetSVNIgnoreToDeployDirectories;
import edu.colorado.phet.buildtools.util.*;
import edu.colorado.phet.buildtools.projects.UpdaterProject;
import edu.colorado.phet.common.phetcommon.resources.PhetProperties;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.util.AnnotationParser;

/**
 * Author: Sam Reid
 * Apr 14, 2007, 2:40:56 PM
 */
public abstract class PhetProject {

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

    public BuildPropertiesFile getBuildPropertiesFileObject() {
        return buildPropertiesFile;
    }

    public File getDeployDir() {
        File file = new File( getProjectDir(), "deploy/" );

        file.mkdirs();

        return file;
    }

    public File getDefaultDeployJar() {
        return new File( getDeployDir(), getName() + "_all.jar" );
    }

    //todo: can the following method be removed?  It looks suspicious.
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

    public File getSimulationsJava() {
        return new File(getTrunk(),"simulations-java" );
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
                    projects.add( new PhetJavaProject( file ) );
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
        File commonProject = new File( getSimulationsJava(), "common/" + token );
        if ( commonProject.exists() && isProject( commonProject ) ) {
            return commonProject;
        }
        File contribPath = new File( getSimulationsJava(), "contrib/" + token );
        if ( contribPath.exists() ) {
            return contribPath;
        }
        File path = new File( projectDir, token );
        if ( path.exists() ) {
            return path;
        }
        File commonPathNonProject = new File( getSimulationsJava(), "common/" + token );
        if ( commonPathNonProject.exists() ) {
            return commonPathNonProject;
        }
        File simProject = new File( getSimulationsJava(), "simulations/" + token );
        if ( simProject.exists() && isProject( simProject ) ) {
            return simProject;
        }
        throw new RuntimeException( "No path found for token=" + token + ", antBaseDir=" + getSimulationsJava().getAbsolutePath() + ", in project=" + this );
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
        File destDir = new File( getSimulationsJava(), "ant_output/projects/" + name );
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

    public abstract Simulation getSimulation( String simulationName, String locale );

    /*
    * Returns an array of the 2-character locale codes supported by this application.
    */
    public abstract Locale[] getLocales();

    protected Locale[] getLocalesImpl( String suffix ) {
        File localeDir = getLocalizationDir();
        File[] children = localeDir.listFiles();
        ArrayList locales = new ArrayList();
        for ( int i = 0; children !=null && i < children.length ; i++ ) {
            File child = children[i];
            String filename = child.getName();
            String prefix = getName() + "-strings_";
//            String suffix = ".properties";
            if ( child.isFile() && filename.startsWith( prefix ) && filename.endsWith( suffix ) ) {
                String languageCode = filename.substring( prefix.length(), filename.length() - suffix.length() );
                locales.add( new Locale( languageCode ) );
            }
        }
        if ( !locales.contains( new Locale( "en" ) ) ) {
            locales.add( new Locale( "en" ) );
        }
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
        for ( int i = 0; simulations!=null && i < simulations.length; i++ ) {
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

    public static PhetProject[] getAllProjects( File trunk ) {
        List phetProjects = new ArrayList();

        phetProjects.addAll( sort( Arrays.asList( PhetJavaProject.getJavaProjects( trunk ) ) ) );
        phetProjects.addAll( sort( Arrays.asList( PhetFlashProject.getFlashProjects( trunk ) ) ) );
        try {
            phetProjects.add(new UpdaterProject( new File( trunk, "util/updater" ) ));
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return (PhetProject[]) phetProjects.toArray( new PhetProject[phetProjects.size()] );
    }

    private static Collection sort( List projectList ) {
        Collections.sort( projectList, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                PhetProject a = (PhetProject) o1;
                PhetProject b = (PhetProject) o2;
                return a.getName().compareTo( b.getName() );
            }
        } );
        return new ArrayList( projectList );
    }

    public String getDeployedSimulationJarURL() {
        return PhetServer.PRODUCTION.getWebDeployURL( this ) + "/" + getName() + "_all.jar";
    }

    public abstract File getTranslationFile( Locale locale );

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

    public void setVersionTimestamp( long value ) {
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

        //also need to get license info for data/ directories and jar/ files, such as cck: nanoxml
        File[] j = getAllJarFiles();
        for ( int i = 0; i < j.length; i++ ) {
            File file = j[i];
            File licenseFile = new File( file.getParentFile(), "license-info.txt" );
            if ( licenseFile.exists() ) {
                infos.addAll( Arrays.asList( LicenseInfo.getAll( licenseFile ) ) );
            }
        }

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
        File contribLicensesDir = getContribLicenseDir();
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

        try {
            SetSVNIgnoreToDeployDirectories.setIgnorePatternsOnDir( contribLicensesDir.getParentFile(), new String[]{contribLicensesDir.getName()} );
            //todo: redirect system.out and system.err
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

    public File getContribLicenseDir() {
        return new File( getDataDirectory(), "contrib-licenses" );
    }


    public PhetProperties getProjectProperties() {
        PhetProperties phetProperties = new PhetProperties();
        try {
            phetProperties.load( new FileInputStream( getProjectPropertiesFile() ) );
            return phetProperties;
        }
        catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public PhetVersion getVersion() {
        return PhetResources.getVersion( getProjectProperties() );
    }

    //this one includes the version, and background color for flash
    private File getProjectPropertiesFile() {
        return new File( getDataDirectory(), getName() + ".properties" );
    }

        //assumes a java simulation
    public File getTrunk() {
        return new File(getProjectDir(),"../../..");
    }

    //returns main class to use other than JARLauncher
    //primarily for use in non-simulation projects such as util/updater
    public String getAlternateMainClass() {
        return null;
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

    public abstract boolean build() throws Exception;

    public abstract String getListDisplayName();

    public abstract void runSim( Locale locale, String simulationName );

    public abstract void buildLaunchFiles( String URL, boolean dev );
}
