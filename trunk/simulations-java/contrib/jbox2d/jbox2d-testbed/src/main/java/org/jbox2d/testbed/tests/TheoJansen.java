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
 * created at 12:22:58 AM Jan 13, 2011
 */
package org.jbox2d.testbed.tests;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;

/**
 * @author Daniel Murphy
 */
public class TheoJansen extends TestbedTest {
	
	Vec2 m_offset = new Vec2();
	Body m_chassis;
	Body m_wheel;
	RevoluteJoint m_motorJoint;
	boolean m_motorOn;
	float m_motorSpeed;
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#initTest()
	 */
	@Override
	public void initTest() {
		m_offset.set(0.0f, 8.0f);
		m_motorSpeed = 2.0f;
		m_motorOn = true;
		Vec2 pivot = new Vec2(0.0f, 0.8f);

		// Ground
		{
			BodyDef bd = new BodyDef();
			Body ground = m_world.createBody(bd);

			PolygonShape shape = new PolygonShape();
			shape.setAsEdge(new Vec2(-50.0f, 0.0f), new Vec2(50.0f, 0.0f));
			ground.createFixture(shape, 0.0f);

			shape.setAsEdge(new Vec2(-50.0f, 0.0f), new Vec2(-50.0f, 10.0f));
			ground.createFixture(shape, 0.0f);

			shape.setAsEdge(new Vec2(50.0f, 0.0f), new Vec2(50.0f, 10.0f));
			ground.createFixture(shape, 0.0f);
		}

		// Balls
		for (int i = 0; i < 40; ++i)
		{
			CircleShape shape = new CircleShape();
			shape.m_radius = 0.25f;

			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.position.set(-40.0f + 2.0f * i, 0.5f);

			Body body = m_world.createBody(bd);
			body.createFixture(shape, 1.0f);
		}

		// Chassis
		{
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(2.5f, 1.0f);

			FixtureDef sd = new FixtureDef();
			sd.density = 1.0f;
			sd.shape = shape;
			sd.filter.groupIndex = -1;
			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.position.set(pivot).addLocal(m_offset);
			m_chassis = m_world.createBody(bd);
			m_chassis.createFixture(sd);
		}

		{
			CircleShape shape = new CircleShape();
			shape.m_radius = 1.6f;

			FixtureDef sd = new FixtureDef();
			sd.density = 1.0f;
			sd.shape = shape;
			sd.filter.groupIndex = -1;
			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.position.set(pivot).addLocal(m_offset);
			m_wheel = m_world.createBody(bd);
			m_wheel.createFixture(sd);
		}

		{
			RevoluteJointDef jd = new RevoluteJointDef();
			
			jd.initialize(m_wheel, m_chassis, pivot.add(m_offset));
			jd.collideConnected = false;
			jd.motorSpeed = m_motorSpeed;
			jd.maxMotorTorque = 400.0f;
			jd.enableMotor = m_motorOn;
			m_motorJoint = (RevoluteJoint)m_world.createJoint(jd);
		}

		Vec2 wheelAnchor;
		
		wheelAnchor = pivot.add(new Vec2(0.0f, -0.8f));

		createLeg(-1.0f, wheelAnchor);
		createLeg(1.0f, wheelAnchor);

		m_wheel.setTransform(m_wheel.getPosition(), 120.0f * MathUtils.PI / 180.0f);
		createLeg(-1.0f, wheelAnchor);
		createLeg(1.0f, wheelAnchor);

		m_wheel.setTransform(m_wheel.getPosition(), -120.0f * MathUtils.PI / 180.0f);
		createLeg(-1.0f, wheelAnchor);
		createLeg(1.0f, wheelAnchor);
	}
	
