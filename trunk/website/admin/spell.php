<?php

    // Utilities for spelling, mostly used in web-utils.php by the 'abbreviate' function

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");

    include_once(SITE_ROOT.'admin/spell/tree.php.bigarray');
    include_once(SITE_ROOT.'admin/spell/SpellCheck.class.php');

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