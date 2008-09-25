package edu.colorado.phet.semiconductor.macro;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;

import edu.colorado.phet.semiconductor.oldphetgraphics.graphics.Graphic;

import edu.colorado.phet.semiconductor.util.RectangleUtils;

/**
 * User: Sam Reid
 * Date: Apr 17, 2004
 * Time: 8:56:23 AM
 */
public class ColumnDebugGraphic implements Graphic {
    EnergySection energySection;
    private ModelViewTransform2D trf;
    private boolean visible = false;

    public ColumnDebugGraphic( EnergySection energySection, ModelViewTransform2D trf ) {
        this.energySection = energySection;
        this.trf = trf;
    }

    Stroke stroke = new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER );
    Font font = new PhetFont( 16 );

    public void paint( Graphics2D g ) {
        if ( visible ) {
            g.setColor( Color.blue );
            g.setFont( font );
            g.setStroke( stroke );
            for ( int i = 0; i < energySection.numColumns(); i++ ) {
                Rectangle2D.Double r = energySection.getColumnRect( i );
                Shape s = trf.createTransformedShape( r );
//            g.draw(s );
                int charge = energySection.getColumnCharge( i );
                Vector2D.Double ctr = RectangleUtils.getCenter( r );
                Point pt = trf.modelToView( ctr );
                g.drawString( "ch=" + charge, pt.x, pt.y );
            }
        }
    }
}
