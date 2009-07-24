package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;


public class SoundState extends SimpleObservable {

	// Default to off.
	private boolean isEnabled = false;

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public void play(String soundName){
		if (isEnabled()){
			NuclearPhysicsResources.getResourceLoader().getAudioClip( soundName ).play();
		}
	}
}
