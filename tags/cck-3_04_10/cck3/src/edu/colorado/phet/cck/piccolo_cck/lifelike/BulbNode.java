package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.common.RoundGradientPaint;
import edu.colorado.phet.cck.model.components.Bulb;
import edu.colorado.phet.cck.piccolo_cck.PhetPPath;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 1:03:35 AM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class BulbNode extends PhetPNode {
    private Bulb bulb;
    private double intensity = 0.0;
    private ArrayList brighties = new ArrayList();
    private BasicStroke brightyStroke;
    private Color brightyColor;
    private Ellipse2D.Double bulbShape;
    private Point2D pin;
    private Point2D.Double rad;
    private Paint paint;
    private Rectangle2D.Double conductor;
    private Ellipse2D.Double insulator;
    private Rectangle2D.Double tip;
    private ArrayList spiralLines;
    private Stroke tipStroke = new BasicStroke( 2 / 40.0f );
    private Color insulatorColor = Color.white;
    private Stroke baseStroke = new BasicStroke( 1 / 40.0f );
    private Stroke linestroke = new BasicStroke( 1 / 90.0f );
    private boolean showCoverOnly = false;
    private PhetPPath highlightNode = new PhetPPath( new BasicStroke( (float)( 2.0 / 60.0 ) ), Color.yellow );
    private SimpleObserver bulbObserver = new SimpleObserver() {
        public void update() {
            BulbNode.this.update();
        }
    };
    //    private static final float SCALE = 0.75f;
    private static final float SCALE = 0.35f;

    public BulbNode( Bulb bulb ) {
        this.bulb = bulb;

        Rectangle2D.Double bounds = new Rectangle2D.Double( 0, 0, 1, 1 );
        //reserve the top part for the bulb.
        double fracInsulator = .1;
        double fracTip = .04;
        double fracConductor = .23504;
        double fracBulb = 1 - fracInsulator - fracTip - fracConductor;
        double fracConductorWidth = .5;
        double fracTipWidth = .08;
        double fracBulbOverlap = .06;

        double bulbY = bounds.getY();
        double bulbHeight = bounds.getHeight() * fracBulb;
        double bulbWidth = bounds.getWidth();
        double bulbX = bounds.getX();

        double conductorWidth = bounds.getWidth() * fracConductorWidth;
        double conductorX = bounds.getX() + ( bounds.getWidth() - conductorWidth ) / 2;
        double conductorY = bulbHeight + bulbY;
        double conductorHeight = fracConductor * bounds.getHeight();

        double insulatorX = conductorX;
        double insulatorHeight = fracInsulator * bounds.getHeight();
        double insulatorY = conductorY + conductorHeight - insulatorHeight / 2;
        double insulatorWidth = conductorWidth;

        double tipWidth = bounds.getWidth() * fracTipWidth;
        double tipHeight = bounds.getHeight() * fracTip;
        double tipX = bounds.getX() + ( bounds.getWidth() - tipWidth ) / 2;
        double tipY = insulatorY + insulatorHeight;

        bulbShape = new Ellipse2D.Double( bulbX, bulbY, bulbWidth, bulbHeight + fracBulbOverlap * bounds.getHeight() );

        pin = getPin( bulbShape );
        rad = new Point2D.Double( bounds.getWidth() / 2, bounds.getHeight() / 2 );
        paint = new RoundGradientPaint( pin.getX(), pin.getY(), Color.white, rad, Color.yellow );
        conductor = new Rectangle2D.Double( conductorX, conductorY, conductorWidth, conductorHeight );

        insulator = new Ellipse2D.Double( insulatorX, insulatorY, insulatorWidth, insulatorHeight );

        tip = new Rectangle2D.Double( tipX, tipY, tipWidth, tipHeight );

        //put angled spiralLines down the conductor rectangle.
        int numLines = 4;
        double dy = conductor.getHeight() / ( numLines );
        spiralLines = new ArrayList();
        for( int i = 0; i < numLines; i++ ) {
            double x1 = conductor.getX();
            double y1 = conductor.getY() + i * dy;
            double x2 = conductor.getX() + conductor.getWidth();
            double y2 = y1 + dy / 2;
            Line2D.Double line = new Line2D.Double( x1, y1, x2, y2 );
            spiralLines.add( line );
        }
        bulb.addObserver( bulbObserver );
        update();
    }

    public Bulb getBulb() {
        return bulb;
    }

    protected void update() {
        removeAllChildren();
        if( showCoverOnly ) {
            updateConductor();
            updateSpiralLines();
        }
        else {
            updateRays();
            updateTip();
            updateBulbShape();
            updateInsulator();
            updateConductor();
            updateSpiralLines();
            if( bulb.isSelected() ) {
                Rectangle2D rect = bulbShape.getBounds2D().createUnion( tip );
                rect = RectangleUtils.expand( rect, 2 / 60.0, 2 / 60.0 );
                highlightNode.setPathTo( rect );
                addChild( highlightNode );
            }
        }
    }

    private void updateSpiralLines() {
        for( int i = 0; i < spiralLines.size(); i++ ) {
            Line2D.Double aDouble = (Line2D.Double)spiralLines.get( i );
            PPath path = new PhetPPath( aDouble, linestroke, Color.black );
            addChild( path );
        }
    }

    private void updateConductor() {
        PPath conductorPath = new PhetPPath( conductor, Color.lightGray, baseStroke, Color.black );
        addChild( conductorPath );
    }

    private void updateInsulator() {
        PPath insulatorPath = new PhetPPath( insulator, insulatorColor );
        addChild( insulatorPath );
    }

    private void updateBulbShape() {
//        System.out.println( "intensity = " + intensity );
        PPath bulbShape = new PhetPPath( this.bulbShape, new BasicStroke( 1.0f / 40.0f ), Color.black );
        if( intensity > 1E-2 ) {
            Color yellow = Color.yellow;
            Color backgroundColor = new Color( yellow.getRed() / 255.0f, yellow.getGreen() / 255.0f, yellow.getBlue() / 255.0f, (float)intensity );
            Color pointColor = new Color( 1, 1, 1.0f, (float)intensity );
            paint = new RoundGradientPaint( pin.getX(), pin.getY(), pointColor, rad, backgroundColor );
            bulbShape.setPaint( paint );
        }
        else {
            bulbShape.setPaint( new Color( 0, 0, 0, 0 ) );
        }
        addChild( bulbShape );
    }

    private void updateTip() {
        PPath tipNode = new PPath();
        tipNode.setPaint( Color.black );
        tipNode.setStroke( tipStroke );
        tipNode.setPathTo( tip );
        addChild( tipNode );
    }

    private void updateRays() {
        for( int i = 0; i < brighties.size(); i++ ) {
            PPath pPath = new PPath( (Shape)brighties.get( i ) );
            pPath.setStrokePaint( brightyColor );
//            pPath.setStroke( new BasicStroke( 0.1f ) );
            pPath.setStroke( brightyStroke );
            addChild( pPath );
        }
    }

    private Point2D getPin( Ellipse2D.Double bulb ) {
        double fracX = .2;
        double fracY = .3;
        return new Point2D.Double( bulb.getX() + bulb.getWidth() * fracX, bulb.getY() + bulb.getHeight() * fracY );
    }

    public void setIntensity( double intensity ) {
        if( Double.isNaN( intensity ) ) {
            throw new RuntimeException( "NaN intensity" );
        }
        if( this.intensity == intensity ) {
            return;
        }
        this.intensity = intensity;

        Color yellow = Color.yellow;
//        Color pointColor = new Color( 1, 1, 1.0f, (float)intensity );
        Color backgroundColor = new Color( yellow.getRed() / 255.0f, yellow.getGreen() / 255.0f, yellow.getBlue() / 255.0f, (float)intensity );
//        Paint paint = new RoundGradientPaint( pin.getX(), pin.getY(), pointColor, rad, backgroundColor );

        int maxBrighties = 40;
        int numBrighties = (int)( intensity * maxBrighties );
        double maxDistance = bulb.getWidth() * 3;
        double distance = intensity * maxDistance;
        double distance0 = Math.max( bulb.getWidth() / 2, bulb.getHeight() / 2 ) * 1.05;
        this.brighties.clear();
        double angleFromDown = Math.PI / 4;
        double startAngle = Math.PI / 2 + angleFromDown;//-Math.PI/2+angleFromDown;
        double endAngle = startAngle + Math.PI * 2 - angleFromDown * 2;//*3/2-angleFromDown;

        double dTheta = Math.abs( endAngle - startAngle ) / ( numBrighties - 1 );

        double angle = startAngle;
        Point2D origin = center( bulbShape );
        double maxStrokeWidth = 3.5;
        double minStrokeWidth = .5;
        double strokeWidth = minStrokeWidth + intensity * maxStrokeWidth;
        for( int i = 0; i < numBrighties; i++ ) {
            AbstractVector2D vec = ImmutableVector2D.Double.parseAngleAndMagnitude( distance0, angle );
            AbstractVector2D vec1 = ImmutableVector2D.Double.parseAngleAndMagnitude( distance + distance0, angle );

            Point2D end = vec.getDestination( origin );
            Point2D end2 = vec1.getDestination( origin );
            Line2D.Double line = new Line2D.Double( end, end2 );
            brighties.add( line );
            angle += dTheta;
        }
        this.brightyStroke = new BasicStroke( (float)strokeWidth / 40.0f * SCALE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        this.brightyColor = backgroundColor;
        update();
    }

    static Point2D center( Ellipse2D r ) {
        return new Point2D.Double( r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2 );
    }

    public Shape getCoverShape() {
        return conductor;
    }

    public void setShowCoverOnly() {
        showCoverOnly = true;
    }

    public Shape getCoverShapeOnFilamentSide() {
        return new Rectangle2D.Double( conductor.getX(), conductor.getY(), conductor.getWidth() * 0.63, conductor.getHeight() );
    }

//    public void setHighlightVisible( boolean selected ) {
//        highlightNode.setVisible( selected );
//    }

    public void delete() {
        bulb.removeObserver( bulbObserver );
    }
}
