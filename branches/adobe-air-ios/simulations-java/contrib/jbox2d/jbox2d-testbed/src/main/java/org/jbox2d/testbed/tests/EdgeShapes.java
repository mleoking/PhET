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
 *.created at 3:31:07 PM Jan 14, 2011
 */
package org.jbox2d.testbed.tests;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;

/**
 * @author Daniel Murphy
 */
public class EdgeShapes extends TestbedTest {
	
	int e_maxBodies = 256;
	int m_bodyIndex;
	Body m_bodies[] = new Body[e_maxBodies];
	PolygonShape m_polygons[] = new PolygonShape[4];
	CircleShape m_circle;

	float m_angle;
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#initTest()
	 */
	@Override
	public void initTest() {
		// Ground body
		{
			BodyDef bd = new BodyDef();
			Body ground = m_world.createBody(bd);

			float x1 = -20.0f;
			float y1 = 2.0f * MathUtils.cos(x1 / 10.0f * MathUtils.PI);
			for (int i = 0; i < 80; ++i)
			{
				float x2 = x1 + 0.5f;
				float y2 = 2.0f * MathUtils.cos(x2 / 10.0f * MathUtils.PI);

				PolygonShape shape = new PolygonShape();
				shape.setAsEdge(new Vec2(x1, y1), new Vec2(x2, y2));
				ground.createFixture(shape, 0.0f);

				x1 = x2;
				y1 = y2;
			}
		}

		{
			Vec2 vertices[] = new Vec2[3];
			vertices[0] = new Vec2(-0.5f, 0.0f);
			vertices[1] = new Vec2(0.5f, 0.0f);
			vertices[2] = new Vec2(0.0f, 1.5f);
			m_polygons[0] = new PolygonShape();
			m_polygons[0].set(vertices, 3);
		}

		{
			Vec2 vertices[] = new Vec2[3];
			vertices[0] = new Vec2(-0.1f, 0.0f);
			vertices[1] = new Vec2(0.1f, 0.0f);
			vertices[2] = new Vec2(0.0f, 1.5f);
			m_polygons[1] = new PolygonShape();
			m_polygons[1].set(vertices, 3);
		}

		{
			float w = 1.0f;
			float b = w / (2.0f + MathUtils.sqrt(2.0f));
			float s = MathUtils.sqrt(2.0f) * b;

			Vec2 vertices[] = new Vec2[8];
			vertices[0] = new Vec2(0.5f * s, 0.0f);
			vertices[1] = new Vec2(0.5f * w, b);
			vertices[2] = new Vec2(0.5f * w, b + s);
			vertices[3] = new Vec2(0.5f * s, w);
			vertices[4] = new Vec2(-0.5f * s, w);
			vertices[5] = new Vec2(-0.5f * w, b + s);
			vertices[6] = new Vec2(-0.5f * w, b);
			vertices[7] = new Vec2(-0.5f * s, 0.0f);

			m_polygons[2] = new PolygonShape();
			m_polygons[2].set(vertices, 8);
		}

		{
			m_polygons[3] = new PolygonShape();
			m_polygons[3].setAsBox(0.5f, 0.5f);
		}

		{
			m_circle = new CircleShape();
			m_circle.m_radius = 0.5f;
		}

		m_bodyIndex = 0;
		m_angle = 0.0f;
	}
	
	void Create(int index)
	{
		if (m_bodies[m_bodyIndex] != null)
		{
			m_world.destroyBody(m_bodies[m_bodyIndex]);
			m_bodies[m_bodyIndex] = null;
		}

		BodyDef bd = new BodyDef();

		float x = MathUtils.randomFloat(-10.0f, 10.0f);
		float y = MathUtils.randomFloat(10.0f, 20.0f);
		bd.position.set(x, y);
		bd.angle = MathUtils.randomFloat(-MathUtils.PI, MathUtils.PI);
		bd.type = BodyType.DYNAMIC;

		if (index == 4)
		{
			bd.angularDamping = 0.02f;
		}

		m_bodies[m_bodyIndex] = m_world.createBody(bd);
		
		if (index < 4)
		{
			FixtureDef fd = new FixtureDef();
			fd.shape = m_polygons[index];
			fd.friction = 0.3f;
			fd.density = 20.0f;
			m_bodies[m_bodyIndex].createFixture(fd);
		}
		else
		{
			FixtureDef fd = new FixtureDef();
			fd.shape = m_circle;
			fd.friction = 0.3f;
			fd.density = 20.0f;
			m_bodies[m_bodyIndex].createFixture(fd);
		}

		m_bodyIndex = (m_bodyIndex + 1) % e_maxBodies;
	}

	void DestroyBody()
	{
		for (int i = 0; i < e_maxBodies; ++i)
		{
			if (m_bodies[i] != null)
			{
				m_world.destroyBody(m_bodies[i]);
				m_bodies[i] = null;
				return;
			}
		}
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#keyPressed(char, int)
	 */
	@Override
	public void keyPressed(char argKeyChar, int argKeyCode) {
		switch (argKeyChar)
		{
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		    Create(argKeyChar - '1');
			break;

		case 'd':
			DestroyBody();
			break;
		}
	}
	
	EdgeShapesCallback callback = new EdgeShapesCallback();
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#step(org.jbox2d.testbed.framework.Testbe.settings)
	 */
	@Override
	public void step(TestbedSettings settings) {
		
		boolean advanceRay = settings.pause == false || settings.singleStep;

		super.step(settings);
		addTextLine("Press 1-5 to drop stuff");

		float L = 25.0f;
		Vec2 point1 = new Vec2(0.0f, 10.0f);
		Vec2 d = new Vec2(L * MathUtils.cos(m_angle), -L * MathUtils.abs(MathUtils.sin(m_angle)));
		Vec2 point2 = point1.add(d);


		callback.m_fixture = null;
		m_world.raycast(callback, point1, point2);

		if (callback.m_fixture != null)
		{
			m_debugDraw.drawPoint(callback.m_point, 5.0f, new Color3f(0.4f, 0.9f, 0.4f));

			m_debugDraw.drawSegment(point1, callback.m_point, new Color3f(0.8f, 0.8f, 0.8f));

			Vec2 head = callback.m_normal.mul(.5f).addLocal(callback.m_point);
			m_debugDraw.drawSegment(callback.m_point, head, new Color3f(0.9f, 0.9f, 0.4f));
		}
		else
		{
			m_debugDraw.drawSegment(point1, point2, new Color3f(0.8f, 0.8f, 0.8f));
		}

		if (advanceRay)
		{
			m_angle += 0.25f * MathUtils.PI / 180.0f;
		}
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#getTestName()
	 */
	@Override
	public String getTestName() {
		return "Edge Shapes";
	}
	
}

class EdgeShapesCallback implements RayCastCallback
{
	EdgeShapesCallback(){
		m_fixture = null;
	}

	public float reportFixture(Fixture fixture, final Vec2 point,
		final Vec2 normal, float fraction){
		m_fixture = fixture;
		m_point = point;
		m_normal = normal;

		return fraction;
	}

	Fixture m_fixture;
	Vec2 m_point;
	Vec2 m_normal;
};
