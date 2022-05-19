package com.application.epa.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

import com.application.epa.Models.SubcategoryModel;
import com.application.epa.R;
import com.application.epa.SeeAllSubcategoryActivity;
import com.application.epa.Utils.Services;


public class SeeAllSubcategoryAdapter extends RecyclerView.Adapter<SeeAllSubcategoryAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<SubcategoryModel> subcategoryModels;

    public SeeAllSubcategoryAdapter(Context context, ArrayList<SubcategoryModel> subcategoryModels){
        this.context = context;
        this.subcategoryModels = subcategoryModels;

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.see_all_subcategoris_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        SubcategoryModel subcategoryModel = subcategoryModels.get(position);

        if (Services.getLocateCode(context).equalsIgnoreCase("pt"))
            holder.cat_name.setText(subcategoryModel.getTitle_pt());
        else
            holder.cat_name.setText(subcategoryModel.getTitle_en());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SeeAllSubcategoryActivity)context).gotoSingleCategoryActivity(subcategoryModel.getId(),holder.cat_name.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subcategoryModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cat_name;
        private final View view;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cat_name = itemView.findViewById(R.id.categoryName);
            view = itemView;
        }
    }




}
