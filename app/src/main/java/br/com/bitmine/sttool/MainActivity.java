package br.com.bitmine.sttool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import br.com.bitmine.sttool.dialog.AboutDialog;
import br.com.bitmine.sttool.entities.Magnitude;
import br.com.bitmine.sttool.fragments.BackKeyHandleFragment;
import br.com.bitmine.sttool.fragments.FinalStepFragment;
import br.com.bitmine.sttool.fragments.FirstStepFragment;
import br.com.bitmine.sttool.fragments.SecondStepFragment;
import br.com.bitmine.sttool.fragments.ThirdStepFragment;
import br.com.bitmine.sttool.utils.Constants;
import br.com.bitmine.sttool.utils.KeyboardUtils;

/**
 * This class represents the main activity.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class MainActivity extends AppCompatActivity {

    private Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        callFragment(Constants.FIRST_STEP_FRAGMENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_about){
            new AboutDialog(this).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Calls a fragment.
     * @param stepIndex - Fragment index.
     */
    public void callFragment(int stepIndex){
        callFragment(stepIndex, null, null);
    }

    /**
     * Calls a fragment.
     * @param stepIndex - Fragment index.
     * @param mag1 - Magnitude 1.
     * @param mag2 - Magnitude 2.
     */
    public void callFragment(int stepIndex, Magnitude mag1, Magnitude mag2){
        Fragment fragment;
        switch(stepIndex){
            default:
            case Constants.FIRST_STEP_FRAGMENT:
                fragment = FirstStepFragment.newInstance(mag1, mag2);
                break;

            case Constants.SECOND_STEP_FRAGMENT:
                fragment = SecondStepFragment.newInstance(mag1, mag2);
                break;

            case Constants.THIRD_STEP_FRAGMENT:
                fragment = ThirdStepFragment.newInstance(mag1, mag2);
                break;

            case Constants.FINAL_STEP_FRAGMENT:
                fragment = FinalStepFragment.newInstance(mag1, mag2);
                break;

        }

        KeyboardUtils.showKeyboard(this, false);
        if(currentFragment == null)
            getSupportFragmentManager().beginTransaction().add(R.id.main_content, fragment).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();

        currentFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        if(currentFragment instanceof BackKeyHandleFragment)
            ((BackKeyHandleFragment) currentFragment).onBackPressed();
        else
            super.onBackPressed();
    }
}
