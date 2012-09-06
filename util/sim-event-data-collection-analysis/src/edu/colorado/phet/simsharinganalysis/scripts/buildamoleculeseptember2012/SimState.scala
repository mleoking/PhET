package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012

/**
 * @author Sam Reid
 */
case class SimState(time: Long, tab: Int, tab0: TabState, tab1: TabState, tab2: TabState) {
  def currentTab = getTab(tab)

  def getTab(t: Int) = t match {
    case 0 => tab0
    case 1 => tab1
    case 2 => tab2
    case _ => throw new RuntimeException("Tab not found")
  }

  def collectedMoleculesWithMetadata = collectedMoleculesWithMetadataWithTab(0) ++ collectedMoleculesWithMetadataWithTab(1) ++ collectedMoleculesWithMetadataWithTab(2)

  def collectedMoleculesWithMetadataWithTab(t: Int) = getTab(t).collected.map(molecule => new Metadata(molecule.time, t, getTab(t).kit, getTab(t).collection, molecule.name))
}

case class MoleculeCollectionEvent(name: String, time: Long)

case class TabState(collected: List[MoleculeCollectionEvent], kit: Int, collection: Int, filledCollectionBoxes: Int) {
  def collect(commonName: String, time: Long) = copy(collected = collected ::: List(MoleculeCollectionEvent(commonName, time)))
}

//Output in the format that EM suggested in email
case class Metadata(time: Long, tab: Int, kit: Int, collection: Int, molecule: String) {
  override def toString = "time: " + time + ", tab: " + tab + ", kit: " + kit + ", collection: " + collection + ", molecule: " + molecule
}
