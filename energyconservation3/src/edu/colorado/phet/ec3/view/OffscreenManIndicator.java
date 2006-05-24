/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.ec3.EC3Module;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Oct 27, 2005
 * Time: 9:18:57 AM
 * Copyright (c) Oct 27, 2005 by Sam Reid
 */

public class OffscreenManIndicator extends PNode {
    private BodyGraphic body;
    private EC3Module module;

    public OffscreenManIndicator( EC3Module ec3Module, BodyGraphic body ) {
        this.body = body;
        this.module = ec3Module;
        JButton bringBackSkater = new JButton( "Bring back the Skater" );
        PSwing pSwing = new PSwing( ec3Module.getEnergyConservationCanvas(), bringBackSkater );
        addChild( pSwing );
    }

    public void setBodyGraphic( BodyGraphic body ) {
        this.body = body;
        update();
    }

    public void update() {
        if( body == null ) {
            setVisible( false );
        }
        else {
            PBounds s = body.getGlobalFullBounds();

        }
    }

    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        setPickable( isVisible );
        setChildrenPickable( isVisible );
    }
}
