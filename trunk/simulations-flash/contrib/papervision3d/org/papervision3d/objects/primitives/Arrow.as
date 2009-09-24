package org.papervision3d.objects.primitives
{
	import org.papervision3d.core.geom.TriangleMesh3D;
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.NumberUV;
	import org.papervision3d.core.proto.MaterialObject3D;

	public class Arrow extends TriangleMesh3D
	{

		public var verts :Array;
		public var faceAr:Array;
		public var uvs :Array;

		private function v(x:Number,y:Number,z:Number):void
		{
			verts.push(new Vertex3D(x,y,z));
		}

		private function uv(u:Number,v:Number):void
		{
			uvs.push(new NumberUV(u,v));
		}

		private function f(vn0:int, vn1:int, vn2:int, uvn0:int, uvn1:int,uvn2:int):void
		{
			faceAr.push( new Triangle3D( this, [verts[vn0],verts[vn1],verts[vn2] ], null, [uvs[uvn0],uvs[uvn1],uvs[uvn2]] ) );
		}

		public function Arrow( material:MaterialObject3D=null )
		{
			super( material, new Array(), new Array(), null );
			verts = this.geometry.vertices;
			faceAr= this.geometry.faces;
			uvs   =new Array();
			v(-100.0,0.0,-257.143);
			v(100.0,0.0,-257.143);
			v(100.0,0.0,42.8571);
			v(200.0,0.0,42.8571);
			v(0.0,0.0,342.857);
			v(-200.0,0.0,42.8571);
			v(-100.0,0.0,42.8571);
			v(-100.0,100.0,-257.143);
			v(100.0,100.0,-257.143);
			v(100.0,100.0,42.8571);
			v(200.0,100.0,42.8571);
			v(0.0,100.0,342.857);
			v(-200.0,100.0,42.8571);
			v(-100.0,100.0,42.8571);
			uv(0.25,0.0714286);
			uv(0.75,0.0714286);
			uv(0.75,0.571429);
			uv(1.0,0.571429);
			uv(0.5,1.07143);
			uv(0.0,0.571429);
			uv(0.25,0.571429);
			uv(0.25,0.0714286);
			uv(0.75,0.0714286);
			uv(0.75,0.571429);
			uv(1.0,0.571429);
			uv(0.5,1.07143);
			uv(0.0,0.571429);
			uv(0.25,0.571429);
			f(0,1,8,0,1,8);
			f(0,8,7,0,8,7);
			f(1,2,9,1,2,9);
			f(1,9,8,1,9,8);
			f(2,3,10,2,3,10);
			f(2,10,9,2,10,9);
			f(3,4,11,3,4,11);
			f(3,11,10,3,11,10);
			f(4,5,12,4,5,12);
			f(4,12,11,4,12,11);
			f(5,6,13,5,6,13);
			f(5,13,12,5,13,12);
			f(6,0,7,6,0,7);
			f(6,7,13,6,7,13);
			f(4,6,5,4,6,5);
			f(2,4,3,2,4,3);
			f(2,6,4,2,6,4);
			f(1,6,2,1,6,2);
			f(1,0,6,1,0,6);
			f(11,12,13,11,12,13);
			f(9,10,11,9,10,11);
			f(9,11,13,9,11,13);
			f(8,9,13,8,9,13);
			f(8,13,7,8,13,7);

			this.geometry.ready = true;
		}

	}

}