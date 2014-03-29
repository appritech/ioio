
// Constructor
var PinRow = function (guid, node, app) {
    this.guid = guid; 
    this.node = node;
    this.app = app;
    this.parseNodeAttributes();
    this.parseCalibrationData();
}

PinRow.prototype = {

    /*  Parse XML nodes
     *
     *  https://developer.mozilla.org/en-US/docs/Web/API/element
     */
    parseNodeAttributes: function(){
        try {
            this.number = this.node.getAttribute("num");
            this.name = this.node.getAttribute("name") == null ? "" : this.node.getAttribute("name");
            this.type = this.node.getAttribute("type");
            this.typePretty = this.app.utility.defaultData.Types[this.type];
            this.subtype = (this.node.hasAttribute("subtype"))? this.node.getAttribute("subtype"): null;
            this.calibrationData = {}

            try {
                if (this.subtype != null){
                    this.subtypePretty = this.app.utility.defaultData.Types[this.type].SubTypes[this.subtype];
                }
            } catch (e){
                this.subtypePretty = "";
            }
            
        } catch (exc){
            // throw new this.app.utility.XmlException(exc.message);
            throw { message: exc.message }
        }

    },

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
        try {
            this.name = this.nameCell.find('input').val();
            return this.name
        } catch (exc){
            return "";
        }
    },

    getType: function(){
        try {
            var typeSelectValue = this.typeCell.find('select').val();
            if (this.type != typeSelectValue){
                this.updateType(typeSelectValue);
            }
            return this.type;
        } catch (exc){
            return "";
        }
    },

    getSubtype: function(){
        try {
            this.subtype = this.subtypeCell.find('select').val();
            return this.subtype;
        } catch (exc){
            return "";
        }
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
    
    parseCalibrationData: function(){
        var defaultCalibration = this.app.utility.defaultData.Types[this.type].CalibrationData;
        var xmlValue = null;
        var xmlMapping = this.app.utility.defaultData.CalibrationXMLmapping;

        for (var key in defaultCalibration){
            if (defaultCalibration.hasOwnProperty(key)){
                xmlValue = xmlMapping[key];
                this.calibrationData[key] = (this.node.hasAttribute(xmlValue))? parseFloat(this.node.getAttribute(xmlValue)) : defaultCalibration[key];
            }
        }
    },

    updateType: function(newType){
        // Remove calibration popover
        this.destroyPopover();

        this.type = newType;
        this.typePretty = this.app.utility.defaultData.TypesPretty[this.type];

        // Update Subtype
        if (newType == 'ain'){
            this.subtype = null;
            this.subtypePretty = null;
        } 

        // Set Default calibration        
        var defaultCalibration = this.app.utility.defaultData.Types[this.type].CalibrationData;
        for (var key in defaultCalibration){
            if (defaultCalibration.hasOwnProperty(key)){
                this.calibrationData[key] = defaultCalibration[key];
            }
        }

        // Update HTML Cells
        this.subtypeCell.html(this.getSubtypeSelect());
        this.statusCell.html(this.getIOStatusHTML());
        this.calibrationCell.html(this.getCalibrationButton());

        // Attach Calibration popover
        this.createPopover();
    },

    updateCalibrationData: function(){
        var defaultCalibration = this.app.utility.defaultData.Types[this.type].CalibrationData;

        for (var key in defaultCalibration){
            this.calibrationData[key] = parseFloat($("#" + this.guid + "-" + key).val());
        }

        this.destroyPopover();
        this.createPopover();
    },

    getCalibrationButton: function(){
        return "<button id='" + this.guid + "-popover' class='btn btn-primary " + this.type + "-popover'>Calibrate</button>";
    },

    getCalibrationPopoverContent: function(){
        var htmlBuffer = [];
        for (var key in this.calibrationData){
            if (this.calibrationData.hasOwnProperty(key)){  // check hasOwnProperty
                    htmlBuffer.push("<label>" + key + " Value: </label>");
                    htmlBuffer.push("<input type='text' ");
                    htmlBuffer.push("id='" + this.guid + "-" + key);
                    htmlBuffer.push("' value='" + this.calibrationData[key]);
                    htmlBuffer.push("' />");
            }
        }
        htmlBuffer.push("<br/><button class='btn btn-success calibration-save' data-pin-guid='" + this.guid + "'>Save</button>");
        return htmlBuffer.join("");
    },

    setIOstatus: function(status){
        var statusNode = this.statusCell.find(".io-status");
        if (statusNode.length == 0){
            return;
        }
        statusNode.removeClass("label-warning");

        if (this.type == 'ain'){
            statusNode.addClass("label-success");
            statusNode.html(status);
        }else if (this.type == 'din'){
            statusNode.html(status);
            if (this.calibrationData != null){
                if (this.calibrationData.True == status){
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
        if (this.type == null){
            return "";
        }
        var typesPretty = this.app.utility.defaultData.TypesPretty;
        return this.app.utility.selectBuilder("type-select", typesPretty, this.type);
    },

    getSubtypeSelect: function(){
        if (this.type == 'ain' || this.subtype == null){
            return "";
        }
        var subTypesPretty = this.app.utility.defaultData.Types[this.type].SubTypesPretty;
        return this.app.utility.selectBuilder("subtype-select", subTypesPretty, this.subtype);
    },

    getNameInput: function(){
        return "<input type='text' class='form-control' name='pin-name' value='" + this.name + "' placeholder='Pin Name...'/>"
    }

}

