
// Constructor
var PinRow = function (app, node) {

    // https://developer.mozilla.org/en-US/docs/Web/API/element
    this.node = node;
    this.app = app;

    this.number = this.node.getAttribute("num");
    this.name = this.node.getAttribute("name") == null ? "" : this.node.getAttribute("name");
    this.type = this.node.getAttribute("type");
    this.typePretty = this.app.utility.pinTypes[this.type];
    this.subtype = (this.node.hasAttribute("subtype"))? this.node.getAttribute("subtype"): null;
    this.subtypePretty = null;
    this.subtypeMap = null;
    this.guid = null; 

    this.calibrationData = null;

    if (this.type == "dout"){
        this.subtypeMap = this.app.utility.doutSubtypes;
    } else if (this.type == "din"){
        this.subtypeMap = this.app.utility.dinSubtypes;
    }

    if (this.subtypeMap){
        this.subtypePretty = this.subtypeMap[this.subtype];
    }

}

PinRow.prototype = {
    
    /* Set after creation with following cells
     * 0 = Pin Number
     * 1 = Pin Name
     * 2 = Pin Type
     * 3 = Pin SubType
     * 4 = IO Status
     */
    setRowElement: function(rowElement){
        this.rowElement = rowElement;
        var cells = rowElement.find('td');
        this.numberCell = cells.eq(0);
        this.nameCell = cells.eq(1);
        this.typeCell = cells.eq(2);
        this.subtypeCell = cells.eq(3);
        this.calibrationCell = cells.eq(4);
        this.statusCell = cells.eq(5);
    },

    getName: function(){
        this.name = this.nameCell.find('input').val();
        return this.name
    },

    getType: function(){
        var typeSelectValue = this.typeCell.find('select').val();
        if (this.type != typeSelectValue){
            this.updateType(typeSelectValue);
        }
        return this.type;
    },

    getSubtype: function(){
        this.subtype = this.subtypeCell.find('select').val();
        return this.subtype;
    },

    updateType: function(newTypeAbbr){
        // Remove calibration popover
        if (this.type != 'dout'){
            $("#" + this.guid + "-popover").popover('destroy');
        }

        this.type = newTypeAbbr;
        this.typePretty = this.app.utility.pinTypes[this.type];

        if (newTypeAbbr == 'din'){ // din
            this.subtypeMap = this.app.utility.dinSubtypes;                
        } else if (newTypeAbbr == 'dout'){ // dout
            this.subtypeMap = this.app.utility.doutSubtypes;
        } else { // ain
            this.subtypeMap = null;
            this.subtype = null;
            this.subtypePretty = null;
        }

        this.subtypeCell.html(this.getSubtypeSelect());
        this.statusCell.html(this.getIOStatusHTML());
        this.calibrationCell.html(this.getCalibrationButton());


        // Attach Calibration popover
        if (this.type != 'dout'){
            $("#" + this.guid + "-popover").popover({html: true, content: this.getCalibrationPopoverContent()});
        }
    },

    updateCalibrationData: function(data){
        // Hopefully this will work
        this.calibrationData = data;
    },

    getCalibrationButton: function(){
        if (this.type == "dout"){
            return "<span class='label label-warning'>N/A</span>";
        }
        return "<button id='" + this.guid + "-popover' class='btn btn-primary " + this.type + "-popover'>Calibrate</button>";
    },

    getCalibrationPopoverContent: function(){
        var htmlBuffer = [];
        if (this.type == 'ain'){
            for (var key in this.app.utility.defaultAnalogValues){
                // check hasOwnProperty
                if (!this.app.utility.defaultAnalogValues.hasOwnProperty(key)){
                    continue;
                }
                var value = this.app.utility.defaultAnalogValues[key]
                htmlBuffer.push("<input type='text' name='" + key + "' value='" + value + "' />");
            }
            htmlBuffer.push("<button class='btn btn-primary calibration-save' data-pin-guid='" + this.guid + "'>Save</button>");
            return htmlBuffer.join("");
        }else if (this.type == 'din'){
            htmlBuffer.push("<input type='text' name='off' value='0.0' />");
            htmlBuffer.push("<input type='text' name='on' value='1.0' />");
            htmlBuffer.push("<button class='btn btn-primary calibration-save' data-pin-guid='" + this.guid + "'>Save</button>");
            return htmlBuffer.join("");
        }else {
            return '';
        }
    },

    setIOstatus: function(status){
        var statusNode = this.statusCell.find(".io-status");
        statusNode.removeClass("label-warning");
        if(status == "1.0"){
            statusNode.addClass("label-success");
            statusNode.html("On");
        }else{
            statusNode.addClass("label-danger");
            statusNode.html("Off");
        }

    },

    getIOStatusHTML: function(){
        if (this.type == "dout"){
            return "<label>Trigger: </label><input type='checkbox' name='trigger-pin' data-pin=" + this.number + " />"
        }
        return "<span class='label label-warning io-status " + this.type +  "-stat' data-pin='" + this.number + "'>Unknown</span>"
    },

    getTypeSelect: function(){
        var htmlBuffer = [];
        htmlBuffer.push("<select class='form-control type-select'>");
        for (var key in this.app.utility.pinTypes){
            // check hasOwnProperty
            if (!this.app.utility.pinTypes.hasOwnProperty(key)){
                continue;
            }

            var curType = this.app.utility.pinTypes[key];
            htmlBuffer.push("<option ")
            if (curType == this.typePretty){
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
    },

    getSubtypeSelect: function(){
        if (this.type == 'ain'){
            return ""
        }
        var htmlBuffer = [];
        htmlBuffer.push("<select class='form-control subtype-select'>");

        for (var key in this.subtypeMap){
            // check hasOwnProperty
            if (!this.subtypeMap.hasOwnProperty(key)){
                continue;
            }

            var curSubtype = this.subtypeMap[key];

            htmlBuffer.push("<option ")
            if (curSubtype == this.subtypePretty){
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
    },

    getNameInput: function(){
        return "<input type='text' class='form-control' name='pin-name' value='" + this.name + "' placeholder='Pin Name...'/>"
    }

}

