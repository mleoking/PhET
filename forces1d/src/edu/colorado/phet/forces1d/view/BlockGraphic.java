/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.forces1d.common.AffineTransformBuilder;
import edu.colorado.phet.forces1d.model.Block;
import edu.colorado.phet.forces1d.model.Force1DModel;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
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
    private AffineTransformBuilder transformBuilder;
    private Force1dObject force1dObject;

    public BlockGraphic( Force1DPanel panel, final Block block, final Force1DModel model,
                         ModelViewTransform2D transform2D, final Function.LinearFunction transform1d, Force1dObject force1dObject ) {
        super( panel );
        this.panel = panel;
        this.block = block;
        this.model = model;
        this.transform2D = transform2D;
        this.transform1d = transform1d;
        this.force1dObject = force1dObject;

//        graphic = new PhetShapeGraphic( panel );
//        graphic = new PhetImageGraphic( panel, "images/fridge.gif" );
//        graphic = new PhetImageGraphic( panel, "images/ollie.gif" );
//        graphic = new PhetImageGraphic( panel, "images/phetbook.gif" );
        graphic = new PhetImageGraphic( panel, force1dObject.getLocation() );

        transformBuilder = new AffineTransformBuilder( graphic.getBounds() );
//        graphic.transform( AffineTransform.getScaleInstance( .5, .5 ) );
//        Rectangle bounds = graphic.getBounds();
//        Point bc = RectangleUtils.getBottomCenter( bounds );
//        int y = panel.getWalkwayGraphic().getPlatformY();
//        int dy = y - bc.y;
//        graphic.transform( AffineTransform.getTranslateInstance( 0, dy ) );
        addGraphic( graphic );

        update();

        addMouseInputListener( new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                Point ctr = getCenter();
                double dx = e.getPoint().x - ctr.x;
                dx /= ArrowSetGraphic.forceLengthScale;
                model.setAppliedForce( dx );
            }
        } );
        setCursorHand();
    }

    public void setImage( Force1dObject force1dObject ) {
        this.force1dObject = force1dObject;
//        graphic = new PhetImageGraphic( panel, force1dObjectt.getLocation() );
        try {
            graphic.setImage( force1dObject.getImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public Dimension computeDimension() {
        return new Dimension( graphic.getWidth(), graphic.getHeight() );
//        int minWidth = 30;
//        int minHeight = 30;
//        int width = (int)( block.getMass() + minWidth );
//        int height = (int)( block.getMass() + minHeight );
//        return new Dimension( width, height );
    }

    protected Rectangle determineBounds() {
        return super.determineBounds();
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
        transformBuilder.setScale( defaultScale, fracSize );
        Point center = getCenter();
        transformBuilder.setLocation( center.x - graphic.getWidth() / 2, center.y - graphic.getHeight() / 2 );
        graphic.setTransform( transformBuilder.toAffineTransform() );
//        float fracKinetic = (float)( block.getKineticFriction() / Force1dControlPanel.MAX_KINETIC_FRICTION );
//        Color color = new Color( fracKinetic, fracKinetic / 2, .75f );

//        Dimension dim = computeDimension();

//        RoundRectangle2D r = new RoundRect( 0, 0, 0, 0, dim.width * .2, dim.height * .2 );
//        r.setFrameFromCenter( center.x, center.y, center.x + dim.width / 2, center.y + dim.height / 2 );
//        graphic.setShape( r );
//        graphic.setColor( color );

//        graphic.setLocation( center.x - graphic.getWidth() / 2, center.y - graphic.getHeight() / 2 );
//        setLocation( (int)r.getX(),(int)r.getY());
//        System.out.println( "r = " + r );
        setBoundsDirty();//so that the bounds gets recalculated when needed
        notifyChanged();
//        repaint();
//        notifyChanged();//how to notify without knowing whether shape changed or color changed?
    }

    public Point getCenter() {
        Dimension dim = computeDimension();
        int x = (int)transform1d.evaluate( block.getPosition() );
        int y = panel.getWalkwayGraphic().getPlatformY() - dim.height / 2;
        return new Point( x, y );
    }
}
