

// Constructor
var Poller = function (app) {
    this.app = app;
    this.failed = 0;       // number of failed requests
    this.interval = 1000;  // starting interval - 1 second
    this.kill = false;     // Stop Polling
}

Poller.prototype  = {
    
    // kicks off the setTimeout
    init:  function(){
        setTimeout( $.proxy(this.getData, this), this.interval );
    },  // init();

    restart: function(){
        this.kill = true;
        this.kill = false;
        this.init();
    },  // restart();

    // get AJAX data + respond to it
    getData: function(){
        var self = this;
        var pinObjectLookup = {};
        var pinArray = [];
        $('.din-stat, .ain-stat').each(function(index){
            var element = $(this);
            guid = element.parents("tr").data('guid'),
            pinObj = self.app.pinRowStore[guid];

            pinObjectLookup[pinObj.number] = pinObj;
            pinArray.push(pinObj.number);
        });

        // Shortcircuit if no Input pins configured
        if (pinArray.length == 0){
            this.init();
        }

        $.ajax({ 
            url: "/api/status",
            type: "GET",
            contentType: "text/plain",
            data: $.param({pins: pinArray}),
            processData: false,
            dataType: "xml",
            error: function(req, status, error) { 
                $("#notifications-box").trigger("notify", ["Error Polling server!", "bg-error"])
            },
            success: function(data) {
                $(data).find('pin').each(function(index, pin){
                    var pinNumber = pin.getAttribute("num");
                    var pinStatus = pin.getAttribute("status");

                    pinObjectLookup[pinNumber].setIOstatus(pinStatus);
                });
            }
        });
        
        // If no Kill signal received, continue polling
        if (!this.kill && !this.app.dirtyConfig){
            this.init();
        }

    },  // getData();
}

