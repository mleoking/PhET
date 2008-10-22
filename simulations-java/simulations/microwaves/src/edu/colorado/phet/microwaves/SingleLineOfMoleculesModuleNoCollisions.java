/**
 * Class: ManyMoleculesModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.microwaves;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.microwaves.coreadditions.MessageFormatter;
import edu.colorado.phet.microwaves.coreadditions.chart.StripChart;
import edu.colorado.phet.microwaves.coreadditions.chart.StripChartDelegate;
import edu.colorado.phet.microwaves.coreadditions.collision.Box2D;
import edu.colorado.phet.microwaves.model.Microwave;
import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.view.DipoleStripChartSubject;
import edu.colorado.phet.microwaves.view.MicrowaveStripCharSubject;
import edu.colorado.phet.microwaves.view.WaterMoleculeGraphic;

public class SingleLineOfMoleculesModuleNoCollisions extends MicrowaveModule {

    private int fieldWidth = 1000;
    private int fieldHeight = 700;
    //    private MicrowaveModel model;
    //    private Microwave muWave;
    private WaterMolecule molecule;
    private JDialog stripChartDlg;


    public SingleLineOfMoleculesModuleNoCollisions() {
        super( MessageFormatter.format( SimStrings.get( "ModuleTitle.SingleLineOfMoleculesModuleNoCollisions" ) ) );
    }

    protected void init() {
        super.init();

        // Put a line of water molecules across the middle of the screen
        Box2D oven = this.getMicrowaveModel().getOven();
        for ( int x = (int) ( oven.getMinX() + WaterMolecule.s_oxygenRadius + WaterMolecule.s_hydrogenRadius * 2 );
              x < (int) ( oven.getMaxX() - WaterMolecule.s_oxygenRadius - WaterMolecule.s_hydrogenRadius * 2 );
              x += WaterMolecule.s_oxygenRadius * 2 + WaterMolecule.s_hydrogenRadius * 3 ) {
            molecule = new WaterMolecule();
            molecule.setLocation( x, 250 );

            molecule.setDipoleOrientation( Math.random() * Math.PI * 2 );
            getMicrowaveModel().addPolarBody( molecule );
            WaterMoleculeGraphic moleculeGraphic = new WaterMoleculeGraphic( molecule, getModelViewTransform() );
            getApparatusPanel().addGraphic( moleculeGraphic, 5 );
        }

    }

    public void activate( PhetApplication app ) {

        // Create and display a dialog with strip charts for the microwave intensity
        // and the orientation of the water molecule
        JFrame frame = app.getApplicationView().getPhetFrame();

//        JFrame frame = PhetApplication.instance().getApplicationView().getPhetFrame();
//        JDialog stripChartDlg = new JDialog( frame );
        stripChartDlg = new JDialog( frame );
        stripChartDlg.getContentPane();
        final StripChart stripChart = new StripChart( 200, 100, 0, 100,
                                                      -MicrowaveConfig.s_maxAmp,
                                                      MicrowaveConfig.s_maxAmp,
                                                      1 );
        muWave.addObserver( new Observer() {
            public void update( Observable o, Object arg ) {
                if ( o instanceof Microwave ) {
                    Microwave microwave = (Microwave) o;
                    stripChart.addDatum( microwave.getAmplitude()[0], 10 );
                }
            }
        } );
        stripChartDlg.getContentPane().add( stripChart );
        stripChartDlg.pack();
        stripChartDlg.show();
//        StripChartDialog stripChartDialog = new StripChartDialog( frame, muWave, molecule );
//        GraphicsUtil.centerDialogInParent( stripChartDialog );
//        stripChartDialog.show();
    }

    public void deactivate( PhetApplication app ) {
        stripChartDlg.dispose();
    }

//    public void toggleMicrowave() {
//        if( muWave.getFrequency() == 0 ) {
//            muWave.setFrequency( (float)MicrowaveConfig.s_initFreq );
//            muWave.setMaxAmplitude( 1.0f );
//        }
//        else {
//            muWave.setFrequency( 0f );
//            muWave.setMaxAmplitude( 0f );
//        }
//    }
//

    //
    // Inner classes
    //

    private class StripChartDialog extends JDialog {

        StripChartDialog( JFrame frame, Microwave muWave, WaterMolecule molecule ) {
            super( frame );
            setTitle( SimStrings.get( "SingleLineOfMoleculesModuleNoCollisions.StripChartDialogTitle" ) );
            StripChart waterChart = new StripChart( 200, 100, 0, 100, 0, Math.PI * 2, 0.01 );

            DipoleStripChartSubject dscs = new DipoleStripChartSubject( molecule );
            new StripChartDelegate( dscs, waterChart );

            StripChart waveChart = new StripChart( 200, 100, 0, 100, -muWave.getMaxAmplitude(), muWave.getMaxAmplitude(), 0.01 );
//            StripChart waveChart = new StripChart( 200, 100, 0, 100, -Math.PI, Math.PI, 0.01 );
            MicrowaveStripCharSubject escs = new MicrowaveStripCharSubject( muWave );
            new StripChartDelegate( escs, waveChart );

            getContentPane().setLayout( new GridLayout( 2, 1, 10, 10 ) );
            getContentPane().add( waveChart );
            getContentPane().add( waterChart );
            pack();
        }

    }

}
