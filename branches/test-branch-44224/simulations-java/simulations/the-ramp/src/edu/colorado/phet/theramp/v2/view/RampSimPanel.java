package edu.colorado.phet.theramp.v2.view;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDebug;

public class RampSimPanel extends PhetPCanvas {
    public RampSimPanel( TestRampModule module ) {
        addScreenChild( new PText( "test" ) );

        PDebug.debugRegionManagement=true;
        RampModelView rampModelView = new RampModelView( module );
        addScreenChild( rampModelView );

    }
}
