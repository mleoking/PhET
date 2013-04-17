/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASWingUtils;
import org.aswing.BorderLayout;
import org.aswing.ButtonGroup;
import org.aswing.JButton;
import org.aswing.JCheckBox;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JRadioButton;
import org.aswing.JScrollPane;
import org.aswing.JToggleButton;


/**
 *
 * @author iiley
 */
class test.FrameTest extends JFrame {
	private var scrollContentPane:JPanel;
	private var jbutton1:JButton;
	private var toggleButton1:JToggleButton;
	private var radioButton1:JRadioButton;
	private var radioButton2:JRadioButton;
	private var radioButton3:JRadioButton;
	private var checkBox1:JCheckBox;
	private var checkBox2:JCheckBox;
	private var checkBox3:JCheckBox;
	private var scrollPane:JScrollPane;
		
	public function FrameTest() {
		super("FrameTest", true);
		
		getContentPane().append(getScrollPane(), BorderLayout.CENTER);
		
		var pane:JPanel = getScrollContentPane();
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
		var theowner = Math.random()>0.5 ? null : this;
		//trace("theowner : " + theowner);
		var nf:JFrame = new JFrame(theowner, "New Frame", true);
		nf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		nf.setResizeDirectly(true);
		nf.setBounds(100, 100, 200, 100);
		nf.setResizable(Math.random()>0.5);
		nf.setDragDirectly(Math.random()>0.5);
		//nf.setClosable(Math.random()>0.1);
		nf.show();
	}
	
	private function _jtoggleButton1Released():Void{
		getCheckBox3().setSelected(!getCheckBox3().isSelected());
	}
		
	private function getShortFloatString(v:Number):String{
		return Math.round(v*100)/100 + "";
	}
	
	public function getScrollContentPane():JPanel{
		if(scrollContentPane == null){
			scrollContentPane = new JPanel(new BorderLayout());
		}
		return scrollContentPane;	
	}
	
	public function getJButton1():JButton{
		if(jbutton1 == null){
			jbutton1 = new JButton("Create A New JFrame");
			jbutton1.addEventListener(JButton.ON_ACT, _jbutton1Released, this);
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
			scrollPane.getViewport().setVerticalBlockIncrement(10);
			scrollPane.getViewport().setVerticalUnitIncrement(100);
		}
		return scrollPane;
	}
	
	public static function main():Void{
		//UIManager.setLookAndFeel(new WinXpLookAndFeel());
		Stage.scaleMode = "noScale";
		Stage.align = "T";
		try{
			trace("try FrameTest");
			
			var p:FrameTest = new FrameTest();
			//p.setClosable(false);
			p.setLocation(50, 50);
			p.setSize(400, 400);
			p.show();
			trace("done FrameTest");
		}catch(e){
			trace("error : " + e);
		}
	}

}
