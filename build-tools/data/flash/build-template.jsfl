//Since we have been unable to find a way to pass args to Flash JSFL
 //We use this template, and replace dummy args TRUNK and SIMS with @ around them

// jsfl script to publish all of the sims listed below
// (publishing creates the SWF files, and could be configured to do more)

// prefix to simulations directory
var prefix = "file:///@TRUNK@/simulations-flash/simulations/";

// sim names to publish
var sims = [@SIMS@];

var closeFlash = @CLOSEFLASH@;

// if execution takes a while, don't display a "your script is running long" message
fl.showIdleMessage(false);

for(var i = 0; i < sims.length; i++) {
	var sim = sims[i];

    // clear the output panel to wipe any previous errors
    fl.outputPanel.clear();

    // open the FLA in the IDE
	var doc = fl.openDocument(prefix + sim + "/src/" + sim + ".fla");

    // publish it
	fl.getDocumentDOM().publish();

    // save any errors to a file
    fl.outputPanel.save("file:///@TRUNK@/simulations-flash/build-output-temp/output-" + sim + ".txt")

    // close the FLA
	fl.closeDocument(doc);
}

fl.showIdleMessage(true);

// close the Flash IDE that we opened
if(closeFlash) {
    fl.quit();
}
