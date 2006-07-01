package org.cove.jade.surfaces;

import org.cove.jade.DynamicsEngine;
import org.cove.jade.primitives.CircleParticle;
import org.cove.jade.primitives.RectangleParticle;
import org.cove.jade.util.GVector;

import java.awt.*;
/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * LineSurface class
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
 * LineSurface class
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
 * Class implementing a line surface (tile). This is a line object, fixed in the world
 * that has a surface (active from both sides) that particles/primitives can collide with.
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * LineSurface class
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

//TBD: this class should be replaced by a rotateable RectangleTile or Capsule (or both)
public class LineSurface extends AbstractTile implements Surface {

    private GVector p1;
    private GVector p2;
    private GVector p3;
    private GVector p4;
    private GVector faceNormal;
    private GVector sideNormal;
    private GVector collNormal;

    private double rise;
    private double run;

    private double invB;
    private double sign;
    private double slope;

    private double minF;
    private double maxF;
    private double minS;
    private double maxS;
    private double collisionDepth;


    public LineSurface( double p1x, double p1y, double p2x, double p2y ) {

        super( 0, 0 );
        p1 = new GVector( p1x, p1y );
        p2 = new GVector( p2x, p2y );

        calcFaceNormal();
        collNormal = new GVector( 0, 0 );
        setCollisionDepth( 30 );
    }


