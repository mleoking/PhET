/*  */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.ScreenControlPanel;
import edu.colorado.phet.waveinterference.view.ScreenNode;
import edu.colorado.phet.waveinterference.ModuleApplication;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:17:23 AM
 */

public class TestScreenGraphic extends TestWaveColor {
    private ScreenNode screenGraphic;

    public TestScreenGraphic() {
        super( "Screen Graphic" );
        getWaveModel().setSize( 50, 50 );
        screenGraphic = new ScreenNode( getWaveModel(), getLatticeScreenCoordinates(), getWaveModelGraphic() );
        screenGraphic.setIntensityScale( 2.5 );
        getPhetPCanvas().addScreenChild( screenGraphic );
        getPhetPCanvas().removeScreenChild( getWaveModelGraphic() );
        getPhetPCanvas().addScreenChild( getWaveModelGraphic() );
        getWaveModelGraphic().setOffset( 100, 100 );

        getControlPanel().addControl( new ScreenControlPanel( screenGraphic ) );
    }

    protected void step() {
        super.step();
        screenGraphic.updateScreen();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestScreenGraphic() );
    }
}
