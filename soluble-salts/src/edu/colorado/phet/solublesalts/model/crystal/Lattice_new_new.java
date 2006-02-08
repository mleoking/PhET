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
import edu.colorado.phet.solublesalts.model.ion.Copper;
import edu.colorado.phet.solublesalts.model.ion.Hydroxide;
import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/**
 * Lattice
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class Lattice_new_new {

    private static final double SAME_POSITION_TOLERANCE = 3;
    private static Random random = new Random();

    private Ion seed;
    private Rectangle2D bounds;
    private List nodes = new ArrayList();
    protected double spacing;

    /**
     * @param spacing
     */
    protected Lattice_new_new( double spacing ) {
        this.spacing = spacing;
    }

    /**
     * Attempts to add an ion to the lattice
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
                Bond newBond = new Bond( orientation, spacing );
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
            for( int i = 0; i < nodes.size(); i++ ) {
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
     * Attempts to add an ion next to a specified ion in the lattice
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

        // Find the open bonds on the ionB node
        List bonds = originNode.getBonds();
        ArrayList openBonds = new ArrayList();
        for( int i = 0; i < bonds.size() && !added; i++ ) {
            Bond bond = (Bond)bonds.get( i );
            if( bond.isOpen() && bounds.contains( bond.getOpenPosition() ) ) {
                openBonds.add( bond );
            }
        }

        // Pick an open node at random and attached a new node for ionA to the end of it
        if( openBonds.size() > 0 ) {
            Bond bondToUse = (Bond)openBonds.get( random.nextInt( openBonds.size() ) );
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

        // The orientation of the bond comes from the first node added to the bond. Because of this,
        // we have to add pi to the orientation for the new bonds, since they are emmanating from
        // this new node.
        int cnt = getNumNeighboringSites( ion );
        for( int k = 1; k < cnt; k++ ) {
            double orientation = k * Math.PI * 2 / cnt + ( bond.getOrientation() + Math.PI );
            Bond newBond = new Bond( orientation, spacing );
            newBond.setOrigin( newNode );
            newNode.addBond( newBond );
        }
        nodes.add( newNode );
    }

    /**
     * Returns an ion with the greatest "least bound" characteristic. This characteristic is
     * defined as the ratio of unoccupied neighboring lattice sites divided by the total
     * number of neighboring sites. If more than one ion in the lattice has this same
     * characteristic value, one of them is chosen at random.
     *
     * @param ionsInLattice
     * @param orientation
     * @return
     */
    public Ion getLeastBoundIon( List ionsInLattice, double orientation ) {
        TreeMap nodeMap = new TreeMap();

        // Find the max number of open bonds for any node in the lattice
        Ion leastBoundIon = null;
        for( int i = 0; i < nodes.size(); i++ ) {
            Node node = (Node)nodes.get( i );

            // Don't consider the seed unless it's the only one left
            if( node.getIon() == getSeed() && ionsInLattice.size() > 1 ) {
                continue;
            }
            Double bindingMetric = new Double( node.getBindingMetric( bounds ) );
            List list = (List)nodeMap.get( bindingMetric );
            if( list == null ) {
                list = new ArrayList();
                nodeMap.put( bindingMetric, list );
            }
            list.add( node );
        }
        List leastBoundNodeList = (List)nodeMap.get( nodeMap.lastKey() );
        Node leastBoundNode = (Node)leastBoundNodeList.get( random.nextInt( leastBoundNodeList.size() ) );
        leastBoundIon = leastBoundNode.getIon();

        // Sanity check
        if( leastBoundIon == null ) {
            throw new RuntimeException( "leastBoundIon == null" );
        }
        if( ( (Double)nodeMap.lastKey() ).doubleValue() == 0 ) {
            throw new RuntimeException( "maxOpenBonds = 0" );
        }

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

    public void removeIon( Ion ion ) {
        Node node = getNode( ion );
        if( node == null ) {
            throw new RuntimeException();
        }
        List bonds = node.getBonds();
        // Remove the node from all the bonds it is part of
        for( int i = 0; i < bonds.size(); i++ ) {
            Bond bond = (Bond)bonds.get( i );
            bond.removeNode( node );
        }
        nodes.remove( node );
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


    /**
     * Test code
     *
     * @param args
     */
    public static void main( String[] args ) {
        Lattice_new_new testLattice = new TwoToOneLattice( Copper.class,
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


