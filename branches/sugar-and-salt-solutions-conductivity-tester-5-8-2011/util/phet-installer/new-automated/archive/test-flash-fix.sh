#!/bin/bash

#############################################################################
# This script tests some changes to the build scripts that downloads the
# flash files that were previously being missed by the builder.
#############################################################################

#----------------------------------------------------------------------------
# Main body of this script.
#----------------------------------------------------------------------------

/usr/local/php/bin/php build-install.php --download-sims


