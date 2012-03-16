/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.awml.AwmlLoader;
import org.aswing.awml.AwmlManager;
import org.aswing.DefaultComboBoxCellEditor;
import org.aswing.JFrame;
import org.aswing.*;

class test.AwmlTest2  {

   public static function main():Void{
       Stage.scaleMode = "noScale";
       Stage.align = "T";
       try{
           var p:AwmlTest2 = new AwmlTest2();
       }catch(e){
           //trace("error : " + e);
       }
   }

   private var frame:JFrame;
   private var loader:AwmlLoader;

   public function AwmlTest2() {
       AwmlManager.addEventListener("frame", this);

       loader = new AwmlLoader(true);
       loader.addActionListener(onAwmlInit, this);
       loader.load("../res/test2.xml");
   }

   private function onAwmlInit(loader:AwmlLoader):Void {
		trace("onAwmlInit");
       try{
               frame = AwmlManager.getFrame("frame", "main");
               frame.setLocation(100, 100);
               frame.show();
               }catch(e){
           //trace("error : " + e);
       }
   }

   private function onGetTextButtonClick():Void {
       var tf:JTextField = AwmlManager.getTextField("group");
       trace(tf.getText());
       JOptionPane.showMessageDialog("Test", tf.getText(), null);
   }
}