/**
 * Created by Trevoris on 4/9/2016.
 * Patient Detail Javascript
 */

var dummyData = [
    ['ID', 'X', 'Y', 'Condition', 'Severity'],
    ['1', 80.66, 1.67, 'Stomach Ache', 2],
    ['2', 79.84, 1.36, 'Back', 1],
    ['3', 78.6, 1.84, 'Back', 3],
    ['3', 72.73, 2.78, 'Heart Ache', 1],
    ['4', 80.05, 2, 'Melinoma', 7]
];

var emptyData = [
    ['ID', 'X', 'Y', 'Condition', 'Severity'],
    ['1', 0, 0, '', 0]
    ];

var chartOptions = {
    /**
     * Setup chart to draw with no borders, axis, or legend
     */
    bubble: {textStyle: {fontSize: 11}},
    backgroundColor: 'none',
    axisTitlesPosition: 'none',
    colorAxis: {legend: {position: 'none'}},
    height: 500,
    width: 250,
    legend: {position: 'none'},
    titlePosition: 'none',
    hAxis: {textPosition: 'none', viewWindow: {max:250, min:0}, gridlines: {color: 'transparent'}},
    vAxis: {textPosition: 'none', viewWindow: {max:500, min:0}, gridlines: {color: 'transparent'}},
    chartArea: {width: '100%', height: '100%'}
};

var chart = null;
var globalPatientData = null;
var selectionMode = true;
var moveMode = false;
var modal = null;
var btn = null;
var span = null;

var globalPatientId = "";
var globalConditionId = "";

var availableDatesObj = [];

google.charts.load('current', {'packages':['corechart']});
//google.charts.setOnLoadCallback(drawSeriesChart);

function createGoogleChart( newData ){
    //Load chart instance with inital data
    //google.charts.load('current', {'packages':['corechart']});
    google.charts.setOnLoadCallback(drawCustomSeriesChart( newData ));
}

function drawSeriesChart() {

    var data = google.visualization.arrayToDataTable(dummyData);

    chart = new google.visualization.BubbleChart(document.getElementById('bodyChart'));
    chart.draw(data, chartOptions);

    google.visualization.events.addListener(chart, 'select', selectHandler);
    google.visualization.events.addListener(chart, 'click', clickHandler);

    /**
     * Action to perform on selection of an element
     */
    function selectHandler(e) {
        alert('The user selected' + JSON.stringify(chart.getSelection()) + ' items.\n e:' + JSON.stringify(e));
    }

    /**
     * Action to perform on click of the chart
     */
    function clickHandler(e) {

        //Do a check for selection mode to see if the next logic needs to happen
        var xCoordinate = chart.getChartLayoutInterface().getXLocation(e.x);
        var yCoordinate = chart.getChartLayoutInterface().getYLocation(e.y);

        /**
         * use e.x and e.y for coordinates to injury/disease
         */
        alert('x position: ' + xCoordinate +' e.x: ' + e.x + '\ny position: ' + yCoordinate + ' e.y: ' + e.y);
    }
}

