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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.application.epa.EditCategoriesActivity;
import com.application.epa.ManageSubcategoryActivity;
import com.application.epa.Models.CategoryModel;
import com.application.epa.R;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;

public class EditCategoriesAdapter extends RecyclerView.Adapter<EditCategoriesAdapter.ViewHolder> {

    private final ArrayList<CategoryModel> categoryModels;
    private final Context context;
    public EditCategoriesAdapter(Context context, ArrayList<CategoryModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_categories_layout_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CategoryModel categoryModel = categoryModels.get(position);
        if (Services.getLocateCode(context).equalsIgnoreCase("pt"))
            holder.cat_title.setText(categoryModel.getTitle_pt());
        else
            holder.cat_title.setText(categoryModel.getTitle_en());

        holder.cat_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditCategoriesActivity.class);
                intent.putExtra("category",categoryModel);
                context.startActivity(intent);
            }
        });
        Glide.with(context).load(categoryModel.image).placeholder(R.drawable.category_placeholder).into(holder.cat_image);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(context,ManageSubcategoryActivity.class);
              intent.putExtra("cat_id",categoryModel.getId());
              if (Services.getLocateCode(context).equalsIgnoreCase("pt"))
                    intent.putExtra("cat_name",categoryModel.getTitle_pt());
              else
                    intent.putExtra("cat_name",categoryModel.getTitle_en());
              context.startActivity(intent);

            }
        });

        //DELETE
        holder.cat_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressHud.show(context,"Deleting...");
                        FirebaseFirestore.getInstance().collection("Categories").document(categoryModel.id).collection("Subcategories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                ProgressHud.dialog.dismiss();
                                Services.showCenterToast(context,"Deleted");
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                            if (documentSnapshot.exists()) {
                                                documentSnapshot.getReference().delete();
                                            }
                                        }
                                    }

                                FirebaseFirestore.getInstance().collection("Categories").document(categoryModel.id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                notifyDataSetChanged();
                                            }
                                    }
                                });

                            }
                        });


                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setTitle("DELETE");
                builder.setMessage("Are you sure you want to delete subcategory?");
                builder.show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView cat_image, cat_edit, cat_delete;
        private TextView cat_title;
        private View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            cat_edit = itemView.findViewById(R.id.cat_edit);
            cat_image = itemView.findViewById(R.id.cat_image);
            cat_title = itemView.findViewById(R.id.cat_title);
            cat_delete = itemView.findViewById(R.id.delete);
        }
    }
}
