 package org.papervision3d.materials.special
{
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.geom.Matrix;
	import flash.geom.Rectangle;
	
	import org.papervision3d.core.geom.renderables.Particle;
	import org.papervision3d.core.geom.renderables.Vertex3DInstance;
	import org.papervision3d.core.math.Number2D;
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.core.math.util.FastRectangleTools;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.draw.IParticleDrawer;
	/**
	 * A Particle material that is made from BitmapData object
	 * 
	 * @author Ralph Hauwert
 	 * @author Seb Lee-Delisle
  	 */
	public class BitmapParticleMaterial extends ParticleMaterial implements IParticleDrawer
	{
		
		
		private var renderRect:Rectangle; 
		
		public var particleBitmap : ParticleBitmap; 
		
		
	
		/**
		 * 
		 * @param bitmap	The BitmapData object to make the material from. 
		 * 
		 */		
		public function BitmapParticleMaterial(bitmap:*, scale: Number = 1, offsetx: Number = 0, offsety : Number = 0)
		{
			super(0,0);
				
			renderRect = new Rectangle() ;
			
			if(bitmap is BitmapData) 
			{
				
				particleBitmap = new ParticleBitmap(bitmap as BitmapData)
				{
					particleBitmap.scaleX = particleBitmap.scaleY = scale; 
				}	
						
				
				particleBitmap.offsetX = offsetx;
				particleBitmap.offsetY = offsety;
			}	
			else 
			if(bitmap is ParticleBitmap)
			{
				particleBitmap = bitmap as ParticleBitmap; 
				
			}
		
		}
		
		/**
		 * Draws the particle as part of the render cycle. 
		 *  
		 * @param particle			The particle we're drawing
		 * @param graphics			The graphics object we're drawing into
		 * @param renderSessionData	The renderSessionData for this render cycle.
		 * 
		 */	
		 
		override public function drawParticle(particle:Particle, graphics:Graphics, renderSessionData:RenderSessionData):void
		{
			var newscale : Number = particle.renderScale*particle.size;
			
			var cullingrect:Rectangle = renderSessionData.viewPort.cullingRectangle;
			
			renderRect = FastRectangleTools.intersection(cullingrect, particle.renderRect, renderRect); 

			graphics.beginBitmapFill(particleBitmap.bitmap, particle.drawMatrix, false, smooth);
			if(particle.rotationZ==0)
				graphics.drawRect(renderRect.x, renderRect.y, renderRect.width, renderRect.height);
			else
			{
				var p1 : Number2D = new Number2D(particleBitmap.offsetX, particleBitmap.offsetY); 
				var p2 : Number2D = new Number2D(particleBitmap.offsetX+particleBitmap.width, particleBitmap.offsetY); 
				var p3 : Number2D = new Number2D(particleBitmap.offsetX+particleBitmap.width, particleBitmap.offsetY+particleBitmap.height); 
				var p4 : Number2D = new Number2D(particleBitmap.offsetX, particleBitmap.offsetY+particleBitmap.height); 
				
				p1.multiplyEq(newscale); 
				p2.multiplyEq(newscale); 
				p3.multiplyEq(newscale); 
				p4.multiplyEq(newscale); 
				p1.rotate(particle.rotationZ); 
				p2.rotate(particle.rotationZ); 
				p3.rotate(particle.rotationZ); 
				p4.rotate(particle.rotationZ); 
				var pos : Number2D = new Number2D(particle.vertex3D.vertex3DInstance.x,particle.vertex3D.vertex3DInstance.y); 
				p1.plusEq(pos); 
				p2.plusEq(pos); 
				p3.plusEq(pos); 
				p4.plusEq(pos); 
				
				graphics.moveTo(p1.x, p1.y); 
				graphics.lineTo(p2.x, p2.y); 
				graphics.lineTo(p3.x, p3.y); 
				graphics.lineTo(p4.x, p4.y); 
			}
	
			graphics.endFill();

			renderSessionData.renderStatistics.particles++;
			
		}
		/*
		public function copyMatrix(fromMatrix : Matrix, toMatrix : Matrix) : void
		{
			
			toMatrix.a = fromMatrix.a; 
			toMatrix.b = fromMatrix.b; 
			toMatrix.c = fromMatrix.c; 
			toMatrix.d = fromMatrix.d; 
			toMatrix.tx = fromMatrix.tx; 
			toMatrix.ty = fromMatrix.ty; 
			
		}*/
		 /**
		 * This is called during the projection cycle. It updates the rectangular area that 
		 * the particle is drawn into. It's important for the culling phase. 
		 *  
		 * @param particle	The particle whose renderRect we're updating. 
		 * 
		 */			
		override public function updateRenderRect(particle : Particle) : void
		{
			
			var renderrect:Rectangle = particle.renderRect; 
			var newscale : Number = particle.renderScale*particle.size;
			
			var osx:Number = particleBitmap.offsetX * newscale;
			var osy:Number = particleBitmap.offsetY * newscale;
			
			var vertex : Vertex3DInstance = particle.vertex3D.vertex3DInstance;
			
			renderrect.x = vertex.x + osx;
			renderrect.y = vertex.y + osy;
			
			renderrect.width = particleBitmap.width * particleBitmap.scaleX * newscale; 
			renderrect.height = particleBitmap.height * particleBitmap.scaleY * newscale; 
			
			
			
			var drawMatrix : Matrix = particle.drawMatrix; 
			
			drawMatrix.identity(); 
			
				
			if(particle.rotationZ!=0) 
			{	
				drawMatrix.scale(renderrect.width/particleBitmap.width, renderrect.height/particleBitmap.height); 
				drawMatrix.translate(osx, osy); 
			
				drawMatrix.rotate(particle.rotationZ * Number3D.toRADIANS); 
				
				//drawMatrix.translate(osx, osy); 
				
				
				drawMatrix.translate(vertex.x, vertex.y); 

		
			}
			else
			{
				drawMatrix.scale(renderrect.width/particleBitmap.width, renderrect.height/particleBitmap.height); 
				drawMatrix.translate(renderrect.left, renderrect.top); 
			}
			
			
			
			
		}
		
		
	}
}