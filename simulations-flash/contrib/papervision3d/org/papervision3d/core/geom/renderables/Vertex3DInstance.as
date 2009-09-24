/*
 *  PAPER    ON   ERVIS  NPAPER ISION  PE  IS ON  PERVI IO  APER  SI  PA
 *  AP  VI  ONPA  RV  IO PA     SI  PA ER  SI NP PE     ON AP  VI ION AP
 *  PERVI  ON  PE VISIO  APER   IONPA  RV  IO PA  RVIS  NP PE  IS ONPAPE
 *  ER     NPAPER IS     PE     ON  PE  ISIO  AP     IO PA ER  SI NP PER
 *  RV     PA  RV SI     ERVISI NP  ER   IO   PE VISIO  AP  VISI  PA  RV3D
 *  ______________________________________________________________________
 *  papervision3d.org � blog.papervision3d.org � osflash.org/papervision3d
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
	import org.papervision3d.core.math.Number3D;
	

	/**
	* The Vertex3DInstance constructor lets you create 2D projected vertices.
	*/
	public class Vertex3DInstance
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
		* [internal-use] A Boolean value that indicates whether the vertex is visible after projection.
		*
		* If false, it indicates that the vertex is behind the camera plane.
		*
		* */
		public var visible :Boolean;
		
		//To be docced
		public var normal:Number3D;
		
		private var persp:Number = 0;
	
		/**
		* Creates a new Vertex2D object whose three-dimensional values are specified by the x, y and z parameters.
		*
		* @param	x	The horizontal coordinate value. The default value is zero.
		* @param	y	The vertical coordinate value. The default value is zero.
		* @param	z	The depth coordinate value. The default value is zero.
		*
		* */
		public function Vertex3DInstance( x:Number=0, y:Number=0, z:Number=0 )
		{
			this.x = x;
			this.y = y;
			this.z = z;
	
			this.visible   = false;
			this.normal = new Number3D();
		}
		
		public function clone():Vertex3DInstance
		{
			var clone:Vertex3DInstance = new Vertex3DInstance(x,y,z);
			clone.visible = visible;
			clone.extra = extra; //TODO : FIX CLONING OF THE EXTRA OBJECT.
			return clone;
		}
		
		public static function dot(v0:Vertex3DInstance, v1:Vertex3DInstance):Number
		{
			return (v0.x * v1.x + v0.y * v1.y);
		}
		
		public static function cross(v0:Vertex3DInstance,v1:Vertex3DInstance):Number
		{
			return (v0.x*v1.y-v1.x*v0.y);
		}
		
		public static function sub( v0:Vertex3DInstance, v1:Vertex3DInstance ):Vertex3DInstance
		{
			return new Vertex3DInstance
			(
				v1.x - v0.x,
				v1.y - v0.y
			);
		}
		
		public static function subTo(v0:Vertex3DInstance, v1:Vertex3DInstance, o:Vertex3DInstance):void
		{
			o.x = v1.x - v0.x;
			o.y = v1.y - v0.y;
		}
		
		public function deperspective(focus:Number):Vertex3D
        {
           persp = 1 + z / focus;

            return new Vertex3D(x * persp, y * persp, z);
        }
		
		/**
		 * Calculates the squared distance between two screen vertex objects.
		 * 
		 * @param	b	The screen vertex object to use for the calcation.
		 * @return		The squared scalar value of the vector between this and the given scren vertex.
		 */
        public function distanceSqr(b:Vertex3DInstance):Number
        {
            return (x - b.x)*(x - b.x) + (y - b.y)*(y - b.y);
        }
		
		/**
		 * Calculates the distance between two screen vertex objects.
		 * 
		 * @param	b	The second screen vertex object to use for the calcation.
		 * @return		The scalar value of the vector between this and the given screen vertex.
		 */
        public function distance(b:Vertex3DInstance):Number
        {
            return Math.sqrt((x - b.x)*(x - b.x) + (y - b.y)*(y - b.y));
        }
        
         public static function median(a:Vertex3DInstance, b:Vertex3DInstance, focus:Number):Vertex3DInstance
        {
            var mz:Number = (a.z + b.z) / 2;

            var faz:Number = focus + a.z;
            var fbz:Number = focus + b.z;
            var ifmz:Number = 1 / (focus + mz) / 2;

            return new Vertex3DInstance((a.x*faz + b.x*fbz)*ifmz, (a.y*faz + b.y*fbz)*ifmz, mz);
        }
		
	}
}