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

package org.jbox2d.dynamics;

import org.jbox2d.collision.FilterData;
import org.jbox2d.collision.Shape;

// Updated to rev 143 of b2WorldCallbacks.h/cpp

/**
 * Default sample implementation of ContactFilter.
 */
public class DefaultContactFilter implements ContactFilter {

	/**
	 * Return true if contact calculations should be performed between these two shapes.
	 * If you implement your own collision filter you may want to build from this implementation.
	 */
	public boolean shouldCollide(Shape shape1, Shape shape2) {
		
		FilterData filter1 = shape1.getFilterData();
		FilterData filter2 = shape2.getFilterData();

		if (filter1.groupIndex == filter2.groupIndex && filter1.groupIndex != 0) {
			return filter1.groupIndex > 0;
		}

		boolean collide = (filter1.maskBits & filter2.categoryBits) != 0 && (filter1.categoryBits & filter2.maskBits) != 0;
		return collide;
	}

}
