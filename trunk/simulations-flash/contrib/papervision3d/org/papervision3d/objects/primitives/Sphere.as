package org.papervision3d.objects.primitives {
	import org.papervision3d.Papervision3D;
	import org.papervision3d.core.geom.*;
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.NumberUV;
	import org.papervision3d.core.proto.*;	

	/**
	* The Sphere class lets you create and display spheres.
	* <p/>
	* The sphere is divided in vertical and horizontal segment, the smallest combination is two vertical and three horizontal segments.
	*/
	public class Sphere extends TriangleMesh3D
	{
		/**
		* Number of segments horizontally. Defaults to 8.
		*/
		private var segmentsW :Number;
	
		/**
		* Number of segments vertically. Defaults to 6.
		*/
		private var segmentsH :Number;
	
		/**
		* Default radius of Sphere if not defined.
		*/
		static public var DEFAULT_RADIUS :Number = 100;
	
		/**
		* Default scale of Sphere texture if not defined.
		*/
		static public var DEFAULT_SCALE :Number = 1;
	
		/**
		* Default value of gridX if not defined.
		*/
		static public var DEFAULT_SEGMENTSW :Number = 8;
	
		/**
		* Default value of gridY if not defined.
		*/
		static public var DEFAULT_SEGMENTSH :Number = 6;
	
		/**
		* Minimum value of gridX.
		*/
		static public var MIN_SEGMENTSW :Number = 3;
	
		/**
		* Minimum value of gridY.
		*/
		static public var MIN_SEGMENTSH :Number = 2;
	
	
		// ___________________________________________________________________________________________________
		//                                                                                               N E W
		// NN  NN EEEEEE WW    WW
		// NNN NN EE     WW WW WW
		// NNNNNN EEEE   WWWWWWWW
		// NN NNN EE     WWW  WWW
		// NN  NN EEEEEE WW    WW
	
		/**
		* Create a new Sphere object.
		* <p/>
		* @param	material	A MaterialObject3D object that contains the material properties of the object.
		* <p/>
		* @param	radius		[optional] - Desired radius.
		* <p/>
		* @param	segmentsW	[optional] - Number of segments horizontally. Defaults to 8.
		* <p/>
		* @param	segmentsH	[optional] - Number of segments vertically. Defaults to 6.
		* <p/>
		*/
		public function Sphere( material:MaterialObject3D=null, radius:Number=100, segmentsW:int=8, segmentsH:int=6 )
		{
			super( material, new Array(), new Array(), null );
	
			this.segmentsW = Math.max( MIN_SEGMENTSW, segmentsW || DEFAULT_SEGMENTSW); // Defaults to 8
			this.segmentsH = Math.max( MIN_SEGMENTSH, segmentsH || DEFAULT_SEGMENTSH); // Defaults to 6
			if (radius==0) radius = DEFAULT_RADIUS; // Defaults to 100
	
			var scale :Number = DEFAULT_SCALE;
	
			buildSphere( radius );
		}
	
		private function buildSphere( fRadius:Number ):void
		{
			var i:Number, j:Number, k:Number;
			var iHor:Number = Math.max(3,this.segmentsW);
			var iVer:Number = Math.max(2,this.segmentsH);
			var aVertice:Array = this.geometry.vertices;
			var aFace:Array = this.geometry.faces;
			var aVtc:Array = new Array();
			for (j=0;j<(iVer+1);j++) { // vertical
				var fRad1:Number = Number(j/iVer);
				var fZ:Number = -fRadius*Math.cos(fRad1*Math.PI);
				var fRds:Number = fRadius*Math.sin(fRad1*Math.PI);
				var aRow:Array = new Array();
				var oVtx:Vertex3D;
				for (i=0;i<iHor;i++) { // horizontal
					var fRad2:Number = Number(2*i/iHor);
					var fX:Number = fRds*Math.sin(fRad2*Math.PI);
					var fY:Number = fRds*Math.cos(fRad2*Math.PI);
					if (!((j==0||j==iVer)&&i>0)) { // top||bottom = 1 vertex
						oVtx = new Vertex3D(fY,fZ,fX);
						aVertice.push(oVtx);
					}
					aRow.push(oVtx);
				}
				aVtc.push(aRow);
			}
			var iVerNum:int = aVtc.length;
			for (j=0;j<iVerNum;j++) {
				var iHorNum:int = aVtc[j].length;
				if (j>0) { // &&i>=0
					for (i=0;i<iHorNum;i++) {
						// select vertices
						var bEnd:Boolean = i==(iHorNum-1);
						var aP1:Vertex3D = aVtc[j][bEnd?0:i+1];
						var aP2:Vertex3D = aVtc[j][(bEnd?iHorNum-1:i)];
						var aP3:Vertex3D = aVtc[j-1][(bEnd?iHorNum-1:i)];
						var aP4:Vertex3D = aVtc[j-1][bEnd?0:i+1];
						// uv
						/*
						 * fix applied as suggested by Philippe to correct the uv mapping on a sphere
						 * */
						var fJ0:Number = j		/ (iVerNum-1);
						var fJ1:Number = (j-1)	/ (iVerNum-1);
						var fI0:Number = (i+1)	/ iHorNum;
						var fI1:Number = i		/ iHorNum;
						var aP4uv:NumberUV = new NumberUV(fI0,fJ1);
						var aP1uv:NumberUV = new NumberUV(fI0,fJ0);
						var aP2uv:NumberUV = new NumberUV(fI1,fJ0);
						var aP3uv:NumberUV = new NumberUV(fI1,fJ1);
						// 2 faces
						if (j<(aVtc.length-1))	aFace.push( new Triangle3D(this, new Array(aP1,aP2,aP3), material, new Array(aP1uv,aP2uv,aP3uv)) );
						if (j>1)				aFace.push( new Triangle3D(this, new Array(aP1,aP3,aP4), material, new Array(aP1uv,aP3uv,aP4uv)) );
	
					}
				}
			}
			for each(var t:Triangle3D in aFace){
				t.renderCommand.create = createRenderTriangle;
			}
			
			this.geometry.ready = true;
			
			if(Papervision3D.useRIGHTHANDED)
				this.geometry.flipFaces();
		}
	}
}