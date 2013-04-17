// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * This class is used to change the phase state (i.e. solid, liquid, or gas)
 * for a set of multi-atomic (i.e. more than one atom/molecule) molecules.
 *
 * @author John Blanco
 */
public class DiatomicPhaseStateChanger extends AbstractPhaseStateChanger {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final double MIN_INITIAL_DIAMETER_DISTANCE = 2.0;

    // The following constants can be adjusted to make the the corresponding
    // phase more or less dense.
    private static final double LIQUID_SPACING_FACTOR = 0.7;
    private static final double GAS_SPACING_FACTOR = 1.0;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final AtomPositionUpdater m_positionUpdater = new DiatomicAtomPositionUpdater();

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public DiatomicPhaseStateChanger( MultipleParticleModel model ) {
        super( model );

        // Make sure this is not being used on an inappropriate data set.
        assert m_model.getMoleculeDataSetRef().getAtomsPerMolecule() == 2;
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------

    public void setPhase( int phaseID ) {
        int postChangeModelSteps = 0;
        switch( phaseID ) {
            case PhaseStateChanger.PHASE_SOLID:
                setPhaseSolid();
                postChangeModelSteps = 0;
                break;
            case PhaseStateChanger.PHASE_LIQUID:
                setPhaseLiquid();
                postChangeModelSteps = 200;
                break;
            case PhaseStateChanger.PHASE_GAS:
                setPhaseGas();
                postChangeModelSteps = 0;
                break;
        }

        MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();

        // Assume that we've done our job correctly and that all the atoms are
        // in safe positions.
        m_model.getMoleculeDataSetRef().setNumberOfSafeMolecules( moleculeDataSet.getNumberOfMolecules() );

        // Sync up the atom positions with the molecule positions.
        m_positionUpdater.updateAtomPositions( moleculeDataSet );

        // Step the model a number of times in order to prevent the particles
        // from looking too organized.  The number of steps was empirically
        // determined.
        for ( int i = 0; i < postChangeModelSteps; i++ ) {
            m_model.step();
        }
    }

    /**
     * Set the phase to the solid state.
     */
    private void setPhaseSolid() {

        // Set the model temperature for this phase.
        m_model.setTemperature( MultipleParticleModel.SOLID_TEMPERATURE );

        // Get references to the various elements of the data set.
        MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();
        int numberOfMolecules = moleculeDataSet.getNumberOfMolecules();
        Point2D[] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
        MutableVector2D[] moleculeVelocities = moleculeDataSet.getMoleculeVelocities();
        double[] moleculeRotationAngles = moleculeDataSet.getMoleculeRotationAngles();

        // Create and initialize other variables needed to do the job.
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_model.getTemperatureSetPoint() );
        int moleculesPerLayer = (int) ( Math.round( Math.sqrt( numberOfMolecules * 2 ) ) / 2 );

        // Establish the starting position, which will be the lower left corner
        // of the "cube".  The molecules will all be rotated so that they are
        // lying down.
        double crystalWidth = moleculesPerLayer * ( 2.0 - 0.3 ); // Final term is a fudge factor that can be adjusted to center the cube.
        double startingPosX = ( m_model.getNormalizedContainerWidth() / 2 ) - ( crystalWidth / 2 );
        double startingPosY = 1.2 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL; // Multiplier can be tweaked to minimize initial "bounce".

        // Place the molecules by placing their centers of mass.

        int moleculesPlaced = 0;
        double xPos, yPos;
        for ( int i = 0; i < numberOfMolecules; i++ ) { // One iteration per layer.
            for ( int j = 0; ( j < moleculesPerLayer ) && ( moleculesPlaced < numberOfMolecules ); j++ ) {
                xPos = startingPosX + ( j * MIN_INITIAL_DIAMETER_DISTANCE );
                if ( i % 2 != 0 ) {
                    // Every other row is shifted a bit to create hexagonal pattern.
                    xPos += ( 1 + DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL ) / 2;
                }
                yPos = startingPosY + ( (double) i * MIN_INITIAL_DIAMETER_DISTANCE * 0.5 );
                moleculeCenterOfMassPositions[( i * moleculesPerLayer ) + j].setLocation( xPos, yPos );
                moleculeRotationAngles[( i * moleculesPerLayer ) + j] = 0;

                moleculesPlaced++;

                // Assign each molecule an initial velocity.
                double xVel = temperatureSqrt * rand.nextGaussian();
                double yVel = temperatureSqrt * rand.nextGaussian();
                moleculeVelocities[( i * moleculesPerLayer ) + j].setComponents( xVel, yVel );
            }
        }
    }

    /**
     * Set the phase to the liquid state.
     */
    private void setPhaseLiquid() {

        // Set the model temperature for this phase.
        m_model.setTemperature( MultipleParticleModel.LIQUID_TEMPERATURE );

        // Get references to the various elements of the data set.
        MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();
        Point2D[] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
        MutableVector2D[] moleculeVelocities = moleculeDataSet.getMoleculeVelocities();
        double[] moleculeRotationAngles = moleculeDataSet.getMoleculeRotationAngles();
        double[] moleculeRotationRates = moleculeDataSet.getMoleculeRotationRates();

        // Create and initialize other variables needed to do the job.
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_model.getTemperatureSetPoint() );
        int numberOfMolecules = moleculeDataSet.getNumberOfMolecules();

        // Initialize the velocities and angles of the molecules.
        for ( int i = 0; i < numberOfMolecules; i++ ) {

            // Assign each molecule an initial velocity.
            moleculeVelocities[i].setComponents( temperatureSqrt * rand.nextGaussian(),
                                                 temperatureSqrt * rand.nextGaussian() );

            // Assign each molecule an initial rotation rate.
            moleculeRotationRates[i] = rand.nextDouble() * temperatureSqrt * Math.PI * 2;
        }

