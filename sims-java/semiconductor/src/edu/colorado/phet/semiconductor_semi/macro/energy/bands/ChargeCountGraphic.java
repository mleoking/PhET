/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor_semi.macro.energy.bands;

import edu.colorado.phet.common_semiconductor.math.PhetVector;
import edu.colorado.phet.common_semiconductor.view.graphics.Graphic;
import edu.colorado.phet.common_semiconductor.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_semiconductor.view.util.SimStrings;
import edu.colorado.phet.semiconductor_semi.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor_semi.util.RectangleUtils;

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
    boolean visible = false;

    public ChargeCountGraphic( EnergySection es, BandSetGraphic bsg, ModelViewTransform2D transform ) {
        this.es = es;
        this.bsg = bsg;
        this.bandSet = bsg.bandSet;
        this.transform = transform;
    }

    public void paint( Graphics2D g ) {
        if( visible ) {
            //TODO fix charge count graphic.
            PhetVector ctr = RectangleUtils.getCenter( bsg.getViewport() );
            Point pt = transform.modelToView( ctr );
            g.setFont( font );
            g.setColor( color );
            String str = SimStrings.get( "ChargeCountGraphic.NetChargeLabel" ) + "=" + es.getExcessCharge( bandSet );
            int width = (int)font.getStringBounds( str, g.getFontRenderContext() ).getWidth();
            g.drawString( str, pt.x - width / 2, pt.y );
        }
    }
}
