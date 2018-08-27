package com.example.android.movies.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.android.movies.listeners.MovieCRUDListenerAdapter;
import com.example.android.movies.models.Movie;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private MovieDao mMovieDao;
    private LiveData<List<Movie>> mAllMovies;

    public MovieViewModel(Application application) {
        super(application);
        MovieDatabase db = MovieDatabase.getDatabase(application);
        mMovieDao = db.getMovieDao();
        mAllMovies = mMovieDao.getFavoriteMovies();
    }

    public LiveData<List<Movie>> getAllMovies() {
        return mAllMovies;
    }

    public GetMovieByIDAsyncTask getMovieByID(){
        return new GetMovieByIDAsyncTask(mMovieDao);
    }

    public MovieAsyncTask insert() {
        return new MovieAsyncTask(mMovieDao, 'I');
    }

    public MovieAsyncTask delete() {
        return new MovieAsyncTask(mMovieDao, 'D');
    }



    public static class MovieAsyncTask extends AsyncTask<Movie, Void, Void> {
        private char action;
        private MovieDao mAsyncTaskDao;
        private MovieCRUDListenerAdapter mListener;

        MovieAsyncTask(MovieDao dao, char action) {
            mAsyncTaskDao = dao;
            this.action = action;
        }

        public void setListener(MovieCRUDListenerAdapter listener) {
            mListener = listener;
        }

        @Override
        protected Void doInBackground(final Movie... params) {
            switch(action){
                case 'I':
                    mAsyncTaskDao.insert(params[0]);
                    break;
                case 'D':
                    mAsyncTaskDao.delete(params[0]);
                    break;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if (mListener != null){
                mListener.onPostExecuteConcluded();
            }
        }
    }


    public static class GetMovieByIDAsyncTask extends AsyncTask<Long, Void, Movie> {

        private MovieCRUDListenerAdapter mListener;
        private MovieDao mAsyncTaskDao;


        public void setListener(MovieCRUDListenerAdapter listener) {
            mListener = listener;
        }



        private GetMovieByIDAsyncTask(MovieDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Movie doInBackground(Long... params) {
            return mAsyncTaskDao.getMovieByID(params[0]);
        }

        @Override
        final protected void onPostExecute(Movie movie) {
            if (mListener != null){
                mListener.onPostExecuteConcluded(movie);
            }
        }
    }
}
