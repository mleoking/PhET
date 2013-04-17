/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.awml.AwmlLoader;
import org.aswing.awml.AwmlManager;
import org.aswing.DefaultComboBoxCellEditor;
import org.aswing.JFrame;
import org.aswing.JTable;
import org.aswing.JWindow;
import org.aswing.JPopup;

/**
 * AWML test class.
 *
 * @author Igor Sadovskiy
 */
class test.AwmlTest {

    public static function main():Void{
        Stage.scaleMode = "noScale";
        Stage.align = "T";
        
        try{
            trace("try AwmlTest");
            var p:AwmlTest = new AwmlTest();
        }catch(e){
            trace("error : " + e);
        }
    }

    private var frame:JPopup;
    private var loader:AwmlLoader;
        
    public function AwmlTest() {
        
        AwmlManager.setComponentParsingStrategy(AwmlManager.STRATEGY_EXCEPTION);
        AwmlManager.addEventListener("frame", this);
        
        loader = new AwmlLoader(true);
        loader.addActionListener(onAwmlParse, this);
        loader.addEventListener(AwmlLoader.ON_LOAD_PROGRESS, onAwmlLoadProgress, this);
        loader.addEventListener(AwmlLoader.ON_PARSE_FAIL, onAwmlParseFail, this);
        loader.load("../res/test.xml");
    }
    
    private function onAwmlLoadProgress(loader:AwmlLoader, bytesLoaded:Number, bytesTotal:Number):Void {
        trace("Loaded " + bytesLoaded + " of " + bytesTotal + " bytes");        
    }
    
    private function onAwmlParse(loader:AwmlLoader):Void {
        
        try{
                
                trace("awml Init");
                
                frame = AwmlManager.getPopup("frame", "main");
                trace("frame = " + frame);
                frame.setLocation(100, 100);
                frame.show();
                
                trace("content = " + AwmlManager.getComponent("content"));
                
                var table:JTable = AwmlManager.getTable("mainTable");
                //trace("table = " + table);
                
                var bands:Array = ["Stoa", "Empyruim", "Therion", "Radiohead", "The Czars"];
                
                var combEditor:DefaultComboBoxCellEditor = new DefaultComboBoxCellEditor();
                combEditor.getComboBox().setListData(bands);
                table.getColumn("Favorite Band").setCellEditor(combEditor);
                
                trace("selection bg : " + table.getSelectionBackground());
                trace("done AwmlTest");
                }catch(e){
            trace("error : " + e);
        }               
    } 
    
    private function onAwmlParseFail(loader:AwmlLoader, message:String):Void {
        trace("ERROR: " + message);     
    }
    
    private function onShowModalClick():Void {
        var childFrame:JFrame = AwmlManager.getFrame("frame", "child");
        childFrame.pack();
        childFrame.show();      
    }
    
}
