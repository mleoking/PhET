package org.papervision3d.materials.special
{
	import flash.display.Graphics;
	import flash.geom.Rectangle;
	
	import org.papervision3d.core.geom.renderables.Particle;
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.draw.IParticleDrawer;	

	/**
	 * @Author Ralph Hauwert
	 * 
	 * updated by Seb Lee-Delisle 
	 *  - added size implementation
	 *  - added rectangle of particle for smart culling and drawing
	 * 
	 */
	public class ParticleMaterial extends MaterialObject3D implements IParticleDrawer
	{
		
		public static var SHAPE_SQUARE:int = 0; 
		public static var SHAPE_CIRCLE:int = 1;
				public var shape : int; 
		public var scale : Number ;
		public function ParticleMaterial(color:Number, alpha:Number, shape:int = 0, scale: Number = 1 )
		{
			super();
			this.shape = shape; 
			this.fillAlpha = alpha;
			this.fillColor = color;
			this.scale = scale; 
		}
		
		public function drawParticle(particle:Particle, graphics:Graphics, renderSessionData:RenderSessionData):void
		{
			graphics.beginFill(fillColor, fillAlpha);
			
			var renderrect:Rectangle = particle.renderRect; 
			
			if(shape == SHAPE_SQUARE){
				graphics.drawRect(renderrect.x, renderrect.y, renderrect.width, renderrect.height);
			}else if(shape == SHAPE_CIRCLE){
				graphics.drawCircle(renderrect.x+renderrect.width/2, renderrect.y+renderrect.width/2, renderrect.width/2);
			}else{
				PaperLogger.warning("Particle material has no valid shape - Must be ParticleMaterial.SHAPE_SQUARE or ParticleMaterial.SHAPE_CIRCLE");
			} 
			
			
			
			graphics.endFill();
			renderSessionData.renderStatistics.particles++;
		}
		
		public function updateRenderRect(particle : Particle) :void
		{
			var renderrect:Rectangle = particle.renderRect; 

			if(particle.size == 0){

				renderrect.width = 1; 
				renderrect.height = 1; 
			}else{
				renderrect.width = particle.renderScale*particle.size*scale;
				renderrect.height = particle.renderScale*particle.size*scale;
			}
			renderrect.x = particle.vertex3D.vertex3DInstance.x - (renderrect.width/2); 
			renderrect.y = particle.vertex3D.vertex3DInstance.y - (renderrect.width/2);
			
		}
	}
}