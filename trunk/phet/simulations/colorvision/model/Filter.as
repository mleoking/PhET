class Filter extends MovieClip {
	private var isEnabled:Boolean = false;
	private var myColorTransform:Object;
	private var red:Number;
	private var green:Number;
	private var blue:Number;
	private var alpha:Number;
	private var transmissionPeak:Number;
	private var xLoc:Number;
	private var yLoc:Number;
	// Width (in nm) of the filter's transmission curve
	private var transmissionSpread:Number = 50;
	function setLocation(xLoc:Number, yLoc:Number):Void {
		this.xLoc = xLoc;
		this.yLoc = yLoc;
	}
	function setEnabled(isEnabled:Boolean) {
		this.isEnabled = isEnabled;
	}
	function setTransmissionPeak(wl:Number):Void {
		this.transmissionPeak = wl;
	}
	function getTransmissionPeak():Number {
		return this.transmissionPeak;
	}
	function setColorTransform(ctx):Void {
		this.myColorTransform = ctx;
	}
	function getColorTransform():Object {
		return this.myColorTransform;
	}
	function percentPassed(wavelength:Number):Number {
		var dLambda:Number = 0;
		// Special case: a wavelength of 0 indicates white light, meaning
		// that 100% is passed
		if (wavelength == 0) {
			return 100;
		}
		if (wavelength < this.transmissionPeak) {
			dLambda = wavelength - (this.transmissionPeak - (this.transmissionSpread / 2));
		}
		else {
			dLambda = this.transmissionPeak + (this.transmissionSpread / 2) - wavelength;
		}
		dLambda = dLambda < this.transmissionSpread / 2 ? dLambda : 0;
		var x:Number = 100 * 2 * dLambda / this.transmissionSpread;
		return x;
	}
	function colorPassed(color) {
		alpha = computeAlpha(color);
		red = Math.min(this.myColorTransform.rb, color.rb);
		green = Math.min(this.myColorTransform.gb, color.gb);
		blue = Math.min(myColorTransform.bb, color.bb);
		color.rb = red;
		color.ra = this.myColorTransform.ra;
		color.gb = green;
		color.ga = this.myColorTransform.ga;
		color.bb = blue;
		color.ba = this.myColorTransform.ba;
		//	color.ab = alpha;
		return color;
	}
	function getAlpha() {
		return this.alpha;
	}
	function computeAlpha(color) {
		red = Math.min(this.myColorTransform.rb, color.rb);
		green = Math.min(myColorTransform.gb, color.gb);
		blue = Math.min(myColorTransform.bb, color.bb);
		var max = Math.max(red, Math.max(green, blue));
		max = Math.max(this.myColorTransform.rb, Math.max(this.myColorTransform.gb, this.myColorTransform.bb));
		alpha = 100 * max / 255;
		this.alpha = alpha;
		return alpha;
	}
	function onEnterFrame() {
		if (this.isEnabled) {
			var photons:Array = Photon.getInstances();
			for (var i = 0; i < photons.length; i++) {
				// Note: 20 in the next line is a factor that should be tweakable
				if (photons[i].getX() > this.xLoc - Photon.ds && photons[i].getX() < this.xLoc + Photon.ds && !this.passes(photons[i].getWavelength())) {
					photons[i].setIsVisible(false);
				}
			}
		}
	}
	private function passes(wavelength:Number):Boolean {
/*
		if (wavelength == this.transmissionPeak) {
			return true;
		}
		else {
			var f = (transmissionSpread - Math.abs((transmissionPeak - wavelength))) / transmissionSpread;
			return Math.random() <= f;
		}
*/		
		return (Math.random() * 100) <= percentPassed(wavelength);
	}
}
