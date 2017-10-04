package edu.niit.android.photogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleyFragment extends Fragment {
    private static final String TAG = "PhothGalleyFragment";

    private RecyclerView mPhotoView;
    private List<GalleryItem> mItems = new ArrayList<>();

    private ThumbDownloader<PhotoHolder> mThumbDownloader;

    public static PhotoGalleyFragment newInstance() {
        return new PhotoGalleyFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // 让fragment在activity重建时保持实例不变
        setHasOptionsMenu(true);
//        new FetchItemsTask().execute();

        updateItems();

        Handler responseHandler = new Handler();
        mThumbDownloader = new ThumbDownloader<>(responseHandler);
        mThumbDownloader.setThumbDownloaderListener(new ThumbDownloader.ThumbDownloaderListener<PhotoHolder>() {
            @Override
            public void onThumbDownloader(PhotoHolder target, Bitmap thumb) {
                Drawable drawable = new BitmapDrawable(getResources(), thumb);
                target.bindGalleryDrawable(drawable);
            }
        });
        mThumbDownloader.start();
        mThumbDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_photo_galley, container, false);

        mPhotoView = (RecyclerView) view.findViewById(R.id.photo_recycler_view);
        mPhotoView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbDownloader.clearQueue();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSumbit: " + query);
                updateItems();
                QueryPreferences.setStoredQuery(getActivity(), query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = QueryPreferences.getStoreQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems() {
        String query = QueryPreferences.getStoreQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    private void setupAdapter() {
        if(isAdded()) {
            mPhotoView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        private String mQuery;

        public FetchItemsTask(String query) {
            this.mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
//            try {
//                String result = new FlickrFetchr().getUrlString("https://www.bignerdranch.com");
//                Log.i(TAG, "Fetched contents of URL: " + result);
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e(TAG, "Failed to fetch URL: " + e);
//            }
//            String query = "robot";
            if(mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotots();
            } else {
                return new FlickrFetchr().searchPhotos(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
//        private TextView mTitleTextView;
        private ImageView mTitleImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
//            mTitleTextView = (TextView) itemView;
            mTitleImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_image_view);
        }

//        public void bindGalleryItem(GalleryItem item) {
//            mTitleTextView.setText(item.toString());
//        }
        public void bindGalleryDrawable(Drawable drawable) {
            mTitleImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            this.mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            TextView textView = new TextView(getActivity());
//            return new PhotoHolder(textView);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem item = mGalleryItems.get(position);
//            holder.bindGalleryItem(item);
            Drawable placeholder = getResources().getDrawable(R.mipmap.ic_launcher);
            holder.bindGalleryDrawable(placeholder);
            mThumbDownloader.queueThumb(holder, item.getUrl_s());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