function drawCustomSeriesChart( newData ) {

    var data = google.visualization.arrayToDataTable(newData);

    chart = new google.visualization.BubbleChart(document.getElementById('bodyChart'));
    chart.draw(data, chartOptions);

    google.visualization.events.addListener(chart, 'select', selectHandler);
    google.visualization.events.addListener(chart, 'click', clickHandler);

    //Check for empty data
    if (newData.length == 2 && (newData[1][1] == 0) && (newData[1][2] == 0)){
        data.removeRow(0);
    }

    /**
     * Action to perform on selection of an element
     */
    function selectHandler(e) {
        if (!moveMode) {
            var selection = chart.getSelection();
            if (selection[0] != undefined && selection[0] != null) {
                var conditionElement = selection[0].row;
                var condition = globalPatientData[conditionElement];
                //alert("Condition ID: " + condition.conditionId + "\nCondition Desc: " + condition.conditionDescription);
                if (condition != undefined && condition != null) {
                    var patientidval = condition.patientId.split("/");
                    var outputPatientId = "";

                    if (patientidval.length > 1) {
                        outputPatientId = patientidval[1];
                    } else {
                        outputPatientId = patientidval[0];
                    }

                    $("#hidden-update-condition-id").val(condition.conditionId);
                    $("#hidden-update-patient-id").val(outputPatientId);
                    $("#updateDisplayConditionName").val(condition.conditionDisplay);
                    $("#updateDisplayConditionDescription").val(condition.conditionDescription);
                    $("#clinical-update-status").val(condition.clinicalStatus);
                    $("#clinical-update-serverity").val(condition.serverityCode);
                    $("#update-recorddate").val(moment(condition.dateRecorded).format("YYYY-MM-DDTHH:mm"));
                    $("#update-onsetdate").val(moment(condition.onsetDateTime).format("YYYY-MM-DDTHH:mm"));

                    //$("#updateDisplayConditionAll").text(JSON.stringify(condition));
                    $("#updateConditionModal").modal();
                }else{
                    $("#updateConditionModal").modal();
                }
            }
        }else{
            var selection = chart.getSelection();
            if (selection[0] != undefined && selection[0] != null) {
                var conditionElement = selection[0].row;
                var condition = globalPatientData[conditionElement];
                //alert("Condition ID: " + condition.conditionId + "\nCondition Desc: " + condition.conditionDescription);

                var patientidval = condition.patientId.split("/");
                var outputPatientId = "";

                if (patientidval.length > 1) {
                    outputPatientId = patientidval[1];
                } else {
                    outputPatientId = patientidval[0];
                }

                globalPatientId = outputPatientId;
                globalConditionId = condition.conditionId;
                console.log("patient id: " + globalPatientId + " condition id: " + globalConditionId );
            }

            //moveMode = false;
            selectionMode = false;
            $("#bodyChart").addClass("highlightmedical");
            //$("#bodyChart").addClass("clickable");
            $("#alert-move-box").hide();
        }
        //alert('The user selected' + JSON.stringify(chart.getSelection()) + ' items.\n e:' + JSON.stringify(e));
    }

    /**
     * Action to perform on click of the chart
     */
    function clickHandler(e) {

        //Do a check for selection mode to see if the next logic needs to happen
        if (!selectionMode && !moveMode) {
            var xCoordinate = chart.getChartLayoutInterface().getXLocation(e.x);
            var yCoordinate = chart.getChartLayoutInterface().getYLocation(e.y);

            /**
             * use e.x and e.y for coordinates to injury/disease
             */
            $("#coordxval").text(e.x);
            $("#coordyval").text(e.y);
            /*$("#coordxval").text(xCoordinate);
            $("#coordyval").text(yCoordinate);*/
            //modal.style.display = "block";
            $("#newConditionModal").modal();
            //alert('x position: ' + xCoordinate + ' e.x: ' + e.x + '\ny position: ' + yCoordinate + ' e.y: ' + e.y);
        }else if (!selectionMode && moveMode){
            var loc = window.location;
            var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
            var url = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));

            var appendedUrl = (url + "moveCondition");

            $("#bodyChart").removeClass("clickable");

            var conditionObj = {};
            conditionObj.patientid = globalPatientId;
            conditionObj.conditionid = globalConditionId;
            conditionObj.coordinatex = e.x;
            conditionObj.coordinatey = e.y;

            console.log(JSON.stringify(conditionObj));

            $.ajax({
                url: appendedUrl,
                type: "POST",
                data: JSON.stringify(conditionObj),
                headers: {
                    'Content-Type':'application/json'
                },
                success: function(result){
                    console.log("Success: " + result);
                    location.reload();
                },
                error: function(result){
                    console.log("request failed" + result);
                }
            });
        }
    }
}

function escapeId( myid ) {

    return "#" + myid.replace( /(:|\.|\[|\]|,)/g, "\\$1" );

}

function escapeClass( myid ) {

    return "." + myid.replace( /(:|\.|\[|\]|,)/g, "\\$1" );

}

function escapeString( myid ) {

    return myid.replace( /(:|\.|\[|\]|,)/g, "\\$1" );

}

function showInitialRecords(){

    //Hide records
    $('.observationdisplay').hide();

    //Select first instance of records
    //var recordid = $('.date-selector').filter(":first").text();
    if (availableDatesObj.length > 0){
        var recordid = availableDatesObj[0];
        $('.observationdisplay.' + recordid).show();
    }
}

