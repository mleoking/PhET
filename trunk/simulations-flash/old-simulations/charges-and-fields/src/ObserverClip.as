class ObserverClip extends MovieClip implements Observer{
	
	public function update(model:ChargeGroup):Void{	//
	
		
		//var EFAC:Number = 0.2046;
		var E:Array = model.getE(this._parent._x,this._parent._y);
		this._parent.setLength(E[0]);
		this._parent.setAngle(E[1]);
		/*  
		this._rotation = 0;
		this._width = E[0];
		this._rotation = E[1];
		this._parent.sensorBody_mc.E_txt.text = String(0.1*Math.round(10*EFAC*E[0]))+" V/m";
		this._parent.sensorBody_mc.theta_txt.text = String(0.1*Math.round(10*-E[1]))+" deg";
		*/
	}

}//end of observerClip