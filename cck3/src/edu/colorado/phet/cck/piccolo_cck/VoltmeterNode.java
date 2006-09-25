package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 9:59:20 AM
 * Copyright (c) Sep 25, 2006 by Sam Reid
 */

public class VoltmeterNode extends PhetPNode {
    private VoltmeterModel voltmeterModel;
    private PImage unitImageNode;

    public VoltmeterNode( final VoltmeterModel voltmeterModel ) {
        this.voltmeterModel = voltmeterModel;
        PPath path = new PhetPPath( new Rectangle( 1, 1 ), Color.blue );
        addChild( path );

        try {
            unitImageNode = new PImage( ImageLoader.loadBufferedImage( "images/vm3.gif" ) );
//            unitImageNode.scale( 1.0 / 80.0 );
//            addChild( unitImageNode );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        voltmeterModel.addListener( new VoltmeterModel.Listener() {
            public void voltmeterChanged() {
                update();
            }
        } );
        update();
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension pt = event.getDeltaRelativeTo( VoltmeterNode.this.getParent().getParent() );
                System.out.println( "pt = " + pt );
                voltmeterModel.translateBody( pt.width, pt.height );
            }
        } );
    }

    private void update() {
        System.out.println( "voltmeterModel.getUnitOffset() = " + voltmeterModel.getUnitOffset() );
        System.out.println( "getGlobalFullBounds() = " + getGlobalFullBounds() );
        setVisible( voltmeterModel.isVisible() );
        setOffset( voltmeterModel.getUnitOffset() );
    }
}
