/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.cordova.voicelayer;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.net.MalformedURLException;

import io.voicelayer.voicelayerSdk.PaginatedResponse;
import io.voicelayer.voicelayerSdk.VoiceLayerChannel;
import io.voicelayer.voicelayerSdk.VoiceLayerChannelInvitation;
import io.voicelayer.voicelayerSdk.VoiceLayerChannelUser;
import io.voicelayer.voicelayerSdk.VoiceLayerClient;
import io.voicelayer.voicelayerSdk.VoiceLayerConfiguration;
import io.voicelayer.voicelayerSdk.VoiceLayerMessage;
import io.voicelayer.voicelayerSdk.VoiceLayerMessagePlayer;
import io.voicelayer.voicelayerSdk.VoiceLayerMessageRecorder;
import io.voicelayer.voicelayerSdk.VoiceLayerObject;
import io.voicelayer.voicelayerSdk.VoiceLayerRecorderEvent;
import io.voicelayer.voicelayerSdk.VoiceLayerUser;
import io.voicelayer.voicelayerSdk.exceptions.VoiceLayerException;
import io.voicelayer.voicelayerSdk.exceptions.VoiceLayerRecorderException;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerChannelInvitationCallback;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerCreateCallback;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerFetchCallback;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerJoinChannelCallback;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerLoginCallback;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerLogoutCallback;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerMessageEventListener;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerPlayerEventListener;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerRecorderEventListener;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerRegistrationCallback;
import io.voicelayer.voicelayerSdk.interfaces.VoiceLayerRemoveCallback;
import retrofit.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * This class provides access to voicelayer.io API's on the device.
 * It uses all the available API's present in 3.4.0
 * https://developers.voicelayer.io/docs/main/android/downloads.html
 */
public class VoiceLayerIo extends CordovaPlugin {
  public static String TAG = "VoiceLayerIo-App";
  private Context context;
  public CallbackContext callbackContext;
  JSONObject config;
  String userName;
  String password;
  String email;
  int channelsPage,channelsPageSize;
  int invitedChannelsPage,invitedChannelsPageSize;
  int messagesPageSize;
  VoiceLayerMessagePlayer player;
  VoiceLayerChannel recordAudioChannel = null;
  int membersPage,membersPageSize;
  Gson gson = null;
  JSONArray array = null;
  JSONObject returnobj = null;
  ArrayList<VoiceLayerUser>  appUserslist;
  public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
  public static final int RECORD_REQ_CODE = 0;
  public static final int PERMISSION_DENIED_ERROR = 1;

