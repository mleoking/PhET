/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:22:45 PM
 */

public class TravoltageModule extends PiccoloModule {
    private TravoltagePanel travoltagePanel;
    private TravoltageModel travoltageModel;
    private TravoltageAudio travoltageAudio;

    public TravoltageModule() {
        super( "Travoltage", createClock() );
        travoltageModel = new TravoltageModel( this );
        setModel( travoltageModel );

        travoltagePanel = new TravoltagePanel( this );
        setSimulationPanel( travoltagePanel );

        travoltageAudio = new TravoltageAudio( travoltageModel );

        getLegNode().addListener( new PickUpElectrons( this, getLegNode() ) );
        travoltageModel.addModelElement( new SparkManager( getArmNode(), getDoorknobNode(), travoltageModel.getJadeElectronSet(), this ) );

        setClockControlPanel( null );
        //debug
//        travoltageModel.addModelElement( new ModelElement() {
//            public void stepInTime( double dt ) {
//                pickUpElectron();
//            }
//        } );
    }

    public void activate() {
        super.activate();
        getTravoltagePanel().showHelpBalloon();
    }

    private DoorknobNode getDoorknobNode() {
        return travoltagePanel.getTravoltageRootNode().getDoorknobNode();
    }

    public LegNode getLegNode() {
        return travoltagePanel.getTravoltageRootNode().getTravoltageBodyNode().getLegNode();
    }

    public TravoltagePanel getTravoltagePanel() {
        return travoltagePanel;
    }

    public TravoltageModel getTravoltageModel() {
        return travoltageModel;
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1.0 );
    }

    public void pickUpElectron() {
        travoltagePanel.getTravoltageRootNode().pickUpElectron();
    }

    public ElectronSetNode getElectronSetNode() {
        return travoltagePanel.getElectronSetNode();
    }

    public ArmNode getArmNode() {
        return travoltagePanel.getTravoltageRootNode().getTravoltageBodyNode().getArmNode();
    }

    public void addElectron( JadeElectron jadeElectron ) {
        travoltageModel.addElectron( jadeElectron );
    }

    public void fireSpark() {
        travoltageModel.startSpark();
//        travoltagePanel.setSparkVisible( true );
    }

}
