package org.cove.jade;

import org.cove.jade.constraints.Constraint;
import org.cove.jade.primitives.Particle;
import org.cove.jade.surfaces.Surface;
import org.cove.jade.util.GVector;

import java.awt.*;
import java.util.Vector;
/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * DynamicsEngine class
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
 * DynamicsEngine class
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
 * <h1>Main JADE simulator class. </h1>
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * DynamicsEngine class
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
 * <h3>Introduction</h3>
 * <p/>
 * Yes documentation is a bit sparse but hopefully it's enough to get you going! :-D
 * <p/>
 * <p/>
 * <a href="http://rsheh.web.cse.unsw.edu.au/jade">JADE, the JAva Dynamics Engine</a>,
 * is a 2D physics/dynamics simulator, written in Java and, currently, a
 * port of <a href="http://www.cove.org/flade">Flade, the Flash Dynamics Engine</a>.
 * It simulates moving objects, linked together with constraints, that can collide with
 * fixed objects in the world, under the influence of gravity, momentum and
 * friction. Currently, the following objects are available in JADE:
 * <ul>
 * <li> Surfaces (things stuck to the world):
 * <ul>
 * <li> Circles
 * <li> Lines
 * <li> Rectangles
 * </ul>
 * <li> Primitives (things that move):
 * <ul>
 * <li> Circles
 * <li> Point particles
 * <li> Rectangles
 * <li> Wheels
 * <li> Sprung rectangles
 * </ul>
 * <li> Constraints (things apart from gravity/friction/momentum/collisions) that affect how primitives move:
 * <ul>
 * <li> Angular springs (AngularConstraint)
 * <li> Linear springs (SpringConstraint)
 * </ul>
 * </ul>
 * </p>
 * <p/>
 * <p/>
 * <br>
 * <h3>To use this simulator, do the following:</h3>
 * <ul>
 * <li> Instantiate a DynamicsEngine (referred to from now as "this object")
 * <li> Call this object's set methods to taste (optional - defaults are fine)
 * <li> Instantiate surface, primitive (particles) and constraint objects to populate your world, tweak to taste
 * <li> Call this object's addSurface/addPrimitive/addConstraint methods and pass the objects you created above to add them to the world
 * <li> At every timestep, call this object's timeStep() to step the world forward in time
 * <li> If desired, alter the surface/primitive/constraint objects as desired (for example, in response
 * to user interface commands)
 * <li> To paint to a Graphics object, call this object's paintSurfaces/paintPrimitives/paintConstraints
 * </ul>
 * Please see CarExample.java for an actual application!
 * <p/>
 * <br>
 * <h3>Misc. useful notes:</h3>
 * <ul>
 * <li> Terminology:
 * <ul>
 * <li> Surface - something that a moving object can collide with.
 * <li> Tile - an object that is fixed to the world, such as a box or a line and can be a surface.
 * Note that (currently) all implemented surfaces are tiles.
 * <li> Primitive - something that moves around the world and can collide with surfaces
 * such as a wheel. Note that in parts of the code, primitives are referred
 * to as particles.
 * <li> Constraint - something limiting how a primitive can move relative to one or more
 * other primitives such as a spring.
 * </ul>
 * <li> Co-ordinate system is from the upper left corner and the units are in pixels (but as doubles, not ints)
 * <li> Boxes are referred to by their center and width/height
 * <li> Don't go crazy with AngularConstraints - weird things can happen at the moment if you have too many of them.
 * <li> Yes, there aren't any collisions between primitives, only between primitives and surfaces.
 * <li> The GVector class was created for compatibility with Flade (which called it "Vector" but
 * Java already has a Vector class). In JADE, a GVector (what Flade called a Vector) is a
 * 2D Geometric Vector (holds 2 numbers that represent X and Y, can do dot and cross product,
 * etc.), what JADE calls a Vector (as in java.util.Vector) is an array-like data structure
 * with no fixed size. Don't get them mixed up!
 * </ul>
 * <p/>
 * <h3>Known bugs in JADE 0.6.1 alpha (not just with this class):</h3>
 * <ul>
 * <li> Colours don't seem to be set properly in some of the paint methods
 * <li> Constraints between two primitives that are on top of each other don't play nice
 * <li> "Gravity" behaves weirdly when there are angular constraints in play.
 * For instance, if you make a "V" shaped object where the arms of the V are the same length and
 * tips are the same mass, the whole lot balances nicely. If you make one arm shorter,
 * the V tips towards the SHORT side (the opposite to what it's supposed to do).
 * </ul>
 * <p/>
 * <h3>Todo list:</h3>
 * <ul>
 * <li> Fix bugs!
 * <li> Implement some form of tyre (current wheels behave like steel wheels - they can't grip corners very well)
 * <li> Implement tank treads of some sort
 * <li> Implement a real rigid body
 * <li> Figure out how to stop objects from "floating" quite so much
 * (but without having them drop through surfaces)
 * </ul>
 * <p/>
 * <h3>Revision history:</h3>
 * Note that where possible, JADE will track Flade version numbers if the two versions are
 * functionally identical (with "j" appended to the version number).
 * <ul>
 * <li> 2005-12-28 - Release 0.6.1 alpha: Fixes made since porting from Flade 0.6 alpha:
 * <ul>
 * <li> Fixed angular constraint issue with [-pi,pi] discontinuity, you can now
 * externally change targetTheta and it doesn't need to be between [-pi,pi] now.
 * <li> Added mass and bounciness properties to particles.
 * </ul>
 * <li> 2005-12-28 - Release 0.6j alpha: First release of JADE, an (almost) direct port from Flade 0.6 alpha
 * with JavaDoc documentation added by Raymond Sheh
 * </ul>
 */
public class DynamicsEngine {

    /**
     * Units moved per timestep due to gravity
     * (before constraints and collisions are accounted for)
     */
    public GVector gravity;
    /**
     * 1+surfaceBounce (see setSurfaceBounce)
     */
    public double coeffRest;
    /**
     * Coefficient of friction (see setSurfaceFriction)
     */
    public double coeffFric;
    /**
     * Coefficient of damping (see setDamping)
     */
    public double coeffDamp;

    /**
     * Vector of primitives (particles) - things that move in the world
     */
    public Vector primitives;
    /**
     * Vector of surfaces - things that are fixed to the world that other things run into
     */
    public Vector surfaces;
    /**
     * Vector of constraints - things that limit how primitives move (apart from collisions)
     */
    public Vector constraints;
    private double dt = 1.0;

    /**
     * Constructs a DynamicsEngine - nothing too interesting here as far as documentation goes!
     */
    public DynamicsEngine() {

        primitives = new Vector();
        surfaces = new Vector();
        constraints = new Vector();

        // default values
        gravity = new GVector( 0, 1 );
        coeffRest = 1 + 0.5;
        coeffFric = 0.01;    // surface friction
        coeffDamp = 0.99;     // global damping
    }


    /**
     * Add a primitive (such as a wheel) to the world. Something that moves in the world.
     */
    public void addPrimitive( Particle p ) {
        primitives.add( p );
    }

    public void removePrimitive( Particle p ) {
        primitives.remove( p );
    }

    /**
     * Add a surface (such as a line) to the world. Something that is stuck to the world.
     * WARNING: Do NOT modify surfaces after they've been added - bad things happen!
     */
    public void addSurface( Surface s ) {
        surfaces.add( s );
    }

    /**
     * Add a constraint (such as spring or angular constraint) to the world.
     * Constraints exist between primitives.
     */
    public void addConstraint( Constraint c ) {
        constraints.add( c );
    }

    /**
     * Paint surfaces (calls the paint methods in all surfaces in order of addition)
     */
    public void paintSurfaces( Graphics g ) {
        for( int j = 0; j < surfaces.size(); j++ ) {
            ( (Surface)( surfaces.elementAt( j ) ) ).paint( g );
        }
    }

    /**
     * Paint primitives (calls the paint methods in all primitives in order of addition)
     */
    public void paintPrimitives( Graphics g ) {
        for( int j = 0; j < primitives.size(); j++ ) {
            ( (Particle)( primitives.elementAt( j ) ) ).paint( g );
        }
    }


    /**
     * Paint constraints (calls the paint methods in all constraints in order of addition).
     */
    public void paintConstraints( Graphics g ) {
        for( int j = 0; j < constraints.size(); j++ ) {
            ( (Constraint)( constraints.elementAt( j ) ) ).paint( g );
        }
    }


    /**
     * Steps the simulator one timestep. Calling this every 10-50ms or so seems to
     * work nicely for something that is supposed to be "realtime" but feel free
     * to call this as fast as possible (such as for machine learning or logfile generation).
     */
    public void timeStep() {
        verlet();
        satisfyConstraints();
        checkCollisions();
    }


    /**
     * Sets "bounciness" of all surfaces (currently can't do them one at a time).
     * A low value (such as 0.1) means very little bounce when primitives hit surfaces,
     * a high value (such as 1) means things just keep bouncing. Note that this doesn't
     * have anything to do with inter-primitive bounce (actually there IS no
     * inter-primitive collision as yet).
     * TBD: Property of surface, not system
     */
    public void setSurfaceBounce( double kfr ) {
        coeffRest = 1 + kfr;
    }


    /**
     * Sets surface friction of all surfaces (currently can't do them one at a time).
     * TBD: Property of surface, not system
     */
    public void setSurfaceFriction( double f ) {
        coeffFric = f;
    }

    /**
     * Sets damping of all primitives (think of it more as air resistance - the
     * amount a particle slows down per timestep when no other force acts on it).
     */
    public void setDamping( double d ) {
        coeffDamp = d;
    }

    /**
     * Sets the amount (in pixels) that each primitive moves in each dimension
     * by each timestep (before constraints and collisions are accounted for).
     * Yes, gravity can work at any angle you like!
     */
    public void setGravity( double gx, double gy ) {
        gravity.x = gx;
        gravity.y = gy;
    }


    private void verlet() {
        for( int i = 0; i < primitives.size(); i++ ) {
            ( (Particle)( primitives.elementAt( i ) ) ).verlet( this );
        }
    }


    private void satisfyConstraints() {
        for( int n = 0; n < constraints.size(); n++ ) {
            ( (Constraint)( constraints.elementAt( n ) ) ).resolve();
        }
    }


    private void checkCollisions() {

        for( int j = 0; j < surfaces.size(); j++ ) {
            Surface s = (Surface)( surfaces.elementAt( j ) );
            if( s.getActiveState() ) {
                for( int i = 0; i < primitives.size(); i++ ) {
                    ( (Particle)( primitives.elementAt( i ) ) ).checkCollision( s, this );
                }
            }
        }
    }

    public double getDt() {
        return dt;
    }

    public void setDt( double dt ) {
        this.dt = dt;
    }
}
