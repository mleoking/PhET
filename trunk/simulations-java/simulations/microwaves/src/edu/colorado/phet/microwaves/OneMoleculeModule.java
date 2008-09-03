/**
 * Class: ManyMoleculesModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.microwaves;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.microwaves.coreadditions.MessageFormatter;
import edu.colorado.phet.microwaves.coreadditions.chart.StripChartDelegate;
import edu.colorado.phet.microwaves.model.Microwave;
import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.view.DipoleStripChartSubject;
import edu.colorado.phet.microwaves.view.MicrowaveStripCharSubject;
import edu.colorado.phet.microwaves.view.WaterMoleculeGraphic;
import edu.colorado.phet.util_microwaves.StripChart;

import javax.swing.*;
import java.awt.*;

public class OneMoleculeModule extends MicrowaveModule {

    private WaterMolecule molecule;


    public OneMoleculeModule() {
        super( MessageFormatter.format( SimStrings.get( "ModuleTitle.OneMoleculeModule" ) ) );
    }

    protected void init() {
        super.init();

        molecule = new WaterMolecule();

        molecule.setLocation( getMicrowaveModel().getOven().getMinX() + ( getMicrowaveModel().getOven().getMaxX() - getMicrowaveModel().getOven().getMinX() ) / 2,
                              getMicrowaveModel().getOven().getMinY() + ( getMicrowaveModel().getOven().getMaxY() - getMicrowaveModel().getOven().getMinY() ) / 2 );

        molecule.setDipoleOrientation( 0 );
//        molecule.setDipoleOrientation( 3 * Math.PI / 2 );
//            molecule.setOmega( -0.01 );
//            molecule.setAlpha( 0.0001);
//        molecule.setVelocity( 0.1f, 0f );
        getMicrowaveModel().addPolarBody( molecule );
        WaterMoleculeGraphic moleculeGraphic = new WaterMoleculeGraphic( molecule, getModelViewTransform() );
        getApparatusPanel().addGraphic( moleculeGraphic, 5 );

    }

    public void activate( PhetApplication app ) {

        // Create and display a dialog with strip charts for the microwave intensity
        // and the orientation of the water molecule
        JFrame frame = app.getApplicationView().getPhetFrame();
//        StripChartDialog stripChartDialog = new StripChartDialog( frame, muWave, molecule );
//        GraphicsUtil.centerDialogInParent( stripChartDialog );
//        stripChartDialog.show();
    }

    public void deactivate( PhetApplication app ) {
    }

    //
    // Inner classes
    //

    private class StripChartDialog extends JDialog {

        StripChartDialog( JFrame frame, Microwave muWave, WaterMolecule molecule ) {
            super( frame );
            setTitle( SimStrings.get( "OneMoleculeModule.StripChartDialogTitle" ) );
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
