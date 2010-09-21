package org.papervision3d.cameras
{
	import flash.display.Sprite;
	import flash.display.Stage;
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.ui.Keyboard;
	
	import org.papervision3d.view.Viewport3D;
	
	/**
	 * <p>
	 * DebugCamera3D serves as a tool to allow you control
	 * the camera with your mouse and keyboard while displaying information
	 * about the camera when testing your swf. Due to its nature,
	 * the Keyboard and Mouse Events may interfere with your custom Keyboard and Mouse Events.
	 * This camera is in no way intended for production use.
	 * </p>
	 * 
	 * <p>
	 * Click and drag for mouse movement. The keys
	 * are setup as follows:
	 * </p>
	 * <pre><code>
	 * w = forward
	 * s = backward
	 * a = left
	 * d = right
	 * q = rotationZ--
	 * e = rotationZ++
	 * r = fov++
	 * f = fov--
	 * t = near++
	 * g = near--
	 * y = far++
	 * h = far--
	 * </code></pre>
	 * 
	 * @author John Lindquist
	 */
	public class DebugCamera3D extends Camera3D
	{
		/** @private */
		protected var _propertiesDisplay:Sprite;
		/** @private */
		protected var _inertia:Number = 3;
		/** @private */
		protected var viewportStage:Stage;
		/** @private */
		protected var startPoint:Point;
		/** @private */
		protected var startRotationY:Number;
		/** @private */
		protected var startRotationX:Number;
		/** @private */
		protected var targetRotationY:Number = 0;
		/** @private */
		protected var targetRotationX:Number = 0;
		/** @private */
		protected var keyRight:Boolean = false;
		/** @private */
		protected var keyLeft:Boolean = false;
		/** @private */
		protected var keyForward:Boolean = false;
		/** @private */
		protected var keyBackward:Boolean = false;
		/** @private */
		protected var forwardFactor:Number = 0;
		/** @private */
		protected var sideFactor:Number = 0;
		/** @private */
		protected var xText:TextField;
		/** @private */
		protected var yText:TextField;
		/** @private */
		protected var zText:TextField;
		/** @private */
		protected var rotationXText:TextField;
		/** @private */
		protected var rotationYText:TextField;
		/** @private */
		protected var rotationZText:TextField;
		/** @private */
		protected var fovText:TextField;
		/** @private */
		protected var nearText:TextField;
		/** @private */
		protected var farText:TextField;
		/** @private */
		protected var viewport3D:Viewport3D;
		
		/**
		 * DebugCamera3D
		 *
		 * @param viewport	Viewport to render to. @see org.papervision3d.view.Viewport3D 
		 * @param fovY		Field of view (vertical) in degrees.
		 * @param near		Distance to near plane.
		 * @param far		Distance to far plane.
		 */
		public function DebugCamera3D(viewport3D:Viewport3D, fovY:Number = 90, near:Number = 10, far:Number = 5000) 
		{
			super(fovY, near, far, true);
			
			this.viewport3D = viewport3D;
			this.viewport = viewport3D.sizeRectangle;
			
			this.focus = (this.viewport.height / 2) / Math.tan((fovY/2) * (Math.PI/180));
			this.zoom = this.focus / near;
			this.focus = near;
			this.far = far;
			
			displayProperties();
			checkStageReady();	
		}
		
		/**
		 * Checks if the viewport is ready for events
		 */
		private function checkStageReady():void
		{
			if(viewport3D.containerSprite.stage == null)
			{
				viewport3D.containerSprite.addEventListener(Event.ADDED_TO_STAGE, onAddedToStageHandler);
			}
			else
			{
				setupEvents();
			}
		}

		/**
		 * Dispatched with the viewport container is added to the stage
		 */
		protected function onAddedToStageHandler(event:Event):void 
		{
			setupEvents();
		}
		
		/**
		 * Builds the Sprite that displays the camera properties
		 */
		protected function displayProperties():void 
		{
			_propertiesDisplay = new Sprite();
			_propertiesDisplay.graphics.beginFill(0x000000);
			_propertiesDisplay.graphics.drawRect(0, 0, 100, 100);
			_propertiesDisplay.graphics.endFill();
			
			_propertiesDisplay.x = 0;
			_propertiesDisplay.y = 0;
			
			var format:TextFormat = new TextFormat("_sans", 9);
			
			xText = new TextField();
			yText = new TextField();
			zText = new TextField();
			rotationXText = new TextField();
			rotationYText = new TextField();
			rotationZText = new TextField();
			fovText = new TextField();
			nearText = new TextField();
			farText = new TextField();
			
			var textFields:Array = [xText, yText, zText, rotationXText, rotationYText, rotationZText, fovText, nearText, farText];
			var textFieldYSpacing:int = 10;
			
			for (var i:Number = 0;i < textFields.length; i++) 
			{
				textFields[i].width = 100;
				textFields[i].selectable = false;
				textFields[i].textColor = 0xFFFF00;
				textFields[i].text = '';
				textFields[i].defaultTextFormat = format;
				textFields[i].y = textFieldYSpacing * i;
				_propertiesDisplay.addChild(textFields[i]);
			}
			
			
			viewport3D.addChild(_propertiesDisplay);
		}
		
		/**
		 * Sets up the Mouse and Keyboard Events required for adjusting the camera properties
		 */
		protected function setupEvents():void 
		{
			viewportStage = viewport3D.containerSprite.stage;
			viewportStage.addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler);
			viewportStage.addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
			viewportStage.addEventListener(KeyboardEvent.KEY_DOWN, keyDownHandler);
			viewportStage.addEventListener(KeyboardEvent.KEY_UP, keyUpHandler);
			viewportStage.addEventListener(Event.ENTER_FRAME, onEnterFrameHandler);
		}
		
		/**
	     *  The default handler for the <code>MouseEvent.MOUSE_DOWN</code> event.
	     *
	     *  @param The event object.
	     */
		protected function mouseDownHandler(event:MouseEvent):void 
		{
			viewportStage.addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
			startPoint = new Point(viewportStage.mouseX, viewportStage.mouseY);
			startRotationY = this.rotationY;
			startRotationX = this.rotationX;
		}
			
		/**
	     *  The default handler for the <code>MouseEvent.MOUSE_MOVE</code> event.
	     *
	     *  @param The event object.
	     */
		protected function mouseMoveHandler(event:MouseEvent):void 
		{
			targetRotationY = startRotationY - (startPoint.x - viewportStage.mouseX) / 2;
			targetRotationX = startRotationX + (startPoint.y - viewportStage.mouseY) / 2;
		}
		
		/**
	     *  Removes the mouseMoveHandler on the <code>MouseEvent.MOUSE_UP</code> event.
	     *
	     *  @param The event object.
	     */
		protected function mouseUpHandler(event:MouseEvent):void 
		{
			viewportStage.removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
		}

		/**
	     *  Adjusts the camera based on the keyCode from the <code>KeyboardEvent.KEY_DOWN</code> event.
	     *
	     *  @param The event object.
	     */
		protected function keyDownHandler(event:KeyboardEvent):void 
		{
			switch( event.keyCode ) 
			{
				case "W".charCodeAt():
				case Keyboard.UP:
					keyForward = true;
					keyBackward = false;
					break;
	
				case "S".charCodeAt():
				case Keyboard.DOWN:
					keyBackward = true;
					keyForward = false;
					break;
	
				case "A".charCodeAt():
				case Keyboard.LEFT:
					keyLeft = true;
					keyRight = false;
					break;
	
				case "D".charCodeAt():
				case Keyboard.RIGHT:
					keyRight = true;
					keyLeft = false;
					break;
					
				case "Q".charCodeAt():
					rotationZ--;
					break;
				
				case "E".charCodeAt():
					rotationZ++;
					break;
				
				case "F".charCodeAt():
					fov--;
					break;
					
				case "R".charCodeAt():
					fov++;
					break;
					
				case "G".charCodeAt():
					near -= 10;
					break;
					
				case "T".charCodeAt():
					near += 10;
					break;
					
				case "H".charCodeAt():
					far -= 10;
					break;
					
				case "Y".charCodeAt():
					far += 10;
					break;
			}
		}
		
		/**
	     *  Checks which Key is released on the <code>KeyboardEvent.KEY_UP</code> event
	     *  and toggles that key's movement off.
	     *
	     *  @param The event object.
	     */
		protected function keyUpHandler(event:KeyboardEvent):void 
		{
			switch( event.keyCode ) 
			{
				case "W".charCodeAt():
				case Keyboard.UP:
					keyForward = false;
					break;
	
				case "S".charCodeAt():
				case Keyboard.DOWN:
					keyBackward = false;
					break;
	
				case "A".charCodeAt():
				case Keyboard.LEFT:
					keyLeft = false;
					break;
	
				case "D".charCodeAt():
				case Keyboard.RIGHT:
					keyRight = false;
					break;
			}
		}

		/**
	     *  Checks which keys are down and adjusts the camera accorindingly on the <code>Event.ENTER_FRAME</code> event.
	     *  Also updates the display of properties.
	     *
	     *  @param The event object.
	     */
		protected function onEnterFrameHandler(event:Event):void 
		{
			if(keyForward) 
			{
				forwardFactor += 50;
			}
			if(keyBackward) 
			{
				forwardFactor += -50;
			}
			if(keyLeft) 
			{
				sideFactor += -50;
			}
			if(keyRight) 
			{
				sideFactor += 50;
			}
			
			// rotation
			var rotationX:Number = this.rotationX + ( targetRotationX - this.rotationX ) / _inertia;
			var rotationY:Number = this.rotationY + ( targetRotationY - this.rotationY ) / _inertia;
			this.rotationX = Math.round(rotationX * 10) / 10;
			this.rotationY = Math.round(rotationY * 10) / 10;
			
			// position
			forwardFactor += ( 0 - forwardFactor ) / _inertia;
			sideFactor += ( 0 - sideFactor ) / _inertia;
			if (forwardFactor > 0) 
			{
				this.moveForward(forwardFactor);
			}else 
			{
				this.moveBackward(-forwardFactor);
			}
			if (sideFactor > 0) 
			{
				this.moveRight(sideFactor);
			}else 
			{
				this.moveLeft(-sideFactor);
			}
			
			xText.text = 'x:' + int(x);
			yText.text = 'y:' + int(y);
			zText.text = 'z:' + int(z);
			
			rotationXText.text = 'rotationX:' + int(rotationX);
			rotationYText.text = 'rotationY:' + int(rotationY);
			rotationZText.text = 'rotationZ:' + int(rotationZ);
			
			fovText.text = 'fov:' + Math.round(fov);
			nearText.text = 'near:' + Math.round(near);
			farText.text = 'far:' + Math.round(far);
		}

		/**
		 * A Sprite that displays the current properties of your camera
		 */	
		public function get propsDisplay():Sprite 
		{
			return _propertiesDisplay;
		}

		public function set propsDisplay(propsDisplay:Sprite):void 
		{
			_propertiesDisplay = propsDisplay;
		}

		/**
		 * The amount of resistance to the change in velocity when updating the camera rotation with the mouse
		 */
		public function get inertia():Number 
		{
			return _inertia;
		}

		public function set inertia(inertia:Number):void 
		{
			_inertia = inertia;
		}
	}
}