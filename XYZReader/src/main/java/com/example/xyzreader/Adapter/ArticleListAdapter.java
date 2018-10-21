package com.example.xyzreader.Adapter;


import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {

    private static final String TAG = ArticleListAdapter.class.getSimpleName();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    private Cursor mCursor;
    private Context mContext;
    private OnArticleClickListener mOnArticleClickListener;

    public interface OnArticleClickListener{
        public void onArticleClicked(View view, long itemId);
    }

    public ArticleListAdapter(OnArticleClickListener mOnArticleClickListener, Cursor cursor) {
        this.mOnArticleClickListener = mOnArticleClickListener;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));

        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {

            holder.subtitleView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + "<br/>" + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        } else {
            holder.subtitleView.setText(Html.fromHtml(
                    outputFormat.format(publishedDate)
                            + "<br/>" + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Setting different Transition Name for each view
            holder.thumbnailView.setTransitionName(mContext.getString(R.string.article_photo_transition_name) + position);
            Log.d("setTransitionName",mContext.getString(R.string.article_photo_transition_name) + position);
        }

        RequestOptions options = new RequestOptions()
                .error(R.drawable.empty_detail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(600, 300)
                .placeholder(R.drawable.empty_detail)
                .error(R.drawable.error_bk)
                .priority(Priority.HIGH);
        Glide.with(mContext)
                .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .into(holder.thumbnailView);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView thumbnailView;
        TextView titleView;
        TextView subtitleView;

        ViewHolder(View view) {
            super(view);

            thumbnailView =  view.findViewById(R.id.thumbnail);
            titleView =  view.findViewById(R.id.article_title);
            subtitleView =  view.findViewById(R.id.article_subtitle);
            thumbnailView.setOnClickListener(this);
            titleView.setOnClickListener(this);
            subtitleView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnArticleClickListener.onArticleClicked(view, getItemId());
        }


    }

}