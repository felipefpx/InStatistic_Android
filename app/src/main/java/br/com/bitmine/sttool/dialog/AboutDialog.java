package br.com.bitmine.sttool.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

import br.com.bitmine.sttool.R;


/**
 * This class represents the About Dialog.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class AboutDialog {

    private Dialog dialog;

    public AboutDialog(Context context){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about);

        TextView version = (TextView) dialog.findViewById(R.id.about_version);
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version.setText(context.getString(R.string.app_name) + " v" + pInfo.versionName);
        }catch(Exception e){}

        TextView description = (TextView) dialog.findViewById(R.id.about_description);
        description.setText(Html.fromHtml(context.getString(R.string.about_description)));
//        description.setMovementMethod(new LinkMovementMethod());

    }

    /**
     * Shows about dialog.
     */
    public void show(){
        if(dialog != null && !dialog.isShowing())
            dialog.show();
    }


    /**
     * Closes about dialog.
     */
    public void hide(){
        if(dialog != null && dialog.isShowing()){
            dialog.hide();
        }
    }
}
