// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.nuclearphysics.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
 
public class ImgShapeTester extends JFrame
{
	public static void main(String[] args) {new ImgShapeTester();}
 
	// The panel where the shape is to be drawn
	private JPanel shapePan = new JPanel() {
		public void paintComponent(Graphics g)
		{
			super.paintComponents(g);
			Graphics2D gg=(Graphics2D)g;
			gg.setPaint(Color.blue);
			// Draw the img in the middle
			gg.fill(shape);
		}
	};
	
	// The panel where the image is to be drawn
	private JPanel imgPan = new JPanel() {
		public void paintComponent(Graphics g)
		{
			super.paintComponents(g);
			// Draw the img in the middle
			g.drawImage(img,(getWidth()/2)-(img.getWidth()/2),
					(getHeight()/2)-(img.getHeight()/2),null);
		}
	};
	
	// The img to draw
	private BufferedImage img=null;
	// The shape to draw
	private Shape shape=null;
	
	public ImgShapeTester()
	{
		loadImage();
		setupControls();
		
		this.setSize(800,600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	private void loadImage()
	{
		try {
			img=NuclearPhysicsResources.getImage("tree_1.png");
		} catch (Exception ex) {ex.printStackTrace();}
		// Get the shape from this img (381,564 =shapePan w,h)
		shape=getShape(img,new Point((381/2)-(img.getWidth()/2),
				(564/2)-(img.getHeight()/2)), 128);
	}
	
	private void setupControls()
	{
		// The split compo
		JSplitPane split = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT,imgPan,shapePan);
		// Half way
		split.setDividerLocation(400);
		// Add it
		this.getContentPane().add(split);
	}
	
	private Shape getShape(BufferedImage img, Point2D pos, int limit)
	{
		// Get the data
		Raster data=img.getData();
		// The area of the shape, start with bounds 
		Area area=new Area();
		// The pixel being checked
		int[] lookAt=null;
		// The alpha value
		int alpha;
		// The start point of the line to subtract
		Point2D start=null;
		// The end point of the line to subtract
		Point2D end=null;
		// All the lines to subtract from the area
		ArrayList lines=new ArrayList();
		
		// Go round height
		for (int y=0;y<data.getHeight();y++)
		{
			// Go round width
			for (int x=0;x<data.getWidth();x++)
			{
				// Get the colour
				lookAt=data.getPixel(x,y,lookAt);
				// The alpha
				alpha=lookAt[3];
				// If > then 0
				if (alpha<=limit)
				{
					// If start is null
					if (start==null)
					{
						// Set the start
						start=new Point2D.Double(x,y);
					} // If at end
					else if (x==data.getWidth())
					{
						// This is the end
						end=new Point2D.Double(x+1,y);
						// Draw the line
						lines.add(new Line2D.Double(start,end));
						// Reset
						start=null;
						end=null;
					}
				} // A colour
				else
				{
					// If not first
					if (x>0)
					{
						// Get the colour of the one before
						lookAt=data.getPixel(x-1,y,lookAt);
						// The alpha
						alpha=lookAt[3];
						// If less then the limit and start is somthing
						if (start!=null&&alpha<=limit)
						{
							// This is the end
							end=new Point2D.Double(x-1,y);
							// Draw the line
							lines.add(new Line2D.Double(start,end));
							// Reset
							start=null;
							end=null;
						}
					}
				}
			}
			// The line bounds
			Rectangle b;
			// Go round all the lines
			for (int i=0;i<lines.size();i++)
			{
				// Get the line
				b=((Line2D)lines.get(i)).getBounds();
				// Add the line
				area.add(new Area(b));
			}
			// Reset
			lines.clear();
		}
		// Return the area moved to the desired location.
		return area.createTransformedArea( AffineTransform.getTranslateInstance(pos.getX(),pos.getY()));
	}
}
