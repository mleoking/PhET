/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 5, 2003
 * Time: 10:09:34 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.idealgas.IdealGasApplication;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.LightSpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

public class GasMoleculeFactory {

    private IdealGasModel model;
    protected Class speciesClass;
    private double initialEnergy;


    public GasMolecule create( IdealGasModel model, double initialEnergy ) {
        this.initialEnergy = ( initialEnergy == 0 ? DEFAULT_ENERGY : initialEnergy );
        return create( model, model.getCurrentGasSpecies() );
    }

    public GasMolecule create( IdealGasModel model ) {
        return create( model, model.getCurrentGasSpecies() );
    }

    public GasMolecule create( IdealGasModel model,
                            Class speciesClass ) {
        this.model = model;
        this.speciesClass = speciesClass;
        this.initialEnergy = DEFAULT_ENERGY;
        return pumpGasMolecule();
    }

    /**
     *
     */
    protected GasMolecule pumpGasMolecule() {

        s_intakePortOffsetY *= -1;

        // TODO: Make a gas factory or something. We only have a class to work with right now
        // and we can't call newInstance()
        GasMolecule newMolecule = null;

        // Compute the average energy of the gas in the box. It will be used to compute the
        // velocity of the new molecule
        if( speciesClass == LightSpecies.class ) {

            // Create the new molecule with no velocity. We will compute and assign it next
            newMolecule = new LightSpecies(
                    new Point2D.Double( s_intakePortX, s_intakePortY + s_intakePortOffsetY * 5 ),
                    new Vector2D.Double( 0, 0 ),
                    new Vector2D.Double( 0, 0 ),
                    5.0f );
        }
        else if( speciesClass == HeavySpecies.class ) {

            // Create the new molecule with no velocity. We will compute and assign it next
            newMolecule = new HeavySpecies(
                    new Point2D.Double( s_intakePortX, s_intakePortY + s_intakePortOffsetY * 5 ),
                    new Vector2D.Double( 0, 0 ),
                    new Vector2D.Double( 0, 0 ),
                    5.0 );
        }
        else {
            throw new RuntimeException( "No gas species set in application" );
        }

        double pe = model.getBodyEnergy( newMolecule );
//        double pe = physicalSystem.getBodyEnergy( newMolecule );
        double vSq = 2 * ( this.initialEnergy - pe ) / newMolecule.getMass();
        if( vSq <= 0 ) {
            System.out.println( "vSq <= 0 in PumpMoleculeCmd.pumpGasMolecule" );
        }
        float v = vSq > 0 ? (float)Math.sqrt( vSq ) : 10;
        float theta = (float)Math.random() * PI_OVER_2 - PI_OVER_4;

        // xV must be negative so that molecules move away from the intake port
        // Set the velocity twice, so the previous velocity is set to be
        // the same
        float xV = -(float)Math.abs( v * Math.cos( theta ) );
        float yV = v * (float)Math.sin( theta );
        newMolecule.setVelocity( xV, yV );
        newMolecule.setVelocity( xV, yV );

        newMolecule.setModel( model );
        model.addModelElement( newMolecule );

        return newMolecule;
    }

    //
    // Static fields and methods
    //
    protected static final float PI_OVER_2 = (float)Math.PI / 2;
    protected static final float PI_OVER_4 = (float)Math.PI / 4;
    protected static final float MAX_V = -30;

    protected static final float DEFAULT_ENERGY = 15000;
    // Coordinates of the intake port on the box
    protected static final float s_intakePortX = 430 + IdealGasConfig.X_BASE_OFFSET;
    protected static final float s_intakePortY = 400 + IdealGasConfig.Y_BASE_OFFSET;
    // Offset for dithering the initial position of particles pumped into the box
    private static float s_intakePortOffsetY = 1;
}
