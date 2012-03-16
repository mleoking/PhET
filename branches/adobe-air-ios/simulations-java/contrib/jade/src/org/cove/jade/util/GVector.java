package org.cove.jade.util;
/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * GVector class
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
 * GVector class
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
 * Class implementing a 2D geometric vector, consisting of two numbers
 * that represent X and Y co-ordinates, and the ability to perform
 * vector manipulation and operations. Note that in Flade, this was
 * called a "Vector" but we can't call it "Vector" in JADE as Java
 * already has a vector class that we use (java.util.Vector), which
 * represents a variable length array-like data structure.
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * GVector class
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

public class GVector {

    public double x;
    public double y;


    public GVector( double px, double py ) {
        x = px;
        y = py;
    }


    public void setTo( double px, double py ) {
        x = px;
        y = py;
    }


    public void copy( GVector v ) {
        x = v.x;
        y = v.y;
    }

    /**
     * Changes this vector (and returns this vector)
     */
    public double dot( GVector v ) {
        return x * v.x + y * v.y;
    }


    /**
     * Changes this vector (and returns this vector)
     */
    public double cross( GVector v ) {
        return x * v.y - y * v.x;
    }


    /**
     * Changes this vector (and returns this vector)
     */
    public GVector plus( GVector v ) {
        x += v.x;
        y += v.y;
        return this;
    }


    public GVector plusNew( GVector v ) {
        return new GVector( x + v.x, y + v.y );
    }


    /**
     * Changes this vector (and returns this vector)
     */
    public GVector minus( GVector v ) {
        x -= v.x;
        y -= v.y;
        return this;
    }


    public GVector minusNew( GVector v ) {
        return new GVector( x - v.x, y - v.y );
    }


    /**
     * Changes this vector (and returns this vector)
     */
    public GVector mult( double s ) {
        x *= s;
        y *= s;
        return this;
    }


    public GVector multNew( double s ) {
        return new GVector( x * s, y * s );
    }


    public double distance( GVector v ) {
        double dx = x - v.x;
        double dy = y - v.y;
        return Math.sqrt( dx * dx + dy * dy );
    }


    /**
     * Changes this vector (and returns this vector)
     */
    public GVector normalize() {
        double mag = Math.sqrt( x * x + y * y );
        x /= mag;
        y /= mag;
        return this;
    }


    public double magnitude() {
        return Math.sqrt( x * x + y * y );
    }


    /**
     * projects this vector onto b
     */
    public GVector project( GVector b ) {
        double adotb = this.dot( b );
        double len = ( b.x * b.x + b.y * b.y );

        GVector proj = new GVector( 0, 0 );
        proj.x = ( adotb / len ) * b.x;
        proj.y = ( adotb / len ) * b.y;
        return proj;
    }
}

