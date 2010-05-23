package edu.colorado.phet.website.data;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Represents the content of a single changelog
 */
public class Changelog {

    private List<Entry> entries = new LinkedList<Entry>();

    /**
     * Parse the start of entry lines with this
     */
    private static final SimpleDateFormat FORMAT_ENTRY_STAMP = new SimpleDateFormat( "M/d/yy" ); // eg, 3/2/09

    /**
     * Parse the entry header date with this
     */
    private static final SimpleDateFormat FORMAT_VERSION_TIMESTAMP = new SimpleDateFormat( "MMM d, yyyy" ); // eg, Feb 3, 2009

    /**
     * Parse the entry header date with this if the main format fails. This is used in some changelogs
     */
    private static final SimpleDateFormat FORMAT_VERSION_BACKUP_TIMESTAMP = new SimpleDateFormat( "MM-dd-yyyy" ); // eg, 03-02-2009

    private static final DecimalFormat FORMAT_VERSION_MAJOR = new DecimalFormat( "0" );
    private static final DecimalFormat FORMAT_VERSION_MINOR = new DecimalFormat( "00" );
    private static final DecimalFormat FORMAT_VERSION_DEV = new DecimalFormat( "00" );

    private static final Logger logger = Logger.getLogger( Changelog.class.getName() );

    /**
     * Parse the changelog from the log file
     *
     * @param logFile The 'changes.txt' changelog file
     */
    public Changelog( File logFile ) {
        // TODO: add unit tests
        try {
            BufferedReader reader = new BufferedReader( new FileReader( logFile ) );
            try {
                parseChangelog( reader );
            }
            finally {
                reader.close();
            }
        }
        catch( FileNotFoundException e ) {
            logger.error( e );
        }
        catch( IOException e ) {
            logger.error( e );
        }
    }

    /**
     * Parse the changelog from a string
     *
     * @param str String version of the 'changes.txt' changelog file
     */
    public Changelog( String str ) {
        try {
            BufferedReader reader = new BufferedReader( new StringReader( str ) );
            try {
                parseChangelog( reader );
            }
            finally {
                reader.close();
            }
        }
        catch( IOException e ) {
            logger.error( e );
        }
    }

    private Changelog() {

    }

    private void parseChangelog( BufferedReader reader ) throws IOException {
        String line;
        StringBuilder lines = new StringBuilder();

        while ( ( line = reader.readLine() ) != null ) {
            if ( line.startsWith( "#" ) && lines.length() > 0 ) {
                entries.add( new Entry( lines.toString() ) );
                lines = new StringBuilder();
            }
            lines.append( line ).append( "\n" );
        }

        if ( lines.length() > 0 ) {
            entries.add( new Entry( lines.toString() ) );
        }
    }

    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * @return A changelog where the only entries are non-development entries (with dev version == 0). Only visible
     *         log lines are included, and log lines of dev entries are added to the next non-dev entry.
     */
    public Changelog getNonDevChangelog() {
        Changelog log = new Changelog();

        // last non-development entry
        Entry lastEntry = null;

        for ( Entry entry : entries ) {
            if ( entry.getDevVersion() == 0 ) {
                lastEntry = entry.cloneWithoutLines();
            }
            if ( lastEntry != null ) {
                for ( Line line : entry.getLines() ) {
                    if ( line.isVisible() ) {
                        lastEntry.getLines().add( line );
                    }
                }
            }
        }

        return log;
    }

    /**
     * Represents a header (starts with #) and the lines that follow until the next header
     */
    public static class Entry {
        private Integer majorVersion;
        private Integer minorVersion;
        private Integer devVersion;
        private Integer revision;
        private Date date;
        private List<Line> lines = new LinkedList<Line>();

        public Entry( String str ) {
            StringTokenizer tokenizer = new StringTokenizer( str, "\n" );

            parseHeader( tokenizer.nextToken() );

            while ( tokenizer.hasMoreTokens() ) {
                String token = tokenizer.nextToken();
                if ( token.trim().length() == 0 ) { continue; }
                lines.add( new Line( token ) );
            }
        }

        public Entry( Integer majorVersion, Integer minorVersion, Integer devVersion, Integer revision, Date date, List<Line> lines ) {
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
            this.devVersion = devVersion;
            this.revision = revision;
            this.date = date;
            this.lines = lines;
        }

        private Entry cloneWithoutLines() {
            return new Entry( majorVersion, minorVersion, devVersion, revision, date, new LinkedList<Line>() );
        }

