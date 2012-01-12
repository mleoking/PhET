package hhmodel.src.fodor.anthony.hhapplet;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;

class LineDivider extends Applet
{	
	public void paint(Graphics g)
	{
		g.setColor(Color.white);
		g.drawLine(0 , 1, size().width, 1);
	}
}

class TitleLabel extends Label
{
	Color color = new Color(0XFFFF00);
	
	public TitleLabel(String message)
	{
		super( message );
		setForeground( color );
	}
}

public class OptionsForm extends Frame 
{

	ModelApplet parent;

	static OptionsForm form = null;
	TextField tSpeed = new TextField(4);
	Button bFaster = new Button("Faster");
	Button bSlower = new Button("Slower");
	Button bThinner = new Button("Thinner");
	Button bThicker = new Button("Thicker");
	Button bZoomInX = new Button("Zoom in");
	Button bZoomOutX = new Button("Zoom out");
	Button bCurrentsZoomIn = new Button("Zoom in");
	Button bCurrentsZoomOut = new Button("Zoom out");
	Button bVoltageZoomIn = new Button("Zoom in");
	Button bVoltageZoomOut = new Button("Zoom out");
	
	TextField tStimSize = new TextField(4);
	
	CheckboxGroup radioGroup = new CheckboxGroup();
	Checkbox cVoltageClamp;
	Checkbox cVCurrentClamp;
	TextField tVClampHold = new TextField(4);
	TextField tVClampStim = new TextField(4);
	TextField tPerNaChannels = new TextField(4);
	TextField tPerKChannels = new TextField(4);
	TextField tEk = new TextField( 4 );
	TextField tENa = new TextField( 4 );

