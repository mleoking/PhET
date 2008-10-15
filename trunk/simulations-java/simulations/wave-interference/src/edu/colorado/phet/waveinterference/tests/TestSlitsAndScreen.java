/*  */
package edu.colorado.phet.waveinterference.tests;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.waveinterference.WaveInterferenceModelUnits;
import edu.colorado.phet.waveinterference.ModuleApplication;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.view.*;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:35:59 AM
 */
//TODO this was thrown together quickly and warrants more attention
public class TestSlitsAndScreen extends TestTopView {
    private ScreenNode screenGraphic;
    private SRRWavelengthSliderComponent slider;

    public TestSlitsAndScreen() {
        super( "Slits & Screen" );

        screenGraphic = new ScreenNode( getWaveModel(), getLatticeScreenCoordinates(), getWaveModelGraphic() );
        getPhetPCanvas().addScreenChild( screenGraphic );
        getPhetPCanvas().removeScreenChild( getWaveModelGraphic() );
        getPhetPCanvas().addScreenChild( getWaveModelGraphic() );//put it on top
        getWaveModelGraphic().setOffset( 100, 100 );

        SlitPotential slitPotential = new SlitPotential( getWaveModel() );
        getWaveModel().setPotential( slitPotential );
        SlitPotentialGraphic slitPotentialGraphic = new SlitPotentialGraphic( slitPotential, getLatticeScreenCoordinates() );
        getPhetPCanvas().addScreenChild( slitPotentialGraphic );
        getOscillator().setAmplitude( 2 );
        getControlPanel().addControlFullWidth( new SlitControlPanel( slitPotential, new WaveInterferenceScreenUnits( new WaveInterferenceModelUnits(), getLatticeScreenCoordinates() ) ) );

        getControlPanel().addControl( new ScreenControlPanel( screenGraphic ) );
        slider = new SRRWavelengthSliderComponent();
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateColor();
            }
        } );
        getControlPanel().addControl( slider );
        updateColor();
        getOscillator().setAmplitude( 2.5 );
    }

    protected void updateColor() {
        getWaveModel().clear();
        PhotonEmissionColorMap colorMap = new PhotonEmissionColorMap( getWaveModel(), slider.getColor() );
        setColorMap( colorMap );
    }

    protected void setColorMap( ColorMap colorMap ) {
        getWaveModelGraphic().setColorMap( colorMap );
        if ( screenGraphic != null ) {
            screenGraphic.setColorMap( colorMap );
        }
    }

    protected void step() {
        super.step();
        screenGraphic.updateScreen();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestSlitsAndScreen() );
    }
}
