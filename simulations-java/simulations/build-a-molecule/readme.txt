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
