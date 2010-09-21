package org.papervision3d.core.geom.renderables
{
	
	
	import flash.geom.Matrix;
	import flash.geom.Rectangle;
	
	import org.papervision3d.core.render.command.IRenderListItem;
	import org.papervision3d.core.render.command.RenderParticle;
	import org.papervision3d.materials.special.ParticleMaterial;	 

	/**
	 * This is the single renderable Particle object, used by Particles.as
	 * 
	 * See Particles.as for a full explanation. 
	 * 
	 * 
	 * @author Ralph Hauwert
	 * @author Seb Lee-Delisle
	 */


	public class Particle extends AbstractRenderable implements IRenderable
	{
		/**
		 * The size or scale factor of the particle.  
		 */		
		public var size:Number;
		public var vertex3D:Vertex3D;
		public var material:ParticleMaterial;
		public var renderCommand:RenderParticle;
		public var renderScale:Number;
		public var drawMatrix : Matrix; 
		public var rotationZ : Number = 0; 
		
		/**
		 * The rectangle containing the particles visible area in 2D.  
		 */		
		public var renderRect:Rectangle;
		
		/**
		 * 
		 * @param material		The ParticleMaterial used for rendering the Particle
		 * @param size			The size of the particle. For some materials (ie BitmapParticleMaterial) this is used as a scale factor. 
		 * @param x				x position of the particle
		 * @param y				y position of the particle
		 * @param z				z position of the particle
		 * 
		 */		
		public function Particle(material:ParticleMaterial, size:Number=1, x:Number=0, y:Number=0, z:Number=0)
		{
			this.material = material;
			this.size = size;
			this.renderCommand = new RenderParticle(this);
			this.renderRect = new Rectangle();
			vertex3D = new Vertex3D(x,y,z);
			drawMatrix  = new Matrix(); 
		}
		
		/**
		 * This is called during the projection cycle. It updates the rectangular area that 
		 * the particle is drawn into. It's important for the culling phase, and changes dependent
		 * on the type of material used.  
		 *  
		 */		

		public function updateRenderRect():void
		{
			material.updateRenderRect(this);
		}
		
		public function set x(x:Number):void
		{
			vertex3D.x = x;
		}
		
		public function get x():Number
		{
			return vertex3D.x;
		}
		
		public function set y(y:Number):void
		{
			vertex3D.y = y;
		}
		
		public function get y():Number
		{
			return vertex3D.y;
		}
		
		public function set z(z:Number):void
		{
			vertex3D.z = z;
		}
		
		public function get z():Number
		{
			return vertex3D.z;
		}
		
		override public function getRenderListItem():IRenderListItem
		{
			return renderCommand;
		}
		
	}
}