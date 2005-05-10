/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.theramp.model.Ramp;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.text.DecimalFormat;

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
    private PhetShadowTextGraphic heightReadoutGraphic;
    private AngleGraphic angleGraphic;


    public RampGraphic( RampPanel rampPanel, final Ramp ramp ) {
        super( rampPanel );
        this.rampPanel = rampPanel;
        this.ramp = ramp;
        screenTransform = new ModelViewTransform2D( new Rectangle2D.Double( -10, 0, 20, 10 ), new Rectangle( -50, -50, 800, 400 ) );

        Stroke stroke = new BasicStroke( 6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
//        Stroke surfaceStroke = new BasicStroke( surfaceStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        surfaceGraphic = new PhetImageGraphic( rampPanel, "images/wood2.jpg" );
        surfaceGraphic = new PhetImageGraphic( rampPanel, "images/wood5.png" );
//        surfaceGraphic = new PhetImageGraphic( rampPanel, "images/wood3.png" );
        floorGraphic = new PhetShapeGraphic( getComponent(), null, stroke, Color.black );
        jackGraphic = new PhetShapeGraphic( getComponent(), null, stroke, Color.blue );
        filledShapeGraphic = new PhetShapeGraphic( getComponent(), null, Color.lightGray );
        addGraphic( filledShapeGraphic );
        filledShapeGraphic.setVisible( false );
        addGraphic( floorGraphic );
        addGraphic( jackGraphic );
        addGraphic( surfaceGraphic );

        heightReadoutGraphic = new PhetShadowTextGraphic( rampPanel, new Font( "Lucida Sans", 0, 14 ), "h=0.0 m", Color.black, 1, 1, Color.gray );
        addGraphic( heightReadoutGraphic );

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

        angleGraphic = new AngleGraphic( this );
        addGraphic( angleGraphic, 15 );


        updateRamp();
        ramp.addObserver( new SimpleObserver() {
            public void update() {
                updateRamp();
            }
        } );

    }

    private Point getViewOrigin() {
        Point2D modelOrigin = ramp.getOrigin();
        final Point viewOrigin = screenTransform.modelToView( modelOrigin );
        return viewOrigin;
    }

    private void updateRamp() {

        Point viewOrigin = getViewOrigin();
        Point2D modelDst = ramp.getEndPoint();
        Point viewDst = screenTransform.modelToView( modelDst );
        viewAngle = Math.atan2( viewDst.y - viewOrigin.y, viewDst.x - viewOrigin.x );

//        Line2D.Double origSurface = new Line2D.Double( viewOrigin, viewDst );
//        double origLength = new Vector2D.Double( origSurface.getP1(), origSurface.getP2() ).getMagnitude();
//        Line2D line = RampUtil.getInstanceForLength( origSurface, origLength * 4 );
//        surfaceGraphic.setShape( line );
        surfaceGraphic.setAutorepaint( false );
        surfaceGraphic.setLocation( getViewOrigin() );
        surfaceGraphic.setTransform( new AffineTransform() );
        surfaceGraphic.rotate( viewAngle );
//        double rampLength = 10;//meters
//        ramp.getLocation( 10);
//        getViewLocation( ramp.getLocation( rampLength ) );

        //todo scale the graphic to fit the length.
        double cur_im_width_model = screenTransform.viewToModelDifferentialX( surfaceGraphic.getImage().getWidth() );

        surfaceGraphic.scale( ramp.getLength() / cur_im_width_model );
//        surfaceGraphic.setAutorepaint( true );
        surfaceGraphic.repaint();

        Point p2 = new Point( viewDst.x, viewOrigin.y );
        Line2D.Double floor = new Line2D.Double( viewOrigin, p2 );
        floorGraphic.setShape( null );

        GeneralPath jackShape = createJackShape();
        jackGraphic.setShape( jackShape );

        DoubleGeneralPath path = new DoubleGeneralPath( viewOrigin );
        path.lineTo( floor.getP2() );
        path.lineTo( viewDst );
        path.closePath();
        filledShapeGraphic.setShape( path.getGeneralPath() );

        heightReadoutGraphic.setLocation( jackShape.getBounds().x + 5, jackShape.getBounds().y );
        double height = ramp.getHeight();
        String heightStr = new DecimalFormat( "0.0" ).format( height );
        heightReadoutGraphic.setText( "h=" + heightStr + " m" );

        rampTickSetGraphic.update();
        angleGraphic.update();
    }

    GeneralPath createJackShape() {
        Point rampStart = getViewLocation( ramp.getLocation( 0 ) );
        Point rampEnd = getViewLocation( ramp.getLocation( ramp.getLength() ) );

        DoubleGeneralPath path = new DoubleGeneralPath( new Point( rampEnd.x, rampStart.y ) );
        path.lineTo( new Point( rampEnd.x, rampEnd.y ) );
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

    public Point getViewLocation( double rampDist ) {
        return getViewLocation( ramp.getLocation( rampDist ) );
    }

    /**
     * Create the AffineTransform that will put an object of size: dim centered along the ramp at position dist
     */
    public AffineTransform createTransform( double dist, Dimension dim ) {
        Point viewLoc = getViewLocation( ramp.getLocation( dist ) );
        AffineTransform transform = new AffineTransform();
        transform.translate( viewLoc.x, viewLoc.y );
//        transform.rotate( getViewAngle(), dim.width / 2, dim.height / 2 );
        transform.rotate( getViewAngle() );
//        transform.translate( 0, -dim.height );
        int onRamp = 7;
        transform.translate( -dim.width / 2, -dim.height + onRamp );
//        transform.translate( 0, -dim.height + onRamp );
        return transform;
    }

    public int getImageHeight() {
        return surfaceGraphic.getImage().getHeight();
    }
}
