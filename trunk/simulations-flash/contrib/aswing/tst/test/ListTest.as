import org.aswing.border.LineBorder;
import org.aswing.BorderLayout;
import org.aswing.FlowLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JList;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.ListCell;
import org.aswing.VectorListModel;

import test.list.IconListCellFactory;

/**
 * @author iiley
 */
class test.ListTest extends JFrame {
    
    private var list:JList;
    private var listData:VectorListModel;
    
    public function ListTest() {
        super("List With Diff Height Items");
        
        var arr:Array = new Array();
        
        //list40
//      for(var i:Number=0; i<40; i++){
//          arr.push("Item " + i);
//      }
        //list60
        var str:String = "A long String with many many many many A long String with many many many many many chars!!!";
        for(var i:Number=0; i<60; i++){
            var startI:Number = Math.floor(Math.random()*40);
            var length:Number =  Math.floor(Math.random()*(str.length - startI));
            arr.push(i + " " + str.substr(startI, length));
        }
        
        //list100000
        /*
        for(var i:Number=0; i<100000; i++){
            arr.push("Item " + i);
        }*/
        
        listData = new VectorListModel(arr);
        //list40
        //list = new JList(listData, new IconListCellFactory(false, false));
        //list60
        list = new JList(listData, new IconListCellFactory(false, false));
        //list100000
        //list = new JList(listData);
        
        var centerPane:JPanel = new JPanel(new BorderLayout());
        var scrollPane:JScrollPane = new JScrollPane(list);
        scrollPane.setBorder(new LineBorder());
        centerPane.append(scrollPane, BorderLayout.CENTER);
        
        var addButton:JButton = new JButton("Add Item");
        addButton.addActionListener(__addItem, this);
        var removeButton:JButton = new JButton("Remove Item");
        removeButton.addActionListener(__removeItem, this);
        var buttonPane:JPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPane.append(addButton);
        buttonPane.append(removeButton);
        centerPane.append(buttonPane, BorderLayout.SOUTH);
        
        setContentPane(centerPane);
        
        list.addEventListener(JList.ON_ITEM_PRESS, __onItemPress, this);
    }
    private function __onItemPress(list:JList, value:Object, cell:ListCell):Void{
        trace("-- list : " + list);
        trace("-- value : " + value);
        trace("-- cell : " + cell);
    }
    
    private function __addItem():Void{
        listData.append("Added Item " + Math.floor(Math.random()*1000));
        list.scrollToBottomLeft();
    }
    private function __removeItem():Void{
        listData.removeAt(0);
        list.scrollToTopLeft();
    }
    
    public static function main():Void{
        Stage.scaleMode = "noScale";
        Stage.align = "T";
        try{
            trace("try ListTest");
            //org.aswing.Component.setDefaultCachePreferSizes(false);
            //UIManager.setLookAndFeel(new XlandsLookAndFeel());
            var p:ListTest = new ListTest();
            p.setLocation(50, 50);
            p.setSize(300, 250);
            p.show();
            trace("done ListTest");
        }catch(e){
            trace("error : " + e);
        }
    }
}