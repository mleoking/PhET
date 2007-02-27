package test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import com.birosoft.liquid.LiquidLookAndFeel.*; 
import com.birosoft.liquid.LiquidLookAndFeel;
import javax.swing.table.JTableHeader;
        
public class LineNumberTable extends JTable
{
	private JTable mainTable;
 
	public LineNumberTable(JTable table)
	{
		super();
		mainTable = table;
                setShowHorizontalLines(false);
		setShowVerticalLines(true);
                mainTable.setShowHorizontalLines(false);
                mainTable.setShowVerticalLines(true);
		setAutoCreateColumnsFromModel( false );
		setModel( mainTable.getModel() );
        setSelectionModel( mainTable.getSelectionModel() );
		setAutoscrolls( false );
 
		addColumn( new TableColumn() );
		getColumnModel().getColumn(0).setCellRenderer( mainTable.getTableHeader().getDefaultRenderer() );
		getColumnModel().getColumn(0).setPreferredWidth(50);
		setPreferredScrollableViewportSize(getPreferredSize());
	}
 
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
 
	public Object getValueAt(int row, int column)
	{
		return new Integer(row + 1);
	}
 
	public int getRowHeight(int row)
	{
		return mainTable.getRowHeight();
	}
 
	public static void main(String[] args)
		throws Exception
	{
		
                  try { 
       javax.swing.UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
        //javax.swing.UIManager.setLookAndFeel("org.jvnet.substance.SubstanceLookAndFeel");
          //javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
          LiquidLookAndFeel.setShowTableGrids(true);
        }
        catch (Exception e) {e.printStackTrace();}
            
            
                DefaultTableModel model = new DefaultTableModel(100, 5);
		JTable table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane( table );
 
		JTable lineTable = new LineNumberTable( table );
                JTableHeader rowHeader = new JTableHeader(lineTable.getColumnModel());
                rowHeader.setTable(lineTable);
		scrollPane.setRowHeaderView( rowHeader );
 
		JFrame frame = new JFrame( "Line Number Table" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setSize(400, 300);
		frame.setVisible(true);
	}
}
