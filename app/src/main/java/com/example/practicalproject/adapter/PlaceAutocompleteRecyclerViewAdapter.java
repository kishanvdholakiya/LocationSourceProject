package com.example.practicalproject.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicalproject.R;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlaceAutocompleteRecyclerViewAdapter extends RecyclerView.Adapter<PlaceAutocompleteRecyclerViewAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "PlaceAutocomplete";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.NORMAL);

    /**
     * Current results returned by this adapter.
     */
    public ArrayList<AutocompletePrediction> mResultList = new ArrayList<>();

    /**
     * Handles autocomplete requests.
     */
    private PlacesClient mGeoDataClient;

    /**
     * The bounds used for Places Geo Data autocomplete API requests.
     */
    private LatLngBounds mBounds;

    /**
     * The autocomplete filter used to restrict queries to a specific set of place types.
     */
    // private AutocompleteFilter mPlaceFilter;
    private Context context;
    public OnItemClickListener onItemClickListener;

    AutocompleteSessionToken token;

    public PlaceAutocompleteRecyclerViewAdapter(Context context, PlacesClient geoDataClient/*, LatLngBounds bounds, AutocompleteFilter filter*/) {

        token = AutocompleteSessionToken.newInstance();

        this.context = context;
        mGeoDataClient = geoDataClient;
        mBounds = null;
        //mPlaceFilter = null;
    }

    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }


    private AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults results = new FilterResults();

                // We need a separate list to store the results, since
                // this is run asynchronously.
                ArrayList<AutocompletePrediction> filterData = new ArrayList<>();

                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    Log.d("callData","before");
                    filterData = getAutocomplete(constraint);
                }

                results.values = filterData;
                if (filterData != null) {
                    results.count = filterData.size();
                } else {
                    results.count = 0;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    mResultList = (ArrayList<AutocompletePrediction>) results.values;
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetChanged();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }


    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
        Log.i(TAG, "Starting autocomplete query for: " + constraint);


        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build();


        Task<FindAutocompletePredictionsResponse> results = mGeoDataClient.findAutocompletePredictions(request);


        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(results, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        try {
            FindAutocompletePredictionsResponse autocompletePredictions = results.getResult();

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getAutocompletePredictions().size()
                    + " predictions.");

            // Freeze the results immutable representation that can be stored safely.
            Log.d("callData","before");
            ArrayList<AutocompletePrediction> arryalist = new ArrayList<>();
            for(int i = 0; i < autocompletePredictions.getAutocompletePredictions().size(); i++) {
                arryalist.add(autocompletePredictions.getAutocompletePredictions().get(i));
            }
            return arryalist;
        } catch (RuntimeExecutionException e) {
            // If the query did not complete successfully return null

            onItemClickListener.showToast("Error contacting API: " + e.toString());

//            Toast.makeText(context, "Error contacting API: " + e.toString(),
//                    Toast.LENGTH_SHORT).show();
            //Log.e(TAG, "Error getting autocomplete prediction API call", e);
            return null;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_place_picker, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Sets the primary and secondary text for a row.
        // Note that getPrimaryText() and getSecondaryText() return a CharSequence that may contain
        // styling based on the given CharacterStyle.

        AutocompletePrediction item = getItem(position);

        holder.textView1.setText(item.getPrimaryText(STYLE_BOLD));
        holder.textView2.setText(item.getSecondaryText(STYLE_BOLD));
    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatTextView textView1, textView2;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView.setOnClickListener(this);
            textView1 = itemView.findViewById(R.id.textViewAddress);
            textView2 = itemView.findViewById(R.id.textViewSubAddress);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onItemClickListener.onRecyclerItemClick(view, position);
        }
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.onItemClickListener = clickListener;
    }

    public void getPlaceByPlaceId(String placeId, final PlaceSearchListener placeSearchListener) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        mGeoDataClient.fetchPlace(request)
                .addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        placeSearchListener.onPlaceFound(place);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        placeSearchListener.onPlaceNotFound(e.getMessage());
                    }
                });

    }

    public interface PlaceSearchListener {

        void onPlaceFound(Place place);

        void onPlaceNotFound(String message);

    }

    public interface OnItemClickListener {
        void onRecyclerItemClick(View view, int position);

        void showToast(String s);
    }
}