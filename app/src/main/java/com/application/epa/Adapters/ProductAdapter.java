package com.application.epa.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;


import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import com.application.epa.Models.ProductModel;
import com.application.epa.R;
import com.application.epa.Utils.Services;
import com.application.epa.ViewProductActivity;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;

    private int mIndex = 0;

    private final ArrayList<ProductModel> productModels;
    public ProductAdapter(Context context, ArrayList<ProductModel> productModels){
        this.context = context;
        this.productModels = productModels;


    }





    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout_view,parent,false));

    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

            ProductModel productModel = productModels.get(position);

            ProductViewHolder productViewHolder = (ProductViewHolder) holder;
            productViewHolder.productTitle.setText(Services.toUpperCase(productModel.title));

             Glide.with(context).load(productModels.get(position).getImages().get("0")).apply(new RequestOptions().fitCenter().override(180, 260)).placeholder(R.drawable.placeholder1).into(productViewHolder.roundedImageView);

            if (productModel.getPrice() == 0) {
                productViewHolder.card_price.setVisibility(View.GONE);
            }
            else {
                productViewHolder.card_price.setVisibility(View.VISIBLE);
                productViewHolder.productPrice.setText("R$"+productModel.price);

            }

            final int finalNewIndex = position;
            productViewHolder.view.setOnClickListener(view -> {


                    Intent intent = new Intent(context, ViewProductActivity.class);
                    intent.putExtra("product",productModel);
                    intent.putExtra("fromDeepLink",false);
                    context.startActivity(intent);


            });

            if (Services.isPromoting(productModel.adLastDate)) {
                productViewHolder.adsCard.setVisibility(View.VISIBLE);
            }
            else {
                productViewHolder.adsCard.setVisibility(View.GONE);
            }



    }



    @Override
    public int getItemCount() {
            return productModels.size();
    }


   static public class ProductViewHolder extends RecyclerView.ViewHolder {
        private final RoundedImageView roundedImageView;
        private final TextView productTitle;
        private final TextView productPrice;
        private final View view;
        private final CardView adsCard;
        private final CardView card_price;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            roundedImageView = itemView.findViewById(R.id.product_image);
            productPrice = itemView.findViewById(R.id.product_price);
            productTitle = itemView.findViewById(R.id.product_title);
            adsCard = itemView.findViewById(R.id.adsCard);
            card_price = itemView.findViewById(R.id.price_card_view);
        }
    }



    public void filter(String text,ArrayList<ProductModel> mainProductModels){
        ArrayList<ProductModel> newproductModels = new ArrayList<>();

        for (ProductModel productModel : mainProductModels) {
            if (productModel.title.toLowerCase().contains(text.toLowerCase()) || productModel.storeCity.toLowerCase().contains(text.toLowerCase()) || productModel.description.toLowerCase().contains(text.toLowerCase())) {
                newproductModels.add(productModel);
            }
        }
        productModels.clear();
        productModels.addAll(newproductModels);
        notifyDataSetChanged();

    }
}
