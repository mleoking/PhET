package org.cove.jade.constraints;

import org.cove.jade.primitives.Particle;
import org.cove.jade.util.GVector;
import org.cove.jade.util.Line;

import java.awt.*;
/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * AngularConstraint class
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
 * AngularConstraint class
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
 * Class implementing an angular constraint. This attempts to constrain the
 * angle between 3 particles to a particular value (by default, the angle they
 * formed when the simulator started).
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * AngularConstraint class
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

public class AngularConstraint implements Constraint {

    /**
     * Desired angle between the 3 particles. May be changed between
     * calls to DynamicsEngine.timeStep() to move things around.
     * This angle is from p1 to p3 around p2, positive clockwise
     * (see constructor) and in radians.
     */
    public double targetTheta;

    private GVector pA;
    private GVector pB;
    private GVector pC;
    private GVector pD;

    private Line lineA;
    private Line lineB;
    private Line lineC;

    private double stiffness;

    /**
     * Instantiates an AngularConstraint. The angle to be maintained is
     * that formed between the lines (p1,p2) and (p2,p3) (ie. the angle at
     * vertex v2 in the triangle formed by p1, p2 and p3).
     */
    public AngularConstraint( Particle p1, Particle p2, Particle p3 ) {

        pA = p1.curr;
        pB = p2.curr;
        pC = p3.curr;

        lineA = new Line( pA, pB );
        lineB = new Line( pB, pC );

        // lineC is the reference line for getting the angle of the line segments
        pD = new GVector( pB.x + 0, pB.y - 1 );
        lineC = new Line( pB, pD );

        // theta to constrain to -- domain is -Math.PI to Math.PI
        targetTheta = calcTheta( pA, pB, pC );

        // coefficient of stiffness
        stiffness = 1;
    }


    /**
     * Used by the engine, don't worry about this.
     */
    public void resolve() {

        GVector center = getCentroid();

        // make sure the reference line position gets updated (this updates pD by reference)
        lineC.p2.x = lineC.p1.x + 0;
        lineC.p2.y = lineC.p1.y - 1;

        double abRadius = pA.distance( pB );
        double bcRadius = pB.distance( pC );

        double thetaABC = calcTheta( pA, pB, pC );
        double thetaABD = calcTheta( pA, pB, pD );
        double thetaCBD = calcTheta( pC, pB, pD );
        // Note that we need to return a halfTheta in the 
        // range [-pi,pi]. thetaABC is the result of atan2 so will be 
        // [-pi,pi] but there is no guarantee that targetTheta 
        // will be in any particular range (and may even be outside 
        // [-2pi,2pi]) if external code has altered it.
        double theta = targetTheta - thetaABC;
        while( theta > Math.PI ) {
            theta -= Math.PI * 2;
        }
        while( theta < -Math.PI ) {
            theta += Math.PI * 2;
        }
        double halfTheta = theta / 2;
        double paTheta = thetaABD + halfTheta * stiffness;
        double pcTheta = thetaCBD - halfTheta * stiffness;

        pA.x = abRadius * Math.sin( paTheta ) + pB.x;
        pA.y = abRadius * Math.cos( paTheta ) + pB.y;
        pC.x = bcRadius * Math.sin( pcTheta ) + pB.x;
        pC.y = bcRadius * Math.cos( pcTheta ) + pB.y;

        // move corrected angle to pre corrected center
        GVector newCenter = getCentroid();
        double dfx = newCenter.x - center.x;
        double dfy = newCenter.y - center.y;
        pA.x -= dfx;
        pA.y -= dfy;
        pB.x -= dfx;
        pB.y -= dfy;
        pC.x -= dfx;
        pC.y -= dfy;
    }


    public void paint( Graphics g ) {
        // maintain the constraint interface. angular constraints are
        // painted by their two component SpringConstraints.
    }


    /**
     * Sets how strongly the angle is held, 1 = very strongly, 0 = not at all.
     * Numbers above 1 can result in weird things happening.
     */
    public void setStiffness( double s ) {
        stiffness = s;
    }


    private double calcTheta( GVector pa, GVector pb, GVector pc ) {

        GVector AB = new GVector( pb.x - pa.x, pb.y - pa.y );
        GVector BC = new GVector( pc.x - pb.x, pc.y - pb.y );

        double dotProd = AB.dot( BC );
        double crossProd = AB.cross( BC );
        return Math.atan2( crossProd, dotProd );
    }


    private GVector getCentroid() {
        double avgX = ( pA.x + pB.x + pC.x ) / 3;
        double avgY = ( pA.y + pB.y + pC.y ) / 3;
        return new GVector( avgX, avgY);
	}
	
}
