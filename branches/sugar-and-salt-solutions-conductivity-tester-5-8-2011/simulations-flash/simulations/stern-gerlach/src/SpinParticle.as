class SpinParticle{
	private var angle:Number;  //angle in degrees: 0 if spin up, 180 if spin down, etc
	
	function SpinParticle(angle:Number){
		this.angle = angle;
	}
	
	function getAngle():Number{
		return this.angle;
	}
	
	function getAngleInRads():Number{
		return this.angle*Math.PI/180;
	}
	
	function setAngle(angle:Number):Void{
		this.angle = angle;
		//trace("angle of spin: "+angle);
	}
}