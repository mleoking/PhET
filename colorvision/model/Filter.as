class Filter extends MovieClip {
	private var myColorTransform:Object;
	private var red:Number;
	private var green:Number;
	private var blue:Number;
	private var alpha:Number;
	private var transmissionPeak:Number;
	private var xLoc:Number;
	private var yLoc:Number;
	// Width (in nm) of the filter's transmission curve
	private var transmissionSpread:Number = 100;
	// This object's clock
	
	function setLocation( xLod:Number, yLoc:Number):Void{
		this.xLoc = xLoc;
		this.yLoc = yLoc;
	}
	function setTransmissionPeak( wl:Number ):Void {
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
	function passes(color):Boolean {
		red = Math.min(this.myColorTransform.rb, color.rb);
		green = Math.min(this.myColorTransform.gb, color.gb);
		blue = Math.min(this.myColorTransform.bb, color.bb);
		var result = (red>0) || (green>0) || (blue>0);
		return result;
	}
	
	function percentPassed( wavelength:Number):Number {
		var dLambda:Number = 0;
		// Special case: a wavelength of 0 indicates white light, meaning
		// that 100% is passed
		if( wavelength == 0 ) {
			return 100;
		}
		if( wavelength < this.transmissionPeak ) {
			dLambda =  wavelength - ( this.transmissionPeak - (this.transmissionSpread / 2 ));
		}
		else {
			dLambda = this.transmissionPeak + (this.transmissionSpread / 2 ) - wavelength;
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
		// Try something else
		//	red = myColorTransform.rb >= 0 ? color.rb : 0;  
		//	green = myColorTransform.gb >= 0 ? color.gb : 0;  
		//	blue = myColorTransform.bb >= 0 ? color.bb : 0;  
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
		alpha = 100*max/255;
		this.alpha = alpha;
		return alpha;
	}

	function onEnterFrame(){
/*		
		var photons:Array = Photon.getInstances();
		for(var i=0; i<photons.length; i++){
			if(photons[i]._x > this.xLoc){
				trace("filter !!");
				photons[i].setRgb(0);
			}
		}
*/		
	}
}
