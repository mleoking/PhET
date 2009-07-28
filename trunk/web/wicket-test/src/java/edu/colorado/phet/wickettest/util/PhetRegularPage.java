package edu.colorado.phet.wickettest.util;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.panels.SideNavMenu;

public abstract class PhetRegularPage extends PhetPage {
    public PhetRegularPage( PageParameters parameters ) {
        super( parameters, true );

        // TODO: add in RTL support? not the easiest thing in CSS for swapping panel layouts like this (implement with a LTR or RTL panel option that specifies either float or direction

        add( new SideNavMenu( "side-navigation", getPageContext() ) );
    }
}
