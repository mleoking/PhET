/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.BorderLayout;
import org.aswing.ButtonGroup;
import org.aswing.JButton;
import org.aswing.JCheckBox;
import org.aswing.JPanel;
import org.aswing.JRadioButton;
import org.aswing.JScrollBar;
import org.aswing.JToggleButton;
import org.aswing.MCPanel;

class test.MainTest3 extends MCPanel{
	
	private var contentPane:JPanel;
	private var jbutton1:JButton;
	private var toggleButton1:JToggleButton;
	private var radioButton1:JRadioButton;
	private var radioButton2:JRadioButton;
	private var radioButton3:JRadioButton;
	private var checkBox1:JCheckBox;
	private var checkBox2:JCheckBox;
	private var checkBox3:JCheckBox;
	private var vScrollBar:JScrollBar;
	private var hScrollBar:JScrollBar;
	
	private var infoText:TextField;
	
	public function MainTest3(){
		super(creater.createMC(_root, "rootMC"), 400, 400);
		setLocation(0, 50);
		infoText = creater.createTF(_root, "info");
		infoText._x = 200;
		
		var pane:JPanel = getContentPane();
		setLayout(new BorderLayout());
		append(pane, BorderLayout.CENTER);
		
		pane.append(getJButton1(), BorderLayout.NORTH);
		//pane.append(getToggleButton1(), BorderLayout.SOUTH);
		pane.append(getHScrollBar(), BorderLayout.SOUTH);
		pane.append(getVScrollBar(), BorderLayout.EAST);
		
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
		this.setSize(Math.floor(48+Math.random()*400), Math.floor(48+Math.random()*400));
		this.revalidate();
	}
	
	private function _jtoggleButton1Released():Void{
		getCheckBox3().setSelected(!getCheckBox3().isSelected());
	}
	
	private function _updateScrollValue():Void{
		var info:String;
		info = "V V:" + getShortFloatString(getVScrollBar().getValue()) + ", ";
		info += "H V:" + getShortFloatString(getHScrollBar().getValue());
		infoText.text = info;
	}
	
	private function getShortFloatString(v:Number):String{
		return Math.round(v*100)/100 + "";
	}
	
	public function getContentPane():JPanel{
		if(contentPane == null){
			contentPane = new JPanel(new BorderLayout());
			trace("contentPane = " + contentPane);
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
	public function getVScrollBar():JScrollBar{
		if(vScrollBar == null){
			vScrollBar = new JScrollBar(JScrollBar.VERTICAL);
			vScrollBar.addAdjustmentListener(_updateScrollValue, this);
			vScrollBar.setMaximum(1000);
			vScrollBar.setMinimum(100);
			vScrollBar.setVisibleAmount(100);
			vScrollBar.setBlockIncrement(100);
		}
		return vScrollBar;
	}
	public function getHScrollBar():JScrollBar{
		if(hScrollBar == null){
			hScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
			hScrollBar.addAdjustmentListener(_updateScrollValue, this);
		}
		return hScrollBar;
	}		
	
	public static function main():Void{
		//Flashout.log("init Flashout");
		try{
			trace("try MainTest2");
			var p:MainTest3 = new MainTest3();
			trace("done MainTest2");
		}catch(e){
			trace("error : " + e);
		}
	}
}
