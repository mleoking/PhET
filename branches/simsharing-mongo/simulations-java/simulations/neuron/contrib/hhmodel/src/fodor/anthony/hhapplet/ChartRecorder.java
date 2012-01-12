package hhmodel.src.fodor.anthony.hhapplet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

/**This class simulates a chart recorder in an electrophysiology lab.  
 * Calling plot(float inDatum) causes inDatum to be plotted and the 
 * chart recorder to move ahead one click.  If the recorder has reached 
 * the end of the screen, the function Finished() returns false.  Calling 
 * clear() clears the chart recorder and resets the recorder back to the 
 * beginning of the screen.
 */
public class ChartRecorder extends Applet
{
	// this is true if the graph has not yet started or when the image has been
	// cleared
	private boolean firstTime = true;
	
	/**  Causes a complete redrawing of the applet
	 */
	public void redraw() 
	{ 
		firstTime = true; 
		repaint();
	}
	
	public synchronized void zoomAxis(int whichAxis, float scaleFactor, boolean redraw)
	{
		if (scaleFactor!= 0)
		{
			float k = (scaleFactor - 1) * topmostY[ whichAxis ] * appHeight * xOffset / ( scaleFactor * lengthY[ whichAxis ] );
		
			lengthY[whichAxis] *= scaleFactor;
			topmostY[whichAxis] *= scaleFactor;
	
			for (int x = 0; x < plotPoint; x++)
			{
				plots[whichAxis].dataPoints[x] = (int)(k + plots[whichAxis].dataPoints[x] / scaleFactor);
			}
			
			if ( redraw ) redraw();
		}
	}
	
 
	// the size of each data point.  The program will attempt to scan for the user's
	// screen resolution and adjust this.
	int ovalSize = 3;

	public int getOvalSize()
	{
		return ovalSize;
	}
	
	public void drawThinner()
	{
		if ( ovalSize > 1 ) ovalSize--;
		redraw();
	}
	
	public void drawThicker()
	{
		ovalSize++;
		redraw();
	}
	
	// this holds the size of the chart recorder
	private Dimension appSize;
	
	public static final int PLOT_MV = 0;
	public static final int PLOT_N = 1;
	public static final int PLOT_M = 2;
	public static final int PLOT_H = 3;
	public static final int PLOT_N4 = 4;
	public static final int PLOT_M3H = 5;
	public static final int NA_CURRENT = 6;
	public static final int K_CURRENT = 7;
	
	//going over this will cause an array out of bounds exception
	public static final int MAXSIZE = 1400;

	// the size of all these arrays!   Kept here just for speed (since also acessible by array.length)
	public final int plotSize = 8;
	
	// in msecs
	public synchronized void setSizeofTimeMarker ( int TimeMarker ) { sizeOfTimeMarker = TimeMarker; }
	public synchronized int getSizeofTimeMarker () { return sizeOfTimeMarker; } 
	int sizeOfTimeMarker = 2;
	
	// the color of each plot
	public Color colorArray[] = { Color.red, Color.blue, Color.darkGray, Color.orange.darker(),
			Color.red.darker(), Color.magenta, Color.blue.darker().darker(), Color.magenta.darker().darker()};

	// the length of each Y axis
	public float lengthY[] = { (float)180, (float)1, (float)1, (float)1, (float) 1, (float) 1, (float) 4000, (float) 4000};
	
	// the topmost value of each Y axis
	public float topmostY[] = { (float)80, (float)1, (float)1, (float)1, (float) 1, (float) 1, (float) 2000, (float) 2000};
	
	// the name of each plot
	public String names[] = {"Voltage (mV)" , "N", "M", "H", "K cond (n^4)", "Na cond (m^3*h)", "Na current", "K current"};
	
	// whether each plot is currently shown
	public boolean plot[] = { true, false, false, false, false, false, false, false};
	
	// the actual data
	public plotInfo plots[] = new plotInfo[plotSize];
	
