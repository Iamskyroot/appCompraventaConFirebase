package com.example.compraeintercambia.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.compraeintercambia.R;
import com.example.compraeintercambia.model.Products;
import com.example.compraeintercambia.otrhers.InterfaceItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptadorProductos extends BaseAdapter {

    private InterfaceItem interfaceItem;

    List<Products> productos;
    List<Products> filteredProducts;
    Context context;

    private ImageView ivProduct;
    private TextView tvPrice;
    TextView itemId;

    private LayoutInflater inflater;
    Products listProducts;

    public AdaptadorProductos(List<Products> productos, Context context) {
        this.productos = productos;
        this.filteredProducts = productos;
        this.context = context;
    }


    @Override
    public int getCount() {
        return filteredProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (inflater == null){
            inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view==null){
            view=inflater.inflate(R.layout.layout_item,null);
        }

        ivProduct = view.findViewById(R.id.ivProduct);
        tvPrice = view.findViewById(R.id.tvPrice);
        //itemId = view.findViewById(R.id.itemId);


        if (productos!=null && productos.size()>0){

            listProducts = filteredProducts.get(position);
            Picasso.get().load(listProducts.getImg())
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerInside()
                    .into(ivProduct);
            if (listProducts.getPrice() == null){
                tvPrice.setText("Intercambiable");
            }else {
                tvPrice.setText(listProducts.getPrice()+"XAF");
            }
            //itemId.setText(listProducts.getId());
            /*ivProduct.setImageResource(Integer.parseInt(listProducts.getImg()));
            tvPrice.setText(listProducts.getPrice());*/
            //setting clicListener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object positionItem = getItem(position);
                    if (positionItem != String.valueOf(GridView.INVALID_POSITION)){
                        interfaceItem.showDetailProduct(position);
                    }
                }
            });
            //create contexmenu listener
            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.setHeaderTitle("Acciones");
                    MenuItem nothing = contextMenu.add(ContextMenu.NONE,1,1,"No hacer nada");
                    MenuItem delete = contextMenu.add(ContextMenu.NONE,2,2,"Eliminar");

                    //actions
                    nothing.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Object positionItem = getItem(position);
                            if (positionItem != String.valueOf(GridView.INVALID_POSITION)){
                                interfaceItem.doNothing(position);
                                return true;
                            }
                            return false;
                        }
                    });
                    delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Object positionItem = getItem(position);
                            if (positionItem != String.valueOf(GridView.INVALID_POSITION)){
                                interfaceItem.deleteProduct(position);
                                return true;
                            }
                            return false;
                        }
                    });
                }
            });
        }

        return view;
    }


    /*@Override
    public Filter getFilter() {

        *//*Filter filter = new Filter() {
            //run on background thread
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults filterResults = new FilterResults();
                if (charSequence.toString().isEmpty()){
                    filterResults.count=filteredProducts.size();
                    filterResults.values=filteredProducts;
                }else{
                    String searchStr = charSequence.toString().toLowerCase();
                    List<Products> resultData = new ArrayList<>();
                    for (Products product: productos) {
                        if (product.getDescription().toLowerCase().contains(searchStr) || product.getType().toLowerCase().contains(searchStr)){
                            resultData.add(product);
                        }
                        filterResults.count=resultData.size();
                        filterResults.values=resultData;
                    }
                }



                return filterResults;
            }
            //run on a UI thread
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredProducts = (List<Products>) filterResults.values;
                notifyDataSetChanged();
            }
        };*//*

        return null;
    }*/


    //set interface item
    public void setInterfaceItem(InterfaceItem iItem){
         interfaceItem = iItem;
    }


}
