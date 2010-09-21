#!/bin/bash
#================================================================================
#
#  Counts the number of translations for each locale.
#
#================================================================================

TRUNK=${1}
TRUNK=/Users/cmalley/phet-workspace/trunk

echo "================== JAVA SIM TRANSLATIONS =================="
for stringFile in `ls ${TRUNK}/simulations-java/*/*/*/*/*/*-strings*.properties | grep "_"`
do
    echo `basename $stringFile | cut -d . -f 1 | cut -d _ -f 2,3`
done | sort | uniq -c

echo "=============== JAVA COMMON TRANSLATIONS ============"
for stringFile in `ls ${TRUNK}/simulations-java/common/phetcommon/data/phetcommon/localization/*-strings*.properties | grep "_"`
do
    echo `basename $stringFile | cut -d . -f 1 | cut -d _ -f 2,3`
done | sort | uniq

echo "================== FLASH SIM TRANSLATIONS =================="
for stringFile in `ls ${TRUNK}/simulations-flash/*/*/*/*/*/*-strings*.xml | grep -v "_en"`
do
    echo `basename $stringFile | cut -d . -f 1 | cut -d _ -f 2,3`
done | sort | uniq -c

echo "=============== FLASH COMMON TRANSLATIONS ============"
for stringFile in `ls ${TRUNK}/simulations-flash/common/data/localization/*-strings*.xml | grep -v "_en"`
do
    echo `basename $stringFile | cut -d . -f 1 | cut -d _ -f 2,3`
done | sort | uniq

#================================================================================
# end of file