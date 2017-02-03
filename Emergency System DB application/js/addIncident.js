var user = $.parseJSON(sessionStorage.user); //using browser session storage to store user info

//actions that needs to be done after page loaded
$(document).ready(function () {
    setUserInto();
    //datepicker for incident date
    $('#datepicker1').datepicker({
        "setDate": new Date(),
        "autoclose": true,
        "format": "yyyy-mm-dd"
    });
    //if submit button is pressed
    $('#addIncidentButton').on('click', function (e) {
        e.preventDefault();
        addIncident($('#addIncidentForm'));
    });
});

function addIncident($this) {
    var Username = user['Username'];
    var formData = $this.serializeArray();
    formData.push({name:'username', value:Username});
    var jsonData = {};
    //gets forms elements by name and created json as {name:value}
    $.map(formData, function (n, i) {
        n['value'] = (n['value'] == 'on' ? true : n['value']);
        jsonData[n['name']] = n['value'];
    });
    var stringData = JSON.stringify(jsonData);
    if(jsonData['lat'].trim() == '' || jsonData['long'].trim() == '' || jsonData['incidentDate'].trim() == ''
        || jsonData['descrit'].trim() == '')
    {
        alert("All fields must be filled out");
    }
    else if(!(isNaN(jsonData['lat']) || isNaN(jsonData['long']) || (!moment(jsonData['incidentDate'], 'YYYY-MM-DD', true).isValid())))
    {
        $.ajax({
            type: 'POST',
            url: 'server/addIncident.php',
            data: stringData,
            dataType: 'json',
            success: function(data)
            {
                if(data.success == true)
                {
                    $this.attr('action', 'addIncident.html');
                    $this.submit();
                    return true;
                }
                else
                {
                    alert('There has been a SQL error');
                }
            },
            error: function(){
                alert('There has been an error, Lord help us all');
            }
        });
    }
    else
    {
        alert('Date must be in YYYY-MM-DD format, and Lat and Long must be numbers');
    }
}
