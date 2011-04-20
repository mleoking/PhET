// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.photonabsorption.model.molecules;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.photonabsorption.model.Molecule;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionStrategy;
import edu.colorado.phet.common.photonabsorption.model.WavelengthConstants;
import edu.colorado.phet.common.photonabsorption.model.atoms.AtomicBond;
import edu.colorado.phet.common.photonabsorption.model.atoms.NitrogenAtom;
import edu.colorado.phet.common.photonabsorption.model.atoms.OxygenAtom;


/**
 * Class that represents N2O in the model.
 *
 * @author John Blanco
 */
public class N2O extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final double INITIAL_NITROGEN_NITROGEN_DISTANCE = 170; // In picometers.
    private static final double INITIAL_NITROGEN_OXYGEN_DISTANCE = 170; // In picometers.

    // Deflection amounts used for the vibration of the CO2 atoms.  These
    // are calculated such that the actual center of gravity should remain
    // pretty much constant.
    private static final double MAX_CENTER_NITROGEN_DEFLECTION = 50;
    private static final double MAX_SIDE_NITROGEN_DEFLECTION = MAX_CENTER_NITROGEN_DEFLECTION / 2;
    private static final double MAX_OXYGEN_DEFLECTION = MAX_CENTER_NITROGEN_DEFLECTION / 2;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final NitrogenAtom sideNitrogenAtom = new NitrogenAtom();
    private final NitrogenAtom centerNitrogenAtom = new NitrogenAtom();
    private final OxygenAtom oxygenAtom = new OxygenAtom();
    private final AtomicBond nitrogenNitrogenBond = new AtomicBond( sideNitrogenAtom, centerNitrogenAtom, 2 );
    private final AtomicBond nitrogenOxygenBond = new AtomicBond( centerNitrogenAtom, oxygenAtom, 2 );

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public N2O(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( sideNitrogenAtom );
        addAtom( centerNitrogenAtom );
        addAtom( oxygenAtom );
        addAtomicBond( nitrogenNitrogenBond );
        addAtomicBond( nitrogenOxygenBond );

        // Set up the photon wavelengths to absorb.
        setPhotonAbsorptionStrategy( WavelengthConstants.IR_WAVELENGTH, new PhotonAbsorptionStrategy.VibrationStrategy( this ) );

        // Set the initial offsets.
        initializeAtomOffsets();

        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }

    public N2O(){
        this(new Point2D.Double(0, 0));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see edu.colorado.phet.common.photonabsorption.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeAtomOffsets() {
        addInitialAtomCogOffset(centerNitrogenAtom, new Vector2D(0, 0));
        addInitialAtomCogOffset(sideNitrogenAtom, new Vector2D(-INITIAL_NITROGEN_NITROGEN_DISTANCE, 0));
        addInitialAtomCogOffset(oxygenAtom, new Vector2D(INITIAL_NITROGEN_OXYGEN_DISTANCE, 0));

        updateAtomPositions();
    }

    @Override
    public void setVibration( double vibrationRadians ){
        super.setVibration( vibrationRadians );
        double multFactor = Math.sin( vibrationRadians );
        addInitialAtomCogOffset(centerNitrogenAtom, new Vector2D(0, multFactor * MAX_CENTER_NITROGEN_DEFLECTION));
        addInitialAtomCogOffset(sideNitrogenAtom, new Vector2D(-INITIAL_NITROGEN_NITROGEN_DISTANCE,
                -multFactor * MAX_SIDE_NITROGEN_DEFLECTION));
        addInitialAtomCogOffset(oxygenAtom, new Vector2D(INITIAL_NITROGEN_OXYGEN_DISTANCE,
                -multFactor * MAX_OXYGEN_DEFLECTION));
        updateAtomPositions();
    }
}
