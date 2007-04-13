/* ====================================================================
 * Copyright (c) 2001-2003 OYOAHA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The names "OYOAHA" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. 
 *    For written permission, please contact email@oyoaha.com.
 *
 * 3. Products derived from this software may not be called "OYOAHA",
 *    nor may "OYOAHA" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OYOAHA OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oyoaha.swing.plaf.oyoaha.ui;

import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class OyoahaColorChooserUI extends BasicColorChooserUI
{
    protected JColorChooser chooser;
    protected JPanel previewPanelHolder;
    protected JComponent previewPanel;

    public static ComponentUI createUI(JComponent c)
    {
	return new OyoahaColorChooserUI();
    }

    public void installUI( JComponent c )
    {
        chooser = (JColorChooser)c;
        super.installUI( c );
    }

    public void uninstallUI( JComponent c )
    {
        super.uninstallUI( c );
        defaultChoosers = null;
	    chooser = null;
    }

    protected void uninstallListeners()
    {
	if(chooser!=null)
        {
          chooser.removePropertyChangeListener(propertyChangeListener);
	  chooser.getSelectionModel().removeChangeListener(previewListener);
        }
    }

    protected void installPreviewPanel()
    {
        previewPanelHolder = new JPanel();
        previewPanelHolder.setLayout(new FlowLayout(FlowLayout.CENTER));
	String previewString = UIManager.getString("ColorChooser.previewText");
	previewPanelHolder.setBorder(new TitledBorder(previewString));

	previewPanel = chooser.getPreviewPanel();

	if (previewPanel == null || previewPanel instanceof UIResource)
        {
	  previewPanel = new OyoahaColorPreviewPanel();
	}

	previewPanel.setForeground(chooser.getColor());
	previewPanelHolder.add(previewPanel);
	chooser.add(previewPanelHolder, BorderLayout.SOUTH);
    }

    protected void installListeners()
    {
        propertyChangeListener = createPropertyChangeListener();
	chooser.addPropertyChangeListener( propertyChangeListener );

	previewListener = new PreviewListener();
	chooser.getSelectionModel().addChangeListener(previewListener);
    }

    protected AbstractColorChooserPanel[] createDefaultChoosers()
    {
        String n = UIManager.getString("ColorChooser.swatchesNameText");
        AbstractColorChooserPanel[] panels = super.createDefaultChoosers();

        for(int i=0;i<panels.length;i++)
        {
          if(n.equalsIgnoreCase(panels[i].getDisplayName()))
          {
            panels[i] = new OyoahaSwatchChooserPanel();
          }
        }

	return panels;
    }

    protected class PreviewListener implements ChangeListener
    {
        public void stateChanged( ChangeEvent e )
        {
	    ColorSelectionModel model = (ColorSelectionModel)e.getSource();

	    if (previewPanel != null)
            {
	        previewPanel.setForeground(model.getSelectedColor());
		previewPanel.repaint();
	    }
	}
    }
}