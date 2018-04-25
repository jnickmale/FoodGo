package edu.temple.foodgo;

import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.temple.foodgo.dummy.DummyContent;


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
    private String menuSelected;
    private ArrayList<String> menus;
    private SpinnerAdapter menusAdapter;
    private ArrayList<OrderItem> shownMenuItems;

    private OnRestaurantInformationListener mListener;
    private DataSnapshot menuData;

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
        menus = null;
        menuSelected = "0";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurants, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spinner spinner;
        spinner = (Spinner)getActivity().findViewById(R.id.menusSpinner);
        if(menus == null) {
            menus = new ArrayList<String>();
        }
        menusAdapter = new ArrayAdapter<String>((Context)getActivity(), android.R.layout.simple_spinner_item, menus);
        spinner.setAdapter(menusAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String menuID = Integer.toString(position);
                setCurrentMenu(menuID);
                updateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setCurrentMenu(String menuID){
        menuSelected = menuID;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateData();
    }


    public void updateData(){
        if(mListener != null) {
            mListener.onRestaurantInformation(this);
        }
        if(restaurantData != null && menuSelected != null){
            ViewGroup insertPoint = null;
            if(this.isAdded()) {
                insertPoint = (ViewGroup) getActivity().findViewById(R.id.menuItems);
                insertPoint.removeAllViews();
            }
            shownMenuItems = new ArrayList<OrderItem>();
            for (DataSnapshot ds : restaurantData.child("menus").child(menuSelected).child("food").getChildren()){
                final DataSnapshot snapshot = ds;
                shownMenuItems.add(new OrderItem(ds));

                if(this.isAdded()) {
                    LinearLayout ll = new LinearLayout(getActivity());
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.setPadding(0, 0, 0, 16);
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
                    infoView.setText(ds.child("name").getValue().toString() + "\n" + ds.child("description").getValue().toString() + "\n$" + ds.child("price").getValue().toString());
                    infoView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

                    Button addToOrderButton = new Button(getActivity());
                    addToOrderButton.setText("Add to Order");
                    addToOrderButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    addToOrderButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((OnRestaurantInformationListener) getActivity()).addToOrder(snapshot);
                        }
                    });
                    infoView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);


                    ll.addView(foodImageView);
                    ll.addView(infoView);
                    ll.addView(addToOrderButton);


                    // insert into main view
                    insertPoint.addView(ll);
                }
            }
            if(insertPoint != null) {
                insertPoint.invalidate();
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
        if(this.restaurantData != null) {
            setMenuData(restaurantData);
        }
    }



    public void setMenuData(DataSnapshot restaurantData){
        menuData = restaurantData.child("menus");
        if(menus == null){
            menus = new ArrayList<String>();
        }
        menus.clear();
        for(DataSnapshot d: menuData.getChildren()){
            menus.add((String)d.child("menuName").getValue());
        }
        ((ArrayAdapter)menusAdapter).notifyDataSetChanged();
    }

    /**
     * Interface for marking that an object holds restaurant information
     */
    public interface OnRestaurantInformationListener {
        void onRestaurantInformation(HoldsRestaurantInformation restaurantInformationHolder);
        void addToOrder(DataSnapshot ds);
    }
}
