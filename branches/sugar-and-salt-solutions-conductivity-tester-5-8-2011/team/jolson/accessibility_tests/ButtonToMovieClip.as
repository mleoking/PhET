stop();

var over : Boolean = false;

this.onRollOver = function() {
	over = true;
	this.gotoAndStop("over");
}

this.onRollOut = function() {
	over = false;
	this.gotoAndStop("up");
}

this.onPress = function() {
	this.gotoAndStop("down");
}

this.onRelease = function() {
	if(over) {
		this.gotoAndStop("over");
	} else {
		this.gotoAndStop("up");
	}
}

this.onReleaseOutside = function() {
	this.gotoAndStop("up");
}