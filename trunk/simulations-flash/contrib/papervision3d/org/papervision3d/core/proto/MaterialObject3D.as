package org.papervision3d.core.proto
{
	import org.papervision3d.core.render.command.RenderTriangle;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.draw.ITriangleDrawer;
	import org.papervision3d.core.render.material.MaterialManager;
	import org.papervision3d.materials.WireframeMaterial;
	import org.papervision3d.objects.DisplayObject3D;
	
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.events.EventDispatcher;
	import flash.geom.Matrix;
	import flash.utils.Dictionary;	

	/**
	* The MaterialObject3D class is the base class for all materials.
	* <p/>
	* Materials collects data about how objects appear when rendered.
	* <p/>
	* A material is data that you assign to objects or faces, so that they appear a certain way when rendered. Materials affect the line and fill colors.
	* <p/>
	* Materials create greater realism in a scene. A material describes how an object reflects or transmits light.
	* <p/>
	* You assign materials to individual objects or a selection of faces; a single object can contain different materials.
	* <p/>
	* MaterialObject3D is an abstract base class; therefore, you cannot call MaterialObject3D directly.
	*/
	public class MaterialObject3D extends EventDispatcher implements ITriangleDrawer
	{
		static private var _totalMaterialObjects :Number = 0;

		/**
		* A transparent or opaque BitmapData texture.
		*/
		public var bitmap :BitmapData;

		/**
		* A Boolean value that determines whether the BitmapData texture is smoothed when rendered.
		*/
		public var smooth :Boolean = false;

		/**
		* A Boolean value that determines whether the texture is tiled when rendered. Defaults to false.
		*/
		public var tiled :Boolean = false;

		/**
		* A Boolean value that determines whether the texture is cached, i.e. not updated before being rendered. Defaults to false.
		*/
		public var baked :Boolean = false;

		/**
		* A RGB color value to draw the faces outline.
		*/
		public var lineColor :Number = DEFAULT_COLOR;

		/**
		* An 8-bit alpha value for the faces outline. If zero, no outline is drawn.
		*/
		public var lineAlpha :Number = 0;
		
		/**
		* An value for the thickness of the faces line.
		*/
		public var lineThickness:Number = 1;

		/**
		* A RGB color value to fill the faces with. Only used if no texture is provided.
		*/
		public var fillColor :Number = DEFAULT_COLOR;

		/**
		* An 8-bit alpha value fill the faces with. If this value is zero and no texture is provided or is undefined, a fill is not created.
		*/
		public var fillAlpha :Number = 0;

		/**
		* A Boolean value that indicates whether the faces are single sided. It has preference over doubleSided.
		*/
		public var oneSide :Boolean = true;

		/**
		* A Boolean value that indicates whether the faces are invisible (not drawn).
		*/
		public var invisible :Boolean = false;

		/**
		* A Boolean value that indicates whether the face is flipped. Only used if doubleSided or not singeSided.
		*/
		public var opposite :Boolean = false;

		/**
		* Color used for DEFAULT material.
		*/
		static public var DEFAULT_COLOR :int = 0x000000;

		/**
		* Color used for DEBUG material.
		*/
		static public var DEBUG_COLOR :int = 0xFF00FF;

		/**
		* The name of the material.
		*/
		public var name :String;

		/**
		* [internal-use] [read-only] Unique id of this instance.
		*/
		public var id :Number;

		/**
		 * Internal use
		 */
		public var maxU :Number;

		/**
		 * Internal use
		 */
		public var maxV :Number;
		 
		 /**
		* Holds the original size of the bitmap before it was resized by Automip mapping
		*/
		 public var widthOffset:Number = 0;
		/**
		* Holds the original size of the bitmap before it was resized by Automip mapping
		*/
		 public var heightOffset:Number = 0;
		
		/**
		 * Defines if this material will be interactive
		 */
		public var interactive:Boolean = false;

		/**
		 * Inventory of registered objects
		 */
		protected var objects:Dictionary;

		/**
		* Creates a new MaterialObject3D object.
		*
		*/
		public function MaterialObject3D()
		{
			this.id = _totalMaterialObjects++;
			MaterialManager.registerMaterial(this);
			objects = new Dictionary(true);
		}

		/**
		* Returns a MaterialObject3D object with the default magenta wireframe values.
		*
		* @return A MaterialObject3D object.
		*/
		static public function get DEFAULT():MaterialObject3D
		{
			var defMaterial :MaterialObject3D = new WireframeMaterial(); //RH, it now returns a wireframe material.
			defMaterial.lineColor   = 0xFFFFFF * Math.random();
			defMaterial.lineAlpha   = 1;
			defMaterial.fillColor   = DEFAULT_COLOR;
			defMaterial.fillAlpha   = 1;
			defMaterial.doubleSided = false;

			return defMaterial;
		}

		static public function get DEBUG():MaterialObject3D
		{
			var defMaterial :MaterialObject3D = new MaterialObject3D();

			defMaterial.lineColor   = 0xFFFFFF * Math.random();
			defMaterial.lineAlpha   = 1;
			defMaterial.fillColor   = DEBUG_COLOR;
			defMaterial.fillAlpha   = 0.37;
			defMaterial.doubleSided = true;

			return defMaterial;
		}
		
		
		/**
		 * Draws the triangle to screen.
		 */
		public function drawTriangle(tri:RenderTriangle, graphics:Graphics, renderSessionData:RenderSessionData, altBitmap:BitmapData = null, altUV:Matrix = null):void
		{
			
		}
		
		public function drawRT(rt:RenderTriangle, graphics:Graphics, renderSessionData:RenderSessionData):void{
			
		}
		
		/**
		* Updates the BitmapData bitmap from the given texture.
		*
		* Draws the current MovieClip image onto bitmap.
		*/
		public function updateBitmap():void {}


		/**
		* Copies the properties of a material.
		*
		* @param	material	Material to copy from.
		*/
		public function copy( material :MaterialObject3D ):void
		{
			this.bitmap    = material.bitmap;
			this.smooth    = material.smooth;

			this.lineColor = material.lineColor;
			this.lineAlpha = material.lineAlpha;
			this.fillColor = material.fillColor;
			this.fillAlpha = material.fillAlpha;
			
			this.oneSide   = material.oneSide;
			this.opposite  = material.opposite;

			this.invisible = material.invisible;
			this.name      = material.name;
			
			this.maxU      = material.maxU;
			this.maxV      = material.maxV;
		}

		/**
		* Creates a copy of the material.
		*
		* @return	A newly created material that contains the same properties.
		*/
		public function clone():MaterialObject3D
		{
			var cloned:MaterialObject3D = new MaterialObject3D();
			cloned.copy(this);
			return cloned;
		}
		
		/**
		 * Registers the <code>DisplayObject3D</code>
		 */
		
		public function registerObject(displayObject3D:DisplayObject3D):void
		{
			objects[displayObject3D] = true;
		}
		
		public function unregisterObject(displayObject3D:DisplayObject3D):void
		{
			if(objects && objects[displayObject3D] ){
				objects[displayObject3D] = null;
			}
		}
		
		public function destroy():void
		{
			objects = null;
			bitmap = null;
			MaterialManager.unRegisterMaterial(this);
		}

		/**
		* Returns a string value representing the material properties.
		*
		* @return	A string.
		*/
		override public function toString():String
		{
			return '[MaterialObject3D] bitmap:' + this.bitmap + ' lineColor:' + this.lineColor + ' fillColor:' + fillColor;
		}
		
		/**
		* A Boolean value that indicates whether the faces are double sided.
		*/
		public function get doubleSided():Boolean
		{
			return ! this.oneSide;
		}

		public function set doubleSided( double:Boolean ):void
		{
			this.oneSide = ! double;
		}

		/**
		* Returns a list of <code>DisplayObject3D</code> objects registered with the material.
		*/
		public function getObjectList():Dictionary
		{
			return objects;
		}
		
		public function isUpdateable():Boolean
		{
			return ! baked;
		}
	}
}