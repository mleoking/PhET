/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Apr 17, 2004
 * Time: 8:56:23 AM
 * Copyright (c) Apr 17, 2004 by Sam Reid
 */
public class ColumnDebugGraphic implements Graphic{
    EnergySection energySection;
    private ModelViewTransform2D trf;

    public ColumnDebugGraphic( EnergySection energySection ,ModelViewTransform2D trf) {
        this.energySection = energySection;
        this.trf = trf;
    }
    Stroke stroke=new BasicStroke( 4,BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    Font font=new Font( "dialog",0,16);
    public void paint( Graphics2D g ) {
        g.setColor( Color.blue );
        g.setFont( font);
        g.setStroke( stroke);
        for (int i=0;i<energySection.numColumns();i++){
            Rectangle2D.Double r=energySection.getColumnRect( i );
            Shape s=trf.createTransformedShape( r );
//            g.draw(s );
            int charge=energySection.getColumnCharge( i );
            PhetVector ctr=RectangleUtils.getCenter( r );
            Point pt=trf.modelToView( ctr );
            g.drawString( "ch="+charge,pt.x,pt.y);
        }
    }
}
