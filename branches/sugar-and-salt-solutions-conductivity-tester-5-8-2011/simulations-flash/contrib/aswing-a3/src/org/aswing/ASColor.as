/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

/**
 * ASColor object to set color and alpha for a movieclip.
 * @author firdosh, iiley, n0rthwood
 */
public class ASColor{
			
	public static const WHITE:ASColor = new ASColor(0xffffff);
	
	public static const LIGHT_GRAY:ASColor = new ASColor(0xc0c0c0);
	
	public static const GRAY:ASColor = new ASColor(0x808080);
	
	public static const DARK_GRAY:ASColor = new ASColor(0x404040);
	
	public static const BLACK:ASColor = new ASColor(0x000000);
	
	public static const RED:ASColor = new ASColor(0xff0000);
	
	public static const PINK:ASColor = new ASColor(0xffafaf);
	
	public static const ORANGE:ASColor = new ASColor(0xffc800);
	
	public static const HALO_ORANGE:ASColor = new ASColor(0xFFC200);
	
	public static const YELLOW:ASColor = new ASColor(0xffff00);
	
	public static const GREEN:ASColor = new ASColor(0x00ff00);
	
	public static const HALO_GREEN:ASColor = new ASColor(0x80FF4D);
	
	public static const MAGENTA:ASColor = new ASColor(0xff00ff);
	
	public static const CYAN:ASColor = new ASColor(0x00ffff);
	
	public static const BLUE:ASColor = new ASColor(0x0000ff);
	
	public static const HALO_BLUE:ASColor = new ASColor(0x2BF5F5);
	
	
	protected var rgb:uint;
	protected var alpha:Number;
	
	protected var hue:Number;
	protected var luminance:Number;
	protected var saturation:Number;
	private var hlsCounted:Boolean;
	
	/**
	 * Create a ASColor
	 */
	public function ASColor (rgb:uint=0x000000, alpha:Number=1){
		this.rgb = rgb;
		this.alpha = Math.min(1, Math.max(0, alpha));
		hlsCounted = false;
	}
	
	/**
	 * Returns the alpha component in the range 0-1.
	 */
	public function getAlpha():Number{
		return alpha;	
	}
	
	/**
	 * Returns the RGB value representing the color.
	 */
	public function getRGB():uint{
		return rgb;	
	}
	
	/**
	 * Returns the ARGB value representing the color.
	 */	
	public function getARGB():uint{
		var a:uint = alpha*255;
		return rgb | (a << 24);
	}
	
	/**
     * Returns the red component in the range 0-255.
     * @return the red component.
     */
	public function getRed():uint{
		return (rgb & 0x00FF0000) >> 16;
	}
	
	/**
     * Returns the green component in the range 0-255.
     * @return the green component.
     */	
	public function getGreen():uint{
		return (rgb & 0x0000FF00) >> 8;
	}
	
	/**
     * Returns the blue component in the range 0-255.
     * @return the blue component.
     */	
	public function getBlue():uint{
		return (rgb & 0x000000FF);
	}
	
	/**
     * Returns the hue component in the range [0, 1].
     * @return the hue component.
     */
	public function getHue():Number{
		countHLS();
		return hue;
	}
	
	
	/**
     * Returns the luminance component in the range [0, 1].
     * @return the luminance component.
     */
	public function getLuminance():Number{
		countHLS();
		return luminance;
	}
	
	
	/**
     * Returns the saturation component in the range [0, 1].
     * @return the saturation component.
     */
	public function getSaturation():Number{
		countHLS();
		return saturation;
	}
	
	private function countHLS():void{
		if(hlsCounted){
			return;
		}
		hlsCounted = true;
		var rr:Number = getRed() / 255.0;
		var gg:Number = getGreen() / 255.0;
		var bb:Number = getBlue() / 255.0;
		
		var rnorm:Number, gnorm:Number, bnorm:Number;
		var minval:Number, maxval:Number, msum:Number, mdiff:Number;
		var r:Number, g:Number, b:Number;
		   
		r = g = b = 0;
		if (rr > 0) r = rr; if (r > 1) r = 1;
		if (gg > 0) g = gg; if (g > 1) g = 1;
		if (bb > 0) b = bb; if (b > 1) b = 1;
		
		minval = r;
		if (g < minval) minval = g;
		if (b < minval) minval = b;
		maxval = r;
		if (g > maxval) maxval = g;
		if (b > maxval) maxval = b;
		
		rnorm = gnorm = bnorm = 0;
		mdiff = maxval - minval;
		msum  = maxval + minval;
		luminance = 0.5 * msum;
		if (maxval != minval) {
			rnorm = (maxval - r)/mdiff;
			gnorm = (maxval - g)/mdiff;
			bnorm = (maxval - b)/mdiff;
		} else {
			saturation = hue = 0;
			return;
		}
		
		if (luminance < 0.5)
		  saturation = mdiff/msum;
		else
		  saturation = mdiff/(2.0 - msum);
		
		if (r == maxval)
		  hue = 60.0 * (6.0 + bnorm - gnorm);
		else if (g == maxval)
		  hue = 60.0 * (2.0 + rnorm - bnorm);
		else
		  hue = 60.0 * (4.0 + gnorm - rnorm);
		
		if (hue > 360)
			hue = hue - 360;
		hue /= 360;
	}	
	
	/**
	 * Create a new <code>ASColor</code> with another alpha but same rgb.
	 * @param newAlpha the new alpha
	 * @return the new <code>ASColor</code>
	 */
	public function changeAlpha(newAlpha:Number):ASColor{
		return new ASColor(getRGB(), newAlpha);
	}
	
