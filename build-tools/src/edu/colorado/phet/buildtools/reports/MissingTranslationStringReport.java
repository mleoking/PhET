package edu.colorado.phet.buildtools.reports;

import java.io.File;
import java.io.IOException;
import java.util.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.flex.FlexSimulationProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.buildtools.translate.Translation;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

/**
 * Show what strings are missing from translations. Puts this in a report located at trunk/build-tools/deploy/missing-translation-string-report.html
 */
public class MissingTranslationStringReport {
    private static Set<String> IGNORE_KEYS = new HashSet<String>() {{
        add( "ksu.credits" );
        add( "translation.credits" );
    }};

    public static void main( String[] args ) {
        File trunk = new File( args[0] );

        // load up build-local properties in case we use it later
        BuildLocalProperties.initRelativeToTrunk( trunk );

        // iterate over all simulation projects
        final List<PhetProject> simProjects = new LinkedList<PhetProject>();
        simProjects.addAll( Arrays.asList( FlashSimulationProject.getFlashProjects( trunk ) ) );
        simProjects.addAll( Arrays.asList( FlexSimulationProject.getFlexProjects( trunk ) ) );
        simProjects.addAll( Arrays.asList( JavaSimulationProject.getJavaSimulations( trunk ) ) );

        List<MissingStringEntry> missingStringEntries = new LinkedList<MissingStringEntry>();
        Set<Locale> usedLocales = new HashSet<Locale>();

        // sort them alphabetically, for convenience
        Collections.sort( simProjects, new Comparator<PhetProject>() {
            public int compare( PhetProject a, PhetProject b ) {
                return a.getName().compareTo( b.getName() );
            }
        } );

        for ( PhetProject project : simProjects ) {
            //System.out.println( "project: " + project.getName() );

            // grab the English translation
            Translation english = new Translation( project.getTranslationFile( Locale.ENGLISH ), trunk );
            if ( !english.getFile().exists() ) {
                System.out.println( "skipping " + project.getName() + " due to missing English translation." );
                continue;
            }

            // get the English string keys
            Set<String> englishStrings = english.getTranslationKeys();

            for ( Locale locale : project.getLocales() ) {
                if ( locale.equals( Locale.ENGLISH ) ) {
                    continue;
                }

                usedLocales.add( locale );

                // copy our string key set. we will subtract from this
                Set<String> missingKeys = new HashSet<String>( englishStrings );

                // strings found in a translation that are not used
                Set<String> unusedKeys = new HashSet<String>();

                Translation translation = new Translation( project.getTranslationFile( locale ), trunk );

                if ( !translation.getFile().exists() ) {
                    System.out.println( "translation not found: " + LocaleUtils.localeToString( locale ) );
                }

                for ( String key : translation.getTranslationKeys() ) {
                    // if we don't have a record, it is unused or duplicate
                    if ( !missingKeys.contains( key ) && !IGNORE_KEYS.contains( key ) ) {
                        unusedKeys.add( key );
                    }

                    // otherwise remove it
                    missingKeys.remove( key );
                }

                if ( !missingKeys.isEmpty() ) {
                    missingStringEntries.add( new MissingStringEntry( project, locale, missingKeys ) );
                    //System.out.println( project.getName() + " " + LocaleUtils.localeToString( locale ) + " missing keys: " + join( missingKeys ) );
                }
            }
        }
        StringBuilder builder = new StringBuilder();

        List<String> projectNames = new LinkedList<String>() {{
            for ( PhetProject project : simProjects ) {
                add( project.getName() );
            }
        }};

        builder.append( "<html><head><title>Missing Translation String Report</title></head><body>" );

        /*---------------------------------------------------------------------------*
        * sorted by project
        *----------------------------------------------------------------------------*/

        builder.append( "<h1>Sorted by Project</h1>" );
        builder.append( "<ul>" );
        for ( String projectName : projectNames ) {
            builder.append( "<li><h2>" + encode( projectName ) + "</h2>" );
            builder.append( "<ul>" );
            boolean dirty = false;
            for ( MissingStringEntry entry : missingStringEntries ) {
                if ( entry.project.getName().equals( projectName ) ) {
                    builder.append( "<li><strong>" + entry.locale + "</strong> missing keys: " + join( entry.strings ) + "</li>" );
                    dirty = true;
                }
            }
            if ( !dirty ) {
                builder.append( "<li>All OK</li>" );
            }
            builder.append( "</ul>" );
            builder.append( "</li>" );
        }
        builder.append( "</ul>" );

        /*---------------------------------------------------------------------------*
        * sorted by locale
        *----------------------------------------------------------------------------*/

        builder.append( "<h1>Sorted by Locale</h1>" );
        builder.append( "<ul>" );
        List<Locale> listLocales = new LinkedList<Locale>( usedLocales );
        Collections.sort( listLocales, new Comparator<Locale>() {
            public int compare( Locale a, Locale b ) {
                return LocaleUtils.localeToString( a ).compareTo( LocaleUtils.localeToString( b ) );
            }
        } );
        for ( Locale locale : listLocales ) {
            builder.append( "<li><h2>" + encode( LocaleUtils.localeToString( locale ) ) + "</h2>" );
            builder.append( "<ul>" );
            boolean dirty = false;
            for ( MissingStringEntry entry : missingStringEntries ) {
                if ( entry.locale.equals( locale ) ) {
                    builder.append( "<li><strong>" + entry.project.getName() + "</strong> missing keys: " + join( entry.strings ) + "</li>" );
                    dirty = true;
                }
            }
            if ( !dirty ) {
                builder.append( "<li>All OK</li>" );
            }
            builder.append( "</ul>" );
            builder.append( "</li>" );
        }
        builder.append( "</ul>" );

        builder.append( "</body></html>" );

        try {
            edu.colorado.phet.common.phetcommon.util.FileUtils.writeString( new File( trunk, "build-tools/deploy/missing-translation-string-report.html" ), builder.toString() );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Encodes a string into an HTML-escaped version
     * TODO: duplicated in website and other areas?
     *
     * @param s String to encode
     * @return HTML encoded response
     */
    private static String encode( String s ) {
        if ( s == null ) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        int len = s.length();

        for ( int i = 0; i < len; i++ ) {
            char c = s.charAt( i );
            switch( c ) {
                case '&':
                    buf.append( "&amp;" );
                    break;
                case '<':
                    buf.append( "&lt;" );
                    break;
                case '>':
                    buf.append( "&gt;" );
                    break;
                case '"':
                    buf.append( "&quot;" );
                    break;
                case '\'':
                    buf.append( "&apos;" );
                    break;
                default:
                    buf.append( c );
            }
        }
        return buf.toString();
    }

    private static String join( Set<String> strings ) {
        // make a list and sort it
        List<String> stringList = new LinkedList<String>( strings );
        Collections.sort( stringList );

        String ret = "";
        for ( String key : stringList ) {
            if ( ret.length() > 0 ) {
                ret += ", ";
            }
            ret += key;
        }
        return ret;
    }

    public static class MissingStringEntry {
        public final PhetProject project;
        public final Locale locale;
        public final Set<String> strings;

        public MissingStringEntry( PhetProject project, Locale locale, Set<String> strings ) {
            this.project = project;
            this.locale = locale;
            this.strings = strings;
        }
    }
}
