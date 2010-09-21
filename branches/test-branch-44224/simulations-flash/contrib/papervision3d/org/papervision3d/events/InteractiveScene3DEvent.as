package org.papervision3d.events
{
	/**
	* ...
	* @author John Grden
	* @version 0.1
	*/
	
	import flash.display.Sprite;
	import flash.events.Event;
	
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.render.data.RenderHitData;
	import org.papervision3d.objects.DisplayObject3D;

	public class InteractiveScene3DEvent extends Event
	{
		/**
		 * Dispatched when a container in the ISM recieves a MouseEvent.CLICK event
		* @eventType mouseClick
		*/
		public static const OBJECT_CLICK:String = "mouseClick";
		/**
		 * Dispatched when a container in the ISM recieves a MouseEvent.CLICK event
		* @eventType mouseClick
		*/
		public static const OBJECT_DOUBLE_CLICK:String = "mouseDoubleClick";
		/**
		 * Dispatched when a container in the ISM receives an MouseEvent.MOUSE_OVER event
		* @eventType mouseOver
		*/
		public static const OBJECT_OVER:String = "mouseOver";
		/**
		 * Dispatched when a container in the ISM receives an MouseEvent.MOUSE_OUT event
		* @eventType mouseOut
		*/
		public static const OBJECT_OUT:String = "mouseOut";
		/**
		 * Dispatched when a container in the ISM receives a MouseEvent.MOUSE_MOVE event
		* @eventType mouseMove
		*/
		public static const OBJECT_MOVE:String = "mouseMove";
		/**
		 * Dispatched when a container in the ISM receives a MouseEvent.MOUSE_PRESS event
		* @eventType mousePress
		*/
		public static const OBJECT_PRESS:String = "mousePress";
		/**
		 * Dispatched when a container in the ISM receives a MouseEvent.MOUSE_RELEASE event
		* @eventType mouseRelease
		*/
		public static const OBJECT_RELEASE:String = "mouseRelease";
		/**
		 * Dispatched when the main container of the ISM is clicked
		* @eventType mouseReleaseOutside
		*/
		public static const OBJECT_RELEASE_OUTSIDE:String = "mouseReleaseOutside";
		/**
		 * Dispatched when a container is created in the ISM for drawing and mouse interaction purposes
		* @eventType objectAdded
		*/
		public static const OBJECT_ADDED:String = "objectAdded";
		
		public var displayObject3D				:DisplayObject3D = null;
		public var sprite						:Sprite = null;
		public var face3d						:Triangle3D = null;
		public var x							:Number = 0;
		public var y							:Number = 0;
		public var renderHitData:RenderHitData;
		
		public function InteractiveScene3DEvent(type:String, container3d:DisplayObject3D=null, sprite:Sprite=null, face3d:Triangle3D=null,x:Number=0, y:Number=0, renderhitData:RenderHitData = null, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this.displayObject3D = container3d;
			this.sprite = sprite;
			this.face3d = face3d;
			this.x = x;
			this.y = y;
			this.renderHitData = renderhitData;
		}
		
		override public function toString():String
		{
			return "Type : "+type+", DO3D : "+displayObject3D+" Sprite : "+sprite+" Face : "+face3d;
		}
	}
}