package com.alaa.deutschbrowser;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends Activity {
    private WebView web;
    private EditText address;
    private boolean filterNoSubs = true;
    private Button filterButton;
    private final int bg = Color.rgb(9,11,19), panel = Color.rgb(22,25,39), purple = Color.rgb(179,77,255), white = Color.WHITE;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(bg);
        LinearLayout top = new LinearLayout(this); top.setPadding(8,8,8,8); top.setBackgroundColor(panel); top.setOrientation(LinearLayout.HORIZONTAL);
        address = new EditText(this); address.setSingleLine(true); address.setTextColor(white); address.setHintTextColor(0xFF9AA3B8); address.setHint("ابحث بالألماني أو اكتب رابط فيديو"); address.setTextSize(14); address.setPadding(12,0,12,0); address.setBackgroundColor(0xFF30354B); address.setImeOptions(EditorInfo.IME_ACTION_GO);
        Button go = button("اذهب"); top.addView(address,new LinearLayout.LayoutParams(0,dp(46),1)); top.addView(go,new LinearLayout.LayoutParams(dp(64),dp(46))); root.addView(top);
        LinearLayout quick = new LinearLayout(this); quick.setPadding(6,5,6,5); quick.setBackgroundColor(bg);
        Button yt = button("YouTube"); filterButton = button("فلترة: بدون ترجمة مخفي"); Button home = button("الرئيسية");
        quick.addView(yt,new LinearLayout.LayoutParams(0,dp(42),1)); quick.addView(filterButton,new LinearLayout.LayoutParams(0,dp(42),2)); quick.addView(home,new LinearLayout.LayoutParams(0,dp(42),1)); root.addView(quick);
        web = new WebView(this); web.setBackgroundColor(Color.BLACK); WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setDatabaseEnabled(true); s.setMediaPlaybackRequiresUserGesture(false); s.setSupportZoom(true); s.setBuiltInZoomControls(true); s.setDisplayZoomControls(false); s.setLoadWithOverviewMode(true); s.setUseWideViewPort(true); s.setUserAgentString(s.getUserAgentString().replace("; wv)", ")"));
        web.setWebChromeClient(new WebChromeClient()); web.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){ return false; }
            @Override public void onPageFinished(WebView view,String url){ address.setText(url); injectTools(); }
        });
        root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout bottom = new LinearLayout(this); bottom.setPadding(5,4,5,4); bottom.setBackgroundColor(panel);
        Button back=button("‹ رجوع"), forward=button("تقدم ›"), reload=button("تحديث"), subtitle=button("SRT/VTT");
        bottom.addView(back,new LinearLayout.LayoutParams(0,dp(44),1)); bottom.addView(forward,new LinearLayout.LayoutParams(0,dp(44),1)); bottom.addView(reload,new LinearLayout.LayoutParams(0,dp(44),1)); bottom.addView(subtitle,new LinearLayout.LayoutParams(0,dp(44),1)); root.addView(bottom);
        setContentView(root);
        go.setOnClickListener(v->navigate()); address.setOnEditorActionListener((v,action,event)->{if(action==EditorInfo.IME_ACTION_GO||action==EditorInfo.IME_ACTION_DONE){navigate();return true;}return false;});
        yt.setOnClickListener(v->web.loadUrl("https://m.youtube.com/") ); home.setOnClickListener(v->web.loadUrl("https://m.youtube.com/") );
        back.setOnClickListener(v->{if(web.canGoBack())web.goBack();}); forward.setOnClickListener(v->{if(web.canGoForward())web.goForward();}); reload.setOnClickListener(v->web.reload());
        filterButton.setOnClickListener(v->{filterNoSubs=!filterNoSubs;filterButton.setText(filterNoSubs?"فلترة: بدون ترجمة مخفي":"الفلترة متوقفة");injectTools();});
        subtitle.setOnClickListener(v->Toast.makeText(this,"لإضافة ترجمة يدويًا، افتح ملف SRT/VTT من صفحة التدريب أو استخدم موقعًا يدعم اختيار ملف ترجمة. اختيار الملفات من داخل المشغل سيُضاف في إصدار لاحق.",Toast.LENGTH_LONG).show());
        web.loadUrl("https://m.youtube.com/");
    }
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    private Button button(String text){Button b=new Button(this);b.setText(text);b.setTextColor(white);b.setTextSize(11);b.setAllCaps(false);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF34314D));return b;}
    private void navigate(){String q=address.getText().toString().trim();if(q.isEmpty())return;String url;if(q.matches("(?i)^https?://.*"))url=q;else if(q.contains(".")&&!q.contains(" "))url="https://"+q;else url="https://m.youtube.com/results?search_query="+Uri.encode(q);((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(address.getWindowToken(),0);web.loadUrl(url);}
    private void injectTools(){
        String js="(function(){try{"+
          "if(window.__alaaDeutschTimer)clearInterval(window.__alaaDeutschTimer);"+
          "var FILTER="+filterNoSubs+";"+
          "function filterResults(){if(!FILTER||!location.hostname.includes('youtube.com')||!location.pathname.includes('/results'))return;"+
          "document.querySelectorAll('ytm-video-with-context-renderer, ytm-compact-video-renderer, ytd-video-renderer, ytd-rich-item-renderer').forEach(function(card){"+
          "if(card.dataset.alaaChecked==='1')return;card.dataset.alaaChecked='1';var txt=(card.innerText||'').toLowerCase();var hasCC=!!card.querySelector('[aria-label*=\\\"caption\\\" i],[aria-label*=\\\"subtitle\\\" i],.badge-style-type-字幕, .yt-badge, ytd-badge-supported-renderer');"+
          "var badges=Array.from(card.querySelectorAll('span,div,[aria-label]')).some(function(x){var t=((x.innerText||'')+' '+(x.getAttribute('aria-label')||'')).toLowerCase().trim();return t==='cc'||t.includes('subtitles')||t.includes('closed captions')||t.includes('untertitel')||t.includes('untertitel verfügbar')||t.includes('captions');});"+
          "if(!hasCC&&!badges){card.style.display='none';card.dataset.alaaHidden='1';}"+
          "});}"+
          "function setupPlayer(){var v=document.querySelector('video');if(!v)return;var old=document.getElementById('alaa-shadow-mini');if(old)return;"+
          "var box=document.createElement('div');box.id='alaa-shadow-mini';box.style.cssText='position:fixed;z-index:2147483647;bottom:82px;left:8px;right:8px;background:rgba(12,14,28,.94);color:white;border:1px solid #a64dff;border-radius:14px;padding:9px;display:flex;gap:7px;align-items:center;font:13px sans-serif;box-shadow:0 3px 20px #0008';"+
          "var label=document.createElement('div');label.textContent='Alaa Deutsch · '+(v.textTracks&&v.textTracks.length?'ترجمة مكتشفة':'ابحث عن CC / Subtitles');label.style.cssText='flex:1;overflow:hidden';"+
          "function btn(t,fn){var b=document.createElement('button');b.textContent=t;b.style.cssText='border:0;border-radius:9px;padding:9px;background:#8e39d6;color:#fff;font-weight:bold';b.onclick=fn;box.appendChild(b);}box.appendChild(label);btn('▶/Ⅱ',function(){v.paused?v.play():v.pause()});btn('−5ث',function(){v.currentTime=Math.max(0,v.currentTime-5)});btn('+5ث',function(){v.currentTime=Math.min(v.duration||1,v.currentTime+5)});btn('إخفاء',function(){box.style.display='none'});document.body.appendChild(box);"+
          "var tracks=v.textTracks;for(var i=0;i<tracks.length;i++){if(tracks[i].mode==='disabled')tracks[i].mode='hidden';}"+
          "}filterResults();setupPlayer();}catch(e){}};window.__alaaDeutschTimer=setInterval(function(){try{var c=document.getElementById('alaa-shadow-mini');if(!c)setupPlayer();filterResults();}catch(e){}},1800);"+
          "})();";
        web.evaluateJavascript(js,null);
    }
    @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
}
