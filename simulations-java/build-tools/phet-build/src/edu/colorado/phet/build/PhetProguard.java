package edu.colorado.phet.build;

import proguard.ant.ProGuardTask;

import java.io.*;

/**
 * Author: Sam Reid
 * Apr 14, 2007, 3:02:14 PM
 */
public class PhetProguard {

    public void proguard( PhetProject phetProject, boolean shrink ) {
        File proguardFile = createProguardFile( phetProject, shrink );
        ProGuardTask proGuardTask = new ProGuardTask();
        proGuardTask.setConfiguration( proguardFile );
        phetProject.runTask( proGuardTask );
    }

    private static File[] append( File[] list, File file ) {
        File[] f = new File[list.length + 1];
        System.arraycopy( list, 0, f, 0, list.length );
        f[list.length] = file;
        return f;
    }

    private static File[] prepend( File[] list, File file ) {
        File[] f = new File[list.length + 1];
        System.arraycopy( list, 0, f, 1, list.length );
        f[0] = file;
        return f;
    }

    public boolean createProguardFile( String name, File template, File proguardFile, File[] injars, File outJar, String[] mainClasses, boolean shrink ) {
        String newline = System.getProperty( "line.separator" );
        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader( template ) );
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( proguardFile ) );
            bufferedWriter.write( "# Proguard configuration file for " + name + "." + newline );
            bufferedWriter.write( "# Automatically generated" + newline );

            for( int i = 0; i < injars.length; i++ ) {
                bufferedWriter.write( "-injars '" + injars[i].getAbsolutePath() + "'" + newline );
            }

            bufferedWriter.write( "-outjars '" + outJar.getAbsolutePath() + "'" + newline );
            bufferedWriter.write( "-libraryjars <java.home>/lib/rt.jar" + newline );//todo: handle mac library
            for( int i = 0; i < mainClasses.length; i++ ) {
                bufferedWriter.write( "-keepclasseswithmembers public class " + mainClasses[i] + "{" + newline +
                                      "    public static void main(java.lang.String[]);" + newline +
                                      "}" + newline );
                bufferedWriter.newLine();
            }

            bufferedWriter.write( "# shrink = " + shrink + newline );
            if( !shrink ) {
                bufferedWriter.write( "-dontshrink" + newline );
            }

            String line = bufferedReader.readLine();
            while( line != null ) {
                bufferedWriter.write( line + newline );
                line = bufferedReader.readLine();
            }

            bufferedWriter.close();
            bufferedReader.close();
            return true;
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public File createProguardFile( PhetProject project, boolean shrink ) {
        File template = new File( project.getBaseDir(), "templates/proguard2.pro" );
        File output = new File( project.getAntOutputDir(), project.getName() + ".pro" );
        File[] injars = prepend( project.getAllJarFiles(), project.getJarFile() );
        File outJar = new File( project.getAntOutputDir(), "jars/" + project.getName() + "_pro.jar" );
        boolean ok = createProguardFile( project.getName(), template, output, injars, outJar, new String[]{project.getMainClass()}, shrink );
//        boolean ok = createProguardFile( project.getName(), template, output, injars, outJar, project.getMainClasses(), shrink );
        return ok ? output : null;
    }

}
