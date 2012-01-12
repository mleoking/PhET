/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.border.LineBorder;
import org.aswing.BorderLayout;
import org.aswing.FlowLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JLoadPane;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class test.LoadPaneTest extends JFrame {
	
	private var topPane:JLoadPane;
	private var centerPane:JLoadPane;
	private var statusLabel:JLabel;
	private var jpgs:Array;
	private var jpgIndex:Number;
	
	public function LoadPaneTest() {
		super("LoadPaneTest");
		jpgIndex = 0;
		jpgs = ["1.jpg","2.jpg","3.jpg","4.jpg","5.jpg"];
		
		topPane = new JLoadPane("top.swf", JLoadPane.PREFER_SIZE_LAYOUT);
		topPane.addEventListener(this);
		topPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		topPane.append(new JButton("a button on animation"));
		centerPane = new JLoadPane(jpgs[0], JLoadPane.PREFER_SIZE_IMAGE);
		centerPane.setHorizontalAlignment(JLoadPane.CENTER);
		centerPane.addEventListener(this);
		centerPane.setBorder(new LineBorder(null, ASColor.BLACK, 2));
		
		var scrollPane:JScrollPane = new JScrollPane(centerPane);
		scrollPane.setPreferredSize(400, 400);
		getContentPane().append(topPane, BorderLayout.NORTH);
		getContentPane().append(scrollPane, BorderLayout.CENTER);
		
		var preButton:JButton = new JButton("prev");
		preButton.addActionListener(Delegate.create(this, changePic, -1));
		preButton.setLocation(5, 100);
		preButton.setSize(preButton.getPreferredSize());
		var nexButton:JButton = new JButton("next");
		nexButton.addActionListener(Delegate.create(this, changePic, 1));
		nexButton.setLocation(5, 130);
		nexButton.setSize(nexButton.getPreferredSize());
		
		centerPane.append(preButton);
		centerPane.append(nexButton);
		
		statusLabel = new JLabel("not loaded any thing yet");
		var bottomPane:JPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomPane.append(statusLabel);
		getContentPane().append(bottomPane, BorderLayout.SOUTH);
	}
	
	private function changePic(source:JLoadPane, dir:Number):Void{
		jpgIndex += dir;
		jpgIndex = (jpgIndex + jpgs.length)%jpgs.length;
		centerPane.setPath(jpgs[jpgIndex]);
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		try{
			trace("try LoadPaneTest");
			var p:LoadPaneTest = new LoadPaneTest();
			p.setLocation(100, 50);
			p.setSize(400, 400);
			p.show();
			trace("done LoadPaneTest");
		}catch(e){
			trace("error : " + e);
		}
	}
	
	private function onLoadComplete(source:JLoadPane):Void{
		var info:String = source.getPath() + " onLoadComplete!";
		statusLabel.setText(info);
		//trace(info);
	}
	
	private function onLoadError(source:JLoadPane, errorCode:String):Void{
		var info:String = source.getPath() + " errorCode:" + errorCode;
		statusLabel.setText(info);
		//trace(info);
	}
	
	private function onLoadInit(source:JLoadPane):Void{
		var info:String = source.getPath() + " onLoadInit!";
		statusLabel.setText(info);
		//trace(info);
		pack();
	}
	
	private function onLoadProgress(source:JLoadPane, loadedBytes:Number, totalBytes:Number):Void{
		var info:String = loadedBytes + "/" + totalBytes;
		statusLabel.setText(info);
		//trace(info);
	}
	
	private function onLoadStart(source:JLoadPane):Void{
		var info:String = source.getPath() + " onLoadStart!";
		statusLabel.setText(info);
		//trace(info);
	}	

}