	private OptionsForm(ModelApplet parent)
	{
		this.parent = parent;
		setTitle("Simulation Options");
		setBackground(	new Color(0X660099));
		
		if ( parent.recorder.getOvalSize() == 1) bThinner.disable();
		if ( parent.getModelRatio() == 1) bZoomInX.disable();
		if ( parent.getModelRatio() >= 30) bZoomOutX.disable();
		updateCurrentButtons();

		tStimSize.setBackground( Color.white );
		tVClampHold.setBackground( Color.white );
		tVClampStim.setBackground( Color.white );
		tPerNaChannels.setBackground( Color.white );
		tPerKChannels.setBackground( Color.white );
		tEk.setBackground( Color.white );
		tENa.setBackground( Color.white );
			
		cVoltageClamp = new Checkbox("Run in voltage clamp mode", radioGroup, parent.getVoltageClampMode() );
		cVoltageClamp.setForeground( new Color(0XFFFF00));
		cVCurrentClamp = new Checkbox("Run in current clamp mode", radioGroup, !parent.getVoltageClampMode() );
		cVCurrentClamp.setForeground( new Color(0XFFFF00));
		
		tPerKChannels.setText( "" + parent.model.getPerKChannels() );
		tPerNaChannels.setText( "" + parent.model.getPerNaChannels() );
		
		resize(500, 300);
		setResizable(false);
		GridBagLayout gridbag = new GridBagLayout();
		setLayout( gridbag );

		tVClampHold.setText( "" + parent.getHoldingVoltage() );
		tVClampStim.setText( "" + parent.getStimulusVoltage() );
		tENa.setText( "" + parent.model.getEna() );
		tEk.setText( "" + parent.model.getEk() );

		GridBagConstraints cLeft = new GridBagConstraints();
		cLeft.gridwidth = GridBagConstraints.REMAINDER;
		cLeft.anchor = GridBagConstraints.WEST;
		Label L1 = new Label("Clamp Options: ");
		L1.setForeground( Color.white );
		gridbag.setConstraints( L1, cLeft );
		add( L1 );
		gridbag.setConstraints( cVoltageClamp , cLeft );
		add(cVoltageClamp);
		
		TitleLabel L2 = new TitleLabel("Holding Voltage: ");
		GridBagConstraints cTabbed = new GridBagConstraints();
		cTabbed.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints( L2, cTabbed );
		add(L2);
		
		GridBagConstraints cVoltageArea = new GridBagConstraints();
		cVoltageArea.anchor = GridBagConstraints.WEST;
		cVoltageArea.insets = new Insets(0 , 0, 5, 0);
		gridbag.setConstraints( tVClampHold , cVoltageArea );
		add(tVClampHold);
		
		TitleLabel L3 = new TitleLabel("mV");
		gridbag.setConstraints( L3, cLeft);
		add(L3);
						
		TitleLabel L4 = new TitleLabel("Stimulus Voltage: " );
		gridbag.setConstraints( L4, cTabbed );
		add(L4);
		gridbag.setConstraints( tVClampStim , cVoltageArea );
		add(tVClampStim);
		
		TitleLabel L5 = new TitleLabel("mV");
		gridbag.setConstraints( L5 , cLeft);
		add(L5);
		
		gridbag.setConstraints( cVCurrentClamp, cLeft);
		add(cVCurrentClamp);
		
		TitleLabel L6 = new TitleLabel("Stimulus button adds: ");
		gridbag.setConstraints( L6, cTabbed );
		add(L6);
		gridbag.setConstraints( tStimSize , cVoltageArea );
		tStimSize.setText(""+ parent.getStimulusAmount());
		add(tStimSize);
		
		TitleLabel L7 = new TitleLabel("mV");
		gridbag.setConstraints( L7, cLeft );
		add(L7);
		
		LineDivider ld = new LineDivider();
		GridBagConstraints cDivider = new GridBagConstraints();
		cDivider.fill = GridBagConstraints.HORIZONTAL;
		cDivider.gridwidth = GridBagConstraints.REMAINDER;
		cDivider.insets = new Insets( 0, 10, 0, 10);
		gridbag.setConstraints( ld, cDivider );
		add(ld);
		
		Label L8 = new Label("Graph Options: ");
		L8.setForeground( Color.white );
		gridbag.setConstraints( L8 , cLeft );
		add(L8);
		
		TitleLabel L9 = new TitleLabel( "X axis scale (changing clears chart!) : ");
		add(L9);
		GridBagConstraints cBordered = new GridBagConstraints();
		cBordered.insets = new Insets( 5, 0, 0, 1);
		gridbag.setConstraints( bZoomInX , cBordered );
		gridbag.setConstraints( bZoomOutX, cBordered );
		add(bZoomInX);
		add(bZoomOutX);
		Label L10 = new Label("");
		gridbag.setConstraints( L10 , cDivider );
		add(L10);
		
		TitleLabel L11V = new TitleLabel("Scale Voltage ( Left Y ) axis:");
		gridbag.setConstraints( L11V, cTabbed);
		add(L11V);
		gridbag.setConstraints(bVoltageZoomIn , cBordered);
		gridbag.setConstraints(bVoltageZoomOut, cBordered);
		add(bVoltageZoomIn);
		add(bVoltageZoomOut);
		Label L12V = new Label("");
		gridbag.setConstraints( L12V, cDivider );
		add(L12V);
		
		
		TitleLabel L11 = new TitleLabel("Scale Current ( Right Y ) axis:");
		gridbag.setConstraints( L11, cTabbed);
		add(L11);
		gridbag.setConstraints(bCurrentsZoomIn , cBordered);
		gridbag.setConstraints(bCurrentsZoomOut, cBordered);
		add(bCurrentsZoomIn);
		add(bCurrentsZoomOut);
		Label L12 = new Label("");
		gridbag.setConstraints( L12, cDivider );
		add(L12);
		
		TitleLabel L13 = new TitleLabel("Pen Size: ");
		gridbag.setConstraints( L13, cTabbed );
		add(L13);
		gridbag.setConstraints( bThinner, cBordered );
		gridbag.setConstraints( bThicker, cBordered );
		add(bThinner);
		add(bThicker);
		Label L14 = new Label("");
		gridbag.setConstraints( L14, cDivider );
		add(L14);
		
		LineDivider ld2 = new LineDivider();
		gridbag.setConstraints( ld2 , cDivider );
		add(ld2);
		
		Label L17 = new Label("Model Options: ");
		L17.setForeground( Color.white );
		gridbag.setConstraints( L17, cLeft );
		add(L17);
		
		TitleLabel L18 = new TitleLabel("Percent Na channels: ");
		gridbag.setConstraints( L18, cTabbed );
		add(L18);
		cVoltageArea.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints( tPerNaChannels , cVoltageArea);
		add( tPerNaChannels );
		
		TitleLabel L19 = new TitleLabel("Percent K channels: ");
		gridbag.setConstraints( L19, cTabbed );
		add(L19);
		gridbag.setConstraints( tPerKChannels , cVoltageArea);
		add( tPerKChannels );
		
		TitleLabel L20 = new TitleLabel("Na reversal potential: ");
		gridbag.setConstraints( L20, cTabbed );
		add(L20);
		gridbag.setConstraints( tENa , cVoltageArea);
		add( this.tENa );
		
		TitleLabel L21 = new TitleLabel("K reversal potential: ");
		gridbag.setConstraints( L21, cTabbed );
		add(L21);
		gridbag.setConstraints( tEk , cVoltageArea);
		add( tEk );
		
		Label L16 = new Label("c1999 Anthony Fodor and William N. Zagotta, v 1.01");
		L16.setForeground( Color.white );
		GridBagConstraints cFinal = new GridBagConstraints();
		cFinal.anchor = GridBagConstraints.CENTER;
		cFinal.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints( L16, cFinal );
		add(L16);
		
		pack();
	}