	void createLeg(float s, Vec2 wheelAnchor){
		Vec2 p1 = new Vec2(5.4f * s, -6.1f);
		Vec2 p2 = new Vec2(7.2f * s, -1.2f);
		Vec2 p3 = new Vec2(4.3f * s, -1.9f);
		Vec2 p4 = new Vec2(3.1f * s, 0.8f);
		Vec2 p5 = new Vec2(6.0f * s, 1.5f);
		Vec2 p6 = new Vec2(2.5f * s, 3.7f);

		FixtureDef fd1 = new FixtureDef();
		FixtureDef fd2 = new FixtureDef();
		fd1.filter.groupIndex = -1;
		fd2.filter.groupIndex = -1;
		fd1.density = 1.0f;
		fd2.density = 1.0f;

		PolygonShape poly1 = new PolygonShape();
		PolygonShape poly2 = new PolygonShape();

		if (s > 0.0f)
		{
			Vec2[] vertices = new Vec2[3];

			vertices[0] = p1;
			vertices[1] = p2;
			vertices[2] = p3;
			poly1.set(vertices, 3);

			vertices[0] = new Vec2();
			vertices[1] = p5.sub(p4);
			vertices[2] = p6.sub(p4);
			poly2.set(vertices, 3);
		}
		else
		{
			Vec2[] vertices = new Vec2[3];

			vertices[0] = p1;
			vertices[1] = p3;
			vertices[2] = p2;
			poly1.set(vertices, 3);

			vertices[0] = new Vec2();
			vertices[1] = p6.sub(p4);
			vertices[2] = p5.sub(p4);
			poly2.set(vertices, 3);
		}

		fd1.shape = poly1;
		fd2.shape = poly2;

		BodyDef bd1 = new BodyDef(), bd2 = new BodyDef();
		bd1.type = BodyType.DYNAMIC;
		bd2.type = BodyType.DYNAMIC;
		bd1.position = m_offset;
		bd2.position = p4.add(m_offset);

		bd1.angularDamping = 10.0f;
		bd2.angularDamping = 10.0f;

		Body body1 = m_world.createBody(bd1);
		Body body2 = m_world.createBody(bd2);

		body1.createFixture(fd1);
		body2.createFixture(fd2);

		DistanceJointDef djd = new DistanceJointDef();

		// Using a soft distance constraint can reduce some jitter.
		// It also makes the structure seem a bit more fluid by
		// acting like a suspension system.
		djd.dampingRatio = 0.5f;
		djd.frequencyHz = 10.0f;

		djd.initialize(body1, body2, p2.add(m_offset), p5.add(m_offset));
		m_world.createJoint(djd);

		djd.initialize(body1, body2, p3.add(m_offset), p4.add(m_offset));
		m_world.createJoint(djd);

		djd.initialize(body1, m_wheel, p3.add(m_offset), wheelAnchor.add(m_offset));
		m_world.createJoint(djd);

		djd.initialize(body2, m_wheel, p6.add(m_offset), wheelAnchor.add(m_offset));
		m_world.createJoint(djd);

		RevoluteJointDef rjd = new RevoluteJointDef();

		rjd.initialize(body2, m_chassis, p4.add(m_offset));
		m_world.createJoint(rjd);
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#keyPressed(char, int)
	 */
	@Override
	public void keyPressed(char key, int argKeyCode) {
		switch (key)
		{
		case 'a':
			m_motorJoint.setMotorSpeed(-m_motorSpeed);
			break;

		case 's':
			m_motorJoint.setMotorSpeed(0.0f);
			break;

		case 'd':
			m_motorJoint.setMotorSpeed(m_motorSpeed);
			break;

		case 'm':
			m_motorJoint.enableMotor(!m_motorJoint.isMotorEnabled());
			break;
		}
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#step(org.jbox2d.testbed.framework.TestbedSettings)
	 */
	@Override
	public void step(TestbedSettings settings) {
		super.step(settings);
		addTextLine("Keys: left = a, brake = s, right = d, toggle motor = m");
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#getTestName()
	 */
	@Override
	public String getTestName() {
		return "TheoJansen Walker";
	}
	
}
