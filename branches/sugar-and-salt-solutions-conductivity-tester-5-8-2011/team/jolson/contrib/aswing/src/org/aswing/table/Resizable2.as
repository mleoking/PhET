/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * @author iiley
 */
interface org.aswing.table.Resizable2 {
    public function  getElementCount():Number;
    public function  getLowerBoundAt(i:Number):Number;
    public function  getUpperBoundAt(i:Number):Number;
    public function setSizeAt(newSize:Number, i:Number):Void;
}