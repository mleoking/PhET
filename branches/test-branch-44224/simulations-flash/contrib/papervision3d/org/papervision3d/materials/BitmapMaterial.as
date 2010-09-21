package org.papervision3d.materials {
	import org.papervision3d.Papervision3D;	import org.papervision3d.core.geom.renderables.Triangle3D;	import org.papervision3d.core.geom.renderables.Vertex3DInstance;	import org.papervision3d.core.log.PaperLogger;	import org.papervision3d.core.material.TriangleMaterial;	import org.papervision3d.core.proto.MaterialObject3D;	import org.papervision3d.core.render.command.RenderTriangle;	import org.papervision3d.core.render.data.RenderSessionData;	import org.papervision3d.core.render.draw.ITriangleDrawer;	import org.papervision3d.materials.utils.PrecisionMode;	import org.papervision3d.materials.utils.RenderRecStorage;		import flash.display.BitmapData;	import flash.display.Graphics;	import flash.geom.Matrix;	import flash.geom.Point;	import flash.geom.Rectangle;	import flash.utils.Dictionary;	
	/**
	* The BitmapMaterial class creates a texture from a BitmapData object.
	*
	* Materials collect data about how objects appear when rendered.
	*
	*/
	public class BitmapMaterial extends TriangleMaterial implements ITriangleDrawer
	{
		
		protected static const DEFAULT_FOCUS:Number = 200;
		protected static var hitRect:Rectangle = new Rectangle();
		
		
		protected var renderRecStorage:Array;
		protected var focus:Number = 200;
		protected var _precise:Boolean;
		protected var _precision:int = 8;
		protected var _perPixelPrecision:int = 8;
		public var minimumRenderSize:Number = 4;
		
		protected var _texture :Object;
		
		/**
		 * Indicates if mip mapping is forced.
		 */
		public static var AUTO_MIP_MAPPING :Boolean = false;

		/**
		 * Levels of mip mapping to force.
		 */
		public static var MIP_MAP_DEPTH :Number = 8;
		/**		 * Precision mode indicates how triangles are created for precise texture render.		 */		public var precisionMode:int = PrecisionMode.ORIGINAL;		
		public var uvMatrices:Dictionary = new Dictionary();
		
		/**
		* @private
		*/
		protected static var _triMatrix:Matrix = new Matrix();
		protected static var _triMap:Matrix;
		
		/**
		* @private
		*/
		protected static var _localMatrix:Matrix = new Matrix();
		
		/**
		* The BitmapMaterial class creates a texture from a BitmapData object.
		*
		* @param	asset				A BitmapData object.
		*/
		public function BitmapMaterial( asset:BitmapData=null, precise:Boolean = false)
		{
			// texture calls createBitmap. That's where all the init happens. This allows to reinit when changing texture. -C4RL05
			// if we have an asset passed in, this means we're the subclass, not the super.  Set the texture, let the fun begin.
			if( asset ) texture = asset;
			this.precise = precise;
			createRenderRecStorage();
		}
		
		protected function createRenderRecStorage():void
		{
			this.renderRecStorage = new Array();
			for(var a:int = 0; a<=100; a++){
				this.renderRecStorage[a] = new RenderRecStorage();
			}	
		}
		
		/**
		* Resets the mapping coordinates. Use when the texture has been resized.
		*/
		public function resetMapping():void
		{
			uvMatrices = new Dictionary();
		}
		
			
		//Local storage. Avoid var's in high usage functions.
		private var x0:Number;
		private var y0:Number;
		private var x1:Number;
		private var y1:Number;
		private var x2:Number;
		private var y2:Number;
		/**
		 *  drawTriangle
		 */
		override public function drawTriangle(tri:RenderTriangle, graphics:Graphics, renderSessionData:RenderSessionData, altBitmap:BitmapData = null, altUV:Matrix = null):void
		{
		//	trace("at drawing triangle???");
			_triMap = altUV ? altUV : (uvMatrices[tri] || transformUVRT(tri));
			if(!_precise || !_triMap){
				if( lineAlpha )
					graphics.lineStyle( lineThickness, lineColor, lineAlpha );
				if( bitmap )
				{
					
					x0 = tri.v0.x;
					y0 = tri.v0.y;
					x1 = tri.v1.x;
					y1 = tri.v1.y;
					x2 = tri.v2.x;
					y2 = tri.v2.y;
	
					_triMatrix.a = x1 - x0;
					_triMatrix.b = y1 - y0;
					_triMatrix.c = x2 - x0;
					_triMatrix.d = y2 - y0;
					_triMatrix.tx = x0;
					_triMatrix.ty = y0;
						
					_localMatrix.a = _triMap.a;
					_localMatrix.b = _triMap.b;
					_localMatrix.c = _triMap.c;
					_localMatrix.d = _triMap.d;
					_localMatrix.tx = _triMap.tx;
					_localMatrix.ty = _triMap.ty;
					_localMatrix.concat(_triMatrix);
					
					graphics.beginBitmapFill( altBitmap ? altBitmap : bitmap, _localMatrix, tiled, smooth);
				}
				graphics.moveTo( x0, y0 );
				graphics.lineTo( x1, y1 );
				graphics.lineTo( x2, y2 );
				graphics.lineTo( x0, y0 );
				if( bitmap )
					graphics.endFill();
				if( lineAlpha )
					graphics.lineStyle();
				renderSessionData.renderStatistics.triangles++;
			}else{
				if(bitmap){
					focus = renderSessionData.camera.focus;
					tempPreBmp = altBitmap ? altBitmap : bitmap;
					tempPreRSD = renderSessionData;
					tempPreGrp = graphics;
					cullRect = renderSessionData.viewPort.cullingRectangle;
					renderRec(_triMap, tri.v0, tri.v1, tri.v2, 0);	 
				}
			}
		}
		
		/**
		* Applies the updated UV texture mapping values to the triangle. This is required to speed up rendering.
		*
		*/
		public function transformUV(face3D:Triangle3D):Matrix
		{			
			if( ! face3D.uv )
			{
				PaperLogger.error( "MaterialObject3D: transformUV() uv not found!" );
			}
			else if( bitmap )
			{
				var uv :Array  = face3D.uv;
				
				var w  :Number = bitmap.width * maxU;
				var h  :Number = bitmap.height * maxV;
				var u0 :Number = w * face3D.uv0.u;
				var v0 :Number = h * ( 1 - face3D.uv0.v );
				var u1 :Number = w * face3D.uv1.u;
				var v1 :Number = h * ( 1 - face3D.uv1.v);
				var u2 :Number = w * face3D.uv2.u;
				var v2 :Number = h * ( 1 - face3D.uv2.v );
				
				// Fix perpendicular projections
				if( (u0 == u1 && v0 == v1) || (u0 == u2 && v0 == v2) )
				{
					u0 -= (u0 > 0.05)? 0.05 : -0.05;
					v0 -= (v0 > 0.07)? 0.07 : -0.07;
				}
				
				if( u2 == u1 && v2 == v1 )
				{
					u2 -= (u2 > 0.05)? 0.04 : -0.04;
					v2 -= (v2 > 0.06)? 0.06 : -0.06;
				}
				
				// Precalculate matrix & correct for mip mapping
				var at :Number = ( u1 - u0 );
				var bt :Number = ( v1 - v0 );
				var ct :Number = ( u2 - u0 );
				var dt :Number = ( v2 - v0 );
				
				var m :Matrix = new Matrix( at, bt, ct, dt, u0, v0 );
				// Need to mirror over X-axis when righthanded
				if(Papervision3D.useRIGHTHANDED)
				{
					m.scale(-1, 1);
					m.translate(w, 0);
				}
				m.invert();
				
				var mapping:Matrix = uvMatrices[face3D] = m.clone();
				mapping.a  = m.a;
				mapping.b  = m.b;
				mapping.c  = m.c;
				mapping.d  = m.d;
				mapping.tx = m.tx;
				mapping.ty = m.ty;
			}
			else PaperLogger.error( "MaterialObject3D: transformUV() material.bitmap not found!" );

			return mapping;
		}
		
		/**
		* Applies the updated UV texture mapping values to the triangle. This is required to speed up rendering.
		*
		*/
		public function transformUVRT(tri:RenderTriangle):Matrix
		{			
			if( bitmap )
			{
				//var uv :Array  = face3D.uv;
				
				var w  :Number = bitmap.width * maxU;
				var h  :Number = bitmap.height * maxV;
				var u0 :Number = w * tri.uv0.u;
				var v0 :Number = h * ( 1 - tri.uv0.v );
				var u1 :Number = w * tri.uv1.u;
				var v1 :Number = h * ( 1 - tri.uv1.v);
				var u2 :Number = w * tri.uv2.u;
				var v2 :Number = h * ( 1 - tri.uv2.v );
				
				// Fix perpendicular projections
				if( (u0 == u1 && v0 == v1) || (u0 == u2 && v0 == v2) )
				{
					u0 -= (u0 > 0.05)? 0.05 : -0.05;
					v0 -= (v0 > 0.07)? 0.07 : -0.07;
				}
				
				if( u2 == u1 && v2 == v1 )
				{
					u2 -= (u2 > 0.05)? 0.04 : -0.04;
					v2 -= (v2 > 0.06)? 0.06 : -0.06;
				}
				
				// Precalculate matrix & correct for mip mapping
				var at :Number = ( u1 - u0 );
				var bt :Number = ( v1 - v0 );
				var ct :Number = ( u2 - u0 );
				var dt :Number = ( v2 - v0 );
				
				var m :Matrix = new Matrix( at, bt, ct, dt, u0, v0 );
				// Need to mirror over X-axis when righthanded
				if(Papervision3D.useRIGHTHANDED)
				{
					m.scale(-1, 1);
					m.translate(w, 0);
				}
				m.invert();
				
				var mapping:Matrix = uvMatrices[tri] = m.clone();
				mapping.a  = m.a;
				mapping.b  = m.b;
				mapping.c  = m.c;
				mapping.d  = m.d;
				mapping.tx = m.tx;
				mapping.ty = m.ty;
			}
			else PaperLogger.error( "MaterialObject3D: transformUV() material.bitmap not found!" );

			return mapping;
		}
		
		
		protected var ax:Number;
		protected var ay:Number;
		protected var az:Number;
		protected var bx:Number;
		protected var by:Number;
		protected var bz:Number;
		protected var cx:Number;
		protected var cy:Number;
		protected var cz:Number;
		protected var faz:Number;
        protected var fbz:Number;
        protected var fcz:Number;
       	protected var mabz:Number;
        protected var mbcz:Number;
        protected var mcaz:Number;
        protected var mabx:Number;
        protected var maby:Number;
        protected var mbcx:Number;
        protected var mbcy:Number;
        protected var mcax:Number;
        protected var mcay:Number;
        protected var dabx:Number;
        protected var daby:Number;
        protected var dbcx:Number;
        protected var dbcy:Number;
        protected var dcax:Number;
        protected var dcay:Number;
        protected var dsab:Number;
        protected var dsbc:Number;
        protected var dsca:Number;
        protected var dmax:Number;
        protected var cullRect:Rectangle;
        
        protected var tempPreGrp:Graphics;
        protected var tempPreBmp:BitmapData;
        protected var tempPreRSD:RenderSessionData;		protected var tempTriangleMatrix:Matrix = new Matrix();
		private var a2:Number;
		private var b2:Number;
		private var c2:Number;
		private var d2:Number;

		private var dx:Number, dy:Number, d2ab:Number, d2bc:Number, d2ca:Number;
        
        protected function renderRec(emMap:Matrix, v0:Vertex3DInstance, v1:Vertex3DInstance, v2:Vertex3DInstance, index:Number):void
        {
        	az = v0.z;
        	bz = v1.z;
        	cz = v2.z;
        	
        	//Cull if a vertex behind near.
            if((az <= 0) && (bz <= 0) && (cz <= 0))
                return;
        	
        	cx = v2.x;
        	cy = v2.y;
        	bx = v1.x;
        	by = v1.y;
        	ax = v0.x;
        	ay = v0.y;
        	
        	//Cull if outside of viewport.
        	if(cullRect){
	    		hitRect.x = (bx < ax ? (bx < cx ? bx : cx) : (ax < cx ? ax : cx ));
				hitRect.width = (bx > ax ? (bx > cx ? bx : cx) : (ax > cx ? ax : cx )) + (hitRect.x < 0 ? -hitRect.x : hitRect.x);
				hitRect.y = (by < ay ? (by < cy ? by : cy) : (ay < cy ? ay : cy ));
				hitRect.height = (by > ay ? (by > cy ? by : cy) : (ay > cy ? ay : cy )) + (hitRect.y < 0 ? -hitRect.y : hitRect.y);
				if(!((hitRect.right<cullRect.left)||(hitRect.left>cullRect.right))){
					if(!((hitRect.bottom<cullRect.top)||(hitRect.top>cullRect.bottom))){
					
					}else{
						return;
					}
				}else{
					return;
				}
        	}
			
			//cull if max iterations is reached, focus is invalid or if tesselation is to small.
            if (index >= 100 || (hitRect.width < minimumRenderSize) || (hitRect.height < minimumRenderSize) || (focus == Infinity))
            {
            	
            	//Draw this triangle.
            	a2 = v1.x - v0.x;
            	b2 = v1.y - v0.y;
            	c2 = v2.x - v0.x;
            	d2 = v2.y - v0.y;
                      	
            	tempTriangleMatrix.a = emMap.a*a2 + emMap.b*c2;
            	tempTriangleMatrix.b = emMap.a*b2 + emMap.b*d2;
            	tempTriangleMatrix.c = emMap.c*a2 + emMap.d*c2;
            	tempTriangleMatrix.d = emMap.c*b2 + emMap.d*d2;
            	tempTriangleMatrix.tx = emMap.tx*a2 + emMap.ty*c2 + v0.x;   
            	tempTriangleMatrix.ty = emMap.tx*b2 + emMap.ty*d2 + v0.y;       
           		
           		if(lineAlpha){
           			tempPreGrp.lineStyle( lineThickness, lineColor, lineAlpha );
           		}
				tempPreGrp.beginBitmapFill(tempPreBmp, tempTriangleMatrix, tiled, smooth);
            	tempPreGrp.moveTo(v0.x, v0.y);
            	tempPreGrp.lineTo(v1.x, v1.y);
            	tempPreGrp.lineTo(v2.x, v2.y);
            	tempPreGrp.endFill();
            	if(lineAlpha){
           			tempPreGrp.lineStyle();
           		}
                
                tempPreRSD.renderStatistics.triangles++;
                return;
            }
			
            faz = focus + az;
            fbz = focus + bz;
            fcz = focus + cz;
			mabz = 2 / (faz + fbz);
            mbcz = 2 / (fbz + fcz);
            mcaz = 2 / (fcz + faz);
            mabx = (ax*faz + bx*fbz)*mabz;
            maby = (ay*faz + by*fbz)*mabz;
            mbcx = (bx*fbz + cx*fcz)*mbcz;
            mbcy = (by*fbz + cy*fcz)*mbcz;
            mcax = (cx*fcz + ax*faz)*mcaz;
            mcay = (cy*fcz + ay*faz)*mcaz;
            dabx = ax + bx - mabx;
            daby = ay + by - maby;
            dbcx = bx + cx - mbcx;
            dbcy = by + cy - mbcy;
            dcax = cx + ax - mcax;
            dcay = cy + ay - mcay;
            dsab = (dabx*dabx + daby*daby);
            dsbc = (dbcx*dbcx + dbcy*dbcy);
            dsca = (dcax*dcax + dcay*dcay);
			
			var nIndex:int = index+1;
			var nRss:RenderRecStorage = RenderRecStorage(renderRecStorage[int(index)]);
			var renderRecMap:Matrix = nRss.mat;
			
            if ((dsab <= _precision) && (dsca <= _precision) && (dsbc <= _precision)){
               //Draw this triangle.
               a2 = v1.x - v0.x;
               b2 = v1.y - v0.y;
               c2 = v2.x - v0.x;
               d2 = v2.y - v0.y;
                      	
            	tempTriangleMatrix.a = emMap.a*a2 + emMap.b*c2;
            	tempTriangleMatrix.b = emMap.a*b2 + emMap.b*d2;
            	tempTriangleMatrix.c = emMap.c*a2 + emMap.d*c2;
            	tempTriangleMatrix.d = emMap.c*b2 + emMap.d*d2;
            	tempTriangleMatrix.tx = emMap.tx*a2 + emMap.ty*c2 + v0.x;   
            	tempTriangleMatrix.ty = emMap.tx*b2 + emMap.ty*d2 + v0.y;       
           		
           		if(lineAlpha){
           			tempPreGrp.lineStyle( lineThickness, lineColor, lineAlpha );
           		}
				tempPreGrp.beginBitmapFill(tempPreBmp, tempTriangleMatrix, tiled, smooth);
            	tempPreGrp.moveTo(v0.x, v0.y);
            	tempPreGrp.lineTo(v1.x, v1.y);
            	tempPreGrp.lineTo(v2.x, v2.y);
            	tempPreGrp.endFill();
               	if(lineAlpha){
               		tempPreGrp.lineStyle();
               	}
               
               
               tempPreRSD.renderStatistics.triangles++;
               return;
            }
            
            if ((dsab > _precision) && (dsca > _precision) && (dsbc > _precision)){
            	renderRecMap.a = emMap.a*2;
            	renderRecMap.b = emMap.b*2;
            	renderRecMap.c = emMap.c*2;
            	renderRecMap.d = emMap.d*2;
            	renderRecMap.tx = emMap.tx*2;
            	renderRecMap.ty = emMap.ty*2;
            	    	
          		nRss.v0.x = mabx * 0.5;
          		nRss.v0.y = maby * 0.5;
          		nRss.v0.z = (az+bz) * 0.5;
          		
          		nRss.v1.x = mbcx * 0.5;
            	nRss.v1.y = mbcy * 0.5;
            	nRss.v1.z = (bz+cz) * 0.5;
          		
          		nRss.v2.x = mcax * 0.5;
          		nRss.v2.y = mcay * 0.5;
          		nRss.v2.z = (cz+az) * 0.5;
                renderRec(renderRecMap, v0, nRss.v0, nRss.v2, nIndex);
				
				renderRecMap.tx -=1;
                renderRec(renderRecMap, nRss.v0, v1, nRss.v1, nIndex);
				
				renderRecMap.ty -=1;
				renderRecMap.tx = emMap.tx*2;
                renderRec(renderRecMap, nRss.v2, nRss.v1, v2, nIndex);
				
				renderRecMap.a = -emMap.a*2;
				renderRecMap.b = -emMap.b*2;
				renderRecMap.c = -emMap.c*2;
				renderRecMap.d = -emMap.d*2;
				renderRecMap.tx = -emMap.tx*2+1;
				renderRecMap.ty = -emMap.ty*2+1;
                renderRec(renderRecMap, nRss.v1, nRss.v2, nRss.v0, nIndex);

                return;
            }

			if( precisionMode == PrecisionMode.ORIGINAL )
			{
				d2ab = dsab;
				d2bc = dsbc;
				d2ca = dsca;
				dmax = (dsca > dsbc ? (dsca > dsab ? dsca : dsab) : (dsbc > dsab ? dsbc : dsab ));
			}
			else
			{
				// Calculate best tessellation edge
				dx = v0.x - v1.x;
				dy = v0.y - v1.y;
				d2ab = dx * dx + dy * dy;
				
				dx = v1.x - v2.x;
				dy = v1.y - v2.y;
				d2bc = dx * dx + dy * dy;
				
				dx = v2.x - v0.x;
				dy = v2.y - v0.y;
				d2ca = dx * dx + dy * dy;
			
				dmax = (d2ca > d2bc ? (d2ca > d2ab ? d2ca : d2ab) : (d2bc > d2ab ? d2bc : d2ab ));		// dmax = Math.max( d2ab, d2bc, d2ac );
			}

			// Break triangle along edge
            if (d2ab == dmax)
            {
            	renderRecMap.a = emMap.a*2;
				renderRecMap.b = emMap.b;
				renderRecMap.c = emMap.c*2;
				renderRecMap.d = emMap.d;
				renderRecMap.tx = emMap.tx*2;
				renderRecMap.ty = emMap.ty;
				nRss.v0.x = mabx * 0.5;
				nRss.v0.y = maby * 0.5;
				nRss.v0.z = (az+bz) * 0.5;
                renderRec(renderRecMap, v0, nRss.v0, v2, nIndex);
				
				renderRecMap.a = emMap.a*2+emMap.b;
				renderRecMap.c = 2*emMap.c+emMap.d;
				renderRecMap.tx = emMap.tx*2+emMap.ty-1;
                renderRec(renderRecMap, nRss.v0, v1, v2, nIndex);
            
                return;
            }

            if (d2ca == dmax){
            	
            	renderRecMap.a = emMap.a;
				renderRecMap.b = emMap.b*2;
				renderRecMap.c = emMap.c;
				renderRecMap.d = emMap.d*2;
				renderRecMap.tx = emMap.tx;
				renderRecMap.ty = emMap.ty*2;
				nRss.v2.x = mcax * 0.5;
				nRss.v2.y = mcay * 0.5;
				nRss.v2.z = (cz+az) * 0.5;
                renderRec(renderRecMap, v0, v1, nRss.v2, nIndex);
				
				renderRecMap.b += emMap.a;
				renderRecMap.d += emMap.c;
				renderRecMap.ty += emMap.tx-1;
                renderRec(renderRecMap, nRss.v2, v1, v2, nIndex);
            	
                return;
            }
            renderRecMap.a = emMap.a-emMap.b;
			renderRecMap.b = emMap.b*2;
			renderRecMap.c = emMap.c-emMap.d;
			renderRecMap.d = emMap.d*2;
			renderRecMap.tx = emMap.tx-emMap.ty;
			renderRecMap.ty = emMap.ty*2;
			
			nRss.v1.x = mbcx * 0.5;
			nRss.v1.y = mbcy * 0.5;
			nRss.v1.z = (bz+cz)*0.5;
            renderRec(renderRecMap, v0, v1, nRss.v1, nIndex);
			
			renderRecMap.a = emMap.a*2;
			renderRecMap.b = emMap.b-emMap.a;
			renderRecMap.c = emMap.c*2;
			renderRecMap.d = emMap.d-emMap.c;
			renderRecMap.tx = emMap.tx*2;
			renderRecMap.ty = emMap.ty-emMap.tx;
            renderRec(renderRecMap, v0, nRss.v1, v2, nIndex);
        }

		/**
		* Returns a string value representing the material properties in the specified BitmapMaterial object.
		*
		* @return	A string.
		*/
		public override function toString(): String
		{
			return 'Texture:' + this.texture + ' lineColor:' + this.lineColor + ' lineAlpha:' + this.lineAlpha;
		}


		// ______________________________________________________________________ CREATE BITMAP

		protected function createBitmap( asset:BitmapData ):BitmapData
		{		
			resetMapping();

			var bm:BitmapData;
			
			if( AUTO_MIP_MAPPING )
			{
				bm = correctBitmap( asset );
			}
			else
			{
				this.maxU = this.maxV = 1;

				bm = asset;
			}
			
			return bm;
		}


		// ______________________________________________________________________ CORRECT BITMAP FOR MIP MAPPING

		protected function correctBitmap( bitmap :BitmapData ):BitmapData
		{
			var okBitmap :BitmapData;

			var levels :Number = 1 << MIP_MAP_DEPTH;
			// this is faster than Math.ceil
			var bWidth :Number = bitmap.width  / levels;
			bWidth = bWidth == uint(bWidth) ? bWidth : uint(bWidth)+1;
			var bHeight :Number = bitmap.height  / levels;
			bHeight = bHeight == uint(bHeight) ? bHeight : uint(bHeight)+1;
			
			var width  :Number = levels * bWidth;
			var height :Number = levels * bHeight;

			// Check for BitmapData maximum size
			var ok:Boolean = true;

			if( width  > 2880 )
			{
				width  = bitmap.width;
				ok = false;
			}

			if( height > 2880 )
			{
				height = bitmap.height;
				ok = false;
			}
			
			if( ! ok ) PaperLogger.warning( "Material " + this.name + ": Texture too big for mip mapping. Resizing recommended for better performance and quality." );

			// Create new bitmap?
			if( bitmap && ( bitmap.width % levels !=0  ||  bitmap.height % levels != 0 ) )
			{
				okBitmap = new BitmapData( width, height, bitmap.transparent, 0x00000000 );

					
				// this is for ISM and offsetting bitmaps that have been resized
				widthOffset = bitmap.width;
				heightOffset = bitmap.height;
				
				this.maxU = bitmap.width / width;
				this.maxV = bitmap.height / height;

				okBitmap.draw( bitmap );

				// PLEASE DO NOT REMOVE
				extendBitmapEdges( okBitmap, bitmap.width, bitmap.height );
			}
			else
			{
				this.maxU = this.maxV = 1;

				okBitmap = bitmap;
			}

			return okBitmap;
		}

		protected function extendBitmapEdges( bmp:BitmapData, originalWidth:Number, originalHeight:Number ):void
		{
			var srcRect  :Rectangle = new Rectangle();
			var dstPoint :Point = new Point();
			
			var i        :int;

			// Check width
			if( bmp.width > originalWidth )
			{
				// Extend width
				srcRect.x      = originalWidth-1;
				srcRect.y      = 0;
				srcRect.width  = 1;
				srcRect.height = originalHeight;
				dstPoint.y     = 0;
				
				for( i = originalWidth; i < bmp.width; i++ )
				{
					dstPoint.x = i;
					bmp.copyPixels( bmp, srcRect, dstPoint );
				}
			}

			// Check height
			if( bmp.height > originalHeight )
			{
				// Extend height
				srcRect.x      = 0;
				srcRect.y      = originalHeight-1;
				srcRect.width  = bmp.width;
				srcRect.height = 1;
				dstPoint.x     = 0;

				for( i = originalHeight; i < bmp.height; i++ )
				{
					dstPoint.y = i;
					bmp.copyPixels( bmp, srcRect, dstPoint );
				}
			}
		}

		// ______________________________________________________________________
		
		
		/**
		 * resetUVMatrices();
		 * 
		 * Resets the precalculated uvmatrices, so they can be recalculated
		 */
		 public function resetUVS():void
		 {
		 	uvMatrices = new Dictionary(false);
		 }
		
		/**
		* Copies the properties of a material.
		*
		* @param	material	Material to copy from.
		*/
		override public function copy( material :MaterialObject3D ):void
		{
			super.copy( material );

			this.maxU = material.maxU;
			this.maxV = material.maxV;
		}

		/**
		* Creates a copy of the material.
		*
		* @return	A newly created material that contains the same properties.
		*/
		override public function clone():MaterialObject3D
		{
			var cloned:MaterialObject3D = super.clone();

			cloned.maxU = this.maxU;
			cloned.maxV = this.maxV;

			return cloned;
		}
		
		/**
		 * Sets the material's precise rendering mode. If set to true, material will adaptively render triangles to conquer texture distortion. 
		 */
		public function set precise(boolean:Boolean):void
		{
			_precise = boolean;
		}
		
		public function get precise():Boolean
		{
			return _precise;
		}
		
		/**
		 * If the material is rendering with @see precise to true, this sets tesselation per pixel ratio.
		 */
		public function set precision(precision:int):void
		{
			_precision = precision;
		}
		
		public function get precision():int
		{
			return _precision;
		}
		
		/**
		 * If the material is rendering with @see precise to true, this sets tesselation per pixel ratio.
		 * 
		 * corrected to set per pixel precision exactly.
		 */
		public function set pixelPrecision(precision:int):void
		{
			_precision = precision*precision*1.4;
			_perPixelPrecision = precision;
		}
		
		public function get pixelPrecision():int
		{
			return _perPixelPrecision;
		}
		
		/**
		* A texture object.
		*/		
		public function get texture():Object
		{
			return this._texture;
		}
		
		/**
		* @private
		*/
		public function set texture( asset:Object ):void
		{
			if( asset is BitmapData == false )
			{
				PaperLogger.error("BitmapMaterial.texture requires a BitmapData object for the texture");
				return;
			}
			
			bitmap   = createBitmap( BitmapData(asset) );
			
			_texture = asset;
		}
		
		override public function destroy():void
		{
			super.destroy();
			if(uvMatrices){
				uvMatrices = null;
			}
			if(bitmap){
				bitmap.dispose();
			}
			this.renderRecStorage = null;
		}
			
	}
}