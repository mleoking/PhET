<?php

    // Utilities for spelling, mostly used in web-utils.php by the 'abbreviate' function

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once('include/spell/tree.php.bigarray');
    require_once('include/spell/SpellCheck.class.php');

    $GLOBALS['tree'] = $tree;

    function spell_is_valid_word($word) {
        $checker = new SpellCheck($word);
        
        return $checker->findNext() == false;
    }

    function test_spell_check($word) {
        if (spell_is_valid_word($word)) {
            print "$word is a valid word";
        }
        else {
            print "$word is not a valid word";
        }
        
        print "<br/>";
    }

?>