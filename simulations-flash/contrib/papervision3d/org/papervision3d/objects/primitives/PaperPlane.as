package org.papervision3d.objects.primitives {
	import org.papervision3d.core.geom.TriangleMesh3D;
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.proto.MaterialObject3D;	

	/**
	* The PaperPlane class lets you create a paper plane object.
	* <p/>
	* Paper planes are useful for testing, when you want to know the direction an object is facing.
	*/
	public class PaperPlane extends TriangleMesh3D
	{
		/**
		* Default value of segments.
		*/
		static public var DEFAULT_SCALE :Number = 1;
	
	
		// ___________________________________________________________________________________________________
		//                                                                                               N E W
		// NN  NN EEEEEE WW    WW
		// NNN NN EE     WW WW WW
		// NNNNNN EEEE   WWWWWWWW
		// NN NNN EE     WWW  WWW
		// NN  NN EEEEEE WW    WW
	
		/**
		* Creates a new PaperPlane object.
		* <p/>
		* @param	material	A Material3D object that contains the material properties of the object.
		* <p/>
		* @param	scale		[optional] - Scaling factor
		* <p/>
		*/
		public function PaperPlane( material :MaterialObject3D=null, scale :Number=0 )
		{
			super( material, new Array(), new Array(), null );
	
			scale = scale || DEFAULT_SCALE;
	
			buildPaperPlane( scale );
		}
	
	
		private function buildPaperPlane( scale :Number ):void
		{
			var a :Number = 100 * scale;
			var b :Number = a/2;
			var c :Number = b/3;
	
			var v:Array =
			[
				new Vertex3D(  0,  0,  a ),
				new Vertex3D( -b,  c, -a ),
				new Vertex3D( -c,  c, -a ),
				new Vertex3D(  0, -c, -a ),
				new Vertex3D(  c,  c, -a ),
				new Vertex3D(  b,  c, -a )
			];
	
			this.geometry.vertices = v;
	
			this.geometry.faces =
			[
				new Triangle3D( this, [v[0], v[1], v[2]] ),
				new Triangle3D( this, [v[0], v[2], v[3]] ),
				new Triangle3D( this, [v[0], v[3], v[4]] ),
				new Triangle3D( this, [v[0], v[4], v[5]] )
			];
			
			this.projectTexture( "x", "z" );
	
			this.geometry.ready = true;
		}
	}
}