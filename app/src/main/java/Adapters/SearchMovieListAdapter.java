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

import java.util.ArrayList;
import java.util.List;

import Interfaces.OnLoadMoreMoviesListener;
import Pojo.FoundMovie;

public class SearchMovieListAdapter extends RecyclerView.Adapter  {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

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

    public void addItem(FoundMovie foundMovie){
        foundMovieList.add(foundMovie);
        notifyItemInserted(foundMovieList.size());
    }

    public void clearList(){
        foundMovieList.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return foundMovieList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_list_search_movie, parent, false);

            vh = new FoundMovieViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar, parent, false);

            vh = new SearchMovieListAdapter.ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchMovieListAdapter.FoundMovieViewHolder) {

            FoundMovie foundMovie= foundMovieList.get(position);

            ((FoundMovieViewHolder) holder).tvTitle.setText(foundMovie.getTitle());
            ((FoundMovieViewHolder) holder).tvDate.setText(foundMovie.getReleaseDate());
            ((FoundMovieViewHolder) holder).tvOverview.setText(foundMovie.getOverview());

            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500/"+foundMovie.getPosterPath())
                    .into(((FoundMovieViewHolder) holder).ivMovie);

        } else {
            ((SearchMovieListAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public int getItemCount() {
        return foundMovieList.size();
    }

    public void setOnLoadMoreMoviesListener(OnLoadMoreMoviesListener onLoadMoreMoviesListener) {
        this.onLoadMoreMoviesListener = onLoadMoreMoviesListener;
    }


    //
    public static class FoundMovieViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public ImageView ivMovie;
        public TextView tvDate;
        public TextView tvOverview;

        public FoundMovieViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tv_titlefoundmovie);
            ivMovie=(ImageView) v.findViewById(R.id.iv_imagefoundmovie);
            tvDate = (TextView) v.findViewById(R.id.tv_yearfoundmovie);
            tvOverview = (TextView) v.findViewById(R.id.tv_overviewfoundmovie);

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
