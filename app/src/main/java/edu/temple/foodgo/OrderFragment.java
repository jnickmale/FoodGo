package edu.temple.foodgo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.temple.foodgo.dummy.DummyContent;
import edu.temple.foodgo.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class OrderFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<OrderItem> order;
    private View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrderFragment() {
    }

    public static OrderFragment newInstance(int columnCount) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        order = ((OnListFragmentInteractionListener)getActivity()).getOrder();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        this.view = view;

        // Set the adapter
        if (view.findViewById(R.id.list) instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyOrderRecyclerViewAdapter(order, mListener));
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.placeOrderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
                int i =5;
            }
        });
        final View v = view;
        EditText tipText = ((EditText)view.findViewById(R.id.tipValueView));
        tipText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView textView = ((TextView)v.findViewById(R.id.totalValueView));
                        textView.setText(Double.toString(calculateTotal()));
            }
        });
        updateSubtotalView();
        updateTotalView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void placeOrder(){
        //DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DataSnapshot restaurantData = mListener.getRestaurantData();
        DataSnapshot orders = restaurantData.child("orders");
        String id = Long.toString(orders.getChildrenCount());
        DataSnapshot order = orders.child(id);
        order.child("userID").getRef().setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        order.child("orderNum").getRef().setValue(id);
        order.child("orderTotal").getRef().setValue(((TextView)view.findViewById(R.id.totalValueView)).getText().toString());

    }

    public double calculateTotal(){
        double subtotal = Double.parseDouble(((TextView)view.findViewById(R.id.subtotalValueView)).getText().toString());
        double tip;
        try {
            tip = Double.parseDouble(((EditText) view.findViewById(R.id.tipValueView)).getText().toString());
        }catch(NumberFormatException e){
            tip = 0;
        }
        double total = subtotal + tip;
        return total;
    }

    public double calculateSubtotal(){
        double subtotal = 0;
        for(OrderItem item:order){
            subtotal += Double.parseDouble(item.getPrice());
        }
        return subtotal;
    }

    private void updateSubtotalView(){
        ((TextView)view.findViewById(R.id.subtotalValueView)).setText(Double.toString(calculateSubtotal()));

    }

    private void updateTotalView(){
        ((TextView)view.findViewById(R.id.totalValueView)).setText(Double.toString(calculateTotal()));
    }


    public interface OnListFragmentInteractionListener {
        void onRemoveButton(int position);
        ArrayList<OrderItem> getOrder();
        DataSnapshot getRestaurantData();
    }
}
