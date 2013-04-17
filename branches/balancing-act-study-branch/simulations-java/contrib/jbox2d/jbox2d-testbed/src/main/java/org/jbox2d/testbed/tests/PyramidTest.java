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
 * Created at 5:36:06 PM Jul 17, 2010
 */
package org.jbox2d.testbed.tests;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.testbed.framework.TestbedTest;

/**
 * @author Daniel Murphy
 */
public class PyramidTest extends TestbedTest {
	
	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#initTest()
	 */
	@Override
	public void initTest() {
		setTitle("Pyramid");
		int count = 20;
		{
			BodyDef bd = new BodyDef();
			Body ground = m_world.createBody(bd);

			PolygonShape shape = new PolygonShape();
			shape.setAsEdge(new Vec2(-40.0f, 0f), new Vec2(40.0f, 0f));
			ground.createFixture(shape, 0.0f);
			
//			CircleShape cs = new CircleShape();
//			cs.m_radius = 8.0f;
//			cs.m_p.set(-0.3f,-8.0f);
//			Fixture f = ground.createFixture(cs, 0.0f);
//			f.setFriction(0.0f);
			
		}

		{
			float a = .5f;
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(a, a);
			
			Vec2 x = new Vec2(-7.0f, 0.75f);
			Vec2 y = new Vec2();
			Vec2 deltaX = new Vec2(0.5625f, 1.25f);
			Vec2 deltaY = new Vec2(1.125f, 0.0f);

			for (int i = 0; i < count; ++i){
				y.set(x);

				for (int j = i; j < count; ++j){
					BodyDef bd = new BodyDef();
					bd.type = BodyType.DYNAMIC;
					bd.position.set(y);
					Body body = m_world.createBody(bd);
					body.createFixture(shape, 5.0f);
					y.addLocal(deltaY);
				}

				x.addLocal(deltaX);
			}
		}
	}

	/**
	 * @see org.jbox2d.testbed.framework.TestbedTest#getTestName()
	 */
	@Override
	public String getTestName() {
		return "Pyramid";
	}

}
