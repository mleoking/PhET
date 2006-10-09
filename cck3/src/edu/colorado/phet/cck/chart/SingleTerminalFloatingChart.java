package edu.colorado.phet.cck.chart;

import edu.colorado.phet.cck.piccolo_cck.CCKSimulationPanel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 6, 2006
 * Time: 7:50:47 PM
 * Copyright (c) Jul 6, 2006 by Sam Reid
 */

public abstract class SingleTerminalFloatingChart extends AbstractFloatingChart {
    private ValueReader valueReader;
    private CrosshairGraphic crosshairGraphic;
    private PhetPCanvas pSwingCanvas;

    public SingleTerminalFloatingChart( PhetPCanvas pSwingCanvas, String title, ValueReader valueReader, IClock clock ) {
        super( pSwingCanvas, title, clock );
        this.pSwingCanvas = pSwingCanvas;
        this.valueReader = valueReader;

        crosshairGraphic = new CrosshairGraphic( this, 10, 15 );
        CrosshairConnection crosshairConnection = new CrosshairConnection( this, crosshairGraphic );
        addChild( crosshairConnection );

        addChild( crosshairGraphic );
        StripChartJFCNode stripChartJFCNode = super.getStripChartJFCNode();
        stripChartJFCNode.setOffset( -stripChartJFCNode.getFullBounds().getWidth() - crosshairGraphic.getFullBounds().getWidth() / 2.0,
                                     -stripChartJFCNode.getFullBounds().getHeight() / 2.0 );
        double crosshairOffsetDX = crosshairGraphic.getFullBounds().getWidth() * 1.25;
        crosshairGraphic.translate( crosshairOffsetDX, 0 );
        stripChartJFCNode.addInputEventListener( new PairDragHandler() );
    }

    public void setValueReader( ValueReader valueReader ) {
        this.valueReader = valueReader;
    }

    public void update() {
        super.update();
        if( crosshairGraphic != null && valueReader != null ) {
            Point2D location = getLocation();
            double value = valueReader.getValue( location.getX(), location.getY() );
            CCKTime cckTime = new CCKTime();
            double t = cckTime.getDisplayTime( super.getClock().getSimulationTime() );
            getStripChartJFCNode().addValue( t, value );
        }
    }

    protected abstract Point2D getLocation();

    class PairDragHandler extends PDragEventHandler {
        protected void drag( PInputEvent event ) {
            super.drag( event );
            if( crosshairGraphic.isAttached() ) {
                crosshairGraphic.translate( event.getCanvasDelta().getWidth(), event.getCanvasDelta().getHeight() );
            }
        }
    }

    public CrosshairGraphic getCrosshairGraphic() {
        return crosshairGraphic;
    }

    protected PhetPCanvas getPhetPCanvas() {
        return pSwingCanvas;
    }

    public static class Piccolo extends SingleTerminalFloatingChart {
        private CCKSimulationPanel cckSimulationPanel;

        public Piccolo( PhetPCanvas pSwingCanvas, String title, ValueReader valueReader, IClock clock, CCKSimulationPanel cckSimulationPanel ) {
            super( pSwingCanvas, title, valueReader, clock);
            this.cckSimulationPanel = cckSimulationPanel;
        }

        protected Point2D getLocation() {
            Point2D location = getCrosshairGraphic().getGlobalTranslation();
            cckSimulationPanel.getCircuitNode().globalToLocal( location );
//            super.getPhetPCanvas().getPhetRootNode().globalToWorld( location );
            return location;
        }
    }


    public static class Phetgraphics extends SingleTerminalFloatingChart {

        public Phetgraphics( PhetPCanvas pSwingCanvas, String title, ValueReader valueReader, IClock clock ) {
            super( pSwingCanvas, title, valueReader, clock );
        }

        protected Point2D getLocation() {
            Point2D location = getCrosshairGraphic().getGlobalTranslation();
            location.setLocation( location.getX() + 1, location.getY() + 1 );//todo this line seems necessary because we are off somewhere by 1 pixel
            return location;
        }
    }
}
