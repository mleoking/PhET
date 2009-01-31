/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.BorderLayout;
import org.aswing.ButtonGroup;
import org.aswing.JCheckBoxMenuItem;
import org.aswing.JFrame;
import org.aswing.JMenu;
import org.aswing.JMenuBar;
import org.aswing.JMenuItem;
import org.aswing.JOptionPane;
import org.aswing.JRadioButtonMenuItem;
import org.aswing.JScrollPane;
import org.aswing.JSeparator;
import org.aswing.JTextArea;
import org.aswing.KeyboardManager;
import org.aswing.KeySequence;
import org.aswing.KeyStroke;

/**
 * @author iiley
 */
class test.menu.NormalMenuTest extends JFrame {
	
	private var textArea:JTextArea; 
	private var openItem:JMenuItem;
	private var fileMenu:JMenu;
	
	public function NormalMenuTest() {
		super("NormalMenuTest");
		
		var bar:JMenuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		var newMenu:JMenu = new JMenu("New");
		fileMenu.append(newMenu);
		newMenu.addMenuItem("AS2 File").addActionListener(__menuItemAct, this);
		newMenu.addMenuItem("AS3 File").addActionListener(__menuItemAct, this);
		newMenu.addMenuItem("haXe File").addActionListener(__menuItemAct, this);
		openItem = fileMenu.addMenuItem("Open...");
		openItem.addActionListener(__menuItemAct, this);
		openItem.setAccelerator(new KeySequence(KeyStroke.VK_CONTROL, KeyStroke.VK_O));
		fileMenu.append(new JSeparator(JSeparator.HORIZONTAL));
		fileMenu.addMenuItem("Save").addActionListener(__menuItemAct, this);
		fileMenu.addMenuItem("Save").addActionListener(__menuItemAct, this);
		fileMenu.addMenuItem("Close").addActionListener(__menuItemAct, this);
		fileMenu.append(new JSeparator(JSeparator.HORIZONTAL));
		fileMenu.addMenuItem("Exit").addActionListener(__menuItemAct, this);
		bar.append(fileMenu);
		
		var editMenu:JMenu = new JMenu("Edit");
		editMenu.addMenuItem("Copy").addActionListener(__menuItemAct, this);
		editMenu.addMenuItem("Cut").addActionListener(__menuItemAct, this);
		editMenu.addMenuItem("Paste").addActionListener(__menuItemAct, this);
		bar.append(editMenu);
		
		var optionMenu:JMenu = new JMenu("Option");
		var check1:JCheckBoxMenuItem = new JCheckBoxMenuItem("Check 1");
		check1.addSelectionListener(__menuSelection, this);
		check1.setAccelerator(new KeySequence(KeyStroke.VK_CONTROL, KeyStroke.VK_C));
		optionMenu.append(check1);
		var radio1:JRadioButtonMenuItem = new JRadioButtonMenuItem("Radio 1");
		radio1.addSelectionListener(__menuSelection, this);
		var radio2:JRadioButtonMenuItem = new JRadioButtonMenuItem("Radio 2");
		radio2.addSelectionListener(__menuSelection, this);
		var group:ButtonGroup = new ButtonGroup();
		group.append(radio1);
		group.append(radio2);
		optionMenu.append(radio1);
		optionMenu.append(radio2);
		bar.append(optionMenu);
		
		var helpMenu:JMenu = new JMenu("Help");
		helpMenu.addMenuItem("About...").addActionListener(__aboutMenuItemAct, this);
		bar.append(helpMenu);
		
		getContentPane().append(bar, BorderLayout.NORTH);
		textArea = new JTextArea();
		getContentPane().append(new JScrollPane(textArea), BorderLayout.CENTER);
		
		KeyboardManager.getInstance().registerKeyAction(
			new KeySequence(KeyStroke.VK_SHIFT, KeyStroke.VK_A), 
			__keyAction, 
			this);
	}
	
	private function __keyAction() : Void {
		textArea.appendText("Key action!\n");
		fileMenu.remove(openItem);
	}
	

	private function __menuItemAct(source:JMenuItem):Void{
		textArea.appendText("Menu " + source.getText() + " acted!\n");
	}
	private function __menuSelection(source:JMenuItem):Void{
		textArea.appendText(
			"Menu " + source.getText() 
			+ " selected ? " 
			+ source.isSelected() + "!\n");
	}
	
	private function __aboutMenuItemAct(source:JMenuItem):Void{
		JOptionPane.showMessageDialog("About", "This is just a menu test demo!");
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		Stage.align = "LT";
		fscommand("trapallkeys", "true");
		try{
			//trace("try ComboBoxTest");
			var p:NormalMenuTest = new NormalMenuTest();
			p.setLocation(10, 10);
			p.setSize(400, 400);
			p.show();
			//trace("done ComboBoxTest");
		}catch(e){
			trace("error : " + e);
		}
	}
}