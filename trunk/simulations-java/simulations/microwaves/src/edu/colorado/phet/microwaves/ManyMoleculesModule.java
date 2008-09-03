/**
 * Class: ManyMoleculesModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.microwaves;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.microwaves.coreadditions.chart.StripChartDelegate;
import edu.colorado.phet.microwaves.coreadditions.collision.Box2D;
import edu.colorado.phet.microwaves.model.Microwave;
import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.view.DipoleStripChartSubject;
import edu.colorado.phet.microwaves.view.MicrowaveStripCharSubject;
import edu.colorado.phet.microwaves.view.WaterMoleculeGraphic;
import edu.colorado.phet.util_microwaves.StripChart;

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

}
