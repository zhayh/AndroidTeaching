package edu.niit.android.photogallery.bing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

import edu.niit.android.photogallery.PhotoPageActivity;
import edu.niit.android.photogallery.PollService;
import edu.niit.android.photogallery.QueryPreferences;
import edu.niit.android.photogallery.R;
import edu.niit.android.photogallery.VisibleFragment;

public class PhotoGalleyFragment extends VisibleFragment {
    private static final String TAG = "PhothGalleyFragment";

    private RecyclerView mPhotoView;
    private List<PhotoItem> mItems = new ArrayList<>();

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

//        Intent intent = PollService.newIntent(getActivity());
//        getActivity().startService(intent);

//        PollService.setServiceAlarm(getActivity(), true);

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

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if(PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shoudStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shoudStartAlarm);
                getActivity().invalidateOptionsMenu();
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

    private class FetchItemsTask extends AsyncTask<Void, Void, List<PhotoItem>> {
        private String mQuery;

        public FetchItemsTask(String query) {
            this.mQuery = query;
        }

        @Override
        protected List<PhotoItem> doInBackground(Void... voids) {
//            try {
//                String result = new FlickrFetchr().getUrlString("https://www.bignerdranch.com");
//                Log.i(TAG, "Fetched contents of URL: " + result);
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e(TAG, "Failed to fetch URL: " + e);
//            }
//            String query = "robot";
            if(mQuery == null) {
                return new ShowApiFetch().fetchRecentPhotots();
            } else {
                return new ShowApiFetch().searchPhotos(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<PhotoItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
//        private TextView mTitleTextView;
        private ImageView mTitleImageView;
        private PhotoItem mGalleryItem;

        public PhotoHolder(View itemView) {
            super(itemView);
//            mTitleTextView = (TextView) itemView;
            mTitleImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_image_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 使用隐式Intent
//                    Intent intent = new Intent(Intent.ACTION_VIEW, mGalleryItem.getPhotoPageUri());
                    Intent intent = PhotoPageActivity.newIntent(getActivity(), mGalleryItem
                            .getPhotoPageUri());
                    startActivity(intent);
                }
            });
        }

//        public void bindGalleryItem(GalleryItem item) {
//            mTitleTextView.setText(item.toString());
//        }
        public void bindGalleryDrawable(Drawable drawable) {
            mTitleImageView.setImageDrawable(drawable);
        }

        public void bindGalleryItem(PhotoItem galleryItem) {
            mGalleryItem = galleryItem;
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<PhotoItem> mGalleryItems;

        public PhotoAdapter(List<PhotoItem> galleryItems) {
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
            PhotoItem item = mGalleryItems.get(position);
//            holder.bindGalleryItem(item);
            holder.bindGalleryItem(item);
            Drawable placeholder = getResources().getDrawable(R.mipmap.ic_launcher);
            holder.bindGalleryDrawable(placeholder);
            mThumbDownloader.queueThumb(holder, item.getPic());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
