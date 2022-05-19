package com.application.epa.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.application.epa.Models.CategoryModel;
import com.application.epa.R;
import com.application.epa.SeeAllSubcategoryActivity;
import com.application.epa.Utils.Services;

public class CategoriesAdaper  extends RecyclerView.Adapter<CategoriesAdaper.ViewHolder> {

   private final Context context;
  // private ArrayList<Drawable> categories_back_view;
    private final ArrayList<CategoryModel> categoryModels;
    public CategoriesAdaper(Context context,  ArrayList<CategoryModel> categoryModels){
        this.context = context;
      //  this.categories_back_view = categories_back_view;
        this.categoryModels = categoryModels;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_layout_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CategoryModel categoryModel = categoryModels.get(position);
        //holder.cat_image_rr.setBackground(categories_back_view.get(position % categories_back_view.size()));
        Glide.with(context).load(categoryModel.image).placeholder(R.drawable.category_placeholder).into(holder.cat_image);
        if (Services.getLocateCode(context).equalsIgnoreCase("pt"))
           holder.cat_title.setText(categoryModel.getTitle_pt());
        else
            holder.cat_title.setText(categoryModel.getTitle_en());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SeeAllSubcategoryActivity.class);
                intent.putExtra("cat_id",categoryModel.id);

                if (Services.getLocateCode(context).equalsIgnoreCase("pt"))
                    intent.putExtra("cat_name",categoryModel.getTitle_pt());
                else
                    intent.putExtra("cat_name",categoryModel.getTitle_en());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout cat_image_rr;
        private ImageView cat_image;
        private TextView cat_title;
        private View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            cat_image_rr = itemView.findViewById(R.id.cat_image_rr);
            cat_image = itemView.findViewById(R.id.cat_image);
            cat_title = itemView.findViewById(R.id.cat_title);
        }
    }
}
