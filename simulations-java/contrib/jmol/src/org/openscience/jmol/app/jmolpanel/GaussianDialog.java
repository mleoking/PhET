/* $RCSfile$
 * $Author: turnera $
 * $Date: 2007-05-08 22:52:24 +0100 (Tue, 08 May 2007) $
 * $Revision: 7642 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software        
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.jmol.app.jmolpanel;

import org.jmol.api.*;
import org.jmol.i18n.GT;
import org.jmol.viewer.Viewer;

import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

public class GaussianDialog extends JDialog implements ActionListener,
                                                       ChangeListener {

  /*
   * By Andy Turner, atrog@sourceforge.net
   * modified and integrated into Jmol by Bob Hanson, hansonr@stolaf.edu, 10/12/2008
   */
  
  JmolViewer viewer;

  private JPanel container;
  private JTextField checkField, optsField, fileField, selectField;
  private JComboBox memBox, methBox, basisBox, dfBox;
  private JSpinner procSpinner, chargeSpinner, multSpinner;
  private JButton fileButton, saveButton, cancelButton;
  private JFileChooser fileChooser;
  private JTextArea editArea;
  private JTabbedPane inputTabs;
  private String check, mem, proc, file, meth, route, charge, mult, select;
  
  private static final String DEFAULT_METHOD = "HF";
  private static final String DEFAULT_BASIS = "3-21G**";
  private static final String DEFAULT_CHARGE = "0";
  private static final String DEFAULT_MULT = "1";
  
  private static final String[] BASIS_LIST = {"Gen",
	                                            "3-21G",
	                                            "3-21G*",
	                                            "3-21G**",
	                                            "6-21G",
	                                            "4-31G",
	                                            "6-31G",
	                                            "6-311G",
	                                            "D95V",
	                                            "D95",
	                                            "SHC",
	                                            "CEP-4G",
	                                            "CEP-31G",
	                                            "CEP-121G",
	                                            "LanL2MB",
	                                            "LanL2DZ",
	                                            "SDD",
	                                            "SDDAll",
	                                            "cc-pVDZ",
	                                            "cc-pVTZ",
	                                            "cc-pVQZ",
	                                            "cc-pV5Z",
	                                            "cc-pV6Z",
	                                            "aug-cc-pVDZ",
	                                            "aug-cc-pVTZ",
	                                            "aug-cc-pVQZ",
	                                            "aug-cc-pV5Z",
	                                            "aug-cc-pV6Z",
	                                            "SV",
	                                            "SVP",
	                                            "TZV",
	                                            "TZVP",
	                                            "MidiX",
	                                            "EPR-II",
	                                            "EPR-III",
	                                            "UGBS",
	                                            "UGBS1P",
	                                            "UGBS2P",
	                                            "UGBS3P",
	                                            "MTSmall",
	                                            "DGDZVP",
	                                            "DGDZVP2",
	                                            "DGTZVP"};
  private static final String[] METHOD_LIST = {"HF",
	                                             "MP2",
	                                             "MP3",
	                                             "MP4",
	                                             "CCSD(T)",
	                                             "CIS",
	                                             "CISD",
	                                             "LSDA",
	                                             "BLYP",
	                                             "BP86",
	                                             "BPW91",
	                                             "OLYP",
	                                             "OP86",
	                                             "OPW91",
	                                             "PBEPBE",
	                                             "VSXC",
	                                             "HCTH93",
	                                             "HCTH147",
	                                             "HCTH407",
	                                             "TPSSTPSS",
	                                             "B3LYP",
	                                             "B3PW91",
	                                             "AM1",
	                                             "PM3",
	                                             "CNDO",
	                                             "INDO",
	                                             "MNDO",
	                                             "MINDO3",
	                                             "ZINDO",
	                                             "UFF",
	                                             "AMBER",
	                                             "DREIDING",
	                                             "Huckel"};
  private static final String[] DF_LIST = {"None",
	                                         "Auto",
	                                         "DGA1",
	                                         "DGA2"};
  private static final String[] MEMORY_LIST = {"Default",
	                                             "100MB",
	                                             "500MB",
	                                             "1GB",
	                                             "2GB",
	                                             "4GB",
	                                             "7GB",
	                                             "15GB"};
  
  private static final String NOBASIS_LIST = 
	  "AM1 PM3 CNDO INDO MNDO MINDO3 ZINDO UFF AMBER DREIDING Huckel";
  private static final String DFT_LIST = 
    "LSDA BLYP BP86 BPW91 OLYP OP86 OPW91 PBEPBE VSXC HCTH93 NCTH147 HCTH407 TPSSTPSS B3LYP B3PW91";

  
  public GaussianDialog(JFrame f, JmolViewer viewer) {

    super(f, false);
    this.viewer = viewer;

    setTitle(GT._("Export Gaussian Input File"));

    container = new JPanel();
    container.setLayout(new BorderLayout());
    inputTabs = new JTabbedPane();

    JPanel basicPanel = buildBasicPanel();
    inputTabs.addTab(GT._("Basic"), null, basicPanel);
    JPanel advancedPanel = buildAdvancedPanel();
    inputTabs.addTab(GT._("Advanced"), null, advancedPanel);
    
    inputTabs.addChangeListener(this);
    
    JPanel filePanel = buildFilePanel();
    JPanel buttonPanel = buildButtonPanel();
    
    container.add(inputTabs, BorderLayout.NORTH);
    container.add(filePanel, BorderLayout.CENTER);
    container.add(buttonPanel, BorderLayout.SOUTH);
    getContentPane().add(container);

    pack();
    centerDialog();
    updateUI();
  }

  private JPanel buildBasicPanel() {

    JPanel showPanel = new JPanel(new BorderLayout());
    
    JPanel linkPanel = new JPanel(new BorderLayout());
    TitledBorder linkTitle = BorderFactory.createTitledBorder("link0 Section");
    linkPanel.setBorder(linkTitle);
    
    JPanel linkLabels = new JPanel(new GridLayout(3,1));
    JPanel linkControls = new JPanel(new GridLayout(3,1));
    
    JLabel checkLabel = new JLabel(GT._("Checkpoint File: "));
    linkLabels.add(checkLabel);
    checkField = new JTextField(20);
    linkControls.add(checkField);
    
    JLabel memLabel = new JLabel(GT._("Amount of Memory:"));
    linkLabels.add(memLabel);
    memBox = new JComboBox(MEMORY_LIST);
    linkControls.add(memBox);
    memBox.setSelectedIndex(0);
    
    JLabel procLabel = new JLabel(GT._("Number of Processors:"));
    linkLabels.add(procLabel);
    SpinnerModel procModel = new SpinnerNumberModel(1, 1, 16, 1);
    procSpinner = new JSpinner(procModel);
    procSpinner.setEditor(new JSpinner.NumberEditor(procSpinner, "#"));
    linkControls.add(procSpinner);
    
    linkPanel.add(linkLabels, BorderLayout.LINE_START);
    linkPanel.add(linkControls, BorderLayout.CENTER);
    
    showPanel.add(linkPanel, BorderLayout.NORTH);
    
    JPanel routePanel = new JPanel(new BorderLayout());
    TitledBorder routeTitle = BorderFactory.createTitledBorder(GT._("Route"));
    routePanel.setBorder(routeTitle);
    
    JPanel routeLabels = new JPanel(new GridLayout(4,1));
    JPanel routeControls = new JPanel(new GridLayout(4,1));
    
    JLabel methLabel = new JLabel(GT._("Method: "));
    routeLabels.add(methLabel);
    methBox = new JComboBox(METHOD_LIST);
    routeControls.add(methBox);
    methBox.setSelectedIndex(0);
    methBox.addActionListener(this);
    
    JLabel basisLabel = new JLabel(GT._("Basis Set: "));
    routeLabels.add(basisLabel);
    basisBox = new JComboBox(BASIS_LIST);
    routeControls.add(basisBox);
    basisBox.setSelectedIndex(3);
   
    
    JLabel dfLabel = 
      new JLabel(GT._("Density Fitting Basis Set (DFT Only): "));
    routeLabels.add(dfLabel);
    dfBox = new JComboBox(DF_LIST);
    routeControls.add(dfBox);
    dfBox.setSelectedIndex(0);
    
    JLabel optsLabel = new JLabel(GT._("Job Options: "));
    routeLabels.add(optsLabel);
    optsField = new JTextField(20);
    routeControls.add(optsField);
    optsField.setText("opt");
    
    routePanel.add(routeLabels, BorderLayout.LINE_START);
    routePanel.add(routeControls, BorderLayout.CENTER);
    
    showPanel.add(routePanel, BorderLayout.CENTER);
    
    JPanel molPanel = new JPanel(new BorderLayout());
    TitledBorder molTitle =
      BorderFactory.createTitledBorder(GT._("Molecular Properties"));
    molPanel.setBorder(molTitle);
    
    JPanel molLabels = new JPanel(new GridLayout(3,1));
    JPanel molControls = new JPanel(new GridLayout(3,1));
    
    JLabel chargeLabel = new JLabel(GT._("Total Charge: "));
    molLabels.add(chargeLabel);
    SpinnerModel chargeModel = new SpinnerNumberModel(0, -10, 10, 1);
    chargeSpinner = new JSpinner(chargeModel);
    chargeSpinner.setEditor(new JSpinner.NumberEditor(chargeSpinner, "#"));
    molControls.add(chargeSpinner);
    
    JLabel multLabel = new JLabel(GT._("Multiplicity: "));
    molLabels.add(multLabel);
    SpinnerModel multModel = new SpinnerNumberModel(1, 0, 10, 1);
    multSpinner = new JSpinner(multModel);
    multSpinner.setEditor(new JSpinner.NumberEditor(multSpinner, "#"));
    molControls.add(multSpinner);
    
    JLabel selectLabel = new JLabel(GT._("Selection: "));
    molLabels.add(selectLabel);
    selectField = new JTextField(20);
    selectField.setText("visible");
    molControls.add(selectField);
   
    molPanel.add(molLabels, BorderLayout.LINE_START);
    molPanel.add(molControls, BorderLayout.CENTER);
    
    showPanel.add(molPanel, BorderLayout.SOUTH);
    
    return showPanel;
  }
  
  private JPanel buildAdvancedPanel() {
  
	  JPanel editPanel = new JPanel(new BorderLayout());
	  TitledBorder editTitle =
	    BorderFactory.createTitledBorder("Edit Gaussian Input File");
	  editPanel.setBorder(editTitle);
	
	  editArea = new JTextArea();
	  JScrollPane editPane = new JScrollPane(editArea);
	  editPane.setPreferredSize(new Dimension(150,100));
	
	  editPanel.add(editPane, BorderLayout.CENTER);
	
	  return editPanel;
	
  }
  
  private JPanel buildFilePanel() {
	
    JPanel showPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    TitledBorder fileTitle =
	  BorderFactory.createTitledBorder(GT._("Gaussian Input File Name"));
    showPanel.setBorder(fileTitle);
    
    fileField = new JTextField(30);
    showPanel.add(fileField);
    fileField.setText(new File("my_input.com").getAbsolutePath());
    fileButton = new JButton(GT._("File..."));
    fileButton.addActionListener(this);
    showPanel.add(fileButton);
    
	  return showPanel;
  }
  
  private JPanel buildButtonPanel() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

    saveButton = new JButton(GT._("Save"));
    saveButton.addActionListener(this);
    buttonPanel.add(saveButton);
    
    cancelButton = new JButton(GT._("Cancel"));
    cancelButton.addActionListener(this);
    buttonPanel.add(cancelButton);
  
    getRootPane().setDefaultButton(saveButton);
    return buttonPanel;
  }
  
  protected void centerDialog() {

    Dimension screenSize = this.getToolkit().getScreenSize();
    Dimension size = this.getSize();
    screenSize.height = screenSize.height / 2;
    screenSize.width = screenSize.width / 2;
    size.height = size.height / 2;
    size.width = size.width / 2;
    int y = screenSize.height - size.height;
    int x = screenSize.width - size.width;
    this.setLocation(x, y);
  }

  private void updateVars() {
	  check = checkField.getText();
	  mem = memBox.getSelectedItem().toString();
	  proc = procSpinner.getValue().toString();
    select = selectField.getText();
    if (select.length() == 0) {
      select = "visible";
      selectField.setText(select);
    }
	
	  charge = chargeSpinner.getValue().toString();
	  if (charge.equals("")) charge = DEFAULT_CHARGE;
	  mult = multSpinner.getValue().toString();
	  if (mult.equals("")) mult = DEFAULT_MULT;
	
	  String basis = basisBox.getSelectedItem().toString();
	  if (basis.equals("")) basis = DEFAULT_BASIS;
	  meth = methBox.getSelectedItem().toString();
	  if (meth.equals("")) meth = DEFAULT_METHOD;
	  if (NOBASIS_LIST.lastIndexOf(meth, NOBASIS_LIST.length()) >= 0) basis = "";
	  if (!basis.equals("")) basis = "/" + basis;
	  String df = dfBox.getSelectedItem().toString();
	  if (DFT_LIST.lastIndexOf(meth, DFT_LIST.length()) < 0) df = "None";
	  if (df.equals("None")) {
	    df = "";
	  } else {
	    df = "/" + df;
  	}
	
	  file = fileField.getText();
	  if (file.equals("")) file = "my_input.com";
	
  	String opts = optsField.getText();
	  route = "# " + meth + basis + df + " " + opts;
	
  }
  
  private void updateUI() {
	  updateVars();
	  if (NOBASIS_LIST.lastIndexOf(meth, NOBASIS_LIST.length()) >= 0) {
	    basisBox.setEnabled(false);
	  } else {
	    basisBox.setEnabled(true);
	  }
	  if (DFT_LIST.lastIndexOf(meth, DFT_LIST.length()) >= 0) {
      dfBox.setEnabled(true);
    } else {
      dfBox.setEnabled(false);
    }
    return;
  }
  
  private void save() {

    if (inputTabs.getSelectedIndex() == 0)
      getCommand();
    Writer output = null;
    try {
      File outputFile = new File(fileField.getText());
      fileField.setText(outputFile.getAbsolutePath());
      output = new BufferedWriter(new FileWriter(outputFile));
      output.write(editArea.getText());
      output.close();
    } catch (IOException err) {
      return;
    }
    dispose();
  }

  private void cancel() {
    dispose();
  }
  
  private void setFile() {
	  fileChooser = new JFileChooser();
    String fname = fileField.getText();
    fileChooser.setSelectedFile(new File(fname));
	  int ierr = fileChooser.showDialog(this, "Set");
	  if (ierr == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      fileField.setText(file.getAbsolutePath());
    }
  }
  
  private void tabSwitched() {
	  if (inputTabs.getSelectedIndex() == 1) {
      getCommand();
	  }
  }
  
  protected void getCommand() {
    updateVars();
    String c = check;
    if (! c.equals("")) c = "%chk=" + c + "\n";
    String m = mem;
    if (! m.equals("Default")) { 
      m = "%mem=" + m + "\n";
    } else {
      m = "";
    }
    String p = proc;
    if (! p.equals("1")) {
      p = "%nproc=" + p + "\n";
    } else {
      p = "";
    }
  
    String data = viewer.getData(select,"USER:%-2e %10.5x %10.5y %10.5z");
  
    editArea.setText(c + m + p + route + "\n\n" + 
      "Title: Created by Jmol version " + Viewer.getJmolVersion() + "\n\n" + charge + " " + mult +
      "\n" + data + "\n");
  }

  public void actionPerformed(ActionEvent event) {
	  if (event.getSource() == saveButton) {
	    save();
	  } else if (event.getSource() == cancelButton) {
	    cancel();
	  } else if (event.getSource() == fileButton) {
	    setFile();
	  } else if (event.getSource() == methBox) {
	    updateUI();
	  }
  }
  
  public void stateChanged(ChangeEvent event) {
	  if (event.getSource() == inputTabs) {
	    tabSwitched();
	  }
  }

}