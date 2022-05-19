package com.application.epa.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.application.epa.EditProductActivity;
import com.application.epa.Models.CategoryModel;
import com.application.epa.Models.ProductModel;
import com.application.epa.R;
import com.application.epa.StripeCheckoutActivity;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<ProductModel> productModels;
    public MyProductAdapter(Context context,ArrayList<ProductModel> productModel) {
        this.context = context;
        this.productModels = productModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.myproduct_view_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ProductModel productModel = productModels.get(position);
        if (productModel.getImages().size() > 0) {
            Glide.with(context).load(productModel.getImages().get("0")).placeholder(R.drawable.placeholder1).into(holder.imageView);

        }
        holder.title.setText(productModel.title);
        holder.category.setText(CategoryModel.getCategoryNameById(context,productModel.getCat_id()));
        holder.date.setText(Services.convertDateToString(productModel.date));
        holder.price.setText("R$"+productModel.price);

        holder.delete.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogTheme);
                builder.setTitle(R.string.delete_product_capital);
                builder.setMessage(R.string.are_you_sure_to_delete_item);
                builder.setCancelable(false);


                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressHud.show(context,context.getString(R.string.deleting));
                        FirebaseFirestore.getInstance().collection("Products").document(productModel.id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {


                                if (task.isSuccessful()){
                                    Services.showCenterToast(context,context.getString(R.string.product_has_deleted));
                                }
                                ProgressHud.dialog.dismiss();
                                dialog.dismiss();

                            }
                        });
                    }
                });

                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });



               builder.show();

            }

        });

        if(Services.isPromoting(productModel.adLastDate)) {
            holder.promote.setEnabled(false);
            holder.promote.setText(R.string.promoting);
        }
        else{
            holder.promote.setEnabled(true);
            holder.promote.setText(R.string.promote);
        }

        holder.promote.setOnClickListener(v -> {
            Intent intent = new Intent(context, StripeCheckoutActivity.class);
            intent.putExtra("productId",productModel.getId());
            context.startActivity(intent);


        });
        holder.view.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditProductActivity.class);
            intent.putExtra("index", position);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {

        return productModels.size();
    }

     public static class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imageView;
        private TextView title, category, date, price, promote;
        private View view;
        private ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imageView = view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);
            category = view.findViewById(R.id.category);
            date = view.findViewById(R.id.date);
            price = view.findViewById(R.id.price);
            promote = view.findViewById(R.id.promote);
            delete = view.findViewById(R.id.delete);
        }
    }
}
