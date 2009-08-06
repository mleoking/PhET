package edu.colorado.phet.wickettest.content;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.LocalizedLabel;
import edu.colorado.phet.wickettest.util.PageContext;

public class TroubleshootingMainPanel extends PhetPanel {
    public TroubleshootingMainPanel( String id, PageContext context ) {
        super( id, context );

        /*
        add( new LocalizedLabel( "about-p1", "about.p1", new String[]{"href=\"http://phet.colorado.edu/simulations/index.php\"", "href=\"http://phet.colorado.edu/research/index.php\""} ) );
        add( new LocalizedLabel( "about-p2", "about.p2" ) );
        add( new LocalizedLabel( "about-p3", "about.p3", new String[]{"href=\"http://phet.colorado.edu/about/legend.php\""} ) );
        add( new LocalizedLabel( "about-p4", "about.p4", new String[]{
                "href=\"http://phet.colorado.edu/index.php\"",
                "href=\"http://phet.colorado.edu/tech_support/support-java.php\"",
                "href=\"http://phet.colorado.edu/tech_support/support-flash.php\""
        } ) );
        */
    }
}