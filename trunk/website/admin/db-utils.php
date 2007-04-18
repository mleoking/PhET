<?php
    include_once("db.inc");
    
    function verify_mysql_result($result, $statement) {
        if (!$result && $statement !== "") {
            $message  = 'Invalid query: ' . mysql_error() . "\n";
            $message .= 'Whole query: ' . $statement;

            die($message);
        }
    }

    function run_sql_statement($statement) {
        global $connection;
    
        $result = mysql_query($statement, $connection);
    
        verify_mysql_result($result, $statement);
    
        return $result;
    }
?>