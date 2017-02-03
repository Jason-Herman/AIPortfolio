/////////////////////////////////////////////////////////////

//  All Javascript functions for search

/////////////////////////////////////////////////////////////
var user = $.parseJSON(sessionStorage.user);//using browser session storage to store user info
var incidentID;
var incidentOwner;

//actions that needs to be done after page loaded
$(document).ready(function () {
    setUserInto(); // js/search.js
    populateSearchForm(); //js/utils.js

    $("#locationGroup").hide();


    //display location if incident was selected
    $("#incidentSelect").change(function () {
        var selected = $(this).find('option:selected');

        if (selected.val().length > 0) {
            $("#locationGroup").show();
            $("location").val("");
            $("#location").attr("required", "");
            incidentOwner=selected.data('owner');
            //console.log(incidentOwner);
        }
        else {
            $("#locationGroup").hide();
            $("#location").removeAttr("required");
        }
    });

    //datepicker for return request form
    $('#datepicker1').datepicker({
        "setDate": new Date(),
        "autoclose": true,
        "format": "yyyy-mm-dd"
    });

    //if submit button is pressed
    $('#searchForm').submit(function (e) {
        e.preventDefault();
        var $this = $(this);
        search($this);  //js/search.js

    });

    //if resource is requested is pressed
    $("#resourceRequestForm").submit(function (e) {
        e.preventDefault();
        var $this = $(this);
        requestResource($this); //js/search.js
    });

    //if repair button in repair form is pressed
    $("#repairForm").submit(function (e) {
        e.preventDefault();
        var $this = $(this);
        repair($this); //js/search.js
    });
})

function search($this) {

    var formData = $this.serializeArray();
    var jsonData = {};

    //gets forms elements by name and created json as {name:value}
    $.map(formData, function (n, i) {
        n['value'] = (n['value'] == 'on' ? true : n['value']);
        jsonData[n['name']] = n['value'];

        if (n["name"] == "incident") {
            incidentID = n['value'];
        }
    });

    jsonData["action"] = "searchResource"; //what to do in php
    var hasDistance =  jsonData["location"] != "" && jsonData["incident"] != "";
    jsonData["username"] = user.Username;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/search.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            var data = $.parseJSON(response.responseText);

            //if php script was reached but something broken or we send custome error
            if (data.hasOwnProperty('error')) {
            }
            //success display record
            else {
                displaySearchResults(data, hasDistance);
                hideErrorMessage();
            }
        },
        //if server script was not reached
        error: function (xhr) {
        }
    });
}

function displaySearchResults(data, hasDistance) {
    //header of table
    var sHTML = '<div class="row bottomBorder">';
    sHTML += '<h4 class="col-md-1">ID</h4>';
    sHTML += '<h4 class="col-md-2">Name</h4>';

    var className = hasDistance ? "col-md-3" : "col-md-4"; //wider column when no distance params were entered
    sHTML += '<h4 class="' + className + '">Owner</h4>';
    sHTML += '<h4 class="col-md-1">Cost ($)</h4>';
    sHTML += '<h4 class="col-md-1">Status</h4>';
    sHTML += '<h4 class="col-md-1">Next Available</h4>';

    //user entered params for distance based search
    if (hasDistance) {
        sHTML += '<h4 class="col-md-1">Distance (mi)</h4>';
    }

    sHTML += '<h4 class="col-md-2">Action</h4>';
    sHTML += '</div>';

    //records
    sHTML += '<div class="searchRecordsWrapper ">';

    // no records
    if (data.length == 0) {
        sHTML += "<h3>There are no available resources.</h3>";
    }
    else {

        data.forEach(function (item, index) {
            sHTML += '<div class="row " id="resource_' + item.ResourceID + '">';
            sHTML += '<div class="col-md-1 resultCell">' + item.ResourceID + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.ResourceName + '</div>';
            sHTML += '<div class="' + className + ' resultCell">' + item.OwnerName + '</div>';
            sHTML += '<div class="col-md-1 resultCell">$' + item.Cost + '/' + item.CostDescription + '</div>';
            sHTML += '<div class="col-md-1 resultCell status">' + item.ResourceStatus + '</div>';

            var dateAvailable = getAvailabilityDate(item.NextAvailableDate);
            sHTML += '<div class="col-md-1 resultCell nextAvailable">' + dateAvailable + '</div>';

            //user entered params for distance based search
            if (hasDistance) {
                var distance = item.Distance ? item.Distance : "&nbsp;";
                sHTML += '<div class="col-md-1 resultCell">' + distance + '</div>';
            }
            //not in repair -  display buttons
            if (item.RepairStatus == null || item.RepairStatus == 0) {

                //user owns resource
                if (user.Username == item.OwnerUsername) {
                    var actionButtons = resourceOwnerActionButtons(item.ResourceID, item.ResourceRequestID, item.ResourceStatus,item.ScheduledRepairID);

                }
                //only owners of the incident can request the resources
                // if resource should not be already requested by user
                else if(typeof incidentOwner!='undefined' && user.Username==incidentOwner && item.IsRequestedByUser=="0"){
                    actionButtons = usersActionButtons(item.ResourceID);
                }
                else if(typeof incidentOwner!='undefined' && user.Username==incidentOwner && item.IsRequestedByUser=="1"){
                    actionButtons ='<button class="btn btn-default actionBtn request disabled"  >Requested By Me</button>';
                }
                else{
                    actionButtons="&nbsp;"
                }
                sHTML += '<div class="col-md-2 resultCell">' + actionButtons + '</div>';
            }

            sHTML += '</div>';
        })
    }
    sHTML += '</div>';
    $("#searchResults").html(sHTML);
}

