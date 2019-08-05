package com.example.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealViewHolder> {

    List<TravelDeals> mDeals;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionRef;
    //private Activity mActivity;
    private String collectionRefString = "travelDeals";
    private EventListener<QuerySnapshot> eventListener;

    public DealsAdapter(Activity activityCalled) {
        FirebaseUtil.openFirebaseReference(collectionRefString,activityCalled);
        //mFirebaseFirestore = FirebaseUtil.mFirestore;
        mDeals = FirebaseUtil.mDeals;
        mCollectionRef = FirebaseUtil.mCollectionReference;
        eventListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed: " + e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            TravelDeals travelDeal = dc.getDocument().toObject(TravelDeals.class);
                            travelDeal.setId(dc.getDocument().getId());
                            mDeals.add(travelDeal);
                            notifyItemInserted(mDeals.size() - 1);
                            break;
                        /*case MODIFIED:
                            System.out.println("Modified city: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            System.out.println("Removed city: " + dc.getDocument().getData());
                            break;*/
                        default:
                            break;
                    }
                }
            }
        };
        mCollectionRef.addSnapshotListener(eventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.activity_deals_layout,parent,false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeals tDeal = mDeals.get(position);
        holder.bind(tDeal);
    }

    @Override
    public int getItemCount() {
        return FirebaseUtil.mDeals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder{

        TextView title,description,price;
        CircleImageView holidayImage;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleRecyclerView);
            description = itemView.findViewById(R.id.descRecyclerView);
            price = itemView.findViewById(R.id.priceRecyclerView);
            holidayImage = itemView.findViewById(R.id.holidayImage);
        }

        public void bind(TravelDeals travelDeal){
            title.setText(travelDeal.getTitle());
            description.setText(travelDeal.getDescription());
            price.setText(String.valueOf(travelDeal.getPrice()));
            Picasso.get().load(travelDeal.getImageUrl()).into(holidayImage);
        }
    }
}
