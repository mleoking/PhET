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
		
		public var cube : Cube;
		
		public var light : PointLight3D;
		
		protected var sceneWidth:Number;
        protected var sceneHeight:Number;
		
		private var dragging : Boolean = false;
		private var dragObject : DisplayObject3D;
		
		public function Main() {
			sceneWidth = stage.stageWidth
            sceneHeight = stage.stageHeight;
			
			
			var size : Number = 200;
			var mass : Number = 50;
			
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
			
			cube = new Cube( ml, 200, 200, 200, 4, 4, 4 );
			cube.z = 101;
			
			init( sceneWidth, sceneHeight );
		}
		
		var poolWidth : Number = 1500;
		var poolHeight : Number = 750;
		var poolDepth : Number = 500;
		var waterHeight: Number = 550;
		var volume : Number = poolWidth * poolDepth * waterHeight;
		var far : Number = 5000;
		
		public function getInteriorMaterial() : MaterialObject3D {
			//return new ColorMaterial( 0xAAAAAA );
			return new FlatShadeMaterial( light, 0xFFFFFF, 0x555555 );
		}
		
		public function getGroundMaterial() : MaterialObject3D {
			return new FlatShadeMaterial( light, 0x00BB00, 0x005500 );
		}
		
		public function getEarthMaterial() : MaterialObject3D {
			return new FlatShadeMaterial( light, 0xAA7733, 0x000000 );
		}
		
		override protected function init3d() : void {
			default_scene.addChild( cube );
			
//			cube.pitch( -10 );

			default_camera.y = 500;
			default_camera.z = -2000;
			default_camera.zoom = 90;
			default_camera.rotationX = 15;
			
			
			var plane : Plane = new Plane( getInteriorMaterial(), poolWidth, poolHeight, 1, 1 );
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
			
			cube.addEventListener( InteractiveScene3DEvent.OBJECT_PRESS, onPress );
			cube.addEventListener( InteractiveScene3DEvent.OBJECT_OVER, onOver );
			cube.addEventListener( InteractiveScene3DEvent.OBJECT_OUT, onOut );
			cube.addEventListener( InteractiveScene3DEvent.OBJECT_MOVE, onMove );
			cube.addEventListener( InteractiveScene3DEvent.OBJECT_RELEASE, onRelease );
			cube.addEventListener( InteractiveScene3DEvent.OBJECT_RELEASE_OUTSIDE, onRelease );
			
			stage.addEventListener( MouseEvent.MOUSE_MOVE, onMouseMove );
			
		}
		
		override protected function processFrame() : void {
			//cone.yaw( 1 );
			
			//default_camera.pan( 1 );
			
			//cube.yaw( 1 );
		}
		
		
		private function onMouseMove( e : MouseEvent ) : void {
			if( dragging ) {
				var zDepth:Number = default_camera.getTrueScaleDistance();
				var screen:Number3D = new Number3D(viewport.containerSprite.mouseX, viewport.containerSprite.mouseY, zDepth);
				var ray:Number3D = default_camera.unprojectMatrix(screen);
				var cameraPosition : Number3D = new Number3D( default_camera.x, default_camera.y, default_camera.z );
				ray = Number3D.add( cameraPosition, ray);
				var flatPlane : Plane3D = Plane3D.fromNormalAndPoint( new Number3D( 0, 0, 1 ), new Number3D( 0, 0, 0 ) );
				var intersect : Number3D = flatPlane.getIntersectionLineNumbers( cameraPosition, ray );
				
				dragObject.x = intersect.x;
				dragObject.y = intersect.y;
			}
		}
		
		private function onMove( e:InteractiveScene3DEvent ) : void {
			//cube.yaw( -2 );
		}
		
		private function onRelease( e : InteractiveScene3DEvent ) : void {
			dragging = false;
		}
		
		private function onOver ( e:InteractiveScene3DEvent ):void {
            
        }
       
        private function onOut ( e:InteractiveScene3DEvent ):void {
            
        }
       
        private function onPress( e:InteractiveScene3DEvent ):void {
            //cube.yaw( 20 );
			dragging = true;
			dragObject = e.displayObject3D;
        }
	}
}