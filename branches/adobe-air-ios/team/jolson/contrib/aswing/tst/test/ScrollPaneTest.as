/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.BorderLayout;
import org.aswing.ButtonGroup;
import org.aswing.JButton;
import org.aswing.JCheckBox;
import org.aswing.JPanel;
import org.aswing.JRadioButton;
import org.aswing.JScrollPane;
import org.aswing.JToggleButton;
import org.aswing.JViewport;
import org.aswing.MCPanel;
 
/**
 *
 * @author iiley
 */
class test.ScrollPaneTest extends MCPanel{

	private var contentPane:JPanel;
	private var jbutton1:JButton;
	private var toggleButton1:JToggleButton;
	private var radioButton1:JRadioButton;
	private var radioButton2:JRadioButton;
	private var radioButton3:JRadioButton;
	private var checkBox1:JCheckBox;
	private var checkBox2:JCheckBox;
	private var checkBox3:JCheckBox;
	private var scrollPane:JScrollPane;
	
	private var infoText:TextField;
	
	public function ScrollPaneTest(){
		var rootPane:MovieClip = creater.createMC(_root, "rootMC");
		super(rootPane, 400, 400);
		setLayout(new BorderLayout());
		setLocation(0, 50);
		infoText = creater.createTF(_root, "info");
		infoText._x = 200;
		
		append(getScrollPane(), BorderLayout.CENTER);
		
		var pane:JPanel = getContentPane();
		getScrollPane().setView(pane);
		pane.setPreferredSize(1000, 1000);
		
		pane.append(getJButton1(), BorderLayout.NORTH);
		pane.append(getToggleButton1(), BorderLayout.SOUTH);
		
		var centerPane:JPanel = new JPanel();
		pane.append(centerPane, BorderLayout.CENTER);
		centerPane.append(getRadioButton1());
		centerPane.append(getRadioButton2());
		centerPane.append(getRadioButton3());
		var group:ButtonGroup = new ButtonGroup();
		group.append(getRadioButton1());
		group.append(getRadioButton2());
		group.append(getRadioButton3());
		
		centerPane.append(getCheckBox1());
		centerPane.append(getCheckBox2());
		centerPane.append(getCheckBox3());
	}
	
	private function _jbutton1Released():Void{
		this.setSize(48+Math.random()*400, 48+Math.random()*400);
		this.revalidate();
	}
	
	private function _jtoggleButton1Released():Void{
		//getCheckBox3().setSelected(!getCheckBox3().isSelected());
	}
		
	private function getShortFloatString(v:Number):String{
		return Math.round(v*100)/100 + "";
	}
	
	public function getContentPane():JPanel{
		if(contentPane == null){
			contentPane = new JPanel(new BorderLayout());
		}
		return contentPane;	
	}
	
	public function getJButton1():JButton{
		if(jbutton1 == null){
			jbutton1 = new JButton("JButton1");
			jbutton1.addEventListener(JButton.ON_RELEASE, _jbutton1Released, this);
		}
		return jbutton1;
	}
	public function getToggleButton1():JToggleButton{
		if(toggleButton1 == null){
			toggleButton1 = new JToggleButton("JToggleButton1");
			toggleButton1.addEventListener(JButton.ON_RELEASE, _jtoggleButton1Released, this);
		}
		return toggleButton1;
	}
	public function getRadioButton1():JRadioButton{
		if(radioButton1 == null){
			radioButton1 = new JRadioButton("JRadioButton1");
		}
		return radioButton1;
	}
	public function getRadioButton2():JRadioButton{
		if(radioButton2 == null){
			radioButton2 = new JRadioButton("JRadioButton2");
		}
		return radioButton2;
	}
	public function getRadioButton3():JRadioButton{
		if(radioButton3 == null){
			radioButton3 = new JRadioButton("JRadioButton3");
		}
		return radioButton3;
	}
	public function getCheckBox1():JCheckBox{
		if(checkBox1 == null){
			checkBox1 = new JCheckBox("JCheckBox1");
		}
		return checkBox1;
	}
	public function getCheckBox2():JCheckBox{
		if(checkBox2 == null){
			checkBox2 = new JCheckBox("JCheckBox2");
		}
		return checkBox2;
	}
	public function getCheckBox3():JCheckBox{
		if(checkBox3 == null){
			checkBox3 = new JCheckBox("JCheckBox3");
		}
		return checkBox3;
	}
	public function getScrollPane():JScrollPane{
		if(scrollPane == null){
			scrollPane = new JScrollPane();
			var viewport:JViewport = JViewport(getScrollPane().getViewport());
			viewport.setHorizontalUnitIncrement(10);
			viewport.setVerticalUnitIncrement(10);
		}
		return scrollPane;
	}
	
	public static function main():Void{
		try{
			trace("try ScrollPaneTest");
			var p:ScrollPaneTest = new ScrollPaneTest();
			trace("done ScrollPaneTest");
		}catch(e){
			trace("error : " + e);
		}
	}	
}
