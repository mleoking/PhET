import org.aswing.BorderLayout;
import org.aswing.JButton;
import org.aswing.JComboBox;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JTextField;
import org.aswing.VectorListModel;

/**
 * @author iiley
 */
class test.ComboBoxTest extends JFrame {
	
	private var box:JComboBox;
	private var statusText:JTextField;
	private var inputText:JTextField;
	private var model:VectorListModel;
	
	public function ComboBoxTest() {
		super("ComboBoxTest");
		
		var listData:Array = ["11111111111111",
								"22222222222222222",
								"3333333333333333",
								"44444444444444444---------5555555555555555",
								"55555555555555555",
								"666666666666666",
								"22222222222222222",
								"3333333333333333",
								"44444444444444444",
								"55555555555555555"];
		model = new VectorListModel(listData);
		
		box = new JComboBox(model);
		box.setName("box");
		box.setPreferredSize(200, box.getPreferredSize().height);
		box.setEditable(true);
		box.setMaximumRowCount(10);
		box.setSelectedIndex(0);
		box.addActionListener(__comboBoxActed, this);
		
		var pane:JPanel = new JPanel();
		pane.append(box);
		
		var box2:JComboBox = new JComboBox(model);
		box2.setName("box2");
		box2.setPreferredSize(150, box.getPreferredSize().height);
		box2.setMaximumRowCount(3);
		box2.setSelectedIndex(9);
		box2.addActionListener(__comboBoxActed, this);
		pane.append(box2);
		
		var box3:JComboBox = new JComboBox(model);
		box3.setName("box3");
		box3.setPreferredSize(150, box.getPreferredSize().height);
		box3.setMaximumRowCount(3);
		box3.setSelectedIndex(9);
		box3.setEnabled(false);
		box3.addActionListener(__comboBoxActed, this);
		pane.append(box3);
		
		getContentPane().append(pane, BorderLayout.NORTH);
		
		inputText = new JTextField(null, 10);
		var button:JButton = new JButton("Add Item");
		button.addActionListener(__addItem, this);
		pane = new JPanel();
		pane.append(inputText);
		pane.append(button);
		getContentPane().append(pane, BorderLayout.CENTER);
		
		statusText = new JTextField();
		getContentPane().append(statusText, BorderLayout.SOUTH);
	}
	
	private function __comboBoxActed(eventBox:JComboBox):Void{
		statusText.setText(eventBox.getName() + " selected : " + eventBox.getSelectedItem());
	}
	private function __addItem():Void{
		model.append(inputText.getText());
		box.setSelectedItem(model.last());
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		//Stage.align = "T";
		try{
			trace("try ComboBoxTest");
			
			var p:ComboBoxTest = new ComboBoxTest();
			p.setLocation(50, 50);
			p.setSize(400, 400);
			p.show();
			trace("done ComboBoxTest");
		}catch(e){
			trace("error : " + e);
		}
	}

}