    public void paint( Graphics g ) {
        if( isVisible ) {
            g.drawLine( (int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y );
        }
    }


    public void resolveCircleCollision( CircleParticle p, DynamicsEngine sysObj ) {
        if( isCircleColliding( p ) ) {
            onContact();
            p.resolveCollision( faceNormal, sysObj );
        }
    }


    public void resolveRectangleCollision( RectangleParticle p, DynamicsEngine sysObj ) {
        if( isRectangleColliding( p ) ) {
            onContact();
            p.resolveCollision( collNormal, sysObj );
        }
    }


    public void setCollisionDepth( double d ) {
        collisionDepth = d;
        precalculate();
    }


    private boolean isCircleColliding( CircleParticle p ) {

        // find the closest point on the surface to the CircleParticle
        findClosestPoint( p.curr, p.closestPoint );

        // get the normal of the circle relative to the location of the closest point
        GVector circleNormal = p.closestPoint.minusNew( p.curr );
        circleNormal.normalize();

        // if the center of the circle has broken the line keep the normal from 'flipping'
        // to the opposite direction. for small circles, this prevents break-throughs
        if( inequality( p.curr ) ) {
            double absCX = Math.abs( circleNormal.x );
            if( faceNormal.x < 0 ) {
                circleNormal.x = absCX;
            }
            else {
                circleNormal.x = -absCX;
            }
            circleNormal.y = Math.abs( circleNormal.y );
        }

        // get contact point on edge of circle
        GVector contactPoint = p.curr.plusNew( circleNormal.mult( p.radius ) );
        if( segmentInequality( contactPoint ) ) {

            if( contactPoint.distance( p.closestPoint ) > collisionDepth ) {
                return false;
            }
            double dx = contactPoint.x - p.closestPoint.x;
            double dy = contactPoint.y - p.closestPoint.y;
            p.mtd.setTo( -dx, -dy );
            return true;
        }
        return false;
    }


    private boolean isRectangleColliding( RectangleParticle p ) {

        p.getCardYProjection();
        double depthY = testIntervals( p.bmin, p.bmax, minY, maxY );
        if( depthY == 0 ) {
            return false;
        }

        p.getCardXProjection();
        double depthX = testIntervals( p.bmin, p.bmax, minX, maxX );
        if( depthX == 0 ) {
            return false;
        }

        p.getAxisProjection( sideNormal );
        double depthS = testIntervals( p.bmin, p.bmax, minS, maxS );
        if( depthS == 0 ) {
            return false;
        }

        p.getAxisProjection( faceNormal );
        double depthF = testIntervals( p.bmin, p.bmax, minF, maxF );
        if( depthF == 0 ) {
            return false;
        }

        double absX = Math.abs( depthX );
        double absY = Math.abs( depthY );
        double absS = Math.abs( depthS );
        double absF = Math.abs( depthF );

        if( absX <= absY && absX <= absS && absX <= absF ) {
            p.mtd.setTo( depthX, 0 );
            collNormal.setTo( p.mtd.x / absX, 0 );
        }
        else if( absY <= absX && absY <= absS && absY <= absF ) {
            p.mtd.setTo( 0, depthY );
            collNormal.setTo( 0, p.mtd.y / absY );
        }
        else if( absF <= absX && absF <= absY && absF <= absS ) {
            p.mtd = faceNormal.multNew( depthF );
            collNormal.copy( faceNormal );
        }
        else if( absS <= absX && absS <= absY && absS <= absF ) {
            p.mtd = sideNormal.multNew( depthS );
            collNormal.copy( sideNormal );
        }
        return true;
    }


    private void precalculate() {
        // precalculations for circle collision
        rise = p2.y - p1.y;
        run = p2.x - p1.x;

        // TBD: sign is a quick bug fix, needs to be review
        if( run >= 0 ) {
            sign = 1;
        }
        else {
            sign = -1;
        }
        slope = rise / run;
        invB = 1 / ( run * run + rise * rise );

        // precalculations for rectangle collision
        createRectangle();
        calcSideNormal();
        setCardProjections();
        setAxisProjections();
    }


    private void calcFaceNormal() {
        faceNormal = new GVector( 0, 0 );
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        faceNormal.setTo( dy, -dx );
        faceNormal.normalize();
    }


    private boolean segmentInequality( GVector toPoint ) {
        double u = findU( toPoint );
        boolean isUnder = inequality( toPoint );
        return ( u >= 0 && u <= 1 && isUnder );
    }


    private boolean inequality( GVector toPoint ) {
        // TBD: sign is a quick bug fix, needs to be review
        double line = ( slope * ( toPoint.x - p1.x ) + ( p1.y - toPoint.y ) ) * sign;
        return ( line <= 0 );
    }


    private void findClosestPoint( GVector toPoint, GVector returnVect ) {

        double u = findU( toPoint );
        if( u <= 0 ) {
            returnVect.copy( p1 );
            return;
        }

        if( u >= 1 ) {
            returnVect.copy( p2 );
            return;
        }

        double x = p1.x + u * ( p2.x - p1.x );
        double y = p1.y + u * ( p2.y - p1.y );
        returnVect.setTo( x, y );
    }


    private double findU( GVector p ) {
        double a = ( p.x - p1.x ) * run + ( p.y - p1.y ) * rise;
        return a * invB;
    }


    private void createRectangle() {

        double p3x = p2.x + -faceNormal.x * collisionDepth;
        double p3y = p2.y + -faceNormal.y * collisionDepth;

        double p4x = p1.x + -faceNormal.x * collisionDepth;
        double p4y = p1.y + -faceNormal.y * collisionDepth;

        p3 = new GVector( p3x, p3y );
        p4 = new GVector( p4x, p4y );

        verts.add( p1 );
        verts.add( p2 );
        verts.add( p3 );
        verts.add( p4 );
    }


    private void setAxisProjections() {

        double temp;

        minF = p2.dot( faceNormal );
        maxF = p3.dot( faceNormal );
        if( minF > maxF ) {
            temp = minF;
            minF = maxF;
            maxF = temp;
        }

        minS = p1.dot( sideNormal );
        maxS = p2.dot( sideNormal );
        if( minS > maxS ) {
            temp = minS;
            minS = maxS;
            maxS = temp;
        }
    }


    private void calcSideNormal() {
        sideNormal = new GVector( 0, 0 );
        double dx = p3.x - p2.x;
        double dy = p3.y - p2.y;
        sideNormal.setTo( dy, -dx );
        sideNormal.normalize();
    }

}

