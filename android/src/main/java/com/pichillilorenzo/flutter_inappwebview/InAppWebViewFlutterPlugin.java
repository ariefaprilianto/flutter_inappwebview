package com.pichillilorenzo.flutter_inappwebview;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.ValueCallback;

import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.platform.PlatformViewRegistry;
import io.flutter.view.FlutterView;

public class InAppWebViewFlutterPlugin implements FlutterPlugin, ActivityAware {

  protected static final String LOG_TAG = "InAppWebViewFlutterPL";

  public static InAppBrowser inAppBrowser;
  public static InAppWebViewStatic inAppWebViewStatic;
  public static MyCookieManager myCookieManager;
  public static CredentialDatabaseHandler credentialDatabaseHandler;
  public static MyWebStorage myWebStorage;
  public static ValueCallback<Uri[]> uploadMessageArray;

  public InAppWebViewFlutterPlugin() {}

  public static void registerWith(PluginRegistry.Registrar registrar) {
    final InAppWebViewFlutterPlugin instance = new InAppWebViewFlutterPlugin();
    Shared.registrar = registrar;
    instance.onAttachedToEngine(
            registrar.context(), registrar.messenger(), registrar.activity(), registrar.platformViewRegistry(), registrar.view());
  }

  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    Shared.flutterAssets = binding.getFlutterAssets();
    onAttachedToEngine(
            binding.getApplicationContext(), binding.getBinaryMessenger(), null, binding.getPlatformViewRegistry(), null);
  }


  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger, Activity activity, PlatformViewRegistry platformViewRegistry, FlutterView flutterView) {
    Shared.applicationContext = applicationContext;
    Shared.activity = activity;

    inAppBrowser = new InAppBrowser(messenger);

    platformViewRegistry.registerViewFactory(
                    "com.pichillilorenzo/flutter_inappwebview", new FlutterWebViewFactory(messenger, flutterView));
    inAppWebViewStatic = new InAppWebViewStatic(messenger);
    myCookieManager = new MyCookieManager(messenger);
    myWebStorage = new MyWebStorage(messenger);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      credentialDatabaseHandler = new CredentialDatabaseHandler(messenger);
    }
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {
    if (inAppBrowser != null) {
      inAppBrowser.dispose();
      inAppBrowser = null;
    }
    if (myCookieManager != null) {
      myCookieManager.dispose();
      myCookieManager = null;
    }
    if (myWebStorage != null) {
      myWebStorage.dispose();
      myWebStorage = null;
    }
    if (credentialDatabaseHandler != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      credentialDatabaseHandler.dispose();
      credentialDatabaseHandler = null;
    }
    if (inAppWebViewStatic != null) {
      inAppWebViewStatic.dispose();
      inAppWebViewStatic = null;
    }
    uploadMessageArray = null;
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    Shared.activityPluginBinding = activityPluginBinding;
    Shared.activity = activityPluginBinding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    Shared.activityPluginBinding = null;
    Shared.activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
    Shared.activityPluginBinding = activityPluginBinding;
    Shared.activity = activityPluginBinding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    Shared.activityPluginBinding = null;
    Shared.activity = null;
  }
}
