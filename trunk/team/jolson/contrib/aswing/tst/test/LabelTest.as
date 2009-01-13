/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.BorderLayout;
import org.aswing.Container;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JLoadPane;
import org.aswing.JPanel;
import org.aswing.ASFont;
import org.aswing.plaf.basic.icon.FrameCloseIcon;
import org.aswing.plaf.basic.icon.FrameNormalIcon;

import test.ColorIcon;

/**
 *
 * @author iiley
 */
class test.LabelTest extends JFrame {

       private var button:JButton;
       private var dynamicLabel:JLabel;

       public function LabelTest(owner, title, modal : Boolean) {
               super("Label Test");
               button = new JButton("Change Label Content");
			   var font:ASFont = new ASFont("Supertext 01 Bold", 24, true, false, false, true);
               dynamicLabel = new JLabel("dynamicLabel");
               dynamicLabel.setFont(font);

               var pane:Container = getContentPane();
               pane.append(button, BorderLayout.SOUTH);

               var labelPane:JPanel = new JPanel();
               dynamicLabel.setBackground(ASColor.WHITE);
               dynamicLabel.setForeground(ASColor.RED);
               labelPane.append(dynamicLabel);
               labelPane.append(new JLabel("Only 2 line Text\nsencondLine"));
               labelPane.append(new JLabel("Text and Icon"));
               labelPane.append(new JLabel(new FrameCloseIcon()));
               pane.append(labelPane, BorderLayout.CENTER);
               pane.append(new JLoadPane("http://192.168.0.22/ihomestatics/flashs/login.swf"), BorderLayout.NORTH);

               button.addActionListener(__buttonAction, this);
       }

       private function __buttonAction():Void{
               var font:ASFont = new ASFont("Supertext 01 Bold", 10, false, false, false, true);
               dynamicLabel.setFont(font);
               dynamicLabel.setOpaque(true);
               dynamicLabel.setBackground(ASColor.WHITE);
               dynamicLabel.setText(Math.random() + "");
               if(Math.random() > 0.5){
                       dynamicLabel.setIcon(new FrameNormalIcon());
               }else{
                       dynamicLabel.setIcon(null);
               }
               if(Math.random() > 0.3){
                       dynamicLabel.setHorizontalTextPosition(JLabel.CENTER);
               }else if(Math.random() > 0.5){
                       dynamicLabel.setHorizontalTextPosition(JLabel.LEFT);
               }else{
                       dynamicLabel.setHorizontalTextPosition(JLabel.RIGHT);
               }
               if(Math.random() > 0.5){
                       dynamicLabel.setIcon(new ColorIcon(null, ASColor.RED, 40, 40));
               }else{
                       dynamicLabel.setIcon(null);
               }
               dynamicLabel.setIconTextGap(Math.random()*20);
               dynamicLabel.setEnabled(Math.random() > 0.3);
       }

       public static function main():Void{
               try{
                       trace("try LabelTest");
                       var p:LabelTest = new LabelTest();
                       p.setClosable(false);
                       p.setLocation(50, 50);
                       p.setSize(400, 400);
                       p.show();
                       trace("done LabelTest");
               }catch(e){
                       trace("error : " + e);
               }
       }
}
