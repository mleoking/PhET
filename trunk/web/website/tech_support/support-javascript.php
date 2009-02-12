<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class JavaScriptTroubleshootingPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $phet_help_email = PHET_HELP_EMAIL;

        print <<<EOT
            <p>JavaScript is a programming language that is mostly used in web pages, usually to add features
            that make the web page more interactive.  JavaScript is required to submit information to PhET.</p>

            <p>JavaScript is completely different than Sun Microsystems' Java Web Start that we use on our
            simulations.  For assistance with Sun Microsystems' Java, <a href="support-java.php">go here</a>.</p>

            <p>If you can't solve your problem here, please notify us by email at the following address: <a href="mailto:{$phet_help_email}"><span class="red">{$phet_help_email}</span></a></p>


            <h2 id="faq_top">FAQ's</h2>

            <div id="faq">
                <ul id="nav">
                    <li class="faq"><a href="#q1">Is JavaScript enabled on my browser?</a></li>

                    <li class="faq"><a href="#q2">Why is JavaScript needed?</a></li>

                    <li class="faq"><a href="#q3">How do I enable JavaScript on Firefox?</a></li>

                    <li class="faq"><a href="#q4">How do I enable JavaScript on Internet Explorer?</a></li>

                    <li class="faq"><a href="#q5">How do I enable JavaScript on Safari?</a></li>

<!--
                    <li class="faq"><a href="#q6">Q6: </a></li>

                    <li class="faq"><a href="#q7">Q7: </a></li>
-->
                </ul>
            </div><br />
            <br />

            <h3 id="q1" >Is JavaScript enabled on by browser?</h3>

            <noscript><p>JavaScript is <strong>NOT</strong> enabled.</p></noscript>
            <script type="text/javascript">
                /* <![CDATA[ */
                    document.write("<p>JavaScript <strong>IS</strong> enabled.</p>");
                /* ]]> */
            </script>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q2" >Why is JavaScript needed?</h3>

            <p>JavaScript is used with many features on the website, including filtering routines and allowing for multiple inputs of data.  Howevery, many pages will work fine without JavaScript enabled.</p>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q3" >How do I enable JavaScript on Firefox?</h3>

            <ol>
                <li>Go to the "Edit" menu and select "Preferences"</li>
                <li>Select the "Content" tab at the top of the Preferences window</li>
                <li>Make sure the "Enable JavaScript" checkbox is checked</li>
                <li>Reload this page, and see <a href="#q1">question 1</a></li>
            </ol>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q4" >How do I enable JavaScript on Internet Explorer?</h3>

            <ol>
                <li>Go to the <strong>Tools</strong> menu and select <strong>Internet Options...</strong></li>
                <li>Select the <strong>Security</strong> tab</li>
                <li>Select on the <strong>Custom Level...</strong> button</li>
                <li>Scroll down until you see <strong>Active Scripting</strong> under the <strong>Scripting</strong> heading</li>
                <li>Make sure that it is set to <strong>Enabled</strong></li>
                <li>You may be asked if you are sure <em>you want change the security settings for this zone.</em>  You will want to select the <strong>Yes</strong> button</li>
                <li>Select the <strong>OK</strong> button on the original <strong>Internet Options</strong> window</li>
                <li>Reload the page (you can check if it worked by reading <a href="#q1">this question</a>)</li>
            </ol>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q5" >How do I enable JavaScript on Safari?</h3>

            <ol>
                <li>Select Preferences from the Safari menu.</li>
                <li>Click Security.</li>
                <li>Check Enable JavaScript.</li>
                <li>Close the window.</li>
                <li>Click Reload.</li>
            </ol>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

<!--
            <h3 id="q6" >Q6?</h3>

            <p>A6</p>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q7" >Q7?</h3>

            <p>A7</p>

            <p><a href="#faq_top"><img src="{$this->prefix}images/top.gif" alt="Go to top" /></a></p>
-->

EOT;
    }

}

$page = new JavaScriptTroubleshootingPage("Troubleshooting JavaScript", NAV_TECH_SUPPORT, null);
$page->update();
$page->render();

?>
