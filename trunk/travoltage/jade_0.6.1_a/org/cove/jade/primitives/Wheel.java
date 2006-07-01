package org.cove.jade.primitives;

import org.cove.jade.DynamicsEngine;
import org.cove.jade.util.GVector;

import java.awt.*;
/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * Wheel class
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
 * Wheel class
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
 * Class implementing a wheel. Note that wheel rotation is in world
 * co-ordinates, not relative to whatever the wheel is attached to (in fact,
 * the only way of attaching the wheel to anything is via constraints which do not
 * give it a particular direction anyway as the wheel is referred to by its center).
 * This means you can roll wheels that aren't attached to anything!
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * Wheel class
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

public class Wheel extends CircleParticle {

    public RimParticle rp;
    private double coeffSlip;


    public Wheel( double x, double y, double r ) {

        super( x, y, r );

        // TBD: set max torque?
        // rim particle (radius, max torque)
        rp = new RimParticle( r, 2 );

        // TBD:Review this for a higher level of friction
        // 1 = totally slippery, 0 = full friction
        coeffSlip = 0.0;
    }


    public void verlet( DynamicsEngine sysObj ) {
        rp.verlet( sysObj );
        super.verlet( sysObj );
    }


    public void resolveCollision( GVector normal, DynamicsEngine sysObj ) {
        super.resolveCollision( normal, sysObj );
        resolve( normal );
    }


    public void paint( Graphics g ) {
        if( isVisible ) {
            // draw wheel circle
            int px = (int)curr.x;
            int py = (int)curr.y;
            int rx = (int)rp.curr.x;
            int ry = (int)rp.curr.y;

            g.drawOval( (int)( px - radius ), (int)( py - radius ), (int)( radius * 2 ), (int)( radius * 2 ) );

            // draw rim cross
            g.drawLine( rx + px, ry + py, px, py );
            g.drawLine( -rx + px, -ry + py, px, py );
            g.drawLine( -ry + px, rx + py, px, py );
            g.drawLine( ry + px, -rx + py, px, py );
        }
    }


    /**
     * Sets wheel traction (well, actually slip) - 0 = no slip/full traction, 1 = full slip/no traction.
     * Note that the wheel may still "slip" with this set to 0 due to constraints, gravity, etc.
     */
    public void setTraction( double t ) {
        coeffSlip = t;
    }


    /**
     * simulates torque/wheel-ground interaction - n is the surface normal
     */
    private void resolve( GVector n ) {

        // this is the tangent vector at the rim particle
        double rx = -rp.curr.y;
        double ry = rp.curr.x;

        // normalize so we can scale by the rotational speed
        double len = Math.sqrt( rx * rx + ry * ry );
        rx /= len;
        ry /= len;

        // sx,sy is the velocity of the wheel's surface relative to the wheel
        double sx = rx * rp.speed;
        double sy = ry * rp.speed;

        // tx,ty is the velocity of the wheel relative to the world
        double tx = curr.x - prev.x;
        double ty = curr.y - prev.y;

        // vx,vy is the velocity of the wheel's surface relative to the ground
        double vx = tx + sx;
        double vy = ty + sy;

        // dp is the the wheel's surfacevel projected onto the ground's tangent
        double dp = -n.y * vx + n.x * vy;

        // set the wheel's spinspeed to track the ground
        rp.prev.x = rp.curr.x - dp * rx;
        rp.prev.y = rp.curr.y - dp * ry;

        // some of the wheel's torque is removed and converted into linear displacement
        double w0 = 1 - coeffSlip;
        curr.x += w0 * rp.speed * -n.y;
        curr.y += w0 * rp.speed * n.x;
        rp.speed *= coeffSlip;
    }
}

