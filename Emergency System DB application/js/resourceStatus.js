function displayResourceInUse(data) {

    var sHTML = '<div class="row bottomBorder">';
    sHTML += '<h5 class="col-md-1">ID</h5>';
    sHTML += '<h5 class="col-md-2">Resource Name</h5>';
    sHTML += '<h5 class="col-md-2">Incident</h5>';
    sHTML += '<h5 class="col-md-2">Owner</h5>';
    sHTML += '<h5 class="col-md-1">Start Date</h5>';
    sHTML += '<h5 class="col-md-1">Return By</h5>';
    sHTML += '<h5 class="col-md-2">Action</h5>';
    sHTML += '</div>';

    //records
    sHTML += '<div class="recordsWrapper ">';

    if (data.length == 0) {
        sHTML += '<div>No Records</div>';
    }
    else {
        data.forEach(function (item, index) {
            sHTML += '<div class="row">';
            sHTML += '<div class="col-md-1 resultCell">' + item.ID + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.ResourceName + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.Description + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.Username + '</div>';
            sHTML += '<div class="col-md-1 resultCell">' + item.StartDate + '</div>';
            sHTML += '<div class="col-md-1 resultCell">' + item.ReturnDate + '</div>';
            var actionButtons = getReturnButtons (item.ID);
            sHTML += '<div class="col-md-2 resultCell">' + actionButtons + '</div>';
            sHTML += '</div>';
        });
    }
    sHTML += '</div>';

    $("#resourcesInUse").html(sHTML);
}

function getReturnButtons( resourceID) {
    var buttons = "&nbsp;" //default - no buttons display
    buttons += '<button class="btn btn-default actionBtn return" onclick="returns(\'' + resourceID + '\')">Return</button>';
    return buttons;
}

function returns(resourceID) {

    hideErrorMessage();

    var jsonData = {};
    jsonData["action"] = "return"; //what to do in php
    jsonData["resourceID"]=resourceID;
    jsonData["username"]= user.Username;
    jsonData["needsUpdate"] = true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/resourceStatus.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            refreshResourceStatusPage();
        },
        //if server script was not reached
        error: function (xhr) {
            // $('#divError').show();
            //$('#divError').html(xhr.responseText);
        }
    })
}

function displayResourcesRequestedByMe(data) {
    var sHTML = '<div class="row bottomBorder">';
    sHTML += '<h5 class="col-md-1">ID</h5>';
    sHTML += '<h5 class="col-md-3">Resource Name</h5>';
    sHTML += '<h5 class="col-md-3">Incident</h5>';
    sHTML += '<h5 class="col-md-2">Owner</h5>';
    sHTML += '<h5 class="col-md-1">Return By</h5>';
    sHTML += '<h5 class="col-md-2">Action</h5>';
    sHTML += '</div>';

    //records
    sHTML += '<div class="recordsWrapper ">';
    if (data.length == 0) {
        sHTML += '<div>No Records</div>';
    }
    else {
        data.forEach(function (item, index) {
             sHTML += '<div class="row">';
            sHTML += '<div class="col-md-1 resultCell">' + item.ID + '</div>';
            sHTML += '<div class="col-md-3 resultCell">' + item.ResourceName + '</div>';
            sHTML += '<div class="col-md-3 resultCell">' + item.Description + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.Username + '</div>';
            sHTML += '<div class="col-md-1 resultCell">' + item.ReturnDate + '</div>';
            var actionButtons = getCancelButtons(item.RequestID);
            sHTML += '<div class="col-md-2 resultCell">' + actionButtons + '</div>';
            sHTML += '</div>';
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
    jsonData["action"] = "cancelRequest"; //what to do in php
    jsonData["requestID"] = requestID;
    jsonData["needsUpdate"] = true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/resourceStatus.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            refreshResourceStatusPage();
        },
        //if server script was not reached
        error: function (xhr) {
            // $('#divError').show();
            //$('#divError').html(xhr.responseText);
        }
    })
}

