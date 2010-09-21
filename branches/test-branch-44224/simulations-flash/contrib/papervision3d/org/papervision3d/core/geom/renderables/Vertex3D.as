/*
 *  PAPER    ON   ERVIS  NPAPER ISION  PE  IS ON  PERVI IO  APER  SI  PA
 *  AP  VI  ONPA  RV  IO PA     SI  PA ER  SI NP PE     ON AP  VI ION AP
 *  PERVI  ON  PE VISIO  APER   IONPA  RV  IO PA  RVIS  NP PE  IS ONPAPE
 *  ER     NPAPER IS     PE     ON  PE  ISIO  AP     IO PA ER  SI NP PER
 *  RV     PA  RV SI     ERVISI NP  ER   IO   PE VISIO  AP  VISI  PA  RV3D
 *  ______________________________________________________________________
 *  papervision3d.org + blog.papervision3d.org + osflash.org/papervision3d
 */

/*
 * Copyright 2006 (c) Carlos Ulloa Matesanz, noventaynueve.com.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

// ______________________________________________________________________
//                                                               Vertex3D
package org.papervision3d.core.geom.renderables
{
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.core.render.command.IRenderListItem;
	

	/**
	* The Vertex3D constructor lets you create 3D vertices.
	*/
	public class Vertex3D extends AbstractRenderable implements IRenderable
	{
		/**
		* An Number that sets the X coordinate of a object relative to the scene coordinate system.
		*/
		public var x :Number;
	
		/**
		* An Number that sets the Y coordinate of a object relative to the scene coordinates.
		*/
		public var y :Number;
	
		/**
		* An Number that sets the Z coordinate of a object relative to the scene coordinates.
		*/
		public var z :Number;
	
		/**
		* An object that contains user defined properties.
		*/
		public var extra :Object;
		
		/**
		 * Used for removing duplicates in clipping procedures
		 */
		public var timestamp:Number;
		
		/**
		 * Vertex2D instance 
		 */
		public var vertex3DInstance:Vertex3DInstance;
		
		//To be docced
		public var normal:Number3D;
		public var connectedFaces:Dictionary;
		
		private var persp:Number=0;
		
		protected var position:Number3D = new Number3D();
	
		/**
		* Creates a new Vertex3D object whose three-dimensional values are specified by the x, y and z parameters.
		*
		* @param	x	The horizontal coordinate value. The default value is zero.
		* @param	y	The vertical coordinate value. The default value is zero.
		* @param	z	The depth coordinate value. The default value is zero.
		*
		* */
		public function Vertex3D( x:Number=0, y:Number=0, z:Number=0 )
		{
			this.x = position.x = x;
			this.y = position.y = y;
			this.z = position.z = z;
			
			this.vertex3DInstance = new Vertex3DInstance();
			this.normal = new Number3D();
			this.connectedFaces = new Dictionary();
		}
		
		public function getPosition():Number3D
		{
			position.x = x;
			position.y = y;
			position.z = z;
			return position;
		}
		
		public function toNumber3D():Number3D
		{
			return new Number3D(x,y,z);
		}
		
		public function clone():Vertex3D
		{
			var clone:Vertex3D = new Vertex3D(x,y,z);
			clone.extra = extra;
			clone.vertex3DInstance = vertex3DInstance.clone();
			clone.normal = normal.clone();
			return clone;
		}
		
		public function calculateNormal():void
		{
			var face:Triangle3D;
			var count:Number = 0;
			normal.reset();
			for each(face in connectedFaces)
			{	
				
				if(face.faceNormal){
					count++;
					normal.plusEq(face.faceNormal);
				}
			}
			//normal.x/=count;
			//normal.y/=count;
			//normal.z/=count;
			var p:Number3D = getPosition();
			p.x /= count;
			p.y /= count;
			p.z /=count;
			p.normalize();
			normal.plusEq(p);
			normal.normalize();
		}
		
		override public function getRenderListItem():IRenderListItem
		{
			return null;
		}
		
		public static function weighted(a:Vertex3D, b:Vertex3D, aw:Number, bw:Number):Vertex3D
        {                
            var d:Number = aw + bw;
            var ak:Number = aw / d;
            var bk:Number = bw / d;
            return new Vertex3D(a.x*ak+b.x*bk, a.y*ak + b.y*bk, a.z*ak + b.z*bk);
        }
        
        public function perspective(focus:Number):Vertex3DInstance
        {
            persp = 1 / (1 + z / focus);

            return new Vertex3DInstance(x * persp, y * persp, z);
        }
		
	}
}