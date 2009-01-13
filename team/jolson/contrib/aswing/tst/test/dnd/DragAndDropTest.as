/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractButton;
import org.aswing.border.TitledBorder;
import org.aswing.Box;
import org.aswing.BoxLayout;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.dnd.DragListener;
import org.aswing.dnd.DragManager;
import org.aswing.dnd.RejectedMotion;
import org.aswing.dnd.SourceData;
import org.aswing.FlowLayout;
import org.aswing.geom.Point;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JTextArea;

/**
 * @author iiley
 */
class test.dnd.DragAndDropTest extends JFrame implements DragListener{
	
	private var tracedText:JTextArea;
	private var destPane:Container;
	private var controls:Container;
	
	public function DragAndDropTest() {
		super("DragAndDropTest");
		
		var pane:Container = this.getContentPane();
		pane.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		
		destPane = new JPanel();
		destPane.setBorder(new TitledBorder(null, "DnD container"));
		destPane.setDropTrigger(true);
		
		var conPane:Container = Box.createHorizontalBox();
		controls = new JPanel(new FlowLayout());
		controls.setBorder(new TitledBorder(null, "DnD container"));
		controls.append(creatDragableComponent());
		controls.append(creatDragableComponent());
		controls.append(creatDragableComponent());
		controls.append(creatDragableComponent());
		controls.setDropTrigger(true);
		conPane.append(controls);
		
		conPane.append(destPane);
		
		pane.append(conPane);
		tracedText = new JTextArea();
		tracedText.setDropTrigger(true);
		pane.append(new JScrollPane(tracedText));
	}
	
	private static var counter:Number = 0;
	private function creatDragableComponent():Component{
		counter ++;
		var dc:Component;
		if(counter == 3){
			dc = new JButton("Drag me " + counter);
		}else{
			dc = new JLabel("Drag me " + counter);
		}
		dc.setDragEnabled(true);
		destPane.addDragAcceptableInitiator(dc);
		dc.addEventListener(JLabel.ON_DRAG_RECOGNIZED, __startDrag, this);
		return dc;
	}
	
	private function __startDrag(dragInitiator:Component, touchedChild:Component):Void{
		traceText("try to start Drag " + dragInitiator);
		if(dragInitiator instanceof AbstractButton){
			var btn:AbstractButton = AbstractButton(dragInitiator);
			btn.getModel().setRollOver(false);
			btn.getModel().setReleased(true);
		}
		DragManager.startDrag(dragInitiator, null, null, this);
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		Stage.align = "LT";
		try{
			//trace("try ComboBoxTest");
			var p:DragAndDropTest = new DragAndDropTest();
			p.setLocation(10, 10);
			p.setSize(400, 400);
			p.show();
			//trace("done ComboBoxTest");
		}catch(e){
			trace("error : " + e);
		}
	}
	
	//--------------------------Drag Listener implementation-----------------------------
	
	public function onDragStart(dragInitiator : Component, dragSource:SourceData, pos : Point) : Void {
		traceText("onDragStart : " + dragInitiator);
		//traceText("pos : " + pos);
	}

	public function onDragEnter(dragInitiator : Component, dragSource:SourceData, pos : Point, targetComponent : Component) : Void {
		traceText("onDragEnter : " + targetComponent);
		//traceText("pos : " + pos);
	}

	public function onDragOverring(dragInitiator : Component, dragSource:SourceData, pos : Point, targetComponent : Component) : Void {
		//traceText("onDragOver : " + targetComponent);
		//traceText("pos : " + pos);
	}

	public function onDragExit(dragInitiator : Component, dragSource:SourceData, pos : Point, targetComponent : Component) : Void {
		traceText("onDragExit : " + targetComponent);
		//traceText("pos : " + pos);
	}

	public function onDragDrop(dragInitiator : Component, dragSource:SourceData, pos : Point, targetComponent : Component) : Void {
		traceText("onDragDrop : " + targetComponent);
		//traceText("pos : " + pos);
		if(targetComponent == controls || targetComponent == destPane){
			if(targetComponent.isDragAcceptableInitiator(dragInitiator)){
				var ct:Container = Container(targetComponent);
				ct.append(dragInitiator);
				ct.removeDragAcceptableInitiator(dragInitiator);
				if(targetComponent == controls){
					destPane.addDragAcceptableInitiator(dragInitiator);
				}else{
					controls.addDragAcceptableInitiator(dragInitiator);
				}
			}else{
				DragManager.setDropMotion(new RejectedMotion());
			}
		}else if(targetComponent instanceof JTextArea){
			var jta:JTextArea = JTextArea(targetComponent);
			var label:JLabel = JLabel(dragInitiator);
			var text:String = label == null ? dragInitiator.getName() : label.getText();
			traceText("hello '" + text + "' don't drop on me!");
			DragManager.setDropMotion(new RejectedMotion());
		}else{
			DragManager.setDropMotion(new RejectedMotion());
		}
	}
	
	private function traceText(text:String):Void{
		tracedText.appendText(text+"\n");
		tracedText.scrollToBottomLeft();
		trace(text);
	}
}