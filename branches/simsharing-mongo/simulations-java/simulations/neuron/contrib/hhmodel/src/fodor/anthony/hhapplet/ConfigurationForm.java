/**  This class is used to configure the model according to the user's specification.
 */
package hhmodel.src.fodor.anthony.hhapplet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;


public class ConfigurationForm extends Applet
{	
	ChartRecorder recorder;
	ModelApplet parent;
	Checkbox[] c = new Checkbox[recorder.plotSize];
	Choice cRightAxis;
	Label L1 = new Label("Right axis: ");
	
	ConfigurationForm(ModelApplet parent, ChartRecorder recorder)
	{
		show();
		
		this.recorder = recorder;
		this.parent = parent;
		
		drawForm();	
	}		

	public void drawForm()
	{

		removeAll();	
		cRightAxis = new Choice();

		Panel mainPanel = new Panel();
		mainPanel.setLayout( new GridLayout(recorder.plotSize + 2, 0) );

		mainPanel.add(L1);
		cRightAxis.addItem("Parameters (0 to 1)");
		cRightAxis.addItem("Currents");
		mainPanel.add(cRightAxis);
		
		for (int x = 0; x < recorder.plotSize; x++)
		{
			c[x] = new Checkbox(recorder.names[x] );
			c[x].setForeground( recorder.colorArray[x] );
			c[x].setState( recorder.plot[x] );
			mainPanel.add(c[x]);
		}
		
		setLayout( new BorderLayout());
		add("Center", mainPanel);
		validate();
	}

	public boolean action (Event evt, Object arg) 
	{
		for(int x = 0; x < recorder.plotSize; x++)
		{
			if(evt.target.equals(c[x]))
			{
				recorder.plot[x] = c[x].getState();
				figureYaxis();
				recorder.redraw();
				return true;
			}
		}
	
		if (evt.target.equals(cRightAxis))
		{
			setRightAxis();	
			return true;
		}

		return super.action(evt, arg);
	}
  
	private void figureYaxis()
	{
		boolean needParamAxis = false;
		boolean needCurrentAxis = false;
		
		int x;
		for (x = recorder.PLOT_N; x<=recorder.PLOT_M3H; x++)
			if (c[x].getState()) needParamAxis = true;
		
		for (x = recorder.NA_CURRENT; x<=recorder.K_CURRENT; x++)
			if (c[x].getState()) needCurrentAxis = true;
		
		// if we need both or either then we can't do anything so just return
		if (needParamAxis && needCurrentAxis) return;
		if (!needParamAxis && !needCurrentAxis) return;
		
		// if we just need param axis, but don't have it switch
		if (needParamAxis && cRightAxis.getSelectedIndex() == 1)
		{
			cRightAxis.select(0);
			setRightAxis();
			return;
		}
		
		// if we just need current axis, but don't have it switch
		if (needCurrentAxis && cRightAxis.getSelectedIndex() == 0)
		{
			cRightAxis.select(1);
			setRightAxis();
			return;
		}
	}
	
	private void setRightAxis()
	{
		if ( cRightAxis.getSelectedIndex() == 0) 
		{
			recorder.setRightAxis(recorder.PARAMS);
		}
		if ( cRightAxis.getSelectedIndex() == 1) 
		{	
			recorder.setRightAxis(recorder.CURRENTS);
		}
	}
	
	/*
	//add a listener so a repaint occurs when the user moves
	//the mouse over the application
	public boolean mouseEnter(Event evt, int x, int y) 
	{
		
		parent.repaint();
		return super.mouseEnter(evt, x, y);
	}
	*/
	
	/**  To fix a bug I sometime see where scrolling messes up the display of the applet
	 */
	/*
	public void reDrawLabels()
	{
		L1.setText(L1.getText());
		
		cRightAxis.select(cRightAxis.getSelectedIndex());
		
		for (int x = 0; x < c.length; x++)
			c[x].setLabel(c[x].getLabel());
	}
	*/
}
