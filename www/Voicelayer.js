/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

var exec = require('cordova/exec');
var platform = require('cordova/platform');

/**
 * Provides access to notifications on the device.
 */

module.exports = {

  /**
   * Open a native alert dialog, with a customizable title and button text.
   *
   * @param {Json} config       Need to Pass Appname,UID,App secret
   * @param {Function} completeCallback   The callback .
   */
  //  voiceLayer.initialize({
  //         appName: "VoiceLayerSDKDemo",
  //         uid: "548a68d47b2d0800007ab82d",
  //         secretKey: "493b01cb1bd74a23bb056e1fb7a22a42"
  //     });
  initialize: function (completeCallback, failure, config) {
    if (config === undefined) {
      throw 'Configuration is missing,Please set Appname,UID,App secret';
    } else {
      exec(completeCallback, failure, "VoiceLayerIo", "initialize", [config]);
    }
  },
  login: function (completeCallback, failure, username, password) {
    exec(completeCallback, failure, "VoiceLayerIo", "login", [username, password]);
  },
  register: function (completeCallback, failure, username, email, password) {
    exec(completeCallback, failure, "VoiceLayerIo", "register", [username, email, password]);
  },
  logout: function (completeCallback, failure) {
    exec(completeCallback, failure, "VoiceLayerIo", "logout", []);
  },
  getInvitations: function (completeCallback, failure, page, pageSize) {

    exec(completeCallback, failure, "VoiceLayerIo", "getInvitations", [page, pageSize]);
  },
  joinChannel: function (completeCallback, failure, channelObj) {

    exec(completeCallback, failure, "VoiceLayerIo", "joinChannel", [channelObj]);
  },
  declineChannelRequest: function (completeCallback, failure, channelObj) {

    exec(completeCallback, failure, "VoiceLayerIo", "declineChannelRequest", [channelObj]);
  },
  getChannels: function (completeCallback, failure, page, pageSize) {

    exec(completeCallback, failure, "VoiceLayerIo", "getChannels", [page, pageSize]);
  },
  postMessage: function (completeCallback, failure, textMessage, channelObj) {
    exec(completeCallback, failure, "VoiceLayerIo", "postMessage", [textMessage, channelObj]);
  },
  getMessages: function (completeCallback, failure, channelObj, pageSize) {
    exec(completeCallback, failure, "VoiceLayerIo", "getMessages", [channelObj, pageSize]);
  },
  messageEventListener: function (successCallback, failureCallback) {
    this.msgSuccessCallback = successCallback;
    this.msgFailCallback = failureCallback;
    exec(this.msgSuccessCallback, this.msgFailCallback, "VoiceLayerIo", "messageEventListener", []);
  },
  msgBroadcastSuccessListener: function (msg) {
    this.msgSuccessCallback(msg);
  },
  msgBroadcastFailureListener: function (err) {
    this.msgFailCallback(err);
  },
  getChannelMembers: function (completeCallback, failure, channelObj, pageIndex, pageSize) {
    exec(completeCallback, failure, "VoiceLayerIo", "getChannelMembers", [channelObj, pageIndex, pageSize]);
  },
  getAllUsers: function (completeCallback, failure, pageIndex, pageSize) {
    exec(completeCallback, failure, "VoiceLayerIo", "getAllUsers", [pageIndex, pageSize]);
  },
  inviteUserToChannel: function (completeCallback, failure, channelObj, userObj) {
    exec(completeCallback, failure, "VoiceLayerIo", "inviteUserToChannel", [channelObj, userObj]);
  },
  createChannel: function (completeCallback, failure, channelName) {
    exec(completeCallback, failure, "VoiceLayerIo", "createChannel", [channelName]);
  },
  playAudioSuccessListener: function (msg) {
    console.log('in playAudioSuccessListener  method--------->' + msg);
    this.audioWinCallback(msg);
  },
  playAudioErrorListener: function (err) {
    this.audioFailCallback(err);
  },
  playAudioMessage: function (completeCallback, failure, messageObj) {
    this.audioWinCallback = completeCallback;
    this.audioFailCallback = failure;
    exec(this.audioWinCallback, this.audioFailCallback, "VoiceLayerIo", "playAudioMessage", [messageObj]);
  },
  recordAudio: function (completeCallback, failure, channelObj) {
    this.recordSuccessCallback = completeCallback;
    this.recordFailureCallback = failure;
    exec(this.recordSuccessCallback, this.recordFailureCallback, "VoiceLayerIo", "recordAudio", [channelObj]);
  },
  recordAudioSuccessListener: function (msg) {
    this.recordSuccessCallback(msg);
  },
  recordAudioFailureListener: function (err) {
    this.recordFailureCallback(err);
  }
};
