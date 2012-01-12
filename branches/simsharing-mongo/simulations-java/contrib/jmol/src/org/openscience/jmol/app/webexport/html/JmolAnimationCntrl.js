/*jmolAnimationCntrl.js

J. Gutow May 2010 
A. Herraez June 2010

This includes 
- the CSS rules for the box and the buttons
- one function that controls the highlighting of the
   animation mode buttons
*/

document.writeln('<style type="text/css"> \n'
    + '  .AnimContrlCSS { text-align:center; border:thin solid black; } \n'
    + '  .AnimContrlCSS button { font-size:0px; padding:0; } \n'
    + '<' + '/style>');

function jmol_animationmode(selected, n){
    var cellID = "jmol_loop_"+n;
    document.getElementById(cellID).style.cssText = "";
    cellID = "jmol_playOnce_"+n;
    document.getElementById(cellID).style.cssText = "";
    cellID = "jmol_palindrome_"+n;
    document.getElementById(cellID).style.cssText = "";
    if (selected=="loop") {
        cellID = "jmol_loop_"+n;
        jmolScript('animation mode loop 0.2 0.2', n);
    } else if (selected=="playOnce") {
        cellID = "jmol_playOnce_"+n;
        jmolScript('animation mode once', n);
    } else if (selected=="palindrome") {
        cellID = "jmol_palindrome_"+n;
        jmolScript('animation mode palindrome 0.2 0.2', n);
    } else {
        return false; 
    }
    document.getElementById(cellID).style.cssText = "background-color:blue";
}
