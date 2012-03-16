package org.aswing.table{

import org.aswing.ListModel;
import org.aswing.event.ListDataEvent;
import org.aswing.event.ListDataListener;

/**
 * The table model return the properties of a row to be column data.
 * <p>
 * PropertyTableModel is very conveniently to use when your table data can be stored in a list 
 * and each columns can be a property of a object of a row.
 * <br/>
 * For example, you can a data like this:<br/>
 * <pre>
 * data = 
 *  [{name:"iiley", sex:1, age:26}, 
 *  {name:"Comeny", sex:0, age:24}, 
 *  {name:"Tom", sex:1, age:30},
 *  {name:"Lita", sex:0, age:16}
 *  ];
 * </pre>
 * Woool, it is very suit for PropertyTableModel to provide data to a JTable to view the datas.
 * You can create your JTable like this:
 * <pre>
 * var dataList:VectorListModel = new VectorListModel();
 * dataList.appendAll(data);
 * var tableModel:PropertyTableModel = new PropertyTableModel(
 * 		dataList, 
 * 		["Guy's Name", "Sex", "Age"], 
 * 		["name", "sex", "age"], 
 * 		[null, new SexTranslator(), null]
 * );
 * var table:JTable = new JTable(tableModel);
 * </pre>
 * Then the table will render a table for each object each properties like this.
 * <pre>
 * -------------------------------
 * | Guy's Name |  Sex   |  Age  | 
 * |------------------------------
 * | iiley      |  male  |  26   | 
 * |------------------------------
 * | Comeny     | female |  24   | 
 * |------------------------------
 * | Tom        |  male  |  30   | 
 * |------------------------------
 * | Lita       | female |  16   | 
 * -------------------------------
 * </pre>
 * </p>
 * 
 * @author iiley
 */
public class PropertyTableModel extends AbstractTableModel implements ListDataListener{
	
	protected var list:ListModel;
	protected var names:Array;
	protected var properties:Array;
	protected var translators:Array;
	protected var columnsEditable:Array;
	
	/**
	 * Create a Property table model, column headers, properties names, and translators.
	 * @param listModel the list model that contains the row objects.
	 * @param names column header labels.
	 * @param properties property names for column values, "." means returns row data object directly.
	 * @param translators the translators for each column, a null translator for a columns means return the property 
	 * of that name directly. translator can be a PropertyTranslator instance or a Function(info:*, key:String):*
	 */
	public function PropertyTableModel(listModel:ListModel, names:Array, properties:Array, translators:Array){
		super();
		this.setList(listModel);
		this.names = names.concat();
		this.properties = properties.concat();
		this.translators = translators.concat();
		columnsEditable = new Array();
	}
	
	/**
	 * Sets the row data provider, a list model.
	 * @param listModel the row object datas.
	 */
	public function setList(listModel:ListModel):void{
		if(list != null){
			list.removeListDataListener(this);
		}
		list = listModel;
		if(list != null){
			list.addListDataListener(this);
		}
		fireTableDataChanged();
	}
	
	/**
	 * Returns the row data provider, a list model.
	 * @returns the row data provider.
	 */
	public function getList():ListModel{
		return list;
	}

	override public function getRowCount():int{
		if(list){
			return list.getSize();
		}else{
			return 0;
		}
	}

	override public function getColumnCount():int{
		return names.length;
	}
	
	/**
	 * Returns the translated value for specified row and column.
	 * @return the translated value for specified row and column.
	 */
	override public function getValueAt(rowIndex:int, columnIndex:int):*{
		var translator:* = translators[columnIndex];
		var info:* = list.getElementAt(rowIndex);
		var key:String = properties[columnIndex];
		if(translator != null){
			if(translator is PropertyTranslator){
				return PropertyTranslator(translator).translate(info, key);
			}else if(translator is Function){
				return translator(info, key);
			}else{
				throw new Error("Translator must be a PropertyTranslator or a Function : " + translator);
			}
		}else{
			if(key == "."){
				return info;
			}
			return info[key];
		}
	}
	
	/**
	 * Returns the column name for specified column.
	 */
	override public function getColumnName(column:int):String {
		return names[column];
	}
	
	/**
	 * Returns is the row column editable, default is true.
	 *
	 * @param   row			 the row whose value is to be queried
	 * @param   column		  the column whose value is to be queried
	 * @return				  is the row column editable, default is true.
	 * @see #setValueAt()
	 * @see #setCellEditable()
	 * @see #setAllCellEditable()
	 */
	override public function isCellEditable(row:int, column:int):Boolean {
		if(columnsEditable[column] == undefined){
			return true;
		}else{
			return columnsEditable[column] == true;
		}
	}

	/**
	 * Returns is the column editable, default is true.
	 *
	 * @param   column		  the column whose value is to be queried
	 * @return				  is the column editable, default is true.
	 * @see #setValueAt()
	 * @see #setCellEditable()
	 * @see #setAllCellEditable()
	 */
	public function isColumnEditable(column:int):Boolean {
		return isCellEditable(0, column);
	}
	
	/**
	 * Sets spcecifed column editable or not.
	 * @param column the column whose value is to be queried
	 * @param editable editable or not
	 */
	public function setColumnEditable(column:int, editable:Boolean):void{
		columnsEditable[column] = editable;
	}
	
	/**
	 * Sets all cells editable or not.
	 * @param editable editable or not
	 */
	public function setAllCellEditable(editable:Boolean):void{
		for(var i:int = getColumnCount()-1; i>=0; i--){
			columnsEditable[i] = editable;
		}
	}
	
	override public function setValueAt(aValue:*, rowIndex:int, columnIndex:int):void{
		var info:* = list.getElementAt(rowIndex);
		var key:String = properties[columnIndex];
		info[key] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	//__________Listeners for List Model, to keep table view updated when row objects changed__________
	
	public function intervalAdded(e:ListDataEvent):void{
		fireTableRowsInserted(e.getIndex0(), e.getIndex1());
	}

	public function intervalRemoved(e:ListDataEvent):void{
		fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
	}

	public function contentsChanged(e:ListDataEvent):void{
		fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
	}		
}
}