//var sourceFile = "file:///C|/svn/trunk/simulations-flash/simulations/geometric-optics/src/geometric-optics.fla";
//var logFile = "file:///tmp/tmpWq0sOy.log";
//var doc = fl.openDocument(sourceFile);
//fl.outputPanel.clear();
//fl.getDocumentDOM().publish();
//fl.closeDocument(doc);

var prefix = "file:///C|/svn/trunk/simulations-flash/simulations/";
var test_sims = ["geometric-optics", "pendulum-lab"];
var sims = ["arithmetic", "blackbody-spectrum", "charges-and-fields", "curve-fitting", "equation-grapher", "estimation", "faradays-law", "friction", "geometric-optics", "mass-spring-lab", "my-solar-system", "ohms-law", "pendulum-lab", "plinko-probability", "projectile-motion", "stern-gerlach", "vector-addition", "wave-on-a-string"];
for(var i = 0; i < sims.length; i++) {
	var sim = sims[i];
	var doc = fl.openDocument(prefix + sim + "/src/" + sim + ".fla");
	fl.getDocumentDOM().publish();
	fl.closeDocument(doc);
}
fl.quit();
