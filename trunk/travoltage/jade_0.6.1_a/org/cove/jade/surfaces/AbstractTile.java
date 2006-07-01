package org.cove.jade.surfaces;

import org.cove.jade.util.GVector;

import java.util.Vector;

/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * AbstractTile class
 * Copyright 2005 Raymond Sheh
 *   A Java port of Flade - Flash Dynamics Engine, 
 *   Copyright 2004, 2005 Alec Cove
 * 
 * This file is part of JADE. The JAva Dynamics Engine. 
 *
 * JADE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JADE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JADE; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * ======== Original file header appears below ========
 * Flade - Flash Dynamics Engine
 * Release 0.6 alpha 
 * AbstractTile class
 * Copyright 2004, 2005 Alec Cove
 * 
 * This file is part of Flade. The Flash Dynamics Engine. 
 *	
 * Flade is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Flade is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Flade; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Flash is a registered trademark of Macromedia
 * ======== Original file header appears above ========
 *
 *
 */

/**
 * Class implementing an abstract tile. Something that is fixed to the world
 * and can be a surface (note that in practice, things inherit from this and
 * also implement the Surface interface). Also implements things for collision
 * detection.
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * AbstractTile class
 * Copyright 2005 Raymond Sheh
 *   A Java port of Flade - Flash Dynamics Engine,
 *   Copyright 2004, 2005 Alec Cove
 * <p/>
 * This file is part of JADE. The JAva Dynamics Engine.
 * <p/>
 * JADE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * JADE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with JADE; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * </pre>
 * <p/>
 * Please see the documentation for the main class, org.cove.jade.DynamicsEngine, for
 * more details about JADE.
 */

// TBD: need to clarify responsibilites between the Surface interface and the AbstractTile
public class AbstractTile {

    protected double minX;
    protected double minY;
    protected double maxX;
    protected double maxY;
    protected Vector verts;

    protected GVector center;
    protected GVector normal;

    protected boolean isVisible;
    protected boolean isActivated;


    public AbstractTile( double cx, double cy ) {
        center = new GVector( cx, cy );
        verts = new Vector();
        normal = new GVector( 0, 0 );

        isVisible = true;
        isActivated = true;
    }

    //TBD:Issues relating to painting, mc's, and visibility could be 
    //centralized somehow, base class, etc.

    /**
     * Is this object to be painted?
     */
    public void setVisible( boolean v ) {
        isVisible = v;
    }


    /**
     * Is this object active in the physics simulator? Note that
     * an object can be inactive (collisions won't be resolved) but
     * still be painted (still visible) and vice versa.
     */
    public void setActiveState( boolean a ) {
        isActivated = a;
    }


    public boolean getActiveState() {
        return isActivated;
    }


    public void createBoundingRect( double rw, double rh ) {

        double t = center.y - rh / 2;
        double b = center.y + rh / 2;
        double l = center.x - rw / 2;
        double r = center.x + rw / 2;

        verts.add( new GVector( r, b ) );
        verts.add( new GVector( r, t ) );
        verts.add( new GVector( l, t ) );
        verts.add( new GVector( l, b ) );
        setCardProjections();
    }


    public double testIntervals(
            double boxMin,
            double boxMax,
            double tileMin,
            double tileMax ) {

        // returns 0 if intervals do not overlap. Returns depth if they do overlap
        if( boxMax < tileMin ) {
            return 0;
        }
        if( tileMax < boxMin ) {
            return 0;
        }

        // return the smallest translation
        double depth1 = tileMax - boxMin;
        double depth2 = tileMin - boxMax;

        if( Math.abs( depth1 ) < Math.abs( depth2 ) ) {
            return depth1;
        }
        else {
            return depth2;
        }
    }


    public void setCardProjections() {
        getCardXProjection();
        getCardYProjection();
    }


    // get projection onto a cardinal (world) axis x 
    // TBD: duplicate methods (with different implementation) in 
    // in the Particle base class. 
    public void getCardXProjection() {

        minX = ( (GVector)( verts.elementAt( 0 ) ) ).x;
        for( int i = 1; i < verts.size(); i++ ) {
            if( ( (GVector)( verts.elementAt( i ) ) ).x < minX ) {
                minX = ( (GVector)( verts.elementAt( i ) ) ).x;
            }
        }

        maxX = ( (GVector)( verts.elementAt( 0 ) ) ).x;
        for( int i = 1; i < verts.size(); i++ ) {
            if( ( (GVector)( verts.elementAt( i ) ) ).x > maxX ) {
                maxX = ( (GVector)( verts.elementAt( i ) ) ).x;
            }
        }
    }


    // get projection onto a cardinal (world) axis y 
    // TBD: duplicate methods (with different implementation) in 
    // in the Particle base class. 
    public void getCardYProjection() {

        minY = ( (GVector)( verts.elementAt( 0 ) ) ).y;
        for( int i = 1; i < verts.size(); i++ ) {
            if( ( (GVector)( verts.elementAt( i ) ) ).y < minY ) {
                minY = ( (GVector)( verts.elementAt( i ) ) ).y;
            }
        }

        maxY = ( (GVector)( verts.elementAt( 0 ) ) ).y;
        for( int i = 1; i < verts.size(); i++ ) {
            if( ( (GVector)( verts.elementAt( i ) ) ).y > maxY ) {
                maxY = ( (GVector)( verts.elementAt( i ) ) ).y;
            }
        }
    }


    /**
     * Override this if you want something to happen when
     * a primitive hits a tile. This can be done at instantiation.
     * For instance, if you want a LineSurface tile to be a switch and
     * to disable an object called toggleLine when it gets hit,
     * try the following code (see CarExample for more):
     * <code><pre>
     * LineSurface switchLine = new LineSurface(25, 350, 150, 350)
     *                            {
     *                              public void onContact()
     *                              {
     *                                toggleLine.setActiveState(false);
     *                              }
     *                            }
     * </pre></code>
     */
    public void onContact() {
    }

}
