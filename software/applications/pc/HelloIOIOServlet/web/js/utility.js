var Utility = function(){
    // Constructor
}

Utility.prototype = {
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

    defaultAnalogValues: {
        "MinInput": 0,
        "MaxInput": 3.28676,
        "CenterInput": 1.6976545,
        "MinOutput": -100,
        "MaxOutput": 100,
        "CenterOutput": 0,
        "Deadpan": 0
    },

    defaultData: {
        Types: {
            "ain": {
                PrettyPrint: "Analog In",
                SubTypes: {},
                CalibrationData: {
                    "MinInput": 0,
                    "MaxInput": 3.28676,
                    "CenterInput": 1.6976545,
                    "MinOutput": -100,
                    "MaxOutput": 100,
                    "CenterOutput": 0,
                    "Deadpan": 0
                }
            },
            "dout": {
                PrettyPrint: "Digital Out",
                SubTypes: {
                    "OD": "Open-Drain",
                    "NL": "Normal"
                },
                CalibrationData: {
                    "True": 1,
                    "False": 0
                }
            },
            "din": {
                PrettyPrint: "Digital In",
                SubTypes: {
                    "FL": "Float",
                    "PD": "Pull-Down",
                    "PU": "Pull-Up"
                },
                CalibrationData: {
                    "True": 1,
                    "False": 0
                }
            }
        },
    },

    xmlToString: function(doc){
        if (window.ActiveXObject){ 
            return xmlString = doc.xml; 
        } else {
            var oSerializer = new XMLSerializer(); 
            return oSerializer.serializeToString(doc);
        }
    },

    s4: function() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    },

    generateGUID: function() {
        return this.s4() + this.s4() ;
    }
}
