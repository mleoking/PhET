/**
 * Class: ManyMoleculesModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.microwave;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.coreadditions.chart.StripChartDelegate;
import edu.colorado.phet.coreadditions.collision.Box2D;
import edu.colorado.phet.microwave.model.Microwave;
import edu.colorado.phet.microwave.model.WaterMolecule;
import edu.colorado.phet.microwave.view.DipoleStripChartSubject;
import edu.colorado.phet.microwave.view.MicrowaveStripCharSubject;
import edu.colorado.phet.microwave.view.WaterMoleculeGraphic;
import edu.colorado.phet.util.StripChart;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;

public class ManyMoleculesModule extends MicrowaveModule {

    public ManyMoleculesModule() {
        super( SimStrings.get( "ModuleTitle.ManyMoleculesModule" ) );
    }

    protected void init() {
        super.init();

        // Put a bunch of water molecules randomly on the screen. Make sure they don't overlap
        // so the collision mechanics stay sane
        WaterMolecule[] molecules = new WaterMolecule[MicrowaveConfig.s_numWaterMoleculesPlaceRandomly];
        Box2D oven = getMicrowaveModel().getOven();

        for( int i = 0; i < MicrowaveConfig.s_numWaterMoleculesPlaceRandomly; i++ ) {
            WaterMolecule molecule = new WaterMolecule();
            double x = -1;
            while( x < oven.getMinX() + WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2
                    || x > oven.getMaxX() - ( WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2 ) ) {
                x = Math.random() * oven.getMaxX();
            }
            double y = -1;
            while( y < oven.getMinY() + WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2
                    || y > oven.getMaxY() - ( WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2 ) ) {
                y = Math.random() * oven.getMaxY();
            }
            molecule.setLocation( x, y );
            molecules[i] = molecule;

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
            setTitle( SimStrings.get( "ManyMoleculesModule.StripChartDialogTitle" ) );
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
