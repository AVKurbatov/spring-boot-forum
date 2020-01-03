"use strict";

$.ajax({
    type: "GET",
    url: '/topics/findAll',
    dataType: 'json',
    async: true,
    success: createTopicsTable,
    error: warnAboutProblemsWithAjax
});

function createTopicsTable(topics) {
    console.log(topics);
    const table = document.getElementById('topics_list_table');
    table.innerHTML = '';
    $(table).append('<table class="forum-table"><tr>');
    $(table).append('<thead>');
    $(table).append('<tr>');
    $(table).append('<th>Id</th>');
    $(table).append('<th>Title</th>');
    $(table).append('</tr>');
    $(table).append('</thead>');


    for (var i = 0; i < topics.length; ++i) {
        var topic = topics[i];
        $(table).append('<tr>');
        $(table).append('<td>' + topic.id + '</td>');
        $(table).append('<td><a href="/messages/' + topic.id + '/">' + topic.title + '</a></td>');
        $(table).append('</tr>');
    }

    $(table).append('</table>');
}

