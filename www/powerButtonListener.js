var cordova = require('cordova'),
    exec = require('cordova/exec');

function handlers() {
    return powerButtonListener.channels.buttonsListener.numHandlers;
}

var PowerButtonListener = function () {
    this.channels = { buttonsListener: cordova.addWindowEventHandler('powerbuttonlistener') };
    for (var key in this.channels)
        this.channels[key].onHasSubscribersChange = PowerButtonListener.onHasSubscribersChange;
};

PowerButtonListener.onHasSubscribersChange = function () {
    if (this.numHandlers === 1 && handlers() === 1)
        exec(powerButtonListener.powerButtonListener, powerButtonListener.errorListener, "PowerButtonListener", "start", []);
    else if (handlers() === 0)
        exec(null, null, "PowerButtonListener", "stop", []);
};

PowerButtonListener.prototype.powerButtonListener = function (info) {
    if (info) {
        cordova.fireWindowEvent("powerbuttonlistener", info);
    }
};

PowerButtonListener.prototype.errorListener = function (e) {
    console.log("Error initializing PowerButtonListener: " + e);
};

var powerButtonListener = new PowerButtonListener();
module.exports = powerButtonListener;