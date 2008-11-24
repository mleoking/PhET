import org.aswing.ASFont;
import org.aswing.BorderLayout;
import org.aswing.JFrame;
import org.aswing.JLabel;

/**
 * @author iiley
 */
class test.CenterLabel extends JFrame {
	
	public function CenterLabel() {
		super("CenterLabel");
		
		var label:JLabel = new JLabel("Centered T");
		var font:ASFont = new ASFont("华文彩云", 24, false);
		//font.setEmbedFonts(true);
		label.setFont(font);
		label.setVerticalAlignment(JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.CENTER);
		getContentPane().append(label, BorderLayout.WEST);
	}

	public static function main():Void{
		try{
			trace("try CenterLabel");
			
			var p:CenterLabel = new CenterLabel();
			p.setClosable(false);
			
			p.setLocation(50, 50);
			p.setSize(400, 200);
			p.show();
			
			trace("done CenterLabel");
		}catch(e){
			trace("error : " + e);
		}
	}
}