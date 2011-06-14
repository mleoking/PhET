/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * ASColor object to set color and alpha for a movieclip.
 * @author firdosh
 */
class org.aswing.ASColor{
	
	private static var FACTOR:Number = 0.7;
	
	private var rgb:Number;
	private var alpha:Number;
	
	public function ASColor (color:Number, alpha:Number){
		setRGB(color);
		setAlpha(alpha);
	}
	
	private function setRGB(rgb:Number):Void{
		if(rgb == null){
			this.rgb = black.getRGB();
		}else{
			this.rgb = rgb;		
		}
	}
	
	private function setAlpha(alpha:Number):Void{
		if(alpha == null){
			this.alpha = 100;
		}else{
			this.alpha = Math.min(100, Math.max(0, alpha));
		}
	}
	
	/**
	 * Returns the alpha component in the range 0-100.
	 */
	public function getAlpha():Number{
		return alpha;	
	}
	
	/**
	 * Returns the RGB value representing the color.
	 */
	public function getRGB():Number{
		return rgb;	
	}
	
	/**
     * Returns the red component in the range 0-255.
     * @return the red component.
     */
	public function getRed():Number{
		return (rgb & 0x00FF0000) >> 16;
	}
	
	/**
     * Returns the green component in the range 0-255.
     * @return the green component.
     */	
	public function getGreen():Number{
		return (rgb & 0x0000FF00) >> 8;
	}
	
	/**
     * Returns the blue component in the range 0-255.
     * @return the blue component.
     */	
	public function getBlue():Number{
		return (rgb & 0x000000FF);
	}
	
    /**
     * darker(factor:Number)<br>
     * darker() default factor to 0.7
     * <p>
     * Creates a new <code>Color</code> that is a darker version of this
     * <code>Color</code>.
     * @param factor the darker factor(0, 1), default is 0.7
     * @return     a new <code>Color</code> object that is  
     *                 a darker version of this <code>Color</code>.
     * @see        #brighter()
     */		
	public function darker(factor:Number):ASColor{
		if(factor == undefined){
			factor = FACTOR;
		}
        var r:Number = getRed();
        var g:Number = getGreen();
        var b:Number = getBlue();
		return getASColor(r*factor, g*factor, b*factor, alpha);
	}
	
    /**
     * brighter(factor:Number)<br>
     * brighter() default factor to 0.7
     * <p>
     * Creates a new <code>Color</code> that is a brighter version of this
     * <code>Color</code>.
     * @param factor the birghter factor(0, 1), default is 0.7
     * @return     a new <code>Color</code> object that is  
     *                 a brighter version of this <code>Color</code>.
     * @see        #darker()
     */	
	public function brighter(factor:Number):ASColor{
		if(factor == undefined){
			factor = FACTOR;
		}
        var r:Number = getRed();
        var g:Number = getGreen();
        var b:Number = getBlue();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        var i:Number = Math.floor(1.0/(1.0-factor));
        if ( r == 0 && g == 0 && b == 0) {
           return getASColor(i, i, i, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;
        
        return getASColor(r/factor, g/factor, b/factor, alpha);
	}
	
	/**
	 * Returns a ASColor with with the specified red, green, blue values in the range (0 - 255) 
	 * and alpha value in range(0-100). 
	 * <p>
	 * getASColor(r:Number, g:Number, b:Number, a:Number)<br>
	 * getASColor(r:Number, g:Number, b:Number)<br> alpha default to 100
	 */
	public static function getASColor(r:Number, g:Number, b:Number, a:Number):ASColor{
		return new ASColor(getRGBWith(r, g, b), a);
	}
		
	/**
	 * Returns the RGB value representing the red, green, and blue values. 
	 */
	public static function getRGBWith(rr:Number, gg:Number, bb:Number):Number {
		var r:Number = Math.round(rr);
		var g:Number = Math.round(gg);
		var b:Number = Math.round(bb);
		if(r < 0){
			r = 0;
		}else if(r > 255){
			r = 255;
		}
		if(g < 0){
			g = 0;
		}else if(g > 255){
			g = 255;
		}
		if(b < 0){
			b = 0;
		}else if(b > 255){
			b = 255;
		}
		var color_n:Number = (r<<16) + (g<<8) +b;
		return color_n;
	}
	
	public function toString():String{
		return "ASColor(rgb:"+rgb.toString(16)+", alpha:"+alpha+")";
	}
	
	public function equals(o:Object):Boolean{
		var c:ASColor = ASColor(o);
		if(c!=null){
			return c.alpha === alpha && c.rgb === rgb;
		}else{
			return false;
		}
	}
	
	private static var white:ASColor = new ASColor(0xffffff);
	public static function get WHITE():ASColor{
		return white;
	}
	
	private static var lightGray:ASColor = new ASColor(0xc0c0c0);
	public static function get LIGHT_GRAY():ASColor{
		return lightGray;
	}
	
	private static var gray:ASColor = new ASColor(0x808080);
	public static function get GRAY():ASColor{
		return gray;
	}
	
	private static var darkGray:ASColor = new ASColor(0x404040);
	public static function get DARK_GRAY():ASColor{
		return darkGray;
	}
	
	private static var black:ASColor = new ASColor(0x000000);
	public static function get BLACK():ASColor{
		return black;
	}
	
	private static var red:ASColor = new ASColor(0xff0000);
	public static function get RED():ASColor{
		return red;
	}
	
	private static var pink:ASColor = new ASColor(0xffafaf);
	public static function get PINK():ASColor{
		return pink;
	}
	
	private static var orange:ASColor = new ASColor(0xffc800);
	public static function get ORANGE():ASColor{
		return orange;
	}
	
	private static var haloOrange:ASColor = new ASColor(0xFFC200);
	public static function get HALO_ORANGE():ASColor{
		return haloOrange;
	}
	
	private static var yellow:ASColor = new ASColor(0xffff00);
	public static function get YELLOW():ASColor{
		return yellow;
	}
	
	private static var green:ASColor = new ASColor(0x00ff00);
	public static function get GREEN():ASColor{
		return green;
	}
	
	private static var haloGreen:ASColor = new ASColor(0x80FF4D);
	public static function get HALO_GREEN():ASColor{
		return haloGreen;
	}
	
	private static var magenta:ASColor = new ASColor(0xff00ff);
	public static function get MAGENTA():ASColor{
		return magenta;
	}
	
	private static var cyan:ASColor = new ASColor(0x00ffff);
	public static function get CYAN():ASColor{
		return cyan;
	}
	
	private static var blue:ASColor = new ASColor(0x0000ff);
	public static function get BLUE():ASColor{
		return blue;
	}
	
	private static var haloBlue:ASColor = new ASColor(0x2BF5F5);
	public static function get HALO_BLUE():ASColor{
		return haloBlue;
	}
	
		
}
