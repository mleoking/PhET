class Module {
	var elements:Array;
	
	function Module() {
		elements = new Array();
	}
	
	function addElement( element:MovieClip ) {
		elements.push(element);
	}
	
	function deactivate():Void {
		this.setVisible(false);
	}
	
	function activate():Void {
		this.setVisible( true );
	}
	
	private function setVisible( isVisible:Boolean):Void{
		for( var i = 0; i < elements.length; i++ ){
			elements[i]._visible = isVisible;
		}
	}
}
		