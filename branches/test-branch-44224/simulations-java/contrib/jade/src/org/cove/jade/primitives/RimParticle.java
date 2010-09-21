package org.cove.jade.primitives;

import org.cove.jade.DynamicsEngine;
import org.cove.jade.util.GVector;
/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * RimParticle class
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
 * RimParticle class
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
 * Class implementing a rim particle. Do not instantiate this by itself, it
 * is used by the Wheel primitive.
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * RimParticle class
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

// TBD: extends particle...or rename
public class RimParticle {

    public GVector curr;
    public GVector prev;
    public double speed;
    public double vs;

    private double wr;
    private double maxTorque;

    /**
     * The RimParticle is really just a second component of the wheel model.
     * The rim particle is simulated in a coordsystem relative to the wheel's
     * center, not in worldspace
     */
    public RimParticle( double r, double mt ) {

        curr = new GVector( r, 0 );
        prev = new GVector( 0, 0 );

        vs = 0;            // variable speed
        speed = 0;         // initial speed
        maxTorque = mt;
        wr = r;
    }

    // TBD: provide a way to get the worldspace position of the rimparticle
    // either here, or in the wheel class, so it can be used to move other
    // primitives / constraints
    public void verlet( DynamicsEngine sysObj ) {

        //clamp torques to valid range
        speed = Math.max( -maxTorque, Math.min( maxTorque, speed + vs ) );

        //apply torque
        //this is the tangent vector at the rim particle
        double dx = -curr.y;
        double dy = curr.x;

        //normalize so we can scale by the rotational speed
        double len = Math.sqrt( dx * dx + dy * dy );
        dx /= len;
        dy /= len;

        curr.x += speed * dx;
        curr.y += speed * dy;

        double ox = prev.x;
        double oy = prev.y;
        double px = prev.x = curr.x;
        double py = prev.y = curr.y;

        curr.x += sysObj.coeffDamp * ( px - ox );
        curr.y += sysObj.coeffDamp * ( py - oy );

        // hold the rim particle in place
        double clen = Math.sqrt( curr.x * curr.x + curr.y * curr.y );
        double diff = ( clen - wr ) / clen;

        curr.x -= curr.x * diff;
        curr.y -= curr.y * diff;
    }

}
