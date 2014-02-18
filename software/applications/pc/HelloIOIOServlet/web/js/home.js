console.log("JavaScript Running!");
// var element = document.getElementById("js-test");
// if(element){
// 	element.textContent = "I executed external JavaScript!";
// }
// $('#js-test').css("color", "#333333");


var App = function () {
    // Constructor
    this.init();
}

App.prototype = {

    init: function () {
        console.log("App Running!");
        this.configXML = this.getConfigXML();
        this.setupTable();
    },

    getConfigXML: function() {
        var xmlConfig = null
        $.get( "/ioioconfig?resource=config.xml", function( xml ) {
            var xmlObj = $( xml );
            xmlConfig = xmlObj;

            // Test XML Doc
            console.log(xmlObj);
            xmlObj.find("pin").each(function(index, value){
                console.log(index, value.getAttribute("type"));
            });
        });
        return xmlConfig;
    },

    setupTable: function(){
        // Loop through XML config Doc and create the table for 
        console.log("TODO: Create the config table.");
    },

    saveConfig: function(){
        // Loop through config table rows, generate XML doc, POST to server
        console.log("TODO: Build/Update XML doc and POST to server.");
    },

    attachHandlers: function(){
        // Attach Event handlers to Save new Config
        console.log("TODO: Attach event handlers to save XML");
    },
}

