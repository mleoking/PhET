package edu.colorado.phet.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by: Sam
 * Nov 19, 2007 at 6:17:23 PM
 */
public class CheckTranslations {
    public static void main( String[] args ) throws IOException {
        Sim[] local = getLocalSims();
        Sim[] web = listWebSims();
        for ( int i = 0; i < local.length; i++ ) {
            Sim localSim = local[i];
            for ( int k = 0; k < localSim.getTranslations().length; k++ ) {
                String t = localSim.getTranslations()[k];
                if ( !webContains( web, localSim.getName(), t ) ) {
                    System.out.println( "Web is missing: " + localSim.getName() + ", translation: " + t );
                }
            }
        }
    }

    private static boolean webContains( Sim[] web, String name, String t ) {
        for ( int i = 0; i < web.length; i++ ) {
            Sim s = web[i];
            if ( s.getName().equals( name ) && s.containsTranslation( t ) ) {
                return true;
            }
        }
        return false;
    }

    public static Sim[] listWebSims() throws IOException {
        URL url = new URL( "http://phet.colorado.edu/new/admin/list-translations.php" );
        URLConnection yc = url.openConnection();
        BufferedReader in = new BufferedReader( new InputStreamReader( yc.getInputStream() ) );
        String inputLine;

        ArrayList lines = new ArrayList();
        while ( ( inputLine = in.readLine() ) != null ) {
            System.out.println( inputLine );
            lines.add( inputLine + "" );
        }
        in.close();
        System.out.println( "lines = " + lines );
        if ( lines.size() > 1 ) {
            System.out.println( "Error, multiple line output" );
        }
        String line = (String) lines.get( 0 );
        line = line.replaceAll( "</br>", "," );
        System.out.println( "line = " + line );

        StringTokenizer st = new StringTokenizer( line, "," );
        ArrayList list = new ArrayList();
        while ( st.hasMoreTokens() ) {
            list.add( st.nextToken() );
        }

//        ArrayList sims=new ArrayList( );
        HashMap map = new HashMap();
        for ( int i = 0; i < list.size(); i++ ) {
            String s = (String) list.get( i );

            final String str = s.substring( prefix.length() );
            String sim = new StringTokenizer( str, "/" ).nextToken();
            map.put( sim, "value" );
        }
        ArrayList simList = new ArrayList();
        Set keys = map.keySet();
        for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            String simName = (String) iterator.next();
            simList.add( new Sim( simName, getSimFlavors( simName, (String[]) list.toArray( new String[0] ) ) ) );
        }
        return (Sim[]) simList.toArray( new Sim[0] );
    }

    static String prefix = "http://phet.colorado.edu/sims/";

    private static String[] getSimFlavors( String simName, String[] jnlpFiles ) {
        ArrayList langs = new ArrayList();
        for ( int i = 0; i < jnlpFiles.length; i++ ) {
            String jnlpFile = jnlpFiles[i];
            if ( jnlpFile.startsWith( prefix + simName ) ) {
                String suffix = ".jnlp";
                String language = jnlpFile.substring( jnlpFile.length() - suffix.length() - 2, jnlpFile.length() - suffix.length() );
                if ( !langs.contains( language ) ) {
                    langs.add( language );
                }
            }
        }
        return (String[]) langs.toArray( new String[0] );
    }

    public static Sim[] getLocalSims() {
        ArrayList sims = new ArrayList();
        File simDir = new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations" );
        File[] f = simDir.listFiles();
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            String projectName = file.getName();
            File localization = new File( file, "data/" + projectName + "/localization" );
            ArrayList locales = new ArrayList();
            if ( localization.exists() && !localization.getName().equalsIgnoreCase( ".svn" ) && !projectName.equalsIgnoreCase( ".svn" ) ) {
                File[] localizations = localization.listFiles();
                for ( int j = 0; j < localizations.length; j++ ) {
                    File localization1 = localizations[j];
                    final String prefix = projectName + "-strings_";
                    if ( localization1.getName().startsWith( prefix ) && localization1.getName().indexOf( "_" ) >= 0 ) {
                        locales.add( localization1.getName().substring( prefix.length(), prefix.length() + 2 ) );
                    }
                }
                sims.add( new Sim( projectName, (String[]) locales.toArray( new String[0] ) ) );
            }

        }
        return (Sim[]) sims.toArray( new Sim[0] );
    }


    public static class Sim {
        private String name;
        private String[] translations;

        public Sim( String name, String[] translations ) {
            this.name = name;
            this.translations = translations;
        }

        public String toString() {
            return "" + name + " " + Arrays.asList( translations );
        }

        public String[] getTranslations() {
            return translations;
        }

        public String getName() {
            return name;
        }

        public boolean containsTranslation( String t ) {
            return Arrays.asList( translations ).contains( t );
        }
    }
}
