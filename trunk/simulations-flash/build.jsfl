// jsfl script to publish all of the sims listed below
// (publishing creates the SWF files, and could be configured to do more)

// prefix to simulations directory
var prefix = "file:///C|/svn/trunk/simulations-flash/simulations/";

// sim names to publish
var sims = ["arithmetic", "blackbody-spectrum", "charges-and-fields", "curve-fitting", "equation-grapher",
    "estimation", "faradays-law", "friction", "geometric-optics", "mass-spring-lab", "my-solar-system",
    "ohms-law", "pendulum-lab", "plinko-probability", "projectile-motion", "stern-gerlach",
    "vector-addition", "wave-on-a-string"];

for(var i = 0; i < sims.length; i++) {
	var sim = sims[i];

    // open the FLA in the IDE
	var doc = fl.openDocument(prefix + sim + "/src/" + sim + ".fla");

    // publish it
	fl.getDocumentDOM().publish();

    // close the FLA
	fl.closeDocument(doc);
}

// close the Flash IDE that we opened
fl.quit();