        private void parseHeader( String str ) {
            StringTokenizer tokenizer = new StringTokenizer( str, " " );

            // if this isn't an entry, throw an exception
            if ( !tokenizer.hasMoreTokens() || !tokenizer.nextToken().equals( "#" ) ) {
                throw new RuntimeException( "trying to parse an invalid entry" );
            }

            if ( !tokenizer.hasMoreTokens() ) { return; }
            String token = tokenizer.nextToken();

            // check for version
            if ( token.contains( "." ) ) {
                parseVersion( token );
                if ( !tokenizer.hasMoreTokens() ) { return; }
                token = tokenizer.nextToken();
            }

            // check for revision
            if ( token.startsWith( "(" ) ) {
                parseRevision( token );
                if ( !tokenizer.hasMoreTokens() ) { return; }
                token = tokenizer.nextToken();
            }

            // gather the rest of the line, and parse it as a date
            while ( tokenizer.hasMoreTokens() ) {
                token += " " + tokenizer.nextToken();
            }
            parseDate( token );
        }

        private void parseVersion( String str ) {
            try {
                StringTokenizer tokenizer = new StringTokenizer( str, "." );
                int major, minor;
                if ( !tokenizer.hasMoreTokens() ) { return; }
                major = Integer.parseInt( tokenizer.nextToken() );
                if ( !tokenizer.hasMoreTokens() ) { return; }
                minor = Integer.parseInt( tokenizer.nextToken() );

                // wait until now to initialize, so if the version is not specified all version numbers will be null
                majorVersion = major;
                minorVersion = minor;

                if ( !tokenizer.hasMoreTokens() ) { return; }
                devVersion = Integer.parseInt( tokenizer.nextToken() );
            }
            catch( RuntimeException e ) {
                logger.warn( "changelog failure at parsing version " + str, e );
            }
        }

        private void parseRevision( String str ) {
            try {
                revision = Integer.parseInt( str.replace( "(", "" ).replace( ")", "" ) );
            }
            catch( RuntimeException e ) {
                logger.warn( "changelog failure at parsing revision " + str, e );
            }
        }

        private void parseDate( String str ) {
            try {
                date = FORMAT_VERSION_TIMESTAMP.parse( str );
            }
            catch( ParseException e ) {
                try {
                    date = FORMAT_VERSION_BACKUP_TIMESTAMP.parse( str );
                }
                catch( ParseException e1 ) {
                    e1.printStackTrace();
                    logger.warn( "changelog failure at parsing date " + str );
                }
            }
        }

        /*---------------------------------------------------------------------------*
        * getters
        *----------------------------------------------------------------------------*/

        public Integer getMajorVersion() {
            return majorVersion;
        }

        public Integer getMinorVersion() {
            return minorVersion;
        }

        public Integer getDevVersion() {
            return devVersion;
        }

        public Integer getRevision() {
            return revision;
        }

        public Date getDate() {
            return date;
        }

        public List<Line> getLines() {
            return lines;
        }
    }

    /**
     * Represents a line entry (not starting with #) in the changelog.
     */
    public static class Line {
        private Date date;
        private boolean visible = false;
        private String message;

        public Line( String str ) {
            StringTokenizer tokenizer = new StringTokenizer( str, " " );

            if ( !tokenizer.hasMoreTokens() ) { return; }
            String token = tokenizer.nextToken();

            if ( token.length() > 0 && Character.isDigit( token.charAt( 0 ) ) ) {
                try {
                    date = FORMAT_ENTRY_STAMP.parse( token );

                    if ( !tokenizer.hasMoreTokens() ) { return; }
                    token = tokenizer.nextToken();
                }
                catch( ParseException e ) {
                    e.printStackTrace();
                }
            }

            if ( token.startsWith( ">" ) ) {
                visible = true;
                token = token.substring( 1 );
            }

            StringBuilder builder = new StringBuilder();
            builder.append( token );
            while ( tokenizer.hasMoreTokens() ) {
                builder.append( tokenizer.nextToken() );
            }

            message = builder.toString();
        }

        public Line( Date date, boolean visible, String message ) {
            this.date = date;
            this.visible = visible;
            this.message = message;
        }

        /*---------------------------------------------------------------------------*
        * getters
        *----------------------------------------------------------------------------*/

        public Date getDate() {
            return date;
        }

        public boolean isVisible() {
            return visible;
        }

        public String getMessage() {
            return message;
        }
    }
}
