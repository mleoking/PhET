package edu.colorado.phet.testpapervision3d {
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.BitmapDataChannel;
	import flash.display.MovieClip;
	import flash.display.Sprite;
    import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.geom.ColorTransform;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.utils.Dictionary;
	import flash.utils.getTimer;
	
	import org.papervision3d.core.*;
	import org.papervision3d.core.geom.renderables.*;
	import org.papervision3d.core.math.*;
	import org.papervision3d.core.proto.*;
	import org.papervision3d.events.*;
	import org.papervision3d.lights.*;
	import org.papervision3d.materials.*;
	import org.papervision3d.materials.shaders.*;
	import org.papervision3d.materials.shadematerials.*;
	import org.papervision3d.materials.utils.*;
	import org.papervision3d.objects.*;
	import org.papervision3d.objects.primitives.*;
	
	import edu.colorado.phet.testpapervision3d.PaperBase;	
	import edu.colorado.phet.testpapervision3d.WallBitmapData;
	
	public class Main extends PaperBase {
		
		public var light : PointLight3D;
		
		protected var sceneWidth:Number;
        protected var sceneHeight:Number;
		
		private var dragging : Boolean = false;
		private var dragObject : DisplayObject3D;
		
		private var faceDepth = 100;
		
		var poolWidth : Number = 1500;
		var poolHeight : Number = 750;
		var poolDepth : Number = 500;
		var waterHeight: Number = 550;
		var volume : Number = poolWidth * poolDepth * waterHeight;
		var far : Number = 5000;
		
		public function Main() {
			sceneWidth = stage.stageWidth
            sceneHeight = stage.stageHeight;
			
			init( sceneWidth, sceneHeight );
		}
		
		public function createBlockTexture( size : Number, mass : Number ) : MaterialsList {
			var sp : Sprite = new Sprite();
			var wallData : BitmapData = new WallBitmapData( size, size );
			sp.addChild( new Bitmap( wallData ) );
			
			var tf : TextField = new TextField();
			tf.text = String( mass ) + " kg";
			tf.height = wallData.height;
			tf.width = wallData.width;
			var format : TextFormat = new TextFormat();
			format.size = int( 45 * (200 / size) );
			format.bold = true;
			format.font = "Arial";
			tf.multiline = true;
			tf.setTextFormat( format );
			sp.addChild( tf );
			
			var plainSprite = new Sprite();
			plainSprite.addChild( new Bitmap( wallData ) );
			
			light = new PointLight3D();
			light.x = 100000;
			light.z = -350000;
			light.y = 300000;
			
			var frontTexture : MovieMaterial = new MovieMaterial( sp );
			var plainTexture : MovieMaterial = new MovieMaterial( plainSprite );
			var flat_a : MaterialObject3D = new ShadedMaterial( plainTexture, new FlatShader( light, 0xFFFFFF, 0x888888 ) );
			var flat_b : MaterialObject3D = new ShadedMaterial( frontTexture, new FlatShader( light, 0xFFFFFF, 0x888888 ) );
			var flat_c : MaterialObject3D = new ShadedMaterial( plainTexture, new FlatShader( light, 0xFFFFFF, 0x888888 ) );
			var flat_d : MaterialObject3D = new ShadedMaterial( plainTexture, new FlatShader( light, 0xFFFFFF, 0x888888 ) );
			var flat_e : MaterialObject3D = new ShadedMaterial( plainTexture, new FlatShader( light, 0xFFFFFF, 0x888888 ) );
			var flat_f : MaterialObject3D = new ShadedMaterial( plainTexture, new FlatShader( light, 0xFFFFFF, 0x888888 ) );
			flat_a.interactive = true;
			flat_b.interactive = true;
			flat_c.interactive = true;
			flat_d.interactive = true;
			flat_e.interactive = true;
			flat_f.interactive = true;
			var ml : MaterialsList = new MaterialsList();
			ml.addMaterial( flat_a, 'front' );
			ml.addMaterial( flat_b, 'back' );
			ml.addMaterial( flat_c, 'right' );
			ml.addMaterial( flat_d, 'left' );
			ml.addMaterial( flat_e, 'top' );
			ml.addMaterial( flat_f, 'bottom' );
			return ml;
		}
		
		public function getWaterMaterial( top : Boolean ) : MaterialObject3D {
			var ret : FlatShadeMaterial;
			if( top ) {
				ret = new FlatShadeMaterial( light, 0x00AAFF, 0x00AAFF );
				ret.fillAlpha = 0.3;
			} else {
				ret = new FlatShadeMaterial( light, 0x0088CC, 0x0088CC );
				ret.fillAlpha = 0.25;
			}
			return ret;
		}
		
		public function getInteriorMaterial() : MaterialObject3D {
			return new FlatShadeMaterial( light, 0xAAAAAA, 0x222222 );
		}
		
		public function getGroundMaterial() : MaterialObject3D {
			return new FlatShadeMaterial( light, 0x00BB00, 0x005500 );
		}
		
		public function getEarthMaterial() : MaterialObject3D {
			return new FlatShadeMaterial( light, 0x98662B );
		}
		
		private var fpsText : TextField = new TextField();
		
		override protected function init2d() : void {
			addChild( fpsText );
			fpsText.text = "X fps";
			fpsText.textColor = 0x000000;
			fpsText.background = true;
			fpsText.height = fpsText.textHeight + 4;
		}
		
		override protected function init3d() : void {
			var cube : Cube;
			
			cube = new Cube( createBlockTexture( 200, 50 ), 200, 200, 200, 4, 4, 4 );
			cube.z = 101 + faceDepth;
			cube.x = 450;
			default_scene.addChild( cube );
			makeObjectDraggable( cube );
			
			cube = new Cube( createBlockTexture( 100, 10 ), 100, 100, 100, 4, 4, 4 );
			cube.z = 101 + faceDepth;
			cube.x = 150;
			default_scene.addChild( cube );
			makeObjectDraggable( cube );
			
			cube = new Cube( createBlockTexture( 300, 100 ), 300, 300, 300, 4, 4, 4 );
			cube.z = 101 + faceDepth;
			cube.x = -150;
			default_scene.addChild( cube );
			makeObjectDraggable( cube );
			
			cube = new Cube( createBlockTexture( 200, 50 ), 200, 200, 200, 4, 4, 4 );
			cube.z = 101 + faceDepth;
			cube.x = -450;
			default_scene.addChild( cube );
			makeObjectDraggable( cube );

			default_camera.y = 500;
			default_camera.z = -2000;
			default_camera.zoom = 90;
			default_camera.rotationX = 15;
			
			var plane : Plane;
			
			// top water
			plane = new Plane( getWaterMaterial( true ), poolWidth - 0.01, poolDepth, 1, 1 );
			plane.z = poolDepth / 2;
			plane.y = -poolHeight + waterHeight;
			plane.rotationX = 90;
			default_scene.addChild( plane );
			
			// front water
			plane = new Plane( getWaterMaterial( false ), poolWidth, waterHeight, 1, 1 );
			plane.y = -poolHeight + waterHeight / 2;
			default_scene.addChild( plane );
			
			// back of pool
			plane = new Plane( getInteriorMaterial(), poolWidth, poolHeight, 1, 1 );
			plane.y = -poolHeight / 2;
			plane.z = poolDepth;
			plane.rotationX = 0;
			default_scene.addChild( plane );
			
			// bottom of pool
			plane = new Plane( getInteriorMaterial(), poolWidth, poolDepth, 1, 1 );
			plane.y = -poolHeight;
			plane.z = poolDepth / 2;
			plane.rotationX = 90;
			default_scene.addChild( plane );
			
			// right side of pool
			plane = new Plane( getInteriorMaterial(), poolHeight, poolDepth, 1, 1 );
			plane.x = poolWidth / 2;
			plane.y = -poolHeight / 2;
			plane.z = poolDepth / 2;
			plane.rotationX = 90;
			plane.rotationZ = 90;
			default_scene.addChild( plane );
			
			// left side of pool
			plane = new Plane( getInteriorMaterial(), poolHeight, poolDepth, 1, 1 );
			plane.x = -poolWidth / 2;
			plane.y = -poolHeight / 2;
			plane.z = poolDepth / 2;
			plane.rotationX = 90;
			plane.rotationZ = -90;
			default_scene.addChild( plane );
			
			// ground behind pool
			plane = new Plane( getGroundMaterial(), poolWidth, far - poolDepth, 1, 1 );
			plane.z = (far - poolDepth) / 2 + poolDepth;
			plane.rotationX = 90;
			default_scene.addChild( plane );
			
			// ground to the right of the pool
			plane = new Plane( getGroundMaterial(), far, far, 1, 1 );
			plane.x = far / 2 + poolWidth / 2;
			plane.z = far / 2;
			plane.rotationX = 90;
			default_scene.addChild( plane );
			
			// ground to the left of the pool
			plane = new Plane( getGroundMaterial(), far, far, 1, 1 );
			plane.x = -far / 2 - poolWidth / 2;
			plane.z = far / 2;
			plane.rotationX = 90;
			default_scene.addChild( plane );
			
			// earth under pool
			plane = new Plane( getEarthMaterial(), poolWidth, far, 1, 1 );
			plane.y = -far / 2 - poolHeight;
			default_scene.addChild( plane );
			
			// earth to the right of the pool
			plane = new Plane( getEarthMaterial(), far, far, 1, 1 );
			plane.x = far / 2 + poolWidth / 2;
			plane.y = -far / 2;
			default_scene.addChild( plane );
			
			// earth to the left of the pool
			plane = new Plane( getEarthMaterial(), far, far, 1, 1 );
			plane.x = -far / 2 - poolWidth / 2;
			plane.y = -far / 2;
			default_scene.addChild( plane );
			
			// TEST SPHERES
			var sphere : Sphere;
			var mat : PhongMaterial;
			
			mat = new PhongMaterial( light, 0x5500AA, 0x110022, 30 );
			mat.interactive = true;
			sphere = new Sphere( mat, 100, 5, 5 );
			sphere.x = -450;
			sphere.y = 300;
			sphere.z = 100;
			default_scene.addChild( sphere );
			makeObjectDraggable( sphere );
			
			mat = new PhongMaterial( light, 0x00AA55, 0x002211, 30 );
			mat.interactive = true;
			sphere = new Sphere( mat, 100, 10, 10 );
			sphere.x = -150;
			sphere.y = 300;
			sphere.z = 100;
			default_scene.addChild( sphere );
			makeObjectDraggable( sphere );
			
			mat = new PhongMaterial( light, 0xAA5500, 0x221100, 30 );
			mat.interactive = true;
			sphere = new Sphere( mat, 100, 15, 15 );
			sphere.x = 150;
			sphere.y = 300;
			sphere.z = 100;
			default_scene.addChild( sphere );
			makeObjectDraggable( sphere );
			
			mat = new PhongMaterial( light, 0xAAAAAA, 0x222222, 30 );
			mat.interactive = true;
			sphere = new Sphere( mat, 100, 15, 15 );
			sphere.x = 450;
			sphere.y = 300;
			sphere.z = 100;
			default_scene.addChild( sphere );
			makeObjectDraggable( sphere );
			
			stage.addEventListener( MouseEvent.MOUSE_MOVE, onMouseMove );
			
		}
		
		public function makeObjectDraggable( object : DisplayObject3D ) : void {
			object.addEventListener( InteractiveScene3DEvent.OBJECT_PRESS, onPress );
			object.addEventListener( InteractiveScene3DEvent.OBJECT_OVER, onOver );
			object.addEventListener( InteractiveScene3DEvent.OBJECT_OUT, onOut );
			object.addEventListener( InteractiveScene3DEvent.OBJECT_RELEASE, onRelease );
			object.addEventListener( InteractiveScene3DEvent.OBJECT_RELEASE_OUTSIDE, onRelease );
		}
		
		private var time : int = 0;
		
		override protected function processFrame() : void {
			var curTime : int = getTimer();
			var fps : int = 1000 / (curTime - time);
			fpsText.text = String( fps ) + " fps (limit 24)";
			if( fps > 20 ) {
				fpsText.backgroundColor = 0x00FF00;
			} else if( fps > 15 ) {
				fpsText.backgroundColor = 0x88FF00;
			} else if( fps > 10 ) {
				fpsText.backgroundColor = 0xFFFF00;
			} else if( fps > 5 ) {
				fpsText.backgroundColor = 0xFF8800;
			} else {
				fpsText.backgroundColor = 0xFF0000;
			}
			time = curTime;
		}
		
		
		private function onMouseMove( e : MouseEvent ) : void {
			if( dragging ) {
				var zDepth:Number = default_camera.getTrueScaleDistance();
				var screen:Number3D = new Number3D(viewport.containerSprite.mouseX, viewport.containerSprite.mouseY, zDepth);
				var ray:Number3D = default_camera.unprojectMatrix(screen);
				var cameraPosition : Number3D = new Number3D( default_camera.x, default_camera.y, default_camera.z );
				ray = Number3D.add( cameraPosition, ray);
				var flatPlane : Plane3D = Plane3D.fromNormalAndPoint( new Number3D( 0, 0, 1 ), new Number3D( 0, 0, -faceDepth ) );
				var intersect : Number3D = flatPlane.getIntersectionLineNumbers( cameraPosition, ray );
				
				dragObject.x = intersect.x;
				dragObject.y = intersect.y;
			}
		}
		
		private function onRelease( e : InteractiveScene3DEvent ) : void {
			dragging = false;
		}
		
		private function onOver ( e:InteractiveScene3DEvent ):void {
			viewport.buttonMode = true;
        }
       
        private function onOut ( e:InteractiveScene3DEvent ):void {
            viewport.buttonMode = false;
        }
       
        private function onPress( e:InteractiveScene3DEvent ):void {
			dragging = true;
			dragObject = e.displayObject3D;
        }
	}
}