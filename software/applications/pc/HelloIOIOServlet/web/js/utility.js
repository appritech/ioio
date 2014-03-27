var Utility = function(){
    // Constructor
}

Utility.prototype = {

    defaultData: {
        Types: {
            ain: {
                PrettyPrint: "Analog In",
                SubTypesPretty: {},
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
            dout: {
                PrettyPrint: "Digital Out",
                SubTypesPretty: {
                    "OD": "Open-Drain",
                    "NL": "Normal"
                },
                CalibrationData: {
                    "True": 1,
                    "False": 0
                }
            },
            din: {
                PrettyPrint: "Digital In",
                SubTypesPretty: {
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
        TypesPretty: {
            ain: "Analog In",
            dout: "Digital Out",
            din: "Digital In"
        }
    },

    XmlException: function(message) {
        this.message = message;
        this.name = "XmlException";
    },

    selectBuilder: function(cssClass, lookupMap, defaultValue){
        if (cssClass == null){
            cssClass = "";
        }
        if (lookupMap == null){
            return "";
        }

        var htmlBuffer = [];
        htmlBuffer.push("<select class='form-control " + cssClass + "'>");

        for (var key in lookupMap){
            if (lookupMap.hasOwnProperty(key)){
                var optionHtml = lookupMap[key];

                htmlBuffer.push("<option ")
                if (key == defaultValue){
                    htmlBuffer.push("selected='selected'");
                }
                htmlBuffer.push(" value='");
                htmlBuffer.push(key)
                htmlBuffer.push("'>");
                htmlBuffer.push(optionHtml);
                htmlBuffer.push("</option>");                
            }
        }
        htmlBuffer.push("</select>");
        return htmlBuffer.join("");
    },

    isEmpty: function(obj) {
        var hasOwnProperty = Object.prototype.hasOwnProperty;

        // null and undefined are "empty"
        if (obj == null) return true;

        // Assume if it has a length property with a non-zero value
        // that that property is correct.
        if (obj.length > 0)    return false;
        if (obj.length === 0)  return true;

        // Otherwise, does it have any properties of its own?
        // Note that this doesn't handle
        // toString and valueOf enumeration bugs in IE < 9
        for (var key in obj) {
            if (hasOwnProperty.call(obj, key)) return false;
        }

        return true;
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
