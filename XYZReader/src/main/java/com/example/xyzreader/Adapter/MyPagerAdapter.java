package com.example.xyzreader.Adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.ui.ArticleDetailFragment;

public class MyPagerAdapter extends FragmentStatePagerAdapter {


    private Cursor mCursor;
    private String transitionName;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        ArticleDetailFragment fragment = (ArticleDetailFragment) object;
        //mediaSharedElementCallback.setSharedElementViews();
    }

    @Override
    public Fragment getItem(int position) {
        mCursor.moveToPosition(position);
        return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID),transitionName);
    }

    @Override
    public int getCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }

    public void setmCursor(Cursor mCursor) {
        this.mCursor = mCursor;
    }
}
