/*  */
package edu.colorado.phet.waveinterference;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.colorado.phet.waveinterference.view.*;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:15:03 PM
 */

public class WaterControlPanel extends WaveInterferenceControlPanel {
    private WaterModule waterModule;

    public WaterControlPanel( WaterModule waterModule ) {
        this.waterModule = waterModule;
        addControl( new MeasurementControlPanel( waterModule.getMeasurementToolSet() ) );

        addControl( new DetectorSetControlPanel( WIStrings.getString( "water.level" ), waterModule.getIntensityReaderSet(), waterModule.getWaterSimulationPanel(), waterModule.getWaveModel(), waterModule.getLatticeScreenCoordinates(), waterModule.getClock() ) );

        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );
        addControl( new ResetModuleControl( waterModule ) );
        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

        addControl( new WaveRotateControl( waterModule.getRotationWaveGraphic() ) );
        addVerticalSpace();

        addControl( new MultiDripControlPanel( waterModule.getMultiDrip(), waterModule.getScreenUnits() ) );
//        addControlFullWidth( new VerticalSeparator() );

        addControl( new SlitControlPanel( waterModule.getSlitPotential(), waterModule.getScreenUnits() ) );

        addControl( new AddWallPotentialButton( waterModule.getWaveInterferenceModel() ) );
    }

    private class AddWallPotentialButton extends JButton {
        private AddWallPotentialButton( final WaveInterferenceModel waveInterferenceModel ) {
            super( "Add Wall Potential" );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    waveInterferenceModel.getWallPotentialGraphic().addPotential( new WallPotential( new Point( 10, 10 ), new Point( 50, 50 ) ,waveInterferenceModel.getWaveModel()) );
                }
            } );
        }

    }

}
