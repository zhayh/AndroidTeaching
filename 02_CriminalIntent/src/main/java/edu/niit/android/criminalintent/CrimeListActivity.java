package edu.niit.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by zhayh on 2017-9-1.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_twopane;
    }
}
