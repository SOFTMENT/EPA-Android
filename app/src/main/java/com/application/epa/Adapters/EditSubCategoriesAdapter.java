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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.application.epa.EditSubcategoryActivity;
import com.application.epa.Models.SubcategoryModel;
import com.application.epa.R;
import com.application.epa.Utils.ProgressHud;
import com.application.epa.Utils.Services;

public class EditSubCategoriesAdapter extends RecyclerView.Adapter<EditSubCategoriesAdapter.ViewHolder> {

    private final ArrayList<SubcategoryModel> subcategoryModels;
    private final Context context;
    private final String cat_id;
    public EditSubCategoriesAdapter(Context context, ArrayList<SubcategoryModel> subcategoryModels, String cat_id) {
        this.context = context;
        this.subcategoryModels = subcategoryModels;
        this.cat_id = cat_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_sub_categories_layout_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SubcategoryModel subcategoryModel = subcategoryModels.get(position);
        if (Services.getLocateCode(context).equalsIgnoreCase("pt"))
            holder.cat_title.setText(subcategoryModel.getTitle_pt());
        else
            holder.cat_title.setText(subcategoryModel.getTitle_en());

        holder.cat_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditSubcategoryActivity.class);
                intent.putExtra("subcategory",subcategoryModel);
                intent.putExtra("cat_id",cat_id);
                context.startActivity(intent);
            }
        });

        //DELETE
        holder.sub_cat_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressHud.show(context,"Deleting...");
                        FirebaseFirestore.getInstance().collection("Categories").document(cat_id).collection("Subcategories").document(subcategoryModel.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                ProgressHud.dialog.dismiss();
                                Services.showCenterToast(context,"Deleted");
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
        return subcategoryModels.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView cat_edit, sub_cat_delete;
        private TextView cat_title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cat_edit = itemView.findViewById(R.id.cat_edit);
            cat_title = itemView.findViewById(R.id.cat_title);
            sub_cat_delete = itemView.findViewById(R.id.delete);
        }
    }
}
