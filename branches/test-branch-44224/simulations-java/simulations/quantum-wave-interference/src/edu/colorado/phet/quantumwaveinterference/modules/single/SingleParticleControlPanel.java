/*  */
package edu.colorado.phet.quantumwaveinterference.modules.single;

import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.controls.*;
import edu.colorado.phet.quantumwaveinterference.model.Detector;

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
 */

public class SingleParticleControlPanel extends QWIControlPanel {
    private IVisualizationPanel particleVisPanel;
    private IVisualizationPanel photonVisPanel;
    private VisualizationPanelContainer visPanel;
    private SingleParticleModule singleParticleModule;

    public SingleParticleControlPanel( final SingleParticleModule singleParticleModule ) {
        super( singleParticleModule );
        this.singleParticleModule = singleParticleModule;
        AdvancedPanel potentialPanel = new AdvancedPanel( QWIResources.getString( "controls.barriers.show" ), QWIResources.getString( "controls.barriers.hide" ) );
        potentialPanel.addControlFullWidth( new PotentialPanel( singleParticleModule ) );
        AdvancedPanel detectorPanel = new AdvancedPanel( QWIResources.getString( "controls.detectors.show" ), QWIResources.getString( "controls.detectors.hide" ) );
        detectorPanel.addControlFullWidth( new DetectorPanel( singleParticleModule ) );

        JButton createDetectorArray = new JButton( QWIResources.getString( "create.detector.array" ) );
        createDetectorArray.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                createDetectorArray();
            }
        } );

        final ModelSlider modelSlider = new ModelSlider( QWIResources.getString( "dectector.prob.scale" ), "", 0, 100, Detector.getProbabilityScaleFudgeFactor() );
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
        addControl( potentialPanel );
        addControl( detectorPanel );

//        final JCheckBox rapid = new JCheckBox( "Rapid", false );
//        rapid.setBorder( BorderFactory.createLineBorder( Color.blue ) );
//        rapid.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                singleParticleModule.setRapid( rapid.isSelected() );
//            }
//        } );
//        addControl( rapid );
//        final LabeledTextField labeledTextField = new LabeledTextField( "M.P.D.", singleParticleModule.getIntensityManager().getMinimumProbabilityForDetection() );

//        final LabeledTextField labeledTextField = new LabeledTextField( "<html>Minimum<br>probability<br>for<br>detection</html>", singleParticleModule.getIntensityManager().getMinimumProbabilityForDetection() );
//        labeledTextField.setBorder( BorderFactory.createLineBorder( Color.blue ) );
//        labeledTextField.addListener( new LabeledTextField.Listener() {
//            public void valueChanged() {
//                singleParticleModule.getIntensityManager().setMinimumProbabilityForDetection( labeledTextField.getValue() );
//            }
//        } );
//        addControl( labeledTextField );

        //developer control
//        addControl( new FadeRateControl( singleParticleModule ) );

        setPreferredWidth( doubleSlitPanel.getControls().getPreferredSize().width + 10 );
    }

    static class FadeRateControl extends JPanel {
        private SingleParticleModule singleParticleModule;

        public FadeRateControl( final SingleParticleModule singleParticleModule ) {
            this.singleParticleModule = singleParticleModule;
            final ModelSlider modelSlider = new ModelSlider( "Fade Delay", "", 1, 1000, singleParticleModule.getSchrodingerPanel().getDetectorSheetPNode().getFadeDelay() );
            add( modelSlider );
            modelSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    singleParticleModule.getSchrodingerPanel().getDetectorSheetPNode().setFadeDelay( (int)modelSlider.getValue() );
                }
            } );
            setBorder( BorderFactory.createLineBorder( Color.blue ) );
        }
    }

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
