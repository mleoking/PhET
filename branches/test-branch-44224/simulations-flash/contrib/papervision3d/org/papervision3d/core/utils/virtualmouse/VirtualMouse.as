/**
* @author Trevor McCauley
* @link www.senocular.com
*/

package org.papervision3d.core.utils.virtualmouse
{	

	
	import flash.display.DisplayObject;
	import flash.display.DisplayObjectContainer;
	import flash.display.InteractiveObject;
	import flash.display.SimpleButton;
	import flash.display.Sprite;
	import flash.display.Stage;
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.log.PaperLogger;
	
    /**
     * Dispatched when the virtual mouse state is updated.
     * @eventType flash.events.Event
     */
	[Event(name="update", type="flash.events.Event")]
	
    /**
     * Dispatched when the virtual mouse fires an
	 * Event.MOUSE_LEAVE event.
     * @eventType flash.events.Event
     */
	[Event(name="mouseLeave", type="flash.events.Event")]
	
    /**
     * Dispatched when the virtual mouse fires an
	 * MouseEvent.MOUSE_MOVE event.
     * @eventType flash.events.MouseEvent
     */
	[Event(name="mouseMove", type="flash.events.MouseEvent")]
	
    /**
     * Dispatched when the virtual mouse fires an
	 * MouseEvent.MOUSE_OUT event.
     * @eventType flash.events.MouseEvent
     */
	[Event(name="mouseOut", type="flash.events.MouseEvent")]
    /**
     * Dispatched when the virtual mouse fires an
	 * MouseEvent.ROLL_OUT event.
     * @eventType flash.events.MouseEvent
     */
	[Event(name="rollOut", type="flash.events.MouseEvent")]
	
    /**
     * Dispatched when the virtual mouse fires an
	 * MouseEvent.MOUSE_OVER event.
     * @eventType flash.events.MouseEvent
     */
	[Event(name="mouseOver", type="flash.events.MouseEvent")]
	
    /**
     * Dispatched when the virtual mouse fires an
	 * MouseEvent.ROLL_OVER event.
     * @eventType flash.events.MouseEvent
     */
	[Event(name="rollOver", type="flash.events.MouseEvent")]
	
    /**
     * Dispatched when the virtual mouse fires an
	 * MouseEvent.MOUSE_DOWN event.
     * @eventType flash.events.MouseEvent
     */
	[Event(name="mouseDown", type="flash.events.MouseEvent")]
	
    /**
     * Dispatched when the virtual mouse fires an
	 * MouseEvent.MOUSE_UP event.
     * @eventType flash.events.MouseEvent
     */
	[Event(name="mouseUp", type="flash.events.MouseEvent")]
	
    /**
     * Dispatched when the virtual mouse fires an
	 * MouseEvent.CLICK event.
     * @eventType flash.events.MouseEvent
     */
	[Event(name="click", type="flash.events.MouseEvent")]
	
    /**
     * Dispatched when the virtual mouse fires an
	 * MouseEvent.DOUBLE_CLICK event.
     * @eventType flash.events.MouseEvent
     */
	[Event(name="doubleClick", type="flash.events.MouseEvent")]
	
	/**
	 * The VirtualMouse class is used to create a programmatic 
	 * version of the users mouse that can be moved about the
	 * Flash player stage firing off mouse events of the display
	 * objects it interacts with.  This can allow you to simulate
	 * interaction with buttons and movie clips through ActionScript.
	 * <br />
	 * Handled events include:
	 * 		Event.MOUSE_LEAVE,
	 * 		MouseEvent.MOUSE_MOVE,
	 * 		MouseEvent.MOUSE_OUT,
	 * 		MouseEvent.ROLL_OUT,
	 * 		MouseEvent.MOUSE_OVER,
	 * 		MouseEvent.ROLL_OVER,
	 * 		MouseEvent.MOUSE_DOWN,
	 * 		MouseEvent.MOUSE_UP.
	 * 		MouseEvent.CLICK, and,
	 * 		MouseEvent.DOUBLE_CLICK.
	 * Along with dispatching those events for their respective
	 * targets, the VirtualMouse instance will also dispatch the
	 * event on itself allowing to capture which events are being
	 * fired by the virtual mouse.  The last event fired can also
	 * be referenced in the lastEvent property.
	 * <br />
	 * VirtualMouse mouse cannot:
	 * 		activate states of SimpleButton instances, 
	 * 		change object focus, 
	 * 		handle mouseWheel related events,
	 * 		change the system's cursor location, or 
	 * 		spoof the location of the mouseX and mouseY properties
	 * 			(which some components rely on).
	 */
	public class VirtualMouse extends EventDispatcher {
		
		public static const UPDATE:String = "update";
		
		private var altKey:Boolean = false;
		private var ctrlKey:Boolean = false;
		private var shiftKey:Boolean = false;
		private var delta:int = 0; // mouseWheel unsupported
		
		private var _stage:Stage;
		private var _container:Sprite;
		private var target:InteractiveObject;
		
		private var location:Point;
		
		private var isLocked:Boolean = false;
		private var isDoubleClickEvent:Boolean = false;
		private static var _mouseIsDown:Boolean = false;
		
		private var disabledEvents:Object = new Object();
		private var ignoredInstances:Dictionary = new Dictionary(true);
		
		private var _lastEvent:Event;
		private var lastMouseDown:Boolean = false;
		private var updateMouseDown:Boolean = false;
		private var lastLocation:Point;
		private var lastDownTarget:DisplayObject;
		private var lastWithinStage:Boolean = true;
			
		private var _useNativeEvents:Boolean = false;
		private var eventEvent:Class = VirtualMouseEvent;
		private var mouseEventEvent:Class = VirtualMouseMouseEvent;
		
		
		
		/**
		 * A reference to the Stage instance. This
		 * reference needs to be passed to the 
		 * VirtualMouse instance either in its constructor
		 * or through assigning it's stage property.
		 * Without a valid reference to the stage, the
		 * virtual mouse will not function.
		 * @see VirtualMouse()
		 */
		public function get stage():Stage {
			return _stage;
		}
		public function set stage(s:Stage):void {
			var hadStage:Boolean;
			if (_stage){
				hadStage = true;
				_stage.removeEventListener(KeyboardEvent.KEY_DOWN, keyHandler);
				_stage.removeEventListener(KeyboardEvent.KEY_UP, keyHandler);
			}else{
				hadStage = false;
			}
			_stage = s;
			if (_stage) {
				_stage.addEventListener(KeyboardEvent.KEY_DOWN, keyHandler);
				_stage.addEventListener(KeyboardEvent.KEY_UP, keyHandler);
				target = _stage;
				if (!hadStage) update();
			}
		}
		
		/**
		* 
		* @param	value Sprite container you want VirtualMouse to use with its testing of sub containers
		* @return
		*/
		public function set container(value:Sprite):void
		{
			_container = value;
		}
		public function get container():Sprite { return _container; }
		
		/**
		 * The last event dispatched by the VirtualMouse
		 * instance.  This can be useful for preventing
		 * event recursion if performing VirtualMouse
		 * operations within MouseEvent handlers.
		 */
		public function get lastEvent():Event {
			return _lastEvent;
		}
		
		/**
		 * True if the virtual mouse is being
		 * pressed, false if not.  The mouse is
		 * down for the virtual mouse if press()
		 * was called.
		 * @see press()
		 * @see release()
		 */
		public function get mouseIsDown():Boolean {
			return _mouseIsDown;
		}
		
		/**
		 * The x location of the virtual mouse. If you are
		 * setting both the x and y properties of the
		 * virtual mouse at the same time, you would probably
		 * want to lock the VirtualMouse instance to prevent
		 * additional events from firing.
		 * @see lock
		 * @see unlock
		 * @see y
		 * @see setLocation()
		 * @see getLocation()
		 */
		public function get x():Number {
			return location.x;
		}
		public function set x(n:Number):void {
			location.x = n;
			if (!isLocked) update();
		}
		
		/**
		 * The y location of the virtual mouse.  If you are
		 * setting both the x and y properties of the
		 * virtual mouse at the same time, you would probably
		 * want to lock the VirtualMouse instance to prevent
		 * additional events from firing.
		 * @see lock
		 * @see unlock
		 * @see x
		 * @see setLocation()
		 * @see getLocation()
		 */
		public function get y():Number {
			return location.y;
		}
		public function set y(n:Number):void {
			location.y = n;
			if (!isLocked) update();
		}
		
		/**
		 * Determines if the events dispatched by the
		 * VirtualMouse instance are IVirualMouseEvent
		 * Events (wrapping Event and MouseEvent) or events
		 * of the native Event and MouseEvent type. When using
		 * non-native events, you can check to see if the
		 * events originated from VirtualMouse by seeing if
		 * the events are of the type IVirualMouseEvent.
		 * @see lastEvent
		 */
		public function get useNativeEvents():Boolean {
			return _useNativeEvents;
		}
		public function set useNativeEvents(b:Boolean):void {
			if (b == _useNativeEvents) return;
			_useNativeEvents = b;
			if (_useNativeEvents){
				eventEvent = VirtualMouseEvent;
				mouseEventEvent = VirtualMouseMouseEvent;
			}else{
				eventEvent = Event;
				mouseEventEvent = MouseEvent;
			}
		}
		
		/** 
		 * Initializes a new VirtualMouse instance. 
		 * @param stage A reference to the stage instance.
		 * @param startX The initial x location of
		 * 		the virtual mouse.
		 * @param startY The initial y location of
		 * 		the virtual mouse.
		 */
		public function VirtualMouse(stage:Stage = null, container:Sprite = null, startX:Number = 0, startY:Number = 0) {
			this.stage = stage;
			this.container = container;
			location = new Point(startX, startY);
			lastLocation = location.clone();
			addEventListener(UPDATE, handleUpdate);
			update();
		}
		
		/**
		 * Returns the location (x and y) of the current
		 * VirtualMouse instance. The location of the
		 * virtual mouse is based in the global
		 * coordinate space.
		 * @return A Point instance representing the 
		 * 		location of the virtual mouse in
		 * 		global coordinate space.
		 * @see x
		 * @see y
		 * @see setLocation()
		 */
		public function getLocation():Point {
			return location.clone();
		}
		
		/**
		 * Sets the location (x and y) of the current
		 * VirtualMouse instance.  There are two ways to
		 * call setLocation, either passing in a single
		 * Point instance, or by passing in two Number
		 * instances representing x and y coordinates.
		 * The location of the virtual mouse is based in
		 * the global coordinate space.
		 * @param a A Point instance or x Number value.
		 * @param b A y Number value if a is a Number.
		 * @see x
		 * @see y
		 * @see getLocation()
		 */
		public function setLocation(a:*, b:* = null):void 
		{
			//log.debug("VM setLocation", a, b);
			if (a is Point) {
				var loc:Point = a as Point;
				location.x = loc.x;
				location.y = loc.y;
			}else{
				location.x = Number(a);
				location.y = Number(b);
			}

			if (!isLocked) update();
		}
		
		/**
		 * Locks the current VirtualMouse instance
		 * preventing updates from being made as 
		 * properties change within the instance.
		 * To release and allow an update, call unlock().
		 * @see lock()
		 * @see update()
		 */
		public function lock():void {
			isLocked = true;
		}
		
		/**
		 * Unlocks the current VirtualMouse instance
		 * allowing updates to be made for the
		 * dispatching of virtual mouse events. After
		 * unlocking the instance, it will update and
		 * additional calls to press(), release(), or
		 * changing the location of the virtual mouse
		 * will also invoke updates.
		 * @see lock()
		 * @see update()
		 */
		public function unlock():void {
			isLocked = false;
			update();
		}
		
		/**
		 * Allows you to disable an event by type
		 * preventing the virtual mouse from 
		 * dispatching that event during an update.
		 * @param type The type for the event to
		 * 		disable, e.g. MouseEvent.CLICK
		 * @see enableEvent()
		 */
		public function disableEvent(type:String):void {
			disabledEvents[type] = true;
		}
		
		/**
		 * Re-enables an event disabled with
		 * disableEvent.
		 * @param type The type for the event to
		 * 		enable, e.g. MouseEvent.CLICK
		 * @see disableEvent()
		 */
		public function enableEvent(type:String):void {
			if (type in disabledEvents) {
				delete disabledEvents[type];
			}
		}
		
		/**
		 * Ignores a display object preventing that
		 * object from recieving events from the
		 * virtual mouse.  This is useful for instances
		 * used for cursors which may always be under
		 * the virtual mouse's location.
		 * @param instance A reference to the
		 * 		DisplayObject instance to ignore.
		 * @see unignore()
		 */
		public function ignore(instance:DisplayObject):void {
			ignoredInstances[instance] = true;
		}
		
		/**
		 * Removes an instance from the ignore list
		 * defined by ignore().  When an ingored
		 * object is passed into unignore(), it will
		 * be able to receive events from the virtual
		 * mouse.
		 * @param instance A reference to the
		 * 		DisplayObject instance to unignore.
		 * @see ignore()
		 */
		public function unignore(instance:DisplayObject):void {
			if (instance in ignoredInstances){
				delete ignoredInstances[instance];
			}
		}
		
		/**
		 * Simulates the pressing of the left
		 * mouse button. To release the mouse
		 * button, use release().
		 * @see release()
		 * @see click()
		 */
		public function press():void {
			//if (_mouseIsDown) return;
			updateMouseDown = true;
			_mouseIsDown = true;
			if (!isLocked) update();
		}
		
		/**
		 * Simulates the release of the left
		 * mouse button.  This method has no
		 * effect unless press() was called first.
		 * @see press()
		 * @see click()
		 */
		public function release():void {
			//if (!_mouseIsDown) return;
			updateMouseDown = true;
			_mouseIsDown = false;
			if (!isLocked) update();
		}
		
		/**
		 * Simulates a click of the left
		 * mouse button (press and release)
		 * @see press()
		 * @see release()
		 * @see click()
		 * @see doubleClick()
		 */
		public function click():void {
			press();
			release();
		}
		
		/**
		 * Simulates a double-click of the left
		 * mouse button (press and release twice).
		 * Calling this command is the only way to
		 * simulate a double-click for the virtual
		 * mouse.  Calling press() and release() or
		 * click() is rapid succession will not
		 * invoke a double-click event. The double-click
		 * event will also only fire for an instance
		 * if it's doubleClickEnabled property is
		 * set to true.
		 * @see click()
		 */
		public function doubleClick():void {
			// if locked, doubleClick will
			// not fire but the mouse will 
			// be released if not already
			if (isLocked) {
				release();
			}else{

				// call update with a click, press, then release
				// and double-click notification for release
				click();
				press();
				isDoubleClickEvent = true;
				release();
				isDoubleClickEvent = false;
			}
		}
		
		/*Added by Jim Kremens kremens@gmail.com 08/16/07 */
		public function exitContainer():void {
			if( !container ) return;
			var targetLocal:Point = target.globalToLocal(location);
			//log.debug("Targetlocal", target != container);
			if (!disabledEvents[MouseEvent.MOUSE_OUT]) {
				_lastEvent = new mouseEventEvent(MouseEvent.MOUSE_OUT, true, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
				container.dispatchEvent(new mouseEventEvent(MouseEvent.MOUSE_OUT, true, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta));
				dispatchEvent(new mouseEventEvent(MouseEvent.MOUSE_OUT, true, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta));
			}
			if (!disabledEvents[MouseEvent.ROLL_OUT]) { // rolls do not propagate
				_lastEvent = new mouseEventEvent(MouseEvent.ROLL_OUT, false, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
				container.dispatchEvent(new mouseEventEvent(MouseEvent.ROLL_OUT, false, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta));
				dispatchEvent(new mouseEventEvent(MouseEvent.ROLL_OUT, false, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta));
			}
			if (target != container) {
				if (!disabledEvents[MouseEvent.MOUSE_OUT]) {
					_lastEvent = new mouseEventEvent(MouseEvent.MOUSE_OUT, true, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
					target.dispatchEvent(new mouseEventEvent(MouseEvent.MOUSE_OUT, true, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta));
					dispatchEvent(new mouseEventEvent(MouseEvent.MOUSE_OUT, true, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta));
				}
				if (!disabledEvents[MouseEvent.ROLL_OUT]) { // rolls do not propagate
					_lastEvent = new mouseEventEvent(MouseEvent.ROLL_OUT, false, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
					target.dispatchEvent(new mouseEventEvent(MouseEvent.ROLL_OUT, false, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta));
					dispatchEvent(new mouseEventEvent(MouseEvent.ROLL_OUT, false, false, targetLocal.x, targetLocal.y, container, ctrlKey, altKey, shiftKey, _mouseIsDown, delta));
				}
			}
			//reset the target to the stage, which is the value it starts at.
			target = _stage;
		}
		
		/**
		 * Updates the VirtualMouse instance's state
		 * to reflect a change in the virtual mouse.
		 * Within this method all events will be dispatched.
		 * update() is called any time a VirtualMouse
		 * property is changed unless lock() was used to
		 * lock the instance.  update() will then not be
		 * called until unlock() is used to unlock
		 * the instance. Typically you would never call
		 * update() directly; it is called automatically
		 * by the VirtualMouse class. Calling update()
		 * manually will override lock(). Whenever update()
		 * is called, the UPDATE event is dispatched.
		 * @see lock()
		 * @see unlock()
		 */
		public function update():void {
			// dispatch an update event indicating that 
			// an update has occured
			dispatchEvent(new Event(UPDATE, false, false));
		}
		
		private function handleUpdate(event:Event):void 
		{
			if (!container) return;
				
			// go through each objectsUnderPoint checking:
			// 		1) is not ignored
			//		2) is InteractiveObject
			//		3) mouseEnabled
			// 		4) all parents have mouseChildren
			// if not interactive object, defer interaction to next object in list
			// if is interactive and enabled, give interaction and ignore rest
			
			// var p:Point = CoordinateTools.localToLocal(container, stage, location);
			//var objectsUnderPoint:Array = container.getObjectsUnderPoint(p); 
			
			if( container.scrollRect ){
				PaperLogger.warning("The container that virtualMouse is trying to test against has a scrollRect defined, and may cause an issue with finding objects under a defined point.  Use MovieMaterial.rect to set a rectangle area instead");
			}
			var originalPoint:Point = new Point();
			originalPoint.x = container.x;
			originalPoint.y = container.y;
			container.x = container.y = 0;
			
			var objectsUnderPoint:Array = container.getObjectsUnderPoint(location);
			
			container.x = originalPoint.x;
			container.y = originalPoint.y;
			
			var currentTarget:InteractiveObject;
			var currentParent:DisplayObject;
			
			var i:int = objectsUnderPoint.length;
			
			while (i--) {
				currentParent = objectsUnderPoint[i];
				
				// go through parent hierarchy
				while (currentParent) {
					
					// don't use ignored instances as the target
					if (ignoredInstances[currentParent]
					) {
						currentTarget = null;
						break;
					}
					
					// invalid target if in a SimpleButton
					if (currentTarget && currentParent is SimpleButton) 
					{
						//log.debug("found SimpleButton - setting currentTarget to null");
						currentTarget = null;
						
					// invalid target if a parent has a
					// false mouseChildren
					} else if (currentTarget && !DisplayObjectContainer(currentParent).mouseChildren) 
					{
						//log.debug("parent false mouseChildren - setting currentTarget to null");
						currentTarget = null;
					}
					
					// define target if an InteractiveObject
					// and mouseEnabled is true
					if (!currentTarget && currentParent is InteractiveObject && InteractiveObject(currentParent).mouseEnabled) 
					{
						//log.debug("found InteractiveObject && mouseEnabled = true - setting currentTarget");
						currentTarget = InteractiveObject(currentParent);
					}
					
					// next parent in hierarchy
					currentParent = currentParent.parent;
				}
				
				// if a currentTarget was found
				// ignore all other objectsUnderPoint
				if (currentTarget){
					break;
				}
			}
			
			
			
			// if a currentTarget was not found
			// the currentTarget is the stage
			if (!currentTarget)
			{
				//trace("no CurrentTarget", container.hitTestPoint(location.x, location.y));
				currentTarget = container;
				//currentTarget = _stage;
				//log.debug("no new target found, using stage");
			}
			
			// get local coordinate locations
			var targetLocal:Point = target.globalToLocal(location);
			var currentTargetLocal:Point = currentTarget.globalToLocal(location);
			
			
			// move event
			if (lastLocation.x != location.x || lastLocation.y != location.y) 
			{				
				var withinStage:Boolean = false;
				if(stage) withinStage = (location.x >= 0 && location.y >= 0 && location.x <= stage.stageWidth && location.y <= stage.stageHeight);
				
				// mouse leave if left stage
				if (!withinStage && lastWithinStage && !disabledEvents[Event.MOUSE_LEAVE]){
					_lastEvent = new eventEvent(Event.MOUSE_LEAVE, false, false);
					stage.dispatchEvent(_lastEvent);
					dispatchEvent(_lastEvent);
				}
				
				// only mouse move if within stage
				if (withinStage && !disabledEvents[MouseEvent.MOUSE_MOVE]){
					_lastEvent = new mouseEventEvent(MouseEvent.MOUSE_MOVE, true, false, currentTargetLocal.x, currentTargetLocal.y, currentTarget, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
					currentTarget.dispatchEvent(_lastEvent);
					dispatchEvent(_lastEvent);
				}
				
				// remember if within stage
				lastWithinStage = withinStage;
			}
			
			// roll/mouse (out and over) events 
			
			if (currentTarget != target) 
			{	
				// off of last target
				if (!disabledEvents[MouseEvent.MOUSE_OUT]){
					_lastEvent = new mouseEventEvent(MouseEvent.MOUSE_OUT, true, false, targetLocal.x, targetLocal.y, currentTarget, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
					target.dispatchEvent(_lastEvent);
					dispatchEvent(_lastEvent);
				}
				if (!disabledEvents[MouseEvent.ROLL_OUT]){ // rolls do not propagate
					_lastEvent = new mouseEventEvent(MouseEvent.ROLL_OUT, false, false, targetLocal.x, targetLocal.y, currentTarget, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
					target.dispatchEvent(_lastEvent);
					dispatchEvent(_lastEvent);
				}
				
				// on to current target
				if (!disabledEvents[MouseEvent.MOUSE_OVER]){
					//log.debug("*** MOUSEOVER****");
					_lastEvent = new mouseEventEvent(MouseEvent.MOUSE_OVER, true, false, currentTargetLocal.x, currentTargetLocal.y, target, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
					currentTarget.dispatchEvent(_lastEvent);
					dispatchEvent(_lastEvent);
				}
				if (!disabledEvents[MouseEvent.ROLL_OVER]){ // rolls do not propagate
					//log.debug("*** ROLLOVER****");
					_lastEvent = new mouseEventEvent(MouseEvent.ROLL_OVER, false, false, currentTargetLocal.x, currentTargetLocal.y, target, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
					currentTarget.dispatchEvent(_lastEvent);
					dispatchEvent(_lastEvent);
				}
			
			
			}
			
			// click/up/down events
			if (updateMouseDown) {
				if (_mouseIsDown) {
					
					if (!disabledEvents[MouseEvent.MOUSE_DOWN]){
						_lastEvent = new mouseEventEvent(MouseEvent.MOUSE_DOWN, true, false, currentTargetLocal.x, currentTargetLocal.y, currentTarget, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
						currentTarget.dispatchEvent(_lastEvent);
						dispatchEvent(_lastEvent);
					}
					
					// remember last down
					lastDownTarget = currentTarget;
					updateMouseDown = false;
				// mouse is up
				}else{
					if (!disabledEvents[MouseEvent.MOUSE_UP]){
						_lastEvent = new mouseEventEvent(MouseEvent.MOUSE_UP, true, false, currentTargetLocal.x, currentTargetLocal.y, currentTarget, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
						currentTarget.dispatchEvent(_lastEvent);
						dispatchEvent(_lastEvent);
					}
					
					if (!disabledEvents[MouseEvent.CLICK] && currentTarget == lastDownTarget) {
						_lastEvent = new mouseEventEvent(MouseEvent.CLICK, true, false, currentTargetLocal.x, currentTargetLocal.y, currentTarget, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
						currentTarget.dispatchEvent(_lastEvent);
						dispatchEvent(_lastEvent);
					}
					
					// clear last down
					lastDownTarget = null;
					updateMouseDown = false;
				}
			}
			
			// explicit call to doubleClick()
			if (isDoubleClickEvent && !disabledEvents[MouseEvent.DOUBLE_CLICK] && currentTarget.doubleClickEnabled) {
				_lastEvent = new mouseEventEvent(MouseEvent.DOUBLE_CLICK, true, false, currentTargetLocal.x, currentTargetLocal.y, currentTarget, ctrlKey, altKey, shiftKey, _mouseIsDown, delta);
				currentTarget.dispatchEvent(_lastEvent);
				dispatchEvent(_lastEvent);
			}
			
			// remember last values
			lastLocation = location.clone();
			lastMouseDown = _mouseIsDown;
			target = currentTarget;
		}
		
		private function keyHandler(event:KeyboardEvent):void {
			// update properties used in MouseEvents
			altKey = event.altKey;
			ctrlKey = event.ctrlKey;
			shiftKey = event.shiftKey;
		}
	}
}