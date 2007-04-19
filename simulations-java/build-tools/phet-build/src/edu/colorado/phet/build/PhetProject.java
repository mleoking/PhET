package edu.colorado.phet.build;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Author: Sam Reid
 * Apr 14, 2007, 2:40:56 PM
 */
public class PhetProject {
    private final File dir;
    private final Properties properties;
    private final String name;

    public PhetProject( File projectRoot ) throws IOException {
        this( projectRoot.getParentFile(), projectRoot.getName() );
    }

    public PhetProject( File parentDir, String name ) throws IOException {
        this.name       = name;
        this.dir        = new File( parentDir, name );
        this.properties = new Properties();

        File propertyFile = PhetBuildUtils.getBuildPropertiesFile( dir, name );

        this.properties.load( new BufferedInputStream( new FileInputStream( propertyFile ) ) );
    }

    public File getDir() {
        return dir;
    }

    public boolean equals( Object obj ) {
        if( obj instanceof PhetProject ) {
            PhetProject phetProject = (PhetProject)obj;
            return phetProject.dir.equals( dir );
        }
        else {
            return false;
        }
    }

    public File getAntBaseDir() {
        return new File( "../../" );
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "project=" + name + ", root=" + dir.getAbsolutePath() + ", properties=" + properties;
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

    public String[] getKeepMains() {
        String v = properties.getProperty( "project.keepmains" );
        if( v == null ) {
            v = "";
        }
        return split( v, ": " );
    }

    private File[] getDataDirectories() {
        return expandPath( getData() );
    }

    public File[] getJarFiles() {
        ArrayList all = new ArrayList( Arrays.asList( expandPath( getLib() ) ) );
        for( int i = 0; i < all.size(); i++ ) {
            File file = (File)all.get( i );
            if( isProject( file ) ) {
                all.remove( i );
                i--;
            }
        }
        return (File[])all.toArray( new File[0] );
    }

    public File[] getSrcFiles() {
        return expandPath( getSource() );
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

    /**
     * Returns the immediate dependencies for this PhetProject
     *
     * @return the immediate dependencies declared for this project
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
        if( !file.exists() || !file.isDirectory() ) {
            return false;
        }
        if( new File( file, file.getName() + ".properties" ).exists() ) {
            return true;
        }
        else {
            return false;
        }
    }

    private String[] split( String str, String delimiters ) {
        ArrayList out = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer( str, delimiters );
        while( stringTokenizer.hasMoreTokens() ) {
            out.add( stringTokenizer.nextToken() );
        }
        return (String[])out.toArray( new String[0] );
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
        File commonProject = new File( getAntBaseDir(), "common/" + token );
        if( commonProject.exists() && isProject( commonProject ) ) {
            return commonProject;
        }
        File contribPath = new File( getAntBaseDir(), "contrib/" + token );
        if( contribPath.exists() ) {
            return contribPath;
        }
        File path = new File( dir, token );
        if( path.exists() ) {
            return path;
        }
        File commonPathNonProject = new File( getAntBaseDir(), "common/" + token );
        if( commonPathNonProject.exists() ) {
            return commonPathNonProject;
        }
        File simProject = new File( getAntBaseDir(), "simulations/" + token );
        if( simProject.exists() && isProject( simProject ) ) {
            return simProject;
        }
        throw new RuntimeException( "No path found for token=" + token + ", in project=" + this );
    }

    public File[] getAllSourceRoots() {
        PhetProject[] dependencies = getAllProjects();
        ArrayList srcDirs = new ArrayList();
        for( int i = 0; i < dependencies.length; i++ ) {
            PhetProject dependency = dependencies[i];
            File[] jf = dependency.getSrcFiles();
            for( int j = 0; j < jf.length; j++ ) {
                File file = jf[j];
                if( !srcDirs.contains( file ) ) {
                    srcDirs.add( file );
                }
            }
        }
        return (File[])srcDirs.toArray( new File[0] );
    }

    public File[] getAllDataDirectories() {
        PhetProject[] all = getAllProjects();
        ArrayList jarFiles = new ArrayList();

        for( int i = 0; i < all.length; i++ ) {
            PhetProject phetProject = all[i];
            File[] jf = phetProject.getDataDirectories();
            for( int j = 0; j < jf.length; j++ ) {
                File file = jf[j];
                if( !jarFiles.contains( file ) ) {
                    jarFiles.add( file );
                }
            }
        }
        return (File[])jarFiles.toArray( new File[0] );
    }

    public File[] getAllJarFiles() {
        PhetProject[] all = getAllProjects();
        ArrayList jarFiles = new ArrayList();

        for( int i = 0; i < all.length; i++ ) {
            PhetProject phetProject = all[i];
            File[] jf = phetProject.getJarFiles();
            for( int j = 0; j < jf.length; j++ ) {
                File file = jf[j];
                if( !jarFiles.contains( file ) ) {
                    jarFiles.add( file );
                }
            }
        }
        return (File[])jarFiles.toArray( new File[0] );
    }

    public File getClassesDirectory() {
        File file = new File( getAntOutputDir(), "classes" );
        file.mkdirs();
        return file;
    }

    public File getJarFile() {
        File file = new File( getAntOutputDir(), "jars/" + name + ".jar" );
        file.getParentFile().mkdirs();
        return file;
    }

    private PhetProject[] getAllProjects() {
        ArrayList projects = new ArrayList( Arrays.asList( getAllDependencies() ) );
        if( !projects.contains( this ) ) {
            projects.add( this );
        }
        return (PhetProject[])projects.toArray( new PhetProject[0] );
    }

    public File getAntOutputDir() {
        File destDir = new File( getAntBaseDir(), "ant_output/projects/" + name );
        destDir.mkdirs();
        return destDir;
    }
    
    public String getMainClass() {
        return properties.getProperty( "project.mainclass" );
    }

    public String[] getMainClasses() {
        return new String[]{ getMainClass() };
    }

    public String[] getAllMainClasses() {
        ArrayList all = new ArrayList();
        all.add( getMainClass() );
        all.addAll( Arrays.asList( getKeepMains() ) );
        return (String[])all.toArray( new String[0] );
    }
}
