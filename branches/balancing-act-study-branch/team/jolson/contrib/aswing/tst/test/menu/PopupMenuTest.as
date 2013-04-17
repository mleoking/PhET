/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.BorderLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JMenu;
import org.aswing.JMenuItem;
import org.aswing.JPopupMenu;
import org.aswing.JScrollPane;
import org.aswing.JTextArea;

import test.ColorIcon;

/**
 * @author iiley
 */
class test.menu.PopupMenuTest extends JFrame {
	
	private var popupMenu:JPopupMenu;
	private var textArea:JTextArea;
	
	public function PopupMenuTest() {
		super("PopupMenuTest");
		
		popupMenu = new JPopupMenu(this);
		popupMenu.addMenuItem("Menu Item Begin").addActionListener(__menuItemAction, this);
		popupMenu.addMenuItem("Menu Item Long Long item 2").addActionListener(__menuItemAction, this);
		popupMenu.addMenuItem("Short3").addActionListener(__menuItemAction, this);
		var iconItem:JMenuItem = new JMenuItem("Has Icon Long Long Long item", new ColorIcon(null, ASColor.RED, 20, 20));
		popupMenu.append(iconItem);
		iconItem.addActionListener(__menuItemAction, this);
		
		var menu:JMenu = new JMenu("Sub Menus");
		menu.addMenuItem("Sub Menu 1");
		menu.addMenuItem("Sub Menu 2");
		popupMenu.append(menu);
		
		var button:JButton = new JButton("PopupMenu");
		textArea = new JTextArea();
		getContentPane().append(button, BorderLayout.SOUTH);
		getContentPane().append(new JScrollPane(textArea), BorderLayout.CENTER);
		
		button.addActionListener(__popupMenu, this);
	}
	
	private function __popupMenu(source:JButton):Void{
		popupMenu.show(source, source.getMousePosition().x, source.getMousePosition().y);
	}
	
	private function __menuItemAction(source:JMenuItem):Void{
		textArea.appendText("Menu " + source.getText() + " acted!\n");
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		Stage.align = "LT";
		try{
			//trace("try ComboBoxTest");
			var p:PopupMenuTest = new PopupMenuTest();
			p.setLocation(10, 10);
			p.setSize(400, 400);
			p.show();
			//trace("done ComboBoxTest");
		}catch(e){
			trace("error : " + e);
		}
	}
}