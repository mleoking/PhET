/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.geom.Point;

/**
 * Utils functions about MovieClip
 * @author iiley
 */
class org.aswing.util.MCUtils{
        
    /**
     * Transform the position in fromMC to toMC and return it.
     */
    public static function locationTrans(fromMC:MovieClip, toMC:MovieClip, p:Point):Point{
        if (p == undefined) p = new Point();
        fromMC.localToGlobal(p);
        toMC.globalToLocal(p);
        return p;
    }
    
    /**
     * Returns is the MovieClip is exist.
     */
    public static function isMovieClipExist(mc:MovieClip):Boolean{
        return mc != undefined && mc._totalframes != undefined;
    }
    
    /**
     * Returns is the MovieClip is exist.
     */
    public static function isTextFieldExist(tf:TextField):Boolean{
        return tf != undefined && tf._height != undefined;
    }
    
    public static function pixelSnapMovieClip(mc:MovieClip, x:Number, y:Number):Void{
        pixelSnap(mc, x, y);
    }
    
    public static function pixelSnapTextField(tf:TextField, x:Number, y:Number):Void{
        pixelSnap(tf, x, y);
    }
    
    private static function pixelSnap(symbol, x:Number, y:Number):Void{
        var offset:Point = new Point(0, 0);
        symbol._parent.localToGlobal(offset);
        symbol._x = Math.round(x) + Math.floor(offset.x) - offset.x;
        symbol._y = Math.round(y) + Math.floor(offset.y) - offset.y;
    }
}
