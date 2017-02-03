/**
 *  Jquery logic to handle homepage
 *  Will be replaced with angularJs at later date
 *
 **/

function searchForPatient(){
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    var url = loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
    var inputVal = $("#searchbar").val();

    //Routing logic
    if ($.isNumeric( inputVal )) {
        $(location).attr('href', (url + "patientSearch?id=" + inputVal));
    }else if ((inputVal.length > 7) && (inputVal.substring(0, 7) == "Patient")) {
        $(location).attr('href', (url + "patientSearch?id=" + inputVal));
    }else{
        var nameArray = inputVal.split(" ");
        if (nameArray.length > 1){
            //More than 1 name
            $(location).attr('href', (url + "patientSearch?fName=" + nameArray[0] + "&lName=" + nameArray[1]));
        }else{
            //Only 1 name exists
            $(location).attr('href', (url + "patientSearch?name=" + inputVal));
        }

    }
    //$.get( "/greetingId", { id: $("#searchbar").val() } );
}

$( document ).ready(function() {


    $( "#searchbar" ).keypress(function( event ) {
        if ( event.which == 13 ) {
            searchForPatient();
            //event.preventDefault();
        }
    });

    $("#search-btn").click(function (){
        searchForPatient();
    });
});