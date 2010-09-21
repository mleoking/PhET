/*  */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.FaucetGraphic;
import edu.colorado.phet.waveinterference.view.WaveSideViewFull;
import edu.colorado.phet.waveinterference.ModuleApplication;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 11:03:18 PM
 */

public class TestSideViewWaterDrops extends TestTopView {
    private FaucetGraphic faucetGraphic;
    private WaveSideViewFull waveSideView;

    public TestSideViewWaterDrops() {
        super( "Side View Water Drops" );
        waveSideView = new WaveSideViewFull( getWaveModel(), getLatticeScreenCoordinates() );
        getWaveModelGraphic().setVisible( false );
        getPhetPCanvas().addScreenChild( waveSideView );
        waveSideView.setOffset( 0, 400 );

        faucetGraphic = new FaucetGraphic( getWaveModel(), getOscillator(), getLatticeScreenCoordinates() );
        getPhetPCanvas().addScreenChild( faucetGraphic );
    }

    protected void step() {
        super.step();
        faucetGraphic.step();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestSideViewWaterDrops() );
    }
}
