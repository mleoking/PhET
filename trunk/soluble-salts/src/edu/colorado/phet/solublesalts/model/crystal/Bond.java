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
import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.awt.geom.Point2D;

/**
 * Bond
 * <p/>
 * Represents a bond between two atoms in a crystal lattice. A bod is directed: It has an origin node and a
 * destination node. A bond also has an orientation, which is the angle of the bond measured relative to the
 * origin node. If the origin node is remove from a bond, the destination node becomes the new origin.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Bond {
    private Node[] nodes = new Node[2];
    private double orientation;
    private double length;

    /**
     * @param orientation
     */
    public Bond( double orientation, double length ) {
        this.orientation = orientation;
        this.length = length;
    }

    public void setOrigin( Node node ) {
        if( nodes[0] != null ) {
            throw new RuntimeException( "origin already set" );
        }
        nodes[0] = node;
    }

    public Node getOrigin() {
        return nodes[0];
    }

    public void setDestination( Node node ) {
        if( nodes[1] != null ) {
            throw new RuntimeException( "destination already set" );
        }
        nodes[1] = node;
    }


    public Node getDestination() {
        return nodes[1];
    }

    public void setOrientation( double orientation ) {
        this.orientation = orientation;
    }

    public double getOrientation() {
        return orientation;
    }

    /**
     * Tells if the bond has an opening for a node at one end
     *
     * @return
     */
    public boolean isOpen() {
        return nodes[1] == null;
    }

    public boolean isOpen( Ion ion ) {

        System.out.println( "Bond.isOpen():  Get rid of this after debug" );
        if( nodes[1] != null && ion == nodes[1].getIon() ) {
            return false;
        }

        if( !( nodes[0] != null && nodes[0].getIon() == ion )
            && !( nodes[1] != null && nodes[1].getIon() == ion ) ) {
            System.out.println( "Bond.isOpen" );
        }
        return nodes[1] == null;
    }

    /**
     * Returns the node at the other end of the bond from a specified node
     *
     * @param originNode
     * @return
     */
    public Node traverse( Node originNode ) {
        if( nodes[0] == originNode ) {
            return nodes[1];
        }
        else if( nodes[1] == originNode ) {
            return nodes[0];
        }
        else {
            throw new IllegalArgumentException( "originNode is not part of the bond" );
        }
    }

    /**
     * Removes a specified node from the bond.
     *
     * @param node
     */
    public void removeNode( Node node ) {
        // If the origin node is being removed, replace it with the destination node, and update the
        // orientation so it is relative to the new origin node.
        if( nodes[0] == node ) {
            nodes[0] = nodes[1];
            nodes[1] = null;
            setOrientation( ( getOrientation() + Math.PI ) % ( Math.PI * 2 ) );
        }
        else if( nodes[1] == node ) {
            nodes[1] = null;
        }
        else {
            throw new RuntimeException( "node not associated with bond" );
        }
    }

    /**
     * Gets the position of the open end of the bond
     *
     * @return
     */
    public Point2D getOpenPosition() {
        if( !isOpen() ) {
            throw new RuntimeException( "bond not open" );
        }
        return MathUtil.radialToCartesian( length, orientation, nodes[0].getPosition() );
    }
}
