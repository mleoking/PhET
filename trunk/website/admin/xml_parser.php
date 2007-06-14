<?php

    if (phpversion() < 5) {
        include_once("parser_php4.php");
    }
    else {
        include_once("parser_php5.php");
    }

?>