	//enforces the singleton logic
	public static OptionsForm getForm(ModelApplet parent)
	{
		int xPos = 100;
		int yPos = 100;
		
		if (form != null)
		{
			if(form.location().x > 0 && form.location().y > 0 && 
				form.location().x < form.getToolkit().getScreenSize().width &&
				form.location().y < form.getToolkit().getScreenSize().height)
			{
				xPos = form.location().x;
				yPos = form.location().y;
			}
		
			form.dispose();
		}
			
		form = new OptionsForm(parent);
		form.move(xPos, yPos);
		form.show();
		form.requestFocus();
		return form;
	}
	
	public boolean handleEvent(Event evt) 
	{
		if (evt.id == Event.WINDOW_DESTROY)
		{
			hide();
			return true;
		}
		else
		{
			return super.handleEvent(evt);
		}
	}

	public boolean action(Event evt, Object obj)
	{
		if(evt.target.equals(tStimSize))
		{
			setStimulusAmount();
			return true;
		}
		else if(evt.target.equals(cVoltageClamp))
		{
			parent.setVoltageClampMode(cVoltageClamp.getState());
			return true;
		}
		else if(evt.target.equals( cVCurrentClamp ))
		{
			parent.setVoltageClampMode(cVoltageClamp.getState());
			return true;	
		}
		else if(evt.target.equals(tVClampHold))
		{
			setVHoldClampAmount();
			return true;
		}
		else if(evt.target.equals(tVClampStim))
		{
			setVStimClampAmount();
			return true;
		}
		else if(evt.target.equals(bThinner))
		{
			parent.recorder.drawThinner();
			
			if (parent.recorder.getOvalSize() == 1)
			{
				bThinner.disable();
			}
			
			return true;
		}
		else if(evt.target.equals(bThicker))
		{
			parent.recorder.drawThicker();
			bThinner.enable();
			return true;
		}
		else if(evt.target.equals(bZoomInX))
		{
			synchronized(this)
			{
				bZoomOutX.enable();
				parent.setModelRatio( parent.getModelRatio() / 2 );
				
				if ( parent.getModelRatio() == 1) 
				{
					parent.recorder.setSizeofTimeMarker( 1 );
					bZoomInX.disable();
				}	
			
			// clear the old data
			parent.recorder.Clear();
			}
			
			return true;
		}
		else if(evt.target.equals(bZoomOutX))
		{
			synchronized(this)
			{
				parent.setModelRatio( parent.getModelRatio() * 2 );
				bZoomInX.enable();
				parent.recorder.setSizeofTimeMarker( 2 );
				parent.recorder.Clear();
				
				if ( parent.getModelRatio() >= 30) bZoomOutX.disable();
			}
			return true;
		}
		else if(evt.target.equals(bCurrentsZoomIn))
		{
			synchronized(this)
			{
				
				parent.recorder.zoomAxis( ChartRecorder.NA_CURRENT, .5f, false);
				parent.recorder.zoomAxis( ChartRecorder.K_CURRENT, .5f, true);
				
				updateCurrentButtons();
				
				bCurrentsZoomOut.enable();
			}
			return true;
		}
		else if(evt.target.equals(bCurrentsZoomOut))
		{
			synchronized(this)
			{
				parent.recorder.zoomAxis( ChartRecorder.NA_CURRENT, 2f, false);
				parent.recorder.zoomAxis( ChartRecorder.K_CURRENT, 2f, true);
				
				updateCurrentButtons();
				
				bCurrentsZoomIn.enable();
			}
			return true;
		}
		else if(evt.target.equals(bVoltageZoomIn))
		{
			synchronized(this)
			{
				bVoltageZoomOut.enable();
				parent.recorder.zoomAxis( ChartRecorder.PLOT_MV, 0.5f, true);
				
				updateCurrentButtons();
			}
			return true;
		}
		else if(evt.target.equals(bVoltageZoomOut))
		{
			synchronized(this)
			{
				bVoltageZoomIn.enable();
				parent.recorder.zoomAxis( ChartRecorder.PLOT_MV, 2f, true );
				
				updateCurrentButtons();
			}
			return true;
		}
		else if(evt.target.equals( tPerNaChannels ) )
		{
			setPercentNaChannel();
			return true;
		}
		else if(evt.target.equals( tPerKChannels ) )
		{
			setPercentKChannel();
			return true;
		}
		else if(evt.target.equals( tEk ))
		{
			setEk();
			return true;
		}
		else if(evt.target.equals( tENa ))
		{
			setENa();
			return true;
		}
		else return super.action(evt, obj);
	}

