/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.AutoFire;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 8:20:48 AM
 * Copyright (c) Jul 18, 2005 by Sam Reid
 */

public class AutoFireCheckBox extends JCheckBox {
    public AutoFireCheckBox( final AutoFire autoFire ) {
        super( "Auto-Fire" );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                autoFire.setAutoFire( isSelected() );
            }
        } );
    }
}
