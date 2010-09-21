package org.papervision3d.core.effects.view {
	import org.papervision3d.cameras.Camera3D;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.core.proto.CameraObject3D;
	import org.papervision3d.view.BasicView;
	import org.papervision3d.view.Viewport3D;
	
	import flash.events.Event;
	import flash.geom.ColorTransform;	

	public class ReflectionView extends BasicView
	{
		
		public var viewportReflection : Viewport3D; 
		public var cameraReflection : CameraObject3D;
		public var surfaceHeight : Number = 0; 
		
		//public var reflectionMatrix : Matrix3D;  // for future use... 
		
		private var _autoScaleToStage : Boolean; 
		
		public function ReflectionView(viewportWidth:Number=640, viewportHeight:Number=320, scaleToStage:Boolean=true, interactive:Boolean=false, cameraType:String="Target")
		{
			super(viewportWidth, viewportHeight, scaleToStage, interactive, cameraType);
			
			//set up reflection viewport and camera
			viewportReflection = new Viewport3D(viewportWidth, viewportHeight,scaleToStage, false); 

			// For future use... 
			//reflectionMatrix = new Matrix3D(); 
			//createReflectionMatrix(null); 
			
			
			
			// add the reflection viewport to the stage 
			addChild(viewportReflection); 
			setChildIndex(viewportReflection,0); 
			
			// flip it
			viewportReflection.scaleY = -1; 

			// and move it down
			viewportReflection.y = viewportHeight;  

			cameraReflection = new Camera3D(); 
			
			

    		// SAVING THIS CODE FOR LATER (may require transparent reflections... )
			/*var matrix:Array = new Array();
            matrix = matrix.concat([0.4, 0, 0, 0, 0]); // red
            matrix = matrix.concat([0, 0.4, 0, 0, 0]); // green
            matrix = matrix.concat([0, 0, 0.4, 0, 0]); // blue
            matrix = matrix.concat([0, 0, 0, 1, 0]); // alpha
			viewportReflection.filters = [new ColorMatrixFilter(matrix),new BlurFilter(8,8,1)]; 
			*/
			
			addEventListener(Event.ADDED_TO_STAGE, onAddedToStage);
			addEventListener(Event.REMOVED_FROM_STAGE, onRemovedFromStage);
			
			this.autoScaleToStage = scaleToStage; 
			
			setReflectionColor(0.5,0.5,0.5); 
		}
		
		public override function singleRender():void
		{
			
			cameraReflection.zoom = camera.zoom; 
			cameraReflection.focus = camera.focus; 
			if(camera is Camera3D)
			{
				Camera3D(cameraReflection).useCulling = Camera3D(camera).useCulling; 
			
			}
			if(camera.target) camera.lookAt(camera.target); 
			cameraReflection.transform.copy(camera.transform);
			
			// reflection matrix! Doesn't work yet - turns planes inside out :-S
			//cameraReflection.transform.calculateMultiply(cameraReflection.transform, reflectionMatrix); 
			
			cameraReflection.y=-camera.y;
			cameraReflection.rotationX = -camera.rotationX;
			cameraReflection.rotationY = camera.rotationY;
			cameraReflection.rotationZ = -camera.rotationZ;
			
			cameraReflection.y+=surfaceHeight; 
			
			
			
			renderer.renderScene(scene, cameraReflection, viewportReflection);			
			super.singleRender(); 
		
		}
		
		
		public function setReflectionColor(redMultiplier:Number=0, greenMultiplier:Number=0, blueMultiplier:Number=0, redOffset:Number=0, greenOffset:Number=0, blueOffset:Number=0): void
		{
			viewportReflection.transform.colorTransform = new ColorTransform(redMultiplier, greenMultiplier, blueMultiplier, 1, redOffset, greenOffset, blueOffset); 
			
		}
	
	
		/* For future use... 
	
		public function createReflectionMatrix(plane:Plane3D):void
		{
			var a:Number = 0;//plane.normal.x;
			var b:Number = 1;//plane.normal.y;
			var c:Number = 0;//plane.normal.z;
			
			
			reflectionMatrix.n11 = 1-(2*a*a);
			reflectionMatrix.n12 = 0-(2*a*b);
			reflectionMatrix.n13 = 0-(2*a*c);
			
			reflectionMatrix.n21 = 0-(2*a*b);
			reflectionMatrix.n22 = 1-(2*b*b);
			reflectionMatrix.n23 = 0-(2*b*c);
			
			reflectionMatrix.n31 = 0-(2*a*c);
			reflectionMatrix.n32 = 0-(2*b*c);
			reflectionMatrix.n33 = 1-(2*c*c);
		}
		
		*/

		/**
		 * We need  to move the reflection view whenever the stage is resized so we have to implement
		 * the same functionality as the Viewport3D, ie we add a stage resize listener (once we're on the stage). 
		 */
		 
		 
		public function set autoScaleToStage(scale:Boolean):void
		{
			_autoScaleToStage = scale;
			if(scale && stage != null)
			{
				onStageResize();
			}
			
		}
		
		/**
		 * Triggered when added to the stage to start listening to stage resizing
		 */
		protected function onAddedToStage(event:Event):void
		{
			stage.addEventListener(Event.RESIZE, onStageResize);
			onStageResize();
		}

		/**
		 * Triggered when removed from the stage to remove the stage resizing listener
		 */
		protected function onRemovedFromStage(event:Event):void
		{
			stage.removeEventListener(Event.RESIZE, onStageResize);
		}
		
		// all we need to do is move the view down
		private function onStageResize(e:Event = null) : void
		{
			viewportReflection.y = stage.stageHeight;  
			
		}
				
				
	}
}