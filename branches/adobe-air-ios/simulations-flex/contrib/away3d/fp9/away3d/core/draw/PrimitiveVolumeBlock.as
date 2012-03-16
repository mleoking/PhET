package away3d.core.draw
{
    import away3d.core.base.*;


    /** Volume block containing drawing primitives */
    public class PrimitiveVolumeBlock
    {
        public var source:Object3D;
        public var list:Array;

        public var minZ:Number = +Infinity;
        public var maxZ:Number = -Infinity;
        public var minX:Number = +Infinity;
        public var maxX:Number = -Infinity;
        public var minY:Number = +Infinity;
        public var maxY:Number = -Infinity;
        

        public function PrimitiveVolumeBlock(source:Object3D)
        {
            this.source = source;
            this.list = [];
        }

        public function push(pri:DrawPrimitive):void
        {
            if (minZ > pri.minZ)
                minZ = pri.minZ;
            if (maxZ < pri.maxZ)
                maxZ = pri.maxZ;
            if (minX > pri.minX)
                minX = pri.minX;
            if (maxX < pri.maxX)
                maxX = pri.maxX;
            if (minY > pri.minY)
                minY = pri.minY;
            if (maxY < pri.maxY)
                maxY = pri.maxY;
            list.push(pri);
        }

        public function remove(pri:DrawPrimitive):void
        {
            var index:int = list.indexOf(pri);
            if (index == -1)
                throw new Error("Can't remove");
            list.splice(index, 1);
        }

        public function toString():String
        {
            return "VolumeBlock " + list.length;
        }

    }
}
