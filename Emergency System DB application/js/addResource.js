var user = $.parseJSON(sessionStorage.user);//using browser session storage to store user info
//actions that needs to be done after page loaded
$(document).ready(function () {
    setUserInto();

    var hasAdditionalESF = true;
    populateSearchForm(hasAdditionalESF); // adds primary esfs -  js/utils.js

    //if submit button is pressed
    $('#addResourceButton').on('click', function (e) {
        e.preventDefault();
        addResource($("#addResourceForm"));  //js/addResource.js
        return true;
    });

    //remove from additional esf list option that was selected in primary esf;
    $('#esfsSelect').on('change', function (e) {
        var selectedPrimaryESF = $(this).find("option:selected").val();
        $("#esfsSelectAdditional").children().show(); //show all additional esf
        $("#esfsSelectAdditional option[value=" + selectedPrimaryESF + "]").hide(); //hide the additional esf option that was selected in primary esf

    });
    var counter = 0;
    $('#addbutton').on('click', function (e) {
        e.preventDefault();
        counter++;
        var newCapability = $("#newCapability").val();
        if(newCapability.trim() == '')
        {
            alert("Cannot insert a blank capability");
        }
        else
        {
            var capability = $("#capabilities");
            capability.append($('<option selected></option>').val(counter).html(newCapability));
            $("#newCapability").val("");
        }
    });
});

function addResource($this) {
    var Username = user['Username'];
    var formData = $this.serializeArray();
    formData.push({name:'username', value:Username});
    var jsonData = {};

    var esfAdditional=[];

//gets forms elements by name and created json as {name:value}
    $.map(formData, function (n, i) {
        n['value'] = (n['value'] == 'on' ? true : n['value']);
        if(n["name"]=="esfsSelectAdditional"){
            esfAdditional.push( n['value']);
        }
        jsonData[n['name']] = n['value'];
    });
    var opts = $('#capabilities')[0].options;

    var capabilities = $.map(opts, function( elem ) {
        return (elem.text);
    });

    jsonData["esfsSelectAdditional"] = esfAdditional;
    jsonData["capabilities"] = capabilities;

    var stringData = JSON.stringify(jsonData);
    var truth = true;

    if(isNaN(jsonData['lat']) || isNaN(jsonData['long']) || isNaN(jsonData['cost']))
    {
        truth = false;
    }
    else if(jsonData['resource'].trim() == '' || jsonData['esf'].trim() == '' || jsonData['model'].trim() == ''
        || jsonData['lat'].trim() == '' || jsonData['long'].trim() == '' || jsonData['cost'].trim() == '')
    {
        truth = false;
    }

    if(truth) {
        $.ajax({
            type: 'POST',
            url: 'server/addResource.php',
            data: stringData,
            dataType: 'json',
            success: function (response) {
                if (response == true) {
                    $this.attr('action', 'addResource.html');
                    $this.submit();
                    return true;
                }
                else {
                    alert('A SQL error occurred.');
                }
            },
            error: function () {
                alert('LORD HELP US');
            }
        });
    }
    else
    {
        alert('Form failed validation, all fields except additional esfs and capabilities must be filled out.\n' +
            'Additionally, lat, long and cost must be numeric values.');
    }
}
