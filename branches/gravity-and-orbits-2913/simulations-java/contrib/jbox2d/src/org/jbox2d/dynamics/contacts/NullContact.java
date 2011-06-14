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

package org.jbox2d.dynamics.contacts;

import java.util.List;

import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.ContactListener;

// Updated to rev 89 of b2NullContact.h
public class NullContact extends Contact {

    @Override
    public void evaluate(ContactListener cl) {
    }

    public NullContact() {
        super();
    }

    public Contact clone() {
        return new NullContact();
    }

    @Override
    public List<Manifold> getManifolds() {
        System.out.println("NullContact.GetManifolds()");
        return null;
    }
}
