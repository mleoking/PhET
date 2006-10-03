package edu.colorado.phet.cck.chart;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jul 6, 2006
 * Time: 7:50:47 PM
 * Copyright (c) Jul 6, 2006 by Sam Reid
 */

public abstract class DoubleTerminalFloatingChart extends AbstractFloatingChart {
    private CrosshairGraphic leftCrosshairGraphic;
    private CrosshairGraphic rightCrosshairGraphic;
    private TwoTerminalValueReader valueReader;
    private PhetPCanvas phetPCanvas;

    public DoubleTerminalFloatingChart( PhetPCanvas phetPCanvas, String title, TwoTerminalValueReader valueReader, IClock clock ) {
        super( phetPCanvas, title, clock );
        this.valueReader = valueReader;
        this.phetPCanvas = phetPCanvas;

        leftCrosshairGraphic = new CrosshairGraphic( this, 10, 15 );
        rightCrosshairGraphic = new CrosshairGraphic( this, 10, 15 );
        CrosshairConnection leftCrosshairConnection = new CrosshairConnection( this, leftCrosshairGraphic );
        addChild( leftCrosshairConnection );
        CrosshairConnection rightCrosshairConnection = new CrosshairConnection( this, rightCrosshairGraphic );
        addChild( rightCrosshairConnection );

        addChild( leftCrosshairGraphic );
        addChild( rightCrosshairGraphic );
        StripChartJFCNode stripChartJFCNode = super.getStripChartJFCNode();
        stripChartJFCNode.setOffset( -stripChartJFCNode.getFullBounds().getWidth() - leftCrosshairGraphic.getFullBounds().getWidth() / 2.0,
                                     -stripChartJFCNode.getFullBounds().getHeight() / 2.0 );
        double crosshairOffsetDX = leftCrosshairGraphic.getFullBounds().getWidth() * 1.25;
        leftCrosshairGraphic.translate( crosshairOffsetDX, -30 );
        rightCrosshairGraphic.translate( crosshairOffsetDX, 30 );
        stripChartJFCNode.addInputEventListener( new DoubleTerminalFloatingChart.PairDragHandler() );
    }

    public CrosshairGraphic getLeftCrosshairGraphic() {
        return leftCrosshairGraphic;
    }

    public CrosshairGraphic getRightCrosshairGraphic() {
        return rightCrosshairGraphic;
    }

    public void update() {
        super.update();
        if( leftCrosshairGraphic != null && valueReader != null ) {
            //get the coordinate in the wavefunctiongraphic.
            double value = valueReader.getValue( getLeftShape(), getRightShape() );
            CCKTime cckTime = new CCKTime();
            double t = cckTime.getDisplayTime( super.getClock().getSimulationTime() );
            getStripChartJFCNode().addValue( t, value );
        }
    }

    protected abstract Shape getRightShape();

    protected abstract Shape getLeftShape();

    public void setValueReader( TwoTerminalValueReader valueReader ) {
        this.valueReader = valueReader;
        update();
    }

    class PairDragHandler extends PDragEventHandler {
        protected void drag( PInputEvent event ) {
            super.drag( event );
            if( leftCrosshairGraphic.isAttached() ) {
                leftCrosshairGraphic.translate( event.getCanvasDelta().getWidth(), event.getCanvasDelta().getHeight() );
            }
        }
    }

    public static class Phetgraphics extends DoubleTerminalFloatingChart {

        public Phetgraphics( PhetPCanvas pSwingCanvas, String title, TwoTerminalValueReader valueReader, IClock clock ) {
            super( pSwingCanvas, title, valueReader, clock );
        }

        protected Shape getRightShape() {
            return getShape( getRightCrosshairGraphic() );
        }

        protected Shape getLeftShape() {
            return getShape( getLeftCrosshairGraphic() );
        }

        private Shape getShape( CrosshairGraphic leftCrosshairGraphic ) {
            Point2D location = leftCrosshairGraphic.getGlobalTranslation();
            location.setLocation( location.getX() + 1, location.getY() + 1 );//todo this line seems necessary because we are off somewhere by 1 pixel
            double w = 2.0;
            double h = 2.0;
            return new Rectangle2D.Double( location.getX() - w / 2, location.getY() - h / 2, w, h );
        }
    }

    public static class Piccolo extends DoubleTerminalFloatingChart {
        public Piccolo( PhetPCanvas pSwingCanvas, String title, TwoTerminalValueReader valueReader, IClock clock ) {
            super( pSwingCanvas, title, valueReader, clock );
        }

        protected Shape getRightShape() {
            return getShape( getRightCrosshairGraphic() );
        }

        private Shape getShape( PNode node ) {
            Point2D location = node.getGlobalTranslation();
            super.getPhetPCanvas().getPhetRootNode().globalToWorld( location );
            return new Rectangle2D.Double( location.getX(), location.getY(), 0.01, 0.01 );
        }

        protected Shape getLeftShape() {
            return getShape( getLeftCrosshairGraphic() );
        }
    }

    protected PhetPCanvas getPhetPCanvas() {
        return phetPCanvas;
    }
}
