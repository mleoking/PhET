
/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.border.EmptyBorder;
import org.aswing.border.LineBorder;
import org.aswing.BorderLayout;
import org.aswing.DefaultComboBoxCellEditor;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JScrollPane;
import org.aswing.JTable;
import org.aswing.table.DefaultTableModel;
import org.aswing.table.GeneralTableCellFactory;
import org.aswing.table.sorter.TableSorter;
import org.aswing.util.Timer;

import test.table.SexIconCell;
/**
 * @author iiley
 */
class test.table.TestSorter extends JFrame{
	
	private var table:JTable;
	private static var timer:Timer;
	
	public function TestSorter(){
		super("TableTest");
		
		var data:Array = [["iiley", 100, true, 23, 33, "the last"], 
		  ["I dont know who", -12, false, 13, 33, "the last"],
		  ["A little cute girl", 98765, false, 0, 33, "the last2"], 
		  ["Therion1", 99, true, 23, 33, "the last3"],
		  ["Therion2", 99, true, 63, 33, "the last4"],
		  ["Therion3", 99, true, 23, 33, "the last5"],
		  ["Therion4", 99, true, 23, 33, "the last5"]];
		for(var i:Number=0; i<10; i++){
			data.push(["other"+i, i, Math.random()<0.5, 13, 323, i+"last"]);
		}
		var column:Array = ["name", "score", "male"];
		
		var model:DefaultTableModel = (new DefaultTableModel()).initWithDataNames(data, column);
		model.setColumnClass(1, "Number");
		model.setColumnClass(2, "Boolean");
		
		
		//*****************************************************************
		//                       Sortable Making
		//*****************************************************************
		var sorter:TableSorter = new TableSorter(model);
		table = new JTable(sorter);
		sorter.setTableHeader(table.getTableHeader());
		//*****************************************************************
		//                       End of Sortable Making
		//*****************************************************************
		
		var combEditor:DefaultComboBoxCellEditor = new DefaultComboBoxCellEditor();
		combEditor.getComboBox().setListData(["Therion1", "Therion2", "Therion3", "Therion4"]);
		table.getColumn("name").setCellEditor(combEditor);
		table.getColumn("male").setCellFactory(new GeneralTableCellFactory(SexIconCell));
		
		//table.setBorder(new EmptyBorder(new LineBorder(null, ASColor.RED, 2), new Insets(5, 5, 5, 5)));
		table.setRowSelectionInterval(10, 13);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		var scrollPane:JScrollPane = new JScrollPane(table); 
		
		getContentPane().append(scrollPane, BorderLayout.CENTER);
		var button:JButton = new JButton("Change Table AutoResizeMode");
		button.addActionListener(__changeAutoResizeMode, this);
		getContentPane().append(button, BorderLayout.SOUTH);
	}
	
	private function __changeAutoResizeMode():Void{
		if(table.getAutoResizeMode() == JTable.AUTO_RESIZE_ALL_COLUMNS){
			table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			
		}else if(table.getAutoResizeMode() == JTable.AUTO_RESIZE_LAST_COLUMN){
			table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			
		}else if(table.getAutoResizeMode() == JTable.AUTO_RESIZE_NEXT_COLUMN){
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
		}else if(table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF){
			table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		}else{
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
		this.setTitle("random " + Math.random());
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		Stage.align = "T";
		var f:JFrame = new JFrame("Starting...");
		f.setBounds(100, 100, 300, 250);
		//f.show();
		
		timer = new Timer(500);
		timer.setRepeats(false);
		timer.addActionListener(startTable, TestSorter);
		timer.start();
		trace("starting...");
	}
	
	public static function startTable():Void{
		try{
			trace("try TableTest");
			var p:TestSorter = new TestSorter();
			//p.setClosable(false);
			p.setLocation(50, 50);
			p.setSize(400, 300);
			p.show();
			trace("done TableTest");
		}catch(e){
			trace("error : " + e);
		}
		
	}
		
	public function toString():String{
		return "TableTest";
	}	
}