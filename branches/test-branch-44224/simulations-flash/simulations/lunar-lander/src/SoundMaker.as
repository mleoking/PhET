class SoundMaker{
	var soundTarget:MovieClip;
	var soundTarget2:MovieClip;
	var soundOn:Boolean;
	var sideThrust_sound:Sound;
	var fuelAlarm_sound:Sound;
	var thrust_sound:Sound;
	var adjustThrust_sound:Sound;
	var fullThrust_sound:Sound;
	var alarm_sound:Sound;
	var explode_sound:Sound;

	
	//constructor
	function SoundMaker(soundTarget:MovieClip, soundTarget2:MovieClip ){
		soundOn = true;
		sideThrust_sound= new Sound(soundTarget);
		fuelAlarm_sound  = new Sound(soundTarget);
		thrust_sound  = new Sound(soundTarget);
		adjustThrust_sound = new Sound(soundTarget2);
		fullThrust_sound  = new Sound(soundTarget);
		alarm_sound  = new Sound(soundTarget);
		explode_sound = new Sound(soundTarget);
		sideThrust_sound.attachSound("sideThrust");
		fuelAlarm_sound.attachSound("fuelAlarm");
		//fullThrust_sound.attachSound("fullThrust");
		thrust_sound.attachSound("thrust");
		adjustThrust_sound.attachSound("thrust");
		adjustThrust_sound.onSoundComplete = function(){
				//XX Error repeats at random intervals
				//trace("adjustThrust sound repeated " + getTimer()/1000);
				this.start();
		}
		
		explode_sound.attachSound("explode");
	}//end of constructor
	
	function soundAlarm(loop:Number):Void{
		playSound(fuelAlarm_sound, loop);
	}
	
	function soundThrust():Void{
		if(soundOn){
			thrust_sound.start(0.1,1);
			thrust_sound.onSoundComplete = function(){
				//trace("thrust sounded repeated " + getTimer()/1000);
				this.start(0.1,1);
			}
		}
	}
	
	function soundThrustOff():Void{
		thrust_sound.stop();
	}
	
	function adjustThrustSound(loudness:Number):Void{
		
		if(soundOn){
			//adjustThrust_sound.stop();
			adjustThrust_sound.setVolume(loudness/2);
			adjustThrust_sound.start();
		}
	}
	
	function adjustThrustSoundOff():Void{
		adjustThrust_sound.stop();
	}
	
	function soundSideThrust(loop:Number):Void{
		playSound(sideThrust_sound,loop);
	}
	
	function soundExplosion(loop:Number):Void{
		playSound(explode_sound,loop);
	}
	
	function playSound(mySound:Sound,loop:Number):Void{
		if(soundOn){
			mySound.start(0,loop);
		}
	}
}//end of class SoundMaker