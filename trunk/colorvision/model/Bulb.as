class Bulb {
	private var theta:Number;
	private var tick:Number;
	private var photons:Array;
	private var photonBoundsX:Number;
	private var photonBoundsY:Number;
	private var photonBoundsWidth:Number;
	private var photonBoundsHeight:Number;
	private var myColorTransform:Object;
	private var filteredBeamColor:Object;
	private var passedColor:Object;
	private var filter:Object;
	private var wavelength:Number;
	private var listeners:Array;
	function Bulb() {
		this.tick = 0;
		this.photons = new Array();
		this.myColorTransform = {rb:0, bb:0, gb:0};
		this.listeners = new Array();
	}
	function addColorListener(listener:ColorListener):Void {
		listeners.push(listener);
	}
	function removeColorListener(listener:ColorListener):Void {
		var found:Boolean = false;
		for (var i = 0; i < listeners.length && !found; i++) {
			if (listeners[i] == listener) {
				listeners.splice(i, 1);
				found = true;
			}
		}
	}
	function setWavelength(wl:Number) {
		this.wavelength = wl;
		this.myColorTransform = ColorUtil.getCtx(wl);
		for (var i = 0; i < listeners.length; i++) {
			listeners[i].colorChanged(this.wavelength);
		}
	}
	function getWavelength():Number {
		return this.wavelength;
	}
	function setFilter(f) {
		this.filter = f;
	}
	function setFilteredBeamColor(ctx) {
		this.filteredBeamColor = ctx;
	}
	function getFilteredBeamColor() {
		return this.filteredBeamColor;
	}
	function getCtx() {
		return this.myColorTransform;
	}
	function setDirection(theta) {
		this.theta = theta;
	}
	function setPhotonBounds(x, y, width, height) {
		this.photonBoundsX = x;
		this.photonBoundsY = y;
		this.photonBoundsWidth = width;
		this.photonBoundsHeight = height;
	}
}
