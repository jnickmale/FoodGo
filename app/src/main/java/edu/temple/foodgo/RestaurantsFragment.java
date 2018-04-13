package edu.temple.foodgo;

import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantsFragment.OnRestaurantInformationListener} interface
 * to handle interaction events.
 * Use the {@link RestaurantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantsFragment extends Fragment implements HoldsRestaurantInformation{


    private DataSnapshot restaurantData;

    private OnRestaurantInformationListener mListener;

    public RestaurantsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RestaurantsFragment.
     */
    public static RestaurantsFragment newInstance() {
        RestaurantsFragment fragment = new RestaurantsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurantData = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurants, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateData();
    }


    public void updateData(){
        mListener.onRestaurantInformation(this);
        if(restaurantData != null){
            ViewGroup insertPoint = (ViewGroup) getActivity().findViewById(R.id.menuItems);
            for (DataSnapshot ds : restaurantData.child("restaurant1").child("food").getChildren()){
                LinearLayout ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                ImageView foodImageView = new ImageView(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.weight = 1;
                foodImageView.setLayoutParams(layoutParams);
                Picasso.get().load(ds.child("foodImageURL").getValue().toString()).resize(128, 128).into(foodImageView);

                TextView infoView = new TextView(getActivity());
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 0, 10, 20);
                infoView.setLayoutParams(layoutParams2);
                infoView.setText(ds.child("name").getValue().toString() +  "\n" + ds.child("description").getValue().toString() + "\n$" +ds.child("price").getValue().toString());
                infoView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);

                Button addToCartButton = new Button(getActivity());
                addToCartButton.setText("Add to Cart");
                addToCartButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                infoView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);


                ll.addView(foodImageView);
                ll.addView(infoView);
                ll.addView(addToCartButton);


                // insert into main view
                insertPoint.addView(ll);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRestaurantInformationListener) {
            mListener = (OnRestaurantInformationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRestaurantInformationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setRestaurantInformation(DataSnapshot restaurantData) {
        this.restaurantData = restaurantData;
    }

    public interface OnRestaurantInformationListener {
        void onRestaurantInformation(HoldsRestaurantInformation restaurantInformationHolder);
    }
}
