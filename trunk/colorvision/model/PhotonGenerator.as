class PhotonGenerator extends MovieClip implements ColorListener {
	private static var canvasLayer:Number = 0;
	private var running:Boolean;
	private var rate:Number;
	private var photons:Array = new Array();
	private var rateHistory:Array = new Array();;
	private var rateAtEyeball:Number = 0;
	private var xLoc:Number;
	private var yLoc:Number;
	private var theta:Number;
	private var color:Number;
	private var alpha:Number;
	private var wavelength:Number;
	public function PhotonGenerator() {
		rate = 1;
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
		this.clear();
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
			var p:Photon;
			var intRate:Number = Math.round(rate);
			for (var n = 0; n < intRate; n++) {
				p = new Photon(xLoc, yLoc, genTheta(theta * Math.PI / 180), c);
				photons.push(p);
			}
			rateHistory.push(Math.round(rate));

			// Paint or prune the photons, as need be
			for (var i = 0; i < photons.length; i++) {
				p = photons[i];
				if (p!= undefined && p.getX() <= _root.head._x) {
					p.paint(this);
				}
				else {
					photons.splice(i,1);
				}
			}
/*
			for(var i=0; i<rateHistory;i++){
				if(
					rateAtEyeball = rateHistory[i];
					rateHistory.splice(i,1);
*/					
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
	function getRateAtEyeball():Number{
		return this.rate;
//		return this.rateAtEyeball;
	}
	private function genTheta(theta0) {
		var d_theta = Math.random() * Math.PI / 16 - Math.PI / 32;
		var angle = theta0 + d_theta;
		return angle;
	}
	function colorChanged(wavelength:Number):Void {
		this.setWavelength(wavelength);
	}
	function onUnload() {
		this.clear();
		super.onUnload();
	}
}
