package edu.colorado.phet.reids.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jun 1, 2010
 * Time: 2:06:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class MonthlyReportFilter {
    private ArrayList<String> sims;
    private ArrayList<String> allList;

    public MonthlyReportFilter() {
        ArrayList<String> categories = getPredefinedCategories();

        sims = new ArrayList<String>();
        File[] simRoots = new File[] { new File( "C:\\workingcopy\\phet\\trunk\\simulations-java\\simulations" ), new File( "C:\\workingcopy\\phet\\trunk\\simulations-flash\\simulations" ), new File( "C:\\workingcopy\\phet\\trunk\\simulations-flex\\simulations" ) };
        for ( File simRoot : simRoots ) {
            for ( File dir : simRoot.listFiles() ) {
                sims.add( dir.getName() );
            }
        }
        allList = new ArrayList<String>();
        allList.addAll( categories );
        allList.addAll( sims );
        allList.add( "molecules-and-light" );
        allList.add( "isotopes-and-atomic-mass" );
        allList.add( "balloons-and-static-electricity" );
        allList.add( "solutions" );
        allList.add( "licensing" );
        allList.add( "leave:sick:family" );
        allList.add( "leave:sick:personal" );
        allList.add( "leave:vacation" );
    }

    public ArrayList<String> getAllCategories() {
        return removeDuplicates( allList );//Have to remove duplicates or there can be overcounting
    }

    private ArrayList<String> removeDuplicates( ArrayList<String> allList ) {
        ArrayList<String> x = new ArrayList<String>();
        for ( String s : allList ) {
            if ( !x.contains( s ) ) { x.add( s ); }
        }
        return x;
    }

    public static ArrayList<String> getPredefinedCategories() {
        String categorieString =
//                "Accessibility\n" +
                "Administrative/Documentation\n" +
                "Build Process\n" +
                "Common Code\n" +
                "Conferences, Workshops and Booths\n" +
//                "Email\n" +
                "Customer Support\n" +
                "Environment/Maintenance/Tools\n" +
//                "Flash Future\n" +
//                "Flash_Flash Common\n" +
                "Installer\n" +
                "Interviewing\n" +
                "KSU Translation Credits\n" +
                "Licensing\n" +
//                "Look & Feel\n" +
//                "Meetings\n" +
                "Miscellaneous\n" +
                "New Sim Investigations\n" +
                "OVSD\n" +
//                "PhET Basic\n" +
//                "PhET Help\n" +
//                "Common Code\n" +
//                "Piccolo\n" +
//                "Respository, SVN, CVS\n" +
                "Research, Surveys, Interviews & Observations\n" +
//                "Scala\n" +
                "Sim-sharing\n" +
//                "Statistics Database\n" +
//                "Testing & IOM\n" +
                "Translations\n" +
                "Unfuddle\n" +
//                "Sim Updates\n" +
                "Website";

        ArrayList<String> categories = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer( categorieString, "\n" );
        while ( st.hasMoreElements() ) {
            categories.add( st.nextToken() );
        }
        return categories;
    }

    public TimesheetModel filter( TimesheetModel selection ) {
        ArrayList<Entry> mapped = new ArrayList<Entry>();
        for ( int i = 0; i < selection.getEntryCount(); i++ ) {
            mapped.add( map( selection.getEntry( i ) ) );
        }
        TimesheetModel timesheetModel = new TimesheetModel();
        for ( Entry entry : mapped ) {
            timesheetModel.addEntry( entry );
        }
        return timesheetModel;
    }

    private Entry map( Entry entry ) {
        return new Entry( entry.getStartSeconds(), entry.getEndSeconds(), mapCategory( entry.getCategory() ), entry.getNotes(), entry.isReport(), entry.isRunning() );
    }

    private String mapCategory( String category ) {
        //if it appears in the list and only differs by casing, whitespace or hyphenation, then use it
        for ( String elm : allList ) {
            if ( matchesCategory( category, elm ) ) { return elm; }
        }

        //didn't match
        if ( category.equalsIgnoreCase( "administrative" ) ) { return mapCategory( "Administrative/Documentation" ); }
        if ( category.equalsIgnoreCase( "documentation" ) ) { return mapCategory( "Administrative/Documentation" ); }
        if ( category.equals( "cck" ) ) { return mapCategory( "circuit-construction-kit" ); }
        if ( category.equals( "timesheet" ) ) { return mapCategory( "administrative" ); }
        if ( category.equals( "mailing lists" ) ) { return mapCategory( "administrative" ); }
        if ( category.equals( "mailing-lists" ) ) { return mapCategory( "administrative" ); }
        if ( category.equals( "phet-meeting" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "phet meeting" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "misc" ) ) { return mapCategory( "miscellaneous" ); }
        if ( category.equals( "record-and-playback" ) ) { return mapCategory( "common code" ); }
        if ( category.equals( "unfuddle-notifier" ) ) { return mapCategory( "unfuddle" ); }
        if ( category.equals( "botany" ) ) { return mapCategory( "misc" ); }
        if ( category.equals( "phetcommon" ) ) { return mapCategory( "common code" ); }
        if ( category.equals( "maintenance" ) ) { return mapCategory( "misc" ); }
        if ( category.equals( "ide" ) ) { return mapCategory( "maintenance" ); }
        if ( category.equals( "logging" ) ) { return mapCategory( "phetcommon" ); }
        if ( category.equals( "translation-utility" ) ) { return mapCategory( "Translations" ); }
        if ( category.equals( "android" ) ) { return mapCategory( "Miscellaneous" ); }
        if ( category.equals( "wicket-deploy" ) ) { return mapCategory( "website" ); }
        if ( category.equals( "wicket-site" ) ) { return mapCategory( "website" ); }
        if ( category.equals( "support" ) ) { return mapCategory( "phet help" ); }
        if ( category.equals( "git" ) ) { return mapCategory( "Respository, SVN, CVS" ); }
        if ( category.equals( "sbt" ) ) { return mapCategory( "build process" ); }
        if ( category.equals( "newsletter" ) ) { return mapCategory( "website" ); }
        if ( category.equals( "conference" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "flashcommon" ) ) { return mapCategory( "Flash_Flash Common" ); }
        if ( category.equals( "flash-common" ) ) { return mapCategory( "Flash_Flash Common" ); }
        if ( category.equals( "mobile" ) ) { return mapCategory( "Miscellaneous" ); }
        if ( category.equals( "build-tools" ) ) { return mapCategory( "Build Process" ); }
        if ( category.equals( "meeting" ) ) { return mapCategory( "meetings" ); }
        if ( category.equals( "buoyancy" ) ) { return mapCategory( "density-and-buoyancy" ); }
        if ( category.equals( "simsharing" ) ) { return mapCategory( "Sim-sharing" ); }
        if ( category.equals( "blog" ) ) { return mapCategory( "Website" ); }
        if ( category.equals( "sun-and-planet" ) ) { return mapCategory( "gravity-and-orbits" ); }
        if ( category.equals( "design-patterns" ) ) { return mapCategory( "Common Code" ); }
        if ( category.equals( "email" ) ) { return mapCategory( "Administrative/Documentation" ); }
        if ( category.equals( "meetings" ) ) { return mapCategory( "Administrative/Documentation" ); }
        if ( category.equals( "ksu-translation-credits" ) ) { return mapCategory( "KSU Translation Credits" ); }
        if ( category.equals( "piccolo" ) ) { return mapCategory( "piccolo-phet" ); }
        if ( category.equals( "piccolo-phet" ) ) { return mapCategory( "Common Code" ); }
        if ( category.equals( "isotopes" ) ) { return mapCategory( "isotopes-and-atomic-mass" ); }
        if ( category.equals( "phet-help" ) ) { return mapCategory( "Customer Support" ); }
        if ( category.equals( "phethelp" ) ) { return mapCategory( "Customer Support" ); }
        if ( category.equals( "ksu-credits" ) ) { return mapCategory( "KSU Translation Credits" ); }

        System.out.println( "No match found for the category: " + category );
        return "unknown: " + category;
    }

    private boolean matchesCategory( String documented, String standardized ) {
        String a = documented.replaceAll( " ", "" ).replaceAll( "-", "" ).trim();
        String b = standardized.replaceAll( " ", "" ).replaceAll( "-", "" ).trim();
        return a.equalsIgnoreCase( b );
    }
}
