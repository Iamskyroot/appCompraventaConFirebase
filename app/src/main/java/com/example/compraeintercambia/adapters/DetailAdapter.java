package com.example.compraeintercambia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.compraeintercambia.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailAdapter extends PagerAdapter {

    List<String> pictures;
    private Context context;

    public DetailAdapter(Context context, List<String> pictures){
        this.context=context;
        this.pictures=pictures;
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_image_detail,container,false);

        //declaration object view
        ImageView picture = view.findViewById(R.id.ivLayoutProduct);
        //set image to imageView
        Picasso.get().load(pictures.get(position))
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerInside()
                .into(picture);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);

    }
}
