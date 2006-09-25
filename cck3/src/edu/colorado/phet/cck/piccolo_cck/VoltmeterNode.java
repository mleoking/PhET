package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 9:59:20 AM
 * Copyright (c) Sep 25, 2006 by Sam Reid
 */

public class VoltmeterNode extends PhetPNode {
    private VoltmeterModel voltmeterModel;
    private PImage unitImageNode;
    private PImage blackProbe;
    private PImage redProbe;
    private PPath redCable;
    private PPath blackCable;

    public VoltmeterNode( final VoltmeterModel voltmeterModel ) {
        this.voltmeterModel = voltmeterModel;
        unitImageNode = PImageFactory.create( "images/vm3.gif" );
        redProbe = PImageFactory.create( "images/probeRed.gif" );
        blackProbe = PImageFactory.create( "images/probeBlack.gif" );
        unitImageNode.scale( 1.0 / 80.0 );
        addChild( unitImageNode );
        voltmeterModel.addListener( new VoltmeterModel.Listener() {
            public void voltmeterChanged() {
                update();
            }
        } );
        update();
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension pt = event.getDeltaRelativeTo( VoltmeterNode.this.getParent() );
                voltmeterModel.translateBody( pt.width, pt.height );
            }
        } );
    }

    private void update() {
        setVisible( voltmeterModel.isVisible() );
        setOffset( voltmeterModel.getUnitOffset() );
    }
}
