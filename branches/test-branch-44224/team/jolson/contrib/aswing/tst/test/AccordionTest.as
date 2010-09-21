/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.border.LineBorder;
import org.aswing.BorderLayout;
import org.aswing.FlowLayout;
import org.aswing.JAccordion;
import org.aswing.JButton;
import org.aswing.JCheckBox;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JPanel;
import org.aswing.JRadioButton;
import org.aswing.JScrollPane;
import org.aswing.JTextArea;
import org.aswing.JTextField;

import test.ColorIcon;

/**
 *
 * @author iiley
 */
class test.AccordionTest extends JFrame {
	
	private var accordion:JAccordion;
	private var statusLabel:JLabel;
	
	public function AccordionTest() {
		super("AccordionTest");
		
		accordion = new JAccordion();
		accordion.setBorder(new LineBorder(null, ASColor.RED, 1));
		accordion.setHorizontalTextPosition(JAccordion.LEFT);
		
		var pane1:JPanel = new JPanel();
		pane1.setBorder(new LineBorder(null, ASColor.BLUE));
		pane1.setOpaque(true);
		var addTabButton:JButton = new JButton("add tab");
		addTabButton.addActionListener(__addTab, this);
		pane1.append(addTabButton);
		pane1.append(new JButton("button2"));
		pane1.append(new JButton("button3"));
		accordion.appendTab(pane1, "Buttons");
		
		pane1 = new JPanel();
		pane1.setOpaque(true);
		pane1.append(new JLabel("label1"));
		pane1.append(new JLabel("label2"));
		pane1.append(new JLabel("label3"));
		accordion.appendTab(pane1, "Labels");
		
		pane1 = new JPanel(new BorderLayout());
		pane1.setBorder(new LineBorder(null, ASColor.BLUE));
		pane1.setOpaque(true);
		pane1.append(new JTextField("JTextField"), BorderLayout.NORTH);
		var p:JPanel = new JPanel();
		p.append(new JCheckBox("JCheckBox"));
		p.append(new JRadioButton("JRadioButton"));
		pane1.append(p, BorderLayout.CENTER);
		accordion.appendTab(pane1, "Complex");
		
		getContentPane().append(accordion, BorderLayout.CENTER);
		
		var labelPane:JPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		statusLabel = new JLabel("First tab selected");
		statusLabel.setTriggerEnabled(true);
		statusLabel.setUseHandCursor(true);
		statusLabel.addEventListener(JLabel.ON_RELEASE, __visibleTab, this);
		labelPane.append(statusLabel);
		getContentPane().append(labelPane, BorderLayout.EAST);
		
		accordion.addEventListener(JAccordion.ON_STATE_CHANGED, __selectionChanged, this);
		//accordion.setEnabledAt(0, false);
		accordion.setVisibleAt(1, false);
	}

	private function __visibleTab() : Void {
		accordion.setVisibleAt(1, !accordion.isVisibleAt(1));
	}
	
	
	private function __selectionChanged():Void{
		var index:Number = accordion.getSelectedIndex();
		statusLabel.setText("Selected : " + index);
	}
	
	private function __addTab():Void{
		var p:JPanel = new JPanel(new BorderLayout());
		p.setOpaque(true);
		p.append(new JLabel("A TextArea"), BorderLayout.NORTH);
		p.append(new JScrollPane(new JTextArea("", 10, 10)), BorderLayout.CENTER);
		accordion.appendTab(p, "title with icon", new ColorIcon(null, ASColor.RED, 40*Math.random(), 40*Math.random()), "the tip");
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		Stage.align = "T";
		try{
			trace("try AccordionTest");
			
			var p:AccordionTest = new AccordionTest();
			p.setLocation(50, 50);
			p.setSize(400, 400);
			p.show();
			trace("done AccordionTest");
		}catch(e){
			trace("error : " + e);
		}
	}
}
