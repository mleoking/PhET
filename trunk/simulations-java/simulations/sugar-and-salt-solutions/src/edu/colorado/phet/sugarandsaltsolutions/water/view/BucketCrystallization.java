// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.water.model.SaltIon;
import edu.colorado.phet.sugarandsaltsolutions.water.model.SodiumChlorideCrystal;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;

/**
 * Experimental features for crystallizing particles in the bucket.
 * TODO: documentation or delete
 *
 * @author Sam Reid
 */
public class BucketCrystallization {

//    saltBucketParticleLayer.addPropertyChangeListener( PNode.PROPERTY_CHILDREN, new PropertyChangeListener() {
//        int lastChildCount = 1;
//
//        public void propertyChange( PropertyChangeEvent evt ) {
//            final int currentCount = saltBucketParticleLayer.getChildrenCount();
//            if ( currentCount > 0 && currentCount != lastChildCount ) {
//
//                //Wait for all other add/removes to complete
//                SwingUtilities.invokeLater( new Runnable() {
//                    public void run() {
//                        int numSodium = getNumSodium( saltBucketParticleLayer );
//                        int numChloride = getNumChloride( saltBucketParticleLayer );
//                        if ( numSodium > 0 || numChloride > 0 ) {
//                            addSaltToBucket( numSodium, numChloride );
//                        }
//                    }
//                } );
//            }
//            lastChildCount = currentCount;
//        }
//    } );


    //Create a model element for the sucrose crystal that the user will drag
    private SodiumChlorideCrystal createCrystal( int numSodium, int numChloride ) {
        if ( numSodium == 2 && numChloride == 2 ) {
            return new SodiumChlorideCrystal( ZERO, 0 ) {{
                addConstituent( new Constituent<SaltIon>( new SaltIon.ChlorideIon(), ZERO ) );
                addConstituent( getOpenSites().get( 1 ).toConstituent() );
                addConstituent( getOpenSites().get( 2 ).toConstituent() );
                addConstituent( getOpenSites().get( 4 ).toConstituent() );
            }};
        }
        else if ( numSodium == 1 && numChloride == 2 ) {
            return new SodiumChlorideCrystal( ZERO, 0 ) {{
                addConstituent( new Constituent<SaltIon>( new SaltIon.ChlorideIon(), ZERO ) );
                addConstituent( getOpenSites().get( 1 ).toConstituent() );
                addConstituent( getOpenSites().get( 1 ).toConstituent() );
            }};
        }
        else if ( numSodium == 2 && numChloride == 1 ) {
            return new SodiumChlorideCrystal( ZERO, 0 ) {{
                addConstituent( new Constituent<SaltIon>( new SaltIon.ChlorideIon(), ZERO ) );
                addConstituent( getOpenSites().get( 1 ).toConstituent() );
                addConstituent( getOpenSites().get( 2 ).toConstituent() );
            }};
        }
        else if ( numSodium == 1 && numChloride == 1 ) {
            return new SodiumChlorideCrystal( ZERO, 0 ) {{
                addConstituent( new Constituent<SaltIon>( new SaltIon.ChlorideIon(), ZERO ) );
                addConstituent( getOpenSites().get( 2 ).toConstituent() );
            }};
        }
        else if ( numSodium == 0 && numChloride == 1 ) {
            return new SodiumChlorideCrystal( ZERO, 0 ) {{
                addConstituent( new Constituent<SaltIon>( new SaltIon.ChlorideIon(), ZERO ) );
            }};
        }
        else if ( numSodium == 1 && numChloride == 0 ) {
            return new SodiumChlorideCrystal( ZERO, 0 ) {{
                addConstituent( new Constituent<SaltIon>( new SaltIon.SodiumIon(), ZERO ) );
            }};
        }
        else {
            return null;
        }
    }

    private int getNumSodium( PNode saltBucketParticleLayer ) {
        int count = 0;
        for ( int i = 0; i < saltBucketParticleLayer.getChildrenCount(); i++ ) {
            CompoundListNode child = (CompoundListNode) saltBucketParticleLayer.getChild( i );
            Compound[] compounds = child.getCompounds();
            for ( Compound compound : compounds ) {
                if ( compound instanceof SaltIon.SodiumIon ) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getNumChloride( PNode saltBucketParticleLayer ) {
        int count = 0;
        for ( int i = 0; i < saltBucketParticleLayer.getChildrenCount(); i++ ) {
            CompoundListNode child = (CompoundListNode) saltBucketParticleLayer.getChild( i );
            Compound[] compounds = child.getCompounds();
            for ( Compound compound : compounds ) {
                if ( compound instanceof SaltIon.ChlorideIon ) {
                    count++;
                }
            }
        }
        return count;
    }
}
