// AboutDialog.as



class AboutDialog {
	
	public static var dialogWidth : Number = 300;
	public static var dialogHeight : Number = 175;
	public static var topHeight = 30;
	public static var bottomHeight = 30;
	public static var checkSize = 16;
	public var fmt : TextFormat;
	public var headFormat : TextFormat;
	public var textFormat : TextFormat;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	public function addAboutMC(dialog : MovieClip) : Void {
		var aboutMC : MovieClip = dialog.createEmptyMovieClip("aboutMC", dialog.getNextHighestDepth());
		aboutMC._y = topHeight;
		
		
		var aboutInfo : TextField = aboutMC.createTextField("aboutInfo", 0, 5, 5, dialogWidth - 10, 200);
		aboutInfo.multiline = true;
		aboutInfo.html = true;
		aboutInfo.wordWrap = true;
		aboutInfo.htmlText = "Physics Education Technology project\n\nCopyright \u00A9 2004-2008 University of Colorado. Some rights reserved.\n\nVisit <a href='http://phet.colorado.edu'>http://phet.colorado.edu</a>";
		
		aboutInfo.setTextFormat(textFormat);
	}
	
	public function uncheck(checkbox : MovieClip) : Void {
		checkbox.clear();
		checkbox.lineStyle(0, 0x000000);
		checkbox.moveTo(0, 0);
		checkbox.beginFill(0xFFFFFF, 100);
		checkbox.lineTo(checkSize, 0);
		checkbox.lineTo(checkSize, checkSize);
		checkbox.lineTo(0, checkSize);
		checkbox.endFill();
	}
	
	public function fillcheck(checkbox : MovieClip) : Void {
		uncheck(checkbox);
		checkbox.moveTo(0, 0);
		checkbox.lineTo(checkSize, checkSize);
		checkbox.moveTo(0, checkSize);
		checkbox.lineTo(checkSize, 0);
	}
	
	public function addUpdatesMC(dialog : MovieClip) : Void {
		var updatesMC : MovieClip = dialog.createEmptyMovieClip("updatesMC", dialog.getNextHighestDepth());
		updatesMC._y = topHeight;
		updatesMC._visible = false;
		var labelOffset : Number = 30;
		
		var updatesCheck : MovieClip = updatesMC.createEmptyMovieClip("updatesCheck", 0);
		updatesCheck._x = (30 - checkSize) / 2;
		updatesCheck._y = (30 - checkSize) / 2;
		
		updatesCheck.onRelease = function() {
			_level0.aboutDialogMC.tentative_updates = !_level0.aboutDialogMC.tentative_updates;
			if(_level0.aboutDialogMC.tentative_updates) {
				_level0.aboutDialog.fillcheck(this);
			} else {
				_level0.aboutDialog.uncheck(this);
			}
		}
		
		var updatesInfo : TextField = updatesMC.createTextField("updatesLabel", 1, 5 + labelOffset, 5, dialogWidth - 10 - labelOffset, 100);
		updatesInfo.multiline = true;
		updatesInfo.html = true;
		updatesInfo.wordWrap = true;
		updatesInfo.htmlText = "Allow checking for updates automatically";
		updatesInfo.setTextFormat(textFormat);
		
		var updateButton : MovieClip = updatesMC.createEmptyMovieClip("updateButton", updatesMC.getNextHighestDepth());
		var updateButtonLabel : TextField = updateButton.createTextField("updateButtonLabel", 0, 5, 5, 190, 30);
		updateButtonLabel.htmlText = "Check for update...";
		updateButtonLabel.setTextFormat(fmt);
		updateButton._x = 50;
		updateButton._y = updatesInfo._y + updatesInfo.textHeight + 10 + 20;
		updateButton.lineStyle(0, 0x000000);
		updateButton.moveTo(0, 0);
		updateButton.beginFill(0xFFFFFF, 0);
		updateButton.lineTo(200, 0);
		updateButton.lineTo(200, updateButtonLabel.textHeight + 15);
		updateButton.lineTo(0, updateButtonLabel.textHeight + 15);
		updateButton.endFill();
		updateButton.onRelease = function() {
			_level0.updateHandler.manualCheck();
			_level0.aboutDialogMC._visible = false;
		}
	}
	
