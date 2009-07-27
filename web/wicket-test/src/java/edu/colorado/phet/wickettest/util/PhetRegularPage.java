package edu.colorado.phet.wickettest.util;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.panels.SideNavMenu;

public abstract class PhetRegularPage extends PhetPage {
    public PhetRegularPage( PageParameters parameters ) {
        super( parameters, true );

        add( new SideNavMenu( "side-navigation", getPageContext() ) );
    }
}
