
// Constructor
var PinRow = function (guid, node, app) {
    this.guid = guid; 
    this.node = node;
    this.app = app;

    // https://developer.mozilla.org/en-US/docs/Web/API/element
    this.number = this.node.getAttribute("num");
    this.name = this.node.getAttribute("name") == null ? "" : this.node.getAttribute("name");
    this.type = this.node.getAttribute("type");
    this.typePretty = this.app.utility.pinTypes[this.type];
    this.subtype = (this.node.hasAttribute("subtype"))? this.node.getAttribute("subtype"): null;
    this.subtypePretty = null;
    this.subtypeMap = null;

    this.calibrationData = null;


    if (this.type == "dout"){
        this.subtypeMap = this.app.utility.doutSubtypes;
    } else if (this.type == "din"){
        this.subtypeMap = this.app.utility.dinSubtypes;
    }

    if (this.subtypeMap){
        this.subtypePretty = this.subtypeMap[this.subtype];
    }

    
    this.setDefaultCalibration();
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

    destroyPopover: function(){
        $("#" + this.guid + "-popover").popover('destroy');
    }, 

    createPopover: function(){
        $("#" + this.guid + "-popover").popover({html: true, content: this.getCalibrationPopoverContent()});
    },

    hidePopover: function(){
        $("#" + this.guid + "-popover").popover("hide");
    },
    
    setDefaultCalibration: function(){
        if (this.type == 'ain'){
            // Parse Node for calibration Data and set Default for missing values
            this.calibrationData = {
                MinInputValue: (this.node.hasAttribute("MinInputValue"))? parseFloat(this.node.getAttribute("MinInputValue")) : 0.0,
                MaxInputValue: (this.node.hasAttribute("MaxInputValue"))? parseFloat(this.node.getAttribute("MaxInputValue")) : 3.28676,
                CenterInputValue: (this.node.hasAttribute("CenterInputValue"))? parseFloat(this.node.getAttribute("CenterInputValue")) : 1.6976545,
                MinOutputValue: (this.node.hasAttribute("MinOutputValue"))? parseFloat(this.node.getAttribute("MinOutputValue")) : -100.0,
                MaxOutputValue: (this.node.hasAttribute("MaxOutputValue"))? parseFloat(this.node.getAttribute("MaxOutputValue")) : 100.0,
                CenterOutputValue: (this.node.hasAttribute("CenterOutputValue"))? parseFloat(this.node.getAttribute("CenterOutputValue")) : 0.0
            }
        }else {
            this.calibrationData = {
                TrueValue: (this.node.hasAttribute("TrueValue"))? parseFloat(this.node.getAttribute("TrueValue")) : 1.0,
                FalseValue: (this.node.hasAttribute("FalseValue"))? parseFloat(this.node.getAttribute("FalseValue")) : 0.0,
            }
        }
    },

    updateType: function(newTypeAbbr){
        // Remove calibration popover
        this.destroyPopover();

        this.type = newTypeAbbr;
        this.typePretty = this.app.utility.pinTypes[this.type];

        // Update Subtype
        if (newTypeAbbr == 'din'){ // din
            this.subtypeMap = this.app.utility.dinSubtypes;                
        } else if (newTypeAbbr == 'dout'){ // dout
            this.subtypeMap = this.app.utility.doutSubtypes;
        } else { // ain
            this.subtypeMap = null;
            this.subtype = null;
            this.subtypePretty = null;
        }

        // Update Default calibration
        this.setDefaultCalibration();

        // Update HTML Cells
        this.subtypeCell.html(this.getSubtypeSelect());
        this.statusCell.html(this.getIOStatusHTML());
        this.calibrationCell.html(this.getCalibrationButton());

        // Attach Calibration popover
        this.createPopover();
    },

    updateCalibrationData: function(){
        var newData = null;
        if (this.type == 'ain'){
            newData = {
                MinInputValue: parseFloat($("#" + this.guid + "-MinInputValue").val()),
                MaxInputValue: parseFloat($("#" + this.guid + "-MaxInputValue").val()),
                CenterInputValue: parseFloat($("#" + this.guid + "-CenterInputValue").val()),
                MinOutputValue: parseFloat($("#" + this.guid + "-MinOutputValue").val()),
                MaxOutputValue: parseFloat($("#" + this.guid + "-MaxOutputValue").val()),
                CenterOutputValue: parseFloat($("#" + this.guid + "-CenterOutputValue").val()),
            };
        }else {
            newData = {
                TrueValue: parseFloat($("#" + this.guid + "-TrueValue").val()),
                FalseValue: parseFloat($("#" + this.guid + "-FalseValue").val()),
            };
        }
        this.calibrationData = newData;
        this.destroyPopover();
        this.createPopover();
    },

    getCalibrationButton: function(){
        return "<button id='" + this.guid + "-popover' class='btn btn-primary " + this.type + "-popover'>Calibrate</button>";
    },

    getCalibrationPopoverContent: function(){
        var htmlBuffer = [];
        if (this.type == 'ain'){
            for (var key in this.calibrationData){
                if (!this.calibrationData.hasOwnProperty(key)){  // check hasOwnProperty
                    continue;
                }
                htmlBuffer.push("<label>" + key + " Value: </label>");
                htmlBuffer.push("<input type='text' ");
                htmlBuffer.push("id='" + this.guid + "-" + key);
                htmlBuffer.push("' value='" + this.calibrationData[key]);
                htmlBuffer.push("' />");
                // htmlBuffer.push("<input type='text' name='" + key + "' value='" + value + "' />");
            }
            htmlBuffer.push("<br/><button class='btn btn-primary calibration-save' data-pin-guid='" + this.guid + "'>Save</button>");
            return htmlBuffer.join("");
        }else {
            htmlBuffer.push("<label for='on'>On Value: </label><br/>");
            htmlBuffer.push("<input type='text' name='on'")
            htmlBuffer.push(" id='" + this.guid + "-TrueValue'");
            htmlBuffer.push(" value='" + this.calibrationData.TrueValue + "' /><br/>");

            htmlBuffer.push("<label for='off'>Off Value: </label><br/>");
            htmlBuffer.push("<input type='text' name='off'");
            htmlBuffer.push(" id='" + this.guid + "-FalseValue'");
            htmlBuffer.push(" value='" + this.calibrationData.FalseValue + "' /><br/>");

            htmlBuffer.push("<button class='btn btn-primary calibration-save' data-pin-guid='" + this.guid + "'>Save</button>");
            return htmlBuffer.join("");
        }
    },

    setIOstatus: function(status){
        var statusNode = this.statusCell.find(".io-status");
        statusNode.removeClass("label-warning");

        if (this.type == 'ain'){
            statusNode.addClass("label-success");
            statusNode.html(status);
        }else if (this.type == 'din'){
            statusNode.html(status);
            if (this.calibrationData != null){
                if (this.calibrationData["TrueValue"] == status){
                    statusNode.addClass("label-success");
                }else {
                    statusNode.addClass("label-danger");
                }
            }else{
                statusNode.addClass("label-warning");
            }
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

