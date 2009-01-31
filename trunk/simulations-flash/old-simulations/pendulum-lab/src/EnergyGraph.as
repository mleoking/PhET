class EnergyGraph{
	private var clip:MovieClip;
	private var pixelsPerJoule:Number;
	private var pendulum:Object;
	private var visibility:Boolean;		//true if graph is visible
	
	
	function EnergyGraph(pendulum:Object){
		this.pendulum = pendulum;
		var pendulumNbr:Number = this.pendulum.getLabelNbr();
		this.setVisibility(false);
		this.pendulum.registerGraphView(this);
		this.pixelsPerJoule = 100;
		this.clip = _root.attachMovie("energyGraph", "energyGraph"+pendulumNbr+"_mc", _root.getNextHighestDepth());
		Util.setXYPosition(this.clip, 0.7*Util.STAGEW, 0.85*Util.STAGEH);
		this.clip.yAxis_txt.text = "energy of " + pendulumNbr;
	}//end of constructor
	
	
	function update():Void{
		if(this.visibility){
			this.clip.KEBar_mc._height = this.pixelsPerJoule*this.pendulum.KE;
			this.clip.PEBar_mc._height = this.pixelsPerJoule*this.pendulum.PE;
			this.clip.thermalBar_mc._height = this.pixelsPerJoule*this.pendulum.thermalE;
			this.clip.totKEBar_mc._height = this.clip.KEBar_mc._height;
			this.clip.totPEBar_mc._y = -this.clip.KEBar_mc._height;
			this.clip.totPEBar_mc._height = this.clip.PEBar_mc._height;
			this.clip.totThermalBar_mc._height = this.clip.thermalBar_mc._height;
			this.clip.totThermalBar_mc._y = -this.clip.KEBar_mc._height - this.clip.PEBar_mc._height;
		}
	}
	
	function setVisibility(tOrF:Boolean):Void{
		this.visibility = tOrF;
		this.clip._visible = this.visibility;
	}
	
}//end of class