	public function addTrackingMC(dialog : MovieClip) : Void {
		var trackingMC : MovieClip = dialog.createEmptyMovieClip("trackingMC", dialog.getNextHighestDepth());
		trackingMC._y = topHeight;
		trackingMC._visible = false;
		var labelOffset : Number = 30;
		
		var trackingInfo : TextField = trackingMC.createTextField("trackingInfo", 0, 5, 5, dialogWidth - 10, 100);
		trackingInfo.multiline = true;
		trackingInfo.html = true;
		trackingInfo.wordWrap = true;
		trackingInfo.htmlText = "PhET is made possible by grants that require us to track anonymous usage statistics.";
		
		trackingInfo.setTextFormat(textFormat);
		
		
		var trackingCheck : MovieClip = trackingMC.createEmptyMovieClip("trackingCheck", 1);
		trackingCheck._x = (30 - checkSize) / 2;
		trackingCheck._y = (30 - checkSize) / 2 + trackingInfo.textHeight + 10;
		
		trackingCheck.onRelease = function() {
			_level0.aboutDialogMC.tentative_tracking = !_level0.aboutDialogMC.tentative_tracking;
			if(_level0.aboutDialogMC.tentative_tracking) {
				_level0.aboutDialog.fillcheck(this);
			} else {
				_level0.aboutDialog.uncheck(this);
			}
		}
		
		var trackingInfoCheck : TextField = trackingMC.createTextField("trackingInfoCheck", 2, 5 + labelOffset, 5 + trackingInfo.textHeight + 10, dialogWidth - 10 - labelOffset, 100);
		trackingInfoCheck.multiline = true;
		trackingInfoCheck.html = true;
		trackingInfoCheck.wordWrap = true;
		trackingInfoCheck.htmlText = "Allow checking for updates automatically";
		trackingInfoCheck.setTextFormat(textFormat);
		
		
		// add details button
		var trackingButton : MovieClip = trackingMC.createEmptyMovieClip("trackingButton", trackingMC.getNextHighestDepth());
		var trackingButtonLabel : TextField = trackingButton.createTextField("trackingButtonLabel", 0, 5, 5, 90, 30);
		trackingButtonLabel.htmlText = "Details...";
		trackingButtonLabel.setTextFormat(fmt);
		trackingButton._x = 100;
		trackingButton._y = trackingInfoCheck._y + trackingInfoCheck.textHeight + 10;
		trackingButton.lineStyle(0, 0x000000);
		trackingButton.moveTo(0, 0);
		trackingButton.beginFill(0xFFFFFF, 0);
		trackingButton.lineTo(100, 0);
		trackingButton.lineTo(100, trackingButtonLabel.textHeight + 15);
		trackingButton.lineTo(0, trackingButtonLabel.textHeight + 15);
		trackingButton.endFill();
		trackingButton.onRelease = function() {
			
		}
	}
	
