package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;

    public RecipeAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(
                R.layout.item_recipe,
                parent,
                false
        );

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position
    ) {

        Recipe recipe = recipes.get(position);

        holder.tvRecipeName.setText(
                recipe.getName()
        );

        holder.tvRecipeDescription.setText(
                "Tap to view recipe details"
        );

        holder.itemView.setOnClickListener(v -> {

            Context context = v.getContext();

            Intent intent = new Intent(
                    context,
                    RecipeDetailActivity.class
            );

            intent.putExtra(
                    "recipe_id",
                    recipe.getId()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvRecipeName;
        TextView tvRecipeDescription;

        public RecipeViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            tvRecipeName = itemView.findViewById(
                    R.id.tvRecipeName
            );

            tvRecipeDescription = itemView.findViewById(
                    R.id.tvRecipeDescription
            );
        }
    }
}