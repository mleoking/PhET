package org.papervision3d.core.culling
{
	import org.papervision3d.core.geom.renderables.Particle;
	
	public interface IParticleCuller
	{
		function testParticle(particle:Particle):Boolean;
	}
}