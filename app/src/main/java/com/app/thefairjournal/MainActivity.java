package com.app.thefairjournal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {


    WebView myWebView;
    ImageView  home, camera,terif,event,contact;
    ImageView logo;
    SwipeRefreshLayout pullToRefresh;
    private static final String TAG = "MainActivity";
    private View splashImgV;
    private static ValueCallback<Uri[]> mFilePathCallback;
    private ValueCallback<Uri> mUploadFile;
    private ValueCallback<String[]> mFilePathCallbackStr;
    private ProgressDialog progressBar;
    public static final String PREFS_NAME = "MyPrefsFile";
    RelativeLayout splash;
    ProgressDialog dialog;
    private String currentUrl = "http://www.example.com";
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();



        camera = (ImageView) findViewById(R.id.camera);
        home = (ImageView) findViewById(R.id.home);
        terif = (ImageView) findViewById(R.id.terif);
        event = (ImageView) findViewById(R.id.event);
        contact = (ImageView) findViewById(R.id.contact);



        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.loadUrl(getResources().getString(R.string.url));
            }
        });


        myWebView = (WebView) findViewById(R.id.webView);
        pullToRefresh = findViewById(R.id.pullToRefresh);

        pullToRefresh.setColorSchemeColors(Color.BLACK);


        if (Build.VERSION.SDK_INT >= 21) {
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        myWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        myWebView.getSettings().setSaveFormData(false);
        webSettings.setSupportZoom(true);
        //CookieManager.getInstance().setAcceptCookie(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.getSettings().setGeolocationEnabled(true);
        WebSettings webSettingss = myWebView.getSettings();
        webSettingss.setAllowContentAccess(true);
        myWebView.getSettings().setAppCacheEnabled(true);
        myWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        myWebView.loadUrl(getResources().getString(R.string.url));


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                myWebView.reload();

            }
        });


//        final ProgressDialog pd = ProgressDialog.show(this, "", "Loading...",true);
        final Activity activity = MainActivity.this;
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.i("onReceivedError", "onReceivedError: " + failingUrl + " errorCode: " + errorCode);
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.stopLoading();
                Intent loadSplash = new Intent(MainActivity.this, Error.class);

                startActivity(loadSplash);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pullToRefresh.setRefreshing(true);


            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pullToRefresh.setRefreshing(false);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else if (url.startsWith("mailto:")) {
                    if (activity != null) {
                        MailTo mt = MailTo.parse(url);

                        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
                        intent.putExtra(Intent.EXTRA_TEXT, mt.getBody());
                        intent.setData(Uri.parse("mailto:" + mt.getTo()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                        try {

                            activity.startActivity(intent);
                        } catch (android.content.ActivityNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Log.d("Email error:", e.toString());
                        }
                        view.reload();
                        return false;
                    }
                } else if (url != null && url.startsWith("whatsapp://")) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;

                } else if (url != null && url.startsWith("https://twitter.com/")) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;

                } else if (url != null && url.startsWith("https://plus.google.com/")) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;

                } else if (url != null && url.startsWith("https://www.facebook.com/")) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;

                } else if (url.startsWith("sms:")) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(intent);
                    return true;


                } else {
                    view.loadUrl(url);
                }
                return false;
            }


        });


        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result.getExtra();
                Context context = view.getContext();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                context.startActivity(browserIntent);
                return false;
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                super.onConsoleMessage(message, lineNumber, sourceID);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                //   Log.d(TAG, "");
                if (mUploadFile != null) {
                    mUploadFile.onReceiveValue(null);
                }
                mUploadFile = uploadMsg;
                showFileChooserPicker();
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                // Log.d(TAG, "");
                if (mUploadFile != null) {
                    mUploadFile.onReceiveValue(null);
                }
                mUploadFile = uploadMsg;
                showFileChooserPicker();
            }


            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                //Log.d(TAG, "");
                if (mUploadFile != null) {
                    mUploadFile.onReceiveValue(null);
                }
                mUploadFile = uploadFile;
                showFileChooserPicker();
            }


            public void showFileChooser(ValueCallback<String[]> filePathCallback, String acceptType, boolean paramBoolean) {
                //Log.d(TAG, "");
                if (mFilePathCallbackStr != null) {
                    mFilePathCallbackStr.onReceiveValue(null);
                }
                mFilePathCallbackStr = filePathCallback;
                showFileChooserPicker();
            }


            public void showFileChooser(ValueCallback<String[]> uploadFileCallback, FileChooserParams fileChooserParams) {
                //Log.d(TAG, "");
                if (mFilePathCallbackStr != null) {
                    mFilePathCallbackStr.onReceiveValue(null);
                }
                mFilePathCallbackStr = uploadFileCallback;
                showFileChooserPicker();
            }


            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                showFileChooserPicker();
                return true;
            }
        });


    }


    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }


    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;
        }
    }


    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooserPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    Uri uri = data.getData();

                    Log.d(TAG, "File Uri: " + uri.toString());

                    if (mFilePathCallback != null) {
                        Uri[] results = new Uri[]{uri};
                        mFilePathCallback.onReceiveValue(results);
                        mFilePathCallback = null;
                    }

                    if (mUploadFile != null) {
                        mUploadFile.onReceiveValue(uri);
                        mUploadFile = null;
                    }

                    if (mFilePathCallbackStr != null) {
                        String[] values = {uri.getPath()};
                        mFilePathCallbackStr.onReceiveValue(values);
                        mFilePathCallbackStr = null;
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }


}

