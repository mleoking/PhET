class Photon extends MovieClip {
	// This shows how a class can be defined in the main timeline, instead
	private var ds:Number = 15;
	private var s:Number = 30;
	private var rgb:Number;
	private var theta:Number;
	//Object.registerClass("PhotonSymbol", Photon);
	private static var instances:Array = new Array();
	static function deleteInstance(p) {
		for (var i = 0; i < Photon.instances.length; i++) {
			if (Photon.instances[i] == p) {
				Photon.instances.splice(i, 1);
			}
		}
	}
	static function getInstances() {
		return instances;
	}
	static function getPhotonsWithinBounds(x, y, w, h) {
		var result = new Array();
		for (var i = 0; i < Photon.instances.length; i++) {
			var p = Photon.instances[i];
			if (p.x >= x && p.x <= x + w && p.y >= y && p.y <= y + h) {
				result.push(p);
			}
		}
		return result;
	}
	function Photon(x, y, theta, rgb) {
		this._x = x;
		this._y = y;
		this.theta = theta;
		this.rgb = rgb;
		Photon.instances.push(this);
	}
	function setRgb(rgb) {
		this.rgb = rgb;
	}
	function stepInTime() {
		this._x += ds * Math.cos(this.theta);
		this._y += ds * Math.sin(this.theta);
	}
	function paint(g) {
		this.stepInTime();
		g.lineStyle(1, this.rgb, 100);
		g.moveTo(this._x, this._y);
		g.lineTo(this._x - s * Math.cos(this.theta), this._y - s * Math.sin(this.theta));
	}
}
