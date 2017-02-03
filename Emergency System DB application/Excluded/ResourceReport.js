function displayResourceInUse(data){

    var sHTML = '<div class="row bottomBorder">';
    sHTML += '<h4 class="col-md-1">ID</h4>';
    sHTML += '<h4 class="col-md-2"> Resource Name</h4>';
    sHTML += '<h4 class="col-md-2">Incident</h4>';
    sHTML += '<h4 class="col-md-2">Owner</h4>';
    sHTML += '<h4 class="col-md-1">Start Date</h4>';
    sHTML += '<h4 class="col-md-1">Return By</h4>';
    sHTML += '<h4 class="col-md-1">Action</h4>';
    sHTML += '</div>';

    //records
    sHTML += '<div class="recordsWrapper ">';

    if(data.length==0){
        sHTML += '<div>No Records</div>';
    }
    else {
        data.forEach(function (item, index) {
            sHTML += '<div class="col-md-1 resultCell">' + item.ID + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.ResourceName + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.Description + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.Username + '</div>';
            sHTML += '<div class="col-md-1 resultCell">' + item.StartDate + '</div>';
            sHTML += '<div class="col-md-1 resultCell">' + item.ReturnDate + '</div>';
            var actionButtons = getReturnButtons(item.ID);
            sHTML += '<div class="col-md-1 resultCell">' + actionButtons + '</div>';
        });
    }
    sHTML += '</div>';

    $("#resourcesInUse").html(sHTML);
}

function getReturnButtons(resourceID) {
    var buttons = "&nbsp;" //default - no buttons display
    buttons += '<button class="btn btn-default actionBtn return" onclick="returns(\'' + resourceID + '\')">Return</button>';
    return buttons;
}

function returns(resourceID) {

    hideErrorMessage();

    var jsonData = {};
    jsonData["action"] = "return"; //what to do in php
    jsonData["resourceID"] = resourceID;
    jsonData["needsUpdate"]=true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/resourceStatus.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            var data = $.parseJSON(response.responseText);
            //successfully returned -> re-do search to retrive the latest data
            if (data.hasOwnProperty('success') && data.success) {
                var updatedInfo = data.resorceUpdated;

                $("#resourceRequest_"+updatedInfo.ID+" .returnDate").html(updatedInfo.ReturnDate);
                $("#resource_"+updatedInfo.ID+" .status").html(updatedInfo.Status);

            }
            else{
                $('#divError').show();
                $('#divError .errorMessage').html("Resource was not returned.");
            }
        },
        //if server script was not reached
        error: function (xhr) {
            // $('#divError').show();
            //$('#divError').html(xhr.responseText);
        }
    })
}

function displayResourcesRequestedByMe(data){
    var sHTML = '<div class="row bottomBorder">';
    sHTML += '<h4 class="col-md-1">ID</h4>';
    sHTML += '<h4 class="col-md-2"> Resource Name</h4>';
    sHTML += '<h4 class="col-md-2">Incident</h4>';
    sHTML += '<h4 class="col-md-2">Owner</h4>';
    sHTML += '<h4 class="col-md-1">Return By</h4>';
    sHTML += '<h4 class="col-md-1">Action</h4>';
    sHTML += '</div>';

    //records
    sHTML += '<div class="recordsWrapper ">';
    if(data.length==0){
        sHTML += '<div>No Records</div>';
    }
    else {
        data.forEach(function (item, index) {
            sHTML += '<div class="col-md-1 resultCell">' + item.ID + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.ResourceName + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.Description + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.Username + '</div>';
            sHTML += '<div class="col-md-1 resultCell">' + item.ReturnDate + '</div>';
            var actionButtons = getCancelButtons(item.ID);
            sHTML += '<div class="col-md-1 resultCell">' + actionButtons + '</div>';
        });
    }
    sHTML += '</div>';


    $("#resourcesRequestedByMe").html(sHTML);
}

function getCancelButtons(requestID) {
    var buttons = "&nbsp;" //default - no buttons display
    buttons += '<button class="btn btn-default actionBtn cancel" onclick="cancel(\'' + requestID + '\')">Cancel</button>';
    return buttons;
}

function cancel(requestID) {

    hideErrorMessage();

    var jsonData = {};
    jsonData["action"] = "cancel"; //what to do in php
    jsonData["requestID"] = requestID;
    jsonData["needsUpdate"]=true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/resourceStatus.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            var data = $.parseJSON(response.responseText);
            //todo: how does this section change for a delete?
            //successfully cancelled -> re-do search to retrive the latest data
            if (data.hasOwnProperty('success') && data.success) {
                var updatedInfo = data.resorceUpdated;

                $("#resourceRequest_"+updatedInfo.ID+" .status").html(updatedInfo.ResourceStatus);


            }
            else{
                $('#divError').show();
                $('#divError .errorMessage').html("Request was not cancelled.");
            }
        },
        //if server script was not reached
        error: function (xhr) {
            // $('#divError').show();
            //$('#divError').html(xhr.responseText);
        }
    })
}

