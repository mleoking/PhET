package edu.colorado.phet.semiconductor_semi.macro.energyprobe;

import edu.colorado.phet.common_semiconductor.math.PhetVector;
import edu.colorado.phet.common_semiconductor.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common_semiconductor.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor_semi.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor_semi.common.TransformGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

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
        PhetVector tip = lead.getTipLocation();
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
