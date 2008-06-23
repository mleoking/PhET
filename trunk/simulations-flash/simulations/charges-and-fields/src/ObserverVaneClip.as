class ObserverVaneClip extends MovieClip implements Observer {
	public static var showDirAndMag:Boolean;
	
	public function update(model:ChargeGroup):Void {
		//
		var E:Array = model.getE(this._x, this._y);
		if (showDirAndMag) {
			this._rotation = 0;
			var intensity = E[0];
			//if(intensity > 10){
			this._alpha = intensity;
			//}else{this._alpha = 0;}
		}else{this._alpha = 100;}
		this._rotation = E[1];
		//trace(this._name + " updated. EAng is "+E[1]);
	}
}
//end of observerClip
