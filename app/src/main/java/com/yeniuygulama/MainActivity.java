package com.yeniuygulama;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.yeniuygulama.UrlActivity.websiteurl;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView username;
    ImageView userimage,userback;
    private SwipeRefreshLayout swipeContainer;
    private AlertDialog.Builder Notify;
    private WebView view;
    private DrawerLayout drawer;
    BottomNavigationView navigation;
    RelativeLayout one;
    AdView mAdView;
    SwitchCompat notification, vibrate;

    private View mCustomView;
    private int mOriginalSystemUiVisibility;
    private int mOriginalOrientation;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private InterstitialAd interstitialAd;
    AdRequest adRequestinst;
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;
    protected int mRequestCodeFilePicker = 51426;
    public static final String gg = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    Locale locale;
    CharSequence[] langname = {"Arabic","Deutsch","English","Español","Français","Italiano","Nederlands","Português","Pусский","Türkçe"};
    String alfabe, urlalfabe;

    String website_url = websiteurl;
    String app_server_url = website_url+"/mobileapp/token.php";
    OSPermissionSubscriptionState status;
    String token;
    SwitchCompat switcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = OneSignal.getPermissionSubscriptionState();
        SharedPreferences getlangd = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String langd = getlangd.getString("sendlang","");
        if (langd != "") {
            locale = new Locale(langd);
        } else {
            locale = Locale.getDefault();
        }

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);

        int Permission_All = 1;
        String[] Permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, };
        if(!hasPermissions(this, Permissions)){
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
        }

        interstitialAd = new InterstitialAd(this);
        adRequestinst = new AdRequest.Builder().build();
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
            @Override
            public void onAdOpened() {
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
            }
        });

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setVisibility(View.GONE);

        // Load an ad into the AdMob banner view.
        View adContainer = findViewById(R.id.adMobView);
        mAdView = new AdView(this);
        ((RelativeLayout)adContainer).addView(mAdView);

        one = (RelativeLayout) findViewById(R.id.viewid);
        one.setVisibility(View.GONE);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_switch);
        View actionView = MenuItemCompat.getActionView(menuItem);

        switcher= (SwitchCompat) actionView.findViewById(R.id.switcher);
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        switcher.setChecked(sharedPreferences.getBoolean("toggleButton", true));
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) {
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("toggleButton", isChecked);
                    editor.commit();

                    OneSignal.setSubscription(true);
                } else {
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("toggleButton", isChecked);
                    editor.commit();

                    OneSignal.setSubscription(false);
                }

            }
        });
        SharedPreferences getavatar = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String av = getavatar.getString("sendavatar","noname");

        SharedPreferences getback = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String back = getback.getString("sendback","noname");

        SharedPreferences getname = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = getname.getString("sendname","noname");

        username = (TextView)header.findViewById(R.id.username);
        username.setText(name);
        userimage = (ImageView)header.findViewById(R.id.userimage);
        Picasso.with(this).load(av).placeholder(R.drawable.avatar).into(userimage);
        userback = (ImageView)header.findViewById(R.id.userback);
        Picasso.with(this).load(back).placeholder(R.drawable.background).into(userback);

        view = (WebView) findViewById(R.id.webview);
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        setUpWebViewDefaults(view);
        view.getSettings().setUserAgentString(gg);
        CookieManager.getInstance().setAcceptCookie(true);
        String openURL = getIntent().getStringExtra("openURL");
        if (openURL == null){
            view.loadUrl(website_url);
        }
        else
        {
            view.loadUrl(openURL);
        }
        ButtonAvatar myJavaScriptInterfaceAvatar = new ButtonAvatar(MainActivity.this);
        view.addJavascriptInterface(myJavaScriptInterfaceAvatar, "bsnav");

        ButtonBack myJavaScriptInterfaceBack = new ButtonBack(MainActivity.this);
        view.addJavascriptInterface(myJavaScriptInterfaceBack, "bsnbck");

        ButtonName myJavaScriptInterfaceName = new ButtonName(MainActivity.this);
        view.addJavascriptInterface(myJavaScriptInterfaceName, "bsnnam");

        ButtonTok myJavaScriptInterface = new ButtonTok(MainActivity.this);
        view.addJavascriptInterface(myJavaScriptInterface, "gsntok");

        if(CheckNetwork.isInternetAvailable(MainActivity.this))
        {
        }
        else
        {
            refresh();
        }
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith("whatsapp://")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else if (url != null && url.startsWith("fb-messenger://")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                try {
                    view.stopLoading();
                } catch (Exception e) {
                }
                try {
                    view.clearView();
                } catch (Exception e) {
                }
                if (view.canGoBack()) {
                    view.goBack();
                }
                view.setVisibility(View.GONE);
                refresh();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                swipeContainer.setRefreshing(true);

            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeContainer.setRefreshing(false);

                view.loadUrl("javascript:(function(){document.getElementById('gsnav').click();})()");
                view.loadUrl("javascript:(function(){document.getElementById('gsnbc').click();})()");
                view.loadUrl("javascript:(function(){document.getElementById('gsnna').click();})()");
                view.loadUrl("javascript:(function(){document.getElementById('gsntok').click();})()");
            }
        });

        view.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        request.grant(request.getResources());
                    }
                });
            }

            @Override
            public Bitmap getDefaultVideoPoster() {

                return BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.video_poster);
            }

            @Override
            public void onShowCustomView(View view,
                                         CustomViewCallback callback) {
                // if a view already exists then immediately terminate the new one
                if (mCustomView != null) {
                    onHideCustomView();
                    return;
                }

                // 1. Stash the current state
                mCustomView = view;
                mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                mOriginalOrientation = getRequestedOrientation();

                // 2. Stash the custom view callback
                mCustomViewCallback = callback;

                // 3. Add the custom view to the view hierarchy
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                decor.addView(mCustomView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));


                // 4. Change the state of the window
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                // 1. Remove the custom view
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                decor.removeView(mCustomView);
                mCustomView = null;

                // 2. Restore the state to it's original form
                getWindow().getDecorView()
                        .setSystemUiVisibility(mOriginalSystemUiVisibility);
                setRequestedOrientation(mOriginalOrientation);

                // 3. Call the custom view callback
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;

            }

            // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, null);
            }

            // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg, acceptType, null);
            }

            // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileInput(uploadMsg, null, false);
            }

            // file upload callback (Android 5.0 (API level 21) -- current) (public method)
            @SuppressWarnings("all")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (Build.VERSION.SDK_INT >= 21) {
                    final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;
                    openFileInput(null, filePathCallback, allowMultiple);
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                view.reload();
                ( new Handler()).postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        swipeContainer.setRefreshing(false);

                    }
                }, 1000);
            }
        });

        SharedPreferences notificount = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editornotificount = notificount.edit();
        editornotificount.remove("counter");
        editornotificount.commit();
    }

    public class ButtonAvatar {
        Context mContext;
        ButtonAvatar(Context c) {
            mContext = c;
        }
        int flag=1;
        @JavascriptInterface
        public void onButtonClick(String tstav) {
            if(flag==1) {
            SharedPreferences sndavatar = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sndavatar.edit();
            editor.putString("sendavatar",tstav);
            editor.commit();
            }
            flag=0;
        }
    }

    public class ButtonBack {
        Context mContext;
        ButtonBack(Context c) {
            mContext = c;
        }
        int flag=1;
        @JavascriptInterface
        public void onButtonClick(String tstb) {
            if(flag==1) {
            SharedPreferences sndback = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sndback.edit();
            editor.putString("sendback",tstb);
            editor.commit();
            }
            flag=0;
        }
    }

    public class ButtonName {
        Context mContext;
        ButtonName(Context c) {
            mContext = c;
        }
        int flag=1;
        @JavascriptInterface
        public void onButtonClick(String tstn) {
            if(flag==1) {
            SharedPreferences sndname = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sndname.edit();
            editor.putString("sendname",tstn);
            editor.commit();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initInstances();
                }
            });
            }
            flag=0;
        }
    }

    public class ButtonTok {
        Context mContext;
        ButtonTok(Context c) {
            mContext = c;
        }
        int flag=1;
        @JavascriptInterface
        public void onButtonClick(String toast) {
            if(flag==1) {
            final String tokenusername = toast;
            getData();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            SharedPreferences sendusername = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editorr2 = sendusername.edit();
            editorr2.putString("sendusername",tokenusername);
            editorr2.commit();


            token = status.getSubscriptionStatus().getUserId();

            new sndtkn().execute(tokenusername,token);
            }
            flag=0;
        }
    }

    public void initInstances() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        SharedPreferences getavatar = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String av = getavatar.getString("sendavatar","noname");

        SharedPreferences getback = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String back = getback.getString("sendback","noname");

        SharedPreferences getname = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = getname.getString("sendname","noname");

        username = (TextView)header.findViewById(R.id.username);
        username.setText(name);
        userimage = (ImageView)header.findViewById(R.id.userimage);
        Picasso.with(this).load(av).placeholder(R.drawable.avatar).into(userimage);
        userback = (ImageView)header.findViewById(R.id.userback);
        Picasso.with(this).load(back).placeholder(R.drawable.background).into(userback);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        SharedPreferences getname = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name2 = getname.getString("sendusername","");
        if (id == R.id.newsid) {
            view.loadUrl(website_url);
        } else if (id == R.id.profileid) {
            view.loadUrl(website_url+"/"+name2);
        } else if (id == R.id.messageid) {
            view.loadUrl(website_url+"/messages");
        } else if (id == R.id.pageid) {
            view.loadUrl(website_url+"/pages");
        } else if (id == R.id.groupid) {
            view.loadUrl(website_url+"/groups");
        } else if (id == R.id.photoid) {
            view.loadUrl(website_url+"/albums");
        } else if (id == R.id.gameid) {
            view.loadUrl(website_url+"/games");
        } else if (id == R.id.savedpostsid) {
            view.loadUrl(website_url+"/saved-posts");
        } else if (id == R.id.shareid) {
            shareTextUrl();
        } else if (id == R.id.langid) {
            showlangDialog();
        } else if (id == R.id.settingsid) {
            view.loadUrl(website_url+"/setting");
        } else if (id == R.id.nav_switch) {
            switcher.setChecked(!switcher.isChecked());
        } else if (id == R.id.exitid) {
            view.loadUrl(website_url+"/logout");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(view.canGoBack()){
                view.goBack();
            }else{
                new AlertDialog.Builder(this)
                        .setMessage(R.string.cikisonay)
                        .setCancelable(false)
                        .setPositiveButton(R.string.evet, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.hayir, null)
                        .show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        view.onPause();
        view.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        view.resumeTimers();
        view.onResume();
    }

    @Override
    protected void onDestroy() {
        view.destroy();
        view = null;
        super.onDestroy();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void  refresh() {
        createDialog();
        Notify.show();
    }

    public void createDialog() {
        Notify = new AlertDialog.Builder(this);
        Notify.setTitle(R.string.noconnection);
        Notify.setPositiveButton(R.string.yenile,
                new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, int which) {
                        if (isNetworkAvailable()) {
                            view.setVisibility(View.VISIBLE);
                            view.reload();
                            Thread timer = new Thread() {
                                public void run() {
                                    try {
                                        sleep(5000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } finally {
                                        dialog.dismiss();
                                    }
                                }
                            };
                            timer.start();

                        } else {
                            refresh();
                        }
                    }

                });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        // Allow use of Local Storage
        settings.setDomStorageEnabled(true);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.setWebViewClient(new WebViewClient());

        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                        mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(MainActivity.this,
                        Environment.DIRECTORY_DOWNLOADS,".pdf");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), R.string.download,
                        Toast.LENGTH_LONG).show();
            }});
    }

    String bannerid, banneronoff, nbottom, interstitialid, interstitialonoff,slide;
    private void getData() {

        String url = website_url+"/mobileapp/getData.php?id=app_navigation_and&id2=app_slide_and&id3=banneron_and&id4=bannerid_and&id5=interstitialon_and&id6=interstitialid_and";
        if(!url.isEmpty()){
            bannerid = "";
            banneronoff = "0";
            interstitialid = "";
            interstitialonoff = "0";
            nbottom = "0";
            slide = "0";
        }
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject collegeData = result.getJSONObject(0);
            nbottom = collegeData.getString("nbottom");
            slide = collegeData.getString("slide");
            banneronoff = collegeData.getString("banneronoff");
            bannerid = collegeData.getString("bannerid");
            interstitialonoff = collegeData.getString("interstitialonoff");
            interstitialid = collegeData.getString("interstitialid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        interstitialAd.setAdUnitId(interstitialid);
        if (interstitialonoff.equals("1")) {
            interstitialAd.loadAd(adRequestinst);
        } else {
        }

        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(bannerid);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        if (banneronoff.equals("0")) {
            mAdView.setVisibility(View.GONE);
        } else {
            mAdView.setVisibility(View.VISIBLE);

            SoftKeyboardLsnedRelativeLayout layout = (SoftKeyboardLsnedRelativeLayout) findViewById(R.id.content_main);
            layout.addSoftKeyboardLsner(new SoftKeyboardLsnedRelativeLayout.SoftKeyboardLsner() {
                LinearLayout ssone = (LinearLayout) findViewById(R.id.teest);
                @Override
                public void onSoftKeyboardShow() {
                    mAdView.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ssone.getLayoutParams();
                    lp.height = 0;
                }

                @Override
                public void onSoftKeyboardHide() {
                    mAdView.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ssone.getLayoutParams();
                    lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                }
            });
        }

        if (nbottom.equals("0")) {
            navigation.setVisibility(View.GONE);
            one.setVisibility(View.GONE);
            if (slide.equals("0")) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
            }
        } else {
            navigation.setVisibility(View.VISIBLE);
            one.setVisibility(View.VISIBLE);

            SoftKeyboardLsnedRelativeLayout layout = (SoftKeyboardLsnedRelativeLayout) findViewById(R.id.content_main);
            layout.addSoftKeyboardLsner(new SoftKeyboardLsnedRelativeLayout.SoftKeyboardLsner() {
                LinearLayout ssone = (LinearLayout) findViewById(R.id.teest);
                @Override
                public void onSoftKeyboardShow() {
                    navigation.setVisibility(View.INVISIBLE);
                    one.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ssone.getLayoutParams();
                    lp.height = 0;
                }

                @Override
                public void onSoftKeyboardHide() {
                    navigation.setVisibility(View.VISIBLE);
                    one.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ssone.getLayoutParams();
                    lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                }
            });
        }
        //navigation.setVisibility(View.GONE);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            SharedPreferences getname = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String name2 = getname.getString("sendusername","");
            switch (item.getItemId()) {
                case R.id.navigation_drawer:
                    drawer.openDrawer(Gravity.START);
                    return true;
                case R.id.navigation_home:
                    view.loadUrl(website_url);
                    return true;
                case R.id.navigation_dashboard:
                    view.loadUrl(website_url+"/messages");
                    return true;
                case R.id.navigation_notifications:
                    view.loadUrl(website_url+"/"+name2);
                    return true;
            }
            return false;
        }
    };

    public void showlangDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.languages);
        builder.setSingleChoiceItems(langname, 11,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            alfabe = "ar";
                            urlalfabe = "arabic";
                        } else if (which == 1) {
                            alfabe = "de";
                            urlalfabe = "german";
                        } else if (which == 2) {
                            alfabe = "en";
                            urlalfabe = "english";
                        } else if (which == 3) {
                            alfabe = "es";
                            urlalfabe = "spanish";
                        } else if (which == 4) {
                            alfabe = "fr";
                            urlalfabe = "french";
                        } else if (which == 5) {
                            alfabe = "it";
                            urlalfabe = "italian";
                        } else if (which == 6) {
                            alfabe = "nl";
                            urlalfabe = "dutch";
                        } else if (which == 7) {
                            alfabe = "pt";
                            urlalfabe = "portuguese";
                        } else if (which == 8) {
                            alfabe = "ru";
                            urlalfabe = "russian";
                        } else if (which == 9) {
                            alfabe = "tr";
                            urlalfabe = "turkish";
                        }
                    }
                });

        String positiveText = getString(R.string.okey);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.loadUrl(website_url+"/?lang="+urlalfabe);
                        SharedPreferences sndlang = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sndlang.edit();
                        editor.putString("sendlang",alfabe);
                        editor.commit();
                        finish();
                        startActivity(getIntent());
                    }
                });

        String negativeText = getString(R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void shareTextUrl() {
        String appPackageName = getPackageName();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + appPackageName);

        startActivity(Intent.createChooser(share, getString(R.string.share)));
    }

    @SuppressLint("NewApi")
    protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");

        if (allowMultiple) {
            if (Build.VERSION.SDK_INT >= 18) {
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent[] intentArray;
        if(takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, i);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, mRequestCodeFilePicker);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == mRequestCodeFilePicker) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    }
                    else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris = null;

                        try {
                            if (intent.getDataString() != null) {
                                dataUris = new Uri[] { Uri.parse(intent.getDataString()) };
                            }
                            else {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    if (intent.getClipData() != null) {
                                        final int numSelectedFiles = intent.getClipData().getItemCount();

                                        dataUris = new Uri[numSelectedFiles];

                                        for (int i = 0; i < numSelectedFiles; i++) {
                                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                        }
                                    }
                                }
                            }
                        }
                        catch (Exception ignored) { }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            }
            else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                }
                else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
                    return  false;
                }
            }
        }
        return true;
    }

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private class sndtkn extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(app_server_url);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("fcm_a", params[0])
                        .appendQueryParameter("fcm_token", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();
                return "exception";

            } catch (IOException e) {
                return "exception";
            }
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}