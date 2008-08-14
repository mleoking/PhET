class SoundMaker{
	private var soundOn:Boolean;
	private var ballFired_sound:Sound;
	private var maskHit_sound:Sound;
	private var ballThru_sound:Sound;
	
	function SoundMaker(){
		this.soundOn = false;
		this.ballFired_sound = new Sound();
		this.maskHit_sound = new Sound();
		this.ballThru_sound = new Sound();
		this.ballFired_sound.attachSound("ballFiredSound");
		this.maskHit_sound.attachSound("maskHitSound");
		this.ballThru_sound.attachSound("ballThruSound");
	}//end of constructor
	
	function ballFired():Void{
		if(this.soundOn){
			this.ballFired_sound.start();
		}
	}
	
	function maskHit():Void{
		if(this.soundOn){
			this.maskHit_sound.start();
			//trace("maskHit sound started.");
		}
	}
	
	function ballThru():Void{
		if(this.soundOn){
			this.ballThru_sound.start();
			//trace("ballThru sound started.");
		}
	}
	
	function turnSoundOn():Void{
		this.soundOn = true;
	}
	
	function turnSoundOff():Void{
		this.soundOn = false;
	}
	
}//end of class