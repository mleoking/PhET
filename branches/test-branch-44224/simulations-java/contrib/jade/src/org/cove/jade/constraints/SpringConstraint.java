package org.cove.jade.constraints;

import org.cove.jade.primitives.Particle;
import org.cove.jade.util.GVector;

import java.awt.*;
/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * SpringConstraint class
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
 * SpringConstraint class
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
 * Class implementing a spring constraint. This attempts to constrain the
 * distance between 2 particles to a particular value (by default, the distance
 * when the simulator started).
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * SpringConstraint class
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

public class SpringConstraint implements Constraint {

    private Particle p1;
    private Particle p2;
    private double restLength;
    private double tearLength;

    private double color;
    private double stiffness;
    private boolean isVisible;


    /**
     * Instantiate a SpringConstraint between two particles.
     */
    public SpringConstraint( Particle p1, Particle p2 ) {

        this.p1 = p1;
        this.p2 = p2;
        restLength = p1.curr.distance( p2.curr );

        stiffness = 0.5;
        color = 0x996633;

        isVisible = true;
    }


    /**
     * Used by the engine, don't worry about this
     */
    public void resolve() {

        GVector delta = p1.curr.minusNew( p2.curr );
        double deltaLength = p1.curr.distance( p2.curr );

        double diff = ( deltaLength - restLength ) / deltaLength;
        GVector dmd = delta.mult( diff * stiffness );

        p1.curr.minus( dmd );
        p2.curr.plus( dmd );
    }


    /**
     * Sets the target distance between the particles
     */
    public void setRestLength( double r ) {
        restLength = r;
    }


    /**
     * Sets stiffness of the spring (0 = not very stiff at all, 1 = very stiff).
     * Numbers beyond 1 can do weird things.
     */
    public void setStiffness( double s ) {
        stiffness = s;
    }


    /**
     * Should this constraint be painted?
     */
    public void setVisible( boolean v ) {
        isVisible = v;
    }


    public void paint( Graphics g ) {

        if( isVisible ) {

            g.drawLine(
                    (int)p1.curr.x,
                    (int)p1.curr.y,
                    (int)p2.curr.x,
                    (int)p2.curr.y );
        }
    }
}
