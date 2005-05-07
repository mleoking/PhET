/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.theramp.model.Ramp;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:17:00 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampGraphic extends GraphicLayerSet {
    private RampPanel rampPanel;
    private Ramp ramp;
    private ModelViewTransform2D screenTransform;
    private double viewAngle;
    private PhetImageGraphic surfaceGraphic;
    private PhetShapeGraphic floorGraphic;
    private PhetShapeGraphic jackGraphic;
    private int surfaceStrokeWidth = 12;
    private PhetShapeGraphic filledShapeGraphic;
    private RampTickSetGraphic rampTickSetGraphic;

    public RampGraphic( RampPanel rampPanel, final Ramp ramp ) {
        super( rampPanel );
        this.rampPanel = rampPanel;
        this.ramp = ramp;
        screenTransform = new ModelViewTransform2D( new Rectangle2D.Double( -10, 0, 20, 10 ), new Rectangle( 800, 400 ) );

        Stroke stroke = new BasicStroke( 6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        Stroke surfaceStroke = new BasicStroke( surfaceStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        surfaceGraphic = new PhetImageGraphic( rampPanel, "images/wood2.jpg" );
        floorGraphic = new PhetShapeGraphic( getComponent(), null, stroke, Color.black );
        jackGraphic = new PhetShapeGraphic( getComponent(), null, stroke, Color.blue );
        filledShapeGraphic = new PhetShapeGraphic( getComponent(), null, Color.lightGray );
        addGraphic( filledShapeGraphic );
        filledShapeGraphic.setVisible( false );
        addGraphic( floorGraphic );
        addGraphic( jackGraphic );
        addGraphic( surfaceGraphic );

        surfaceGraphic.addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                Point pt = e.getPoint();
                Vector2D.Double vec = new Vector2D.Double( getViewOrigin(), pt );
                double angle = -vec.getAngle();
                angle = MathUtil.clamp( 0, angle, Math.PI / 2.0 );
                ramp.setAngle( angle );
            }
        } );
        surfaceGraphic.setCursorHand();

        rampTickSetGraphic = new RampTickSetGraphic( this );
        addGraphic( rampTickSetGraphic, 10 );

        updateRamp();
        ramp.addObserver( new SimpleObserver() {
            public void update() {
                updateRamp();
            }
        } );

        double rampLength = 10;//meters.

    }

    private Point getViewOrigin() {
        Point2D modelOrigin = ramp.getOrigin();
        final Point viewOrigin = screenTransform.modelToView( modelOrigin );
        return viewOrigin;
    }

    private void updateRamp() {
        System.out.println( "RampGraphic.updateRamp" );
        Point viewOrigin = getViewOrigin();
        Point2D modelDst = ramp.getEndPoint();
        Point viewDst = screenTransform.modelToView( modelDst );
        viewAngle = Math.atan2( viewDst.y - viewOrigin.y, viewDst.x - viewOrigin.x );

        Line2D.Double origSurface = new Line2D.Double( viewOrigin, viewDst );
        double origLength = new Vector2D.Double( origSurface.getP1(), origSurface.getP2() ).getMagnitude();
        Line2D line = RampUtil.getInstanceForLength( origSurface, origLength * 4 );
//        surfaceGraphic.setShape( line );
        surfaceGraphic.setAutorepaint( false );
        surfaceGraphic.setLocation( getViewOrigin() );
        surfaceGraphic.setTransform( new AffineTransform() );
        surfaceGraphic.rotate( viewAngle );
//        double rampLength = 10;//meters
//        ramp.getLocation( 10);
//        getViewLocation( ramp.getLocation( rampLength ) );
        surfaceGraphic.scale( 0.8 );
        surfaceGraphic.setAutorepaint( true );
        surfaceGraphic.autorepaint();

        Point p2 = new Point( viewDst.x, viewOrigin.y );
        Line2D.Double floor = new Line2D.Double( viewOrigin, p2 );
        floorGraphic.setShape( floor );

        GeneralPath jackShape = createJackShape( p2, viewDst, 10 );
        jackGraphic.setShape( jackShape );

        DoubleGeneralPath path = new DoubleGeneralPath( viewOrigin );
        path.lineTo( floor.getP2() );
        path.lineTo( viewDst );
        path.closePath();
        filledShapeGraphic.setShape( path.getGeneralPath() );

        rampTickSetGraphic.update();
    }

    GeneralPath createJackShape( Point src, Point dst, int wavelength ) {
        DoubleGeneralPath path = new DoubleGeneralPath( src );
        path.lineTo( dst );
        return path.getGeneralPath();
    }

    public double getViewAngle() {
        return viewAngle;
    }

    public ModelViewTransform2D getScreenTransform() {
        return screenTransform;
    }

    public Ramp getRamp() {
        return ramp;
    }

    public int getSurfaceStrokeWidth() {
        return surfaceStrokeWidth;
    }

    public Point getViewLocation( Point2D location ) {
        Point viewLoc = getScreenTransform().modelToView( location );
        return viewLoc;
    }
}
