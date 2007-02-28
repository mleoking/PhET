class PhotonGenerator extends MovieClip implements ColorListener {
	private var running:Boolean;
	// The x coord at which photons are deleted
	private var xCutoff:Number;
	private var rate:Number;
	// NOTE!!! The following line results in the Array being allocated
	// statically to the class!!! I have to allocate it in the constructor;
	//	private var photons:Array = new Array();
	private var photons:Array;
	private var rateAtEyeball:Number;
	private var xLoc:Number;
	private var yLoc:Number;
	private var theta:Number;
	private var color:Number;
	private var alpha:Number;
	private var wavelength:Number;
	private var intervalID:Number;
	public function PhotonGenerator() {
		rate = 0;
		alpha = 100;
		photons = new Array();
		rateAtEyeball = 0;
		running = false;
		xCutoff = _root.head._x;
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
	/**
		 * Get a photon to a given specification.
		 */
	private function allocatePhoton(xLoc:Number, yLoc:Number, theta:Number, wavelength:Number, rate:Number) {
		var p:Photon = undefined;
		// Look through the photon records we already have to see if there is one that is not in use
		for (var i = 0; i < photons.length && p == undefined; i++) {
			if (!photons[i].isInUse()) {
				p = photons[i].getPhoton();
				p.setLocation(xLoc, yLoc);
				p.setWavelength(wavelength);
				photons[i].setRate(rate);
				photons[i].setInUse(true);
				p.setIsVisible(true);
			}
		}
		// If we didn't find an unused photon that is not in use, allocate one
		if (p == undefined) {
			p = new Photon(xLoc, yLoc, theta, wavelength);
			photons.push(new PhotonGeneratorRecordEntry(p, rate));
		}
		return p;
	}
	public function onEnterFrame() {
		this.clear();
		if (running == true) {
			var wl;
			var p:Photon;
			for (var n = 0; n < Math.round(rate); n++) {
				if (this.wavelength == 0) {
					wl = Math.random() * (_root.maxWavelength - _root.minWavelength) + _root.minWavelength;
				}
				else {
					wl = this.wavelength;
				}
				//				p = new Photon(xLoc, yLoc, genTheta(theta * Math.PI / 180), wl);
				//				photons.push(new PhotonGeneratorRecordEntry(p, rate));
				p = allocatePhoton(genXLoc(xLoc), yLoc, genTheta(theta * Math.PI / 180), wl, rate);
			}
			// Paint or prune the photons, as need be
			var somePhotonInUse:Boolean = false;
			for (var i = 0; i < photons.length; i++) {
				if (photons[i].isInUse()) {
					somePhotonInUse = true;
					p = photons[i].getPhoton();
					if (p != undefined && p.getX() <= _root.head._x) {
						p.paint(this);
					}
					else {
						photons[i].setInUse(false);
						this.rateAtEyeball = photons[i].getRate();
						//					photons.splice(i, 1);
						//					Photon.deleteInstance(p);
					}
				}
			}
			//			if (photons.length == 0) {
			if (!somePhotonInUse) {
				this.rateAtEyeball = 0;
			}
		}
	}
	function stop() {
		running = false;
		super.stop();
	}
	function start() {
		running = true;
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
	function getRateAtEyeball():Number {
		return this.rateAtEyeball;
	}
	private function genTheta(theta0) {
		var d_theta = Math.random() * Math.PI / 16 - Math.PI / 32;
		var angle = theta0 + d_theta;
		return angle;
	}
	private function genXLoc( xLoc:Number):Number{
		return xLoc + Math.random() * Photon.ds / 2;
	}
	function colorChanged(wavelength:Number):Void {
		this.setWavelength(wavelength);
	}
	function onUnload() {
		this.clear();
		super.onUnload();
	}
}
