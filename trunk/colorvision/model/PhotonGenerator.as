class PhotonGenerator extends MovieClip implements ColorListener {
	private static var canvasLayer:Number = 0;
	private var running:Boolean;
	private var rate:Number;
	private var photons:Array;
	private var photonCanvas_mc:MovieClip;
	private var xLoc:Number;
	private var yLoc:Number;
	private var theta:Number;
	private var color:Number;
	private var alpha:Number;
	private var wavelength:Number;
	public function PhotonGenerator() {
		rate = 1;
		photons = new Array();
		photonCanvas_mc = _root.createEmptyMovieClip("clock", canvasLayer++);
		alpha = 100;
	}
	public function setLocation(xLoc:Number, yLoc:Number) {
		this.xLoc = xLoc;
		this.yLoc = yLoc;
	}
	public function setTheta(theta:Number) {
		this.theta = theta;
	}
	public function setAlpha(alpha:Number):Void {
		this.alpha = alpha;
	}
	public function setColor(color:Number) {
		this.color = color;
		var ctx = ColorUtil.colorToCtx(color);
		this.wavelength = ColorUtil.ctxToWavelength(ctx);
		if (color == 0xFFFFFF) {
			this.wavelength = 0;
		}
	}
	public function setWavelength(wavelength:Number) {
		this.wavelength = wavelength;
		var ctx = ColorUtil.getCtx(wavelength);
		this.color = ColorUtil.ctxToColor(ctx);
	}
	public function onEnterFrame() {
		photonCanvas_mc.clear();
		if (running == true) {
			var c;
			var wl;
			if (this.wavelength == 0) {
				wl = Math.random() * (_root.maxWavelength - _root.minWavelength) + _root.minWavelength;
				c = ColorUtil.getColor(wl);
			}
			else {
				c = this.color;
			}
			for (var n = 0; n < Math.round(rate); n++) {
				var photon = new Photon(xLoc, yLoc, genTheta(theta * Math.PI / 180), c);
				photons.push(photon);
			}
			// Paint or prune the photons, as need be
			for (var i = 0; i < photons.length; i++) {
//				trace("photonGenerator: " + photons[i]._x + "  " + _root.head._x );
				if (photons[i]._x <= _root.head._x) {
					photons[i].paint(photonCanvas_mc);
				}
				else {
//					trace("photongenerator: prune");
					var p = photons[i];
					photons.splice(i, 1);
//					p.removeMovieClip();
				}
			}
		}
	}
	function stop() {
		running = false;
		photonCanvas_mc.stop();
	}
	function start() {
		running = true;
		photonCanvas_mc.play();
	}
	function isRunning():Boolean {
		return running;
	}
	function activate() {
		super.activate();
		this.start();
	}
	function deactivate() {
		super.deactivate();
		this.stop();
	}
	function getMaxRate():Number {
		return 8;
	}
	function setRate(rate:Number) {
		this.rate = rate;
	}
	private function genTheta(theta0) {
		var d_theta = Math.random() * Math.PI / 8 - Math.PI / 16;
//		var d_theta = Math.random() * Math.PI / 16 - Math.PI / 32;
		var angle = theta0 + d_theta;
		return angle;
	}
	function colorChanged(wavelength:Number):Void {
		this.setWavelength(wavelength);
	}
	function onUnload() {
		this.photonCanvas_mc.clear();
		super.onUnload();
	}
}