	/**
	 * Create a new <code>ASColor</code> with just change hue channel value.
	 * @param newHue the new hue value
	 * @return the new <code>ASColor</code>
	 */	
	public function changeHue(newHue:Number):ASColor{
		return getASColorWithHLS(newHue, getLuminance(), getSaturation(), getAlpha());
	}
	
	/**
	 * Create a new <code>ASColor</code> with just change luminance channel value.
	 * @param newLuminance the new luminance value
	 * @return the new <code>ASColor</code>
	 */	
	public function changeLuminance(newLuminance:Number):ASColor{
		return getASColorWithHLS(getHue(), newLuminance, getSaturation(), getAlpha());
	}
	
	/**
	 * Create a new <code>ASColor</code> with just change saturation channel value.
	 * @param newSaturation the new saturation value
	 * @return the new <code>ASColor</code>
	 */	
	public function changeSaturation(newSaturation:Number):ASColor{
		return getASColorWithHLS(getHue(), getLuminance(), newSaturation, getAlpha());
	}
	
    /**
     * Creates a new <code>ASColor</code> that is a darker version of this
     * <code>ASColor</code>.
     * @param factor the darker factor(0, 1), default is 0.7
     * @return     a new <code>ASColor</code> object that is  
     *                 a darker version of this <code>ASColor</code>.
     * @see        #brighter()
     */		
	public function darker(factor:Number=0.7):ASColor{
        var r:uint = getRed();
        var g:uint = getGreen();
        var b:uint = getBlue();
		return getASColor(r*factor, g*factor, b*factor, alpha);
	}
	
    /**
     * Creates a new <code>ASColor</code> that is a brighter version of this
     * <code>ASColor</code>.
     * @param factor the birghter factor 0 to 1, default is 0.7
     * @return     a new <code>ASColor</code> object that is  
     *                 a brighter version of this <code>ASColor</code>.
     * @see        #darker()
     */	
	public function brighter(factor:Number=0.7):ASColor{
        var r:uint = getRed();
        var g:uint = getGreen();
        var b:uint = getBlue();

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
	 * Returns a ASColor with the specified red, green, blue values in the range [0 - 255] 
	 * and alpha value in range[0, 1]. 
	 * @param r red channel
	 * @param g green channel
	 * @param b blue channel
	 * @param a alpha channel
	 */
	public static function getASColor(r:uint, g:uint, b:uint, a:Number=1):ASColor{
		return new ASColor(getRGBWith(r, g, b), a);
	}
	
	/**
	 * Returns a ASColor with specified ARGB uint value.
	 * @param argb ARGB value representing the color
	 * @return the ASColor
	 */
	public static function getWithARGB(argb:uint):ASColor{
		var rgb:uint = argb & 0x00FFFFFF;
		var alpha:Number = (argb >>> 24)/255;
		return new ASColor(rgb, alpha);
	}
	
	/**
	 * Returns a ASColor with with the specified hue, luminance, 
	 * saturation and alpha values in the range [0 - 1]. 
	 * @param h hue channel
	 * @param l luminance channel
	 * @param s saturation channel
	 * @param a alpha channel
	 */	
	public static function getASColorWithHLS(h:Number, l:Number, s:Number, a:Number=1):ASColor{
		var c:ASColor = new ASColor(0, a);
		c.hlsCounted = true;
		c.hue = Math.max(0, Math.min(1, h));
		c.luminance = Math.max(0, Math.min(1, l));
		c.saturation = Math.max(0, Math.min(1, s));
		
		var H:Number = c.hue;
		var L:Number = c.luminance;
		var S:Number = c.saturation;
		
		var p1:Number, p2:Number, r:Number, g:Number, b:Number;
		p1 = p2 = 0;
		H = H*360;
		if(L<0.5){
			p2=L*(1+S);
		}else{
			p2=L + S - L*S;
		}
		p1=2*L-p2;
		if(S==0){
			r=L;
			g=L;
			b=L;
		}else{
			r = hlsValue(p1, p2, H+120);
			g = hlsValue(p1, p2, H);
			b = hlsValue(p1, p2, H-120);
		}
		r *= 255;
		g *= 255;
		b *= 255;
		var color_n:Number = (r<<16) + (g<<8) +b;
		var color_rgb:uint = Math.max(0, Math.min(0xFFFFFF, Math.round(color_n)));
		c.rgb = color_rgb;
		return c;
	}
	
	private static function hlsValue(p1:Number, p2:Number, h:Number):Number{
	   if (h > 360) h = h - 360;
	   if (h < 0)   h = h + 360;
	   if (h < 60 ) return p1 + (p2-p1)*h/60;
	   if (h < 180) return p2;
	   if (h < 240) return p1 + (p2-p1)*(240-h)/60;
	   return p1;
	}	
		
	/**
	 * Returns the RGB value representing the red, green, and blue values. 
	 * @param rr red channel
	 * @param gg green channel
	 * @param bb blue channel
	 */
	public static function getRGBWith(rr:uint, gg:uint, bb:uint):uint {
		var r:uint = rr;
		var g:uint = gg;
		var b:uint = bb;
		if(r > 255){
			r = 255;
		}
		if(g > 255){
			g = 255;
		}
		if(b > 255){
			b = 255;
		}
		var color_n:uint = (r<<16) + (g<<8) +b;
		return color_n;
	}
	
	public function toString():String{
		return "ASColor(rgb:"+rgb.toString(16)+", alpha:"+alpha+")";
	}

	/**
	 * Compare if compareTo object has the same value as this ASColor object does
	 * @param compareTo the object to compare with
	 * 
	 * @return  a Boolean value that indicates if the compareTo object's value is the same as this one
	 */	
	public function equals(o:Object):Boolean{
		var c:ASColor = o as ASColor;
		if(c != null){
			return c.alpha === alpha && c.rgb === rgb;
		}else{
			return false;
		}
	}
}

}