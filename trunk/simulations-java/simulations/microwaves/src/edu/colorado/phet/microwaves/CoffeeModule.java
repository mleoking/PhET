/**
 * Class: CoffeeModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Nov 12, 2003
 */
package edu.colorado.phet.microwaves;


import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.common_microwaves.view.graphics.Graphic;
import edu.colorado.phet.coreadditions_microwaves.graphics.ImageGraphic;
import edu.colorado.phet.microwaves.coreadditions.collision.Box2D;
import edu.colorado.phet.microwaves.model.MicrowaveModel;
import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.view.CoffeeControlPanel;
import edu.colorado.phet.microwaves.view.WaterMoleculeGraphic;

public class CoffeeModule extends MicrowaveModule {
    private Box2D mug;
    private PowerManager powerManager;
    private boolean ovenOn;
    private double powerLevel;
    private CoffeeControlPanel coffeeControlPanel;

    public CoffeeModule() {
        super( MicrowavesResources.getString( "ModuleTitle.CoffeeModule" ) );
    }

    protected void init() {

        super.init();

        // Put the coffee mug on the screen
        try {
            BufferedImage mugBI = ImageLoader.loadBufferedImage( "microwaves/images/coffee-cup-2.gif" );
            Graphic mugGraphic = new ImageGraphic( mugBI, new Point2D.Double( 150, 120 ) );
            getApparatusPanel().addGraphic( mugGraphic, 10 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        mug = new Box2D( new Point2D.Double( 230, 150 ),
                         new Point2D.Double( 380, 320 ) );
        getModel().addModelElement( mug );
        ( (MicrowaveModel) getModel() ).setOven( mug );

        // Put a bunch of water molecules randomly on the screen. Make sure they don't overlap
        // so the collision mechanics stay sane
        WaterMolecule[] molecules = new WaterMolecule[MicrowaveConfig.s_numWaterMoleculesInCoffeeCup * 10];
        Box2D oven = getMicrowaveModel().getOven();

        for ( int i = 0; i < MicrowaveConfig.s_numWaterMoleculesInCoffeeCup; i++ ) {
            WaterMolecule molecule = new WaterMolecule();
            double x = -1;
            while ( x < oven.getMinX() + WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2
                    || x > oven.getMaxX() - ( WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2 ) ) {
                x = Math.random() * oven.getMaxX();
            }
            double y = -1;
            while ( y < oven.getMinY() + WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2
                    || y > oven.getMaxY() - ( WaterMolecule.s_hydrogenOxygenDist + WaterMolecule.s_hydrogenRadius * 2 ) ) {
                y = Math.random() * oven.getMaxY();
            }
            molecule.setLocation( x, y );
            molecule.setVelocity( (float) Math.random() / 5, (float) Math.random() / 5 );
            molecules[i] = molecule;

            molecule.setDipoleOrientation( Math.random() * Math.PI * 2 );
            getMicrowaveModel().addPolarBody( molecule );
            WaterMoleculeGraphic moleculeGraphic = new WaterMoleculeGraphic( molecule, getModelViewTransform() );
            getApparatusPanel().addGraphic( moleculeGraphic, 5 );
            molecule.setVisible( true );
        }

        powerManager = new PowerManager( powerLevel );
        if ( coffeeControlPanel == null ) {
            coffeeControlPanel = new CoffeeControlPanel( this, getMicrowaveModel() );
        }
        setControlPanel( coffeeControlPanel );

        // Set initial conditions
        ovenOn = false;
    }

    public void activate( PhetApplication phetApplication ) {
        super.activate( phetApplication );
        powerManager = new PowerManager( powerLevel );
        powerManager.isAlive();
        powerManager.start();

        // Turn down the damping on the water molecules
//        dampingSave = WaterMolecule.s_b;
//        WaterMolecule.s_b = 0.0012;
    }

    public void deactivate( PhetApplication app ) {
        powerManager.setRunning( false );
//        WaterMolecule.s_b = dampingSave;
    }

    public void setPowerLevel( double powerLevel ) {
        powerManager.setPowerLevel( powerLevel );
    }

    public synchronized void toggleMicrowave() {
        ovenOn = !ovenOn;
    }


    public void reset() {
        super.reset();
        coffeeControlPanel.reset();
//        coffeeControlPanel = new CoffeeControlPanel( this, getMicrowaveModel() );
//        setControlPanel( coffeeControlPanel );
//        PhetApplication.instance().getApplicationView().getBasicPhetPanel().setControlPanel( coffeeControlPanel );
    }

    //
    // Inner classes
    //

    private class PowerManager extends Thread {
        private boolean isRunning = true;
        private double period = 6000;

        PowerManager( double powerLevel ) {
            CoffeeModule.this.powerLevel = powerLevel;
        }

        void setPowerLevel( double powerLevel ) {
            CoffeeModule.this.powerLevel = powerLevel;
        }

        void setRunning( boolean isRunning ) {
            this.isRunning = isRunning;
        }

        public void run() {
            while ( isRunning ) {
                if ( ovenOn ) {
                    double now = System.currentTimeMillis();
                    double t = now % period;
                    if ( t < powerLevel * period ) {
                        turnMicrowaveOn( 0.002, 0.33 );
                    }
                    if ( t > powerLevel * period ) {
                        turnMicrowaveOff();
                    }
                }
                else {
                    turnMicrowaveOff();
                }
                try {
                    this.sleep( 100 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
