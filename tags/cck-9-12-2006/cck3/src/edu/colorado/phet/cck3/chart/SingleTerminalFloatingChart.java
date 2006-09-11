package edu.colorado.phet.cck3.chart;

import edu.colorado.phet.common.model.clock.IClock;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 6, 2006
 * Time: 7:50:47 PM
 * Copyright (c) Jul 6, 2006 by Sam Reid
 */

public class SingleTerminalFloatingChart extends AbstractFloatingChart {
    private ValueReader valueReader;
    private CrosshairGraphic crosshairGraphic;

    public SingleTerminalFloatingChart( PSwingCanvas pSwingCanvas, String title, ValueReader valueReader, IClock clock ) {
        super( pSwingCanvas, title, clock );
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
            if( crosshairGraphic.isAttached() ) {
                crosshairGraphic.translate( event.getCanvasDelta().getWidth(), event.getCanvasDelta().getHeight() );
            }
        }
    }

    public CrosshairGraphic getCrosshairGraphic() {
        return crosshairGraphic;
    }

}
