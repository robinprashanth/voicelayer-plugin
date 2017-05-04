# cordova-voicelayer-plugin

## Description

Cross-platform voicelayer for Cordova/Phonegap
Plugin can be used for accessing all the API's or features available in https://developers.voicelayer.io/docs/main/android/index.html.
Plugin dosen't support iOS currently. 
Please Note: Readme File may not be updated with all the available API's.Developers are required to either refer www/Voicelayer.js or src/android/VoiceLayerIo.java File for implemented API's

## Installing the plugin
```
cordova plugin add https://bitbucket.org/basitmunir/voicelayersdk-phonegap
```

## Registering plugin for Adobe® PhoneGap™ Build

This plugin should work with Adobe® PhoneGap™ Build without any modification.
To register plugin add following line into your config.xml:

```
<plugin name="cordova-voicelayer-plugin" spec="https://bitbucket.org/basitmunir/voicelayersdk-phonegap"/>
```

## Quick Example

```javascript
document.addEventListener('deviceready', onDeviceReady, false);

function onDeviceReady () {

    /**
    * Make this as a first call to plugin to initializevoicelayer.
    */
   var voicelayerintialize = {
          appName: 'myrotestapp',
          uid: 'XX----------XX',
          secretKey: 'XX-----------XX',
        };
        voiceLayer.initialize((success) => {
         
        }, (err) => {
         
        }, voicelayerintialize);

}
```

## API's currently Supported
### initialize(success, fail, options)

| Parameter | Type          | Platform | Description                                                                     |
|-----------|---------------|----------|---------------------------------------------------------------------------------|
| `success` | `Function`    | all      | Callback to be executed every time a initilization is successfull  |
| `fail`    | `Function`    | all      | Callback to be executed every time a  error occurs during initialization.|
| `options` | `JSON Object` | all      | Configure options|

Configure options:

| Parameter                 | Type              | Platform     | Description                                                                                                                                                                                                                                                                                                                                        |
|---------------------------|-------------------|--------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `appName`         | `String`          | all          | AppName created in voicelayer account |   
| `uid`         | `String`          | all          | uid generated when a app is created in voicelayer account |  
| `secretKey`         | `String`          | all          | secretKey generated when a app is created in voicelayer account |      


### login(success, fail,username,password)
Platform: Android

### register(success, fail,username,email,password)
Platform: Android

### logout(success, fail)
Platform: Android

### getInvitations(success, fail,page, pageSize)
Platform: Android

Note: page, pageSize is required 

### joinChannel(success, failure, channelObj)
Platform: Android

Need to pass selected Invited Channel Object as string that was retrieved in getInvitations(success, fail,page, pageSize)

### declineChannelRequest(success, failure, channelObj)
Platform: Android

Need to pass selected Invited Channel Object as string that was retrieved in getInvitations(success, fail,page, pageSize)

### getChannels(success, fail,page, pageSize)
Platform: Android

Note: page, pageSize is required 

### postMessage(success, fail,textMessage,channelObj)
Platform: Android

Note: channelObj is the current selected channel and should be passed as string.

### getMessages (success, fail,channelObj, pageSize)
Platform: Android

Note: channelObj is the current selected channel and should be passed as string.

### getChannelMembers (success, fail,channelObj, pageIndex, pageSize)
Platform: Android

Note: channelObj is the current selected channel and should be passed as string.

### getAllUsers(success, failure, pageIndex, pageSize)
Platform: Android

### inviteUserToChannel(success, failure, channelObj, userObj)
Platform: Android

InviteUserToChannel can be used to invite other users to a channel.Required properties channelObj and selected userObj

### createChannel(success, failure, channelName)
Platform: Android

### playAudioMessage(success, failure, messageObj)
Platform: Android

| Parameter | Type          | Platform | Description                                                                     |
|-----------|---------------|----------|---------------------------------------------------------------------------------|
| `success` | `Function`    | all      | Callback to be executed every time a initilization is successfull  |
| `fail`    | `Function`    | all      | Callback to be executed every time a  error occurs during initialization.|
| `messageObj` | `Object` | all      | Pass Message obj as string from selected Message Array|

### recordAudio(success, failure, channelObj)
Platform: Android

### messageEventListener(success, failure)
Platform: Android

Call to this method registers success and failure callbacks that will be explicitly called when a new message is received or if any failure occurs during new message receive event.