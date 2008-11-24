/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.border.TitledBorder;
import org.aswing.BoxLayout;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.dnd.DragManager;
import org.aswing.dnd.SourceData;
import org.aswing.geom.Point;
import org.aswing.JFrame;
import org.aswing.JList;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JTextArea;

/**
 * @author iiley
 */
class test.dnd.DnDListTest extends JFrame {
	
	private var tracedText:JTextArea;
	
	public function DnDListTest() {
		super("Drag and Drop List Test");
		
		var pane:Container = new JPanel();
		pane.setLayout(new BoxLayout(BoxLayout.X_AXIS));
		
		var list1:JList = new JList(["0", "1" , "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"]);
		list1.setAutoDragAndDropType(JList.DND_MOVE);
		var sp1:JScrollPane = new JScrollPane(list1);
		sp1.setBorder(new TitledBorder(null, "list1"));
		var list2:JList = new JList(["one", "two" , "three", "four", "yes", "this", "is", "just", "a", "test", "!!", "..."]);
		list2.setAutoDragAndDropType(JList.DND_MOVE);
		var sp2:JScrollPane = new JScrollPane(list2);
		sp2.setBorder(new TitledBorder(null, "list2"));
		
		var list3:JList = new JList(["0", "1" , "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"]);
		list3.setAutoDragAndDropType(JList.DND_MOVE);
		var sp3:JScrollPane = new JScrollPane(list3);
		sp3.setBorder(new TitledBorder(null, "list3"));
		var list4:JList = new JList(["one", "two" , "three", "four", "yes", "this", "is", "just", "a", "test", "!!", "..."]);
		list4.setAutoDragAndDropType(JList.DND_MOVE);
		var sp4:JScrollPane = new JScrollPane(list4);
		sp4.setBorder(new TitledBorder(null, "list4"));
				
		list2.addDragAcceptableInitiator(list1);
		list3.addDragAcceptableInitiator(list1);
		list3.addDragAcceptableInitiator(list2);
		list4.addDragAcceptableInitiator(list1);
		list4.addDragAcceptableInitiator(list2);
		list4.addDragAcceptableInitiator(list3);
		list4.addDragAcceptableInitiator(list4);
		
		pane.append(sp1);
		pane.append(sp2);
		pane.append(sp3);
		pane.append(sp4);
		
		getContentPane().setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		getContentPane().append(pane);
		tracedText = new JTextArea("list1 accept drop nothing,\n" +
			"list2 accept dropped list1 items,\n" +
			"list3 accept dropped list1 and list2 items,\n" +
			"list4 accept dropped list1,2,3 and itself items\n" +
			"Drop list items to here to see some thing\n");
		tracedText.setDropTrigger(true);
		getContentPane().append(new JScrollPane(tracedText));
		
		tracedText.addEventListener(JTextArea.ON_DRAG_DROP, __dropOnText, this);
	}
	
	private function __dropOnText(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		tracedText.appendText(sourceData.getName() + "'s indices dropped here : " + sourceData.getData() + "\n");
	}

	public static function main():Void{
		Stage.scaleMode = "noScale";
		Stage.align = "LT";
		try{
			//trace("try ComboBoxTest");
			var p:DnDListTest = new DnDListTest();
			p.setLocation(0, 10);
			p.setSize(600, 360);
			p.show();
			//trace("done ComboBoxTest");
		}catch(e){
			trace("error : " + e);
		}
	}
}