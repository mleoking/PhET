// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.semiconductor.macro.energyprobe;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;


/**
 * User: Sam Reid
 * Date: Feb 2, 2004
 * Time: 8:26:16 PM
 */
public class LeadGraphic extends TransformGraphic implements SimpleObserver {
    Lead lead;
    private BufferedImage image;
    SimpleBufferedImageGraphic graphic;

    public LeadGraphic( ModelViewTransform2D transform, Lead lead, BufferedImage image ) {
        super( transform );
        this.lead = lead;
        this.image = image;

        graphic = new SimpleBufferedImageGraphic( image );
        lead.addObserver( this );
        update();
    }

    public void update() {
        MutableVector2D tip = lead.getTipLocation();
        Point tipView = super.getTransform().modelToView( tip );
        Point center = new Point( tipView.x, image.getHeight() / 2 + tipView.y );
        graphic.setPosition( center );
    }

    public void paint( Graphics2D graphics2D ) {
        graphic.paint( graphics2D );
    }

    public Point getTail() {
        Shape sh = graphic.getShape();
        Rectangle r = sh.getBounds();
        return new Point( r.x + r.width / 2, r.y + r.height );
    }

}
