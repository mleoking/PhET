/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.forces1d.model.Block;
import edu.colorado.phet.forces1d.model.Force1DModel;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 13, 2004
 * Time: 10:27:14 AM
 * Copyright (c) Nov 13, 2004 by Sam Reid
 */
public class BlockGraphic extends CompositePhetGraphic {
    private Block block;
    private Force1DModel model;
    private ModelViewTransform2D transform2D;
    private Function.LinearFunction transform1d;
    private PhetImageGraphic graphic;
    private Force1DPanel panel;
    private Force1dObject force1dObject;
    private Point lastCenter;
    private MouseInputAdapter mouseListener;

    public BlockGraphic( Force1DPanel panel, final Block block, final Force1DModel model,
                         ModelViewTransform2D transform2D, final Function.LinearFunction transform1d, Force1dObject force1dObject ) {
        super( panel );
        this.panel = panel;
        this.block = block;
        this.model = model;
        this.transform2D = transform2D;
        this.transform1d = transform1d;
        this.force1dObject = force1dObject;

        graphic = new PhetImageGraphic( panel, force1dObject.getLocation() );
        addGraphic( graphic );

        update();

        this.mouseListener = new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                Point ctr = getCenter();
                double dx = e.getPoint().x - ctr.x;
                double appliedForce = dx / ArrowSetGraphic.forceLengthScale;
                model.setAppliedForce( appliedForce );
            }
        };
        addMouseInputListener( this.mouseListener );
        setCursorHand();
    }

    public MouseInputAdapter getMouseListener() {
        return mouseListener;
    }

    public void setImage( Force1dObject force1dObject ) {
        this.force1dObject = force1dObject;
        try {
            graphic.setImage( force1dObject.getImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public Dimension computeDimension() {
        return new Dimension( graphic.getWidth(), graphic.getHeight() );
    }

    protected Rectangle determineBounds() {
        return super.determineBounds();
    }

    public Block getBlock() {
        return block;
    }

    //Workaround for lack of equals in java's RoundRectangle2D.
    public static class RoundRect extends RoundRectangle2D.Double {

        public RoundRect( double x, double y, double w, double h, double arcw, double arch ) {
            super( x, y, w, h, arcw, arch );
        }

        public boolean equals( Object obj ) {
            if( obj instanceof RoundRect ) {
                RoundRect roundRect = (RoundRect)obj;
                return roundRect.getX() == x && roundRect.getY() == y && roundRect.getWidth() == getWidth() && roundRect.getHeight() == getHeight() &&
                        roundRect.getArcWidth() == getArcWidth() && roundRect.getArcHeight() == getArcHeight();
            }
            return false;
        }

        public String toString() {
            return "x=" + super.getX() + ", y=" + super.getY() + ", width=" + super.getWidth() + ", height=" + super.getHeight();
        }
    }

    public void update() {
        double mass = block.getMass();
        double defaultScale = 0.35;
        double fracSize = mass / 1000.0 / 2.0 + defaultScale;
        setAutoRepaint( false );
        setTransform( new AffineTransform() );
        scale( defaultScale, fracSize );

        Point center = getCenter();
        setLocation( center.x - graphic.getWidth() / 2, center.y - graphic.getHeight() / 2 );
        setBoundsDirty();//so that the bounds gets recalculated when needed
        setAutoRepaint( true );
        if( lastCenter == null || lastCenter.equals( center ) ) {

        }
        else {
            autorepaint();
            notifyChanged();
        }
        lastCenter = center;
    }

    public Point getCenter() {
        Dimension dim = computeDimension();
        int x = (int)transform1d.evaluate( block.getPosition() );
        int y = panel.getWalkwayGraphic().getFloorY() - dim.height / 2;
        return new Point( x, y );
    }
}
