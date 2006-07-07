/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.chart;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.IClock;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;
import java.awt.geom.*;

/**
 * User: Sam Reid
 * Date: Jul 6, 2006
 * Time: 7:50:47 PM
 * Copyright (c) Jul 6, 2006 by Sam Reid
 */

public class SingleTerminalFloatingChart extends AbstractFloatingChart {
    private ValueReader valueReader;
    private CrosshairGraphic crosshairGraphic;
    private boolean detached = false;
    private Vector2D originalDisplacement;

    public SingleTerminalFloatingChart( PSwingCanvas pSwingCanvas, String title, ValueReader valueReader, IClock clock ) {
        super( pSwingCanvas, title, clock );
        this.valueReader = valueReader;


        crosshairGraphic = new CrosshairGraphic( this, 10, 15 );
        CrosshairConnection crosshairConnection = new CrosshairConnection( this );
        addChild( crosshairConnection );

        addChild( crosshairGraphic );
        StripChartJFCNode stripChartJFCNode = super.getStripChartJFCNode();
        stripChartJFCNode.setOffset( -stripChartJFCNode.getFullBounds().getWidth() - crosshairGraphic.getFullBounds().getWidth() / 2.0,
                                     -stripChartJFCNode.getFullBounds().getHeight() / 2.0 );
        double crosshairOffsetDX = crosshairGraphic.getFullBounds().getWidth() * 1.25;
        crosshairGraphic.translate( crosshairOffsetDX, 0 );
        stripChartJFCNode.addInputEventListener( new PairDragHandler() );
        originalDisplacement = getDisplacement();

    }

    public void setValueReader( ValueReader valueReader ) {
        this.valueReader = valueReader;
    }

    private Vector2D getDisplacement() {
        return new Vector2D.Double( getStripChartJFCNode().getFullBounds().getCenter2D(), crosshairGraphic.getFullBounds().getCenter2D() );
    }

    public void update() {
        super.update();
        if( crosshairGraphic != null && valueReader != null ) {
            //get the coordinate in the wavefunctiongraphic.
            Point2D location = crosshairGraphic.getGlobalTranslation();
            location.setLocation( location.getX() + 1, location.getY() + 1 );//todo this line seems necessary because we are off somewhere by 1 pixel
            double value = valueReader.getValue( location.getX(), location.getY() );
            CCKTime cckTime = new CCKTime();
            double t = cckTime.getDisplayTime( super.getClock().getSimulationTime() );
            getStripChartJFCNode().addValue( t, value );
        }
    }

    class PairDragHandler extends PDragEventHandler {
        protected void drag( PInputEvent event ) {
            super.drag( event );
            if( !detached ) {
                crosshairGraphic.translate( event.getCanvasDelta().getWidth(), event.getCanvasDelta().getHeight() );
            }
        }
    }

    public CrosshairGraphic getCrosshairGraphic() {
        return crosshairGraphic;
    }

    private void crosshairDropped() {
        double threshold = 30;
        if( MathUtil.isApproxEqual( getDisplacement().getX(), originalDisplacement.getX(), threshold )
            && MathUtil.isApproxEqual( getDisplacement().getY(), originalDisplacement.getY(), threshold ) ) {
            attachCrosshair();
        }
    }

    private void attachCrosshair() {
        detached = false;
        crosshairGraphic.setOffset( getStripChartJFCNode().getFullBounds().getCenterX() + originalDisplacement.getX() - crosshairGraphic.getFullBounds().getWidth() / 2,
                                    getStripChartJFCNode().getFullBounds().getCenterY() + originalDisplacement.getY() );
    }

    private void detachCrosshair() {
        detached = true;
    }

    static class CrosshairGraphic extends PComposite {
        private static final Paint CROSSHAIR_COLOR = Color.white;
        private BasicStroke CROSSHAIR_STROKE = new BasicStroke( 2 );
        private CrosshairDragHandler listener;
        private SingleTerminalFloatingChart intensityReader;

        public CrosshairGraphic( SingleTerminalFloatingChart intensityReader, int innerRadius, int outerRadius ) {
            this.intensityReader = intensityReader;
            Ellipse2D.Double aShape = new Ellipse2D.Double( -innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2 );
            PPath circle = new PPath( aShape );
            circle.setStrokePaint( Color.red );
            circle.setStroke( new BasicStroke( 2 ) );

            PPath vertical = new PPath( new Line2D.Double( 0, -outerRadius, 0, outerRadius ) );
            vertical.setStroke( CROSSHAIR_STROKE );
            vertical.setStrokePaint( CROSSHAIR_COLOR );

            PPath horizontalLine = new PPath( new Line2D.Double( -outerRadius, 0, outerRadius, 0 ) );
            horizontalLine.setStroke( CROSSHAIR_STROKE );
            horizontalLine.setStrokePaint( CROSSHAIR_COLOR );

            Area backgroundShape = new Area();
            backgroundShape.add( new Area( new Rectangle2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2 ) ) );
            backgroundShape.subtract( new Area( aShape ) );
            PPath background = new PPath( backgroundShape );
            background.setPaint( Color.lightGray );
            background.setStrokePaint( Color.gray );

            addChild( background );
            addChild( circle );
            addChild( vertical );
            addChild( horizontalLine );
            //to ensure the entire object is grabbable
            PPath overlay = new PPath( new Rectangle2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2 ) );
            overlay.setPaint( new Color( 255, 255, 255, 0 ) );
            overlay.setStrokePaint( new Color( 255, 255, 255, 0 ) );
            addChild( overlay );
            listener = new CrosshairDragHandler();
            addInputEventListener( listener );
        }

        class CrosshairDragHandler extends PDragEventHandler {
            protected void drag( PInputEvent event ) {
                super.drag( event );
                intensityReader.detachCrosshair();
                event.setHandled( true );
            }

            protected void superdrag( PInputEvent event ) {
                super.drag( event );
            }

            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                intensityReader.crosshairDropped();
            }
        }

        public void drag( PInputEvent event ) {
            listener.superdrag( event );
        }
    }
}