	public function AboutDialog() {
		debug("AboutDialog initializing\n");
		
		var dialog : MovieClip;
		
		if(!_level0.aboutDialog) {
			debug("New AboutDialog\n");
			
			
			dialog = _level0.createEmptyMovieClip("aboutDialogMC", _level0.getNextHighestDepth());
			_level0.aboutDialog = this;
			_level0.aboutDialogMC = dialog;
			
			dialog.lineStyle(0, 0x000000);
			dialog.moveTo(0, 0);
			dialog.beginFill(0xFFFFFF, 85);
			dialog.lineTo(dialogWidth, 0);
			dialog.lineTo(dialogWidth, dialogHeight);
			dialog.lineTo(0, dialogHeight);
			dialog.endFill();
			
			//dialog.lineStyle(0, 0xCCCCCC);
			//dialog.moveTo(0, topHeight);
			//dialog.lineTo(dialogWidth, topHeight);
			
			fmt = new TextFormat();
			fmt.font = "_sans";
			fmt.size = 14;
			fmt.align = "center";
			
			headFormat = new TextFormat();
			headFormat.font = "_sans";
			headFormat.size = 14;
			headFormat.align = "center";
			//headFormat.bold = true;
			
			textFormat = new TextFormat();
			textFormat.font = "_sans";
			textFormat.size = 12;
			
			var aboutBackground : MovieClip = dialog.createEmptyMovieClip("aboutBackground", 0);
			aboutBackground.lineStyle(0, 0x000000);
			aboutBackground.beginFill(0xBBBBBB, 100);
			aboutBackground.lineTo(100, 0);
			aboutBackground.lineTo(100, topHeight);
			aboutBackground.lineTo(0, topHeight);
			aboutBackground.endFill();
			
			aboutBackground._visible = false;
			
			var updatesBackground : MovieClip = dialog.createEmptyMovieClip("updatesBackground", 1);
			updatesBackground.lineStyle(0, 0x000000);
			updatesBackground.beginFill(0xBBBBBB, 100);
			updatesBackground.lineTo(100, 0);
			updatesBackground.lineTo(100, topHeight);
			updatesBackground.lineTo(0, topHeight);
			updatesBackground.endFill();
			updatesBackground._x = 100;
			
			var trackingBackground : MovieClip = dialog.createEmptyMovieClip("trackingBackground", 2);
			trackingBackground.lineStyle(0, 0x000000);
			trackingBackground.beginFill(0xBBBBBB, 100);
			trackingBackground.lineTo(100, 0);
			trackingBackground.lineTo(100, topHeight);
			trackingBackground.lineTo(0, topHeight);
			trackingBackground.endFill();
			trackingBackground._x = 200;
			
			var aboutLabel : MovieClip = dialog.createEmptyMovieClip("aboutLabel", 3);
			var aboutText : TextField = aboutLabel.createTextField("aboutText", 0, 0, 4, 100, 30);
			aboutText.text = "About";
			aboutText.setTextFormat(headFormat);
			aboutLabel.hitArea = aboutBackground;
			aboutLabel.onRelease = function() {
				_level0.aboutDialogMC.aboutBackground._visible = false;
				_level0.aboutDialogMC.updatesBackground._visible = true;
				_level0.aboutDialogMC.trackingBackground._visible = true;
				
				_level0.aboutDialogMC.aboutMC._visible = true;
				_level0.aboutDialogMC.updatesMC._visible = false;
				_level0.aboutDialogMC.trackingMC._visible = false;
			}
			
			var updatesLabel : MovieClip = dialog.createEmptyMovieClip("updatesLabel", 4);
			var updatesText : TextField = updatesLabel.createTextField("updatesText", 0, 0, 4, 100, 30);
			updatesText.text = "Updates";
			updatesText.setTextFormat(headFormat);
			updatesLabel._x = 100;
			updatesLabel.hitArea = updatesBackground;
			updatesLabel.onRelease = function() {
				_level0.aboutDialogMC.aboutBackground._visible = true;
				_level0.aboutDialogMC.updatesBackground._visible = false;
				_level0.aboutDialogMC.trackingBackground._visible = true;
				
				_level0.aboutDialogMC.aboutMC._visible = false;
				_level0.aboutDialogMC.updatesMC._visible = true;
				_level0.aboutDialogMC.trackingMC._visible = false;
			}
			
			var trackingLabel : MovieClip = dialog.createEmptyMovieClip("trackingLabel", 5);
			var trackingText : TextField = trackingLabel.createTextField("trackingText", 0, 0, 4, 100, 30);
			trackingText.text = "Tracking";
			trackingText.setTextFormat(headFormat);
			trackingLabel._x = 200;
			trackingLabel.hitArea = trackingBackground;
			trackingLabel.onRelease = function() {
				_level0.aboutDialogMC.aboutBackground._visible = true;
				_level0.aboutDialogMC.updatesBackground._visible = true;
				_level0.aboutDialogMC.trackingBackground._visible = false;
				
				_level0.aboutDialogMC.aboutMC._visible = false;
				_level0.aboutDialogMC.updatesMC._visible = false;
				_level0.aboutDialogMC.trackingMC._visible = true;
			}
			
			
			addAboutMC(dialog);
			addUpdatesMC(dialog);
			addTrackingMC(dialog);
			
			
			dialog.moveTo(0, dialogHeight - bottomHeight);
			dialog.lineTo(dialogWidth, dialogHeight - bottomHeight);
			dialog.moveTo(150, dialogHeight - bottomHeight);
			dialog.lineTo(150, dialogHeight);
			
			var okLabel : MovieClip = dialog.createEmptyMovieClip("okLabel", dialog.getNextHighestDepth());
			var okText : TextField = okLabel.createTextField("okText", 0, 0, 4, 150, 30);
			okText.text = "OK";
			okText.setTextFormat(fmt);
			okLabel._x = 0;
			okLabel._y = dialogHeight - bottomHeight;
			okLabel.beginFill(0x000000, 0);
			okLabel.moveTo(0,0);
			okLabel.lineTo(150, 0);
			okLabel.lineTo(150, bottomHeight);
			okLabel.lineTo(0, bottomHeight);
			okLabel.endFill();
			okLabel.onRelease = function() {
				_level0.aboutDialogMC._visible = false;
				_level0.preferences.setTracking(_level0.aboutDialogMC.tentative_updates, _level0.aboutDialogMC.tentative_tracking);
			}
			
			
			var cancelLabel : MovieClip = dialog.createEmptyMovieClip("cancelLabel", dialog.getNextHighestDepth());
			var cancelText : TextField = cancelLabel.createTextField("okText", 0, 0, 4, 150, 30);
			cancelText.text = "Cancel";
			cancelText.setTextFormat(fmt);
			cancelLabel._x = 150;
			cancelLabel._y = dialogHeight - bottomHeight;
			cancelLabel.beginFill(0x000000, 0);
			cancelLabel.moveTo(0,0);
			cancelLabel.lineTo(150, 0);
			cancelLabel.lineTo(150, bottomHeight);
			cancelLabel.lineTo(0, bottomHeight);
			cancelLabel.endFill();
			cancelLabel.onRelease = function() {
				_level0.aboutDialogMC._visible = false;
			}
			
		} else {
			dialog = _level0.aboutDialogMC;
		}
		
		dialog._x = (Stage.width - dialogWidth) / 2;
		dialog._y = (Stage.height - dialogHeight) / 2;
		
		dialog.tentative_tracking = _level0.preferences.allowTracking();
		dialog.tentative_updates = _level0.preferences.checkForUpdates();
		
		if(dialog.tentative_updates) {
			fillcheck(dialog.updatesMC.updatesCheck);
		} else {
			uncheck(dialog.updatesMC.updatesCheck);
		}
		
		if(dialog.tentative_tracking) {
			fillcheck(dialog.trackingMC.trackingCheck);
		} else {
			uncheck(dialog.trackingMC.trackingCheck);
		}
		
		dialog._visible = true;
	}
}