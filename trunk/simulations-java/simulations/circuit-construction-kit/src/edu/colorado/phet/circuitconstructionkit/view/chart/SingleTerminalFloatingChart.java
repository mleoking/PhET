package edu.colorado.phet.circuitconstructionkit.view.chart;

import java.awt.geom.Point2D;

import edu.colorado.phet.circuitconstructionkit.view.piccolo.CCKSimulationPanel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * User: Sam Reid
 * Date: Jul 6, 2006
 * Time: 7:50:47 PM
 */

public abstract class SingleTerminalFloatingChart extends AbstractFloatingChart {
    private ValueReader valueReader;
    private CrosshairNode crosshairNode;
    private PhetPCanvas pSwingCanvas;

    public SingleTerminalFloatingChart( PhetPCanvas pSwingCanvas, String title, ValueReader valueReader, IClock clock ) {
        super( title, clock );
        this.pSwingCanvas = pSwingCanvas;
        this.valueReader = valueReader;

        crosshairNode = new CrosshairNode( this, 10, 15 );
        CrosshairConnection crosshairConnection = new CrosshairConnection( this, crosshairNode );
        addChild( crosshairConnection );

        addChild( crosshairNode );
        StripChartJFCNode stripChartJFCNode = super.getStripChartJFCNode();
        stripChartJFCNode.setOffset( -stripChartJFCNode.getFullBounds().getWidth() - crosshairNode.getFullBounds().getWidth() / 2.0,
                                     -stripChartJFCNode.getFullBounds().getHeight() / 2.0 );
        double crosshairOffsetDX = crosshairNode.getFullBounds().getWidth() * 1.25;
        crosshairNode.translate( crosshairOffsetDX, 0 );
        stripChartJFCNode.addInputEventListener( new PairDragHandler() );
    }

    public void setValueReader( ValueReader valueReader ) {
        this.valueReader = valueReader;
    }

    public void update() {
        super.update();
        if ( crosshairNode != null && valueReader != null ) {
            Point2D location = getLocation();
            double value = valueReader.getValue( location.getX(), location.getY() );
            double t = CCKTime.getDisplayTime( super.getClock().getSimulationTime() );
            getStripChartJFCNode().addValue( t, value );
        }
    }

    protected abstract Point2D getLocation();

    class PairDragHandler extends PDragEventHandler {
        protected void drag( PInputEvent event ) {
            super.drag( event );
            if ( crosshairNode.isAttached() ) {
                crosshairNode.translate( event.getCanvasDelta().getWidth(), event.getCanvasDelta().getHeight() );
            }
        }
    }

    public CrosshairNode getCrosshairGraphic() {
        return crosshairNode;
    }

    protected PhetPCanvas getPhetPCanvas() {
        return pSwingCanvas;
    }

    public static class Piccolo extends SingleTerminalFloatingChart {
        private CCKSimulationPanel cckSimulationPanel;

        public Piccolo( PhetPCanvas pSwingCanvas, String title, ValueReader valueReader, IClock clock, CCKSimulationPanel cckSimulationPanel ) {
            super( pSwingCanvas, title, valueReader, clock );
            this.cckSimulationPanel = cckSimulationPanel;
        }

        protected Point2D getLocation() {
            Point2D location = getCrosshairGraphic().getGlobalTranslation();
            cckSimulationPanel.getCircuitNode().globalToLocal( location );
            return location;
        }
    }
}
