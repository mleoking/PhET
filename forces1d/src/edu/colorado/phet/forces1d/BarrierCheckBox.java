/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jan 25, 2005
 * Time: 3:18:39 AM
 * Copyright (c) Jan 25, 2005 by Sam Reid
 */

public class BarrierCheckBox extends JCheckBox {
    public BarrierCheckBox( final Force1DModule module ) {
        super( "Barriers" );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if( !isSelected() ) {
                    module.getForceModel().setBoundsOpen();
                }
                else {
                    module.getForceModel().setBoundsWalled();
                }
            }
        } );
    }
}
