function insert_submit_stamp(input) {
    if ((input.tagName != 'INPUT') || (input.type != 'submit')) {
        return false;
    }

    new_input = document.createElement('input');
    new_input.name = 'submition';
    new_input.value = 'x:' + new Date();//'xxy0z';
    new_input.type = 'hidden';

    input_parent = input.parentNode;
    input_parent.insertBefore(new_input, input);

    return true;
}
