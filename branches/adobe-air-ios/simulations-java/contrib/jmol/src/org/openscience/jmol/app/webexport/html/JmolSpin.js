/* Functions for turning Jmol spinning on and off
   using a checkbox.  This allows arbitrary assignment
   of the appletID (n) unlike the JmolCheckBox function
   in Jmol.js.

   by Jonathan Gutow 2010.03.07
*/

function jmol_spin (state, n) {
    if(state == true){
        result = jmolScriptWait("spin on", n);
        }else {
        result = jmolScriptWait("spin off", n);
        }
    }