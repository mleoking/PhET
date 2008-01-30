package org.cove.jade.primitives;

import org.cove.jade.DynamicsEngine;
import org.cove.jade.surfaces.Surface;
import org.cove.jade.util.GVector;

import java.awt.*;
/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * Particle class
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
 * Particle class
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
 * Class implementing a particle (sometimes called a primitive in parts of JADE).
 * Something that moves in the world and collides with surfaces.
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * Particle class
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


public class Particle {

    /**
     * Current position
     */
    public GVector curr;
    /**
     * Previous position, used for
     * velocity calculations
     */
    public GVector prev;
    /**
     * Collision resolution stuff
     */
    public double bmin;
    /**
     * Collision resolution stuff
     */
    public double bmax;
    /**
     * Collision resolution stuff
     */
    public GVector mtd;
    /**
     * Bounciness - how bouncy the particle is, 1 is default,
     * greater than 1 is more bouncy, less than 1 is less bouncy (minimum of zero).
     * Make this too bouncy and things become unstable. Do not make this less than zero!
     * Yes I know this isn't quite the scientific term to use but springiness or elasticity
     * won't work (lest it be confused with a SpringConstraint) ...
     */
    public double bounciness;
    /**
     * Mass - extent to which particle is affected by gravity, 1 is the default,
     * greater than 1 to be affected more, less than 1 to be affected less, less than
     * zero to float. Note that this does not affect "momentum" (maybe that should be
     * done later, as a sort of particle-specific damping factor?).
     */
    public double mass;

    protected GVector init;
    protected GVector temp;
    protected GVector extents;

    protected boolean isVisible;
    private double ax;
    private double ay;


    public Particle( double posX, double posY ) {

        // store initial position, for pinning
        init = new GVector( posX, posY );

        // current and previous positions - for integration
        curr = new GVector( posX, posY );
        prev = new GVector( posX, posY );
        temp = new GVector( 0, 0 );

        // attributes for collision detection with tiles
        this.extents = new GVector( 0, 0 );

        bmin = 0;
        bmax = 0;
        mtd = new GVector( 0, 0 );
        mass = 1.0;
        bounciness = 1.0;

        isVisible = true;
    }

    public void setVisible( boolean v ) {
        isVisible = v;
    }

    public void setAcceleration( double ax, double ay ) {
        this.ax = ax;
        this.ay = ay;
    }

    public void verlet( DynamicsEngine sysObj ) {

        temp.x = curr.x;
        temp.y = curr.y;

        curr.x += ( sysObj.coeffDamp * ( curr.x - prev.x ) + ( sysObj.gravity.x + ax ) * mass );
        curr.y += ( sysObj.coeffDamp * ( curr.y - prev.y ) + ( sysObj.gravity.y + ay ) * mass );

        prev.x = temp.x;
        prev.y = temp.y;
    }


    public void pin() {
        curr.x = init.x;
        curr.y = init.y;
        prev.x = init.x;
        prev.y = init.y;
    }


    public void setPos( double px, double py ) {
        curr.x = px;
        curr.y = py;
        prev.x = px;
        prev.y = py;
    }


    /**
     * Get projection onto a cardinal (world) axis x
     */
    // TBD: rename to something other than "get" 
    // TBD: there is another implementation of this in the 
    // AbstractTile base class.
    public void getCardXProjection() {
        bmin = curr.x - extents.x;
        bmax = curr.x + extents.x;
    }


    /**
     * Get projection onto a cardinal (world) axis y
     */
    // TBD: there is another implementation of this in the 
    // AbstractTile base class. see if they can be combined
    public void getCardYProjection() {
        bmin = curr.y - extents.y;
        bmax = curr.y + extents.y;
    }


    /**
     * Get projection onto arbitrary axis. Note that axis need not be unit-length. If
     * it is not, min and max will be scaled by the length of the axis. This is fine
     * if all we're doing is comparing relative values. If we need the 'actual' projection,
     * the axis should be unit length.
     */
    public void getAxisProjection( GVector axis ) {
        GVector absAxis = new GVector( Math.abs( axis.x ), Math.abs( axis.y ) );
        double projectedCenter = curr.dot( axis );
        double projectedRadius = extents.dot( absAxis );

        bmin = projectedCenter - projectedRadius;
        bmax = projectedCenter + projectedRadius;
    }


    /**
     * Find minimum depth and set mtd appropriately. mtd is the minimum translational
     * distance, the vector along which we must move the box to resolve the collision.
     */
    //TBD: this is only for right triangle surfaces - make generic
    public void setMTD( double depthX, double depthY, double depthN, GVector surfNormal ) {

        double absX = Math.abs( depthX );
        double absY = Math.abs( depthY );
        double absN = Math.abs( depthN );

        if( absX < absY && absX < absN ) {
            mtd.setTo( depthX, 0 );
        }
        else if( absY < absX && absY < absN ) {
            mtd.setTo( 0, depthY );
        }
        else if( absN < absX && absN < absY ) {
            mtd = surfNormal.multNew( depthN );
        }
    }


    /**
     * Set the mtd for situations where there are only the x and y axes to consider.
     */
    public void setXYMTD( double depthX, double depthY ) {

        double absX = Math.abs( depthX );
        double absY = Math.abs( depthY );

        if( absX < absY ) {
            mtd.setTo( depthX, 0 );
        }
        else {
            mtd.setTo( 0, depthY );
        }
    }


    // TBD: too much passing around of the DynamicsEngine object. Probably better if
    // it was static.  there is no way to individually set the kfr and friction of the
    // surfaces since they are calculated here from properties of the DynamicsEngine
    // object. Also, review for too much object creation
    public void resolveCollision( GVector normal, DynamicsEngine sysObj ) {

        // get the velocity
        GVector vel = curr.minusNew( prev );
        double sDotV = normal.dot( vel );

        // compute momentum of particle perpendicular to normal
        GVector velProjection = vel.minusNew( normal.multNew( sDotV ) );
        GVector perpMomentum = velProjection.multNew( sysObj.coeffFric );

        // compute momentum of particle in direction of normal
        GVector normMomentum = normal.multNew( sDotV * sysObj.coeffRest * bounciness );
        GVector totalMomentum = normMomentum.plusNew( perpMomentum );

        // set new velocity w/ total momentum
        GVector newVel = vel.minusNew( totalMomentum );

        // project out of collision
        curr.plus( mtd );

        // apply new velocity
        prev = curr.minusNew( newVel );
    }


    public void paint( Graphics g ) {
    }


    public void checkCollision( Surface surface, DynamicsEngine sysObj ) {
    }


}

