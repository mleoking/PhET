package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 9:58:49 AM
 * Copyright (c) Sep 25, 2006 by Sam Reid
 */

public class MeasurementToolSetNode extends PhetPNode {
    private VoltmeterNode voltmeterNode;
    private VirtualAmmeterNode virtualAmmeterNode;

    public MeasurementToolSetNode( CCKModel model, PSwingCanvas pSwingCanvas, ICCKModule module, VoltmeterModel voltmeterModel ) {
        voltmeterNode = new VoltmeterNode( voltmeterModel );
        addChild( voltmeterNode );
        this.virtualAmmeterNode = new VirtualAmmeterNode( model.getCircuit(), pSwingCanvas, module );
        addChild( virtualAmmeterNode );
    }

    public void setVoltmeterVisible( boolean visible ) {
        voltmeterNode.setVisible( visible );
    }

    public void setVirtualAmmeterVisible( boolean selected ) {
        virtualAmmeterNode.setVisible( selected );
    }
}
