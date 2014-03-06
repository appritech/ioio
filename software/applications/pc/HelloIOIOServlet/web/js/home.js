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
            self.attachHandlers();
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
        var xmlDoc = document.implementation.createDocument(null, "ioio", null);

        $('#ioio-table-body tr').each(function(index){
            var row = $(this),
                pin = xmlDoc.createElement("pin"),
                numCell = row.find('td').eq(0),
                typeSelect = row.find('td').eq(1).find('select'),
                subtypeSelect = row.find('td').eq(2).find('select');

            pin.setAttribute('num', numCell.text());
            pin.setAttribute('type', typeSelect.val());

            if (subtypeSelect.length > 0){
                pin.setAttribute('subtype', subtypeSelect.val());
            }
            xmlDoc.documentElement.appendChild(pin);

        });

        $.ajax({ 
            url: "/api/config",
            type: "POST",
            contentType: "text/xml",
            data: xmlDoc,
            processData: false,
            error: function(req, status, error) { 
                console.log("Error");
                console.log(error);
            },
            success: function() {
                console.log("XML Saved");
            }
        });
    },

    attachHandlers: function(){
        var self = this;

        $( "tbody" ).on( "change", ".type-select", function(){
            var subtypeMap = null,
                subtypeCell = $(this).parent().next('td');

            if ($(this).val() == 'din'){
                subtypeMap = self.dinSubtypes;                
            } else if ($(this).val() == 'dout'){
                subtypeMap = self.doutSubtypes;
            }

            if (subtypeMap){
                var subtypeSelect = subtypeCell.find('select');
                if (subtypeSelect.length > 0){
                    subtypeSelect.html("");
                }else {
                    subtypeSelect = $('<select class="subtype-select"></select>');
                }

                for (var key in subtypeMap){
                    // check hasOwnProperty
                    if (!subtypeMap.hasOwnProperty(key)){
                        continue;
                    }
                    var option = $("<option></option>");
                    var curSubtype = 
                    option.val(key);
                    option.text(subtypeMap[key]);
                    subtypeSelect.append(option);
                }
                subtypeCell.html(subtypeSelect);

            }else {
                subtypeCell.html('');
            }
        });

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
            htmlBuffer.push("<select class='type-select'>");
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
                htmlBuffer.push(" value='");
                htmlBuffer.push(key)
                htmlBuffer.push("'>");
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
            htmlBuffer.push("<select class='subtype-select'>");

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
                htmlBuffer.push(" value='");
                htmlBuffer.push(key)
                htmlBuffer.push("'>");
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

function xmlToString(doc){
    if (window.ActiveXObject){ 
        return xmlString = doc.xml; 
    } else {
        var oSerializer = new XMLSerializer(); 
        return oSerializer.serializeToString(doc);
    }
}