	public boolean lostFocus(Event evt, Object obj)
	{
		if(evt.target.equals(tStimSize))
		{
			setStimulusAmount();
			return true;
		}
		else if(evt.target.equals(tVClampHold))
		{
			setVHoldClampAmount();
			return true;
		}
		else if(evt.target.equals(tVClampStim))
		{
			setVStimClampAmount();
			return true;
		}
		else if(evt.target.equals( tPerNaChannels ) )
		{
			setPercentNaChannel();
			return true;
		}
		else if(evt.target.equals( tPerKChannels ) )
		{
			setPercentKChannel();
			return true;
		}						  
		else if(evt.target.equals( tEk ))
		{
			setEk();
			return true;
		}
		else if(evt.target.equals( tENa ))
		{
			setENa();
			return true;
		}
		else return super.action(evt, obj);
	}

	public void setStimulusAmount()
	{
		try
		{
			parent.setStimulusAmount(new Float(tStimSize.getText()).floatValue());
		}
		catch(NumberFormatException e)
		{
		}
		// give some feedback to the user if the change was accepted or not
		tStimSize.setText("" + parent.getStimulusAmount());
	}

	public void setVHoldClampAmount()
	{
		//System.out.println("Setting VCLAMP Hold amount");
		try
		{
			parent.setHoldingVoltage(new Float(tVClampHold.getText()).floatValue());
		}
		catch(NumberFormatException e)
		{
		}
		tVClampHold.setText("" + parent.getHoldingVoltage() );
	}
	
	public void setVStimClampAmount()
	{
		try
		{
			parent.setStimulusVoltage(new Float(tVClampStim.getText()).floatValue());
		}
		catch(NumberFormatException e)
		{
		}
		tVClampStim.setText("" + parent.getStimulusVoltage() );
	}
	
	public void setPercentNaChannel()
	{
		try
		{
			parent.model.setPerNaChannels( new Float(tPerNaChannels.getText()).floatValue() );
		}
		catch(NumberFormatException e)
		{
		}
		tPerNaChannels.setText("" + parent.model.getPerNaChannels() );
	}
	
	public void setPercentKChannel()
	{
		try
		{
			parent.model.setPerKChannels( new Float(tPerKChannels.getText()).floatValue() );
		}
		catch(NumberFormatException e)
		{
		}
		tPerKChannels.setText("" + parent.model.getPerKChannels() );
	}
	
	public void setEk()
	{
		try
		{
			parent.model.setEk( new Float( tEk.getText()).floatValue() );
		}
		catch(NumberFormatException e)
		{
		}
		tEk.setText("" + parent.model.getEk() );
	}
	
	public void setENa()
	{
		try
		{
			parent.model.setEna( new Float( tENa.getText()).floatValue() );
		}
		catch(NumberFormatException e)
		{
		}
		tENa.setText("" + parent.model.getEna() );
	}
	
	public void updateCurrentButtons()
	{
		if (parent.recorder.topmostY[ChartRecorder.PLOT_MV] > 200 )
					bVoltageZoomOut.disable();
		
		if ( parent.recorder.topmostY[ChartRecorder.PLOT_MV] < 60 )
					bVoltageZoomIn.disable();
		
		if ( parent.recorder.topmostY[ChartRecorder.NA_CURRENT] > 6400 )
					bCurrentsZoomOut.disable();
		
		if ( parent.recorder.topmostY[ChartRecorder.NA_CURRENT] < 100.0 )
					bCurrentsZoomIn.disable();		
	}

}