function createSlider(){
    //var slider = document.getElementById('slider');

    var slider = $('#slider').get(0);
    noUiSlider.create(slider, {
        start: 0,
        direction: 'rtl',
        step: 1,
        behaviour: 'snap',
        connect: 'lower',
        range: {
            'min': 0,
            'max': 100
        },
        format: {
            to: function ( value ) {
                return value;
            },
            from: function ( value ) {
                return Math.floor(value);
            }
        }
    });

    slider.noUiSlider.on('update', function( values, handle ) {
        var datetext = availableDatesObj[Math.floor(values[handle])];
        updateDisplayedDates(datetext);
        $("#slider-output").text(datetext);
    });
}

function setSliderInitialValue(){
    var slider = $('#slider').get(0);
    var initValue = slider.noUiSlider.get();
    var datetextinit = availableDatesObj[initValue];
    $("#slider-output").text(datetextinit);
}

function updateSliderValues( min, max){
    var slider = $('#slider').get(0);

    if ((max-1 == min) || (max<1)){
        slider.setAttribute('disabled', true);
    }else{
        slider.removeAttribute('disabled');
        slider.noUiSlider.updateOptions({
            range: {
                'min': min,
                'max': (max-1)
            }
        });
    }

}

function getAvailableDatesLists(){
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    var url = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));

    var appendedUrl = (url + "availableDatesList");

    $.ajax({
        url: appendedUrl,
        type: "POST",
        headers: {
            'Content-Type':'application/json'
        },
        success: function(result){
            //alert("Data Loaded: " + result );
            console.log("Data Loaded: " + result.length + " " + result[0]);
            availableDatesObj = result;
            showInitialRecords();
            updateSliderValues(0, (availableDatesObj.length));
            setSliderInitialValue();
            getConditionsList();
        },
        error: function(result){
            //alert("request failed");
            console.log("request failed" + result);
        }
    });
}

function getConditionsList(){
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    var url = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));

    var appendedUrl = (url + "conditionsList");

    $.ajax({
        url: appendedUrl,
        type: "POST",
        headers: {
            'Content-Type':'application/json'
        },
        success: function(result){
            //alert("Data Loaded: " + result );
            /*console.log("Data Loaded: " + result.length + " " + result[0]);
            console.log(result);*/
            globalPatientData = result;
            //console.log("condition list below");
            //console.log(result);
            setTimeout(function () {
                populatePatientChart( result );
                //$(".center-loading-container").hide();
                $("#loading-modal").modal("hide");
            },5000);
        },
        error: function(result){
            //alert("request failed");
            console.log("request failed" + result);
        }
    });
}

