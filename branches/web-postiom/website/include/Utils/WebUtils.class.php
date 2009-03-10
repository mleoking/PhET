<?php

require_once('include/locale-codes-language.php');
require_once('include/locale-codes-country.php');

class WebUtils {
    // For singleton pattern
    private static $instance;

    private function __construct() {
    }

    private function __clone() {
    }

    public static function inst() {
        if (!isset(self::$instance)) {
            self::$instance = new self;
        }
        return self::$instance;
    }

    private function buildOpenTag($tag_name, $attributes) {
        if (empty($attributes)) {
            return "<{$tag_name}>";
        }

        return "<{$tag_name} ".join(' ', $attributes).'>';
    }

    private function processAttributes($attributes, $attribute_order = array('id', 'class')) {
        //private function buildGenericTag($attribute_order, $attributes) {
        $attrs = array();

        // Assemble attributes with the specified order
        foreach ($attribute_order as $attr_name) {
            if (isset($attributes[$attr_name])) {
                $attrs[] = $attr_name.'="'.$attributes[$attr_name].'"';
                unset($attributes[$attr_name]);
            }
        }

        // Assemble the rest of the attributes
        foreach ($attributes as $key => $value) {
            $attrs[] = $key.'="'.$value.'"';
        }

        return $attrs;
    }

    public function buildImageTag($src, $attributes = array()) {
        if (empty($src)) {
            return '';
        }
        $attribute_order = array('id', 'class', 'src', 'alt', 'title');
        $all_attributes = $attributes;
        $all_attributes['src'] = $src;
        
        $attrs = $this->processAttributes($all_attributes, $attribute_order);
        return '<img '.join(' ', $attrs).' />';
    }

    public function buildAnchorTag($url, $text, $attributes = array()) {
        if (empty($url)) {
            return '';
        }

        // Put the attributes in order:
        // <a id class href ..other..>text</a>
        $attribute_order = array('id', 'class', 'href');
        $all_attributes = $attributes;
        $all_attributes['href'] = $url;
        $attrs = $this->processAttributes($all_attributes, $attribute_order);
        return '<a '.join(' ', $attrs).">{$text}</a>";
    }

    public function buildSpanCommaList($list, $span_attributes = array()) {
        // Build to replace:
        // web-utils.php::convert_comma_list_into_linked_keyword_list()
        if (empty($list)) {
            return '';
        }

        // Put the attributes in order:
        // <a id class href ..other..>text</a>
        $attrs = $this->processAttributes($span_attributes);

        $span_open_tag = $this->buildOpenTag('span', $attrs);

        return $span_open_tag.join(', ', $list)."</span>";
        
    }

    public function buildBulletedList($list, $ul_attributes = array(), $li_attributes = array()) {
        if (empty($list)) {
            return '';
        }

        $ul_open_tag = $this->buildOpenTag('ul', $ul_attributes);
        $li_open_tag = $this->buildOpenTag('li', $li_attributes);

        return $ul_open_tag.$li_open_tag.join('</li>'.$li_open_tag, $list).'</li></ul>';
    }

    public function encodeString($string) {
        $string = str_replace(' ',              '_',    $string);
        $string = str_replace('&amp;',          'and',  $string);
        $string = str_replace('&',              'and',  $string);
        $string = preg_replace('/[^\\w_\\d]+/',  '',     $string);

        return $string;
    }
}

?>
