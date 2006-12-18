package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.StopwatchDecorator;
import edu.colorado.phet.cck.chart.CCKTime;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 9:58:49 AM
 * Copyright (c) Sep 25, 2006 by Sam Reid
 */

public class MeasurementToolSetNode extends PhetPNode {
    private VoltmeterNode voltmeterNode;
    private VirtualAmmeterNode virtualAmmeterNode;
    private PNode stopwatchNode;

    public MeasurementToolSetNode( CCKModel model, PSwingCanvas pSwingCanvas, ICCKModule module, VoltmeterModel voltmeterModel ) {
        voltmeterNode = new VoltmeterNode( voltmeterModel );
        addChild( voltmeterNode );
        this.virtualAmmeterNode = new VirtualAmmeterNode( model.getCircuit(), pSwingCanvas, module );
        virtualAmmeterNode.setVisible( false );
        addChild( virtualAmmeterNode );

        SwingClock clock = new SwingClock( 30, 1 );
        clock.start();
        stopwatchNode = new PhetPNode( new PSwing( pSwingCanvas, new StopwatchDecorator( clock, 1.0 * CCKTime.viewTimeScale, "s" ) ) );
//        stopwatchNode = new PhetPNode( new PSwing( pSwingCanvas, new StopwatchDecorator( clock, 1.0 , "s" ) ) );
        stopwatchNode.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        stopwatchNode.addInputEventListener( new PDragEventHandler() );
        stopwatchNode.setVisible( false );
        stopwatchNode.scale( 1.0 / 60.0 );
        stopwatchNode.translate( 2, 2 );
        addChild( stopwatchNode );
    }

    public void setVoltmeterVisible( boolean visible ) {
        voltmeterNode.setVisible( visible );
    }

    public void setVirtualAmmeterVisible( boolean selected ) {
        virtualAmmeterNode.setVisible( selected );
    }

    public void setStopwatchVisible( boolean selected ) {
        stopwatchNode.setVisible( selected );
    }
}
