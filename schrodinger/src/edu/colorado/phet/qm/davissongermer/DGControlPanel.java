/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.qm.controls.ClearButton;
import edu.colorado.phet.qm.controls.RulerPanel;
import edu.colorado.phet.qm.model.DiscreteModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 10:54:25 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class DGControlPanel extends ControlPanel {
    private DGModule dgModule;

    public DGControlPanel( DGModule dgModule ) {
        super( dgModule );
        this.dgModule = dgModule;
        addRulerPanel();
        addProtractorPanel();
        addControl( new ClearButton( dgModule.getSchrodingerPanel() ) );

        DGModel dgModel = new DGModel( getDiscreteModel() );
        addControlFullWidth( new AtomLatticeControlPanel( dgModel ) );
    }

    private DiscreteModel getDiscreteModel() {
        return dgModule.getDiscreteModel();
    }

    private void addRulerPanel() {
        try {
            RulerPanel rulerPanel = new RulerPanel( dgModule.getSchrodingerPanel() );
            addControl( rulerPanel );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void addProtractorPanel() {
        final JCheckBox protractor = new JCheckBox( "Protractor", false );
        protractor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dgModule.setProtractorVisible( protractor.isSelected() );
            }
        } );
        addControl( protractor );
    }
}
