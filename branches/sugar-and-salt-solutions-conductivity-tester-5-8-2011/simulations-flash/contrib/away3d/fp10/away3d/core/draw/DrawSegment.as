package away3d.core.draw
{
	import away3d.arcane;
	import away3d.core.utils.*;
	import away3d.materials.*;

	use namespace arcane;
	
    /**
    * Line segment drawing primitive
    */
    public class DrawSegment extends DrawPrimitive
    {
		/** @private */
		arcane function onepointcut(v01x:Number, v01y:Number, v01z:Number):Array
		{
			var index0:int = screenIndices.length;
        	screenIndices[screenIndices.length] = startIndex;
        	screenIndices[screenIndices.length] = screenVertices.length;
        	var index1:int = screenIndices.length;
        	screenIndices[screenIndices.length] = screenVertices.length;
        	screenIndices[screenIndices.length] = startIndex+1;
        	var index2:int = screenIndices.length;
        	
        	screenVertices[screenVertices.length] = v01x;
			screenVertices[screenVertices.length] = v01y;
			screenVertices[screenVertices.length] = v01z;
			
            return [
                create(source, segmentVO, material, screenVertices, screenIndices, screenCommands, index0, index1, true),
                create(source, segmentVO, material, screenVertices, screenIndices, screenCommands, index1, index2, true)
            ];
    	}
    	
    	private var focus:Number;  
        private var ax:Number;
        private var ay:Number;
        private var az:Number;
        private var bx:Number;
        private var by:Number;
        private var bz:Number;
        private var dx:Number;
        private var dy:Number;
        private var azf:Number;
        private var bzf:Number;
        private var faz:Number;
        private var fbz:Number;
        private var xfocus:Number;
        private var yfocus:Number;
        private var axf:Number;
        private var bxf:Number;
        private var ayf:Number;
        private var byf:Number;
        private var det:Number;
        private var db:Number;
        private var da:Number;
        private var _index:int;
        
        private function distanceToCenter(x:Number, y:Number):Number
        {   
            var centerx:Number = (v0x + v1x) / 2;
            var centery:Number = (v0y + v1y) / 2;

            return Math.sqrt((centerx-x)*(centerx-x) + (centery-y)*(centery-y));
        }
        
		/**
		 * The x position of the v0 screenvertex of the segment primitive.
		 */
        public var v0x:Number;
        
		/**
		 * The y position of the v0 screenvertex of the segment primitive.
		 */
        public var v0y:Number;
        
		/**
		 * The z position of the v0 screenvertex of the segment primitive.
		 */
        public var v0z:Number;
        
		/**
		 * The x position of the v1 screenvertex of the segment primitive.
		 */
        public var v1x:Number;
        
		/**
		 * The y position of the v1 screenvertex of the segment primitive.
		 */
        public var v1y:Number;
        
		/**
		 * The z position of the v1 screenvertex of the segment primitive.
		 */
        public var v1z:Number;
		
		/**
		 * The screen length of the segment primitive.
		 */
        public var length:Number;
		
    	/**
    	 * A reference to the segment value object used by the segment primitive.
    	 */
        public var segmentVO:SegmentVO;
        
		/**
		 * The material of the segment primitive.
		 */
        public var material:ISegmentMaterial;
        
        public var screenVertices:Array;
        
        public var screenIndices:Array;
        
        public var screenCommands:Array;
        
        public var startIndex:int;
        
        public var endIndex:int;
        
		/**
		 * @inheritDoc
		 */
        public override function clear():void
        {
            //v0 = null;
            //v1 = null;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function render():void
        {
            material.renderSegment(this);
        }
        
		/**
		 * @inheritDoc
		 */
        public override function contains(x:Number, y:Number):Boolean
        {
            if (Math.abs(v0x*(y - v1y) + v1x*(v0y - y) + x*(v1y - v0y)) > 0.001*1000*1000)
                return false;

            if (distanceToCenter(x, y)*2 > length)
                return false;

            return true;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function getZ(x:Number, y:Number):Number
        {
            focus = view.camera.focus;
              
            ax = v0x;
            ay = v0y;
            az = v0z;
            bx = v1x;
            by = v1y;
            bz = v1z;

            if ((ax == x) && (ay == y))
                return az;

            if ((bx == x) && (by == y))
                return bz;

            dx = bx - ax;
            dy = by - ay;

            azf = az / focus;
            bzf = bz / focus;

            faz = 1 + azf;
            fbz = 1 + bzf;

            xfocus = x;
            yfocus = y;

            axf = ax*faz - x*azf;
            bxf = bx*fbz - x*bzf;
            ayf = ay*faz - y*azf;
            byf = by*fbz - y*bzf;

            det = dx*(axf - bxf) + dy*(ayf - byf);
            db = dx*(axf - x) + dy*(ayf - y);
            da = dx*(x - bxf) + dy*(y - byf);

            return (da*az + db*bz) / det;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function quarter(focus:Number):Array
        {
            if (length < 5)
                return null;
			
			var index0:int = screenIndices.length;
        	screenIndices[screenIndices.length] = startIndex;
        	screenIndices[screenIndices.length] = screenVertices.length;
        	var index1:int = screenIndices.length;
        	screenIndices[screenIndices.length] = screenVertices.length;
        	screenIndices[screenIndices.length] = startIndex+1;
        	var index2:int = screenIndices.length;
        	
        	ScreenVertex.median(startIndex, startIndex+1, screenVertices, screenIndices, focus);
			
            return [
                create(source, segmentVO, material, screenVertices, screenIndices, screenCommands, index0, index1, true),
                create(source, segmentVO, material, screenVertices, screenIndices, screenCommands, index1, index2, true)
            ];
        }
		
		/**
		 * @inheritDoc
		 */
        public override function calc():void
        {
        	_index = screenIndices[startIndex]*3;
        	v0x = screenVertices[_index];
        	v0y = screenVertices[_index+1];
        	v0z = screenVertices[_index+2];
        	
        	_index = screenIndices[startIndex+1]*3;
        	v1x = screenVertices[_index];
        	v1y = screenVertices[_index+1];
        	v1z = screenVertices[_index+2];
        	
        	if (v0z < v1z) {
        		minZ = v0z;
        		maxZ = v1z + 1;
        	} else {
        		minZ = v1z;
        		maxZ = v0z + 1;
        	}
            screenZ = (v0z + v1z) / 2;
            
            if (v0x < v1x) {
        		minX = v0x;
        		maxX = v1x + 1;
        	} else {
        		minX = v1x;
        		maxX = v0x + 1;
        	}
        	
        	if (v0y < v1y) {
        		minY = v0y;
        		maxY = v1y + 1;
        	} else {
        		minY = v1y;
        		maxY = v0y + 1;
        	}
            
            length = Math.sqrt((maxX - minX)*(maxX - minX) + (maxY - minY)*(maxY - minY));
        }
        
		/**
		 * @inheritDoc
		 */
        public override function toString():String
        {
            return "S{ screenZ = " + screenZ + ", minZ = " + minZ + ", maxZ = " + maxZ + " }";
        }
    }
}
