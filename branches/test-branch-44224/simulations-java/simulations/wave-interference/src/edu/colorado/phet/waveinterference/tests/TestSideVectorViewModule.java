/*  */
package edu.colorado.phet.waveinterference.tests;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.waveinterference.EBFieldGraphic;
import edu.colorado.phet.waveinterference.ModuleApplication;
import edu.colorado.phet.waveinterference.view.ExplicitCoordinates;
import edu.colorado.phet.waveinterference.view.WaveSideView;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:02:57 AM
 */
public class TestSideVectorViewModule extends BasicWaveTestModule {
    private WaveSideView waveSideView;

    public TestSideVectorViewModule() {
        super( "Side View" );

        waveSideView = new EBFieldGraphic( getWaveModel(), new ExplicitCoordinates(), 3 );
        waveSideView.setOffset( 0, 300 );

        getPhetPCanvas().addScreenChild( waveSideView );

        double distBetweenCells = waveSideView.getDistBetweenCells();
        System.out.println( "distBetweenCells = " + distBetweenCells );
        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, distBetweenCells );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                waveSideView.setSpaceBetweenCells( cellDim.getValue() );
            }
        } );
        getControlPanel().addControl( cellDim );
//        JButton photonView = new JButton( "Photon View" );
//        photonView.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                getPhetPCanvas().removeScreenChild( waveSideView );
//                waveSideView = new VectorViewGraphic( getLattice(), new ExplicitCoordinates() ,3);
//                waveSideView.setSpaceBetweenCells( cellDim.getValue());
//                waveSideView.setOffset( 0,300);
//                getPhetPCanvas().addScreenChild( waveSideView );
//            }
//        } );
//        getControlPanel().addControl( photonView );
//        setControlPanel( controlPanel );
    }

    protected void step() {
        super.step();
        waveSideView.update();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestSideVectorViewModule() );
    }
}
