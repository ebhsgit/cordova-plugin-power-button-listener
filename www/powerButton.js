var cordova = require('cordova'),
    exec = require('cordova/exec');

function handlers() {
    return powerButton.channels.buttonsListener.numHandlers;
}

var PowerButton = function () {
    this.channels = { buttonsListener: cordova.addWindowEventHandler('powerButtonListener') };
    for (var key in this.channels)
        this.channels[key].onHasSubscribersChange = PowerButton.onHasSubscribersChange;
};

PowerButton.onHasSubscribersChange = function () {
    if (this.numHandlers === 1 && handlers() === 1)
        exec(powerButton.powerButtonListener, powerButton.errorListener, "PowerButton", "start", []);
    else if (handlers() === 0)
        exec(null, null, "PowerButton", "stop", []);
};

PowerButton.prototype.powerButtonListener = function (info) {
    if (info) {
        cordova.fireWindowEvent("powerButtonListener", info);
    }
};

PowerButton.prototype.errorListener = function (e) {
    console.log("Error initializing PowerButton: " + e);
};

var powerButton = new PowerButton();
module.exports = powerButton;