	public static final Color BACKCOLOR = Color.white;  // background color
	public static final Color AXISCOLOR = Color.black;  // axis color
    
	// which right Axis is being shown 
	public static final int NONE = 0;
	public static final int PARAMS = 1;
	public static final int CURRENTS = 2;
	
	private int rightAxis = PARAMS;
	
	/**
	Will return which right axis is currently being graphed.
	Valid responses right now are ChartRecorder.NONE, ChartRecorder.PARAMS or ChartRecorder.CURRENTS.
	*/
	public int getRightAxis() { return rightAxis; }

	/**  
	Sets the right axis to be graphed.  Valid input right now is either ChartRecorder.NONE, 
	ChartRecorder.PARAMS or ChartRecorder.CURRENTS.
	*/
	public void setRightAxis( int inAxis )
	{
		rightAxis = inAxis;
		redraw();
	}
	
	
	// these are used for double buffering
	private Image offImage;
	private Graphics offGraphics;
    
	Image dataImage;
	Graphics dataGraphics;
	
	// this is the  percentage in the x dimension allowed for the graph.
	// the rest is saved for the x axis and label
	private float xOffset = (float) 0.95;
	
	// this is the percentage in the y dimension that is allowed for the graph
	// the rest is split between the two y-axes and labels
	private float yOffset = (float) 0.90;
	private float splitYoffset = (float)((1.0 - yOffset) / 2.0);
	
	// tick length (expressed as a fraction of the width of the graph)
	private float tickLength = (float) 0.01;
   
	// this holds the place on the x-axis where the graph is
	// currently being drawn. when this equals the width of the applet, 
	// Finished() will return true
	private int plotPoint = 0;
	
	// save some space at the right side (in pixels).  This space is for a border for the Configuration Panel. 
	private final int rightOffset = 25;
	
	// the number of pixels to the left and right of the actual plot (including the left axis and label)
	int leftPixelOffset; 
	int rightPixelOffset;
	
	// the width of the rectangle used for plotting data (the height is just bottomY)
	int dataWidth;

	
	// these hold information the size of things that we can draw on
	int appWidth;
	int appHeight;
	int leftX;
	int rightX;
	int bottomY;
	int tickSize;
	
	// These hold font info 
	Font font;
	FontMetrics fm;
	
	// how many pixels to offset the data image.
	public int scrollOffset = 0;
	
	private float timePerClick = (float) 0.05;
	public synchronized float getTimePerClick() 
	{ 
		return timePerClick;
	}
	public synchronized void setTimePerClick( float timePerClick) 
	{	
		this.timePerClick = timePerClick;	
	}
	
	/**  Advances the ChartRecorder one time click.  Does not check to see if the plot has
	 *   run out of the room on the screen.  Call Finished() to find that out.
	 *   @see  ChartRecorder#setTimePerClick( float ) 
	 *   @see  ChartRecorder#Finished()
	 */
	public void advance()
	{
		plotPoint++;
		
		scrollOffset = Math.min(0, dataWidth - plotPoint);
		
		// if the buffer is running low, refresh it.
		// (will also be called when the user hits stop()
		if ( plotPoint >= dataWidth * 2.1 )
		{
			recycleBuffer();
			redraw();
		}
	}
	
	private synchronized void recycleBuffer()
	{

		int index;
				
		// Grab some new memory to stuff the old data into
		plotInfo newPlots[] = new plotInfo[plotSize];
		for (int x = 0; x < plotSize; x++)
		{
			newPlots[x] = new plotInfo();
			index = 0;
			for (int y = plotPoint - dataWidth; y <= plotPoint; y++)
			{
				newPlots[x].dataPoints[index++] = plots[x].dataPoints[y];
			}
		}
		
		// swap the new memory for the old
		plots = newPlots;
		newPlots = null;
		plotPoint = dataWidth;
		
	}
	
	ModelApplet parent;
	
