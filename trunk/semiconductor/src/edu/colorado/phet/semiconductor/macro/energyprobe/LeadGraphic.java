package edu.colorado.phet.semiconductor.macro.energyprobe;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Feb 2, 2004
 * Time: 8:26:16 PM
 * Copyright (c) Feb 2, 2004 by Sam Reid
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

    public Boundary getBoundary() {
        return new Boundary() {
            public boolean contains( int x, int y ) {
                return graphic.contains( x, y );
            }
        };
    }

    public Point getTail() {
        Shape sh = graphic.getShape();
        Rectangle r = sh.getBounds();
        return new Point( r.x + r.width / 2, r.y + r.height );
    }

}