function displayResourceReceivedByMe(data) {
    var sHTML = '<div class="row bottomBorder">';
    sHTML += '<h5 class="col-md-1">ID</h5>';
    sHTML += '<h5 class="col-md-3">Resource Name</h5>';
    sHTML += '<h5 class="col-md-3">Resource Description</h5>';
    sHTML += '<h5 class="col-md-2">Requested By</h5>';
    sHTML += '<h5 class="col-md-1">Return By</h5>';
    sHTML += '<h5 class="col-md-2">Action</h5>';
    sHTML += '</div>';

    //records
    sHTML += '<div class="recordsWrapper">';
    if (data.length == 0) {
        sHTML += '<div>No Records</div>';
    }
    else {
        data.forEach(function (item, index) {
            sHTML += '<div class="row ">';
            sHTML += '<div class="col-md-1 resultCell">' + item.ID + '</div>';
            sHTML += '<div class="col-md-3 resultCell">' + item.ResourceName + '</div>';
            sHTML += '<div class="col-md-3 resultCell">' + item.Description + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.RequestedBy + '</div>';
            sHTML += '<div class="col-md-1 resultCell">' + item.ReturnDate + '</div>';
            var actionButtons = getRequestButtons(item.ID, item.RequestID, item.Username, item.Status, item.IncidentID);
            sHTML += '<div class="col-md-2 resultCell">' + actionButtons + '</div>';
            sHTML += '</div>';
        });
    }
    sHTML += '</div>';

    $("#resourcesReceivedByMe").html(sHTML);
}

function getRequestButtons(resourceID, requestID, username, status, incidentID) {
    var buttons = "&nbsp;" //default - no buttons display

    buttons += '<button class="btn btn-default actionBtn reject" onclick="reject(\'' + requestID + '\')">Reject</button>';
    if (status != "In Use") {
        buttons += '<button class="btn btn-default actionBtn deploy" onclick="deploy(\'' + requestID + '\',\'' + incidentID + '\',\'' + username + '\')">Deploy</button>';
    }

    return buttons;
}

function deploy(requestID,incidentID, username) {

    hideErrorMessage();

    var jsonData = {};
    jsonData["action"] = "deploy"; //what to do in php
    jsonData["username"] = username;
    jsonData["requestID"] = requestID;
    jsonData["incidentID"] = incidentID;
    jsonData["needsUpdate"] = true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/resourceStatus.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            refreshResourceStatusPage();
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
    jsonData["action"] = "rejectRequest"; //what to do in php
    jsonData["requestID"] = requestID;
    jsonData["needsUpdate"] = true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/resourceStatus.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            refreshResourceStatusPage();
        },
        //if server script was not reached
        error: function (xhr) {
            // $('#divError').show();
            //$('#divError').html(xhr.responseText);
        }
    })
}

function displayResourcesInRepair(data) {

    var sHTML = '<div class="row bottomBorder">';
    sHTML += '<h5 class="col-md-2">ID</h5>';
    sHTML += '<h5 class="col-md-3">Resource Name</h5>';
    sHTML += '<h5 class="col-md-3">Start on</h5>';
    sHTML += '<h5 class="col-md-2">Ready By</h5>';
    sHTML += '<h5 class="col-md-2">Action</h5>';
    sHTML += '</div>';

    //records
    sHTML += '<div class="recordsWrapper ">';
    if (data.length == 0) {
        sHTML += '<div>No Records</div>';
    }
    else {
        data.forEach(function (item, index) {
            sHTML += '<div class="row">';
            sHTML += '<div class="col-md-2 resultCell">' + item.ID + '</div>';
            sHTML += '<div class="col-md-3 resultCell">' + item.ResourceName + '</div>';
            sHTML += '<div class="col-md-3 resultCell">' + item.DateStarted + '</div>';
            sHTML += '<div class="col-md-2 resultCell">' + item.ReadyBy + '</div>';
            var actionButtons = getCancelRepairButtons(item.RepairID, item.Status);
            sHTML += '<div class="col-md-2 resultCell">' + actionButtons + '</div>';
            sHTML += '</div>';
        });
    }
    sHTML += '</div>';

    $("#resourcesInRepair").html(sHTML);
}

function getCancelRepairButtons(resourceID, status) {
    var buttons = "&nbsp;" //default - no buttons display
    if (status == 0) {
        buttons += '<button class="btn btn-default actionBtn cancelRepair" onclick="cancelRepair(\'' + resourceID + '\')">Cancel</button>';
    }
    return buttons;
}

function cancelRepair(resourceID) {

    hideErrorMessage();

    var jsonData = {};
    jsonData["action"] = "cancelRepair"; //what to do in php
    jsonData["resourceID"] = resourceID;
    jsonData["needsUpdate"] = true;

    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/resourceStatus.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            refreshResourceStatusPage();
        },
        //if server script was not reached
        error: function (xhr) {
            // $('#divError').show();
            //$('#divError').html(xhr.responseText);
        }
    })
}