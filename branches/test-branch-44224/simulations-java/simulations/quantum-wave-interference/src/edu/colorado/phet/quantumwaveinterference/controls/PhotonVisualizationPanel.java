/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.colorado.phet.quantumwaveinterference.view.colormaps.WaveValueAccessor;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.ComplexColorMap;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.GrayscaleColorMap;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.MagnitudeColorMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 17, 2005
 * Time: 7:40:43 PM
 */

public class PhotonVisualizationPanel extends VerticalLayoutPanel implements IVisualizationPanel {
    private QWIPanel QWIPanel;
    private VisButton grayMag;
    private VisButton realGray;
    private VisButton[] visButton;
//    private JRadioButton phaseColorRadioButton;

    public PhotonVisualizationPanel( QWIPanel QWIPanel ) {
        this.QWIPanel = QWIPanel;

        setBorder( BorderFactory.createTitledBorder( QWIResources.getString( "controls.em-wave-display" ) ) );
        ButtonGroup buttonGroup = new ButtonGroup();

        grayMag = createVisualizationButton( QWIResources.getString( "controls.em-wave-display.intensity" ), new MagnitudeColorMap(), new WaveValueAccessor.Magnitude(), true, buttonGroup );
        addFullWidth( grayMag );

        realGray = createVisualizationButton( QWIResources.getString( "controls.em-wave-display.e-field" ), new GrayscaleColorMap.Real(), new WaveValueAccessor.Real(), false, buttonGroup );
        addFullWidth( realGray );

//        JRadioButton complexGray = createVisualizationButton( "Imaginary Part        ", new GrayscaleColorMap.Imaginary(), new WaveValueAccessor.Imag(), false, buttonGroup );
//        addFullWidth( complexGray );
//
//        phaseColorRadioButton = createVisualizationButton( "Phase Color", new VisualColorMap3(), new WaveValueAccessor.Magnitude(), false, buttonGroup );
//        addFullWidth( phaseColorRadioButton );
        visButton = new VisButton[]{grayMag, realGray};
    }

    private VisButton createVisualizationButton( String s, final ComplexColorMap colorMap, final WaveValueAccessor waveValueAccessor, boolean b, ButtonGroup buttonGroup ) {
        VisButton radioButton = new VisButton( s, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                QWIPanel.setVisualizationStyle( colorMap, waveValueAccessor );
            }
        } );
        buttonGroup.add( radioButton );
        radioButton.setSelected( b );
        return radioButton;
    }

    public Component getPanel() {
        return this;
    }

    public void applyChanges() {
        for( int i = 0; i < visButton.length; i++ ) {
            VisButton button = visButton[i];
            if( button.isSelected() ) {
                button.fireEvent();
            }
        }
    }
}
