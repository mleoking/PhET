class InternationalView{
	var myControlPanelView:ControlPanelView;
	function InternationalView(myControlPanelView:ControlPanelView){
		this.myControlPanelView = myControlPanelView;
		var panel_mc:MovieClip = this.myControlPanelView.getControlPanelClip();
		trace("startButton text frame1 before:"+panel_mc.dropButton_mc.startButton1_txt.text);
		panel_mc.dropButton_mc.startButton1_txt.text = "Go1";//_global.simStrings.get( "startButton" );
		//panel_mc.dropButton_mc.startButton1_txt.selectable = false;
		panel_mc.dropButton_mc.gotoAndStop(3);
		trace("startButton text frame1 after:"+panel_mc.dropButton_mc.startButton1_txt.text);
		panel_mc.dropButton_mc.startButton2_txt.text = "Go2";//_global.simStrings.get( "startButton" );
		trace("startButton text frame3 before:"+panel_mc.dropButton_mc.startButton3_txt.text);
		panel_mc.dropButton_mc.startButton3_txt.text = "Go3";//_global.simStrings.get( "startButton" );
		trace("startButton text frame3  after:"+panel_mc.dropButton_mc.startButton3_txt.text);
		//trace("InternationalView constructor called");
	}//end of constructor
}//end of class