package away3d.core.utils
{
	import flash.display.BitmapData;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.filters.BlurFilter;
	import flash.utils.Dictionary;
	
	/**
	 * Provides static pre-blurred bitmap images for depth of field-effect
	 * when used together with billboarded sprites, such as Billboards.
	 */
	public class DofCache
	{
		private var bitmaps:Array;
		private var levels:Number;
		private var maxBitmap:BitmapData;
		private var minBitmap:BitmapData;

		static public var focus:Number;

        static public var aperture:Number = 22;
        static public var maxblur:Number = 150;
        static public var doflevels:Number = 16;
    	static public var usedof:Boolean = false;
		static private var caches:Dictionary = new Dictionary();
		
		static public function resetDof(enabled:Boolean):void
		{
			usedof = enabled;
			for each(var cache:DofCache in caches)
			{				
				if(!enabled)
				{
					cache = new DofCache(1,cache.bitmaps[0]);											
				}
				else
				{
					cache = new DofCache(doflevels,cache.bitmaps[0]);						
				}
			}
		}
		
		static public function getDofCache(bitmap:BitmapData):DofCache
		{
			if(caches[bitmap] == null)
			{
				if(!usedof)
				{
					caches[bitmap] = new DofCache(1, bitmap);
				}
				else
				{
					caches[bitmap] = new DofCache(doflevels, bitmap);					
				}
			}	
			
			return caches[bitmap];
		}
		
		public function DofCache(levels:Number, texture:BitmapData)
		{			
			this.levels = levels;
			
			var mat:Matrix = new Matrix();
			var pnt:Point = new Point(0,0);
			bitmaps = new Array(levels);
			for(var j:Number = 0;j<levels;++j)
			{
				var tfilter:BlurFilter = new BlurFilter(2+maxblur*j/levels, 2+maxblur*j/levels, 4);
				mat.identity();
				var t:int =(texture.width*(1+j/(levels/2)));
				mat.translate(-texture.width/2, -texture.height/2);
				mat.translate(t/2,t/2);
				var tbmp:BitmapData = new BitmapData(t,t,true,0);
				tbmp.draw(texture, mat);
				tbmp.applyFilter(tbmp, tbmp.rect,pnt,tfilter);
				bitmaps[j] = tbmp;
			}					
			minBitmap = bitmaps[0];
			maxBitmap = bitmaps[bitmaps.length-1];
		}
		
		public function getBitmap(depth:Number):BitmapData
		{
			var t:Number = focus-depth;
			if(focus-depth<0) t = -t;
			t = t / aperture;
			t = Math.floor(t);
			if(t > (levels-1)) return maxBitmap;
			if(t < 0) return minBitmap;
			return bitmaps[t];
		}
	}
}