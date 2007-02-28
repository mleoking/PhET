#
# author: Ron LeMaster
# date created: 1/25/2007
#
# This sed script changes the codebase of jnlp files that have codebases pointing to phet.colorado.edu
# or phet-web.colorado.edu to point to www.colorado.edu/physics/phet.
#
# This is needed to make the jnlp files work properly for SimLauncher
#

s/phet.colorado.edu/www.colorado.edu\/physics\/phet/
s/phet-web.colorado.edu/www.colorado.edu\/physics\/phet/