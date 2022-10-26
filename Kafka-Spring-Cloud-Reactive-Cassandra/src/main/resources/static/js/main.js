$(document).ready(function () {

    $("#search-form").submit(function (event) {
        event.preventDefault();
        search_submit();
    });
});


function suggest(suggestion) {
    search_submit()
}


function search_submit() {
	var t0 = performance.now();
    var search = "department=" + $("#term").val();
    search["department"] = $("#term").val();

    $("#btn-search").prop("disabled", true);

    console.log("Search term: {}", $("#term").val());
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/api/customers",
        data: search,
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            $('#feedback').empty();
            var table = [];
            console.log("Data returned: {}", data);
            if (data.length > 0) {
            	var total = data.length;
            	var t1 = performance.now();
            	var diff = ((t1 - t0)/1000).toFixed(2);
            	$('#feedback').append('Returned ' + total + ' records in ' + diff + ' seconds!');
                console.log("Data returned: {}", data);
                var items = data;
                for (var i = 0; items.length > i; i++) {
                    var item = items[i];
                    table.push([item.id, item.firstName, item.lastName, item.department, item.designation])
                }
                makeTable($('#feedback'), table);
                $("#btn-search").prop("disabled", false);
            } else {
                $('#feedback').append('No Records Found');
            }
        },
        error: function (e) {
            $('#feedback').empty();
            var json = "<h4>Search Error Response</h4><pre>"
                + e.responseText + "</pre>";
            $('#feedback').html(json);

            console.log("ERROR : ", e);
            $("#btn-search").prop("disabled", false);

        }
    });

    function makeTable(container, data) {
        var table = $("<table/>").addClass('table table-striped table-dark');
        var headers = ["ID", "First Name", "Last Name", "Department", "Designation"];
        var thead = $("<thead/>").addClass('thead-dark');
        var heads = $("<tr/>");
        $.each(headers, function (colIndex, c) {
            heads.append($("<th/>").text(c));
        });
        var tbody = $("<tbody/>");
        table.append(thead.append(heads));

        $.each(data, function (rowIndex, r) {
            var row = $("<tr/>");
            $.each(r, function (colIndex, c) {
            	row.append($("<td/>").text(c));
            });
            tbody.append(row);
        });
        table.append(tbody);
        return container.append(table);
    }


}