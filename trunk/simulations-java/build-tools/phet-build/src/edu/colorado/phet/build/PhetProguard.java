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

    public File createProguardFile( PhetProject project, boolean shrink ) {
        try {
            File template = new File( project.getBaseDir(), "templates/proguard2.pro" );
            File output = new File( project.getAntOutputDir(), project.getName() + ".pro" );
            BufferedReader bufferedReader = new BufferedReader( new FileReader( template ) );
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( output ) );
            bufferedWriter.write( "# Proguard configuration file for " + project.getName() + "." );
            bufferedWriter.newLine();
            bufferedWriter.write( "# Automatically generated" );
            bufferedWriter.newLine();

            File[] libs = prepend( project.getAllJarFiles(), project.getJarFile() );
            for( int j = 0; j < libs.length; j++ ) {
                bufferedWriter.write( "-injars '" + libs[j].getAbsolutePath() + "'" );
                bufferedWriter.newLine();
            }
            File outJar = new File( project.getAntOutputDir(), "jars/" + project.getName() + "_pro.jar" );
            bufferedWriter.write( "-outjars '" + outJar.getAbsolutePath() + "'" );
            bufferedWriter.newLine();
            bufferedWriter.write( "-libraryjars <java.home>/lib/rt.jar" );//todo: handle mac library
            bufferedWriter.newLine();
            bufferedWriter.write( "-keepclasseswithmembers public class " + project.getMainClass() + "{\n" +
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

}
