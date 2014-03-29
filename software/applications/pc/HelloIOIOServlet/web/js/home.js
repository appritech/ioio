console.log("JavaScript Running!");



var IOIOApp = function () {
    // Constructor
    this.init();
}

IOIOApp.prototype = {

    init: function () {
        console.log("App Running!");

        this.dirtyConfig = true;
        this.utility = new Utility();
        this.getConfigXML();
        this.poller = new Poller(this);
        this.pinRowStore = {};
    },

    getConfigXML: function() {
        var self = this; 
        var xmlConfig = null
        $.get( "/api/config?resource=config.xml", function( xml ) {
            self.configXML = $( xml );
            self.setupTable();
            self.attachHandlers();
            self.poller.init();
            self.dirtyConfig = false;
        });

    },

    setupTable: function(){
        var self = this;
        var tableBody = $("#ioio-table-body");

        // Loop through XML config Doc and create the table for 
        this.configXML.find("pin").each(function(index, pin){
            var guid = self.utility.generateGUID();
            var pinObject = new PinRow(guid, pin, self);
            var strBuffer = [];

            // Start Row
            strBuffer.push("<tr data-guid='" + guid + "'>");
            
            // Pin Number Cell
            strBuffer.push("<td>");
            strBuffer.push(pinObject.number);
            strBuffer.push("</td>");
            
            // Pin Name cell
            strBuffer.push("<td>");
            strBuffer.push(pinObject.getNameInput());
            strBuffer.push("</td>");

            // Pin Type Cell
            strBuffer.push("<td>");
            strBuffer.push(pinObject.getTypeSelect());
            strBuffer.push("</td>");

            // Pin Subtype Cell
            strBuffer.push("<td>");
            strBuffer.push(pinObject.getSubtypeSelect());
            strBuffer.push("</td>");

            // Pin Calibration Cell
            strBuffer.push("<td>");
            strBuffer.push(pinObject.getCalibrationButton());
            strBuffer.push("</td>");

            
            // Pin I/O Status cell
            strBuffer.push("<td class='io-cell'>");
            strBuffer.push(pinObject.getIOStatusHTML());
            strBuffer.push("</td>");

            // End Row
            strBuffer.push("</tr>");
            
            var row = $(strBuffer.join(""));

            self.pinRowStore[guid] = pinObject;
            pinObject.setRowElement(row);
            tableBody.append(row);
            pinObject.createPopover();
        });
        
        
    },

    attachHandlers: function(){
        var self = this;
        $('tbody').on("change", ".type-select", { "app": self }, self.updateDynamicInputsEvent);
        $('tbody').on("change", "input[name='trigger-pin']", {"app": self}, self.triggerPinEvent);
        $('tbody').on("click", "button.calibration-save", {"app": self}, self.collectCalibrationEvent);
        $('.save-config').on("click", function(){ self.saveConfigEvent() });
        $("#notifications-box").on("notify", self.notificationEvent);

    },

    notificationEvent: function(event, message, stateClass){
        var notificationBox = $('#notifications-box');
        var div = $('<div class="notify"></div>');
        
        if (stateClass){
            div.addClass(stateClass);
        } else {
            div.addClass("bg-warning");
        }

        div.append(message);
        notificationBox.append(div);
        notificationBox.fadeIn();
        notificationBox.powerTimer({
            name: 'push-notification',
            delay: 5000,
            func: function() {
                var notifyBox = $("#notifications-box");
                notifyBox.html("");
                notifyBox.fadeOut();
            },
        });

    },

    collectCalibrationEvent: function(event){
        var guid = $(this).data('pin-guid'),
            pinObject = event.data.app.pinRowStore[guid];

        // Collect inputs in the parent and send to pinObject
        pinObject.updateCalibrationData();
    },

    updateDynamicInputsEvent: function(event){
        event.data.app.dirtyConfig = true;
        $("#dirty-config").removeClass("hidden");

        var guid = $(this).parent().parent().data('guid'),
            newTypeAbbr = $(this).val();

        event.data.app.pinRowStore[guid].updateType(newTypeAbbr);
    },

    triggerPinEvent: function(event){
        var element = $(this);
            guid = element.parents('tr').data('guid'),
            pinObject = event.data.app.pinRowStore[guid],
            state = (this.checked)? pinObject.calibrationData.True : pinObject.calibrationData.False;

        // Send state from calibration data
        $.post("/api/trigger", {"pin": pinObject.number, "state": state }, function( data){
            console.log(data);
        });
        // For Bootstrap Switch
        // var $element = $(data.el);
        // $.post( "/api/trigger", {"pin": $element.data("pin"), "state": data.value },function( data ) {
        //     console.log(data);
        // });
    },

    saveConfigEvent: function(){
        var self = this;
        var xmlDoc = document.implementation.createDocument(null, "ioio", null);

        // Loop through config table rows, generate XML doc, POST to server
        $('#ioio-table-body tr').each(function(index){
            var row = $(this),
                pin = xmlDoc.createElement("pin"),
                guid = row.data('guid'),
                pinObject = self.pinRowStore[guid];

            pin.setAttribute('name', pinObject.getName());
            pin.setAttribute('num', pinObject.number);
            pin.setAttribute('type', pinObject.getType());

            // Set Subtype
            if (pinObject.getType() != 'ain'){
                pin.setAttribute('subtype', pinObject.getSubtype());
            }

            // Set Calibration Data
            for (var key in pinObject.calibrationData){
                if (pinObject.calibrationData.hasOwnProperty(key)){  // check hasOwnProperty
                    pin.setAttribute(key + "Value", pinObject.calibrationData[key]);                    
                }
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
                $("#notifications-box").trigger("notify", ["Error Saving XML!", "bg-error"])
                console.log(error);
                console.log(status);
                console.log(req);
            },
            success: function() {
                $("#dirty-config").addClass("hidden");
                $("#notifications-box").trigger("notify", ["XML Saved!", "bg-success"])
                self.dirtyConfig = false;
                self.poller.restart();
            }
        });
    },


    /* 
     * Bootstrap Switch from:
     * http://www.bootstrap-switch.org/#getting-started
     *
     * DEPRECATED because it gets weird when it Destroys switches
     */
    attachSwitches: function(){
        $("input[name='trigger-pin']").bootstrapSwitch().on('switch-change', this.triggerPinEvent);
    }
    
}

var ioapp = new IOIOApp;