function addCondition(){
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    var url = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));

    var appendedUrl = (url + "addCondition");

    var patientId = $("#hidden-patient-id").val();
    var dateInput = $("#onsetdate").val();
    var dateTimeFormatted = new Date(dateInput).toISOString();
    var momentFormattedDate = moment(dateInput).format("YYYY-MM-DD");
    var dateRecorded = $("#recorddate").val();
    var xcoord = $("#coordxval").text();
    var ycoord = $("#coordyval").text();
    //var momentFormattedDT = moment(dateInput);
    //var dateFormatted = new Date(dateRecorded).toDateString();
    var conditionObj = {};

    conditionObj.resourceType = "Condition";
    conditionObj.patientid = patientId;
    conditionObj.asserter = "Practitioner-18392";
    conditionObj.dateRecorded = momentFormattedDate;
    conditionObj.codingsystem = "http://snomed.info/sct";
    conditionObj.codecode = "A666";
    conditionObj.codedisplay = $("#condition-field").val();
    conditionObj.codetext = $("#condition-field").val();
    conditionObj.clinicalStatustype = "http://hl7.org/fhir/2015May/condition-status.html";
    conditionObj.severitysystem = "http://snomed.info/sct";
    conditionObj.severitycode = $("#clinical-serverity").val();
    conditionObj.onsetDateTime = dateTimeFormatted;
    conditionObj.notes = $("#condition-field-details").val();
    conditionObj.clinicalStatus = $("#clinical-status").val();
    conditionObj.coordinatex = xcoord;
    conditionObj.coordinatey = ycoord;


    //Old version
    /*conditionObj.resourceType = "Condition";
    conditionObj.patient = {};
    conditionObj.patient.reference = ("Patient/"+patientId);
    conditionObj.asserter = {};
    conditionObj.asserter.reference = ("Practitioner/"+"Practitioner-18392");
    conditionObj.dateRecorded = momentFormattedDate;
    conditionObj.code = {};
    conditionObj.code.coding = [];
    conditionObj.code.coding[0] = {};
    conditionObj.code.coding[0].system = "http://snomed.info/sct";
    conditionObj.code.coding[0].code = "A666";
    conditionObj.code.coding[0].display = $("#condition-field").val();
    conditionObj.code.text = $("#condition-field").val();
    conditionObj.clinicalStatus = "http://hl7.org/fhir/2015May/condition-status.html";
    conditionObj.severity = {};
    conditionObj.severity.coding = [];
    conditionObj.severity.coding[0] = {};
    conditionObj.severity.coding[0].system = "http://snomed.info/sct";
    conditionObj.severity.coding[0].code = $("#clinical-serverity").val();
    conditionObj.onsetDateTime = dateTimeFormatted;
    conditionObj.notes = $("#condition-field-details").val();
    conditionObj.clinicalStatus = $("#clinical-status").val();
    */

    /*conditionObj.x = $("#coordxval").text();
    conditionObj.y = $("#coordyval").text();
    conditionObj.condition = $("#condition-field").val();
    conditionObj.conditionDesc = $("#condition-field-details").val();
    conditionObj.status = $("#clinical-status").val();
    conditionObj.serverity = $("#clinical-serverity").val();
    conditionObj.recorddate = $("#recorddate").val();
    conditionObj.onsetdate = $("#onsetdate").val();*/

    console.log(JSON.stringify(conditionObj));

    $.ajax({
        url: appendedUrl,
        type: "POST",
        data: JSON.stringify(conditionObj),
        headers: {
            'Content-Type':'application/json'
        },
        success: function(result){
            console.log("Success: " + result);
            //getConditionsList();
            $('#body_img').hide();
            //$(".center-loading-container").show();
            $("#loading-modal").modal("show");
            location.reload();
        },
        error: function(result){
            //alert("request failed");
            console.log("request failed" + result);
        }
    });
}

function updateCondition(){
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    var url = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));

    var appendedUrl = (url + "updateCondition");

    var patientId = $("#hidden-update-patient-id").val();
    var conditionId = $("#hidden-update-condition-id").val();
    var dateInput = $("#update-onsetdate").val();
    var dateTimeFormatted = new Date(dateInput).toISOString();
    var momentFormattedDate = moment(dateInput).format("YYYY-MM-DD");
    var dateRecorded = moment($("#update-recorddate").val()).format("YYYY-MM-DD");
    var xcoord = $("#coordxval").text();
    var ycoord = $("#coordyval").text();
    //var momentFormattedDT = moment(dateInput);
    //var dateFormatted = new Date(dateRecorded).toDateString();
    var conditionObj = {};

    conditionObj.resourceType = "Condition";
    conditionObj.patientid = patientId;
    conditionObj.conditionid = conditionId;
    conditionObj.asserter = "Practitioner-18392";
    conditionObj.dateRecorded = dateRecorded;
    conditionObj.codingsystem = "http://snomed.info/sct";
    conditionObj.codecode = "A666";
    conditionObj.codedisplay = $("#updateDisplayConditionName").val();
    conditionObj.codetext = $("#updateDisplayConditionName").val();
    conditionObj.clinicalStatustype = "http://hl7.org/fhir/2015May/condition-status.html";
    conditionObj.severitysystem = "http://snomed.info/sct";
    conditionObj.severitycode = $("#clinical-update-serverity").val();
    conditionObj.onsetDateTime = dateTimeFormatted;
    conditionObj.notes = $("#updateDisplayConditionDescription").val();
    conditionObj.clinicalStatus = $("#clinical-update-status").val();
    conditionObj.coordinatex = xcoord;
    conditionObj.coordinatey = ycoord;


    console.log(JSON.stringify(conditionObj));

    $.ajax({
        url: appendedUrl,
        type: "POST",
        data: JSON.stringify(conditionObj),
        headers: {
            'Content-Type':'application/json'
        },
        success: function(result){
            console.log("Success: " + result);
            //getConditionsList();
            $('#body_img').hide();
            //$(".center-loading-container").show();
            $("#loading-modal").modal("show");
            location.reload();
        },
        error: function(result){
            //alert("request failed");
            console.log("request failed" + result);
        }
    });
}

