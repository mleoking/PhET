package org.papervision3d.core.render.draw
{
	
	/**
	 * @Author Ralph Hauwert
	 */
	 
	import flash.display.Graphics;
	
	import org.papervision3d.core.geom.renderables.Particle;
	import org.papervision3d.core.render.data.RenderSessionData;
	
	public interface IParticleDrawer
	{
		function drawParticle(particle:Particle, graphics:Graphics, renderSessionData:RenderSessionData):void;
		function updateRenderRect(particle:Particle):void;  

	}
}