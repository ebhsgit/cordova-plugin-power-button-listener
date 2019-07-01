Power-Button-Listener
=======================

Cordova plugin which notifies when the user presses the power button on the device. It achieves this by inferring from Android screen on and off event.

When the power button is inferred, the plugin fires an `window` event:

* powerButtonListener

## Installation

``` bash
cordova plugin add https://github.com/8bhsolutions/cordova-plugin-power-button-listener.git
```

## Remove

``` bash
cordova plugin remove 8bhsolutions-cordova-plugin-power-button-listener
```

## Returned object

- __keyCode__: KeyEvent.Power or 'unknown'
- __keyAction__: Intent.action
- __platform__: 'android'

Applications have to use `window.addEventListener`to attach this event listener once the `deviceready`event fires.

### Supported Platforms

- Android

### Example
``` js
	window.addEventListener("powerbuttonlistener", (info) => { this.onEvent(info); }, false);
	
	function onEvent(info){
		console.log("Action: " + info.keyAction);
	}
```