	public ChartRecorder(ModelApplet inParent)
	{
		super();
		parent = inParent;
		
		for (int x = 0; x < plotSize; x++)
			plots[x] = new plotInfo();
		
	}
		
   	/* Plots the data point inDatum at the current plotPoint.
	   whichToPlot is one of the public static final int plotX's defined
	in this class
	*/
	public synchronized void Plot( float inDatum , int whichToPlot )
	{
		int y = (int) ((topmostY[whichToPlot] - inDatum) * this.appHeight * xOffset / lengthY[whichToPlot] );
		
		if (plot[whichToPlot])
		{
			dataGraphics.setColor( colorArray[ whichToPlot ] );    
			dataGraphics.fillOval( plotPoint , y, ovalSize, ovalSize );
		}
		
		plots[whichToPlot].dataPoints[plotPoint] = y;
		repaint();

	}
    
	/** clears the image */
	public synchronized void Clear()
	{
		firstTime = true;
		plotPoint = 0;
		
		for (int x = 0; x < plotSize; x++)
			plots[x] = new plotInfo();

		repaint();
	}
    
 public synchronized void update(Graphics g)
	{
		if (firstTime)
		{
			if ( offGraphics != null) offGraphics.dispose();
			
			appSize = size();
			offImage = createImage(appSize.width, appSize.height);
			offGraphics = offImage.getGraphics();
			offGraphics.setColor(BACKCOLOR);
			offGraphics.fillRect(0, 0, appSize.width, appSize.height);
        
			//draw and label the y axis
			offGraphics.setColor(AXISCOLOR);
			appWidth = size().width;
			appHeight = size().height;
			leftX = (int) ( appWidth * splitYoffset );
			rightX = (int) ( appWidth - appWidth * splitYoffset ) - rightOffset;
			bottomY = (int) (appHeight * xOffset);
			tickSize = (int) (tickLength * appWidth);
		
			font = new Font("sansserif", Font.BOLD, 12);
			fm = getFontMetrics(font);
			offGraphics.setFont(font);
		
			// add 8 y-axis tick marks
			int yCounter = 1;
			String s;
			float y; 
			int yPlace;
			for (y = bottomY - bottomY/9.0f; 
								y >=  bottomY/9.0f; 
							y-= bottomY/9.0f )
			{
				offGraphics.drawLine(leftX, (int) y, leftX - tickSize, (int) y);
				s = "" + (int) ( (topmostY[PLOT_MV] - lengthY[PLOT_MV]) +  yCounter * lengthY[PLOT_MV]  / 9.0);
				offGraphics.drawString(s, leftX - tickSize - fm.stringWidth(s)-1 , (int)(y + 3) );
				yCounter++;
			}
			
			offGraphics.setColor(AXISCOLOR);
			offGraphics.drawLine( rightX, 0, rightX, bottomY );
		
			// draw the 0 to 1 (right) y axis 
			if ( rightAxis == PARAMS )
			{
				yCounter = 1;
				for (y = bottomY - bottomY/10.0f; 
								y >= bottomY/10.0f; 
							y-= bottomY/10.0f )
				{
					offGraphics.drawLine(rightX, (int) y, rightX + tickSize, (int) y);
					s = "" +  ((topmostY[PLOT_N] - lengthY[PLOT_N]) +  yCounter * lengthY[PLOT_N]  / 10.0);
					offGraphics.drawString(s, rightX +  fm.stringWidth(s) - 5 , (int)(y + 3) );
					yCounter++;
				}
			}
			
			// draw the currents (right) axis
			if ( rightAxis == CURRENTS )
			{
				yCounter = 1;
				for (y = bottomY - bottomY/10.0f; 
								y >= bottomY/10.0f; 
							y-= bottomY/10.0f )
				{
					offGraphics.drawLine(rightX, (int) y, rightX + tickSize, (int) y);
					s = "" +  ((topmostY[NA_CURRENT] - lengthY[NA_CURRENT]) +  yCounter * lengthY[NA_CURRENT]  / 10.0);
					offGraphics.drawString(s, rightX + 5 , (int) (y + 3) );
					yCounter++;
				}
			}

			leftPixelOffset = (int) (appWidth * splitYoffset);
			rightPixelOffset = (int) (rightOffset + appWidth * splitYoffset);
			dataWidth = appWidth - leftPixelOffset - rightPixelOffset - 1;
			
			if (dataGraphics != null) dataGraphics.dispose();
			dataImage = createImage( (int) (dataWidth * 2.5)  , bottomY + 2 );
			dataGraphics = dataImage.getGraphics();
			dataGraphics.setColor( BACKCOLOR );
			dataGraphics.fillRect( 0, 0, (int) (dataWidth * 2.5), bottomY + 2 );
			
			offGraphics.clipRect( leftPixelOffset, 0, dataWidth, bottomY + 2);
			
			// draws all the old data and the labels
			drawHistory();
				
			firstTime = false;
		}
        
		offGraphics.drawImage( dataImage , leftPixelOffset + scrollOffset, 0, null);
		drawLabels();
		g.drawImage(offImage, 0, 0, null);
        
	}
    