  /**
   * Constructor.
   */
  public VoiceLayerIo() {
  }

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
  }

  /**
   * Executes the request and returns PluginResult.
   *
   * @param action          The action to execute.
   * @param args            JSONArray of arguments for the plugin.
   * @param callbackContext The callback context used when calling back into JavaScript.
   * @return True when the action was valid, false otherwise.
   */
  public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    context = this.cordova.getActivity().getApplicationContext();
    this.callbackContext = callbackContext;

    if (action.equals("initialize")) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          try {
            config = args.getJSONObject(0);
            VoiceLayerClient.Initialize(context, new VoiceLayerConfiguration(
              config.getString("appName"),
              config.getString("uid"),
              config.getString("secretKey")
            ));
            Log.d(TAG, "Voicelayer initialized successfully");
            callbackContext.success("SUCCESS");
          } catch (Exception e) {
            throw new RuntimeException("Unable to initialize client.");
          }
        }
      });

    } else if (action.equals("login")) {
      userName = args.getString(0);
      password = args.getString(1);
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {

          VoiceLayerClient.getInstance().login(userName, password, new VoiceLayerLoginCallback() {
            @Override
            public void onLoginComplete(VoiceLayerUser voiceLayerUser, String s, VoiceLayerException e) {
              if (e == null) {
                callbackContext.success("SUCCESS");
              } else {
                Log.e(TAG, "Login failed " + e.toString());
                callbackContext.error("Login failed");
              }
            }
          });
        }
      });
      return true;
    } else if (action.equals("register")) {
      userName = args.getString(0);
      email = args.getString(1);
      password = args.getString(2);

      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          VoiceLayerClient.getInstance().register(userName, email, password, new VoiceLayerRegistrationCallback() {
            @Override
            public void onRegistrationComplete(String username, String email, VoiceLayerException e) {
              if (e == null) {
                callbackContext.success("SUCCESS");
              } else {
                callbackContext.error("Registration failed");
              }
            }
          });
        }
      });
      return true;
    } else if(action.equals("logout")){
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          VoiceLayerClient.getInstance().logout(new VoiceLayerLogoutCallback() {
            @Override
            public void onLogoutComplete(VoiceLayerException e) {
              callbackContext.success("SUCCESS");
            }
          });
        }
      });

    } else if (action.equals("getInvitations")){
      invitedChannelsPage = args.getInt(0);
      invitedChannelsPageSize = args.getInt(1);
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          VoiceLayerClient.getInstance().getInvitedChannels(invitedChannelsPage, invitedChannelsPageSize, new VoiceLayerFetchCallback<PaginatedResponse<List<VoiceLayerChannel>>>() {
            @Override
            public void onFetchComplete(PaginatedResponse<List<VoiceLayerChannel>> response, VoiceLayerException e1) {
              if (e1 == null && response.getResult() != null && response.getResult().size() > 0) {
                gson = new Gson();
                JSONArray array = null;
                String jsonInString = gson.toJson(response.getResult());
                try {
                  array = new JSONArray(jsonInString);
                }catch (Exception e) {

                }

                Log.e(TAG, "jsonInString " + jsonInString);
                callbackContext.success(array);
              } else {
                callbackContext.error("No New Invitations");
              }
            }
          });
        }
      });
    } else if (action.equals("joinChannel")){
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          VoiceLayerChannel joinChannel = null;
          try {
            joinChannel = VoiceLayerChannel.FromJson(args.getString(0));
          }catch (Exception e){

          }
          joinChannel.join(new VoiceLayerJoinChannelCallback() {
            @Override
            public void onJoinChannelComplete(VoiceLayerException e) {
              callbackContext.success("SUCCESS");
            }
          });
        }
      });
    } else if (action.equals("declineChannelRequest")){
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          VoiceLayerChannel declineChannel = null;
          try {
            declineChannel = VoiceLayerChannel.FromJson(args.getString(0));
          }catch (Exception e){

          }
          declineChannel.declineInvitation(new VoiceLayerRemoveCallback<VoiceLayerUser>() {
            @Override
            public void onRemoveCompleted(VoiceLayerUser voiceLayerUser, VoiceLayerException e) {
              callbackContext.success("SUCCESS");
            }
          });
        }
      });
    }else if (action.equals("getChannels")){
      channelsPage = args.getInt(0);
      channelsPageSize = args.getInt(1);
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          VoiceLayerClient.getInstance().getChannels(channelsPage, channelsPageSize, new VoiceLayerFetchCallback<PaginatedResponse<List<VoiceLayerChannel>>>() {
            @Override
            public void onFetchComplete(PaginatedResponse<List<VoiceLayerChannel>> response, VoiceLayerException e1) {
              if (e1 == null && response.getResult() != null && response.getResult().size() > 0) {
                //Log.e(TAG, "channels " + response.getResult());
                gson = new Gson();
                JSONArray array = null;
                String jsonInString = gson.toJson(response.getResult());
                try {
                  array = new JSONArray(jsonInString);
                }catch (Exception e) {

                }

                Log.e(TAG, "jsonInString " + jsonInString);
                callbackContext.success(array);
              } else {
                callbackContext.error("No channels");
              }
            }
          });
        }
      });

    } else if (action.equals("createChannel")) {

      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          String channelName = null;
          try {
            channelName = args.getString(0);
          } catch (Exception e) {

          }
          VoiceLayerClient.getInstance().createChannel(channelName, EnumSet.of(VoiceLayerChannel.VoiceLayerChannelTrait.PUBLIC), new VoiceLayerCreateCallback<VoiceLayerChannel>() {
            @Override
            public void onCreateComplete(VoiceLayerChannel voiceLayerChannel, VoiceLayerException e) {
              if (e != null) {
                callbackContext.error("Unable to Create Channel");
              } else {
                callbackContext.success("SUCCESS");
              }
            }
          });
        }
      });


    } else if (action.equals("postMessage")) {

      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          String textMsg = null;
          VoiceLayerChannel  myChannel = null;
          try {
            myChannel = VoiceLayerChannel.FromJson(args.getString(1));
            textMsg = args.getString(0);
          } catch (Exception e) {
            callbackContext.error("Unable to Post Message check parameters");
          }
          myChannel.postMessage(textMsg, new VoiceLayerCreateCallback<VoiceLayerMessage>() {
            @Override
            public void onCreateComplete(VoiceLayerMessage voiceLayerMessage, VoiceLayerException e) {
              if (e == null) {
                gson = new Gson();
                String jsonInString = gson.toJson(voiceLayerMessage);
                try {
                  returnobj = new JSONObject(jsonInString);
                }catch (Exception exp) {
                  callbackContext.error("Unable to Post Message");
                }

                Log.e(TAG, "textobject " + jsonInString);
                callbackContext.success(returnobj);
              } else {
                callbackContext.error("Unable to Post Message");
              }
            }
          });
        }
      });
    } else if(action.equals("getMessages")){

      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          VoiceLayerChannel  myChannel = null;
          try {
            myChannel = VoiceLayerChannel.FromJson(args.getString(0));
            messagesPageSize = args.getInt(1);
          } catch (Exception e) {
            callbackContext.error("Unable to list Message - check parameters");
          }
          Log.e(TAG, "channel----------> " + myChannel);
          myChannel.getMessages("1", messagesPageSize, new VoiceLayerFetchCallback<PaginatedResponse<List<VoiceLayerMessage>>>() {
            @Override
            public void onFetchComplete(PaginatedResponse<List<VoiceLayerMessage>> response, VoiceLayerException e1) {
              if (e1 != null) {
                callbackContext.error("No messages");
              }
              gson = new Gson();
              String jsonInString = gson.toJson(response.getResult());
              try {
                array = new JSONArray(jsonInString);
              }catch (Exception e) {
                callbackContext.error("Unable to list Message");
              }
              Log.e(TAG, "jsonInString " + jsonInString);
              callbackContext.success(array);
            }
          });
        }
      });

    } else if(action.equals("messageEventListener")){
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          VoiceLayerClient.getInstance().setMessageEventListener(new VoiceLayerMessageEventListener() {
            @Override
            public void onMessagePosted(VoiceLayerMessage voiceLayerMessage) {
              Log.d(TAG, "Message Posted");
              gson = new Gson();
              String jsonInString = gson.toJson(voiceLayerMessage);
              try {
                returnobj = new JSONObject(jsonInString);
              }catch (Exception exp) {

              }

              Log.e(TAG, "textobject " + jsonInString);
              webView.loadUrl("javascript:window.voiceLayer.msgBroadcastSuccessListener('" + returnobj + "');");
            }

            @Override
            public void onMessageUpdated(VoiceLayerMessage voiceLayerMessage) {
              Log.d(TAG, "Message updated");
            }

            @Override
            public void onMessageRemoved(String s, VoiceLayerChannel voiceLayerChannel) {
              Log.d(TAG, "Message deleted");
              webView.loadUrl("javascript:window.voiceLayer.msgBroadcastSuccessListener('" + "MSG REMOVED" + "');");
            }

            @Override
            public void onMessageReuploaded(VoiceLayerMessage message) {
              Log.d(TAG, "Message reuploaded");
            }

            @Override
            public void onMissedMessagesRetrieved(Map<VoiceLayerChannel, List<VoiceLayerMessage>> messages) {
              Log.d(TAG, "Missed messages received.");
            }

          });
        }
      });

    } else if(action.equals("getChannelMembers")){

      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          VoiceLayerChannel myChannel = null;
          try {
            myChannel = VoiceLayerChannel.FromJson(args.getString(0));
            membersPage = args.getInt(1);
            membersPageSize = args.getInt(2);
          } catch (Exception e) {
            callbackContext.error("Unable to list members- check parameters");
          }
          myChannel.getUsers(membersPage, membersPageSize, new VoiceLayerFetchCallback<PaginatedResponse<List<VoiceLayerChannelUser>>>() {
            @Override
            public void onFetchComplete(PaginatedResponse<List<VoiceLayerChannelUser>> response, VoiceLayerException e1) {
              if (e1 != null) {
                callbackContext.error("No Members");
              }
              List<VoiceLayerChannelUser> users = response.getResult();

              gson = new Gson();
              String jsonInString = gson.toJson(response.getResult());
              try {
                array = new JSONArray(jsonInString);
              }catch (Exception e) {

              }
              Log.e(TAG, "jsonInString " + jsonInString);
              callbackContext.success(array);
            }
          });
        }
      });

    } else if(action.equals("getAllUsers")){

      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          int page = 0;
          int pageSize =10;
          try {
            page = args.getInt(0);
            pageSize = args.getInt(1);
          } catch (Exception e) {

          }
          VoiceLayerClient.getInstance().getUsers(page, pageSize, new VoiceLayerFetchCallback<PaginatedResponse<List<VoiceLayerUser>>>() {
            @Override
            public void onFetchComplete(PaginatedResponse<List<VoiceLayerUser>> response, VoiceLayerException e1) {
              if (e1 != null) {
                callbackContext.error("No Members Found");
              }

              List<VoiceLayerUser> result = response.getResult();
              if (result == null) {
                result = new ArrayList<VoiceLayerUser>();
              }

              appUserslist = new ArrayList<VoiceLayerUser>(result);
              gson = new Gson();
              String jsonInString = gson.toJson(response.getResult());
              try {
                array = new JSONArray(jsonInString);
              }catch (Exception e) {

              }
              Log.e(TAG, "jsonInString " + jsonInString);
              callbackContext.success(array);

            }
          });
        }
      });

    } else if(action.equals("inviteUserToChannel")){
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          String userID = null;
          VoiceLayerUser userToInvite = null;
          VoiceLayerChannel myChannel = null;
          try {
            myChannel = VoiceLayerChannel.FromJson(args.getString(0));
            userToInvite = userObjFromJson(args.getString(1));
          } catch (Exception e) {
            callbackContext.error("No Members Found" + e.toString());
          }
          myChannel.inviteUser(userToInvite, new VoiceLayerChannelInvitationCallback() {
            @Override
            public void onChannelInvitationCompleted(List<VoiceLayerChannelInvitation> list, VoiceLayerException e) {
              if (e != null) {
                callbackContext.error("No Members Found" + e.toString());
              } else {
                callbackContext.success("Invitation was sent Successfully");
              }
            }
          });
        }
      });

    } else if(action.equals("recordAudio")){
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {

          try {
            recordAudioChannel = VoiceLayerChannel.FromJson(args.getString(0));
          } catch (Exception e) {

          }
          if(cordova.hasPermission(RECORD_AUDIO))
          {
            manageAudio(recordAudioChannel);
          }
          else
          {
            getRecordPermission(RECORD_REQ_CODE);
          }

        }
      });

    } else if(action.equals("playAudioMessage")){
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          VoiceLayerMessage message = null;
          try {
            message = messageObjFromJson(args.getString(0));
          } catch (Exception e) {

          }

          player = VoiceLayerClient.getInstance().getMessagePlayer();
          player.playMessage(message);
          player.setPlayerEventListener(new VoiceLayerPlayerEventListener() {
            @Override
            public void onMessagePlaybackStarted(VoiceLayerMessage voiceLayerMessage) {
              Log.d(TAG, "Player: message started "+voiceLayerMessage.id);
              webView.loadUrl("javascript:window.voiceLayer.playAudioSuccessListener('" + "AUDIO_PLAY_STARTED" + "');");
            }

            @Override
            public void onMessagePlaybackPaused(VoiceLayerMessage voiceLayerMessage) {
              Log.d(TAG, "Player: message paused "+voiceLayerMessage.id);
            }

            @Override
            public void onMessagePlaybackResumed(VoiceLayerMessage voiceLayerMessage) {
              Log.d(TAG, "Player: message resumed "+voiceLayerMessage.id);
            }

            @Override
            public void onMessagePlaybackFinished(VoiceLayerMessage voiceLayerMessage) {
              Log.d(TAG, "Player: message finished "+voiceLayerMessage.id);
              webView.loadUrl("javascript:window.voiceLayer.playAudioSuccessListener('" + "AUDIO_PLAY_END" + "');");
            }

            @Override
            public void onMessagePlaybackStopped(VoiceLayerMessage voiceLayerMessage) {
              Log.d(TAG, "Player: message stopped "+voiceLayerMessage.id);
            }

            @Override
            public void onMessagePlaybackFailed(VoiceLayerMessage voiceLayerMessage, VoiceLayerException e) {
              Log.d(TAG, "Player: message failed "+voiceLayerMessage.id);
              webView.loadUrl("javascript:window.voiceLayer.playAudioErrorListener('" + "AUDIO_PLAY_FAILED" + "');");
            }
          });

        }
      });
    }
    return true;

  }

  protected void getRecordPermission(int requestCode)
  {
    cordova.requestPermission(this, requestCode, RECORD_AUDIO);
  }

  public void onRequestPermissionResult(int requestCode, String[] permissions,
                                        int[] grantResults) throws JSONException
  {
    for(int r:grantResults)
    {
      if(r == PackageManager.PERMISSION_DENIED)
      {
        this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
        return;
      }
    }
    switch(requestCode)
    {
      case RECORD_REQ_CODE:
        manageAudio(recordAudioChannel);
        break;
    }
  }

  protected void manageAudio(VoiceLayerChannel channel){
    VoiceLayerClient client = VoiceLayerClient.getInstance();

    final VoiceLayerMessageRecorder recorder = client.getMessageRecorder();
    if (recorder.isRecording()) {
      recorder.stopRecording();
      //callbackContext.success("AUDIO RECORD STOPPED");
    } else {
      recorder.startRecording(channel);
      //callbackContext.success("AUDIO RECORD STARTED");
    }
    recorder.setRecorderEventListener(new VoiceLayerRecorderEventListener() {
      @Override
      public void onRecorderEvent(VoiceLayerRecorderEvent event, VoiceLayerMessage message) {
        switch (event) {
          case START:
            sendRecordingStatustoCallback(1, "RECORDING_STARTED");
            Log.d(TAG, "Recorder: started recording "+message.id);
            break;
          case FINISH:
            Log.d(TAG, "Recorder: finished recording "+message.id);
            gson = new Gson();
            String jsonInString = gson.toJson(message);
            Log.e(TAG, "textobject " + jsonInString);
            sendRecordingStatustoCallback(2,jsonInString);
            break;
          case UPLOAD_START:
            sendRecordingStatustoCallback(3,"UPLOAD_STARTED");
            break;
          case UPLOAD_FINISH:
            Log.d(TAG, "Recorder: finished uploading "+message.id);
            sendRecordingStatustoCallback(4,"UPLOAD_FINISHED");
            break;
          default:
            Log.d(TAG, "Recorder: unexpected event "+message.id);
            break;
        }
      }

      @Override
      public void onRecordingFailed(VoiceLayerRecorderException exception, VoiceLayerMessage message) {
        Log.d(TAG, "Recorder: recording failed "+message.id);
        try {
          webView.loadUrl("javascript:window.voiceLayer.recordAudioFailureListener('" + createRetunObj(1, "RECORDING_FAILED") + "');");
        }catch (Exception e) {

        }
      }

      @Override
      public void onUploadFailed(VoiceLayerRecorderException exception, VoiceLayerMessage message) {
        Log.d(TAG, "Recorder: uploading failed "+message.id);
        try {
          webView.loadUrl("javascript:window.voiceLayer.recordAudioFailureListener('" + createRetunObj(2, "UPLOAD_FAILED") + "');");
        }catch (Exception e) {

        }
      }
    });
  }

  private void sendRecordingStatustoCallback(int code, String msg){
    try {
      webView.loadUrl("javascript:window.voiceLayer.recordAudioSuccessListener('" + createRetunObj(code, msg) + "');");
    }catch (Exception e) {

    }
  }

  private JSONObject createRetunObj(int code, String message) throws JSONException {
    JSONObject retunObj = new JSONObject();
    retunObj.put("code", code);
    retunObj.put("message", message);
    return retunObj;
  }

  public static VoiceLayerMessage messageObjFromJson(String var0) {
    VoiceLayerMessage messegeObj = new GsonBuilder().create().fromJson(var0, VoiceLayerMessage.class);
    return messegeObj;
  }

  public static VoiceLayerUser userObjFromJson(String var0) {
    VoiceLayerUser userObj = new GsonBuilder().create().fromJson(var0, VoiceLayerUser.class);
    return userObj;
  }
}
