/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.controls.DetectorPanel;
import edu.colorado.phet.qm.controls.InverseSlitsCheckbox;
import edu.colorado.phet.qm.controls.SchrodingerControlPanel;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.view.swing.VisualizationPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:20:42 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleControlPanel extends SchrodingerControlPanel {
    public SingleParticleControlPanel( SingleParticleModule singleParticleModule ) {
        super( singleParticleModule );
        AdvancedPanel advancedPanel = new AdvancedPanel( "Advanced>>", "Hide Advanced<<" );
        VerticalLayoutPanel detectorPanel = new DetectorPanel( singleParticleModule );
        advancedPanel.addControlFullWidth( super.createPotentialPanel( singleParticleModule ) );
        advancedPanel.addControlFullWidth( detectorPanel );
        advancedPanel.addControlFullWidth( new InverseSlitsCheckbox( getSchrodingerPanel() ) );

        JButton createDetectorArray = new JButton( "Create Detector Array" );
        createDetectorArray.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                createDetectorArray();
            }
        } );

        final ModelSlider modelSlider = new ModelSlider( "Dectector prob-scale", "", 0, 100, Detector.getProbabilityScaleFudgeFactor() );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Detector.setProbabilityScaleFudgeFactor( modelSlider.getValue() );
            }
        } );
        modelSlider.setModelTicks( new double[]{0, 50, 100} );
        getAdvancedPanel().addControl( createDetectorArray );
        getAdvancedPanel().addControlFullWidth( modelSlider );

        VerticalLayoutPanel colorPanel = new VisualizationPanel( getSchrodingerPanel() );
        addControlFullWidth( colorPanel );

//        super.addSlitCheckbox();
        super.addResetButton();
        addControlFullWidth( advancedPanel );
    }

    private void createDetectorArray() {
        createDetectorArray( 20 );
    }

    private void createDetectorArray( int width ) {
        int height = width;
        int nx = getDiscreteModel().getGridWidth() / width;
        int ny = getDiscreteModel().getGridHeight() / height;

        for( int i = 0; i < nx; i++ ) {
            for( int j = 0; j < ny; j++ ) {
                int x = i * width;
                int y = j * height;
                Detector detector = new Detector( getDiscreteModel(), x, y, width, height );
                getSchrodingerPanel().getSchrodingerModule().addDetector( detector );
            }

        }
    }

    private VerticalLayoutPanel createExpectationPanel() {
        return new ObservablePanel( getSchrodingerPanel() );
    }
}
