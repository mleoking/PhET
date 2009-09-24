package org.papervision3d.objects.special {
	import org.papervision3d.core.geom.Particles;
	import org.papervision3d.core.geom.renderables.Particle;
	import org.papervision3d.materials.special.ParticleMaterial;	

	/**
	 * @Author Ralph Hauwert
	 */
	 
	public class ParticleField extends Particles
	{
		
		private var fieldDepth:Number;
		private var fieldHeight:Number;
		private var fieldWidth:Number;
		private var quantity:int;		
		private var color:int;
		
		/**
		* The ParticleField class creates an object with an amount of particles randomly distributed over a specied 3d area.
		* @param	material 	The Material for the to be created particles
		* @param	quantity	The number of particles in the field
		* @param	particleSize	The size of the created particles
		* @param	fieldWidth 	The width of the area
		* @param 	fieldHeight The height of the area
		* @param	fieldDepth	The depth of the area 
		*/
		public function ParticleField(mat:ParticleMaterial, quantity:int = 200, particleSize:Number=4, fieldWidth:Number = 2000, fieldHeight:Number = 2000, fieldDepth:Number = 2000)
		{
			super("ParticleField");
			
			this.material = mat;
			this.quantity = quantity;
			
			this.fieldWidth = fieldWidth;
			this.fieldHeight = fieldHeight;
			this.fieldDepth = fieldDepth;
			
			createParticles(particleSize);
		}
		
		private function createParticles(size:Number):void
		{
			var width2  :Number = fieldWidth /2;
			var height2 :Number = fieldHeight /2;
			var depth2  :Number = fieldDepth /2;
			
			for( var i:Number = 0; i < quantity; i++ )
			{
				addParticle(new Particle(material as ParticleMaterial, size,Math.random() * fieldWidth  - width2, Math.random() * fieldHeight - height2, Math.random() * fieldDepth  - depth2 ));
			}
		}
		
	}
}