function deleteCondition(){
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    var url = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));

    var appendedUrl = (url + "deleteCondition");

    var patientId = $("#hidden-update-patient-id").val();
    var conditionId = $("#hidden-update-condition-id").val();
    var dateInput = $("#update-onsetdate").val();
    var dateTimeFormatted = new Date(dateInput).toISOString();
    var momentFormattedDate = moment(dateInput).format("YYYY-MM-DD");
    var dateRecorded = moment($("#update-recorddate").val()).format("YYYY-MM-DD");
    var xcoord = $("#coordxval").text();
    var ycoord = $("#coordyval").text();
    //var momentFormattedDT = moment(dateInput);
    //var dateFormatted = new Date(dateRecorded).toDateString();
    var conditionObj = {};

    conditionObj.resourceType = "Condition";
    conditionObj.patientid = patientId;
    conditionObj.conditionid = conditionId;
    conditionObj.asserter = "Practitioner-18392";
    conditionObj.dateRecorded = dateRecorded;
    conditionObj.codingsystem = "http://snomed.info/sct";
    conditionObj.codecode = "A666";
    conditionObj.codedisplay = $("#updateDisplayConditionName").val();
    conditionObj.codetext = $("#updateDisplayConditionName").val();
    conditionObj.clinicalStatustype = "http://hl7.org/fhir/2015May/condition-status.html";
    conditionObj.severitysystem = "http://snomed.info/sct";
    conditionObj.severitycode = $("#clinical-update-serverity").val();
    conditionObj.onsetDateTime = dateTimeFormatted;
    conditionObj.notes = $("#updateDisplayConditionDescription").val();
    conditionObj.clinicalStatus = $("#clinical-update-status").val();
    conditionObj.coordinatex = xcoord;
    conditionObj.coordinatey = ycoord;


    console.log(JSON.stringify(conditionObj));

    $.ajax({
        url: appendedUrl,
        type: "POST",
        data: JSON.stringify(conditionObj),
        headers: {
            'Content-Type':'application/json'
        },
        success: function(result){
            console.log("Success: " + result);
            //getConditionsList();
            $('#body_img').hide();
            //$(".center-loading-container").show();
            $("#loading-modal").modal("show");
            location.reload();
        },
        error: function(result){
            //alert("request failed");
            console.log("request failed" + result);
        }
    });
}

function getPatientDetails(){
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    var url = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));

    var appendedUrl = (url + "getPatientDetails");

    var patientId = $("#global-hidden-patient-id").val();

    $.ajax({
        url: appendedUrl,
        type: "POST",
        data: patientId,
        headers: {
            'Content-Type':'application/json'
        },
        success: function(result){
            //console.log(JSON.stringify(result));
            $("#loading-modal").modal("hide");
            populatePatientUpdateModal(result);


        },
        error: function(result){
            //alert("request failed");
            console.log("request failed" + JSON.stringify(result));
        }
    });
}

function updatePatient(){
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    var url = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));

    var appendedUrl = (url + "updatePatient");

    var patientId = $("#global-hidden-patient-id").val();
    var dateInput = $("#update-onsetdate").val();
    var dateRecorded = moment($("#update-recorddate").val()).format("YYYY-MM-DD");
    var conditionObj = {};

    conditionObj.patientId = patientId;
    conditionObj.patientFname = $("#updateDisplayPatientFirstName").val();
    conditionObj.patientMidName = $("#updateDisplayPatientMiddleName").val();
    conditionObj.patientLname = $("#updateDisplayPatientLastName").val();
    conditionObj.suffix = $("#updateDisplayPatientTitleName").val();
    conditionObj.gender = $("#patient-clinical-gender").val();
    conditionObj.birthDt = $("#update-dob").val();
    conditionObj.addressLine1 = $("#update-addr-line1").val();
    conditionObj.addressLine2 = $("#update-addr-line2").val();
    conditionObj.city = $("#update-addr-city").val();
    conditionObj.state = $("#update-addr-state").val().toUpperCase();
    conditionObj.zipCode = $("#update-addr-zip").val();
    conditionObj.phonenum = $("#update-phone").val();
    conditionObj.email = $("#update-email").val();

    console.log(JSON.stringify(conditionObj));

    $.ajax({
        url: appendedUrl,
        type: "POST",
        data: JSON.stringify(conditionObj),
        headers: {
            'Content-Type':'application/json'
        },
        success: function(result){
            console.log("Success: " + result);
            //getConditionsList();
            $('#body_img').hide();
            //$(".center-loading-container").show();
            $("#loading-modal").modal("show");
            location.reload();
        },
        error: function(result){
            //alert("request failed");
            console.log("request failed" + result);
        }
    });
}