	public void drawLabels()
	{
		int x;
		int yPlace = 10;
		
		//say what the left axis will graph (always the first in the array)
			if (plot[0])
			{
				offGraphics.setColor(colorArray[0]);
				offGraphics.drawString(names[0], leftX+5 , 10);
			}
		
		if (rightAxis == CURRENTS)
		{
				// add labels for any graphs using this axis
				// depends on all the graphs using this axis being in order
				for ( x = NA_CURRENT; x <= K_CURRENT; x++)
				{
					if ( plot[x] )
					{
						offGraphics.setColor(colorArray[x]);
						offGraphics.drawString(names[x] + " (uA)", rightX - fm.stringWidth(names[x]) - 33, yPlace);
						yPlace+= fm.getHeight() + 3;
					}
				}
		}
		else if ( rightAxis == PARAMS )
		{
			yPlace = 10;
				for ( x = PLOT_N; x <=PLOT_M3H; x++)
				{
					if ( plot[x] )
					{
						offGraphics.setColor(colorArray[x]);
						offGraphics.drawString(names[x], rightX - fm.stringWidth(names[x]) - 5, yPlace);
						yPlace+= fm.getHeight() + 3;
					}
				}	
		}
			
		//draw the x axis marker
		offGraphics.setColor( AXISCOLOR );
		offGraphics.drawLine( (int) (rightX * 0.3), (int) (bottomY * 0.1), 
				(int) (rightX * 0.3 + sizeOfTimeMarker/timePerClick), (int) (bottomY * 0.1));

		offGraphics.drawLine( leftX, bottomY + 1, rightX, bottomY + 1);
		// draw the left y axis
		offGraphics.drawLine( leftX, 0, leftX, bottomY);
		
		offGraphics.setColor(Color.black);
		offGraphics.drawString(sizeOfTimeMarker + " msec", (int)(rightX * 0.3), (int)(bottomY * 0.1 + fm.getHeight())) ;
		
	}
	public void drawHistory()
	{	
		//draw the old data
		int start = 0;
		
		// if this is true, we only need to draw the last dataWidth's worth
		if (plotPoint > dataWidth)
		{
			start = plotPoint - dataWidth;
		}
			
		
		for (int y = 0; y < plotSize; y++)
		{
			if (plot[y] == true)
			{
				dataGraphics.setColor( colorArray[ y ] );    
				for (int x = start; x < plotPoint; x++)
				{
					dataGraphics.fillOval( x , plots[y].dataPoints[x], ovalSize, ovalSize );	
				}
			}			
		}
	}
	
	public void paint(Graphics g)
	{
		update(g);
	} 
  
}

class plotInfo
{
	int[] dataPoints = new int[ChartRecorder.MAXSIZE];
}
	
