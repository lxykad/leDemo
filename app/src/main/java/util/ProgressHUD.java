package util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.user.ble.R;


public class ProgressHUD extends ProgressDialog {
    
    static ProgressHUD mDialog = null;
    public TextView mMsgTextView;
    public RelativeLayout mLoadingLayout;
    public static String mMsg;

    private ProgressHUD(Context context) {
        super(context, R.style.ProgressHUD);
        this.setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.progress_hud);
        
        mMsgTextView = (TextView)findViewById(R.id.tips_msg);
        mLoadingLayout = (RelativeLayout)findViewById(R.id.tips_loading);

        setOnKeyListener(keylistener);
    }
    
    static public void show(Context context, final String message) {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

    	mDialog = new ProgressHUD(context);
    	mDialog.show();
        //mDialog.oncreateDialog();
    	
    	mDialog.mMsgTextView.setText(message);
    	mDialog.mMsgTextView.setVisibility(View.VISIBLE);
    	mDialog.mLoadingLayout.setVisibility(View.GONE);

    	new Handler().postDelayed(new Runnable(){
    	    public void run() {
                hidden();
    	    }
    	 }, 1000);
    }
    
    static public void showLoding(Context context) {

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }


    	mDialog = new ProgressHUD(context);    	
    	mDialog.show();
    	
    	mDialog.mMsgTextView.setVisibility(View.GONE);
    	mDialog.mLoadingLayout.setVisibility(View.VISIBLE);
    }
    
    static public void hidden() {
    	if (mDialog != null && mDialog.isShowing()) {
    		mDialog.dismiss();
    	}
    }
    
	OnKeyListener keylistener = new OnKeyListener(){

		@Override
		public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
			if (arg1 == KeyEvent.KEYCODE_BACK)
            {
                //dialog.dismiss();
                return true;
            }
			return false;
		}
    } ;

    
    /*
    public LoadingDialog(Context context, String message) {
        super(context);
        this.message = message;
        this.setCancelable(false);
    }

    public LoadingDialog(Context context, int theme, String message) {
        super(context, theme);
        this.message = message;
        this.setCancelable(false);
    }
    */
}
