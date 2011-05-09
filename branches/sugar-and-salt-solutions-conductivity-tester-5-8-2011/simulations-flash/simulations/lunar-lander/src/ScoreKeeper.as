class ScoreKeeper{
	var score:Number;
	var nbrSoftLandings:Number;
	var nbrHardLandings:Number;
	var spotScore_arr:Array;
	var landerClip:MovieClip;
	var landingSpots:MovieClip;
	var theView:MovieClip;
	var myLander:Lander;
	
	
	function ScoreKeeper(theView:MovieClip, myLander:Lander){
		this.theView = theView;
		this.myLander = myLander;
		this.landingSpots = theView.landingSpots_mc;
		this.landerClip = theView.landerHolder_mc.lander_mc.landerBody_mc;
		spotScore_arr = new Array(0,30,10,10,5,10,10,5,10,10,5,10,5,10,50,30)
		//trace(this.theView._name);
		//trace(this.landerClip._name);
		initialize();
	}
	
	function initialize():Void{
		this.score = 0;
		this.nbrSoftLandings = 0;
		this.nbrHardLandings = 0;
		for(var clip in this.landingSpots){
			this.landingSpots[clip].landedOn = false;
			this.landingSpots[clip]._visible = false;
			//this.landingSpots[clip].testMe = function(){
				//trace("message from " + this);
			//}
		}
	}
	
	function updateLandings():Void{
		//trace("updateLandings called");
		for(var clip in this.landingSpots){
			if(this.landerClip.hitTest(this.landingSpots[clip])){
				//trace("Hit on " + this.landingSpots[clip]._name);
				if(this.landingSpots[clip].landedOn == false){
					this.landingSpots[clip].landedOn = true;
					var spotNbr:Number = Number(this.landingSpots[clip]._name.slice(4));
					//trace("crashState:" + this.myLander.crashState);
					if(this.myLander.crashState != "crashLanded"){
						this.score += spotScore_arr[spotNbr];
						if(this.myLander.crashState == "softLanded"){
							this.nbrSoftLandings += 1;
							this.score += 10;
						}else if(this.myLander.crashState == "hardLanded"){
							this.nbrHardLandings += 1;
						}
						//trace("spotNbr:" + spotNbr + "   Score:" + score);
						//this.landingSpots[clip]._visible = true;
					}
				}
			}
		}
	}//end of updateLandings()
	
	
}//end of class ScoreKeeper