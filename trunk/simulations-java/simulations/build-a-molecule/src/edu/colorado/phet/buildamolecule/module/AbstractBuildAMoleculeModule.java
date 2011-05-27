package edu.colorado.phet.buildamolecule.module;

import java.awt.*;
import java.util.*;
import java.util.List;

import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Superclass for modules in Build a Molecule. Handles code required for all modules (bounds, canvas handling, and the ability to switch models)
 */
public abstract class AbstractBuildAMoleculeModule extends PiccoloModule {
    protected final LayoutBounds bounds;
    protected BuildAMoleculeCanvas canvas;
    private CollectionList collectionList;

    private static Random random = new Random( System.currentTimeMillis() );

    public AbstractBuildAMoleculeModule( Frame parentFrame, String name, LayoutBounds bounds ) {
        super( name, new ConstantDtClock( 30 ) );
        this.bounds = bounds;
        setClockControlPanel( null );
    }

    protected abstract BuildAMoleculeCanvas buildCanvas( CollectionList collectionList );

    protected void setInitialCollection( KitCollection collection ) {
        this.collectionList = new CollectionList( collection, bounds );
        canvas = buildCanvas( collectionList );
        setSimulationPanel( canvas );
    }

    protected KitCollection generateModel() {
        return null;
    }

    public void addGeneratedCollection() {
        KitCollection collection = generateModel();
        if ( collection != null ) {
            collectionList.addCollection( collection );
        }
    }

    public BuildAMoleculeCanvas getCanvas() {
        return canvas;
    }

    /**
     * Generate a group of collection boxes and kits such that the boxes can be filled.
     *
     * @param allowMultipleMolecules Whether collection boxes can have more than 1 molecule
     * @param numBoxes               Number of collection boxes
     * @return A consistent model
     */
    protected KitCollection generateModel( boolean allowMultipleMolecules, int numBoxes ) {
        final int MAX_IN_BOX = 3;

        Set<CompleteMolecule> usedMolecules = new HashSet<CompleteMolecule>();
        List<Kit> kits = new LinkedList<Kit>();
        List<CollectionBox> boxes = new LinkedList<CollectionBox>();

        List<MoleculeStructure<Atom>> molecules = new LinkedList<MoleculeStructure<Atom>>(); // store all the molecules that will need to be created

        for ( int i = 0; i < numBoxes; i++ ) {
            CompleteMolecule molecule = pickRandomMoleculeNotIn( usedMolecules );
            usedMolecules.add( molecule );

            int numberInBox = allowMultipleMolecules ? random.nextInt( MAX_IN_BOX ) + 1 : 1;

            // restrict the number of carbon that we can have
            int carbonCount = molecule.getHistogram().getQuantity( Element.C );
            if ( carbonCount > 1 ) {
                numberInBox = Math.min( 2, numberInBox );
            }

            CollectionBox box = new CollectionBox( molecule, numberInBox );
            boxes.add( box );

            // add in that many molecules
            for ( int j = 0; j < box.getCapacity(); j++ ) {
                molecules.add( molecule.getAtomCopy() );
            }
        }

        // randomize the molecules that we will pull from
        Collections.shuffle( molecules );

        // while more molecules to construct are left, create another kit
        while ( !molecules.isEmpty() ) {
            List<Bucket> buckets = new LinkedList<Bucket>();

            // pull off the 1st molecule
            MoleculeStructure<Atom> molecule = molecules.get( 0 );

            // get the set of atoms that we need
            Set<String> atomSymbols = new HashSet<String>(); // TODO: use set of elements instead
            for ( Atom atom : molecule.getAtoms() ) {
                atomSymbols.add( atom.getSymbol() );
            }

            // TODO: potentially add another type of atom?

            int equivalentMoleculesRemaining = 0;
            for ( MoleculeStructure<Atom> moleculeStructure : molecules ) {
                if ( moleculeStructure.getHillSystemFormulaFragment().equals( molecule.getHillSystemFormulaFragment() ) ) {
                    equivalentMoleculesRemaining++;
                }
            }

            boolean ableToIncreaseMultiple = allowMultipleMolecules && equivalentMoleculesRemaining > 1;
//            int atomMultiple = 1 + ( ableToIncreaseMultiple ? random.nextInt( equivalentMoleculesRemaining ) : 0 );
            int atomMultiple = 1 + ( ableToIncreaseMultiple ? equivalentMoleculesRemaining : 0 );

            // for each type of atom
            for ( String symbol : atomSymbols ) {
                // find out how many atoms of this type we need
                int requiredAtomCount = 0;
                for ( Atom atom : molecule.getAtoms() ) {
                    if ( atom.getSymbol().equals( symbol ) ) {
                        requiredAtomCount++;
                    }
                }

                Element element = Element.getElementBySymbol( symbol );

                // create a multiple of the required number of atoms, so they can construct 'atomMultiple' molecules with this
                int atomCount = requiredAtomCount * atomMultiple;

                // possibly add more, if we can only have 1 molecule per box
                if ( !symbol.equals( "C" ) && ( symbol.equals( "H" ) || atomCount < 4 ) ) {
                    atomCount += random.nextInt( 2 );
                }

                // funky math part. sqrt scales it so that we can get two layers of atoms if the atom count is above 2
                int bucketWidth = Bucket.calculateIdealBucketWidth( element.getRadius(), atomCount );
                System.out.println( bucketWidth );
                //int bucketWidth = ( (int) ( 2 * atomRadius * Math.pow( atomCount + 1, 0.4 ) ) ) + 200;

                buckets.add( new Bucket( new PDimension( bucketWidth, 200 ), getClock(), element, atomCount ) );
            }

            // add the kit
            kits.add( new Kit( bounds, buckets.toArray( new Bucket[buckets.size()] ) ) );

            // remove our 1 main molecule
            molecules.remove( molecule );
            atomMultiple -= 1;

            // TODO: sort through and find out if we can construct another whole atom within our larger margins

            // if we can remove others (due to an atom multiple), remove the others
            while ( atomMultiple > 0 ) {
                for ( int i = 0; i < molecules.size(); i++ ) {
                    if ( molecules.get( i ).getHillSystemFormulaFragment().equals( molecule.getHillSystemFormulaFragment() ) ) {
                        molecules.remove( i );
                        break;
                    }
                }
                atomMultiple -= 1;
            }
        }

        KitCollection collection = new KitCollection();
        for ( Kit kit : kits ) {
            collection.addKit( kit );
        }
        for ( CollectionBox box : boxes ) {
            collection.addCollectionBox( box );
        }
        return collection;
    }

    private CompleteMolecule pickRandomMoleculeNotIn( Set<CompleteMolecule> molecules ) {
        while ( true ) {
            CompleteMolecule molecule = MoleculeList.COLLECTION_BOX_MOLECULES[MoleculeList.random.nextInt( MoleculeList.COLLECTION_BOX_MOLECULES.length )];
            if ( !molecules.contains( molecule ) ) {
                return molecule;
            }
        }
    }

    @Override public void reset() {
        collectionList.reset();
    }
}
