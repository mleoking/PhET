/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.modules.intensity.IntensityControlPanel;

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

public class DGControlPanel extends IntensityControlPanel {
    private DGModule dgModule;

    public DGControlPanel( DGModule dgModule ) {
        super( dgModule );
        setupDG();
        this.dgModule = dgModule;
    }

    private void setupDG() {
        DGModel dgModel = new DGModel( getDiscreteModel() );
        addControlFullWidth( new AtomLatticeControlPanel( dgModel ) );
    }

    protected void addMeasuringTools() throws IOException {
        addRulerPanel();
        addProtractorPanel();
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
