/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;

/**
 * AdvancedColor contains both rgb and hls model for a color.
 * @author iiley
 */
class org.aswing.util.AdvancedColor extends ASColor {
	
	private var red:Number;
	private var green:Number;
	private var blue:Number;
	
	private var hue:Number;
	private var luminance:Number;
	private var saturation:Number;
	
	public function AdvancedColor (color:Number, alpha:Number){
		setRGB(color);
		setAlpha(alpha);
	}
	
	private function setRGB(rgb:Number):Void{
		if(rgb == null){
			this.rgb = black.getRGB();
		}else{
			this.rgb = rgb;
		}
		red = ((rgb & 0x00FF0000) >> 16);
		green = ((rgb & 0x0000FF00) >> 8);
		blue = (rgb & 0x000000FF);
		
		var hls:Object = rgb2Hls(red / 255, green / 255, blue / 255);
		hue = hls.h;
		luminance = hls.l;
		saturation = hls.s;
	}
	
	/**
     * Returns the hue component in the range [0, 1].
     * @return the hue component.
     */
	public function getHue():Number{
		return hue;
	}
	
	
	/**
     * Returns the luminance component in the range [0, 1].
     * @return the luminance component.
     */
	public function getLuminance():Number{
		return luminance;
	}
	
	
	/**
     * Returns the saturation component in the range [0, 1].
     * @return the saturation component.
     */
	public function getSaturation():Number{
		return saturation;
	}
	
	/**
     * Returns the red component in the range [0, 255].
     * @return the red component.
     */
	public function getRed():Number{
		return red;
	}
	
	/**
     * Returns the green component in the range [0, 255].
     * @return the green component.
     */	
	public function getGreen():Number{
		return green;
	}
	
	/**
     * Returns the blue component in the range [0, 255].
     * @return the blue component.
     */	
	public function getBlue():Number{
		return blue;
	}
	
	/**
	 * Creates a AdvancedColor object based on the ASColor.
	 * @return a AdvancedColor object based on the ASColor.
	 */
	public static function getAdvancedColor(color:ASColor):AdvancedColor{
		return new AdvancedColor(color.getRGB(), color.getAlpha());
	}
	
	/**
	 * Creates a AdvancedColor object based on the specified values for the HSL color model.
	 * @param h the hue, range[0, 1]
	 * @param l the luminance, range[0, 1]
	 * @param s the saturation, range[0, 1]
	 * @param a (optional)the alpha[0, 100,] default is 100
	 * @return a AdvancedColor object with the specified hue, luminance, saturation and alpha.
	 */
	public static function getHLSColor(h:Number, l:Number, s:Number, a:Number):AdvancedColor{
		var c:ASColor = HLSA2ASColor(h, l, s, a);
		return new AdvancedColor(c.getRGB(), c.getAlpha());
	}
	
	/**
	 * Creates a AdvancedColor object based on the specified values for the RGB color model.
	 * @param r the red, range[0, 255]
	 * @param g the green, range[0, 255]
	 * @param b the blue, range[0, 255]
	 * @param a (optional)the alpha[0, 100], default is 100
	 * @return a AdvancedColor object with the specified red, green, blue and alpha.
	 */
	public static function getRGBColor(r:Number, g:Number, b:Number, a:Number):AdvancedColor{
		var c:ASColor = getASColor(r, g, b, a);
		return new AdvancedColor(c.getRGB(), c.getAlpha());
	}
	
	/**
	 * Creates a new AdvancedColor that hue component is bigger or smaller.
	 * @param factor the change factor[-1, 1], -1 to make value to smallest, 1 to make value biggest.
	 * @return the new AdvancedColor with changed value
	 */
	public function hueAdjusted(factor:Number):AdvancedColor{
		factor = Math.max(-1, Math.min(1, factor));
		var h:Number;
		if(factor == 0){
			h = hue;
		}else if(factor > 0){
			h = hue + (1-hue) * factor;
		}else{
			h = hue + hue*factor;
		}
		return getHLSColor(h, luminance, saturation, alpha);
	}
	/**
	 * Creates a new AdvancedColor that luminance component is bigger or smaller.
	 * @param factor the change factor[-1, 1], -1 to make value to smallest, 1 to make value biggest.
	 * @return the new AdvancedColor with changed value
	 */
	public function luminanceAdjusted(factor:Number):AdvancedColor{
		factor = Math.max(-1, Math.min(1, factor));
		var l:Number;
		if(factor == 0){
			l = luminance;
		}else if(factor > 0){
			l = luminance + (1-luminance) * factor;
		}else{
			l = luminance + luminance*factor;
		}
		return getHLSColor(hue, l, saturation, alpha);
	}
	/**
	 * Creates a new AdvancedColor that saturation component is bigger or smaller.
	 * @param factor the change factor[-1, 1], -1 to make value to smallest, 1 to make value biggest.
	 * @return the new AdvancedColor with changed value
	 */
	public function saturationAdjusted(factor:Number):AdvancedColor{
		factor = Math.max(-1, Math.min(1, factor));
		var s:Number;
		if(factor == 0){
			s = saturation;
		}else if(factor > 0){
			s = saturation + (1-saturation) * factor;
		}else{
			s = saturation + saturation*factor;
		}
		return getHLSColor(hue, luminance, s, alpha);
	}
	
	public function toString():String{
		return "AdvanedColor[rgb:"+rgb.toString(16)+", alpha:"+alpha+
				"h:" + hue + "l:" + luminance + "s:" + saturation + "]";
	}
	
	//------------------
	
	/**
	 * H, L, S -> [0, 1], alpha -> [0, 100]
	 */
	private static function HLSA2ASColor(H:Number, L:Number, S:Number, alpha:Number):ASColor{
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
		return ASColor.getASColor(r, g, b, alpha);
	}
	
	private static function hlsValue(p1:Number, p2:Number, h:Number):Number{
	   if (h > 360) h = h - 360;
	   if (h < 0)   h = h + 360;
	   if (h < 60 ) return p1 + (p2-p1)*h/60;
	   if (h < 180) return p2;
	   if (h < 240) return p1 + (p2-p1)*(240-h)/60;
	   return p1;
	}
	
	private static function rgb2Hls(rr:Number, gg:Number, bb:Number):Object{
	   // Static method to compute HLS from RGB. The r,g,b triplet is between
	   // [0,1], h, l, s are [0,1].
	
		var rnorm:Number, gnorm:Number, bnorm:Number;
		var minval:Number, maxval:Number, msum:Number, mdiff:Number;
		var r:Number, g:Number, b:Number;
	   	var hue:Number, light:Number, satur:Number;
	   	
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
		light = 0.5 * msum;
		if (maxval != minval) {
			rnorm = (maxval - r)/mdiff;
			gnorm = (maxval - g)/mdiff;
			bnorm = (maxval - b)/mdiff;
		} else {
			satur = hue = 0;
			return {h:hue, l:light, s:satur};
		}
		
		if (light < 0.5)
		  satur = mdiff/msum;
		else
		  satur = mdiff/(2.0 - msum);
		
		if (r == maxval)
		  hue = 60.0 * (6.0 + bnorm - gnorm);
		else if (g == maxval)
		  hue = 60.0 * (2.0 + rnorm - bnorm);
		else
		  hue = 60.0 * (4.0 + gnorm - rnorm);
		
		if (hue > 360)
			hue = hue - 360;
		hue/=360;
		return {h:hue, l:light, s:satur};
	}		
}