/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.modules.intensity.IntensityBeamPanel;
import edu.colorado.phet.quantumwaveinterference.modules.intensity.IntensityModule;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 10:04:32 AM
 */

public class SlitDetectorPanel extends VerticalLayoutPanel {
    private IntensityModule intensityModule;
    private JCheckBox leftSlit;
    private JCheckBox rightSlit;

    public SlitDetectorPanel( final IntensityModule intensityModule ) {
        this.intensityModule = intensityModule;
        leftSlit = new JCheckBox( QWIResources.getString( "controls.slits.left-detector" ) );
        leftSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityModule.setLeftDetectorEnabled( leftSlit.isSelected() );
            }
        } );
        add( leftSlit );

        rightSlit = new JCheckBox( QWIResources.getString( "controls.slits.right-detector" ) );
        rightSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityModule.setRightDetectorEnabled( rightSlit.isSelected() );
            }
        } );
        add( rightSlit );
        intensityModule.getSchrodingerPanel().addListener( new QWIPanel.Adapter() {
            public void inverseSlitsChanged() {
                setButtonsEnabled( !isInverseSlits() );
            }

        } );

        intensityModule.addListener( new IntensityModule.Adapter() {
            public void detectorsChanged() {
                leftSlit.setSelected( intensityModule.isLeftDetectorEnabled() );
                rightSlit.setSelected( intensityModule.isRightDetectorEnabled() );
            }
        } );
        intensityModule.getQWIModel().addListener( new QWIModel.Adapter() {
            public void doubleSlitVisibilityChanged() {
                synchronizeModelState();
            }
        } );
    }

    private boolean isInverseSlits() {
        return intensityModule.getSchrodingerPanel().isInverseSlits();
    }

    private void setButtonsEnabled( boolean enabled ) {
        rightSlit.setEnabled( enabled );
        leftSlit.setEnabled( enabled );
    }

    public IntensityBeamPanel getIntensityPanel() {
        return intensityModule.getIntensityPanel();
    }

    public void synchronizeModelState() {
        intensityModule.setRightDetectorEnabled( rightSlit.isSelected() );
        intensityModule.setLeftDetectorEnabled( leftSlit.isSelected() );
    }

    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        leftSlit.setEnabled( enabled );
        rightSlit.setEnabled( enabled );
    }
}
