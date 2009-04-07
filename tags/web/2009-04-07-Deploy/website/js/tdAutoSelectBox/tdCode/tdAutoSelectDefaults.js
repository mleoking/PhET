/*****************************************************************
 *
 *  This file contains the default variables for input parameters to 
 *  createSelectBox function. Do not modify this file directly, unless 
 *  you want to set site-wide default values.  You should override all 
 *  of these for each unique auto-select box you create on your page.
 *  Please see the documentation for more information.
 *
 *****************************************************************/

var def_SelectBoxID = "AutoSelectBox";
var def_ValList = ""; 
var def_ValArr = ""; 
var def_Maxwidth = "100"; //pixils
var def_Maxchar = "30";
var def_Maxheight = "110"; //pixils
var def_AutoTrunc = true;
var def_Freetype = false;
var def_DefaultValue = "";
var def_DefaultName = "";
var def_OnSelect = "";
var def_MultiBoxID = "MultiSelectBox";
var def_Multiwidth = "100"; //pixils
var def_MultiDefList = "";
var def_MultiDefArr = ""; 

/* Set PathToImages to the path to the directory where you have 
 * the images. Start with a / to point to the root of the website 
 * and enter the folder where the images exists,
 * but do not end with a /
 *
 * Currently, this only matters for the drop-down arrow image
 * and the clear.gif transparent spacer image.
 * If the drop-arrow image is not showing, you may have this
 * value set incorrectly -- so double-check.
 *
 * By default, this should be "/tdAutoSelectBox/css"
 *
 * Also, do not change the variable name itself
 */
var def_PathToImages = "tdAutoSelectBox/css"