function updateDisplayedDates( availDate ){
    $('.observationdisplay').hide();
    $('.observationdisplay.' + availDate).show();
}

function populatePatientChart( patientData ){
    /**
     * Populating patient data
     */
    var newData = [
        ['ID', 'X', 'Y', 'Condition', 'Severity']
    ];

    console.log(patientData);

    for (var i=0; i<patientData.length; i++){
        var element = "";
        element = ("" + (i+1) + "");
        var elementx = 0;
        if (patientData[i].conditionx != null
            && $.isNumeric(patientData[i].conditionx)){
            elementx = Number(patientData[i].conditionx);
        }else{
            //elementx = Math.floor((Math.random() * 250) + 1);;
        }
        var elementy = 0;
        if (patientData[i].conditiony != null
            && $.isNumeric(patientData[i].conditiony)){
            elementy = (500 - Number(patientData[i].conditiony));
        }else{
            //elementy = Math.floor((Math.random() * 500) + 1);
        }
        newData.push([("" + patientData[i].conditionId + ""), elementx, elementy, patientData[i].conditionDisplay, patientData[i].serverityCode]);
    }


    console.log(newData);

    //var data = google.visualization.arrayToDataTable(newData);
    //chart.draw(data, chartOptions);
    if (newData.length > 1){
        createGoogleChart( newData );
    }else{
        createGoogleChart( emptyData );
    }

    $("#add-condition-btn").show();
    $("#move-condition-btn").show();
}

function populatePatientUpdateModal(response){
    $("#updateDisplayPatientFirstName").val(response.patientFname);
    $("#updateDisplayPatientMiddleName").val(response.patientMidName);
    $("#updateDisplayPatientLastName").val(response.patientLname);
    $("#updateDisplayPatientTitleName").val(response.suffix);
    $("#patient-clinical-gender").val(response.gender);
    $("#update-addr-line1").val(response.addressLine1);
    $("#update-addr-line2").val(response.addressLine2);
    $("#update-addr-city").val(response.city);
    $("#update-addr-state").val(response.state);
    $("#update-addr-zip").val(response.zipCode);
    $("#update-phone").val(response.phonenum);
    $("#update-email").val(response.email);
    var dateVal = moment(response.birthDt).format("YYYY-MM-DD");
    $("#update-dob").val(dateVal);

    $("#updatePatientModal").modal("show");
}



/*var dialog = $( "#dialog-form" ).dialog({
    autoOpen: false,
    height: 300,
    width: 350,
    modal: true,
    buttons: {
        "Create an account": function(){},
        Cancel: function() {
            dialog.dialog( "close" );
        }
    },
    close: function() {
        form[ 0 ].reset();
        allFields.removeClass( "ui-state-error" );
    }
});

var form = dialog.find( "form" ).on( "submit", function( event ) {
    event.preventDefault();
    addUser();
});*/


