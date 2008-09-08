/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.quantumwaveinterference.davissongermer.QWIStrings;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen.DetectorSheetPNode;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen.IntensityManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 30, 2005
 * Time: 9:17:04 AM
 */

public class IntensityScreenPanel extends VerticalLayoutPanel {
    private QWIControlPanel qwiControlPanel;

    public IntensityScreenPanel( QWIControlPanel qwiControlPanel ) {
        this.qwiControlPanel = qwiControlPanel;
        setBorder( BorderFactory.createTitledBorder( QWIStrings.getString( "intensity.screen" ) ) );

        final IntensityManager intensityManager = qwiControlPanel.getModule().getIntensityDisplay();
        JPanel inflationPanel = new HorizontalLayoutPanel();
        final JSpinner probabilityInflation = new JSpinner( new SpinnerNumberModel( intensityManager.getProbabilityScaleFudgeFactor(), 0.0, 1000, 0.1 ) );
        probabilityInflation.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double val = ( (Number)probabilityInflation.getValue() ).doubleValue();
                intensityManager.setProbabilityScaleFudgeFactor( val );
            }
        } );
        inflationPanel.add( new JLabel( QWIStrings.getString( "probability.inflation" ) ) );
        inflationPanel.add( probabilityInflation );
        super.addFullWidth( inflationPanel );

        JPanel pan = new HorizontalLayoutPanel();
        pan.add( new JLabel( QWIStrings.getString( "waveform.decrement" ) ) );
        final JSpinner waveformDec = new JSpinner( new SpinnerNumberModel( intensityManager.getNormDecrement(), 0, 1.0, 0.1 ) );
//        waveformDec.setBorder( BorderFactory.createTitledBorder( "Waveform Decrement" ) );
        waveformDec.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double val = ( (Number)waveformDec.getValue() ).doubleValue();
                intensityManager.setNormDecrement( val );
            }
        } );
        pan.add( waveformDec );
        super.addFullWidth( pan );

        JPanel p3 = new HorizontalLayoutPanel();
        p3.add( new JLabel( QWIStrings.getString( "multiplier" ) ) );
        final JSpinner mult = new JSpinner( new SpinnerNumberModel( intensityManager.getMultiplier(), 0, 1000, 5 ) );
        mult.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                intensityManager.setMultiplier( ( (Number)mult.getValue() ).intValue() );
            }
        } );
        p3.add( mult );
        super.addFullWidth( p3 );

        JPanel p4 = new HorizontalLayoutPanel();
        p4.add( new JLabel( QWIStrings.getString( "opacity" ) ) );
        int opacity = getDetectorSheetPNode().getOpacity();

        opacity=Math.max( opacity, 0);
        opacity=Math.min( opacity, 255);
        final JSpinner transparency = new JSpinner( new SpinnerNumberModel( opacity, 0, 255, 1 ) );
        transparency.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int val = ( (Number)transparency.getValue() ).intValue();
                getDetectorSheetPNode().setOpacity( val );
            }
        } );
        p4.add( transparency );
        addFullWidth( p4 );
    }

    private DetectorSheetPNode getDetectorSheetPNode() {
        return qwiControlPanel.getSchrodingerPanel().getDetectorSheetPNode();
    }
}
