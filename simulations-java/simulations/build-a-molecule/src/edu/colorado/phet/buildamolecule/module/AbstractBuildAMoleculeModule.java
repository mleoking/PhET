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
    protected Frame parentFrame;

    private static Random random = new Random( System.currentTimeMillis() );

    public AbstractBuildAMoleculeModule( Frame parentFrame, String name, boolean wide ) {
        super( name, new ConstantDtClock( 30 ) );
        this.parentFrame = parentFrame;
        setClockControlPanel( null );
        bounds = new LayoutBounds( wide );
    }

    protected abstract BuildAMoleculeCanvas buildCanvas( KitCollectionModel model );

    protected void setModel( KitCollectionModel model ) {
        canvas = buildCanvas( model );
        setSimulationPanel( canvas );
    }

    protected KitCollectionModel generateModel() {
        return null;
    }

    public void regenerateModelIfPossible() {
        KitCollectionModel model = generateModel();
        if ( model != null ) {
            setModel( model );
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
    protected KitCollectionModel generateModel( boolean allowMultipleMolecules, int numBoxes ) {
        final int MAX_IN_BOX = 3;

        Set<CompleteMolecule> usedMolecules = new HashSet<CompleteMolecule>();
        List<Kit> kits = new LinkedList<Kit>();
        List<CollectionBox> boxes = new LinkedList<CollectionBox>();

        List<MoleculeStructure> molecules = new LinkedList<MoleculeStructure>(); // store all the molecules that will need to be created

        for ( int i = 0; i < numBoxes; i++ ) {
            CompleteMolecule molecule = pickRandomMoleculeNotIn( usedMolecules );
            usedMolecules.add( molecule );

            int numberInBox = allowMultipleMolecules ? random.nextInt( MAX_IN_BOX ) + 1 : 1;

            // restrict the number of carbon that we can have
            int carbonCount = molecule.getMoleculeStructure().getHistogram().getQuantity( Element.C );
            if ( carbonCount > 1 ) {
                numberInBox = Math.min( 2, numberInBox );
            }

            CollectionBox box = new CollectionBox( molecule, numberInBox );
            boxes.add( box );

            // add in that many molecules
            for ( int j = 0; j < box.getCapacity(); j++ ) {
                molecules.add( molecule.getMoleculeStructure().getCopy() );
            }
        }

        // randomize the molecules that we will pull from
        Collections.shuffle( molecules );

        // while more molecules to construct are left, create another kit
        while ( !molecules.isEmpty() ) {
            List<Bucket> buckets = new LinkedList<Bucket>();

            // pull off the 1st molecule
            MoleculeStructure molecule = molecules.get( 0 );

            // get the set of atoms that we need
            Set<String> atomSymbols = new HashSet<String>(); // TODO: use set of elements instead
            for ( Atom atom : molecule.getAtoms() ) {
                atomSymbols.add( atom.getSymbol() );
            }

            // TODO: potentially add another type of atom?

            int equivalentMoleculesRemaining = 0;
            for ( MoleculeStructure moleculeStructure : molecules ) {
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
                int bucketWidth = calculateIdealBucketWidth( element.getRadius(), atomCount );
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

        KitCollectionModel model = new KitCollectionModel( bounds );
        for ( Kit kit : kits ) {
            model.addKit( kit );
        }
        for ( CollectionBox box : boxes ) {
            model.addCollectionBox( box );
        }
        return model;
    }

    /**
     * Make sure we can fit all of our atoms in just two rows
     *
     * @param radius   Atomic radius (picometers)
     * @param quantity Quantity of atoms in bucket
     * @return Width of bucket
     */
    public static int calculateIdealBucketWidth( double radius, int quantity ) {
        // calculate atoms to go on the bottom row
        int numOnBottomRow = ( quantity <= 2 ) ? quantity : ( quantity / 2 + 1 );

        // figure out our width, accounting for radius-padding on each side
        double width = 2 * radius * ( numOnBottomRow + 1 );

        // add a bit, and make sure we don't go under 350
        return (int) Math.max( 350, width + 1 );
    }

    private CompleteMolecule pickRandomMoleculeNotIn( Set<CompleteMolecule> molecules ) {
        while ( true ) {
            CompleteMolecule molecule = MoleculeList.COLLECTION_BOX_MOLECULES[MoleculeList.random.nextInt( MoleculeList.COLLECTION_BOX_MOLECULES.length )];
            if ( !molecules.contains( molecule ) ) {
                return molecule;
            }
        }
    }
}
