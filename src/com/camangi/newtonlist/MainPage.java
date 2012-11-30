package com.camangi.newtonlist;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Bitmap;


public class MainPage extends Activity {
    /** Called when the activity is first created. */
	WebView mWebView1;
	boolean isError = false;
	boolean isMonitor = false;
	String mainURL;
	BroadcastReceiver mConnectionReceiver = new ConnectionMonitor();
	/*
	public class JSInterface
	{
		 public void callApp(String uriString) {
			ComponentName componentName =
				 new ComponentName("com.camangi.btvmarket", "com.camangi.btvmarket.Login");
	        Uri _uri = Uri.parse(uriString);
	        Intent intent = new Intent(Intent.ACTION_VIEW, _uri);
	        intent.setComponent(componentName);
			startActivity(intent);
		 }
	}
	*/
	public void callApp(String uriString) {
		ComponentName componentName =
			 new ComponentName("com.camangi.btvmarket", "com.camangi.btvmarket.Login");
		Uri _uri = Uri.parse(uriString);
		Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(_uri.getHost());
		if (LaunchIntent == null) {
	        Intent intent = new Intent(Intent.ACTION_VIEW, _uri);
	        intent.setComponent(componentName);
			startActivity(intent);
		} else {
			startActivity(LaunchIntent);
		}
	 }
	
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mWebView1 = (WebView) findViewById(R.id.webview1);
        mWebView1.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView1.getSettings().setJavaScriptEnabled(true);
        mWebView1.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView1.clearCache(true);
        
        mainURL = "file:///android_asset/local/index.html";

        mWebView1.setWebViewClient(new WebViewClient(){  
        ProgressDialog d=new ProgressDialog(MainPage.this);
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) { 
            	if (d != null) {
            		d.setTitle(getString(R.string.loading));
            		d.setMessage(getString(R.string.wait));
            		d.show(); 
            	}
            }
          
            @Override
            public void onPageFinished(WebView view, String url) {
            	String title = null;
            	if (view != null)
            		title = view.getTitle();
            	if ( title != null && (title.compareTo("Error")!=0 
            			&& title.compareTo("Web page not available")!=0) 
            			&& isError ) {
            		isError = false;
            		view.clearHistory();
            	}
            	
            	if (d != null)
            		d.dismiss();

            	super.onPageFinished(view, url);
            }
        	@Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        	@Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        		mWebView1.loadUrl("file:///android_asset/html_no_copy/error.html");
        		isError = true;
                super.onReceivedError(view, errorCode, description, failingUrl);
        	}
        });
        
        
        //mWebView1.addJavascriptInterface(new JSInterface(), "StoryList");
        mWebView1.setWebChromeClient(new WebChromeClient() {

    		public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)  
    		{
	    		  //Toast.makeText(MainPage.this, message, Toast.LENGTH_LONG).show();
	    		  result.confirm();
	    		  callApp(message);
	    		  return true;
    		};
    	});

        if (savedInstanceState != null)
        	mWebView1.restoreState(savedInstanceState);
        else
        	mWebView1.loadUrl(mainURL);

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView1.canGoBack()) { 
    		if ((mWebView1.getTitle().compareTo("Error")!=0 
        			&& mWebView1.getTitle().compareTo("Web page not available")!=0))
    		{
    			mWebView1.goBack(); 
    			return true; 
    		}
    		else {
    			mWebView1.destroy();
    		}
    	} 
    	
    	return super.onKeyDown(keyCode, event); 
    }
    protected void onSaveInstanceState(Bundle outState) {
    	mWebView1.saveState(outState);
    }
    
    private class ConnectionMonitor extends BroadcastReceiver {
    	

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                return;
            }

            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false); 
            NetworkInfo aNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);                     

            if (!noConnectivity)
            {
                if ((aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) ||
                    (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI))
                {
                   	mWebView1.loadUrl(mainURL);
                }
            }
            else
            {
                if ((aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) ||
                        (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI))
                {
                    //Handle disconnected case
                }
            }
        }
    }

}