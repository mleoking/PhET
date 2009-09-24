package  org.papervision3d.core.render.data
{
	import flash.display.Graphics;
	import flash.geom.Rectangle;
	
	import org.papervision3d.core.render.command.RenderableListItem;
	import org.papervision3d.objects.DisplayObject3D;
	

    
    /**
    * Quadrant tree node
    */
    public final class QuadTreeNode
    {
        private var render_center_length:int = -1;
        private var render_center_index:int = -1;
        private var halfwidth:Number;
        private var halfheight:Number;
        private var level:int;
        public var maxlevel:int = 4;
        
        private function render_other(limit:Number, renderSessionData:RenderSessionData, graphics:Graphics):void
        {
        	if (lefttopFlag)
                lefttop.render(limit, renderSessionData, graphics);
            if (leftbottomFlag)
                leftbottom.render(limit, renderSessionData, graphics);
            if (righttopFlag)
                righttop.render(limit, renderSessionData, graphics);
            if (rightbottomFlag)
                rightbottom.render(limit, renderSessionData, graphics);
        }
        
        /**
        * Array of primitives that lie in the center of the quadrant.
        */
        public var center:Array;
        
        /**
        * The quadrant tree node for the top left quadrant.
        */
        public var lefttop:QuadTreeNode;
        
        /**
        * The quadrant tree node for the bottom left quadrant.
        */
        public var leftbottom:QuadTreeNode;
        
        /**
        * The quadrant tree node for the top right quadrant.
        */
        public var righttop:QuadTreeNode;
        
        /**
        * The quadrant tree node for the bottom right quadrant.
        */
        public var rightbottom:QuadTreeNode;
        
        /**
        * Determines if the bounds of the top left quadrant need re-calculating.
        */
        public var lefttopFlag:Boolean;
        
        /**
        * Determines if the bounds of the bottom left quadrant need re-calculating.
        */
        public var leftbottomFlag:Boolean;
        
        /**
        * Determines if the bounds of the top right quadrant need re-calculating.
        */
        public var righttopFlag:Boolean;
        
        /**
        * Determines if the bounds of the bottom right quadrant need re-calculating.
        */
        public var rightbottomFlag:Boolean;
                
        /**
        * Determines if the quadrant node contains only one source.
        */
		public var onlysourceFlag:Boolean = true;
		
		/**
		 * hold the 3d object referenced when <code>onlysourceFlag</code> is true.
		 */
        public var onlysource:DisplayObject3D;
        
        /**
        * The x coordinate of the quadrant division.
        */
        public var xdiv:Number;
        
        /**
        * The x coordinate of the quadrant division.
        */
        public var ydiv:Number;
		
		/**
		 * The quadrant parent.
		 */
        public var parent:QuadTreeNode;
		
        /**
        * Placeholder function for creating new quadrant node from a cache of objects.
        * Saves recreating objects and GC problems.
        */
		public var create:Function;
		
		
		/**
		 * Says if node has content or not
		 */
		public var hasContent:Boolean = false;
		
		/**
		 * Creates a new <code>PrimitiveQuadrantTreeNode</code> object.
		 *
		 * @param	xdiv	The x coordinate for the division between left and right child quadrants.
		 * @param	ydiv	The y coordinate for the division between top and bottom child quadrants.
		 * @param	width	The width of the quadrant node.
		 * @param	xdiv	The height of the quadrant node.
		 * @param	level	The iteration number of the quadrant node.
		 * @param	parent	The parent quadrant of the quadrant node.
		 * @param	maxLevel	The deepest a Node can go
		 */
        public function QuadTreeNode(xdiv:Number, ydiv:Number, width:Number, height:Number, level:int, parent:QuadTreeNode = null, maxLevel:uint = 4)
        {
            this.level = level;
            this.xdiv = xdiv;
            this.ydiv = ydiv;
            halfwidth = width / 2;
            halfheight = height / 2;
            this.parent = parent;
            this.maxlevel = maxLevel;
        }
		
		/**
		 * Adds a primitive to the quadrant
		 */
        public function push(pri:RenderableListItem):void
        {
        	hasContent = true;
        	
            if (onlysourceFlag) {
	            if (onlysource != null && onlysource != pri.instance)
	            	onlysourceFlag = false;
                onlysource = pri.instance;
            }
			
			if (level < maxlevel) {
	            if (pri.maxX <= xdiv)
	            {
	                if (pri.maxY <= ydiv)
	                {
	                    if (lefttop == null) {
	                    	lefttopFlag = true;
	                        lefttop = new QuadTreeNode(xdiv - halfwidth/2, ydiv - halfheight/2, halfwidth, halfheight, level+1, this, maxlevel);
	                    } else if (!lefttopFlag) {
	                    	lefttopFlag = true;
	                    	lefttop.reset(xdiv - halfwidth/2, ydiv - halfheight/2, halfwidth, halfheight, maxlevel);
	                    }
	                    lefttop.push(pri);
	                    return;
	                }
	                else if (pri.minY >= ydiv)
	                {
	                	if (leftbottom == null) {
	                    	leftbottomFlag = true;
	                        leftbottom = new QuadTreeNode(xdiv - halfwidth/2, ydiv + halfheight/2, halfwidth, halfheight, level+1, this, maxlevel);
	                    } else if (!leftbottomFlag) {
	                    	leftbottomFlag = true;
	                    	leftbottom.reset(xdiv - halfwidth/2, ydiv + halfheight/2, halfwidth, halfheight, maxlevel);
	                    }
	                    leftbottom.push(pri);
	                    return;
	                }
	            }
	            else if (pri.minX >= xdiv)
	            {
	                if (pri.maxY <= ydiv)
	                {
	                	if (righttop == null) {
	                    	righttopFlag = true;
	                        righttop = new QuadTreeNode(xdiv + halfwidth/2, ydiv - halfheight/2, halfwidth, halfheight, level+1, this, maxlevel);
	                    } else if (!righttopFlag) {
	                    	righttopFlag = true;
	                    	righttop.reset(xdiv + halfwidth/2, ydiv - halfheight/2, halfwidth, halfheight, maxlevel);
	                    }
	                    righttop.push(pri);
	                    return;
	                }
	                else if (pri.minY >= ydiv)
	                {
	                	if (rightbottom == null) {
	                    	rightbottomFlag = true;
	                        rightbottom = new QuadTreeNode(xdiv + halfwidth/2, ydiv + halfheight/2, halfwidth, halfheight, level+1, this, maxlevel);
	                    } else if (!rightbottomFlag) {
	                    	rightbottomFlag = true;
	                    	rightbottom.reset(xdiv + halfwidth/2, ydiv + halfheight/2, halfwidth, halfheight, maxlevel);
	                    }
	                    rightbottom.push(pri);
	                    return;
	                }
	            }
			}
			
			//no quadrant, store in center array
            if (center == null)
                center = new Array();
            center.push(pri);
            
            pri.quadrant = this;
          
        }
        
        /**
        * Clears the quadrant of all primitives and child nodes
        */
		public function reset(xdiv:Number, ydiv:Number, width:Number, height:Number, maxLevel:uint):void
		{
			this.xdiv = xdiv;
			this.ydiv = ydiv;
			halfwidth = width / 2;
            halfheight = height / 2;
			
            lefttopFlag = false;
            leftbottomFlag = false;
            righttopFlag = false;
            rightbottomFlag = false;
            
            onlysourceFlag = true;
            onlysource = null;
            
            render_center_length = -1;
            render_center_index = -1;
            hasContent = false;
            maxlevel = maxLevel;
           
		}
		
		public function getRect():Rectangle{
			return new Rectangle(xdiv, ydiv, halfwidth*2, halfheight*2);
		}
		
		
		/**
		 * Sorts and renders the contents of the quadrant tree
		 */
        public function render(limit:Number, renderSessionData:RenderSessionData, graphics:Graphics):void
        {
        	
            if (render_center_length == -1)
            {
                if (center != null)
                {
                    render_center_length = center.length;
                    if (render_center_length > 1)
                        center.sortOn("screenZ", Array.DESCENDING | Array.NUMERIC);
                }
                else
                    render_center_length = 0;
                render_center_index = 0;
            }

            while (render_center_index < render_center_length)
            {
                var pri:RenderableListItem = center[render_center_index];
				
                if (pri.screenZ < limit)
                    break;

                render_other(pri.screenZ, renderSessionData, graphics);

                pri.render(renderSessionData, graphics);
                renderSessionData.viewPort.lastRenderList.push(pri);

                render_center_index++;
            }
            
            if (render_center_index == render_center_length)
				center = null;
			
            render_other(limit, renderSessionData, graphics);
        }
        
        
    }
}
