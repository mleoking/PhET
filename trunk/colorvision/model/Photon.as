class Photon {
	private var xLoc:Number;
	private var yLoc:Number;
	private var ds:Number = 10;
	private var s:Number = 15;
	private var rgb:Number;
	private var theta:Number;
	private static var instances:Array = new Array();
	static function deleteInstance(p) {
		for (var i = 0; i < Photon.instances.length; i++) {
                trace("photon: " + i );
			if (Photon.instances[i] == p) {
                        trace("photon: found");
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
		this.xLoc = x;
		this.yLoc = y;
		this.theta = theta;
		this.rgb = rgb;
		Photon.instances.push(this);
	}
	function getX():Number{
		return xLoc;
	}
	function setRgb(rgb) {
		this.rgb = rgb;
	}
	function getRgb():Number{
		return rgb;
	}
	function stepInTime() {
		this.xLoc += ds * Math.cos(this.theta);
		this.yLoc += ds * Math.sin(this.theta);
	}
	function paint(g) {
		this.stepInTime();
		g.lineStyle(1, this.rgb, 100);
		g.moveTo(this.xLoc, this.yLoc);
		g.lineTo(this.xLoc - s * Math.cos(this.theta), this.yLoc - s * Math.sin(this.theta));
	}
}
