/*****************************************************************
 *  tdAutoSelectBox version 2.1
 *  Copyright (C) 2006 Tetraktys Development. All Rights Reserved. 
 *
 *  WARNING: This software program is protected by copyright law 
 *  and international treaties. It has been released to the
 *  public as "DonationWare", but has not been released on
 *  the General Public License (open source).
 *
 *  Please visit http://www.tetraktysdevelopment.com
 *  for more information.
 *
 *  This file has been compressed for optimization
 *****************************************************************/
var clientOpera = navigator.userAgent.indexOf("Opera") > -1; var clientIE = navigator.userAgent.indexOf("MSIE") > 1 && !clientOpera; var clientMozilla = navigator.userAgent.indexOf("Mozilla/5.") == 0 && !clientOpera; var isSelectingField; function highlightText(textboxObj, iStart, iEnd) { switch(arguments.length) { case 1:
textboxObj.select(); break; case 2:
iEnd = textboxObj.value.length; case 3:
if (clientIE) { var oRange = textboxObj.createTextRange(); oRange.moveStart("character", iStart); oRange.moveEnd("character", -textboxObj.value.length + iEnd); oRange.select();} else if (clientMozilla || clientOpera){ textboxObj.setSelectionRange(iStart, iEnd);}
}
textboxObj.focus();}
function updateTextbox(textboxObj, sText) { if (clientIE ) { var oRange = document.selection.createRange(); oRange.text = sText; oRange.collapse(true); oRange.select();} else if (clientMozilla || clientOpera) { var iStart = textboxObj.selectionStart; textboxObj.value = textboxObj.value.substring(0, iStart) + sText + textboxObj.value.substring(textboxObj.selectionEnd, textboxObj.value.length); textboxObj.setSelectionRange(iStart + sText.length, iStart + sText.length);}
textboxObj.focus();}
function autoSelectHandler(textboxObj, oEvent, SelectBoxObj) { switch (oEvent.keyCode) { case 38:
case 40:
case 37:
case 39:
case 33:
case 34:
case 36:
case 34:
case 35:
case 39:
case 13: isSelectingField=false; hideSelectBox(SelectBoxObj); return false; case 9: isSelectingField=false; hideSelectBox(SelectBoxObj); case 27:
case 16:
case 17:
case 18:
case 20:
return true; break; default:
if ((oEvent.keyCode==8 || oEvent.keyCode==46 ) ) { if (textboxObj.value!="") { return true;}
}
selectBoxDiv = document.getElementById("h"+SelectBoxObj.selectBoxID); updateTextbox(textboxObj, String.fromCharCode(clientIE||clientOpera ? oEvent.keyCode : oEvent.charCode)); var iLen = textboxObj.value.length; var sMatch = getMatchIndex(textboxObj.value, SelectBoxObj.valArr); if (sMatch != null) { textboxObj.value = SelectBoxObj.valArr[sMatch][0]; selectBoxDiv.value = SelectBoxObj.valArr[sMatch][1]; highlightText(textboxObj, iLen, textboxObj.value.length); popupSelectBox(textboxObj, oEvent, SelectBoxObj);} else if (SelectBoxObj.freetype) { textboxObj.value = textboxObj.value; selectBoxDiv.value = textboxObj.value; isSelectingField = false; hideSelectBox(SelectBoxObj);} else { iLen = iLen-1; sMatch = getMatchIndex(textboxObj.value.substring(0, iLen), SelectBoxObj.valArr); if (sMatch != null) { textboxObj.value = SelectBoxObj.valArr[sMatch][0]; selectBoxDiv.value = SelectBoxObj.valArr[sMatch][1]; highlightText(textboxObj, iLen, textboxObj.value.length); popupSelectBox(textboxObj, oEvent, SelectBoxObj); if (SelectBoxObj.freetype) { textboxObj.value = textboxObj.value; selectBoxDiv.value = textboxObj.value; isSelectingField = false; hideSelectBox(SelectBoxObj);}
}
}
return false;}
}
function checkForEmpty(textboxObj, oEvent, SelectBoxObj) { if ((oEvent.keyCode==8 || oEvent.keyCode==46 ) ) { if (textboxObj.value!="") { return true;} else { return popupSelectBox(textboxObj, oEvent, SelectBoxObj);}
}
}
function getMatchIndex(sText, valArr) { for (var i=0; i < valArr.length; i++) { thisValue = ""+valArr[i][0]; if (thisValue.toLowerCase().indexOf(sText.toLowerCase()) == 0) { return i;}
}
return null;}
function popupSelectBox(textboxObj, oEvent, SelectBoxObj) { if (SelectBoxObj.onSelect=="") { popupSelectBoxFast(textboxObj, oEvent, SelectBoxObj)
} else { var n,i; var popupSelectTable; var popupSelectArr = new Array(); i=0; n=0; if (SelectBoxObj.maxheight > 0) { selectBoxDiv = document.getElementById("p"+SelectBoxObj.selectBoxID); popupSelectArr[popupSelectArr.length] = "<table id='t"+SelectBoxObj.selectBoxID+"' border=0 class='autoSelectTable'>"; re = new RegExp(/%20/ig); onSelectEsc = SelectBoxObj.onSelect.replace(re, "\""); nameFound = false; for (var i=0; i < SelectBoxObj.valArr.length; i++) { if (SelectBoxObj.autotrunc) { if (n <= SelectBoxObj.valArr.length) { thisValue = ""+SelectBoxObj.valArr[i][0]; if (thisValue.toLowerCase().indexOf(textboxObj.value.toLowerCase()) == 0) { nameFound = true;}
if (nameFound==true) { n=n+1; popupSelectArr[popupSelectArr.length]= "<tr><td "; popupSelectArr[popupSelectArr.length]= " onMouseOver='return mouseOverSelectBoxField(this);' onMouseOut='return mouseOutSelectBoxField(this);' onClick='clickOnSelectBoxField("+SelectBoxObj.name+", \""+SelectBoxObj.valArr[i][1]+"\");"; if (onSelectEsc=="") { popupSelectArr[popupSelectArr.length]= "'";} else { popupSelectArr[popupSelectArr.length]= onSelectEsc + "'";}
popupSelectArr[popupSelectArr.length]= ">" + SelectBoxObj.valArr[i][0] + "</td></tr>";}
} else { break;}
} else { popupSelectArr[popupSelectArr.length]= "<tr><td "; popupSelectArr[popupSelectArr.length]= " onMouseOver='return mouseOverSelectBoxField(this);' onMouseOut='return mouseOutSelectBoxField(this);' onClick='clickOnSelectBoxField("+SelectBoxObj.name+", \""+SelectBoxObj.valArr[i][1]+"\"); "; if (onSelectEsc=="") { popupSelectArr[popupSelectArr.length]= "'";} else { popupSelectArr[popupSelectArr.length]= onSelectEsc + "'";}
popupSelectArr[popupSelectArr.length]= ">" + SelectBoxObj.valArr[i][0] + "</td></tr>";}
}
popupSelectArr[popupSelectArr.length]= "</table>"; var innerText = popupSelectArr.join(""); selectBoxDiv.innerHTML = innerText; selectBoxDiv.style.width=textboxObj.offsetWidth+14+'px'; selectBoxDiv.style.display='block'; selectBoxDiv.style.height=SelectBoxObj.maxheight+"px"; selectBoxTable = document.getElementById("t"+SelectBoxObj.selectBoxID); selectBoxTable.style.width = textboxObj.offsetWidth-5+'px'; if (parseInt(selectBoxTable.offsetHeight) < parseInt(SelectBoxObj.maxheight)) { selectBoxDiv.style.height = selectBoxTable.offsetHeight+1+"px";}
}
}
return true;}
function popupSelectBoxFast(textboxObj, oEvent, SelectBoxObj) { var n,i; var popupSelectTable; i=0; n=0; if (SelectBoxObj.maxheight > 0) { selectBoxDiv = document.getElementById("p"+SelectBoxObj.selectBoxID); selectBoxDiv.innerHTML = ""; oTable = document.createElement("TABLE"); oTable.id = "t"+SelectBoxObj.selectBoxID; oTable.className = "autoSelectTable"; var trElem, tdElem, txtNode; re = new RegExp(/%20/ig); onSelectEsc = SelectBoxObj.onSelect.replace(re, "\""); nameFound = false; for (i=0; i < SelectBoxObj.valArr.length; i++) { if (SelectBoxObj.autotrunc) { if (n <= SelectBoxObj.valArr.length) { thisValue = ""+SelectBoxObj.valArr[i][0]; if (thisValue.toLowerCase().indexOf(textboxObj.value.toLowerCase()) == 0) { nameFound = true;}
if (nameFound==true) { n=n+1; trElem = oTable.insertRow(oTable.rows.length); tdElem = trElem.insertCell(trElem.cells.length); tdElem.id = "t"+SelectBoxObj.selectBoxID+"_row_"+i; tdElem.onmouseover=function(){return mouseOverSelectBoxField(this);}; tdElem.onmouseout=function(){return mouseOutSelectBoxField(this);}; tdElem.onclick = function(){ var rowcount = this.id; rowcount = rowcount.substring(rowcount.lastIndexOf("_")+1); clickedValue = SelectBoxObj.valArr[rowcount][1]; hiddenValue = document.getElementById("h"+SelectBoxObj.selectBoxID); visibleValue = document.getElementById("v"+SelectBoxObj.selectBoxID); for (var z=0; z < SelectBoxObj.valArr.length; z++) { thisValue = ""+SelectBoxObj.valArr[z][1]; if (thisValue.indexOf(clickedValue) == 0) { visibleValue.value = SelectBoxObj.valArr[z][0]; hiddenValue.value = SelectBoxObj.valArr[z][1]; break;}
}
selectBoxDiv = document.getElementById("p"+SelectBoxObj.selectBoxID); selectBoxDiv.style.display='none'; return true;}; txtNode = document.createTextNode(SelectBoxObj.valArr[i][0]); tdElem.appendChild(txtNode);}
} else { break;}
} else { trElem = oTable.insertRow(oTable.rows.length); tdElem = trElem.insertCell(trElem.cells.length); tdElem.id = "t"+SelectBoxObj.selectBoxID+"_row_"+i; tdElem.onmouseover=function(){return mouseOverSelectBoxField(this);}; tdElem.onmouseout=function(){return mouseOutSelectBoxField(this);}; tdElem.onclick = function(){ var rowcount = this.id; rowcount = rowcount.substring(rowcount.lastIndexOf("_")+1); clickedValue = SelectBoxObj.valArr[rowcount][1]; hiddenValue = document.getElementById("h"+SelectBoxObj.selectBoxID); visibleValue = document.getElementById("v"+SelectBoxObj.selectBoxID); for (var z=0; z < SelectBoxObj.valArr.length; z++) { thisValue = ""+SelectBoxObj.valArr[z][1]; if (thisValue.indexOf(clickedValue) == 0) { visibleValue.value = SelectBoxObj.valArr[z][0]; hiddenValue.value = SelectBoxObj.valArr[z][1]; break;}
}
selectBoxDiv = document.getElementById("p"+SelectBoxObj.selectBoxID); selectBoxDiv.style.display='none'; return true;}; txtNode = document.createTextNode(SelectBoxObj.valArr[i][0]); tdElem.appendChild(txtNode);}
}
selectBoxDiv.appendChild(oTable); selectBoxDiv.style.width=textboxObj.offsetWidth+14+'px'; selectBoxDiv.style.display='block'; selectBoxDiv.style.height=SelectBoxObj.maxheight+"px"; selectBoxTable = document.getElementById("t"+SelectBoxObj.selectBoxID); selectBoxTable.style.width = textboxObj.offsetWidth-5+'px'; if (parseInt(selectBoxTable.offsetHeight) < parseInt(SelectBoxObj.maxheight)) { selectBoxDiv.style.height = selectBoxTable.offsetHeight+1+"px";}
}
return true;}
function hideSelectBox(SelectBoxObj) { if (!isSelectingField) { selectBoxDiv = document.getElementById("p"+SelectBoxObj.selectBoxID); selectBoxDiv.style.display='none'; hiddenValue = document.getElementById("h"+SelectBoxObj.selectBoxID); visibleValue = document.getElementById("v"+SelectBoxObj.selectBoxID); if (!visibleValue.value) { hiddenValue.value = ""; visibleValue.value = "";} else if (hiddenValue.value) { for (var i=0; i < SelectBoxObj.valArr.length; i++) { thisValue = ""+SelectBoxObj.valArr[i][1]; if (thisValue.indexOf(hiddenValue.value) == 0) { visibleValue.value = SelectBoxObj.valArr[i][0]; return true;}
}
} else { hiddenValue.value = ""; visibleValue.value = "";}
}
return true;}
function mouseOverSelectBox() { isSelectingField = true; return true;}
function mouseOutSelectBox() { isSelectingField = false; return true;}
function mouseOverSelectBoxField(oTableRow) { oTableRow.className = "autoSelectRowOn"; return true;}
function mouseOutSelectBoxField(oTableRow) { oTableRow.className = "autoSelectRowOff"; return true;}
function clickOnSelectBoxField(SelectBoxObj, clickedValue) { hiddenValue = document.getElementById("h"+SelectBoxObj.selectBoxID); visibleValue = document.getElementById("v"+SelectBoxObj.selectBoxID); for (var i=0; i < SelectBoxObj.valArr.length; i++) { thisValue = ""+SelectBoxObj.valArr[i][1]; if (thisValue.indexOf(clickedValue) == 0) { visibleValue.value = SelectBoxObj.valArr[i][0]; hiddenValue.value = SelectBoxObj.valArr[i][1]; break;}
}
selectBoxDiv = document.getElementById("p"+SelectBoxObj.selectBoxID); selectBoxDiv.style.display='none'; return true;}
function selectSelectBox(textboxObj, oEvent, SelectBoxObj) { highlightText(textboxObj, 0, textboxObj.value.length); return popupSelectBox(textboxObj, oEvent, SelectBoxObj);}
function addSelected(MultiBoxObj, SelectBoxObj) { selectBoxDiv = document.getElementById("h"+SelectBoxObj.selectBoxID); selectValue = selectBoxDiv.value; var NewArray = new Array(); var tempArray = new Array(); var alreadyOccurs=false; var foundValue=false; var i, z; i = 0; z = 0; for (var k=0; k < 1000; k++) { multiBoxElm=document.getElementById(MultiBoxObj.multiBoxID+"_row_"+k); if (multiBoxElm != null) { if (multiBoxElm.checked) { foundValue=false; if (multiBoxElm.value==selectValue) { alreadyOccurs=true;}
if (MultiBoxObj.multiDefArr != null && MultiBoxObj.multiDefArr.length>0) { for (var i=0; i < MultiBoxObj.multiDefArr.length; i++) { if (MultiBoxObj.multiDefArr[i][1] == multiBoxElm.value) { tempArray = new Array(); tempArray[0]=MultiBoxObj.multiDefArr[i][0]; tempArray[1]=MultiBoxObj.multiDefArr[i][1]; NewArray[z]=tempArray; z=z+1; foundValue=true;}
}
}
if (!foundValue) { tempArray = new Array(); thisValue = multiBoxElm.value; thisName=multiBoxElm.parentNode.innerHTML; thisName = thisName.substring(thisName.indexOf(">")+1); tempArray[0]=thisName; tempArray[1]=thisValue; NewArray[z]=tempArray; z=z+1;}
}
} else { break;}
}
foundValue=false; if (!alreadyOccurs) { if (SelectBoxObj.valArr != null && SelectBoxObj.valArr.length>0) { for (i=0; i < SelectBoxObj.valArr.length; i++) { if (SelectBoxObj.valArr[i][1] == selectValue) { tempArray = new Array(); tempArray[0]=SelectBoxObj.valArr[i][0]; tempArray[1]=SelectBoxObj.valArr[i][1]; NewArray[z]=tempArray; foundValue=true;}
}
}
if (!foundValue) { tempArray = new Array(); tempArray[0]=selectValue; tempArray[1]=selectValue; NewArray[z]=tempArray;}
}
if (NewArray.length>0) { MultiBoxObj.multiDefArr = NewArray;} else { MultiBoxObj.multiDefArr = "";}
createMultiSelectBox(MultiBoxObj, SelectBoxObj); return true;}
function delSelected(MultiBoxObj, SelectBoxObj) { selectBoxDiv = document.getElementById("h"+SelectBoxObj.selectBoxID); selectValue = selectBoxDiv.value; var NewArray = new Array(); var tempArray = new Array(); var alreadyOccurs=false; var i, z; i = 0; z = 0; for (var k=0; k < 1000; k++) { multiBoxElm=document.getElementById(MultiBoxObj.multiBoxID+"_row_"+k); if (multiBoxElm != null) { if (multiBoxElm.checked) { foundValue=false; if (multiBoxElm.value==selectValue) { alreadyOccurs=true;}
if (MultiBoxObj.multiDefArr != null && MultiBoxObj.multiDefArr.length>0) { for (i=0; i < MultiBoxObj.multiDefArr.length; i++) { if (MultiBoxObj.multiDefArr[i][1] == multiBoxElm.value) { tempArray = new Array(); tempArray[0]=MultiBoxObj.multiDefArr[i][0]; tempArray[1]=MultiBoxObj.multiDefArr[i][1]; NewArray[z]=tempArray; z=z+1; foundValue=true;}
}
}
if (!foundValue) { tempArray = new Array(); thisValue = multiBoxElm.value; thisName=multiBoxElm.parentNode.innerHTML; thisName = thisName.substring(thisName.indexOf(">")+1); tempArray[0]=thisName; tempArray[1]=thisValue; NewArray[z]=tempArray; z=z+1;}
}
} else { break;}
}
if (NewArray.length>0) { MultiBoxObj.multiDefArr = NewArray;} else { MultiBoxObj.multiDefArr = "";}
createMultiSelectBox(MultiBoxObj, SelectBoxObj); return true;}
function toggleChecked(oTableRow, checkBoxID) { checkBoxElm=document.getElementById(checkBoxID); if (checkBoxElm.checked) { oTableRow.className = "multiRowUnchecked"; checkBoxElm.checked=false;} else { oTableRow.className = "multiRowChecked"; checkBoxElm.checked=true;}
return true;}
function createMultiSelectBox(MultiBoxObj, SelectBoxObj) { var selectBoxHTML; if (MultiBoxObj.name==null||MultiBoxObj.name=="") { alert("You have not set the MultiBox Object name. Please read documentation.");}
if (MultiBoxObj.multiBoxID==null||MultiBoxObj.multiBoxID=="") { MultiBoxObj.multiBoxID = def_MultiBoxID;}
if (MultiBoxObj.multiDefList==null||MultiBoxObj.multiDefList=="") { MultiBoxObj.multiDefList = def_MultiDefList;}
if (MultiBoxObj.multiDefArr==null||MultiBoxObj.multiDefArr=="") { MultiBoxObj.multiDefArr = parseToArray(MultiBoxObj.multiDefList);}
if (MultiBoxObj.multiwidth==null||MultiBoxObj.multiwidth=="") { MultiBoxObj.multiwidth = def_Multiwidth;}
multiBoxDiv = document.getElementById(MultiBoxObj.multiBoxID); if (parseInt(multiBoxDiv.style.width) != parseInt(MultiBoxObj.multiwidth)) { MultiBoxObj.multiwidth = parseInt(MultiBoxObj.multiwidth)+14;}
selectBoxHTML = "<table id='m"+MultiBoxObj.multiBoxID+"' width='"+MultiBoxObj.multiwidth+"px' class='outerMultiSelectTable'><tr><td valign='top'>"; selectBoxHTML = selectBoxHTML + "<table class='multiSelectTable' width='"+MultiBoxObj.multiwidth+"px'>"; if (MultiBoxObj.multiDefArr != null) { if (MultiBoxObj.multiDefArr.length>0) { for (var i=0; i < MultiBoxObj.multiDefArr.length; i++) { thisName = ""+MultiBoxObj.multiDefArr[i][0]; thisValue = ""+MultiBoxObj.multiDefArr[i][1]; selectBoxHTML = selectBoxHTML + "<tr class='multiRowChecked' onclick='return toggleChecked(this, \""+MultiBoxObj.multiBoxID+"_row_"+i+"\");'><td style='width: "+MultiBoxObj.multiwidth+"px'>"; selectBoxHTML = selectBoxHTML + "<input id='"+MultiBoxObj.multiBoxID+"_row_"+i+"' class='multiCheckBoxHidden' type='checkbox' name='"+MultiBoxObj.multiBoxID+"' value='"+thisValue+"' checked />"; selectBoxHTML = selectBoxHTML + thisName +"</td></tr>";}
} else { selectBoxHTML = selectBoxHTML + "<tr><td style='width: "+MultiBoxObj.multiwidth+"px'>&nbsp;</td></tr>";}
} else { selectBoxHTML = selectBoxHTML + "<tr><td style='width: "+MultiBoxObj.multiwidth+"px'>&nbsp;</td></tr>";}
selectBoxHTML = selectBoxHTML + "</table>"; selectBoxHTML = selectBoxHTML + "</td>"; if (clientOpera) { selectBoxHTML = selectBoxHTML + "<td valign='top' nowrap>"; selectBoxHTML = selectBoxHTML + "<input class='multiBoxButtonOpera' type='button' value='Add' "; selectBoxHTML = selectBoxHTML + "onClick='if(document.images) addSelected("+MultiBoxObj.name+","+SelectBoxObj.name+");'>&nbsp;<br />"; selectBoxHTML = selectBoxHTML + "<input class='multiBoxButtonOpera' type='button' value='Del' "; selectBoxHTML = selectBoxHTML + "onClick='if(document.images) delSelected("+MultiBoxObj.name+","+SelectBoxObj.name+");'>&nbsp;<br />"; selectBoxHTML = selectBoxHTML + "</td></tr></table>";} else { selectBoxHTML = selectBoxHTML + "<td valign='top' nowrap>"; selectBoxHTML = selectBoxHTML + "<input class='multiBoxButton' type='button' value='Add' "; selectBoxHTML = selectBoxHTML + "onClick='if(document.images) addSelected("+MultiBoxObj.name+","+SelectBoxObj.name+");'><br />"; selectBoxHTML = selectBoxHTML + "<input class='multiBoxButton' type='button' value='Del' "; selectBoxHTML = selectBoxHTML + "onClick='if(document.images) delSelected("+MultiBoxObj.name+","+SelectBoxObj.name+");'><br />"; selectBoxHTML = selectBoxHTML + "</td></tr></table>";}
multiBoxDiv.innerHTML = selectBoxHTML; multiBoxDiv.style.width=MultiBoxObj.multiwidth+'px'; return true;}
function createSelectBox(SelectBoxObj) { var selectBoxHTML; if (SelectBoxObj.name==null||SelectBoxObj.name=="") { alert("You have not set the SelectBox Object name. Please read documentation.");}
if (SelectBoxObj.selectBoxID==null||SelectBoxObj.selectBoxID=="") { SelectBoxObj.selectBoxID = def_SelectBoxID;}
if (SelectBoxObj.valList==null||SelectBoxObj.valList=="") { SelectBoxObj.valList = def_ValList;}
if (SelectBoxObj.valArr==null||SelectBoxObj.valArr=="") { SelectBoxObj.valArr = parseToArray(SelectBoxObj.valList);}
if (SelectBoxObj.maxwidth==null||SelectBoxObj.maxwidth=="") { SelectBoxObj.maxwidth = def_Maxwidth;}
if (SelectBoxObj.maxchar==null||SelectBoxObj.maxchar=="") { SelectBoxObj.maxchar = def_Maxchar;}
if (SelectBoxObj.maxheight==null||SelectBoxObj.maxheight=="") { SelectBoxObj.maxheight = def_Maxheight;}
if (SelectBoxObj.pathToImages==null||SelectBoxObj.pathToImages=="") { SelectBoxObj.pathToImages = def_PathToImages;}
try { if (!SelectBoxObj.hasOwnProperty("autotrunc")) { SelectBoxObj.autotrunc = def_AutoTrunc;}
if (!SelectBoxObj.hasOwnProperty("freetype")) { SelectBoxObj.freetype = def_Freetype;}
} catch (e) { }
if (SelectBoxObj.defaultName==null || SelectBoxObj.defaultName=="") { SelectBoxObj.defaultName = def_DefaultName;}
if (SelectBoxObj.defaultValue==null || SelectBoxObj.defaultValue=="") { SelectBoxObj.defaultValue = def_DefaultValue;}
if (SelectBoxObj.onSelect==null||SelectBoxObj.onSelect=="") { SelectBoxObj.onSelect = def_OnSelect;} else { re = new RegExp(/'/gi);
        SelectBoxObj.onSelect = SelectBoxObj.onSelect.replace(re, "%20");
    }

    selectBoxDiv = document.getElementById(SelectBoxObj.selectBoxID);

    selectBoxHTML = "<input autocomplete='off' type='text' class='autoSelectTextBox' id='v"+SelectBoxObj.selectBoxID+"' maxlength='"+SelectBoxObj.maxchar+"' value='"+SelectBoxObj.defaultName+"' ";
    selectBoxHTML = selectBoxHTML + " onfocus='return selectSelectBox(this, event, "+SelectBoxObj.name+");' onKeyPress='return autoSelectHandler(this, event, "+SelectBoxObj.name+");' onblur='return hideSelectBox("+SelectBoxObj.name+");' onKeyUp='return checkForEmpty(this, event, "+SelectBoxObj.name+");' />";
    selectBoxHTML = selectBoxHTML + "<img src='" + SelectBoxObj.pathToImages + "/clear.gif' class='autoSelectDropImage' onclick='var thisTextBox=document.getElementById(\"v"+SelectBoxObj.selectBoxID+"\");return selectSelectBox(thisTextBox, event, "+SelectBoxObj.name+");' />&nbsp;&nbsp;&nbsp;";
    selectBoxHTML = selectBoxHTML + "<br /><div class='autoSelectDiv' id='p"+SelectBoxObj.selectBoxID+"'  onMouseOver='return mouseOverSelectBox();' onMouseOut='return mouseOutSelectBox();' onblur='return hideSelectBox("+SelectBoxObj.name+");'></div>";
    selectBoxHTML = selectBoxHTML + "<input type='hidden' value='"+SelectBoxObj.defaultValue+"' name='"+SelectBoxObj.selectBoxID+"' id='h"+SelectBoxObj.selectBoxID+"' />";

    selectBoxDiv.innerHTML = selectBoxHTML;

    selectTextBoxDiv = document.getElementById("v"+SelectBoxObj.selectBoxID);
    selectTextBoxDiv.style.width=SelectBoxObj.maxwidth+'px';

    return true;
}
function initRequestor() { var xmlreq = false; if (window.XMLHttpRequest) { xmlreq = new XMLHttpRequest();} else if (window.ActiveXObject) { try { xmlreq = new ActiveXObject("Msxml2.XMLHTTP");} catch (e1) { try { xmlreq = new ActiveXObject("Microsoft.XMLHTTP");} catch (e2) { xmlreq = false;}
}
}
return xmlreq;}
function waitForReadyState(req, responseFunction) { return function() { if (req.readyState == 4) { if (req.status == 200) { responseFunction(req.responseXML, req.responseText);} else { alert("An error has occured: "+req.status+": "+req.statusText);}
}
}
}
function parameterizeFormElements(formObj) { var getstr = ""; for (i=0; i<formObj.elements.length; i++) { if (formObj.elements[i].tagName == "INPUT" || formObj.elements[i].tagName == "input") { if (formObj.elements[i].name) { if (formObj.elements[i].type == "text") { getstr += formObj.elements[i].name + "=" + encodeURI(formObj.elements[i].value) + "&";}
if (formObj.elements[i].type == "checkbox") { if (formObj.elements[i].checked) { getstr += formObj.elements[i].name + "=" + encodeURI(formObj.elements[i].value) + "&";} else { getstr += formObj.elements[i].name + "=&";}
}
if (formObj.elements[i].type == "radio") { if (formObj.elements[i].checked) { getstr += formObj.elements[i].name + "=" + encodeURI(formObj.elements[i].value) + "&";}
}
if (formObj.elements[i].type == "hidden") { getstr += formObj.elements[i].name + "=" + encodeURI(formObj.elements[i].value) + "&";}
}
}
if (formObj.elements[i].tagName == "SELECT" || formObj.elements[i].tagName == "select") { var sel = formObj.elements[i]; getstr += sel.name + "=" + encodeURI(sel.options[sel.selectedIndex].value) + "&";}
}
return getstr;}
function parseToArray(sText) { var arr = new Array() ; if (sText == null) { sText = "";}
arr = sText.split("|"); for(i = 0; i < arr.length; i++) { arr2 = new Array(); if (arr[i].indexOf(";")>0) { arr2 = arr[i].split( ";" );} else { arr2[0] = arr[i]; arr2[1] = arr[i];}
arr[i] = arr2;}
return arr;}
function flattenArray(sArray) { var sFlattened = ""
if (sArray != null && sArray.length>0) { for (var i=0; i < sArray.length; i++) { if ((sArray[i][0] != null) && (sArray[i][0] != "")) { sFlattened += sArray[i][0] + ";"; if ((sArray[i][1] != null) && (sArray[i][1] != "")) { sFlattened += sArray[i][1] + "|";} else { sFlattened += sArray[i][0] + "|";}
}
}
}
return sFlattened;}
