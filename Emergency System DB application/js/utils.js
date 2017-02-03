/* exit the app and display login page */

var searchParams={};

function exit() {
    sessionStorage.removeItem('user'); //destroy browser session info for user
    window.location.href = "login.html";
}

/* redirect to main menu */
function homePage() {
    window.location.href = "mainMenu.html"
}

/* refresh Resource status Page*/
function refreshResourceStatusPage() {
    window.location.href = "resourceStatus.html"
}

/* displays user info */
function setUserInto() {

    // $("#name").html(user.Name);
    $("#userName").html(ucFirstAllWords(user.Name));
    $("#userTypeName").html(ucFirstAllWords(user.Type));

    if (user.Type == "3") {
        $("#userDetailGroup").show();
        $("#userDetailsLabel").html("Company Headquarters: ");
        $("#userDetails").html(user.Detail);
        $("#userTypeName").html("Company");
    }
    else if (user.Type == "2") {
        $("#userDetailGroup").show();
        $("#userDetailsLabel").html("Jurisdiction: ");
        $("#userDetails").html(user.Detail);
        $("#userTypeName").html("Government Agency");
    }
    else if (user.Type == "1") {
        $("#userDetailGroup").show();
        $("#userDetailsLabel").html("Population Size: ");
        $("#userDetails").html(user.Detail);
        $("#userTypeName").html("Municipality");
    }
    else if (user.Type == 0) {
        $("#userTypeName").html("Individual");
    }

}

 // filler so that arrays can be traversed with arrayName.forEach(function(item, index){});

// Production steps of ECMA-262, Edition 5, 15.4.4.18
// Reference: http://es5.github.io/#x15.4.4.18
if (!Array.prototype.forEach) {

    Array.prototype.forEach = function(callback, thisArg) {

        var T, k;

        if (this === null) {
            throw new TypeError(' this is null or not defined');
        }

        // 1. Let O be the result of calling toObject() passing the
        // |this| value as the argument.
        var O = Object(this);

        // 2. Let lenValue be the result of calling the Get() internal
        // method of O with the argument "length".
        // 3. Let len be toUint32(lenValue).
        var len = O.length >>> 0;

        // 4. If isCallable(callback) is false, throw a TypeError exception.
        // See: http://es5.github.com/#x9.11
        if (typeof callback !== "function") {
            throw new TypeError(callback + ' is not a function');
        }

        // 5. If thisArg was supplied, let T be thisArg; else let
        // T be undefined.
        if (arguments.length > 1) {
            T = thisArg;
        }

        // 6. Let k be 0
        k = 0;

        // 7. Repeat, while k < len
        while (k < len) {

            var kValue;

            // a. Let Pk be ToString(k).
            //    This is implicit for LHS operands of the in operator
            // b. Let kPresent be the result of calling the HasProperty
            //    internal method of O with argument Pk.
            //    This step can be combined with c
            // c. If kPresent is true, then
            if (k in O) {

                // i. Let kValue be the result of calling the Get internal
                // method of O with argument Pk.
                kValue = O[k];

                // ii. Call the Call internal method of callback with T as
                // the this value and argument list containing kValue, k, and O.
                callback.call(T, kValue, k, O);
            }
            // d. Increase k by 1.
            k++;
        }
        // 8. return undefined
    };
}


function populateSearchForm(hasAdditionalESF) {
    var jsonData = {action: "getDropopdownsData"};
    var stringData = JSON.stringify(jsonData);

    $.ajax({
        type: 'POST',
        url: 'server/search.php',
        data: stringData,
        dataType: 'json',
        complete: function (response) {
            var data = $.parseJSON(response.responseText);


            if (data.hasOwnProperty('esfs')) {
                var esfs = data.esfs; //data

                //ESF dropdowns
                var dropdownHTML = '  <option value="" selected="selected"></option>';
                esfs.forEach(function (item, index) {
                    dropdownHTML += '  <option value="' + item.UniqueNumber + '">' + item.Description + '</option>';
                });

                //append to contaner that supposed to have the dropdown
                $("#esfsSelect").html(dropdownHTML);


                //additional ESFS
                if(typeof hasAdditionalESF!=undefined && hasAdditionalESF){
                    //ESF dropdowns
                    var dropdownHTML = '  <option value="" selected="selected"></option>';
                    esfs.forEach(function (item, index) {
                        dropdownHTML += '  <option value="' + item.UniqueNumber + '">' + item.Description + '</option>';
                    });

                    //append to contaner that supposed to have the dropdown
                    $("#esfsSelectAdditional").html(dropdownHTML);
                }

            }


            if (data.hasOwnProperty('incidents')) {
                var incidents = data.incidents; //data

                //dropdowns
                var dropdownHTML = '  <option value=""></option>'; //default; when empty get all esf
                incidents.forEach(function (item, index) {
                    dropdownHTML += '  <option data-owner="'+item.IncidentUsername+'" value="' + item.ID + '">' + item.Description + '</option>';
                });

                //append to contaner that supposed to have the dropdown
                $("#incidentSelect").html(dropdownHTML);
            }

        },
        error: function (xhr) {
            //TODO: remove later
            console.log(xhr.responseText);
        }
    });
}

function hideErrorMessage() {
    $('#divError').hide();
    $('#divError .errorMessage').html("");
}

function ucFirstAllWords( str )
{
    str.toLowerCase();
    var pieces = str.split(" ");
    for ( var i = 0; i < pieces.length; i++ )
    {
        var j = pieces[i].charAt(0).toUpperCase();
        pieces[i] = j + pieces[i].substr(1);
    }
    return pieces.join(" ");
}
