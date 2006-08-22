/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.chart;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jul 6, 2006
 * Time: 8:38:51 PM
 * Copyright (c) Jul 6, 2006 by Sam Reid
 */
public class CrosshairGraphic extends PComposite {
    private static final Paint CROSSHAIR_COLOR = Color.white;
    private BasicStroke CROSSHAIR_STROKE = new BasicStroke( 2 );
    private CrosshairDragHandler listener;
    private AbstractFloatingChart intensityReader;
    private boolean attached = false;
    private Vector2D originalDisplacement;
    private PPath background;
    private PPath innerCircle;

    public CrosshairGraphic( AbstractFloatingChart intensityReader, int innerRadius, int outerRadius ) {
        this.intensityReader = intensityReader;
        Ellipse2D.Double aShape = new Ellipse2D.Double( -innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2 );
        innerCircle = new PPath( aShape );
        innerCircle.setStrokePaint( Color.darkGray );
        innerCircle.setStroke( new BasicStroke( 1 ) );

        PPath vertical = new PPath( new Line2D.Double( 0, -outerRadius, 0, outerRadius ) );
        vertical.setStroke( CROSSHAIR_STROKE );
        vertical.setStrokePaint( CROSSHAIR_COLOR );

        PPath horizontalLine = new PPath( new Line2D.Double( -outerRadius, 0, outerRadius, 0 ) );
        horizontalLine.setStroke( CROSSHAIR_STROKE );
        horizontalLine.setStrokePaint( CROSSHAIR_COLOR );

        Area backgroundShape = new Area();
        backgroundShape.add( new Area( new Rectangle2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2 ) ) );
        backgroundShape.subtract( new Area( aShape ) );
        background = new PPath( backgroundShape );
        background.setPaint( Color.lightGray );
        background.setStrokePaint( Color.gray );

        addChild( background );
        addChild( innerCircle );
        addChild( vertical );
        addChild( horizontalLine );
        //to ensure the entire object is grabbable
        PPath overlay = new PPath( new Rectangle2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2 ) );
        overlay.setPaint( new Color( 255, 255, 255, 0 ) );
        overlay.setStrokePaint( new Color( 255, 255, 255, 0 ) );
        addChild( overlay );
        listener = new CrosshairDragHandler();
        addInputEventListener( listener );

        originalDisplacement = getDisplacement();
    }

    public StripChartJFCNode getStripChartJFCNode() {
        return intensityReader.getStripChartJFCNode();
    }

    public PPath getBackground() {
        return background;
    }

    private void attachCrosshair() {
        attached = true;
        setOffset( getStripChartJFCNode().getFullBounds().getCenterX() + originalDisplacement.getX() - getFullBounds().getWidth() / 2,
                   getStripChartJFCNode().getFullBounds().getCenterY() + originalDisplacement.getY() );
    }

    private Vector2D getDisplacement() {
        return new Vector2D.Double( getStripChartJFCNode().getFullBounds().getCenter2D(), getFullBounds().getCenter2D() );
    }

    private void crosshairDropped() {
        double threshold = 30;
        if( MathUtil.isApproxEqual( getDisplacement().getX(), originalDisplacement.getX(), threshold )
            && MathUtil.isApproxEqual( getDisplacement().getY(), originalDisplacement.getY(), threshold ) ) {
            attachCrosshair();
        }
    }

    private void detachCrosshair() {
        attached = false;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setColor( Color red ) {
        background.setPaint( red );
        innerCircle.setStrokePaint( Color.gray );
    }

    class CrosshairDragHandler extends PDragEventHandler {
        protected void drag( PInputEvent event ) {
            super.drag( event );
            detachCrosshair();
            event.setHandled( true );
        }

        protected void superdrag( PInputEvent event ) {
            super.drag( event );
        }

        protected void endDrag( PInputEvent event ) {
            super.endDrag( event );
            crosshairDropped();
        }
    }

    public void drag( PInputEvent event ) {
        listener.superdrag( event );
    }
}
