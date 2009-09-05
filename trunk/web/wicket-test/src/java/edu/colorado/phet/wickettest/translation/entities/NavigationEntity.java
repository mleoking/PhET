package edu.colorado.phet.wickettest.translation.entities;

import java.util.HashSet;

import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.menu.NavLocation;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.panels.SideNavMenu;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class NavigationEntity extends TranslationEntity {
    public NavigationEntity() {
        addString( "nav.home" );
        addString( "nav.simulations" );
        addString( "nav.featured" );
        addString( "nav.new" );
        addString( "nav.physics" );
        addString( "nav.motion" );
        addString( "nav.sound-and-waves" );
        addString( "nav.work-energy-and-power" );
        addString( "nav.heat-and-thermodynamics" );
        addString( "nav.quantum-phenomena" );
        addString( "nav.light-and-radiation" );
        addString( "nav.electricity-magnets-and-circuits" );
        addString( "nav.biology" );
        addString( "nav.chemistry" );
        addString( "nav.earth-science" );
        addString( "nav.math" );
        addString( "nav.tools" );
        addString( "nav.applications" );
        addString( "nav.cutting-edge-research" );
        addString( "nav.all" );
        addString( "nav.simulations.translated" );
        addString( "nav.simulations.by-keyword" );
        addString( "nav.workshops" );
        addString( "nav.get-phet" );
        addString( "nav.get-phet.on-line" );
        addString( "nav.get-phet.full-install" );
        addString( "nav.get-phet.one-at-a-time" );
        addString( "nav.troubleshooting.main" );
        addString( "nav.troubleshooting.java" );
        addString( "nav.troubleshooting.flash" );
        addString( "nav.troubleshooting.javascript" );
        addString( "nav.contribute" );
        addString( "nav.research" );
        addString( "nav.about" );
        addString( "nav.about.source-code" );
        addString( "nav.about.contact" );
        addString( "nav.about.who-we-are" );
        addString( "nav.about.licensing" );
        addString( "nav.sponsors" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                HashSet<NavLocation> locations = new HashSet<NavLocation>();
                locations.add( ( (WicketApplication) requestCycle.getApplication() ).getMenu().getLocationByKey( "motion" ) );
                locations.add( ( (WicketApplication) requestCycle.getApplication() ).getMenu().getLocationByKey( "tools" ) );
                locations.add( ( (WicketApplication) requestCycle.getApplication() ).getMenu().getLocationByKey( "get-phet.full-install" ) );
                locations.add( ( (WicketApplication) requestCycle.getApplication() ).getMenu().getLocationByKey( "troubleshooting.java" ) );
                locations.add( ( (WicketApplication) requestCycle.getApplication() ).getMenu().getLocationByKey( "about.licensing" ) );
                return new SideNavMenu( id, context, locations );
            }
        }, "Navigation Menu" );
    }

    public String getDisplayName() {
        return "Navigation";
    }
}
