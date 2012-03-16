import edu.colorado.phet.simsharinganalysis._
import phet._

val res0 = phet load "C:\\Users\\Sam\\Desktop\\session-dirs"
res0.print

res0.count(_.simName == "Molecule Polarity")
res0.count(_.simName == "Molecule Shapes")
res0.filter(_.simName == "Molecule Polarity").print
res0.filter(_.simName == "Molecule Polarity").size

res0.map(_.session).size

res0.map(_.machine)
res0.map(_.machine).distinct.size

res0.map(_.user)
res0.map(_.user).distinct
res0.map(_.user).distinct.sorted
res0.map(_.user).distinct.sortBy(numerical)

res0.map(_.size)
res0.map(_.size).sum

val res15 = res0(0)
//res15.<tab>
res15.histogramByObject

phet xyplot res0.map(_.eventCountData)

phet xyplot res0.filter(_.simName == "Molecule Polarity").map(_.countEntries(simEvents.moleculePolarity))

