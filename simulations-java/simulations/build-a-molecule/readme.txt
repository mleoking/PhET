Molecules grabbed from pubchem (http://pubchem.ncbi.nlm.nih.gov/). Current count of 1602.

Units for distance: picometers in general, however the molecules pulled from pubchem have 2d / 3d distance in Angstroms.

Model at a glance:

TODO: update with latest (CollectionList, etc)
KitCollection: represents everything in a tab. holds collection boxes and kits
    CollectionBox: stores box state and allowed molecules
    Kit: handles everything in buckets and in play for a particular kit, and remembers which of its atoms are in collection boxes
        Bucket: handles the atoms it contains, and positions them
        LewisDotModel: handles the North/South/East/West connectivity for all atoms in a kit
        (kit also tracks molecule structures that are in play or are in collection boxes)
    LayoutBounds: where all the view stuff is in model coordinates

View at a glance:

BuildAMoleculeCanvas: view for each tab. if the tab has collection boxes, it uses subclass MoleculeCollectingCanvas
    4 layers:
    top: shows the bucket "tops"
    atoms: (guess?)
    metadata: shows molecule information
    bottom: shows collection area (CollectionAreaNode), kit background (KitPanel) and bucket "bottoms"

    KitView: created for each kit, and creates content in all 4 layers. it is responsible for visibility of the elements in its layers