function getAvailabilityDate(nextAvailableDate) {
    var dateAvailable = nextAvailableDate; //default available

    var dateobj = new Date(); //current date
    var month = dateobj.getMonth() + 1;
    var day = dateobj.getDate();
    var year = dateobj.getFullYear();
    var date = year + "-" + month + "-" + day;

    if (date == dateAvailable) {
        dateAvailable = "NOW";
    }
    return dateAvailable;
}


//Deploy, rapair buttons
function resourceOwnerActionButtons(resouceID, resourceRequestID, resourceStatus, scheduledRepairID) {

    var buttons = "&nbsp;";
    var isIncidentSelected = typeof incidentID != 'undefined';

    //incident must be selected to view the Deploy button; there should be a reuqest of resource to deploy
    if (isIncidentSelected && resourceStatus == "Available" && resourceRequestID != null) {
        buttons += '<button class="btn btn-default actionBtn deploy" onclick="deploy(\'' + resourceRequestID + '\',\'' + incidentID + '\',\'' + resouceID + '\')">Deploy</button>';
    }
    if (resourceStatus != "In Repair" && scheduledRepairID == null) {
        buttons += '<button class="btn btn-default actionBtn repair" onclick="repairBtn(\'' + resouceID + '\',\'' + resourceStatus +  '\',\'' + resourceRequestID + '\')">Repair</button>';
    }
    else if (resourceStatus != "In Repair" && scheduledRepairID != null) {
        buttons += '<button class="btn btn-default actionBtn repair disabled" >Scheduled for Repair</button>';
    }
    else {
        buttons += '<button class="btn btn-default actionBtn repair disabled" >In Repair</button>';
    }

    return buttons;
}

//request button
function usersActionButtons(resouceID, isRequestedByUser) {
    var buttons ="";
    if (incidentID !=null && incidentID!=''){
         buttons = '<button class="btn btn-default actionBtn request"  onclick="requestBtn(\'' + resouceID + '\',\'' + incidentID + '\')">Request</button>';
    }

    return buttons;

}
function deploy(resourceRequestID, incidentID, resourceID) {

    hideErrorMessage();

    var jsonData = {};
    jsonData["action"] = "deploy"; //what to do in php
    jsonData["username"] = user.Username;
    jsonData["resourceRequestID"] = resourceRequestID;
    jsonData["resourceID"] = resourceID;
    jsonData["incidentID"] = incidentID;
    jsonData["needsUpdate"] = true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/actionButtons.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            var data = $.parseJSON(response.responseText);

            //successfully deployed -> re-do search to retrive the latest data
            if (data.hasOwnProperty('success') && data.success) {


                var updatedInfo = data.resorceUpdated;
                updatedInfo.forEach(function (item, index) {
                    $("#resource_" + item.ResourceID + " .status").html(item.ResourceStatus);
                    $("#resource_" + item.ResourceID + " .nextAvailable").html(item.NextAvailableDate);
                })

                $("#resource_" + jsonData["resourceID"] + " .deploy").hide();

            }
            else {
                $('#divError').show();
                $('#divError .errorMessage').html("Resource was not deployed.");
            }
        },
        //if server script was not reached
        error: function (xhr) {
            // $('#divError').show();
            //$('#divError').html(xhr.responseText);
        }
    })
}

