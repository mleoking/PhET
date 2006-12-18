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
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Bond
 * <p/>
 * Represents a bond between two atoms in a crystal lattice. A bod is a directed arc: It has an origin node and a
 * destination node. A bond also has an orientation, which is the angle of the bond measured relative to the
 * origin node. If the origin node is remove from a bond, the destination node becomes the new origin and the
 * orientation is reversed.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Bond {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    // Class-level events and listeners that can be used for notification when instances
    // are created
    static EventChannel constructionChannel = new EventChannel( ConstructionListener.class );
    static ConstructionListener constructionListenerProxy = (ConstructionListener)constructionChannel.getListenerProxy();

    static public class ConstructionEvent extends EventObject {
        private Bond bond;

        public ConstructionEvent( Bond bond ) {
            super( Bond.class );
            this.bond = bond;
        }

        public Bond getInstance() {
            return bond;
        }
    }

    static public interface ConstructionListener extends EventListener {
        void instanceConstructed( ConstructionEvent event );

        void instanceRemoved( ConstructionEvent event );
    }

    static public void addConstructionListener( ConstructionListener listener ) {
        constructionChannel.addListener( listener );
    }

    static public void removeConstructionListener( ConstructionListener listener ) {
        constructionChannel.removeListener( listener );
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private boolean debug;
    private Node[] nodes = new Node[2];
    private double orientation;
    private double length;
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    /**
     * Only constructor
     *
     * @param orientation
     */
    public Bond( Node origin, double orientation, double length ) {
        setOrigin( origin );
        this.orientation = orientation;
        this.length = length;
        constructionListenerProxy.instanceConstructed( new ConstructionEvent( this ) );
    }

    protected void finalize() throws Throwable {
        super.finalize();
        constructionListenerProxy.instanceRemoved( new ConstructionEvent( this ) );
    }

    public void setOrigin( Node node ) {
        nodes[0] = node;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public Node getOrigin() {
        return nodes[0];
    }

    public void setDestination( Node node ) {
        if( nodes[1] != null ) {
            throw new RuntimeException( "destination already set" );
        }
        nodes[1] = node;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }


    public Node getDestination() {
        return nodes[1];
    }

    public void setOrientation( double orientation ) {
        this.orientation = orientation;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
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
     * Removes a specified node from the bond. If the node is the origin node, the current destination
     * node becomes the new origin, and the orientation is reversed
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
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
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

    public void setDebugEnabled( boolean b ) {
        debug = b;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public boolean isDebugEnabled() {
        return debug;
    }


    //----------------------------------------------------------------
    // Event and listener definitions
    //----------------------------------------------------------------


    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Bond source ) {
            super( source );
        }

        public Bond getBond() {
            return (Bond)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

}
