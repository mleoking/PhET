/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/ 
 * Box2D homepage: http://www.box2d.org
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.jbox2d.collision;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;

//Related to b2Distance.cpp
//Necessary for DistanceGeneric(Vec2 x1, Vec2 x2,
//SupportsGenericDistance shape1, XForm xf1,
//SupportsGenericDistance shape2, XForm xf2) function (cleaner than template)

/**
 * A shape that implements this interface can be used in distance calculations
 * for continuous collision detection.  This does not remove the necessity of
 * specialized penetration calculations when CCD is not in effect, however.
 */
public interface SupportsGenericDistance {
	public Vec2 support(XForm xf, Vec2 v);
	public Vec2 getFirstVertex(XForm xf);

}
