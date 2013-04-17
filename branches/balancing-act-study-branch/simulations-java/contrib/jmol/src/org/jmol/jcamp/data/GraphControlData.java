package org.jmol.jcamp.data;

public class GraphControlData {
  double realFistX;
  double realLastX;
  double lastRealFirstX;
  double lastRealLastX;
  double lastFirstX;
  double lastLastX;
  
  double savFirstX;
  double savLastX;
  
  boolean containsClickablePeaks; // Variable to indicate the presence of clickable peaks in the data file provided
  int numberOfClickablePeaks; // Number of clickable peaks present in the file
  
  double peakStart[]; //Starting points of ranges of clickable peaks
  double peakStop[]; //Ending points of ranges of clickable peaks
  double peakHtml[]; 
  
  
  
  
}
