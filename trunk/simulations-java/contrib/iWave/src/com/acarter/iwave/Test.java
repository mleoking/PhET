/**
 * Copyright (c) 2008, Andrew Carter
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of 
 * conditions and the following disclaimer. Redistributions in binary form must reproduce 
 * the above copyright notice, this list of conditions and the following disclaimer in 
 * the documentation and/or other materials provided with the distribution. Neither the 
 * name of Andrew Carter nor the names of contributors may be used to endorse or promote 
 * products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.acarter.iwave;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.acarter.iwave.IWave.PaintMode;

/**
 * @author Carter
 * 
 */
public class Test {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/**
	 * 
	 */
	private static void createAndShowGUI() {

		JFrame frame = new JFrame("iWave Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new WavePanel());
		frame.pack();
		frame.setVisible(true);

	}
}

class WavePanel extends JPanel {

	/**  */
	private static final long serialVersionUID = 1L;

	private static final int dimension = 200;

	private static final int renderScale = 4;

	private final IWave wave;

	private BufferedImage bufImage = null;

	/**
	 * 
	 */
	public WavePanel() {

		wave = new IWave(dimension);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();

		// Create an image that does not support transparency
		bufImage = gc.createCompatibleImage(dimension, dimension, Transparency.OPAQUE);

		setBorder(BorderFactory.createLineBorder(Color.black));

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				if ((e.getButton() == MouseEvent.BUTTON2) || (e.getButton() == MouseEvent.BUTTON3))
					wave.setPaintMode(PaintMode.PAINT_OBSTRUCTION);
				else
					wave.setPaintMode(PaintMode.PAINT_SOURCE);

				wave.xmouse_prev = e.getX();
				wave.ymouse_prev = e.getY();

				wave.dabSomePaint(e.getX() / renderScale, dimension - (e.getY() / renderScale));

			}
		});

		addMouseMotionListener(new MouseMotionListener() {

			public void mouseMoved(MouseEvent e) {
			}

			public void mouseDragged(MouseEvent e) {

				wave.xmouse_prev = e.getX();
				wave.ymouse_prev = e.getY();

				wave.dabSomePaint(e.getX() / renderScale, dimension - (e.getY() / renderScale));
			}
		});

		final JPanel panel = this;

		new Thread() {

			public void run() {

				long time = System.currentTimeMillis();
				
				while (true) {
					
					float tpf = (System.currentTimeMillis() - time) / 1000.0f;
					time = System.currentTimeMillis();

					wave.propagate(tpf);
					bufImage = wave.converToDisplay(bufImage);

					panel.repaint();
				}
			}
		}.start();
	}

	/**
	 * 
	 */
	public Dimension getPreferredSize() {
		return new Dimension(dimension * renderScale, dimension * renderScale);
	}

	HashMap<Integer, Color> colorMap = new HashMap<Integer, Color>();

	/**
	 * 
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(bufImage, 0, 0, dimension * renderScale, dimension * renderScale, this);
	}
}
