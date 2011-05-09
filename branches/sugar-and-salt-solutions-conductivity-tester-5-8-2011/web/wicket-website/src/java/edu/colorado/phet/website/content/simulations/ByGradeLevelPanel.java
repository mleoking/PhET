package edu.colorado.phet.website.content.simulations;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

public class ByGradeLevelPanel extends PhetPanel {

    public ByGradeLevelPanel( String id, final PageContext context ) {
        super( id, context );

        // TODO: localize (alt attributes)

        add( getNavMenu().getLocationByKey( "elementary-school" ).getLink( "elementary-school-link", context, getPhetCycle() ) );
        add( getNavMenu().getLocationByKey( "middle-school" ).getLink( "middle-school-link", context, getPhetCycle() ) );
        add( getNavMenu().getLocationByKey( "high-school" ).getLink( "high-school-link", context, getPhetCycle() ) );
        add( getNavMenu().getLocationByKey( "university" ).getLink( "university-link", context, getPhetCycle() ) );

    }

}