/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.crystal;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.ion.Copper;
import edu.colorado.phet.solublesalts.model.ion.Hydroxide;
import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Lattice
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class Lattice {

    private static final double SAME_POSITION_TOLERANCE = 5;
    private static Random random = new Random();

    private Ion seed;
    // The bounds within which the lattice can be built
    private Rectangle2D bounds;
    private List nodes = new ArrayList();
    protected double spacing;

    /**
     * @param spacing
     */
    protected Lattice( double spacing ) {
        this.spacing = spacing;
    }

    /**
     * Attempts to add an ion to the lattice. This is used when a crystal is being built from a list of ions.
     *
     * @param ion
     * @return true if a place was found for the ion, false otherwise
     */
    public boolean add( Ion ion ) {
        boolean added = false;

        // If this is the first ion to be added, we only have a bit to do
        if( nodes.size() == 0 ) {
            Node newNode = new Node( ion );
            int cnt = getNumNeighboringSites( ion );
            for( int k = 0; k < cnt; k++ ) {
                double orientation = k * Math.PI * 2 / cnt;
                Bond newBond = new Bond( newNode, orientation, spacing );
                newBond.setOrigin( newNode );
                newNode.addBond( newBond );
            }
            nodes.add( newNode );
            setSeed( ion );
            added = true;
        }

        // If this is the second ion, the we need to establish orientation for the seed node's bonds
        else if( nodes.size() == 1 && ( (Node)nodes.get( 0 ) ).getPolarity() * ion.getCharge() < 0 ) {

            Node originNode = (Node)nodes.get( 0 );
            double orientation = MathUtil.getAngle( originNode.getPosition(), ion.getPosition() );
            ion.setPosition( MathUtil.radialToCartesian( spacing, orientation, originNode.getPosition() ) );
            originNode.setBaseOrientation( orientation );
            // Pick
            Bond bond = (Bond)originNode.getBonds().get( random.nextInt( originNode.getBonds().size() ) );
            addNewNode( ion, bond );
            added = true;
        }

        // Fimd an open bond that will accept an ion of the right polarity
        else {
            for( int i = 0; i < nodes.size() && !added; i++ ) {
                Node node = (Node)nodes.get( i );

                // If the node is of the opposite polarity of the ion, see if it has a free
                // bond. If we find one, set the position of the ion to be at the free end
                // of the bond
                if( node.getPolarity() * ion.getCharge() < 0 ) {
                    List bonds = node.getBonds();
                    for( int j = 0; j < bonds.size() && !added; j++ ) {
                        Bond bond = (Bond)bonds.get( j );
                        // If the bond is open, add a new node for the ion to it
                        if( bond.isOpen() ) {
                            addNewNode( ion, bond );
                            added = true;
                        }
                    }
                }
            }
        }
        return added;
    }


    /**
     * Attempts to add an ion next to a specified ion in the lattice. This is used when a crystal is being built
     * one ion at a time.
     *
     * @param ionA
     * @param ionB
     * @return true if ionA was added to the lattice next to ionB
     */
    public boolean addAtIonNode( Ion ionA, Ion ionB ) {
        boolean added = false;
        Node originNode = getNode( ionB );

        // Sanity check
        if( originNode == null ) {
            throw new RuntimeException( "originNode == null" );
        }

        // If we are adding the second ion to the lattice, then the position of the new ion establishes
        // the orienation of the bonds on the seed node.
        if( nodes.size() == 1 ) {
            double orientation = MathUtil.getAngle( originNode.getPosition(), ionA.getPosition() );
            originNode.setBaseOrientation( orientation );
        }

        // Pick the open bond whose open node is closest to the ion being added
        List bonds = originNode.getBonds();
        ArrayList openBonds = new ArrayList();
        for( int i = 0; i < bonds.size() && !added; i++ ) {
            Bond bond = (Bond)bonds.get( i );
            if( bond.isOpen() && bounds.contains( bond.getOpenPosition() ) ) {
                openBonds.add( bond );
            }
        }
        Bond bondToUse = null;
        if( openBonds.size() > 0 ) {
            double minDSq = Double.MAX_VALUE;
            for( int i = 0; i < openBonds.size(); i++ ) {
                Bond bond = (Bond)openBonds.get( i );
                double dSq = ionA.getPosition().distanceSq( bond.getOpenPosition() );
                if( dSq < minDSq ) {
                    minDSq = dSq;
                    bondToUse = bond;
                }
            }

            // Sanity check
            if( bondToUse == null ) {
                throw new RuntimeException( "bondToUse == null" );
            }
            addNewNode( ionA, bondToUse );
            added = true;
        }

        return added;
    }

    /**
     * Creates a new node for an ion at the free end of a specified bond. Open bonds are added to
     * the new node, but only when the open end of the bond is within the bounds for this lattice.
     *
     * @param ion
     * @param bond
     */
    private void addNewNode( Ion ion, Bond bond ) {

        // Sanity check
        if( !bond.isOpen() ) {
            bond.isOpen();
            throw new RuntimeException( "Bond is not open" );
        }

        ion.setPosition( bond.getOpenPosition() );
        Node newNode = new Node( ion );
        newNode.addBond( bond );
        bond.setDestination( newNode );

        // Create new bonds
        // The orientation of the bond that came in as a parameter comes from the first node added to the bond.
        // Because of this, we have to add pi to the orientation for the new bonds, since they are emmanating from
        // this new node.
        int cnt = getNumNeighboringSites( ion );
        for( int k = 1; k < cnt; k++ ) {
            double orientation = ( k * Math.PI * 2 / cnt + ( bond.getOrientation() + Math.PI ) ) % ( Math.PI * 2 );

            // Determine if there is already a bond where this one would go. This can happen easily, if a new node
            // is being created at a place that is at the open end of more than one existing bond.
            boolean addNewBond = true;
            Point2D testPoint = MathUtil.radialToCartesian( spacing, orientation, newNode.getPosition() );
            for( int i = 0; i < nodes.size() && addNewBond; i++ ) {
                Node node = (Node)nodes.get( i );
                List bonds = node.getBonds();
                for( int j = 0; j < bonds.size(); j++ ) {
                    Bond existingBond = (Bond)bonds.get( j );
                    if( existingBond.isOpen()
                        && MathUtil.isApproxEqual( existingBond.getOrigin().getPosition(),
                                                   testPoint,
                                                   SAME_POSITION_TOLERANCE )
                        && MathUtil.isApproxEqual( existingBond.getOpenPosition(), ion.getPosition(), SAME_POSITION_TOLERANCE ) )
                    {
                        existingBond.setDestination( newNode );
                        newNode.addBond( existingBond );
                        addNewBond = false;
                    }
                }
            }
            // If there isn't already a bond where this one should go, add one
            if( addNewBond ) {
                Bond newBond = new Bond( newNode, orientation, spacing );
                newBond.setOrigin( newNode );
                newNode.addBond( newBond );
            }
        }
        nodes.add( newNode );
    }

    /**
     * Returns the best ion to release. If possible, an ion of a specified type is selected, but only
     * if it ranks as well as any other type of ion with regard to the other criteria used for selection.
     * The criteria are:
     * <ul>
     * <li>the ion should have no bonds with children at the other end
     * <li>the ion be the child on a minimum number of bonds
     *
     *
     * @param ionsInLattice
     * @param preferredIonType
     * @return the best ion in the lattice to release
     */
    public Ion getBestIonToRelease( List ionsInLattice, Class preferredIonType ) {

        int numLists = 6;
        ArrayList[] preferredIons = new ArrayList[numLists];
        for( int i = 0; i < preferredIons.length; i++ ) {
            preferredIons[i] = new ArrayList( );
        }
        ArrayList[] otherIons = new ArrayList[numLists];
        for( int i = 0; i < otherIons.length; i++ ) {
            otherIons[i] = new ArrayList();
        }

        // Get a list of all nodes that have no children (i.e., are the origins of no bonds with destinations)
        Ion leastBoundIon = null;
        List preferredCandidates = new ArrayList();
        List otherCandidates = new ArrayList();
        for( int i = 0; i < nodes.size(); i++ ) {
            Node node = (Node)nodes.get( i );

            // Don't consider the seed unless it's the only ion in the lattice
            if( node.getIon() == seed && nodes.size() > 1 ) {
                continue;
            }

            if( node.hasNoChildren() ) {
                if( preferredIonType.isAssignableFrom( node.getIon().getClass() )) {
                    preferredIons[node.getNumFilledBonds()].add( node );
//                    preferredCandidates.add( node );
                }
                else {
                    otherIons[node.getNumFilledBonds()].add( node );
//                    otherCandidates.add( node );
                }
            }
        }

        List candidates = null;
        for( int i = 0; i < numLists && candidates == null; i++ ) {
            if( preferredIons[i].size() > 0 ) {
                candidates = preferredIons[i];
            }
            else if( otherIons[i].size() > 0 ) {
                candidates = otherIons[i];
            }
        }

        // If we have candidates of the prefered type, select from them, otherwise, select
        // from the other candidates
//        List candidates = preferredCandidates.size() > 0 ? preferredCandidates : otherCandidates;

        // Sanity check
        if( candidates.size() == 0 ) {
            throw new RuntimeException( "candidates.size() == 0" );
        }

        // Pick a random ion from the list of candidates
        Node leastBoundNode = (Node)candidates.get( random.nextInt( candidates.size() ) );
        leastBoundIon = leastBoundNode.getIon();
        return leastBoundIon;
    }

    /**
     * Returns a list of the lattice sites that are neighboring a specified ion that are
     * not occupied.
     *
     * @param ion
     * @return
     */
    public List getOpenNeighboringSites( Ion ion ) {

        List bonds = getNode( ion ).getBonds();
        List sites = new ArrayList();
        for( int i = 0; i < bonds.size(); i++ ) {
            Bond bond = (Bond)bonds.get( i );
            if( bond.isOpen() ) {
                Point2D p = MathUtil.radialToCartesian( spacing, bond.getOrientation(), ion.getPosition() );
                if( bounds.contains( p ) ) {
                    sites.add( p );
                }
            }
        }
        return sites;
    }

    /**
     * Removes an ion from the lattice
     *
     * @param ion
     */
    public void removeIon( Ion ion ) {
        Node node = getNode( ion );
        if( node == null ) {
            throw new RuntimeException();
        }
        // Remove the node from all the bonds it is part of
        List bonds = node.getBonds();
        for( int i = 0; i < bonds.size(); i++ ) {
            Bond bond = (Bond)bonds.get( i );
            bond.removeNode( node );
        }
        nodes.remove( node );

        // Sanity check
        if( SolubleSaltsConfig.DEBUG ) {
            for( int i = 0; i < nodes.size(); i++ ) {
                Node testNode = (Node)nodes.get( i );
                List testBonds = testNode.getBonds();
                for( int j = 0; j < testBonds.size(); j++ ) {
                    Bond existingBond = (Bond)testBonds.get( j );
                    if( existingBond.getOrigin() == node || existingBond.getDestination() == node ) {
                        throw new RuntimeException( "existingBond.getOrigin() == node || existingBond.getDestination() == node" );
                    }
                }
            }
        }

    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    /**
     * Returns the node associated with a specified ion
     *
     * @param ion
     * @return
     */
    public Node getNode( Ion ion ) {
        Node result = null;
        for( int i = 0; i < nodes.size() && result == null; i++ ) {
            Node node = (Node)nodes.get( i );
            if( node.getIon() == ion ) {
                result = node;
            }
        }
        return result;
    }

    /**
     * @param seedIon
     */
    void setSeed( Ion seedIon ) {
        // Sanity check
        Ion oldSeed = null;
        if( seed != null && seed != seedIon ) {
            oldSeed = seed;
        }
        seed = seedIon;

        // Make all the bonds radiate out from the seed
        new SeedSetter().setSeed( seed );

        seed.notifyObservers();
        if( oldSeed != null ) {
            oldSeed.notifyObservers();
        }
    }

    protected Ion getSeed() {
        return seed;
    }

    /**
     * @param bounds
     */
    void setBounds( Rectangle2D bounds ) {
        this.bounds = bounds;
    }

    protected Rectangle2D getBounds() {
        return bounds;
    }

    //----------------------------------------------------------------
    // Utility
    //----------------------------------------------------------------
    public String toStringRep() {
        Node node = getNode( getSeed() );
        StringBuffer sb = visitNode( node );

        return sb.toString();
    }

    private StringBuffer visitNode( Node node ) {
        StringBuffer sb = new StringBuffer();
        sb.append( node.toString() + " | " + node.getPosition().toString() );
        sb.append( "\n" );
        List bonds = node.getBonds();
        for( int i = 0; i < bonds.size(); i++ ) {
            Bond bond = (Bond)bonds.get( i );
            sb.append( "\t" + bond.getOrientation() + bond.getOrigin() + " : " + bond.getDestination() + "  " + "\n" );
        }

        for( int i = 0; i < bonds.size(); i++ ) {
            Bond bond = (Bond)bonds.get( i );
            if( bond.getDestination() != node && bond.getDestination() != null ) {
                sb.append( visitNode( bond.getDestination() ) );
            }
        }
        return sb;
    }

    //----------------------------------------------------------------
    // Abstract methods
    //----------------------------------------------------------------

    /**
     * Returns the number of possible sites there are in the lattice neighboring a
     * specified ion.
     *
     * @param ion
     * @return
     */
    abstract protected int getNumNeighboringSites( Ion ion );

    /**
     * All concrete subclasses are required to implement clone()
     *
     * @return
     */
    abstract public Object clone();

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Sets the direction of all the arcs in the lattice graph so that they point outward from a designated seed node
     */
    private class SeedSetter {

        void setSeed( Ion ion ) {
            Node node = getNode( ion );
            visitNode( node, new ArrayList(), null );
        }

        private void visitNode( Node node, List nodesVisited, Bond incomingBond ) {
            List bonds = node.getBonds();
            nodesVisited.add( node );
            for( int i = 0; i < bonds.size(); i++ ) {
                Bond bond = (Bond)bonds.get( i );

                // Do we need to reverse this bond?
                if( bond != incomingBond && bond.getOrigin() != node ) {
                    Node otherNode = bond.getOrigin();
                    bond.removeNode( otherNode );
                    bond.removeNode( node );
                    bond.setOrigin( node );
                    bond.setDestination( otherNode );
                    bond.setOrientation( bond.getOrientation() + Math.PI );
                }
                Node destinationNode = bond.getDestination();
                if( destinationNode != null && !nodesVisited.contains( destinationNode ) ) {
                    visitNode( destinationNode, nodesVisited, bond );
                }
            }
        }
    }

    /**
     * Test code
     *
     * @param args
     */
    public static void main( String[] args ) {
        Lattice testLattice = new TwoToOneLattice( Copper.class,
                                                           Hydroxide.class,
                                                           Copper.RADIUS + Hydroxide.RADIUS );
        testLattice.setBounds( new Rectangle2D.Double( 0, 0, 1000, 1000 ) );

        Ion ion1 = new Copper();
        ion1.setPosition( 10, 15 );
        testLattice.add( ion1 );

        Ion ion2 = new Hydroxide();
        ion2.setPosition( 20, 30 );
        testLattice.add( ion2 );

//        Ion ion3 = new Hydroxide();
//        ion3.setPosition( 30, 20 );
//        testLattice.add( ion3 );

        Ion ion4 = new Copper();
        ion4.setPosition( 30, 20 );
        testLattice.add( ion4 );

        Ion ion5 = new Copper();
        ion5.setPosition( 20, 30 );
        testLattice.add( ion5 );

        return;
    }
}


