/*  */
package edu.colorado.phet.waveinterference.tests;

import java.awt.*;

import edu.colorado.phet.waveinterference.WaveInterferenceModelUnits;
import edu.colorado.phet.waveinterference.ModuleApplication;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.view.*;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:35:59 AM
 */

public class TestIntensityWithSlits extends TestTopView {
    public TestIntensityWithSlits() {
        super( "Slits" );
        SlitPotential slitPotential = new SlitPotential( getWaveModel() );
//        VerticalDoubleSlit potential = new VerticalDoubleSlit( getWaveModel().getWidth(), getWaveModel().getHeight(), 40, 5, 10, 15, 100 );
        getWaveModel().setPotential( slitPotential );
        SlitPotentialGraphic slitPotentialGraphic = new SlitPotentialGraphic( slitPotential, getLatticeScreenCoordinates() );
        getPhetPCanvas().addScreenChild( slitPotentialGraphic );
        getOscillator().setAmplitude( 2 );
        getControlPanel().addControlFullWidth( new SlitControlPanel( slitPotential, new WaveInterferenceScreenUnits( new WaveInterferenceModelUnits(), getLatticeScreenCoordinates() ) ) );
        getWaveModelGraphic().setColorMap( new IndexColorMap( getLattice(), Color.blue, new WaveValueReader.Abs() ) );
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestIntensityWithSlits() );
    }
}
