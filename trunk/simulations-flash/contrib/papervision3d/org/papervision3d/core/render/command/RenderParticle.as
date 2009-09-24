package org.papervision3d.core.render.command
{
	
	/**
	 * @Author Ralph Hauwert
	 * 
	 * updated by Seb Lee-Delisle 
	 *  - added HitTestPoint2D so that it works with interactivity.
	 */
	 
	import flash.display.Graphics;
	import flash.geom.Point;
	
	import org.papervision3d.core.geom.renderables.Particle;
	import org.papervision3d.core.render.data.RenderHitData;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.materials.special.ParticleMaterial;	
	public class RenderParticle extends RenderableListItem implements IRenderListItem
	{
	
		public var particle:Particle;
		
		public var renderMat:ParticleMaterial; 
		
		
		public function RenderParticle(particle:Particle)
		{
			super();
			this.particle = particle;
			this.renderableInstance = particle;
			this.renderable = Particle;
			this.instance = particle.instance;
		}
		
		override public function render(renderSessionData:RenderSessionData, graphics:Graphics):void
		{
			particle.material.drawParticle(particle, graphics, renderSessionData);
		}
		
		override public function hitTestPoint2D(point:Point, rhd:RenderHitData):RenderHitData
		{
			renderMat = particle.material;
			//if( !renderMat ) renderMat = triangle.instance.material;
			
			if(renderMat.interactive)
			{
				if(particle.renderRect.contains(point.x, point.y)) 
				{
					rhd.displayObject3D = particle.instance; 
					rhd.material = renderMat;
					rhd.renderable = particle; 
					rhd.hasHit = true;
					
					//TODO UPDATE 3D hit point and UV
					rhd.x = particle.x; 
					rhd.y = particle.y; 
					rhd.z = particle.z; 
					rhd.u = 0;
					rhd.v = 0; 
					return rhd; 
				}
				
			}
			return rhd;
		}
		
		
	}
}