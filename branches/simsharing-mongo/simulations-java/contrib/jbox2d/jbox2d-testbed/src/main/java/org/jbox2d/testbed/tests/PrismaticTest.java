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
 * Created at 6:00:03 AM Jan 12, 2011
 */
package org.jbox2d.testbed.tests;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.testbed.framework.TestPanel;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;

/**
 * @author Daniel Murphy
 */
public class PrismaticTest extends TestbedTest {
	
	PrismaticJoint m_joint;
	
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
			shape.setAsEdge(new Vec2(-40.0f, 0.0f), new Vec2(40.0f, 0.0f));
			ground.createFixture(shape, 0.0f);
		}
		
		{
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(2.0f, 0.5f);
			
			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.position.set(-10.0f, 10.0f);
			bd.angle = 0.5f * MathUtils.PI;
			bd.allowSleep = false;
			Body body = m_world.createBody(bd);
			body.createFixture(shape, 5.0f);
			
			PrismaticJointDef pjd = new PrismaticJointDef();
			
			// Bouncy limit
			Vec2 axis = new Vec2(2.0f, 1.0f);
			axis.normalize();
			pjd.initialize(ground, body, new Vec2(0.0f, 0.0f), axis);
			
			// Non-bouncy limit
			// pjd.Initialize(ground, body, Vec2(-10.0f, 10.0f), Vec2(1.0f, 0.0f));
			
			pjd.motorSpeed = 10.0f;
			pjd.maxMotorForce = 10000.0f;
			pjd.enableMotor = true;
			pjd.lowerTranslation = 0.0f;
			pjd.upperTranslation = 20.0f;
			pjd.enableLimit = true;
			
			m_joint = (PrismaticJoint) m_world.createJoint(pjd);
		}
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#step(org.jbox2d.testbed.framework.TestbedSettings)
	 */
	@Override
	public void step(TestbedSettings settings) {
		super.step(settings);
		addTextLine("Keys: (l) limits, (m) motors, (s) speed");
		float force = m_joint.getMotorForce();
		addTextLine("Motor Force = " + force);
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#keyPressed(char, int)
	 */
	@Override
	public void keyPressed(char argKeyChar, int argKeyCode) {
		
		switch (argKeyChar) {
			case 'l' :
				m_joint.enableLimit(!m_joint.isLimitEnabled());
				TestPanel.keys['l'] = false;
				break;
			case 'm' :
				m_joint.enableMotor(!m_joint.isMotorEnabled());
				TestPanel.keys['m'] = false;
				break;
			case 's' :
				m_joint.setMotorSpeed(-m_joint.getMotorSpeed());
				TestPanel.keys['s'] = false;
				break;
		}
	}
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#getTestName()
	 */
	@Override
	public String getTestName() {
		return "Prismatic";
	}
	
}