$( document ).ready(function() {
    $('#body_img').css('background', 'url("../../img/bodyoutline2.jpg") no-repeat');

    //showInitialRecords();
    //$(".center-loading-container").show();
    $("#loading-modal").modal();
    $("#loading-modal").modal("show");

    $("#add-condition-btn").hide();
    $("#move-condition-btn").hide();

    $("#main_info").hide();
    $("#alert-move-box").hide();

    createSlider();

    getAvailableDatesLists();

    $("#conditionAppend").click(function(e){

        /**
         * Populating random dummy data
         */
        var newDummyData = [
            ['ID', 'X', 'Y', 'Condition', 'Severity'],
            ['1', 130, 100, 'Blood Clot', 4],
            ['2', 150, 330, 'Back', 1],
            ['3', 200, 455, 'Head', 3]
        ];

        newDummyData[1][1] = Math.floor((Math.random() * 250) + 1);
        newDummyData[1][2] = Math.floor((Math.random() * 500) + 1);
        newDummyData[1][4] = Math.floor((Math.random() * 10) + 1);
        newDummyData[2][1] = Math.floor((Math.random() * 250) + 1);
        newDummyData[2][2] = Math.floor((Math.random() * 500) + 1);
        newDummyData[2][4] = Math.floor((Math.random() * 10) + 1);
        newDummyData[3][1] = Math.floor((Math.random() * 250) + 1);
        newDummyData[3][2] = Math.floor((Math.random() * 500) + 1);
        newDummyData[3][4] = Math.floor((Math.random() * 10) + 1);

        var data = google.visualization.arrayToDataTable(newDummyData);

        chart.draw(data, chartOptions);

        var loc = window.location;
        var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
        var url = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));

        //var url = "http://localhost:8080/createCoordinate";
        var appendedUrl = (url + "createCoordinate");
        //$(location).attr('href', (url + "patientSearch?id=" + inputVal));
        //var exampleUrl = "http://example.com/page?parameter=value&also=another";

        var patientId = $("#hidden-patient-id").val();
        var xcoord = $("#coordxval").val();
        var ycoord = $("#coordyval").val();
        var conditionName = $("#condition-field").val();
        var conditionDesc = $("#condition-field-details").val();
        var conditionSeverity = $("#clinical-serverity").val();
        var coordinateRequest = {
            patientId:patientId,
            conditionId: 1,
            coordinateX:xcoord,
            coordinateY:ycoord,
            conditionName:conditionName,
            conditionDesc:conditionDesc,
            conditionSeverity: conditionSeverity
        };

        $.ajax({
            url: appendedUrl,
            type: "POST",
            data: JSON.stringify(coordinateRequest),
            headers: {
                'Content-Type':'application/json'
            },
            success: function(result){
                //alert("Data Loaded: " + result );
                console.log("Data Loaded: " + result);
            },
            error: function(result){
                //alert("request failed");
                console.log("request failed" + result);
            }
        });
        /*$.ajax({
            url: exampleUrl,
            type: "GET",
            headers: {
                'Content-Type':'application/json',
                'Access-Control-Allow-Origin':'*'
            },
            success: function(result){
                alert("Data Loaded: " + result );
            }
        });*/
        /*$.get( exampleUrl, function( data ) {
            alert( "Data Loaded: " + data );
        });*/
        /*$.ajax({
            type: "GET",
            url: exampleUrl,
            data: data,
            success: function(e){
                //alert(JSON.stringify(e));
                console.log(JSON.stringify(e));
            },
            error: function(e) {
                console.log(e);
            },
            dataType: "json"
        });*/
    });

    $(".date-selector").click(function(e){
        //alert($(this).text());
        //var availDate = $(this).attr("value");
        var availDate = $(this).text();
        $('.observationdisplay').hide();

        $('.observationdisplay.' + availDate).show();
        //$(".observationdisplay").hide();

        //alert(availDate);
        //$(".observationdisplay."+availDate).show();
        //console.log(availDate.replace("/\//g", '\\/'));
        //$(".o3\\/2\\/1").hide();
        //$('.observationdisplay' + escapeString(availDate)).hide();
        //$("."+availDate).hide();
    });

    $('#newConditionModal').on('hide.bs.modal', function (event) {
        selectionMode = true;
        $("#bodyChart").removeClass("highlightmedical");
    });

    $("#add-condition-btn").click(function(e){
        selectionMode = false;
        $("#bodyChart").addClass("highlightmedical");
    });

    $("#move-condition-btn").click(function(e){
        moveMode = true;
        $("#alert-move-box").show();
    });

    $("#submit-condition").click(function(e){
        $("#bodyChart").removeClass("highlightmedical");
        //modal.style.display = "none";
        selectionMode = true;
        addCondition();
    });

    $("#save-condition-changes").click(function(e){
        updateCondition();
    });

    $("#delete-condition-changes").click(function(e){
        deleteCondition();
    });

    $("#custMyModal").click(function(e){
        //$("#newConditionModal").modal();
    });

    $("#edit-patient-details").click(function(e){
        $("#loading-modal").modal("show");
        getPatientDetails();
    });

    $("#save-patient-changes").click(function(e){
        updatePatient();
    });
});

