/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.qm.view.swing.SchrodingerPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 11, 2005
 * Time: 9:19:53 PM
 * Copyright (c) Dec 11, 2005 by Sam Reid
 */

public class InverseSlitsCheckbox extends JCheckBox {
    private SchrodingerPanel schrodingerPanel;

    public InverseSlitsCheckbox( final SchrodingerPanel schrodingerPanel ) {
        super( "Inverse Barrier" );
        this.schrodingerPanel = schrodingerPanel;
        setSelected( schrodingerPanel.isInverseSlits() );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                schrodingerPanel.setInverseSlits( isSelected() );
            }
        } );
        schrodingerPanel.addListener( new SchrodingerPanel.Adapter() {
            public void inverseSlitsChanged() {
                setSelected( schrodingerPanel.isInverseSlits() );
            }
        } );
    }
}
