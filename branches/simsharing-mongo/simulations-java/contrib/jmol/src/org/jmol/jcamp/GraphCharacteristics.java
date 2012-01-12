/*
 * File Name   : GraphCharacteristics.java
 * Description : This class contains and modifies a list of spectrometer plot controlling factors. Ideally,
 *               all manipulation or setting of values controlling the look and feel of the graphs should
 *               flow through the data elements in this class.
 * Author      : Shravan Sadasivan
 * Organization: Department Of Chemistry,
 *				 The Ohio State University
 */

package org.jmol.jcamp;

import java.util.*;
import java.text.DecimalFormat;

public class GraphCharacteristics{
	private static final String INTEGRATION_VALUES_DELIM = ":";
	private static final String INTEGRATION_DELIM = ",";
	private static final int MAX_DECIMALS = 2;

  private boolean _zoomIn; /* Parameter to indicate if "Zoom In" is desired on the graph */
  private boolean _integrate; /* Parameter to indicate if "Integration" is desired on the graph */
  private boolean _grid; /* Parameter to indicate if "Grid" is desired on the graph */
  private boolean _reverse; /* Parameter to indicate if "Reverse" is desired on the graph */
  private String _allIntegrationValues = null;
  private Map _integrationValues = null; /* Sorted mapping of integration curve values to be printed */
  private ArrayList _unsortedIntegrationValues = null; /* Unsorted integration curve value strings */

	//Variables to control the various colors in the elements of the graph
  private String _textColor = null;
  private String _axisColor = null;
  private String _axisTextColor = null;
  private String _integrateCurveColor = null;
  private String _integrateTextColor = null;
  private String _graphCurveColor = null;
  private DecimalFormat _decForm = null;
  private String _lastPoint = null;

  public GraphCharacteristics(){
		this._zoomIn = false;
		this._integrate = false;
		this._grid = false;
		this._reverse = false;
		this._allIntegrationValues = new String();
		this._integrationValues = new Hashtable();
		this._unsortedIntegrationValues = new ArrayList();
		this._decForm = null;
	}

    public GraphCharacteristics(boolean zoomIn,boolean integrate,boolean grid,
    					   boolean reverse,String allIntegrationValues,String axisColor,
    					    String axisTextColor,String integrateCurveColor,String graphCurveColor,
    					     String textColor, String integrateTextColor){
		this._zoomIn = zoomIn;
		this._integrate = integrate;
		this._grid = grid;
		this._reverse = reverse;
		this._allIntegrationValues = allIntegrationValues;
		setIntegrationValues(this._unsortedIntegrationValues);
		this._axisColor = axisColor;
		this._axisTextColor = axisTextColor;
		this._integrateCurveColor = integrateCurveColor;
		this._integrateTextColor = integrateTextColor;
		this._graphCurveColor = graphCurveColor;
		this._textColor = textColor;
		this._decForm = null;
	}

    /**
     * Method to create a <code>HashTable</code> of integration curve area relationship
     * to the concerned points
     */
	public void setIntegrationValues(ArrayList unsortedIntegrationValues){
		String[] _temp = null; // Output of the String split operation
		String _tempString = new String();
		Map _tempTable = new Hashtable(); // Temp Storage for values extracted from the integration values string

		for(int i=0;i<unsortedIntegrationValues.size();i++){
			_tempString = (String) unsortedIntegrationValues.get(i);
			_temp = _tempString.split(INTEGRATION_VALUES_DELIM);
			_tempTable.put(_temp[0],_temp[1]);
		}

		this._integrationValues = _tempTable;
	}

    /**
     * Method to split and create an <code>ArrayList</code> of Integration relationship values
     */
	public void setUnsortedIntegrationValues(String unsortedIntegrationValues){
		String[] _temp = unsortedIntegrationValues.split(INTEGRATION_DELIM);
		ArrayList _tempList = new ArrayList();

		for(int i=0;i<_temp.length;i++){
			_tempList.add(_temp[i]);
		}

		this._unsortedIntegrationValues = _tempList;
		setIntegrationValues(_tempList);
	}

	/**
	 *
	 */
	private String isIntegrationCurvePoint(Double point){
		String[] formats = {"####.00","####.0","####"};
		for(int i=0;i<=MAX_DECIMALS;i++){
			_decForm = new DecimalFormat(formats[i]);
			if(this._integrationValues.containsKey(_decForm.format(point)))	{
				if(_lastPoint == null || !_lastPoint.equalsIgnoreCase(_decForm.format(point))){
					_lastPoint = _decForm.format(point);
					return _decForm.format(point);
				}else{
					return null;
				}
			}
		}
		return null;
	}

	/**
	 *
	 */
	public String getIntegrationCurveAreaValue(Double point){
		String integratePeakValue = isIntegrationCurvePoint(point);

		if(integratePeakValue != null){
			return (String) this._integrationValues.get(integratePeakValue);
		}
		return null;
	}

	/**
	 * Setter and Getter methods for the 'Zoom In' option
	 */
	public void setZoomIn(boolean zoomIn){
		this._zoomIn = zoomIn;
	}

	public boolean getZoomIn(){
		return this._zoomIn;
	}

	/**
	 * Setter and Getter methods for the 'Grid' option
	 */
	public void setGrid(boolean grid){
		this._grid = grid;
	}

	public boolean getGrid(){
		return this._grid;
	}

	/**
	 * Setter and Getter methods for the 'Integrate' option
	 */
	public void setIntegrate(boolean integrate){
		this._integrate = integrate;
	}

	public boolean getIntegrate(){
		return this._integrate;
	}

	/**
	 * Setter and Getter methods for the 'Reverse' option
	 */
	public void setReverse(boolean reverse){
		this._reverse = reverse;
	}

	public boolean getReverse(){
		return this._reverse;
	}

	/**
	 * Setter and Getter methods for the 'Integration Values' option
	 */
	public void setIntegrationValues(){

	}

	/**
	 * Setter and Getter methods for the 'Axis Color' option
	 */
	public void setAxisColor(String axisColor){
		this._axisColor = axisColor;
	}

	public String getAxisColor(){
		return this._axisColor;
	}

	/**
	 * Setter and Getter methods for the 'Axis Text Color' option
	 */
	public void setAxisTextColor(String axisTextColor){
		this._axisTextColor = axisTextColor;
	}

	public String getAxisTextColor(){
		return this._axisTextColor;
	}

	/**
	 * Setter and Getter methods for the 'Integrate Curve Color' option
	 */
	public void setIntegrateCurveColor(String integrateCurveColor){
		this._integrateCurveColor = integrateCurveColor;
	}

	public String getIntegrateCurveColor(){
		return this._integrateCurveColor;
	}

	/**
	 * Setter and Getter methods for the 'Graph Curve Color' option
	 */
	public void setGraphCurveColor(String graphCurveColor){
		this._graphCurveColor = graphCurveColor;
	}

	public String getGraphCurveColor(){
		return this._graphCurveColor;
	}

	/**
	 * Setter and Getter methods for the 'Graph Text Color' option
	 */
	public void setTextColor(String textColor){
		this._textColor = textColor;
	}

	public String getTextColor(){
		return this._textColor;
	}

	public void setIntegrateTextColor(String integrateTextColor){
		this._integrateTextColor = integrateTextColor;
	}

	public String getIntegrateTextColor(){
		return _integrateTextColor;
	}
}
