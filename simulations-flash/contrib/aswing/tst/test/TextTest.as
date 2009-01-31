
import org.aswing.ASColor;
import org.aswing.border.EmptyBorder;
import org.aswing.border.TitledBorder;
import org.aswing.BorderLayout;
import org.aswing.Container;
import org.aswing.FlowLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JTextArea;
import org.aswing.JTextComponent;
import org.aswing.JTextField;
import org.aswing.MCPanel;

/**
 *
 * @author Tomato
 */
class test.TextTest extends JFrame {
	
	private var button:JButton;
	private var jtextfield:JTextField;
	private var jtextarea:JTextArea;
	private var jscrollpane:JScrollPane;
	private var titleBorder:TitledBorder;
	private var panel:JPanel;
	
	public function TextTest() {
		super("Text Test");
		
		button = new JButton("Change TitledBorder Colors");
		button.setToolTipText("Button tip");
		jtextfield = new JTextField(null, 10);
		jtextfield.setRegainTextFocusEnabled(false);
		jtextfield.setText("I'm a TextField");
		jtextfield.setToolTipText("TextField tip");
		jtextfield.addEventListener(JTextField.ON_ROLLOUT, function(){trace("/d/JTextField roll out");});
		
		jtextarea = new JTextArea();
		jtextarea.setHtml(true);
		jtextarea.setWordWrap(true);
		var newsText:String = "<p class='headline'>Description</p> Method; "
    + "starts loading the CSS file into styleSheet. The load operation is asynchronous; "
    + "use the <span class='bold'>TextField.StyleSheet.onLoad</span> "
    + "callback handler to determine when the file has finished loading. "
    + "<span class='important'>The CSS file must reside in exactly the same "
    + "domain as the SWF file that is loading it.</span> For more information about "
    + "restrictions on loading data across domains, see Flash Player security features.";
		var my_styleSheet:TextField.StyleSheet = new TextField.StyleSheet();
		
		var css:String = ".important {color: #FF0000;} .bold {font-weight: bold;}.headline {color: #000000;font-family: Arial,Helvetica,sans-serif;font-size: 18px;font-weight: bold;display: block;}.important {color: #FF00FF;}.bold {font-weight: bold;}.headline {color: #00FF00;font-family: Arial,Helvetica,sans-serif;font-size: 18px; font-weight: bold;display: block;}";
		
		if(my_styleSheet.parseCSS(css)){
			trace("parse CSS successful!!");
			jtextarea.setCSS(my_styleSheet);
		}else{
			trace("parse CSS Failed!!");
		}
		
		this.jtextarea.setText(newsText);
		panel=new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		panel.append(jtextfield);
		titleBorder = new TitledBorder(null, "Attributes", TitledBorder.TOP, TitledBorder.LEFT, 3, 5);
		panel.setBorder(new EmptyBorder(titleBorder, new Insets(10, 10, 10, 10)));
		var pane:Container = getContentPane();
		pane.append(this.button, BorderLayout.SOUTH);
		pane.append(panel , BorderLayout.NORTH);
		pane.append(this.getScrollPane() , BorderLayout.CENTER);
		this.getScrollPane().setView(this.jtextarea);
		
		this.button.addActionListener(__buttonAction, this);
		button.addEventListener(JButton.ON_RELEASE, __buttonPressed, this);
		this.jtextfield.addEventListener(JTextComponent.ON_TEXT_CHANGED , __textChanged , this);
		jtextfield.addEventListener(JTextComponent.ON_KEY_DOWN, __onKeyDownTest, this);
	}
	
	private function __onKeyDownTest():Void{
		trace("__onKeyDownTest");
	}
	
	private function __buttonPressed():Void{
		trace("__buttonPressed");
	}
	
	private function __textChanged():Void{
		this.jtextarea.setText(this.jtextfield.getText());
	}
	
	private function __buttonAction():Void{
		trace("__buttonAction");
		titleBorder.setColor(new ASColor(0xFFFFFF*Math.random()));
		jtextfield.setToolTipText(null);
		if(titleBorder.getLineColor() == TitledBorder.DEFAULT_LINE_COLOR){
			titleBorder.setLineColor(ASColor.HALO_ORANGE);
	        titleBorder.setLineLightColor(ASColor.YELLOW);
		}else{                
			titleBorder.setLineColor(TitledBorder.DEFAULT_LINE_COLOR);
            titleBorder.setLineLightColor(TitledBorder.DEFAULT_LINE_LIGHT_COLOR);
		}
		panel.repaint();
	}
	
	public function getScrollPane():JScrollPane{
		if(this.jscrollpane == null){
			this.jscrollpane = new JScrollPane();
		}
		return this.jscrollpane;
	}
	
	private static var holdBtton:JButton;
	private static var instance:TextTest;
	public static function main():Void{
		try{
			trace("try LabelTest");
			var p:TextTest = new TextTest();
			instance = p;
			p.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			p.setLocation(50, 50);
			p.setSize(400, 400);
			p.show();
			
			holdBtton = new JButton("open");
			var mcP:MCPanel = new MCPanel(_root, 300, 300);
			mcP.setLayout(new FlowLayout());
			mcP.append(holdBtton);
			holdBtton.addActionListener(function(){
				p.show();
			});
			
			trace("done LabelTest");
		}catch(e){
			trace("error : " + e);
		}
	}
}