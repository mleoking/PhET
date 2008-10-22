package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 27, 2008 at 12:03:24 PM
 */
public class HealthLevel extends PNode {
    private HealthBar bar;
    private PhetPPath path;

    public HealthLevel( HealthBar bar ) {
        this.bar = bar;
        path = new PhetPPath( createPath(), Color.blue );
        addChild( path );
    }

    public void setValue( double value ) {
        double viewY = bar.getViewY( value );
        setOffset( 0, viewY );
    }

    private GeneralPath createPath() {
        DoubleGeneralPath path = new DoubleGeneralPath();
        double leftX = 0 - 2;
        double pinPoint = 4 - 2;
        double rightX = 20;
        double topY = 3;
        double bottomY = -topY;

        path.moveTo( leftX, 0 );
        path.lineTo( pinPoint, topY );
        path.lineTo( rightX, 0 );
        path.lineTo( pinPoint, bottomY );
        path.lineTo( leftX, 0 );
        return path.getGeneralPath();
    }
}
