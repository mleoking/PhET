 /**
* @author firdosh
*/
import org.aswing.AttachIcon;
import org.aswing.BorderLayout;
import org.aswing.FlowLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JPanel;

class test.MessageBox extends JFrame
{
	private var buttonsPanel:JPanel;
	private var labelPanel:JPanel;
	private var okBtn : JButton;
	private var cancelBtn : JButton;
	private var title : String;
	private var message : String;
	private var messageDisplay:JLabel;
	
	public function MessageBox (title:String ,message:String)
	{
		super (_root, true);
		this.title=title;
		this.message=message;
		
		var info:AttachIcon =new AttachIcon("INFO",51,43);
		var buttonsPanel:JPanel =new JPanel();
		var labelPanel:JPanel =new JPanel();
		okBtn = new JButton ("Ok", info);
		//okBtn.setPreferredSize(75,22);
		cancelBtn = new JButton ("Cancel");
		cancelBtn.setPreferredSize(75,22);
		messageDisplay = new JLabel(message,info);
		
		
		
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,20,8));
		buttonsPanel.append(okBtn);
		buttonsPanel.append(cancelBtn);
		
		labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		labelPanel.append(messageDisplay);
		
		getContentPane().append(labelPanel, BorderLayout.EAST);
		getContentPane().append(buttonsPanel, BorderLayout.SOUTH);
		setTitle(title);
	}
	public static function main (Void) : Void
	{
		var myWindow : MessageBox = new MessageBox ("Information","ASWing ROCKS!!");
		myWindow.setLocation (50, 50);
		myWindow.setSize (200, 200);
		//myWindow.setResizable(false);
		myWindow.show ();
		myWindow.messageDisplay.revalidate();
		
		_root.test.text="hello";
	}
}
