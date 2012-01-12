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
package org.jbox2d.testbed.tests;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.testbed.framework.TestbedTest;

public class OneSidedTest extends TestbedTest{

	enum State
	{
		e_unknown,
		e_above,
		e_below,
	};
	
	float m_radius, m_top, m_bottom;
	State m_state;
	Fixture m_platform;
	Fixture m_character;
	
	@Override
	public String getTestName() {
		return "One Sided";
	}

	@Override
	public void initTest() {
		// Ground
		{
			BodyDef bd = new BodyDef();
			Body ground = m_world.createBody(bd);

			PolygonShape shape = new PolygonShape();
			shape.setAsEdge(new Vec2(-20.0f, 0.0f), new Vec2(20.0f, 0.0f));
			ground.createFixture(shape, 0.0f);
		}

		// Platform
		{
			BodyDef bd = new BodyDef();
			bd.position.set(0.0f, 10.0f);
			Body body = m_world.createBody(bd);

			PolygonShape shape = new PolygonShape();
			shape.setAsBox(3.0f, 0.5f);
			m_platform = body.createFixture(shape, 0.0f);

			m_bottom = 10.0f - 0.5f;
			m_top = 10.0f + 0.5f;
		}

		// Actor
		{
			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.position.set(0.0f, 12.0f);
			Body body = m_world.createBody(bd);

			m_radius = 0.5f;
			CircleShape shape = new CircleShape();
			shape.m_radius = m_radius;
			m_character = body.createFixture(shape, 20.0f);

			body.setLinearVelocity(new Vec2(0.0f, -50.0f));

			m_state = State.e_unknown;
		}
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		super.preSolve(contact, oldManifold);
		
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if (fixtureA != m_platform && fixtureA != m_character){
			return;
		}

		if (fixtureB != m_character && fixtureB != m_character){
			return;
		}

		Vec2 position = m_character.getBody().getPosition();

		if (position.y < m_top + m_radius - 3.0f * Settings.linearSlop){
			contact.setEnabled(false);
		}
	}

}
