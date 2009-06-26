** STILL A WORK IN PROGRESS **

Directory structure of phet.colorado.edu:

There are many files in PhET's HTROOT.  Here's a brief primer of what
is where.

Files that contain website scripts, or files related directly to the website's functioning:
about
  - contains files for the "About PhET" section of the website
admin
  - contians files for the "Administrative" section of the website
contribute
  - contians files for the "Contribute" section of the website
css
  - contains stylesheets for the website
get_phet
  - contians files for the "Run our Simulations" section of the website
images
  - contains all images for the website
include
  - contains support php scripts (base classes, utils, etc)
index.html
  - main page that redirects to index.php
index.php
  - main PhET page
js
  - contains all javascrits for the website
random-thumbnail.php
  - sends a random animated thumbnail of a sim in action (used in index.php)
research
  - contians files for the "Research" section of the website
services
  - contians files to interact with website files/data with programatically (e.g. from a sim)
simulations
  - contains files for the "Simulations" section of the website
sponsors
  - contains files for the "Sponsors" section of the website
teacher_ideas
  - contains files for the "Teacher Ideas & Activities" section of the website
tech_support
  - contains files for the "Troubleshooting" section of the website

Other directories, and what you'll find there:
backups
  - Temporary directory to store backups of things in case there are
   problems.  For example, when a sim is deployed the old sim is moved
   here and the new sim is put in it's place.
cl_utils
  - Utilities to support various aspects of the website.
crossdomain.xml
  - From JO: Before Flash can access data from a host (to check for
  updates, send tracking information, EVEN open a link to the site),
  it checks host/crossdomain.xml to see if it is allowed to do so. Our
  crossdomain.xml tells Flash it can communicate with
  phet.colorado.edu and all its sub-directories.
  - It is theoretically possible not to have it at web root, however
  it only allows access to everything beneath it in the directory
  structure (so it couldn't direct to phet.colorado.edu/sims/.... AND
  track to phet.colorado.edu/tracking/....)
installer-builder
  - scripts and files related to ripping the website and making the
  PhET installer
phet-dist/
  - A misc directory that contians things without a specific home.  It
  is mostly stuff that is downloaded directly.  Here's a breakdown:
phet-dist/build-tools
phet-dist/build-tools-config
  - Support on tigercat for building and deploying sims
phet-dist/flash-launcher
  - Template to generate Flash sim .jar files
phet-dist/installers
  - Old copies of PhET installers, kept around in case bad versions
  are generated.
phet-dist/newsletters
  - Copies of the official PhET newsletter(s)
phet-dist/phet-updater
  - Support for sims that need to update themselves
phet-dist/publications
  - Contains all publications hosted on this site
phet-dist/translation-utility
  - Contains the translation utility (to translate sims)
phet-dist/workshops
  - Contains workshop materials and source material for the Uganda
  workshop (mostly images)
sims/
  - Contains all sims, each directory is the main "project".  Each
  project may contain several sims related to that project.  For
  example: nuclear-phyiscs has these sims: Alpha Decay, Nuclear
  Fission, The Radioactive Dating Game.
  - It also contains directories that are not sims, elaborated upon
  below:
sims/build-tools/
  - ??? Is this the same as phet-dist/build-tools?
  - **owned by JO, ask him
sims/flash-common-strings/
sims/java-common-strings/
  - Contains support to translate strings common to all sims of a
  specific type
sims/resources/
  - ??? What are these files for?
  - **owned by JO, ask him
sims/sim-template/
  - A dummy java sim that is used for testing deployment procedures.
sims/test-flash-project/
sims/test-project/
  - Test projects for Flash and Java, respectively.  These are not
  official sims, just designed to be able to run them from the main
  website.  There is special support of "seeing" theses sims on main
  website.
sims/translations/
  - ??? Contains temporary files when translations are deployed.  They are
  removed with a CRON job after they are a week old.
  - ??? Check this, I may be confusing it with backups.  This one may
  be in limbo requiring feedback from Marj and work by SR or JO
staging
  - A staging are for deploying things to the website.  Copies over
  the network can be slow, this allows everything to be on tigercat
  before doing things with it.  For example, it takes about 10-15
  minutes to deploy the website, but we don't want each individal file
  to overwrite it's equivalent on the server or it would be out of
  sync while all files are being copied (and possibly a broken website
  while that was happening).  Instead, all files are loaded here and then
  copied over their counterparts, which only takes seconds.
statistics
  - Contians scripts that hanle gathering and reporting the statistics
  sent from sims
webcache
  - Cached versions of the webpages.  We have our own custom solution,
  all files can be found here.
web-pages
  - Legacy from the old old website.  It has been stripped down to the
  important minimum (the publications) but is being kept until someone
  gets a chance to make sure that it is not accessed anymore.  NOTE:
  it has a .htaccess file, so this dir and that file should not be
  deletede, just everything else inside this dir.

Redirects:
Many directories contain .htaccess files, that are usually used to
store redirects from old to new pages.  Don't delete or meddle with
these files unless you really know what you are doing.

