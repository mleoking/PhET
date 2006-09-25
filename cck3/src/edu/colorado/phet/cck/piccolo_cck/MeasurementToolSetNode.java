package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.piccolo.PhetPNode;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 9:58:49 AM
 * Copyright (c) Sep 25, 2006 by Sam Reid
 */

public class MeasurementToolSetNode extends PhetPNode {
    private VoltmeterNode voltmeterNode;

    public MeasurementToolSetNode( VoltmeterModel voltmeterModel ) {
        voltmeterNode = new VoltmeterNode( voltmeterModel );
        addChild( voltmeterNode );
    }

    public void setVoltmeterVisible( boolean visible ) {
        voltmeterNode.setVisible( visible );
    }
}
