package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import static cz.muni.fi.pv256.movio2.uco_422476.App.BACKDROP_URL;
import static cz.muni.fi.pv256.movio2.uco_422476.App.COVER_URL;

/**
 * Created by Matej on 3.11.2017.
 */

public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();
    private static final String ARGS_FILM = "args_film";


    private Context mContext;
    private Film mFilm;
    private SQLiteDatabase mDatabase;
    private FilmManager mFilmManager;
    private FilmDbHelper mDbHelper;
    private FloatingActionButton favoriteButton;

    public static DetailFragment newInstance(Film film) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGS_FILM, film);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Bundle args = getArguments();
        if (args != null) {
            mFilm = args.getParcelable(ARGS_FILM);
        }
        mDbHelper = new FilmDbHelper(getActivity());
        mDatabase = mDbHelper.getWritableDatabase();
        mFilmManager = new FilmManager(mDatabase);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView titleTv = (TextView) view.findViewById(R.id.detail_film);
        TextView titleLowTv = (TextView) view.findViewById(R.id.detail_film_low);
        TextView popularityTv = (TextView) view.findViewById(R.id.detail_popularity);
        TextView descTv = (TextView) view.findViewById(R.id.detail_film_desc);
        ImageView coverIv = (ImageView) view.findViewById(R.id.detail_cover);
        ImageView backdropIv = (ImageView) view.findViewById(R.id.detail_backdrop);
        if (mFilm != null) {
            setVisibility(view, View.VISIBLE);
            titleTv.setText(mFilm.getTitle());
            java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
            String dateString = dateFormat.format(mFilm.getReleaseDate());
            titleLowTv.setText(dateString);
            popularityTv.setText(String.valueOf(mFilm.getPopularity()));
            descTv.setText(mFilm.getDescription());
            Picasso.with(mContext).load(String.format(COVER_URL, mFilm.getCoverPath())).into(coverIv);
            Picasso.with(mContext).load(String.format(BACKDROP_URL, mFilm.getBackdrop())).into(backdropIv);
            setFavoriteButtonListener(view);
        }
        else {
            setVisibility(view, View.INVISIBLE);
        }
        return view;
    }

    private void setVisibility(View view, int visible) {
        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.favorite);
        TextView overview = (TextView) view.findViewById(R.id.overview);
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        button.setVisibility(visible);
        overview.setVisibility(visible);
        rl.setVisibility(visible);
    }

    private void setFavoriteButtonListener(View view){
        favoriteButton = (FloatingActionButton) view.findViewById(R.id.favorite);
        if (mFilmManager.containsId(mFilm.getId())) {
            favoriteButton.setImageResource(R.mipmap.ic_remove);
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFilmManager.containsId(mFilm.getId())) {
                    mFilmManager.deleteFilm(mFilm);
                    Toast.makeText(getActivity(), mFilm.getTitle() + " " + getResources().getString(R.string.favoriteRemoved), Toast.LENGTH_SHORT).show();
                    favoriteButton.setImageResource(R.mipmap.ic_add);
                }
                else {
                    mFilmManager.createFilm(mFilm);
                    Toast.makeText(getActivity(), mFilm.getTitle() + " " + getResources().getString(R.string.favoriteAdded), Toast.LENGTH_SHORT).show();
                    favoriteButton.setImageResource(R.mipmap.ic_remove);
                }
                if (getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_main) != null) {
                    ((MainFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_main)).updateData();
                }
            }
        });
    }
}