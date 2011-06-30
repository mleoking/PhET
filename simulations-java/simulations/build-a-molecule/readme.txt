//REVIEW move this file to doc directory?
Molecules grabbed from pubchem (http://pubchem.ncbi.nlm.nih.gov/). See molecule-data-readme.txt for more info

Units for distance: picometers in general, however the molecules pulled from pubchem have 2d / 3d distance in Angstroms.

Model structure at a glance (nesting indicates has-a relationship):

CollectionList: an ordered list of collections. represents the contents of an entire tab, basically
    KitCollection: represents atoms, collection boxes and kits for a particular collection
        CollectionBox: stores box state and allowed molecules
        Kit: handles everything in buckets and in play for a particular kit, and remembers which of its atoms are in collection boxes
            Bucket: handles the atoms it contains, and positions them
            LewisDotModel: handles the North/South/East/West connectivity for all atoms in a kit
            (kit also tracks molecule structures that are in play or are in collection boxes)
    LayoutBounds: where all the view stuff is in model coordinates

View at a glance (nesting indicates has-a relationship):

BuildAMoleculeCanvas: view for each tab (collection list). if the tab has collection boxes, it uses subclass MoleculeCollectingCanvas
    KitCollectionNode: one of these for each collection. only one visible at a time
        4 layers:
          top: shows the bucket "tops"
          atoms: (guess?)
          metadata: shows molecule information
          bottom: shows collection area (CollectionAreaNode), kit background (KitPanel) and bucket "bottoms"
    CollectionPanel: collection area panel if applicable, only 1 for the canvas. responsible for sound on/off and collection label/controls
        CollectionAreaNode: CollectionPanels contain one of these at a time (they swap in/out when the user changes collections). Handles the "Reset Collection" button and the collection box nodes:
            CollectionBoxNode: Shows label and black area for one collection box. Either Single or Multiple subclasses.


//REVIEW comment in this section should be in appropriate class javadoc, if they're not already
Notes useful for code review (or if you need to edit the sim):

* Some layout in the model is calculated based on the view layout:
    Play area bounds are based on the maximum collection area width (depends on string translation lengths, etc.)
    CollectionBox coordinates are set based on their view coordinates.
         Relies on canvas computations, so in one case a listener setup is needed to wait until all PNodes are hooked together to compute this.
         (ugliest code part of the sim in JO's opinion)
* Things under model.data are not used during the regular sim's runtime
* Reading molecule-data-readme.txt is recommended.
* JO is dissatisfied with code duplication and use of MoleculeStructure (has references to hydrogens as atoms) and StrippedMolecule (just has hydrogen counts on heavy atoms). Ideally they could be combined?
* LewisDotModel is separate from MoleculeStructure since future extensions may add in a Pseudo3DModel which handles 2d connectivity in a different way than LewisDotModel
* I switched some collection area layout to use GeneralLayoutNode, since SwingLayoutNode presented with multiple problems. GeneralLayoutNode works for these purposes, but would need more work to be moved to phetcommon