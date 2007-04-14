package edu.colorado.phet.build;

import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import proguard.ant.ProGuardTask;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Author: Sam Reid
 * Apr 14, 2007, 2:40:56 PM
 */
public class PhetProject {
    private File rootDir;
    private Properties properties;
    private String name;
    private PhetBuildTask phetBuildTask;

    public PhetProject( PhetBuildTask phetBuildTask, File parentDir, String name ) throws IOException {
        this.phetBuildTask = phetBuildTask;
        this.name = name;
        this.rootDir = new File( parentDir, name );
        this.properties = new Properties();
        File propertyFile = new File( rootDir, name + ".properties" );
        this.properties.load( new BufferedInputStream( new FileInputStream( propertyFile ) ) );
    }

    public boolean equals( Object obj ) {
        if( obj instanceof PhetProject ) {
            PhetProject phetProject = (PhetProject)obj;
            return phetProject.rootDir.equals( rootDir );
        }
        else {
            return false;
        }
    }

    public PhetProject( PhetBuildTask phetBuildTask, File projectRoot ) throws IOException {
        this( phetBuildTask, projectRoot.getParentFile(), projectRoot.getName() );
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

    private String toString( File[] files ) {
        String string = "";
        for( int i = 0; i < files.length; i++ ) {
            File file = files[i];
            string += file.getAbsolutePath();
            if( i < files.length - 1 ) {
                string += " : ";
            }
        }
        return string;
    }

    /**
     * Returns the immediate dependencies for this PhetProject
     *
     * @return
     */
    public PhetProject[] getDependencies() {
        ArrayList projects = new ArrayList();
        File[] path = expandPath( getLib() );
        for( int i = 0; i < path.length; i++ ) {
            File file = path[i];
            if( file.exists() && isProject( file ) ) {
                try {
                    projects.add( new PhetProject( phetBuildTask, file ) );
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
        File commonProject = new File( phetBuildTask.getBaseDir(), "common/" + token );
        if( commonProject.exists() && isProject( commonProject ) ) {
            return commonProject;
        }
        File contribPath = new File( phetBuildTask.getBaseDir(), "contrib/" + token );
        if( contribPath.exists() ) {
            return contribPath;
        }
        File path = new File( rootDir, token );
        if( path.exists() ) {
            return path;
        }
        File commonPathNonProject = new File( phetBuildTask.getBaseDir(), "common/" + token );
        if( commonPathNonProject.exists() ) {
            return commonPathNonProject;
        }
        File simProject = new File( phetBuildTask.getBaseDir(), "simulations/" + token );
        if( simProject.exists() && isProject( simProject ) ) {
            return simProject;
        }
        throw new RuntimeException( "No path found for token=" + token + ", in project=" + this );
    }

    public void buildAll( boolean shrink ) {
        compile( getAllSourceRoots(), getAllJarFiles(), getClassesDirectory() );

        jar();

        File proguardFile = createProguardFile( shrink );
        ProGuardTask proGuardTask = new ProGuardTask();
        proGuardTask.setConfiguration( proguardFile );
        phetBuildTask.runTask( proGuardTask );
//            ProGuard.main( new String[]{"@" + proguardFile.getAbsolutePath()} );//causes jvm exit
    }

    public File[] append( File[] list, File file ) {
        File[] f = new File[list.length + 1];
        System.arraycopy( list, 0, f, 0, list.length );
        f[list.length] = file;
        return f;
    }

    public File[] prepend( File[] list, File file ) {
        File[] f = new File[list.length + 1];
        System.arraycopy( list, 0, f, 1, list.length );
        f[0] = file;
        return f;
    }

    public File createProguardFile( boolean shrink ) {

        try {
            File template = new File( phetBuildTask.getBaseDir(), "templates/proguard2.pro" );
            File output = new File( getAntOutputDir(), name + ".pro" );
            BufferedReader bufferedReader = new BufferedReader( new FileReader( template ) );
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( output ) );
            bufferedWriter.write( "# Proguard configuration file for " + name + "." );
            bufferedWriter.newLine();
            bufferedWriter.write( "# Automatically generated" );
            bufferedWriter.newLine();

            File[] libs = prepend( getAllJarFiles(), getJarFile() );
            for( int j = 0; j < libs.length; j++ ) {
                bufferedWriter.write( "-injars '" + libs[j].getAbsolutePath() + "'" );
                bufferedWriter.newLine();
            }
            File outJar = new File( getAntOutputDir(), "jars/" + name + "_pro.jar" );
            bufferedWriter.write( "-outjars '" + outJar.getAbsolutePath() + "'" );
            bufferedWriter.newLine();
            bufferedWriter.write( "-libraryjars <java.home>/lib/rt.jar" );//todo: handle mac library
            bufferedWriter.newLine();
            bufferedWriter.write( "-keepclasseswithmembers public class " + getMainClass() + "{\n" +
                                  "    public static void main(java.lang.String[]);\n" +
                                  "}" );
            bufferedWriter.newLine();


            bufferedWriter.write( "# shrink = " + shrink );
            bufferedWriter.newLine();
            if( !shrink ) {
                bufferedWriter.write( "-dontshrink" );
                bufferedWriter.newLine();
            }

            String line = bufferedReader.readLine();
            while( line != null ) {
                bufferedWriter.write( line );
                bufferedWriter.newLine();
                line = bufferedReader.readLine();
            }

            bufferedWriter.close();
            bufferedReader.close();
            return output;
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    private File[] getAllSourceRoots() {
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

    private File getClassesDirectory() {
        File file = new File( getAntOutputDir(), "classes" );
        file.mkdirs();
        return file;
    }

    public void jar() {

        Jar jar = new Jar();
        File[] dataDirectories = getAllDataDirectories();
        for( int i = 0; i < dataDirectories.length; i++ ) {
            FileSet set = new FileSet();
            set.setDir( dataDirectories[i] );
            jar.addFileset( set );
        }
        jar.setBasedir( getClassesDirectory() );
        jar.setJarfile( getJarFile() );
        Manifest manifest = new Manifest();
        try {
            Manifest.Attribute attribute = new Manifest.Attribute();
            attribute.setName( "Main-Class" );
            attribute.setValue( getMainClass() );
            manifest.addConfiguredAttribute( attribute );
            jar.addConfiguredManifest( manifest );
        }
        catch( ManifestException e ) {
            e.printStackTrace();
        }

        phetBuildTask.runTask( jar );
    }

    private String getMainClass() {
        return properties.getProperty( "project.mainclass" );
    }

    private File getJarFile() {
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

    public void compile( File[] src, File[] classpath, File dst ) {
        phetBuildTask.output( "compiling " + name );
        Javac javac = new Javac();
        javac.setSource( "1.4" );
        javac.setSrcdir( new Path( phetBuildTask.getProject(), toString( src ) ) );
        javac.setDestdir( getClassesDirectory() );
        javac.setClasspath( new Path( phetBuildTask.getProject(), toString( classpath ) ) );
        phetBuildTask.runTask( javac );
        phetBuildTask.output( "Finished compiling " + name + "." );
    }

    private File getAntOutputDir() {
        File destDir = new File( phetBuildTask.getProject().getBaseDir(), "ant_output/projects/" + name );
        destDir.mkdirs();
        return destDir;
    }

    public void build() {
        compile( getSrcFiles(), getJarFiles(), new File( phetBuildTask.getProject().getBaseDir(), "ant_output/projects/" + name ) );
    }
}
