console.log("JavaScript Running!");



var IOIOApp = function () {
    // Constructor
    this.init();
}

IOIOApp.prototype = {
    doutSubtypes: {
        "OD": "Open-Drain",
        "NL": "Normal"
    },

    dinSubtypes: {
        "FL": "Float",
        "PD": "Pull-Down",
        "PU": "Pull-Up",
    },

    pinTypes: {
        "ain": "Analog In",
        "dout": "Digital Out",
        "din": "Digital In"
    },

    analogValues: {
        "MinInput": 0,
        "MaxInput": 3.28676,
        "CenterInput": 1.6976545,
        "MinOutput": -100,
        "MaxOutput": 100,
        "CenterOutput": 0
    },

    init: function () {
        console.log("App Running!");
        this.getConfigXML();

    },

    getConfigXML: function() {
        var self = this; 
        var xmlConfig = null
        $.get( "/api/config?resource=config.xml", function( xml ) {
            self.configXML = $( xml );
            self.setupTable();
        });

    },

    setupTable: function(){
        // Loop through XML config Doc and create the table for 
        var strBuffer = [];
        var self = this;
        self.pinRowStore = [];

        // https://developer.mozilla.org/en-US/docs/Web/API/element
        self.configXML.find("pin").each(function(index, pin){
            var pinRow = new self.PinRow(self, pin);
            self.pinRowStore.push(pinRow);

            strBuffer.push("<tr>");
            
            // Pin Number Cell
            strBuffer.push("<td>");
            strBuffer.push(pinRow.number);
            strBuffer.push("</td>");

            // Pin Type Cell
            strBuffer.push("<td>");
            strBuffer.push(pinRow.getTypeSelect());
            strBuffer.push("</td>");

            // Pin Subtype Cell
            strBuffer.push("<td>");
            strBuffer.push(pinRow.getSubtypeSelect());
            strBuffer.push("</td>");

            // Pin I/O Status cell
            strBuffer.push("<td>");
            strBuffer.push(pinRow.getIOStatusHTML());
            strBuffer.push("</td>");
            
            strBuffer.push("</tr>");
        });

        this.tableContents = strBuffer.join("");
        $("#ioio-table-body").html(this.tableContents);
    },

    saveConfig: function(){
        // Loop through config table rows, generate XML doc, POST to server
        console.log("TODO: Build/Update XML doc and POST to server.");
    },

    attachHandlers: function(){
        // Attach Event handlers to Save new Config
        console.log("TODO: Attach event handlers to save XML");
    },
    
    PinRow: function (app, node){

        this.node = node;
        this.number = node.getAttribute("num");
        this.typeAbbr = node.getAttribute("type");
        this.type = app.pinTypes[this.typeAbbr];
        this.subtypeAbbr = (node.hasAttribute("subtype"))? node.getAttribute("subtype"): null;
        this.subtype = null;

        if (this.typeAbbr == "dout"){
            this.subtypeMap = app.doutSubtypes;
        } else if (this.typeAbbr == "din"){
            this.subtypeMap = app.dinSubtypes;
        }

        if (this.subtypeMap){
            this.subtype = this.subtypeMap[this.subtypeAbbr];
        }
        
        this.getTypeSelect = function(){
            var htmlBuffer = [];
            htmlBuffer.push("<select>");
            for (var key in app.pinTypes){
                // check hasOwnProperty
                if (!app.pinTypes.hasOwnProperty(key)){
                    continue;
                }

                var curType = app.pinTypes[key];
                htmlBuffer.push("<option ")
                if (curType == this.type){
                    htmlBuffer.push("selected='selected'");
                }
                htmlBuffer.push(">");
                htmlBuffer.push(curType);
                htmlBuffer.push("</option>");
            }
            htmlBuffer.push("</select>");
            return htmlBuffer.join("");
        }

        this.getSubtypeSelect = function(){
            if (this.typeAbbr == 'ain'){
                return ""
            }
            var htmlBuffer = [];
            htmlBuffer.push("<select>");

            for (var key in this.subtypeMap){
                // check hasOwnProperty
                if (!this.subtypeMap.hasOwnProperty(key)){
                    continue;
                }

                var curSubtype = this.subtypeMap[key];

                htmlBuffer.push("<option ")
                if (curSubtype == this.subtype){
                    htmlBuffer.push("selected='selected'");
                }
                htmlBuffer.push(">");
                htmlBuffer.push(curSubtype);
                htmlBuffer.push("</option>");
            }
            htmlBuffer.push("</select>");
            return htmlBuffer.join("");
        }

        this.getIOStatusHTML = function(){
            if (this.typeAbbr == "ain"){
                return "<input type='checkbox' />"
            }
            return "<span class='label label-warning'>Stats</span>"
        }
    }

}


var ioapp = new IOIOApp;
