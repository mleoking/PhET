




class TabEntry {
	public var control : Object;
	public var highlight : String;
	public var keys : Object;
	public function TabEntry(obj : Object, high : String) {
		control = obj;
		highlight = high;
		if(highlight == undefined) {
			highlight = TabHandler.HIGHLIGHT_GLOBAL;
		}
		keys = new Object();
	}
}
