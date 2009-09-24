package org.papervision3d.objects.primitives {
	import org.papervision3d.Papervision3D;
	import org.papervision3d.core.geom.*;
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.NumberUV;
	import org.papervision3d.core.proto.*;	

	/**
	* The Plane class lets you create and display flat rectangle objects.
	* <p/>
	* The rectangle can be divided in smaller segments. This is usually done to reduce linear mapping artifacts.
	* <p/>
	* Dividing the plane in the direction of the perspective or vanishing point, helps to reduce this problem. Perspective distortion dissapears when the plane is facing straignt to the camera, i.e. it is perpendicular with the vanishing point of the scene.
	*/
	public class Plane extends TriangleMesh3D
	{
		/**
		* Number of segments horizontally. Defaults to 1.
		*/
		public var segmentsW :Number;
	
		/**
		* Number of segments vertically. Defaults to 1.
		*/
		public var segmentsH :Number;
	
		/**
		* Default size of Plane if not texture is defined.
		*/
		static public var DEFAULT_SIZE :Number = 500;
	
		/**
		* Default size of Plane if not texture is defined.
		*/
		static public var DEFAULT_SCALE :Number = 1;
	
		/**
		* Default value of gridX if not defined. The default value of gridY is gridX.
		*/
		static public var DEFAULT_SEGMENTS :Number = 1;
	
	
		// ___________________________________________________________________________________________________
		//                                                                                               N E W
		// NN  NN EEEEEE WW    WW
		// NNN NN EE     WW WW WW
		// NNNNNN EEEE   WWWWWWWW
		// NN NNN EE     WWW  WWW
		// NN  NN EEEEEE WW    WW
	
		/**
		* Create a new Plane object.
		* <p/>
		* @param	material	A MaterialObject3D object that contains the material properties of the object.
		* <p/>
		* @param	width		[optional] - Desired width or scaling factor if there's bitmap texture in material and no height is supplied.
		* <p/>
		* @param	height		[optional] - Desired height.
		* <p/>
		* @param	segmentsW	[optional] - Number of segments horizontally. Defaults to 1.
		* <p/>
		* @param	segmentsH	[optional] - Number of segments vertically. Defaults to segmentsW.
		* <p/>
		*/
		public function Plane( material:MaterialObject3D=null, width:Number=0, height:Number=0, segmentsW:Number=0, segmentsH:Number=0 )
		{
			super( material, new Array(), new Array(), null );
	
			this.segmentsW = segmentsW || DEFAULT_SEGMENTS; // Defaults to 1
			this.segmentsH = segmentsH || this.segmentsW;   // Defaults to segmentsW
	
			var scale :Number = DEFAULT_SCALE;
	
			if( ! height )
			{
				if( width )
					scale = width;
	
				if( material && material.bitmap )
				{
					width  = material.bitmap.width  * scale;
					height = material.bitmap.height * scale;
				}
				else
				{
					width  = DEFAULT_SIZE * scale;
					height = DEFAULT_SIZE * scale;
				}
			}
	
			buildPlane( width, height );
		}
	
		private function buildPlane( width:Number, height:Number ):void
		{
			var gridX    :Number = this.segmentsW;
			var gridY    :Number = this.segmentsH;
			var gridX1   :Number = gridX + 1;
			var gridY1   :Number = gridY + 1;
	
			var vertices :Array  = this.geometry.vertices;
			var faces    :Array  = this.geometry.faces;
	
			var textureX :Number = width /2;
			var textureY :Number = height /2;
	
			var iW       :Number = width / gridX;
			var iH       :Number = height / gridY;
	
			// Vertices
			for( var ix:int = 0; ix < gridX + 1; ix++ )
			{
				for( var iy:int = 0; iy < gridY1; iy++ )
				{
					var x :Number = ix * iW - textureX;
					var y :Number = iy * iH - textureY;
	
					vertices.push( new Vertex3D( x, y, 0 ) );
				}
			}
	
			// Faces
			var uvA :NumberUV;
			var uvC :NumberUV;
			var uvB :NumberUV;
	
			for(  ix = 0; ix < gridX; ix++ )
			{
				for(  iy= 0; iy < gridY; iy++ )
				{
					// Triangle A
					var a:Vertex3D = vertices[ ix     * gridY1 + iy     ];
					var c:Vertex3D = vertices[ ix     * gridY1 + (iy+1) ];
					var b:Vertex3D = vertices[ (ix+1) * gridY1 + iy     ];
	
					uvA =  new NumberUV( ix     / gridX, iy     / gridY );
					uvC =  new NumberUV( ix     / gridX, (iy+1) / gridY );
					uvB =  new NumberUV( (ix+1) / gridX, iy     / gridY );
	
					faces.push(new Triangle3D(this, [ a, b, c ], material, [ uvA, uvB, uvC ] ) );
	
					// Triangle B
					a = vertices[ (ix+1) * gridY1 + (iy+1) ];
					c = vertices[ (ix+1) * gridY1 + iy     ];
					b = vertices[ ix     * gridY1 + (iy+1) ];
	
					uvA =  new NumberUV( (ix+1) / gridX, (iy+1) / gridY );
					uvC =  new NumberUV( (ix+1) / gridX, iy      / gridY );
					uvB =  new NumberUV( ix      / gridX, (iy+1) / gridY );
					
					faces.push(new Triangle3D(this, [ a, b, c ], material, [ uvA, uvB, uvC ] ) );
				}
			}
	
			this.geometry.ready = true;
			
			if(Papervision3D.useRIGHTHANDED)
				this.geometry.flipFaces();
		}
	}
}