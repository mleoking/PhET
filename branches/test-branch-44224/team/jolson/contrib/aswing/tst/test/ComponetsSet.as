/*
 CopyRight @ 2005 XLands.com INC. All rights reserved.
*/

import org.aswing.ASWingUtils;
import org.aswing.border.SimpleTitledBorder;
import org.aswing.BorderLayout;
import org.aswing.Container;
import org.aswing.JAccordion;
import org.aswing.JButton;
import org.aswing.JCheckBox;
import org.aswing.JComboBox;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JPanel;
import org.aswing.JProgressBar;
import org.aswing.JRadioButton;
import org.aswing.JScrollPane;
import org.aswing.JSeparator;
import org.aswing.JSlider;
import org.aswing.JTabbedPane;
import org.aswing.JTable;
import org.aswing.JTextArea;
import org.aswing.JTextField;
import org.aswing.JToggleButton;
import org.aswing.JTree;
import org.aswing.JWindow;
import org.aswing.SoftBox;
import org.aswing.table.DefaultTableModel;

/**
 * @author iiley
 */
class test.ComponetsSet extends JWindow {
	
	public function ComponetsSet() {
		super();
		
		var frame:JFrame = new JFrame(this, "Title", false);
		frame.setBounds(400, 100, 150, 150);
		frame.show();
		
		var content:Container = SoftBox.createVerticalBox(4);
		
		var button:JButton = new JButton("Button");
		button.setToolTipText("Button ToolTip!");
		var ratio:JRadioButton = new JRadioButton("Radio");
		ratio.setToolTipText("Radio ToolTip!");
		var check:JCheckBox = new JCheckBox("CheckBox");
		check.setToolTipText("CheckBox ToolTip!");
		var togle:JToggleButton = new JToggleButton("ToggleButton");
		togle.setToolTipText("ToggleButton ToolTip!");
		var p:Container = new JPanel();
		p.append(button);
		p.append(ratio);
		p.append(check);
		p.append(togle);
		p.setBorder(new SimpleTitledBorder(null, "Buttons"));
		content.append(p);
		content.append(new JSeparator(JSeparator.HORIZONTAL));
		
		var label:JLabel = new JLabel("Label");
		var textField:JTextField = new JTextField("TextField", 10);
		var textArea:JTextArea = new JTextArea("TextArea", 2, 10);
		p = new JPanel();
		p.append(label);
		p.append(textField);
		p.append(textArea);
		p.setBorder(new SimpleTitledBorder(null, "Texts"));
		content.append(p);
		content.append(new JSeparator(JSeparator.HORIZONTAL));
		
		var ta:JTextArea = new JTextArea("One of the previous reviewers wrote that there appeared to be no middle\n ground for opinions of Love Story; one loved it or hated it.\n But there seems to be a remarkable distribution of opinions\n throughout the scale of 1 to 10. For me, \nthis movie rated a 4. There are some beautiful scenes and locations,\n and Ray Milland turns in a fabulous job as Oliver's father. \nBut the movie did not do a particularly compelling job of telling its story, \nand the story was not so unique as to warrant multiple viewings, \nat least, not for me. \nI may be a bit of a snob, but I tend to avoid movies with Ryan O'Neal -- I still haven't seen Barry Lyndon -- because most of them, \nbut not all, \nare ruined for me by his presence. \nThe lone exception is What's Up, Doc?, \nin which his straight performance is the perfect underlining for Barbra Streisand's goofball protagonist -- and, \nnot coincidentally, \nhe takes a shot at Love Story for good measure! McGraw and O'Neal tend to mug their lines, \nrather than act them.");
		var scrollBar:JScrollPane = new JScrollPane(ta);
		scrollBar.setPreferredSize(150, 100);
		var slider:JSlider = new JSlider(JSlider.HORIZONTAL);
		slider.setPaintTicks(true);
		slider.setPaintTrack(true);
		slider.setPreferredHeight(50);
		slider.setMajorTickSpacing(25);
		slider.setMinorTickSpacing(5);
		var progress:JProgressBar = new JProgressBar(JProgressBar.HORIZONTAL);
		progress.setIndeterminate(true);
		p = SoftBox.createVerticalBox(4);
		p.append(ASWingUtils.createPaneToHold(scrollBar));
		p.append(ASWingUtils.createPaneToHold(slider));
		p.append(ASWingUtils.createPaneToHold(progress));
		p.setBorder(new SimpleTitledBorder(null, "Bars"));
		content.append(p);
		content.append(new JSeparator(JSeparator.HORIZONTAL));
		
		var comboBox:JComboBox = new JComboBox(["I love", "Comeny", "very", "much", "!!!!"]);
		comboBox.setPreferredWidth(100);
		var tabbedPane:JTabbedPane = new JTabbedPane();
		tabbedPane.appendTab(new JLabel("content1"), "Tab1");
		tabbedPane.appendTab(new JLabel("content2"), "Tab2");
		tabbedPane.appendTab(new JLabel("content3"), "Tab3");
		var accordion:JAccordion = new JAccordion();
		accordion.appendTab(new JLabel("content 1"), "accordion header1");
		accordion.appendTab(new JLabel("content 2"), "accordion header2");
		accordion.appendTab(new JLabel("content 3"), "accordion header3");
		p = SoftBox.createVerticalBox(4);
		p.append(ASWingUtils.createPaneToHold(comboBox));
		p.append(ASWingUtils.createPaneToHold(tabbedPane));
		p.append(accordion);
		p.setBorder(new SimpleTitledBorder(null, "Others"));
		p.append(new JSeparator(JSeparator.HORIZONTAL));
		var tree:JTree = new JTree();
		tree.setPreferredSize(tree.getPreferredWidth(), 70);
		p.append(new JScrollPane(tree));
		p.append(new JSeparator(JSeparator.HORIZONTAL));
		
		var data:Array = [["iiley", 100, true], 
		  ["I dont know who", -12, false],
		  ["A little cute girl", 98765, false]];
		var column:Array = ["name", "score", "male"];
		var model:DefaultTableModel = (new DefaultTableModel()).initWithDataNames(data, column);
		p.append(new JScrollPane(new JTable(model)));
		
		content.append(p);
		content.append(new JSeparator(JSeparator.HORIZONTAL));
		getContentPane().append(content, BorderLayout.CENTER);
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		Stage.align = "TL";
		try{
			//UIManager.setLookAndFeel(new HightecLAF());
			trace("try ComponetsSet");
			//UIManager.setLookAndFeel(new XlandsLookAndFeel());
			var t:ComponetsSet = new ComponetsSet();
			t.setSize(400, 800);
			t.show();
			trace("done ComponetsSet");
		}catch(e){
			trace("error : " + e);
		}
	}
}