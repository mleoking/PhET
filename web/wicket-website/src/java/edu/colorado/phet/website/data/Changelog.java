package edu.colorado.phet.website.data;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.PhetWicketApplication;

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

    /*---------------------------------------------------------------------------*
    * constructors
    *----------------------------------------------------------------------------*/

    /**
     * Parse the changelog from the log file
     *
     * @param logFile The 'changes.txt' changelog file
     */
    public Changelog( File logFile ) {
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

    /*---------------------------------------------------------------------------*
    * public functions
    *----------------------------------------------------------------------------*/

    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    /**
     * NOT guaranteed to be in the same format (may leave off dev versions, add spaces after >'s, etc)
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for ( Entry entry : entries ) {
            builder.append( entry.toString() ).append( "\n" );
        }
        return builder.toString();
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
            if ( entry.getDevVersion() == null || entry.getDevVersion() == 0 ) {
                lastEntry = entry.cloneWithoutLines();
                log.getEntries().add( lastEntry );
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

    /*---------------------------------------------------------------------------*
    * implementation
    *----------------------------------------------------------------------------*/

    private void parseChangelog( BufferedReader reader ) throws IOException {
        String line;
        StringBuilder lines = new StringBuilder();

        while ( ( line = reader.readLine() ) != null ) {
            if ( line.startsWith( "# " ) && lines.length() > 0 ) {
                entries.add( new Entry( lines.toString() ) );
                lines = new StringBuilder();
            }
            lines.append( line ).append( "\n" );
        }

        if ( lines.length() > 0 ) {
            entries.add( new Entry( lines.toString() ) );
        }
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

        /*---------------------------------------------------------------------------*
        * constructors
        *----------------------------------------------------------------------------*/

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

        /*---------------------------------------------------------------------------*
        * public methods
        *----------------------------------------------------------------------------*/

        /**
         * Header string.
         * <p/>
         * NOTE: shouldn't contain XML entity characters
         *
         * @param locale The locale in which to print dates
         * @return A string representing the header of this entry
         */
        public String headerString( Locale locale ) {
            StringBuilder builder = new StringBuilder();

            appendVersion( builder );

            if ( date != null ) {
                DateFormat format = DateFormat.getDateInstance( DateFormat.SHORT, locale );
                if ( locale.equals( PhetWicketApplication.getDefaultLocale() ) ) {
                    format = FORMAT_VERSION_TIMESTAMP;
                }

                builder.append( " (" );
                builder.append( format.format( date ) );
                builder.append( ")" );
            }

            return builder.toString();
        }

        @Override
        /**
         * NOT guaranteed to be the same format. Useful for debugging
         */
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append( "# " );
            appendVersion( builder );
            if ( date != null ) {
                builder.append( " " );
                builder.append( FORMAT_VERSION_TIMESTAMP.format( date ) );
            }
            builder.append( "\n" );
            for ( Line line : lines ) {
                builder.append( line.toString() ).append( "\n" );
            }
            return builder.toString();
        }

        /*---------------------------------------------------------------------------*
        * public getters
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

        /*---------------------------------------------------------------------------*
        * implementation
        *----------------------------------------------------------------------------*/

        private void appendVersion( StringBuilder builder ) {
            if ( majorVersion != null ) {
                builder.append( FORMAT_VERSION_MAJOR.format( majorVersion ) );
                if ( minorVersion != null ) {
                    builder.append( "." ).append( FORMAT_VERSION_MINOR.format( minorVersion ) );
                    if ( devVersion != null && devVersion != 0 ) {
                        builder.append( "." ).append( FORMAT_VERSION_DEV.format( devVersion ) );
                        if ( revision != null ) {
                            builder.append( " (" ).append( revision ).append( ")" );
                        }
                    }
                }
            }
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

    }

    /**
     * Represents a line entry (not starting with #) in the changelog.
     */
    public static class Line {
        /**
         * The date when this line was added
         */
        private Date date;

        /**
         * Whether this line is visible. Lines are visible if they start with a ">".
         */
        private boolean visible = false;

        /**
         * The message of this line
         */
        private String message;

        /*---------------------------------------------------------------------------*
        * constructors
        *----------------------------------------------------------------------------*/

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
                }
            }

            // discard the ">" if it exists
            if ( token.startsWith( ">" ) ) {
                visible = true;

                // chop it off in case there is no space before the message
                token = token.substring( 1 );

                // but if there is a space before the next message, grab the next token
                if ( token.trim().length() == 0 && tokenizer.hasMoreTokens() ) {
                    token = tokenizer.nextToken();
                }
            }

            StringBuilder builder = new StringBuilder();
            builder.append( token );
            while ( tokenizer.hasMoreTokens() ) {
                builder.append( " " ).append( tokenizer.nextToken() );
            }

            message = builder.toString();
        }

        public Line( Date date, boolean visible, String message ) {
            this.date = date;
            this.visible = visible;
            this.message = message;
        }

        /*---------------------------------------------------------------------------*
        * public methods
        *----------------------------------------------------------------------------*/

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if ( date != null ) {
                builder.append( FORMAT_ENTRY_STAMP.format( date ) ).append( " " );
            }
            if ( visible ) {
                builder.append( "> " );
            }
            if ( message != null ) {
                builder.append( message );
            }
            return builder.toString();
        }

        /*---------------------------------------------------------------------------*
        * public getters
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
