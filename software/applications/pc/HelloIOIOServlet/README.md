#IOIO Server

Desperately in need of a slick name :)

Use an embedded Tomcat instance to serve a Web Interface that configures the 
pins of an attached IOIO device.  

- Set pin type to Analog In/Out or Digital In/Out: ain, aout, din, dout.
- Set subtype: FL, PU, PD, OD
- Invert a pin: True, False
- See status of pins

##API Documentation

The following endpoints are (will soon be) available from the server.

###/api/config/

- GET: Return configuration data of pin(s).
    + pin [optional]: pin configuration data to return.  Returns all if not 
    specified.
- POST: Update config file of pin(s).
    + pin [optional]: pin configuration to update. Update all if not specified.
      
###/api/status/

- GET: Return IO status of pin(s).
    + pin [optional]: pin status to check.  Returns all if not specified.

###/api/trigger/

- POST: Send signal to pin.
    + pin: pin to trigger.