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

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.CircleShape;
import org.jbox2d.collision.CollideCircle;
import org.jbox2d.collision.Collision;
import org.jbox2d.collision.ContactID;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.ManifoldPoint;
import org.jbox2d.collision.Shape;
import org.jbox2d.collision.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.ContactListener;

// Updated to rev 142 of b2CircleContact.h/cpp

public class CircleContact extends Contact implements ContactCreateFcn {

    Manifold m_manifold;

    public Contact create(Shape shape1, Shape shape2) {
        return new CircleContact(shape1, shape2);
    }

    public static void Destroy(Contact contact) {
        ((CircleContact) contact).destructor();
    }

    public CircleContact clone() {
        return this;
    }

    public CircleContact() {
        super();
        m_manifold = new Manifold();
        m_manifoldCount = 0;
    }

    public CircleContact(Shape shape1, Shape shape2) {
        super(shape1, shape2);
        m_manifold = new Manifold();
        assert (m_shape1.getType() == ShapeType.CIRCLE_SHAPE);
        assert (m_shape2.getType() == ShapeType.CIRCLE_SHAPE);
        m_manifold.pointCount = 0;
        m_manifold.points[0].normalImpulse = 0.0f;
        m_manifold.points[0].tangentImpulse = 0.0f;
        m_manifold.points[0].localPoint1 = new Vec2();
        m_manifold.points[0].localPoint2 = new Vec2();
    }

    public void destructor() {

    }

    public void evaluate(ContactListener listener) {
        //CollideCircle.collideCircle(m_manifold, (CircleShape) m_shape1,
        //        (CircleShape) m_shape2, false);
        Body b1 = m_shape1.getBody();
    	Body b2 = m_shape2.getBody();

    	Manifold m0 = new Manifold(m_manifold);
        for (int k = 0; k < m_manifold.pointCount; k++) {
            m0.points[k] = new ManifoldPoint(m_manifold.points[k]);
            m0.points[k].normalImpulse = m_manifold.points[k].normalImpulse;
            m0.points[k].tangentImpulse = m_manifold.points[k].tangentImpulse;
            m0.points[k].separation = m_manifold.points[k].separation;
            //m0.points[k].id.key = m_manifold.points[k].id.key;
            m0.points[k].id.features.set(m_manifold.points[k].id.features);
            //System.out.println(m_manifold.points[k].id.key);
        }
        m0.pointCount = m_manifold.pointCount;
    	
    	CollideCircle.collideCircles(m_manifold, (CircleShape)m_shape1, b1.m_xf, (CircleShape)m_shape2, b2.m_xf);

    	ContactPoint cp = new ContactPoint();
    	cp.shape1 = m_shape1;
    	cp.shape2 = m_shape2;
    	cp.friction = m_friction;
    	cp.restitution = m_restitution;

    	
        if (m_manifold.pointCount > 0) {
            m_manifoldCount = 1;
            ManifoldPoint mp = m_manifold.points[0];
            if (m0.pointCount == 0) {
            	mp.normalImpulse = 0.0f;
    			mp.tangentImpulse = 0.0f;

    			if (listener != null) {
    				cp.position = b1.getWorldPoint(mp.localPoint1);
    				Vec2 v1 = b1.getLinearVelocityFromLocalPoint(mp.localPoint1);
    				Vec2 v2 = b2.getLinearVelocityFromLocalPoint(mp.localPoint2);
    				cp.velocity = v2.sub(v1);
    				cp.normal = m_manifold.normal.clone();
    				cp.separation = mp.separation;
    				cp.id = new ContactID(mp.id);
    				listener.add(cp);
    			}
    		} else {
    			ManifoldPoint mp0 = m0.points[0];
    			mp.normalImpulse = mp0.normalImpulse;
    			mp.tangentImpulse = mp0.tangentImpulse;

    			if (listener != null) {
    				cp.position = b1.getWorldPoint(mp.localPoint1);
    				Vec2 v1 = b1.getLinearVelocityFromLocalPoint(mp.localPoint1);
    				Vec2 v2 = b2.getLinearVelocityFromLocalPoint(mp.localPoint2);
    				cp.velocity = v2.sub(v1);
    				cp.normal = m_manifold.normal.clone();
    				cp.separation = mp.separation;
    				cp.id = new ContactID(mp.id);
    				listener.persist(cp);
    			}
    		}
        }
        else {
            m_manifoldCount = 0;
    		if (m0.pointCount > 0 && (listener != null)) {
    			ManifoldPoint mp0 = m0.points[0];
    			cp.position = b1.getWorldPoint(mp0.localPoint1);
    			Vec2 v1 = b1.getLinearVelocityFromLocalPoint(mp0.localPoint1);
    			Vec2 v2 = b2.getLinearVelocityFromLocalPoint(mp0.localPoint2);
    			cp.velocity = v2.sub(v1);
    			cp.normal = m0.normal.clone();
    			cp.separation = mp0.separation;
    			cp.id = new ContactID(mp0.id);
    			listener.remove(cp);
    		}
        }
    }

    @Override
    public List<Manifold> getManifolds() {
        List<Manifold> ret = new ArrayList<Manifold>(1);
        if (m_manifold != null) {
            ret.add(m_manifold);
        }

        return ret;
    }

}