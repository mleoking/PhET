// This function is used for the Suckerfish (flyout) menus

sfHover = function() {
	var sfEls = document.getElementById("nav").getElementsByTagName("LI");
	for (var i=0; i<sfEls.length; i++) {
		sfEls[i].onmouseover=function() {
			this.className+=" sfhover";
		}
		sfEls[i].onmouseout=function() {
			this.className=this.className.replace(new RegExp(" sfhover\\b"), "");
		}
	}
}
if (window.attachEvent) window.attachEvent("onload", sfHover);



// menuExpandable.js - implements an expandable menu based on a HTML list
// Author: Dave Lindquist (http://www.gazingus.org)

if (!document.getElementById)
    document.getElementById = function() { return null; }

function initializeMenu(menuId, actuatorId) {
    var menu = document.getElementById(menuId);
    var actuator = document.getElementById(actuatorId);

    if (menu == null || actuator == null) return;

    //if (window.opera) return; // I'm too tired

   actuator.parentNode.style.background = "url(../img/arrow-down-sm.gif) 90% 10px no-repeat";
    actuator.onclick = function() {
        var display = menu.style.display;
        this.parentNode.style.background =
            (display == "block") ? "url(../img/arrow-down-sm.gif) 90% 10px no-repeat" : "url(../img/arrow-up-sm.gif) 90% 10px no-repeat";
        menu.style.background = "url(/images/square.gif)";
        menu.style.display = (display == "block") ? "none" : "block";

        return false;
    }
}


// Preload images

function Preload() {
 	var args = simplePreload.arguments;
	document.imageArray = new Array(args.length);
	for(var i=0; i<args.length; i++) {
		document.imageArray[i] = new Image;
		document.imageArray[i].src = args[i];
	}
   }