function displayResourceReceivedByMe(data){
    var sHTML = '<div class="row bottomBorder">';
    sHTML += '<h4 class="col-md-1">ID</h4>';
    sHTML += '<h4 class="col-md-2">Resource Name</h4>';
    sHTML += '<h4 class="col-md-2">Requested By</h4>';
    sHTML += '<h4 class="col-md-1">Return By</h4>';
    sHTML += '<h4 class="col-md-1">Action</h4>';
    sHTML += '</div>';

    //records
    sHTML += '<div class="recordsWrapper ">';
    if(data.length==0){
        sHTML += '<div>No Records</div>';
    }
    else {
        data.forEach(function (item, index) {
            sHTML += '<div class="col-md-1 resultCell">' + item.ID + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.ResourceName + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.Description + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.Username + '</div>';
            sHTML += '<div class="col-md-1 resultCell">' + item.ReturnDate + '</div>';
            var actionButtons = getRequestButtons(item.ID,item.Username,item.Status,item.IncidentID);
            sHTML += '<div class="col-md-1 resultCell">' + actionButtons + '</div>';
        });
    }
    sHTML += '</div>';

    $("#resourcesReceivedByMe").html(sHTML);
}

function getRequestButtons(requestID,username,status,incidentID) {
    var buttons = "&nbsp;" //default - no buttons display
    if (status=="Available") {
        buttons += '<button class="btn btn-default actionBtn deploy" onclick="deploy(\'' + requestID + '\',\'' + incidentID + '\',\'' + username + '\')">Deploy</button>';
    }
    buttons += '<button class="btn btn-default actionBtn reject" onclick="reject(\'' + requestID + '\')">Reject</button>';
    return buttons;
}

function deploy(resourceID, incidentID, username) {

    hideErrorMessage();

    var jsonData = {};
    jsonData["action"] = "deploy"; //what to do in php
    jsonData["username"] = username;
    jsonData["resourceID"] = resourceID;
    jsonData["incidentID"] = incidentID;
    jsonData["needsUpdate"]=true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/resourceStatus.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            var data = $.parseJSON(response.responseText);
            //successfully deployed -> re-do search to retrive the latest data
            if (data.hasOwnProperty('success') && data.success) {
                var updatedInfo = data.resorceUpdated;

                $("#resource_"+updatedInfo.ID+" .status").html(updatedInfo.ResourceStatus);
                $("#resource_"+updatedInfo.ID+" .nextAvailable").html(updatedInfo.NextAvailableDate);

            }
            else{
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

function reject(requestID) {

    hideErrorMessage();

    var jsonData = {};
    jsonData["action"] = "reject"; //what to do in php
    jsonData["requestID"] = requestID;
    jsonData["needsUpdate"]=true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/resourceStatus.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            var data = $.parseJSON(response.responseText);
            //todo: how does this section change for a delete?
            //successfully cancelled -> re-do search to retrive the latest data
            if (data.hasOwnProperty('success') && data.success) {
                var updatedInfo = data.resorceUpdated;

                $("#resourceRequestReceived_"+updatedInfo.ID+" .status").html(updatedInfo.ResourceStatus);


            }
            else{
                $('#divError').show();
                $('#divError .errorMessage').html("Request was not rejected.");
            }
        },
        //if server script was not reached
        error: function (xhr) {
            // $('#divError').show();
            //$('#divError').html(xhr.responseText);
        }
    })
}

function displayResourcesInRepair(data){
    var resourcesInRepair = data.resourcesInRepair;
    var sHTML = '<div class="row bottomBorder">';
    sHTML += '<h4 class="col-md-1">ID</h4>';
    sHTML += '<h4 class="col-md-2"> Resource Name</h4>';
    sHTML += '<h4 class="col-md-2">Start on</h4>';
    sHTML += '<h4 class="col-md-2">Ready By</h4>';
    sHTML += '<h4 class="col-md-1">Action</h4>';
    sHTML += '</div>';

    //records
    sHTML += '<div class="recordsWrapper ">';
    if(data.length==0){
        sHTML += '<div>No Records</div>';
    }
    else {
        data.forEach(function (item, index) {
            sHTML += '<div class="col-md-1 resultCell">' + item.ID + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.ResourceName + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.DateStarted + '</div>';
            sHTML += '<div class="col-md-1 resultCell">' + item.ReadyBy + '</div>';
            var actionButtons = getCancelRepairButtons(item.ID, item.Status);
            sHTML += '<div class="col-md-1 resultCell">' + actionButtons + '</div>';

        });
    }
    sHTML += '</div>';

    $("#resourcesReceivedByMe").html(resourcesInRepair);
}

function getCancelRepairButtons(repairID,status) {
    var buttons = "&nbsp;" //default - no buttons display
    if (status==0) {
        buttons += '<button class="btn btn-default actionBtn cancelRepair" onclick="cancelRepair(\'' + repairID + '\')">Cancel</button>';
    }
    return buttons;
}

function cancelRepair(repairID) {

    hideErrorMessage();

    var jsonData = {};
    jsonData["action"] = "cancelRepair"; //what to do in php
    jsonData["repairID"] = repairID;
    jsonData["needsUpdate"]=true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/resourceStatus.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            var data = $.parseJSON(response.responseText);
            //todo: how does this section change for a delete?
            //successfully cancelled -> re-do search to retrive the latest data
            if (data.hasOwnProperty('success') && data.success) {
                var updatedInfo = data.resorceUpdated;

                $("#repair_"+updatedInfo.ID+" .status").html(updatedInfo.ResourceStatus);


            }
            else{
                $('#divError').show();
                $('#divError .errorMessage').html("Repair was not cancelled.");
            }
        },
        //if server script was not reached
        error: function (xhr) {
            // $('#divError').show();
            //$('#divError').html(xhr.responseText);
        }
    })
}