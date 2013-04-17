/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASFont;
import org.aswing.border.TitledBorder;
import org.aswing.Box;
import org.aswing.Container;
import org.aswing.DefaultComboBoxCellEditor;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JScrollPane;
import org.aswing.JTable;
import org.aswing.table.DefaultTableModel;
import org.aswing.table.GeneralTableCellFactory;
import org.aswing.table.sorter.TableSorter;
import org.aswing.UIManager;

import test.table.SexIconCell;

/**
 * @author iiley
 */
class test.table.TableDemo extends JFrame {
	
	private var tableModel:DefaultTableModel;
	private var sortableTable:JTable;
	private var unsortableTable:JTable;
	
	public function TableDemo() {
		super("TableDemo -- one model two view - MVC test");
		
		initDemo();
	}
	
	private function initDemo():Void{
		//*****************************************************************
		//                       Init Data
		var bands:Array = ["Stoa", "Empyruim", "Therion", "Radiohead", "The Czars"];
		var data:Array = [["iiley", 25, true, bands[1]], 
		  ["Igor", 27, true, bands[0]],
		  ["Firdosh", 24, true, bands[3]], 
		  ["Guy", 27, true, bands[2]],
		  ["Tomato", 24, true, bands[4]],
		  ["Some one A", 18, false, bands[4]],
		  ["Some one B", 18, false, bands[4]]];
		for(var i:Number=0; i<20; i++){
			data.push(["Some one"+i, 
						20+i, 
						Math.random()<0.5, 
						bands[Math.floor(Math.random()*bands.length)]]);
		}
		var column:Array = ["Name", "Age", "Male", "Favorite Band"];		
		
		tableModel = (new DefaultTableModel()).initWithDataNames(data, column);
		tableModel.setColumnClass(1, "Number");
		tableModel.setColumnClass(2, "Boolean");
		tableModel.setColumnEditable(0, false);
		//                       End of Init Data
		//*****************************************************************	
		
		unsortableTable = new JTable(tableModel);
		unsortableTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		//*****************************************************************
		//                       Sortable Making
		var sorter:TableSorter = new TableSorter(tableModel);
		sortableTable = new JTable(sorter);
		sorter.setTableHeader(sortableTable.getTableHeader());
		sorter.setColumnSortable(1, false);
		sorter.setSortingStatus(3, TableSorter.ASCENDING);
		//                       End of Sortable Making
		//*****************************************************************	
		
		//set comboBox editor
		var combEditor:DefaultComboBoxCellEditor = new DefaultComboBoxCellEditor();
		combEditor.getComboBox().setListData(bands);
		unsortableTable.getColumn("Favorite Band").setCellEditor(combEditor);
		sortableTable.getColumn("Favorite Band").setCellEditor(combEditor);
		
		//set sex icon renderer
		sortableTable.getColumnAt(2).setCellFactory(new GeneralTableCellFactory(SexIconCell));
		
		var pane:Container = Box.createVerticalBox(0);
		var topPane:JScrollPane = new JScrollPane(unsortableTable);
		topPane.setBorder(new TitledBorder(null, "Unsortable Table"));
		var bottomPane:JScrollPane = new JScrollPane(sortableTable);
		bottomPane.setBorder(new TitledBorder(null, "Sortable Table"));	
		pane.append(topPane);
		pane.append(bottomPane);
		pane.append(new JButton("JButton"));
		setContentPane(pane);
	}
	public static function main():Void{
		Stage.scaleMode = "noScale";
		Stage.align = "T";
		try{
			trace("try TableDemo");
			var p:TableDemo = new TableDemo();
			p.setClosable(false);
			p.setLocation(50, 50);
			p.setSize(400, 400);
			p.show();
		}catch(e){
			trace("error : " + e);
		}
		
	}
}