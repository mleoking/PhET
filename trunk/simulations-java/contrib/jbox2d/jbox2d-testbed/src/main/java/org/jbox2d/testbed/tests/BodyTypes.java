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
 * .created at 1:14:57 AM Jan 14, 2011
 */
package org.jbox2d.testbed.tests;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;

/**
 * @author Daniel Murphy
 */
public class BodyTypes extends TestbedTest {
	
	Body m_attachment;
	Body m_platform;
	float m_speed;
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#initTest()
	 */
	@Override
	public void initTest() {
		Body ground = null;
		{
			BodyDef bd = new BodyDef();
			ground = m_world.createBody(bd);
			
			PolygonShape shape = new PolygonShape();
			shape.setAsEdge(new Vec2(-20.0f, 0.0f), new Vec2(20.0f, 0.0f));
			
			FixtureDef fd = new FixtureDef();
			fd.shape = shape;
			
			ground.createFixture(fd);
		}
		
		// Define attachment
		{
			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.position.set(0.0f, 3.0f);
			m_attachment = m_world.createBody(bd);
			
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(0.5f, 2.0f);
			m_attachment.createFixture(shape, 2.0f);
		}
		
		// Define platform
		{
			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.position.set(-4.0f, 5.0f);
			m_platform = m_world.createBody(bd);
			
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(0.5f, 4.0f, new Vec2(4.0f, 0.0f), 0.5f * MathUtils.PI);
			
			FixtureDef fd = new FixtureDef();
			fd.shape = shape;
			fd.friction = 0.6f;
			fd.density = 2.0f;
			m_platform.createFixture(fd);
			
			RevoluteJointDef rjd = new RevoluteJointDef();
			rjd.initialize(m_attachment, m_platform, new Vec2(0.0f, 5.0f));
			rjd.maxMotorTorque = 50.0f;
			rjd.enableMotor = true;
			m_world.createJoint(rjd);
			
			PrismaticJointDef pjd = new PrismaticJointDef();
			pjd.initialize(ground, m_platform, new Vec2(0.0f, 5.0f), new Vec2(1.0f, 0.0f));
			
			pjd.maxMotorForce = 1000.0f;
			pjd.enableMotor = true;
			pjd.lowerTranslation = -10.0f;
			pjd.upperTranslation = 10.0f;
			pjd.enableLimit = true;
			
			m_world.createJoint(pjd);
			
			m_speed = 3.0f;
		}
		
		// .create a payload
		{
			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.position.set(0.0f, 8.0f);
			Body body = m_world.createBody(bd);
			
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(0.75f, 0.75f);
			
			FixtureDef fd = new FixtureDef();
			fd.shape = shape;
			fd.friction = 0.6f;
			fd.density = 2.0f;
			
			body.createFixture(fd);
		}
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#step(org.jbox2d.testbed.framework.TestbedSettings)
	 */
	@Override
	public void step(TestbedSettings settings) {
		super.step(settings);
		
		addTextLine("Keys: (d) dynamic, (s) static, (k) kinematic");
		// Drive the kinematic body.
		if (m_platform.getType() == BodyType.KINEMATIC) {
			Vec2 p = m_platform.getTransform().position;
			Vec2 v = m_platform.getLinearVelocity();
			
			if ((p.x < -10.0f && v.x < 0.0f) || (p.x > 10.0f && v.x > 0.0f)) {
				v.x = -v.x;
				m_platform.setLinearVelocity(v);
			}
		}
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#keyPressed(char, int)
	 */
	@Override
	public void keyPressed(char argKeyChar, int argKeyCode) {
		switch (argKeyChar) {
			case 'd' :
				m_platform.setType(BodyType.DYNAMIC);
				break;
			
			case 's' :
				m_platform.setType(BodyType.STATIC);
				break;
			
			case 'k' :
				m_platform.setType(BodyType.KINEMATIC);
				m_platform.setLinearVelocity(new Vec2(-m_speed, 0.0f));
				m_platform.setAngularVelocity(0.0f);
				break;
		}
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#getTestName()
	 */
	@Override
	public String getTestName() {
		return "Body Types";
	}
	
}