        // Assign each molecule to a position.

        int moleculesPlaced = 0;

        Point2D centerPoint = new Point2D.Double( m_model.getNormalizedContainerWidth() / 2,
                                                  m_model.getNormalizedContainerHeight() / 4 );
        int currentLayer = 0;
        int particlesOnCurrentLayer = 0;
        int particlesThatWillFitOnCurrentLayer = 1;

        for ( int i = 0; i < numberOfMolecules; i++ ) {

            for ( int j = 0; j < MAX_PLACEMENT_ATTEMPTS; j++ ) {

                double distanceFromCenter = currentLayer * MIN_INITIAL_DIAMETER_DISTANCE * LIQUID_SPACING_FACTOR;
                double angle = ( (double) particlesOnCurrentLayer / (double) particlesThatWillFitOnCurrentLayer * 2 * Math.PI ) +
                               ( (double) particlesThatWillFitOnCurrentLayer / ( 4 * Math.PI ) );
                double xPos = centerPoint.getX() + ( distanceFromCenter * Math.cos( angle ) );
                double yPos = centerPoint.getY() + ( distanceFromCenter * Math.sin( angle ) );
                particlesOnCurrentLayer++;  // Consider this spot used even if we don't actually put the
                // particle there.
                if ( particlesOnCurrentLayer >= particlesThatWillFitOnCurrentLayer ) {

                    // This layer is full - move to the next one.
                    currentLayer++;
                    particlesThatWillFitOnCurrentLayer =
                            (int) ( currentLayer * 2 * Math.PI / ( MIN_INITIAL_DIAMETER_DISTANCE * LIQUID_SPACING_FACTOR ) );
                    particlesOnCurrentLayer = 0;
                }

                // Check if the position is too close to the wall.  Note
                // that we don't check inter-particle distances here - we rely
                // on the placement algorithm to make sure that this is not a
                // problem.
                if ( ( xPos > MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE ) &&
                     ( xPos < m_model.getNormalizedContainerWidth() - MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE ) &&
                     ( yPos > MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE ) &&
                     ( xPos < m_model.getNormalizedContainerHeight() - MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE ) ) {

                    // This is an acceptable position.
                    moleculeCenterOfMassPositions[moleculesPlaced].setLocation( xPos, yPos );
                    moleculeRotationAngles[moleculesPlaced] = angle + Math.PI / 2;
                    moleculesPlaced++;
                    break;
                }
            }
        }
    }

    /**
     * Set the phase to the gaseous state.
     */
    private void setPhaseGas() {

        // Set the model temperature for this phase.
        m_model.setTemperature( MultipleParticleModel.GAS_TEMPERATURE );

        // Get references to the various elements of the data set.
        MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();
        Point2D[] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
        MutableVector2D[] moleculeVelocities = moleculeDataSet.getMoleculeVelocities();
        double[] moleculeRotationAngles = moleculeDataSet.getMoleculeRotationAngles();
        double[] moleculeRotationRates = moleculeDataSet.getMoleculeRotationRates();

        // Create and initialize other variables needed to do the job.
        Random rand = new Random();
        double temperatureSqrt = Math.sqrt( m_model.getTemperatureSetPoint() );
        int numberOfMolecules = moleculeDataSet.getNumberOfMolecules();

        for ( int i = 0; i < numberOfMolecules; i++ ) {
            // Temporarily position the molecules at (0,0).
            moleculeCenterOfMassPositions[i].setLocation( 0, 0 );

            // Assign each molecule an initial velocity.
            moleculeVelocities[i].setComponents( temperatureSqrt * rand.nextGaussian(),
                                                 temperatureSqrt * rand.nextGaussian() );

            // Assign each molecule an initial rotational position.
            moleculeRotationAngles[i] = rand.nextDouble() * Math.PI * 2;

            // Assign each molecule an initial rotation rate.
            moleculeRotationRates[i] = rand.nextDouble() * temperatureSqrt * Math.PI * 2;
        }

        // Redistribute the molecules randomly around the container, but make
        // sure that they are not too close together or they end up with a
        // disproportionate amount of kinetic energy.
        double newPosX, newPosY;
        double rangeX = m_model.getNormalizedContainerWidth() - ( 2 * MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE );
        double rangeY = m_model.getNormalizedContainerHeight() - ( 2 * MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE );
        for ( int i = 0; i < numberOfMolecules; i++ ) {
            for ( int j = 0; j < MAX_PLACEMENT_ATTEMPTS; j++ ) {
                // Pick a random position.
                newPosX = MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE + ( rand.nextDouble() * rangeX );
                newPosY = MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE + ( rand.nextDouble() * rangeY );
                boolean positionAvailable = true;
                // See if this position is available.
                for ( int k = 0; k < i; k++ ) {
                    if ( moleculeCenterOfMassPositions[k].distance( newPosX, newPosY ) <
                         MIN_INITIAL_DIAMETER_DISTANCE * GAS_SPACING_FACTOR ) {

                        positionAvailable = false;
                        break;
                    }
                }
                if ( positionAvailable ) {
                    // We found an open position.
                    moleculeCenterOfMassPositions[i].setLocation( newPosX, newPosY );
                    break;
                }
                else if ( j == MAX_PLACEMENT_ATTEMPTS - 1 ) {
                    // This is the last attempt, so use this position anyway.
                    Point2D openPoint = findOpenMoleculeLocation();
                    if ( openPoint != null ) {
                        moleculeCenterOfMassPositions[i].setLocation( openPoint );
                    }
                }
            }
        }
    }
}
