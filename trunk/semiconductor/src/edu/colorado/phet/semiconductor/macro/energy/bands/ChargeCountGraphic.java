/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.util.RectangleUtils;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 17, 2004
 * Time: 10:23:00 AM
 * Copyright (c) Mar 17, 2004 by Sam Reid
 */
public class ChargeCountGraphic implements Graphic {
    private EnergySection es;
    private BandSet bandSet;
    private ModelViewTransform2D transform;
    private Font font = new Font( "dialog", Font.BOLD, 20 );
    Color color = Color.black;
    BandSetGraphic bsg;

    public ChargeCountGraphic( EnergySection es, BandSetGraphic bsg, ModelViewTransform2D transform ) {
        this.es = es;
        this.bsg = bsg;
        this.bandSet = bsg.bandSet;
        this.transform = transform;
    }

    public void paint( Graphics2D g ) {
        //TODO fix charge count graphic.
        PhetVector ctr = RectangleUtils.getCenter( bsg.getViewport() );
        Point pt = transform.modelToView( ctr );
        g.setFont( font );
        g.setColor( color );
        String str = "Net Charge=" + es.getExcessCharge( bandSet );
        int width = (int)font.getStringBounds( str, g.getFontRenderContext() ).getWidth();
        g.drawString( str, pt.x - width / 2, pt.y );
    }
}
