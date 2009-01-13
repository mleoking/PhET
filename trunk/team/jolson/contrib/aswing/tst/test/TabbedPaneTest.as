/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.border.BevelBorder;
import org.aswing.border.EmptyBorder;
import org.aswing.border.LineBorder;
import org.aswing.BorderLayout;
import org.aswing.Container;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JCheckBox;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JPanel;
import org.aswing.JRadioButton;
import org.aswing.JScrollPane;
import org.aswing.JTabbedPane;
import org.aswing.JTextArea;
import org.aswing.JTextField;
import org.aswing.SoftBox;

import test.ColorIcon;

/**
 * @author iiley
 */
class test.TabbedPaneTest extends JFrame {
	
	private var tabbedPane:JTabbedPane;
	private var statusLabel:JLabel;
	private var placementButton:JButton;
	
	public function TabbedPaneTest() {
		super("TabbedPaneTest");
		
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.TOP);
		//tabbedPane.setVerticalAlignment(JTabbedPane.BOTTOM);
		//tabbedPane.setBorder(new BevelBorder(new EmptyBorder(new LineBorder(null, ASColor.GRAY.darker(), 1), new Insets(2,2,2,2)), BevelBorder.LOWERED));
		
		var pane1:JPanel = new JPanel();
		pane1.setOpaque(true);
		var addTabButton:JButton = new JButton("add tab");
		var removeTabButton:JButton = new JButton("remove tab");
		removeTabButton.setToolTipText("Remove a random tab");
		pane1.append(new JButton("button2"));
		pane1.append(new JButton("button3"));
		tabbedPane.appendTab(pane1, "Buttons", new ColorIcon(null, ASColor.GREEN, 30, 20), "tip for tab");
		
		pane1 = new JPanel();
		pane1.append(new JLabel("label1"));
		pane1.append(new JLabel("label2"));
		pane1.append(new JLabel("label3"));
		tabbedPane.appendTab(pane1, "Labels", null, "tip2\nline2");
		
		pane1 = new JPanel(new BorderLayout());
		pane1.append(new JTextField("JTextField"), BorderLayout.NORTH);
		var p:JPanel = new JPanel();
		p.append(new JCheckBox("JCheckBox"));
		p.append(new JRadioButton("JRadioButton"));
		pane1.append(p, BorderLayout.CENTER);
		tabbedPane.appendTab(pane1, "Complex", null, "Disabled");
		
		getContentPane().append(tabbedPane, BorderLayout.CENTER);
		
		var rightPane:Container = SoftBox.createVerticalBox(10);
		statusLabel = new JLabel("First tab selected");
		statusLabel.addEventListener(JLabel.ON_RELEASE, __visibleTab, this);
		placementButton = new JButton("Change Placement");
		rightPane.append(statusLabel);
		rightPane.append(placementButton);
		rightPane.append(addTabButton);
		rightPane.append(removeTabButton);
		getContentPane().append(rightPane, BorderLayout.EAST);
		
		tabbedPane.appendTab(new JButton("Button"), "Four a long title hahhahaa");
		tabbedPane.appendTab(new JButton("Button2"), "Tab2");
		tabbedPane.appendTab(new JButton("Button3"), "Tab tab cool tab");
				
		tabbedPane.addEventListener(JTabbedPane.ON_STATE_CHANGED, __selectionChanged, this);
		placementButton.addActionListener(__changePlacement, this);
		addTabButton.addActionListener(__addTab, this);
		removeTabButton.addActionListener(__removeTab, this);
		
		tabbedPane.setEnabledAt(2, false);
	}
	
	private function __addTab():Void{
		var p:JPanel = new JPanel(new BorderLayout());
		p.setOpaque(true);
		p.append(new JLabel("A TextArea" + tabbedPane.getComponentCount()), BorderLayout.NORTH);
		p.append(new JScrollPane(new JTextArea("", 10, 10)), BorderLayout.CENTER);
		tabbedPane.insertTab(Math.floor(Math.random()*tabbedPane.getComponentCount()+0.5), p, "title with icon", new ColorIcon(null, ASColor.RED, 40*Math.random(), 40*Math.random()), "the tip");
	}
	private function __removeTab():Void{
		tabbedPane.removeTabAt(Math.floor(Math.random()*tabbedPane.getComponentCount()));
	}
	
	private function __visibleTab() : Void {
		tabbedPane.setVisibleAt(2, !tabbedPane.isVisibleAt(2));
	}
	
	private function __selectionChanged():Void{
		var index:Number = tabbedPane.getSelectedIndex();
		statusLabel.setText("Selected : " + index);
	}
	private function __changePlacement():Void{
		if(tabbedPane.getTabPlacement() == JTabbedPane.LEFT){
			tabbedPane.setTabPlacement(JTabbedPane.RIGHT);
		}else if(tabbedPane.getTabPlacement() == JTabbedPane.RIGHT){
			tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		}else if(tabbedPane.getTabPlacement() == JTabbedPane.BOTTOM){
			tabbedPane.setTabPlacement(JTabbedPane.TOP);
		}else{
			tabbedPane.setTabPlacement(JTabbedPane.LEFT);
		}
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		Stage.align = "T";
		try{
			trace("try TabbedPaneTest");
			
			var p:TabbedPaneTest = new TabbedPaneTest();
			p.setLocation(50, 50);
			p.setSize(400, 400);
			p.show();
			trace("done TabbedPaneTest");
		}catch(e){
			trace("error : " + e);
		}
	}

}