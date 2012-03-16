/*******************************************************************************
 * Copyright (c) 2011, Daniel Murphy
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL DANIEL MURPHY BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
/**
 * Created at 5:43:20 AM Jan 14, 2011
 */
package org.jbox2d.testbed.tests;

import java.util.Random;

import org.jbox2d.callbacks.TreeCallback;
import org.jbox2d.callbacks.TreeRayCastCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.broadphase.DynamicTree;
import org.jbox2d.collision.broadphase.DynamicTreeNode;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.pooling.arrays.Vec2Array;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;

/**
 * @author Daniel Murphy
 */
public class DynamicTreeTest extends TestbedTest implements TreeCallback,
		TreeRayCastCallback {

	int e_actorCount = 128;
	float worldExtent;
	float m_proxyExtent;

	DynamicTree m_tree;
	AABB m_queryAABB;
	RayCastInput m_rayCastInput;
	RayCastOutput m_rayCastOutput;
	Actor m_rayActor;
	Actor m_actors[] = new Actor[e_actorCount];
	int m_stepCount;
	boolean m_automated;
	Random rand = new Random();

	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#initTest()
	 */
	@Override
	public void initTest() {
		worldExtent = 15.0f;
		m_proxyExtent = 0.5f;

		m_tree = new DynamicTree();

		for (int i = 0; i < e_actorCount; ++i) {
			Actor actor = m_actors[i] = new Actor();
			GetRandomAABB(actor.aabb);
			actor.proxyId = m_tree.createProxy(actor.aabb, actor);
		}

		m_stepCount = 0;

		float h = worldExtent;
		m_queryAABB = new AABB();
		m_queryAABB.lowerBound.set(-3.0f, -4.0f + h);
		m_queryAABB.upperBound.set(5.0f, 6.0f + h);

		m_rayCastInput = new RayCastInput();
		m_rayCastInput.p1.set(-5.0f, 5.0f + h);
		m_rayCastInput.p2.set(7.0f, -4.0f + h);
		// m_rayCastInput.p1.set(0.0f, 2.0f + h);
		// m_rayCastInput.p2.set(0.0f, -2.0f + h);
		m_rayCastInput.maxFraction = 1.0f;

		m_rayCastOutput = new RayCastOutput();

		m_automated = false;
	}

	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#keyPressed(char, int)
	 */
	@Override
	public void keyPressed(char argKeyChar, int argKeyCode) {
		switch (argKeyChar) {
		case 'a':
			m_automated = !m_automated;
			break;

		case 'c':
			CreateProxy();
			break;

		case 'd':
			DestroyProxy();
			break;

		case 'm':
			MoveProxy();
			break;
		}
	}

	private Vec2Array vecPool = new Vec2Array();

	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#step(org.jbox2d.testbed.framework.TestbedSettings)
	 */
	@Override
	public void step(TestbedSettings settings) {
		m_rayActor = null;
		for (int i = 0; i < e_actorCount; ++i) {
			m_actors[i].fraction = 1.0f;
			m_actors[i].overlap = false;
		}

		if (m_automated == true) {
			int actionCount = MathUtils.max(1, e_actorCount >> 2);

			for (int i = 0; i < actionCount; ++i) {
				Action();
			}
		}

		Query();
		RayCast();
		Vec2[] vecs = vecPool.get(4);

		for (int i = 0; i < e_actorCount; ++i) {
			Actor actor = m_actors[i];
			if (actor.proxyId == null)
				continue;

			Color3f c = new Color3f(0.9f, 0.9f, 0.9f);
			if (actor == m_rayActor && actor.overlap) {
				c.set(0.9f, 0.6f, 0.6f);
			} else if (actor == m_rayActor) {
				c.set(0.6f, 0.9f, 0.6f);
			} else if (actor.overlap) {
				c.set(0.6f, 0.6f, 0.9f);
			}
			actor.aabb.getVertices(vecs);
			m_debugDraw.drawPolygon(vecs, 4, c);
		}

		Color3f c = new Color3f(0.7f, 0.7f, 0.7f);
		m_queryAABB.getVertices(vecs);
		m_debugDraw.drawPolygon(vecs, 4, c);

		m_debugDraw.drawSegment(m_rayCastInput.p1, m_rayCastInput.p2, c);

		Color3f c1 = new Color3f(0.2f, 0.9f, 0.2f);
		Color3f c2 = new Color3f(0.9f, 0.2f, 0.2f);
		m_debugDraw.drawPoint(m_rayCastInput.p1, 6.0f, c1);
		m_debugDraw.drawPoint(m_rayCastInput.p2, 6.0f, c2);

		if (m_rayActor != null) {
			Color3f cr = new Color3f(0.2f, 0.2f, 0.9f);
			Vec2 p = m_rayCastInput.p2.sub(m_rayCastInput.p1)
					.mulLocal(m_rayActor.fraction).addLocal(m_rayCastInput.p1);
			m_debugDraw.drawPoint(p, 6.0f, cr);
		}

		++m_stepCount;

		if (settings.drawDynamicTree) {
			m_tree.drawTree(m_debugDraw);
		}

		m_textLine += 15;
		m_debugDraw.drawString(5, m_textLine,
				"(c)reate proxy, (d)estroy proxy, (a)utomate", Color3f.WHITE);
	}

	public boolean treeCallback(DynamicTreeNode proxyId) {
		Actor actor = (Actor) proxyId.userData;
		actor.overlap = AABB.testOverlap(m_queryAABB, actor.aabb);
		return true;
	}

	public float raycastCallback(final RayCastInput input,
			DynamicTreeNode proxyId) {
		Actor actor = (Actor) proxyId.userData;

		RayCastOutput output = new RayCastOutput();
		boolean hit = actor.aabb.raycast(output, input, m_world.getPool());

		if (hit) {
			m_rayCastOutput = output;
			m_rayActor = actor;
			m_rayActor.fraction = output.fraction;
			return output.fraction;
		}

		return input.maxFraction;
	}

	public static class Actor {
		AABB aabb = new AABB();
		float fraction;
		boolean overlap;
		DynamicTreeNode proxyId;
	}

	public void GetRandomAABB(AABB aabb) {
		Vec2 w = new Vec2();
		w.set(2.0f * m_proxyExtent, 2.0f * m_proxyExtent);
		// aabb.lowerBound.x = -m_proxyExtent;
		// aabb.lowerBound.y = -m_proxyExtent + worldExtent;
		aabb.lowerBound.x = MathUtils.randomFloat(rand, -worldExtent,
				worldExtent);
		aabb.lowerBound.y = MathUtils.randomFloat(rand, 0.0f,
				2.0f * worldExtent);
		aabb.upperBound.set(aabb.lowerBound).addLocal(w);
	}

	public void MoveAABB(AABB aabb) {
		Vec2 d = new Vec2();
		d.x = MathUtils.randomFloat(rand, -0.5f, 0.5f);
		d.y = MathUtils.randomFloat(rand, -0.5f, 0.5f);
		// d.x = 2.0f;
		// d.y = 0.0f;
		aabb.lowerBound.addLocal(d);
		aabb.upperBound.addLocal(d);

		Vec2 c0 = aabb.lowerBound.add(aabb.upperBound).mulLocal(.5f);
		Vec2 min = new Vec2();
		min.set(-worldExtent, 0.0f);
		Vec2 max = new Vec2();
		max.set(worldExtent, 2.0f * worldExtent);
		Vec2 c = MathUtils.clamp(c0, min, max);

		aabb.lowerBound.addLocal(c.sub(c0));
		aabb.upperBound.addLocal(c.sub(c0));
	}

	public void CreateProxy() {
		for (int i = 0; i < e_actorCount; ++i) {
			int j = MathUtils.abs(rand.nextInt() % e_actorCount);
			Actor actor = m_actors[j];
			if (actor.proxyId == null) {
				GetRandomAABB(actor.aabb);
				actor.proxyId = m_tree.createProxy(actor.aabb, actor);
				return;
			}
		}
	}

	public void DestroyProxy() {
		for (int i = 0; i < e_actorCount; ++i) {
			int j = MathUtils.abs(rand.nextInt() % e_actorCount);
			Actor actor = m_actors[j];
			if (actor.proxyId != null) {
				m_tree.destroyProxy(actor.proxyId);
				actor.proxyId = null;
				return;
			}
		}
	}

	public void MoveProxy() {
		for (int i = 0; i < e_actorCount; ++i) {
			int j = MathUtils.abs(rand.nextInt() % e_actorCount);
			Actor actor = m_actors[j];
			if (actor.proxyId == null) {
				continue;
			}

			AABB aabb0 = new AABB(actor.aabb);
			MoveAABB(actor.aabb);
			Vec2 displacement = actor.aabb.getCenter().sub(aabb0.getCenter());
			m_tree.moveProxy(actor.proxyId, new AABB(actor.aabb), displacement);
			return;
		}
	}

	public void Action() {
		int choice = MathUtils.abs(rand.nextInt() % 20);

		switch (choice) {
		case 0:
			CreateProxy();
			break;

		case 1:
			DestroyProxy();
			break;

		default:
			MoveProxy();
		}
	}

	public void Query() {
		m_tree.query(this, m_queryAABB);

		for (int i = 0; i < e_actorCount; ++i) {
			if (m_actors[i].proxyId == null) {
				continue;
			}

			boolean overlap = AABB.testOverlap(m_queryAABB, m_actors[i].aabb);
			assert (overlap == m_actors[i].overlap);
		}
	}

	public void RayCast() {
		m_rayActor = null;

		RayCastInput input = m_rayCastInput;

		// Ray cast against the dynamic tree.
		m_tree.raycast(this, input);

		// Brute force ray cast.
		Actor bruteActor = null;
		RayCastOutput bruteOutput = new RayCastOutput();
		for (int i = 0; i < e_actorCount; ++i) {
			if (m_actors[i].proxyId == null) {
				continue;
			}

			RayCastOutput output = new RayCastOutput();
			boolean hit = m_actors[i].aabb.raycast(output, input,
					m_world.getPool());
			if (hit) {
				bruteActor = m_actors[i];
				bruteOutput = output;
			    //input.set(m_rayCastInput);
			}
		}

		if (bruteActor != null) {
			assert (MathUtils.abs(bruteOutput.fraction
					- m_rayCastOutput.fraction) <= Settings.EPSILON);
		}
	}

	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#getTestName()
	 */
	@Override
	public String getTestName() {
		return "Dynamic Tree";
	}

}
