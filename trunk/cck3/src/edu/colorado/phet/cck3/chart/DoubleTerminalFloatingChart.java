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

public class DoubleTerminalFloatingChart extends AbstractFloatingChart {
    private CrosshairGraphic leftCrosshairGraphic;
    private CrosshairGraphic rightCrosshairGraphic;
    private TwoTerminalValueReader valueReader;

    public DoubleTerminalFloatingChart( PSwingCanvas pSwingCanvas, String title, TwoTerminalValueReader valueReader, IClock clock ) {
        super( pSwingCanvas, title, clock );
        this.valueReader = valueReader;

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

    public void update() {
        super.update();
        if( leftCrosshairGraphic != null && valueReader != null ) {
            //get the coordinate in the wavefunctiongraphic.
            Point2D location = leftCrosshairGraphic.getGlobalTranslation();
            location.setLocation( location.getX() + 1, location.getY() + 1 );//todo this line seems necessary because we are off somewhere by 1 pixel

            Point2D locRight = rightCrosshairGraphic.getGlobalTranslation();
            locRight.setLocation( locRight.getX() + 1, locRight.getY() + 1 );

            double value = valueReader.getValue( location.getX(), location.getY(), locRight.getX(), locRight.getY() );
            CCKTime cckTime = new CCKTime();
            double t = cckTime.getDisplayTime( super.getClock().getSimulationTime() );
            getStripChartJFCNode().addValue( t, value );
        }
    }

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

}
