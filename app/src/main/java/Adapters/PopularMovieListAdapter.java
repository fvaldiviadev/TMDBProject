package Adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fvaldiviadev.tmdb_project.R;

import java.util.List;

import Interfaces.OnLoadMoreMoviesListener;
import Pojo.PopularMovie;
import Utils.Constants;

public class PopularMovieListAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<PopularMovie> popularMovieList;

    // The minimum amount of items to have below your current scroll position
// before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreMoviesListener onLoadMoreMoviesListener;


    public PopularMovieListAdapter(List<PopularMovie> popularMovies, RecyclerView recyclerView) {
        popularMovieList = popularMovies;

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
                                // End has been reached
                                if (onLoadMoreMoviesListener != null) {
                                    onLoadMoreMoviesListener.onLoadMoreMovies();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    public void addItem(PopularMovie popularMovie) {
        popularMovieList.add(popularMovie);
        notifyItemInserted(popularMovieList.size());
    }

    @Override
    public int getItemViewType(int position) {
        return popularMovieList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_list_popular_movie, parent, false);

            vh = new PopularMovieViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PopularMovieViewHolder) {

            PopularMovie popularMovie = (PopularMovie) popularMovieList.get(position);

            ((PopularMovieViewHolder) holder).tvTitle.setText(popularMovie.getTitle());

            Glide.with(holder.itemView.getContext())
                    .load(Constants.URL_IMAGE_DEFAULT + popularMovie.getPosterPath())
                    .into(((PopularMovieViewHolder) holder).ivMovie);

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public int getItemCount() {
        return popularMovieList.size();
    }

    public void setOnLoadMoreMoviesListener(OnLoadMoreMoviesListener onLoadMoreMoviesListener) {
        this.onLoadMoreMoviesListener = onLoadMoreMoviesListener;
    }


    public static class PopularMovieViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public ImageView ivMovie;

        public PopularMovieViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tv_titlepopularmovie);
            ivMovie = (ImageView) v.findViewById(R.id.iv_imagepopularmovie);

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                }
            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.pb_popularmovielist);
        }
    }


}
