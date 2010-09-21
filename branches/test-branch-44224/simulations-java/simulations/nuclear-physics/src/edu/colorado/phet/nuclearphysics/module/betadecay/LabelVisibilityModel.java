package edu.colorado.phet.nuclearphysics.module.betadecay;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

public class LabelVisibilityModel extends SimpleObservable {
	
	private boolean _labelsVisible;

	public LabelVisibilityModel(boolean labelsVisible){
		_labelsVisible = labelsVisible;
	}
	
	public LabelVisibilityModel(){
		this(true);
	}
	
	public boolean isVisible() {
		return _labelsVisible;
	}

	public void setIsVisible(boolean isVisible) {
		_labelsVisible = isVisible;
		notifyObservers();
	}
}
