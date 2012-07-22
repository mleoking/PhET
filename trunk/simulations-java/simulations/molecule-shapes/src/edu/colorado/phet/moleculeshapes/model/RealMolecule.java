// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.moleculeshapes.model.AttractorModel.ResultMapping;

/**
 * Represents a physically malleable version of a real molecule, with lone pairs if necessary.
 */
public class RealMolecule extends Molecule {

    private final RealMoleculeShape realMolecule;

    private Set<Element> elementsUsed = new HashSet<Element>();

    private final Map<PairGroup, LocalShape> localShapeMap = new HashMap<PairGroup, LocalShape>();

    public RealMolecule( final RealMoleculeShape realMolecule ) {
        this.realMolecule = realMolecule;

        final int numLonePairs = realMolecule.getCentralLonePairCount();
        final int numBonds = realMolecule.getBonds().size();

        List<Vector3D> idealCentralOrientations = new ArrayList<Vector3D>();
        List<PairGroup> centralPairGroups = new ArrayList<PairGroup>();

        addCentralAtom( new RealPairGroup( new Vector3D(), false, realMolecule.getCentralAtom().getElement() ) );

        // add in bonds
        for ( Bond<Atom3D> bond : realMolecule.getBonds() ) {
            Atom3D atom = bond.getOtherAtom( realMolecule.getCentralAtom() );
            final Vector3D normalizedPosition = atom.position.get().normalized();
            idealCentralOrientations.add( normalizedPosition );
            final double bondLength = atom.position.get().magnitude();

            Vector3D atomLocation = normalizedPosition.times( PairGroup.REAL_TMP_SCALE * bondLength );
            final RealPairGroup group = new RealPairGroup( atomLocation, false, atom.getElement() );
            centralPairGroups.add( group );
            addGroup( group, getCentralAtom(), bond.order, bondLength );
            elementsUsed.add( atom.getElement() );

            addTerminalLonePairs( group, atom.lonePairCount );
        }

        // all of the ideal vectors (including for lone pairs)
        VseprConfiguration vseprConfiguration = new VseprConfiguration( numBonds, numLonePairs );
        final List<Vector3D> idealModelVectors = vseprConfiguration.getAllUnitVectors();

        ResultMapping mapping = vseprConfiguration.getIdealBondRotationToPositions( LocalShape.sortedLonePairsFirst( getNeighboringAtoms( getCentralAtom() ) ) );

        // add in lone pairs in their correct "initial" positions
        for ( int i = 0; i < numLonePairs; i++ ) {
            Vector3D normalizedPosition = mapping.rotateVector( idealModelVectors.get( i ) );
            idealCentralOrientations.add( normalizedPosition );
            PairGroup group = new PairGroup( normalizedPosition.times( PairGroup.LONE_PAIR_DISTANCE ), true, false );
            addGroup( group, getCentralAtom(), 0, PairGroup.LONE_PAIR_DISTANCE / PairGroup.REAL_TMP_SCALE );
            centralPairGroups.add( group );
        }

        localShapeMap.put( getCentralAtom(), new LocalShape( LocalShape.realPermutations( centralPairGroups ), getCentralAtom(), centralPairGroups, idealCentralOrientations ) );

        // basically only use VSEPR model for the attraction on non-central atoms
        for ( PairGroup atom : getRadialAtoms() ) {
            localShapeMap.put( atom, getLocalVSEPRShape( atom ) );
        }
    }

    @Override public void update( float tpf ) {
        super.update( tpf );

        // angle-based repulsion
        for ( PairGroup atom : getAtoms() ) {
            List<PairGroup> neighbors = getNeighbors( atom );
            if ( neighbors.size() > 1 ) {
                LocalShape localShape = getLocalShape( atom );

                localShape.applyAngleAttractionRepulsion( tpf );
            }
        }
    }

    @Override public LocalShape getLocalShape( PairGroup atom ) {
        return localShapeMap.get( atom );
    }

    @Override public boolean isReal() {
        return true;
    }

    @Override public Option<Float> getMaximumBondLength() {
        return new None<Float>();
    }

    public RealMoleculeShape getRealMolecule() {
        return realMolecule;
    }
}