function requestBtn(resouceID, incidentID) {
    clearRequestResourceForm();
    $("#returnDateModal").modal('show');
    $("#resourceID").val(resouceID);
    $("#incidentID").val(incidentID);
}

function clearRequestResourceForm() {
    hideErrorMessage();
    $("#returnDate").val("");
    $("#resourceRequestStatus").hide();
    $("#resourceRequestStatus .alert").hide();
    $("#requestResourceSubmitBtn").removeClass('disabled');
}

function clearRepairResourceForm() {
    $("#durationLabel").html('Repair duration for next days:');
    $("#repairRequestStatus").hide();
    $("#repairRequestStatus .alert").hide();
    $("#repairSubmitBtn").removeClass('disabled');
    $("#repair").html("");
    $("#requestInfo").hide();
    $("#requestResourceID").val("");
}

function repairBtn(resouceID, resourceStatus, requestResourceID) {
    clearRepairResourceForm();
    $("#repairModal").modal('show');
    $("#repairResourceID").val(resouceID);

    if (resourceStatus == 'In Use') {
        $("#durationLabel").html('Repair duration for next days after return:');
        $("#requestInfo").show();
        $("#requestResourceID").val(requestResourceID);
    }
}


function requestResource($this) {
    var formData = $this.serializeArray();
    var jsonData = {};

    //gets forms elements by name and created json as {name:value}
    $.map(formData, function (n, i) {
        n['value'] = (n['value'] == 'on' ? true : n['value']);
        jsonData[n['name']] = n['value'];
    });

    jsonData["action"] = "requestResource"; //what to do in php
    jsonData["username"] = user.Username;
    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/search.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            var data = $.parseJSON(response.responseText);
            var updatedInfo = data.resourceUpdated;

            $("#resourceRequestStatus").show();
            $("#resourceRequestStatus .alert").hide();

            if (data.hasOwnProperty("success") && data.success) {
                $("#resourceRequestStatus .alert-success").show();

                updatedInfo.forEach(function (item, index) {
                    $("#resource_" + item.ResourceID + " .status").html(item.ResourceStatus);

                    var availableDate = getAvailabilityDate(item.NextAvailableDate);
                    $("#resource_" + item.ResourceID + " .nextAvailable").html(availableDate);
                    $("#resource_" + item.ResourceID + " .request").addClass('disabled');
                    $("#resource_" + item.ResourceID + " .request").html('Requested By Me');
                })


                $("#requestResourceSubmitBtn").addClass('disabled');

            } //failed to insert request
            else {
                $("#resourceRequestStatus .alert-danger").show();
            }
        },
        //if server script was not reached
        error: function (xhr) {

            $("#resourceRequestStatus").show();
            $("#resourceRequestStatus .alert-danger").show();
            //$('#resourceRequestStatus .alert-danger').html(xhr.responseText);
        }
    });
}


function repair($this) {
    var formData = $this.serializeArray();
    var jsonData = {};

    //gets forms elements by name and created json as {name:value}
    $.map(formData, function (n, i) {
        n['value'] = (n['value'] == 'on' ? true : n['value']);
        jsonData[n['name']] = n['value'];
    });

    jsonData["action"] = "requestRepairResource"; //what to do in php
    jsonData["username"] = user.Username;
    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/actionButtons.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            var data = $.parseJSON(response.responseText);

            var updatedInfo = data.resorceUpdated;

            $("#repairRequestStatus").show();
            $("#repairRequestStatus .alert").hide();

            if (data.hasOwnProperty("success") && data.success) {
                $("#repairRequestStatus .alert-success").show();

                updatedInfo.forEach(function (item, index) {
                    $("#resource_" + item.ResourceID + " .status").html(item.ResourceStatus);
                    var availableDate = getAvailabilityDate(item.NextAvailableDate);
                    $("#resource_" + item.ResourceID + " .nextAvailable").html(availableDate);
                    $("#resource_" + item.ResourceID + " .repair").addClass('disabled');

                    if(item.ResourceStatus=='In Use'){
                        $("#resource_" + item.ResourceID + " .repair").html('Scheduled for Repair');
                    }
                    else{
                        $("#resource_" + item.ResourceID + " .repair").html('In Repair')
                    }
                })


                $("#repairSubmitBtn").addClass('disabled');
            } //failed to insert request
            else {
                $("#repairRequestStatus .alert-danger").show();
            }
        }
    });
}