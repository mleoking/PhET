// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.chart;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.circuitconstructionkit.view.piccolo.CCKSimulationPanel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * User: Sam Reid
 * Date: Jul 6, 2006
 * Time: 7:50:47 PM
 */

public abstract class DoubleTerminalFloatingChart extends AbstractFloatingChart {
    private CrosshairNode leftCrosshairNode;
    private CrosshairNode rightCrosshairNode;
    private TwoTerminalValueReader valueReader;
    private PhetPCanvas phetPCanvas;

    public DoubleTerminalFloatingChart( PhetPCanvas phetPCanvas, String title, TwoTerminalValueReader valueReader, IClock clock ) {
        super( title, clock );
        this.valueReader = valueReader;
        this.phetPCanvas = phetPCanvas;

        leftCrosshairNode = new CrosshairNode( this, 10, 15 );
        rightCrosshairNode = new CrosshairNode( this, 10, 15 );
        CrosshairConnection leftCrosshairConnection = new CrosshairConnection( this, leftCrosshairNode );
        addChild( leftCrosshairConnection );
        CrosshairConnection rightCrosshairConnection = new CrosshairConnection( this, rightCrosshairNode );
        addChild( rightCrosshairConnection );

        addChild( leftCrosshairNode );
        addChild( rightCrosshairNode );
        StripChartJFCNode stripChartJFCNode = super.getStripChartJFCNode();
        stripChartJFCNode.setOffset( -stripChartJFCNode.getFullBounds().getWidth() - leftCrosshairNode.getFullBounds().getWidth() / 2.0,
                                     -stripChartJFCNode.getFullBounds().getHeight() / 2.0 );
        double crosshairOffsetDX = leftCrosshairNode.getFullBounds().getWidth() * 1.25;
        leftCrosshairNode.translate( crosshairOffsetDX, -30 );
        rightCrosshairNode.translate( crosshairOffsetDX, 30 );
        stripChartJFCNode.addInputEventListener( new DoubleTerminalFloatingChart.PairDragHandler() );
    }

    public CrosshairNode getLeftCrosshairGraphic() {
        return leftCrosshairNode;
    }

    public CrosshairNode getRightCrosshairGraphic() {
        return rightCrosshairNode;
    }

    public void update() {
        super.update();
        if ( leftCrosshairNode != null && valueReader != null ) {
            //get the coordinate in the wavefunctiongraphic.
            double value = valueReader.getValue( getLeftShape(), getRightShape() );
            double t = super.getClock().getSimulationTime();
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
            if ( leftCrosshairNode.isAttached() ) {
                leftCrosshairNode.translate( event.getCanvasDelta().getWidth(), event.getCanvasDelta().getHeight() );
            }
        }
    }

    public static class Piccolo extends DoubleTerminalFloatingChart {
        private CCKSimulationPanel cckSimulationPanel;

        public Piccolo( CCKSimulationPanel cckSimulationPanel, String title, TwoTerminalValueReader valueReader, IClock clock ) {
            super( cckSimulationPanel, title, valueReader, clock );
            this.cckSimulationPanel = cckSimulationPanel;
        }

        protected Shape getRightShape() {
            return getShape( getRightCrosshairGraphic() );
        }

        private Shape getShape( PNode node ) {
            Point2D location = node.getGlobalTranslation();
            cckSimulationPanel.getCircuitNode().globalToLocal( location );
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
