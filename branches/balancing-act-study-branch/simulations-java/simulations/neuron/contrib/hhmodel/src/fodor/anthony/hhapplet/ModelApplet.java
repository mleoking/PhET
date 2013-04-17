// <APPLET code=fodor.anthony.hhapplet.ModelApplet.class height=300 width=750 ></APPLET>
package hhmodel.src.fodor.anthony.hhapplet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Panel;

/**
 *   This class contians the init function that is called when the 
 *   Hodgkin-Huxley neuron model is run.  It connects the Model class,
 *   which does the mathematical calculations, with the ChartRecorder
 *   class, which graph the data.
 */
public class ModelApplet extends Applet implements Runnable
{
	private Button bStop, bStart, bStimulate, bConfigure, bOptions;
	
	// When this is set to true, the chart recorder starts to 
	// show data
	private boolean go = false;
	
	boolean voltageClampMode = false;
	boolean atHoldingVoltage;
	
	public boolean getVoltageClampMode() { return voltageClampMode; }
	
	private float holdingVoltage = -65;
	public float getHoldingVoltage() 
	{ 
	return holdingVoltage; 
	}
	
	public void setHoldingVoltage( float holdingVoltage ) 
	{ 
		this.holdingVoltage = holdingVoltage; 
		if (voltageClampMode && atHoldingVoltage)
		{
			model.set_vClampValue( holdingVoltage );
		}
	}
	
	private float stimulusVoltage = 0;
	public float getStimulusVoltage() 
	{ 
		return stimulusVoltage; 
	}
	
	public void setStimulusVoltage( float stimulusVoltage ) 
	{ 
		this.stimulusVoltage = stimulusVoltage; 
		if (voltageClampMode && !atHoldingVoltage)
		{
			model.set_vClampValue( stimulusVoltage );
		}
	}
	
	public void setVoltageClampMode( boolean vClampMode )
	{
		if (vClampMode)
		{
			voltageClampMode = true;
			bStimulate.setLabel("Jump to stimulus voltage" );
			atHoldingVoltage = true;
			model.setVClampOn(true);
			model.set_vClampValue( holdingVoltage );
			bStimulate.enable();
		}
		else
		{
			voltageClampMode = false;
			bStimulate.setLabel("Stimulate");
			model.setVClampOn(false);
			if ( ! bStop.isEnabled() ) bStimulate.disable();
		}
	}
	
	public void setModelRatio(int modelRatio)
	{
		modelToChartRatio = modelRatio;
		recorder.setTimePerClick((float)(model.getDt() * modelToChartRatio));
	}
	
	public int getModelRatio()
	{
		return modelToChartRatio;
	}
	
	private int modelToChartRatio = 10;
	
	ChartRecorder recorder;
	Model model;
	ConfigurationForm cForm;

	Float stimulusAmount = new Float(15f);

	public float getStimulusAmount()  { return stimulusAmount.floatValue(); }
	public void setStimulusAmount( float stimulusAmount ) 
	{ 
		synchronized(this.stimulusAmount)
		{
			this.stimulusAmount = new Float(stimulusAmount); 
		}
	}

	public void init()
	{
		setLayout( new BorderLayout() );
		setBackground(Color.white);
		model = new Model(this);
		recorder = new ChartRecorder(this);
		
		cForm = new ConfigurationForm(this, recorder);
		add("East", cForm);
		
		recorder.setTimePerClick((float)(model.getDt() * modelToChartRatio));
		add("Center", recorder);
        
		bStop = new Button("Stop");
		bStop.disable();
		bStart = new Button("Start");
		bStimulate = new Button("Stimulate");
		bStimulate.disable();
		bOptions = new Button("Options...");

		Panel pButton = new Panel();
		pButton.setLayout(new GridLayout(0, 4));
		pButton.add(bStop);
		pButton.add(bStart);
		pButton.add(bStimulate);
		pButton.add(bOptions);
		add("South", pButton);
		
				
		show();
		new Thread(this).start();
	} 
	
	public void stop()
	{
		action( new Event( bStop, Event.ACTION_EVENT, null ), null);
	}
	
	public boolean keyDown(Event evt, int key)
	{
		if ( key == 's' || key == 'S' )
		{
			if (bStop.isEnabled())
			{
				action( new Event( bStop, Event.ACTION_EVENT, null), null);
			}
			else
			{
				action( new Event( bStart, Event.ACTION_EVENT, null), null);
			}
			return true;
		}
		else if( key == 't' || key == 'T' )
		{
			action( new Event( bStimulate, Event.ACTION_EVENT, null), null);
			return true;
		}
		else if( key == 'o' || key == 'O' )
		{
			action( new Event( bOptions, Event.ACTION_EVENT, null), null);
			return true;
		}
		return false;
	}
	
	public boolean action(Event evt, Object arg)
	{
		if (evt.target.equals(bStimulate))
		{
			
			if (! voltageClampMode )
			{
				// when this button gets pressed, +stimulusAmount mV gets added to the neuron
				synchronized(model)
				{
					model.setV(model.getV()+stimulusAmount.floatValue() );
				}
			}
			else
			{
				if ( atHoldingVoltage )
				{
					bStimulate.setLabel("Jump to holding voltage");	
					atHoldingVoltage = false;
					model.set_vClampValue( stimulusVoltage );
				}
				else
				{
					bStimulate.setLabel("Jump to stimulus voltage");
					atHoldingVoltage = true;
					model.set_vClampValue( holdingVoltage );
				}
			}
		
		
			return true;	
		}
		else if (evt.target.equals(bStop))
		{
			//this button stops the chart recorder from moving	
			//synchronized(this)
			{
					bStop.disable();
					bStart.enable();
					if (! voltageClampMode) bStimulate.disable();
					go = false;
			}
		}
		else if ( evt.target.equals(bStart) )
		{
			//this button starts the chart recorder
			synchronized(recorder)
			{
				try
				{
					bStop.enable();
					bStart.disable();
					bStimulate.enable();
					go = true;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		else if ( evt.target.equals(bOptions))
		{
			OptionsForm.getForm(this);
			return true;	
		}
		else return super.action(evt, arg);
		
		return true;  // we handled it here
	}

	/*
	public void repaint()
	{
		super.repaint();
		bStimulate.setLabel(bStimulate.getLabel());
		bStart.setLabel(bStart.getLabel());
		bStop.setLabel(bStop.getLabel());
		cForm.reDrawLabels();
	}
	*/
	
    /**  This thread calls the Model class to do the calculations and
     *   sends the results of those calculations to the ChartRecorder
     *   class.
     */
	public void run()
	{
		while(true)
		{
			synchronized(this)
			{
				try { Thread.sleep(20); }
				catch (InterruptedException e) {}
				if( go )
				{
					for (int x = 0; x< modelToChartRatio; x++) model.Advance();
					
					recorder.Plot( (float) model.getV() , ChartRecorder.PLOT_MV );
					recorder.Plot( (float) model.getM(), ChartRecorder.PLOT_M);
					recorder.Plot( (float) model.getH(), ChartRecorder.PLOT_H);
					recorder.Plot( (float) model.getN(), ChartRecorder.PLOT_N);
					recorder.Plot( (float) model.get_m3h(), ChartRecorder.PLOT_M3H);
					recorder.Plot( (float) model.get_n4(), ChartRecorder.PLOT_N4);
					recorder.Plot( (float) model.get_k_current(), ChartRecorder.K_CURRENT);
					recorder.Plot( (float) model.get_na_current(), ChartRecorder.NA_CURRENT);
					
					recorder.advance();
				}
			}
		}
	}
}