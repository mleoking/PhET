package org.cove.jade.surfaces;

import org.cove.jade.DynamicsEngine;
import org.cove.jade.primitives.CircleParticle;
import org.cove.jade.primitives.RectangleParticle;

import java.awt.*;
/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * RectangleTile class
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
 * RectangleTile class
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
 * Class implementing a rectangle tile. This is a rectangular object,
 * fixed in the world, with 4 axis-parallel surfaces that primitives/particles
 * can collide with.
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * RectangleTile class
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


public class RectangleTile extends AbstractTile implements Surface {

    private double rectWidth;
    private double rectHeight;


    /**
     * Instantiate a RectangleTile, (cx,cy) = center
     */
    public RectangleTile( double cx, double cy, double rw, double rh ) {

        super( cx, cy );
        rectWidth = rw;
        rectHeight = rh;
        createBoundingRect( rw, rh );
    }


    public void paint( Graphics g ) {
        if( isVisible ) {
            g.setColor( Color.BLUE );
            g.drawRect( (int)( center.x - rectWidth / 2 ), (int)( center.y - rectHeight / 2 ), (int)rectWidth, (int)rectHeight );
        }
    }


    public void resolveCircleCollision( CircleParticle p, DynamicsEngine sysObj ) {
        if( isCircleColliding( p ) ) {
            onContact();
            p.resolveCollision( normal, sysObj );
        }
    }


    public void resolveRectangleCollision( RectangleParticle p, DynamicsEngine sysObj ) {
        if( isRectangleColliding( p ) ) {
            onContact();
            p.resolveCollision( normal, sysObj );
        }
    }


    private boolean isCircleColliding( CircleParticle p ) {

        p.getCardXProjection();
        double depthX = testIntervals( p.bmin, p.bmax, minX, maxX );
        if( depthX == 0 ) {
            return false;
        }

        p.getCardYProjection();
        double depthY = testIntervals( p.bmin, p.bmax, minY, maxY );
        if( depthY == 0 ) {
            return false;
        }

        // determine if the circle's center is in a vertex voronoi region
        boolean isInVertexX = Math.abs( depthX ) < p.radius;
        boolean isInVertexY = Math.abs( depthY ) < p.radius;

        if( isInVertexX && isInVertexY ) {

            // get the closest vertex
            double vx = center.x + sign( p.curr.x - center.x ) * ( rectWidth / 2 );
            double vy = center.y + sign( p.curr.y - center.y ) * ( rectHeight / 2 );

            // get the distance from the vertex to circle center
            double dx = p.curr.x - vx;
            double dy = p.curr.y - vy;
            double mag = Math.sqrt( dx * dx + dy * dy );
            double pen = p.radius - mag;

            // if there is a collision in one of the vertex regions
            if( pen > 0 ) {
                dx /= mag;
                dy /= mag;
                p.mtd.setTo( dx * pen, dy * pen );
                normal.setTo( dx, dy );
                return true;
            }
            return false;

        }
        else {
            // collision on one of the 4 edges
            p.setXYMTD( depthX, depthY );
            normal.setTo( p.mtd.x / Math.abs( depthX ), p.mtd.y / Math.abs( depthY ) );
            return true;
        }
    }


    private boolean isRectangleColliding( RectangleParticle p ) {

        p.getCardXProjection();
        double depthX = testIntervals( p.bmin, p.bmax, minX, maxX );
        if( depthX == 0 ) {
            return false;
        }

        p.getCardYProjection();
        double depthY = testIntervals( p.bmin, p.bmax, minY, maxY );
        if( depthY == 0 ) {
            return false;
        }

        p.setXYMTD( depthX, depthY );
        normal.setTo( p.mtd.x / Math.abs( depthX ), p.mtd.y / Math.abs( depthY ) );
        return true;
    }


    // TBD: Put in a util class
    private double sign( double val ) {
        if( val < 0 ) {
            return -1;
        }
        if( val > 0 ) {
            return 1;
        }
        return 0;
    }

}


		
		
	

