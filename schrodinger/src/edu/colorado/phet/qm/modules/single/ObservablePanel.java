/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 24, 2005
 * Time: 5:09:07 PM
 * Copyright (c) Jul 24, 2005 by Sam Reid
 */

public class ObservablePanel extends AdvancedPanel {
    private SchrodingerPanel schrodingerPanel;

    public ObservablePanel( SchrodingerPanel schrodingerPanel ) {
        super( "Observables>>", "Hide Observables<<" );
        this.schrodingerPanel = schrodingerPanel;
//        VerticalLayoutPanel lay = this;
//        setBorder( BorderFactory.createTitledBorder( "Observables" ) );
        final JCheckBox x = new JCheckBox( "<X>" );
        x.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayXExpectation( x.isSelected() );
            }
        } );
        addControl( x );

        final JCheckBox y = new JCheckBox( "<Y>" );
        y.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayYExpectation( y.isSelected() );
            }
        } );
        addControl( y );

//        final JCheckBox c = new JCheckBox( "collapse-to" );
//        c.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                getSchrodingerPanel().getWavefunctionGraphic().setDisplayCollapsePoint( c.isSelected() );
//            }
//        } );
//        addControl( c );

    }

    private SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }
}
