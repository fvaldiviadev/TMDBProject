package Adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fvaldiviadev.tmdb_project.R;

import java.util.ArrayList;
import java.util.List;

import Interfaces.OnLoadMoreMoviesListener;
import Pojo.FoundMovie;
import Utils.Constants;

public class SearchMovieListAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;

    private List<FoundMovie> foundMovieList;

    // The minimum amount of items to have below your current scroll position
// before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreMoviesListener onLoadMoreMoviesListener;


    public SearchMovieListAdapter(RecyclerView recyclerView) {
        foundMovieList = new ArrayList<FoundMovie>();

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                                if (onLoadMoreMoviesListener != null) {
                                    onLoadMoreMoviesListener.onLoadMoreMovies();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    public void addItem(FoundMovie foundMovie) {
        foundMovieList.add(foundMovie);
        notifyItemInserted(foundMovieList.size());
    }

    public void clearList() {
        foundMovieList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return foundMovieList.get(position) != null ? VIEW_ITEM : null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_list_search_movie, parent, false);

            vh = new FoundMovieViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchMovieListAdapter.FoundMovieViewHolder) {

            FoundMovie foundMovie = foundMovieList.get(position);

            ((FoundMovieViewHolder) holder).tvTitle.setText(foundMovie.getTitle());
            String dateString = foundMovie.getReleaseDate();
            String year = "-";
            if (dateString != null && dateString.length() > 4) {
                year = dateString.substring(0, 4);
            }
            ((FoundMovieViewHolder) holder).tvDate.setText(year);
            ((FoundMovieViewHolder) holder).tvOverview.setText(foundMovie.getOverview());

            Glide.with(holder.itemView.getContext())
                    .load(Constants.URL_IMAGE_DEFAULT + foundMovie.getPosterPath())
                    .into(((FoundMovieViewHolder) holder).ivMovie);

        }
    }

    @Override
    public int getItemCount() {
        return foundMovieList.size();
    }

    public void setOnLoadMoreMoviesListener(OnLoadMoreMoviesListener onLoadMoreMoviesListener) {
        this.onLoadMoreMoviesListener = onLoadMoreMoviesListener;
    }


    public static class FoundMovieViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public ImageView ivMovie;
        public TextView tvDate;
        public TextView tvOverview;

        public FoundMovieViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tv_titlefoundmovie);
            ivMovie = (ImageView) v.findViewById(R.id.iv_imagefoundmovie);
            tvDate = (TextView) v.findViewById(R.id.tv_yearfoundmovie);
            tvOverview = (TextView) v.findViewById(R.id.tv_overviewfoundmovie);

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                }
            });
        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}
