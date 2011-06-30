//REVIEW move this file to doc directory?
*** General use

A molecule (electrically neutral of 2+ atoms) is generally called a "complete molecule"
On the way to constructing a complete molecule, we pass through "molecule structures" -- 1 or more connected atoms

There are two files with data about complete molecules:
    collection-molecules.txt (those that are loaded in the 1st pass and can appear as targets in collection boxes)
    other-molecules.txt (all other molecules that can be built)
//REVIEW what is a CID number?
They correspond to compounds in PubChem's Compound database, and have CID numbers so that the PubChem compound can be found from a referenced molecule
Additionally, they have the needed 2d/3d data, bonding information, etc.

There also is a file (structures.txt) that contains molecule structures used for quickly testing partial molecules to see if they are a sub-molecule of
anything in the previous molecule files. This file is more lightweight, and just contains atom/bonding info. It is computed such that
if a molecule structure in the sim is "possible" to make, then you can add 0 or more hydrogens to it to arrive at a bond structure held in structures.txt.
Thus, structures.txt does not contain ALL possible molecule structures (this would be far too large), but omits structures that are a
"hydrogen submolecule" of others (can be made by stripping hydrogens off of another structure).

*** Formats

* General Molecule Structures

The general "body" of the data is represented by the following ('|' is literal):
     * line = numAtoms|numBonds(|atomBondSpec)*
     * atomBondSpec = atomSpec(,bondSpec)*
     * atomSpec --- determined by implementation of atom. does not contain '|' or ','
     * bondSpec --- determined by implementation of bond. does not contain '|' or ','
For molecule structures, the above format is used. atomSpec is the atom symbol, and bondSpec specifies previous atom index to bond

Example (water): 3|2|O|H,0|H,0
     in words: 3 atoms, 2 bonds. add oxygen (atom 0). add hydrogen (atom 1), bond to atom 0. add hydrogen (atom 2), bond to atom 0.

* Complete molecules

For complete molecules, there is a prepended "header" in front of the main body section:
     * completeLine = commonName|molecularFormula|cid|format|line
     * format = one of the following: full, 3d, or 2d
AtomSpec changes depending on whether 2d, 3d, or both types of coordinates are involved. Bonds are like above, but also include the bond order.
2d and 3d coordinates are completely independent. Additionally, right now 2d coordinates should not presume to have correct bond information,
as pubchem's 2d and 3d data can have separate atom orders. I didn't feel like writing an isomorphism-detection bit to do this, since 2d view
doesn't show bonds AND coordinates

Example (water): water|H2O|962|full|3|2|O 2.5369 -0.155 0 0 0|H 3.0739 0.155 0.2774 0.8929 0.2544,0-1|H 2.0 0.155 0.6068 -0.2383 -0.7169,0-1
     in words:
          common name is "water"
          molecular formula is "H2O"
          PubChem CID is 962
          full => it includes both 2d AND 3d data
          3 atoms
          2 bonds
          oxygen (atom 0). 2d coordinates (2.5369, -0.155), 3d coordinates (0, 0, 0)
          hydrogen (atom 1). 2d coordinates (3.0739, 0.155), 3d coordinates (0.2774, 0.8929, 0.2544). bond to atom 0 (bond has order 1 -- single bond)
          hydrogen (atom 2). 2d coordinates (2.0, 0.155), 3d coordinates (0.6068, -0.2383, -0.7169). bond to atom 0 (bond has order 1)

*** Generation (from PubChem data)

1. Download 2d/3d SDFs from ftp://ftp.ncbi.nlm.nih.gov/pubchem/Compound/CURRENT-Full/SDF/ and ftp://ftp.ncbi.nlm.nih.gov/pubchem/Compound_3D/01_conf_per_cmpd/SDF/,
2. Run MoleculeSDFCombinedParser with the 2d and 3d directories to output collection-molecules.txt and other-molecules.txt (unfiltered)
3. Use MoleculeKitFilterer to remove molecules from other-molecules.txt that cannot be constructed in the sim (based on kits available)
4. Use MoleculeDuplicateNameFilter to further filter other-molecules.txt for molecules with duplicate names
5. Move the latest molecule files into the data directory for the sim.
6. Use MoleculePreprocessing to generate a proper structures.txt based on the molecule files