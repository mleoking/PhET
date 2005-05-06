/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetgraphics.BufferedPhetGraphic2;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.theramp.common.LocationDebugGraphic;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.RampModel;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:55:39 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class BlockGraphic extends CompositePhetGraphic {
    private RampPanel rampPanel;
    private RampGraphic rampGraphic;
    private Block block;
    private PhetImageGraphic graphic;
    private ThresholdedDragAdapter mouseListener;
    private double forceLengthScale = 5;
    private LocationDebugGraphic locationDebugGraphic;

    public BlockGraphic( RampPanel rampPanel, RampGraphic rampGraphic, Block block ) {
        super( rampPanel );
        this.rampPanel = rampPanel;
        this.rampGraphic = rampGraphic;
        this.block = block;

        final RampModel model = rampPanel.getRampModule().getRampModel();

        int blockHeight = 30;
        Paint color = new GradientPaint( 0, 0, Color.blue, blockHeight + blockHeight / 4, blockHeight + blockHeight / 4, Color.yellow );
        Stroke stroke = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        Paint border = Color.black;
        PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( rampPanel, new Rectangle( 0, 0, blockHeight, blockHeight ), color, stroke, border );
        graphic = BufferedPhetGraphic2.createBuffer( shapeGraphic, new BasicGraphicsSetup(), BufferedImage.TYPE_INT_ARGB, new Color( 255, 255, 255, 0 ) );
        addGraphic( graphic );

        locationDebugGraphic = new LocationDebugGraphic( getComponent(), 10 );
//        addGraphic( locationDebugGraphic );

        updateBlock();
        block.addObserver( new SimpleObserver() {
            public void update() {
                updateBlock();
            }
        } );

        MouseInputAdapter mia = new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                Point ctr = getCenter();
                double dx = e.getPoint().x - ctr.x;
                double appliedForce = dx / forceLengthScale;
                model.setAppliedForce( appliedForce );
            }

            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                model.setAppliedForce( 0.0 );
            }
        };

        this.mouseListener = new ThresholdedDragAdapter( mia, 10, 0, 1000 );
        addMouseInputListener( this.mouseListener );
        setCursorHand();
        rampGraphic.getRamp().addObserver( new SimpleObserver() {
            public void update() {
                updateBlock();
            }
        } );

    }

    Point getCenter() {
//        Rectangle r = graphic.getBounds();
//        return RectangleUtils.getCenter( r );
        Point2D ctr = getNetTransform().transform( new Point2D.Double( graphic.getBounds().getWidth() / 2, graphic.getBounds().getHeight() / 2 ), null );
        return new Point( (int)ctr.getX(), (int)ctr.getY() );
    }


    public void updateBlock() {

        Point viewLoc = rampGraphic.getTransform2D().modelToView( block.getLocation() );
        locationDebugGraphic.setLocation( viewLoc );
//        setLocation( viewLoc );
        AffineTransform transform = new AffineTransform();
        transform.translate( viewLoc.x, viewLoc.y );
        transform.rotate( rampGraphic.getViewAngle(), graphic.getBounds().getWidth() / 2, graphic.getBounds().getHeight() / 2 );
        transform.translate( 0, -graphic.getBounds().getHeight() + 2 );
        setTransform( transform );

        double mass = block.getMass();
        double defaultScale = 0.35;
        double fracSize = mass / 1000.0 / 2.0 + defaultScale;
//        setAutoRepaint( false );
//        setTransform( new AffineTransform() );
//        scale( defaultScale, fracSize );
    }

    public Dimension computeDimension() {
        return new Dimension( getWidth(), getHeight() );
    }

    public void setImage( BufferedImage image ) {
        graphic.setImage( image );
    }
}
