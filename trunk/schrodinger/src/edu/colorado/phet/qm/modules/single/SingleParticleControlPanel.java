/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.controls.*;
import edu.colorado.phet.qm.model.Detector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:20:42 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleControlPanel extends QWIControlPanel {
    private IVisualizationPanel particleVisPanel;
    private IVisualizationPanel photonVisPanel;
    private VisualizationPanelContainer visPanel;

    public SingleParticleControlPanel( SingleParticleModule singleParticleModule ) {
        super( singleParticleModule );
        AdvancedPanel advancedPanel = new AdvancedPanel( "Advanced>>", "Hide Advanced<<" );
        VerticalLayoutPanel detectorPanel = new DetectorPanel( singleParticleModule );
        advancedPanel.addControlFullWidth( new PotentialPanel( singleParticleModule ) );
        advancedPanel.addControlFullWidth( detectorPanel );

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

        photonVisPanel = new PhotonVisualizationPanel( getSchrodingerPanel() );
        particleVisPanel = new ParticleVisualizationPanel( getSchrodingerPanel() );

        visPanel = new VisualizationPanelContainer( photonVisPanel, particleVisPanel );
        singleParticleModule.addListener( new QWIModule.Listener() {
            public void deactivated() {
            }

            public void activated() {
            }

            public void beamTypeChanged() {
                updateVisualizationPanel();
            }

        } );
        updateVisualizationPanel();
        ExpandableDoubleSlitPanel doubleSlitPanel = new ExpandableDoubleSlitPanel( singleParticleModule );

        addSeparator();
        addSpacer();
        getContentPanel().setAnchor( GridBagConstraints.CENTER );
        addControl( new ResetButton( singleParticleModule ) );
        addControl( new ClearButton( singleParticleModule.getSchrodingerPanel() ) );
        getContentPanel().setAnchor( GridBagConstraints.WEST );
        addSpacer();
        addSeparator();
        addSpacer();
        addControl( visPanel );
        addControl( doubleSlitPanel );
        addControl( advancedPanel );

        setPreferredWidth( doubleSlitPanel.getControls().getPreferredSize().width + 10 );
    }

//    private void updateVisualizationPanel() {
//        colorPanelParticle.setPhaseColorEnabled( !isPhoton() );
//
//    }

    private void updateVisualizationPanel() {
        visPanel.setContent( isPhoton() ? photonVisPanel : particleVisPanel );
        revalidate();
    }

    private boolean isPhoton() {
        return getSchrodingerPanel().getGunGraphic().isPhotonMode();
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
