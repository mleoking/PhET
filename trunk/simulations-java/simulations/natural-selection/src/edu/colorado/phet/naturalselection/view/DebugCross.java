package edu.colorado.phet.naturalselection.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.nodes.PPath;

public class DebugCross extends PPath {
    public DebugCross() {
        setStroke( new BasicStroke( 5 ) );
        setStrokePaint( Color.RED );

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, -50 );
        path.lineTo( 0, 50 );
        path.moveTo( -50, 0 );
        path.lineTo( 50, 0 );
        setPathTo( path.getGeneralPath() );
    }
}
