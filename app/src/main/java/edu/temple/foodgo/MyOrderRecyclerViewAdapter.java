package edu.temple.foodgo;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import edu.temple.foodgo.OrderFragment.OnListFragmentInteractionListener;
import edu.temple.foodgo.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;


public class MyOrderRecyclerViewAdapter extends RecyclerView.Adapter<MyOrderRecyclerViewAdapter.ViewHolder>{

    private final ArrayList mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyOrderRecyclerViewAdapter(ArrayList items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int pos = position;
        //holder.mItem = mValues.get(position);
       // holder.mIdView.setText(mValues.get(position).id);
        //holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mListener.onRemoveButton(mValues.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final Button removeButton;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.item_name);
            mIdView = (TextView) view.findViewById(R.id.item_name);
            mContentView = (TextView) view.findViewById(R.id.item_cost);
            removeButton = (Button) view.findViewById(R.id.remove_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
