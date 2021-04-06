package com.afroexaentric.Our_Services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.afroexaentric.Comman_Stuffs.Gson_List_Prefs;
import com.afroexaentric.Comman_Stuffs.IsSelected_Product;
import com.afroexaentric.R;

import static com.afroexaentric.Comman_Stuffs.Log_Constants.TAG;

public class Sub_Service_Adaptor extends RecyclerView.Adapter<Sub_Service_Adaptor.ViewHolder> {
    private ArrayList<HashMap<String, String>> list;

    private Activity activity;
    private SharedPreferences sharedPreferences;
    private int pos;
    private IsSelected_Product isSelected_product;

    public static final int MAX_LINES = 3;

    public Sub_Service_Adaptor(FragmentActivity activity, ArrayList<HashMap<String, String>> list) {
        this.activity = activity;
        this.list = list;
        isSelected_product= new IsSelected_Product(activity);
    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_service_layout, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d("json", "onBindViewHolder: " + position);



        final HashMap<String, String> map = list.get(position);
        holder.tvName.setText(map.get(Sub_Service.SERVICE_NAME));
        holder.tvDesc.setText(map.get(Sub_Service.DESCRIPTION));
        holder.tvPrice.setText("$"+map.get(Sub_Service.PRICE));
        holder.tvTime.setText(map.get(Sub_Service.TIME)+" "+map.get(Sub_Service.DURATION_TYPE));

        Picasso.with(activity).load(map.get(Sub_Service.IMAGE_URL)).placeholder(R.color.lightgrey).into(holder.imgUser);

        Object[] isProductId = isSelected_product.isSelectedItem(map.get(Sub_Service.SERVICE_ID));
        // boolean isProductId= isSelectedItem(listShopingCard.get(i).get(ShopingCard.SHOP_PRODUCT_ID));
        ///shoping cart status
        if ((boolean) isProductId[1]){
            holder.imgCart.setImageResource(R.drawable.addcart_minus);
        }
        else {
            holder.imgCart.setImageResource(R.drawable.addcart);
        }


//        ReadMoreOption readMoreOption = new ReadMoreOption.Builder(activity)
//                .textLength(3, ReadMoreOption.TYPE_LINE) // OR
//                //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
//                .moreLabel("MORE")
//                .lessLabel("LESS")
//                .moreLabelColor(ContextCompat.getColor(activity,R.color.colorPrimary))
//                .lessLabelColor(ContextCompat.getColor(activity,R.color.colorAccent))
//                .labelUnderLine(true)
//                .expandAnimation(true)
//                .build();
//
//        readMoreOption.addReadMoreTo(holder.tvDesc, map.get(Sub_Service.DESCRIPTION));
        //makeTextViewResizable(holder.tvDesc, 3, "View More", true);



        holder.tvDesc.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {

                see_More_Text(holder.tvDesc,map.get(Sub_Service.DESCRIPTION));
               // makeTextViewResizable(holder.tvDesc, 3, "View More", true);
                Log.d("json", "onBindViewHolder: "+holder.tvDesc.getLineCount());
            }
        });
    }

    private void see_More_Text(TextView tvDesc, String strMore_text) {
        // Past the maximum number of lines we want to display.
        if (tvDesc.getLineCount() > MAX_LINES) {
            int lastCharShown = tvDesc.getLayout().getLineVisibleEnd(MAX_LINES - 1);

            tvDesc.setMaxLines(MAX_LINES);

            String moreString = "More";
            String suffix = "  " + moreString;

            // 3 is a "magic number" but it's just basically the length of the ellipsis we're going to insert
            String actionDisplayText = strMore_text.substring(0, lastCharShown - suffix.length() - 3) + "..." + suffix;

            SpannableString truncatedSpannableString = new SpannableString(actionDisplayText);
            int startIndex = actionDisplayText.indexOf(moreString);
            truncatedSpannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity,R.color.colorPrimary)), startIndex,
                    startIndex + moreString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDesc.setText(truncatedSpannableString);
        }
    }

    /// recycle view item
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private boolean isCheck=true;
        public LinearLayout layoutRoot;
        private ImageView imgUser,imgCart;
        private TextView tvName,tvDesc,tvPrice,tvTime;
        private int pos;

        public ViewHolder(View itemView) {
            super(itemView);
            layoutRoot = (LinearLayout) itemView.findViewById(R.id.layoutRoot);

            imgUser = (ImageView) itemView.findViewById(R.id.imgUser);
            imgCart = (ImageView) itemView.findViewById(R.id.img_Cart);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_Desc);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_Price);
            tvTime = (TextView) itemView.findViewById(R.id.tv_Time);

            layoutRoot.setOnClickListener(this);
            imgCart.setOnClickListener(this);
            tvDesc.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            pos = getAdapterPosition();

           // SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.d("json", "onClick: " + pos);
            switch (view.getId()) {
                case R.id.img_Cart:
                  //  int strCount= Integer.parseInt(((Drawer_Stuff)activity).tvAddcart.getText().toString());

                    Object[] isProductId= new IsSelected_Product(activity).isSelectedItem(list.get(pos).get(Sub_Service.SERVICE_ID));

                    if ((boolean)isProductId[1]){
                      //  ((Drawer_Stuff)activity).tvAddcart.setText(strCount-1+"");
                        imgCart.setImageResource(R.drawable.addcart);
                        Toast.makeText(activity,list.get(pos).get(Sub_Service.SERVICE_NAME)+" removed from cart successfully",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //((Drawer_Stuff)activity).tvAddcart.setText(strCount+1+"");
                        imgCart.setImageResource(R.drawable.addcart_minus);
                        Toast.makeText(activity,list.get(pos).get(Sub_Service.SERVICE_NAME)+" added in cart successfully",Toast.LENGTH_SHORT).show();
                    }

                    //save or remove item from cart list
                    new Gson_List_Prefs(activity).saveItem(list.get(pos));
                   // DashBoard.strProductId=list.get(pos).get(DashBoard_Constant.ID);
                    //comman_stuff_interface.comman_Stuff("ok");
                    break;
                case R.id.layoutRoot:
                    //save or remove item from cart list
                new Gson_List_Prefs(activity).saveItem(list.get(pos));
                activity.startActivity(new Intent(activity, Service_Add_Data.class));
                    break;
                case R.id.tv_Desc:
                    Log.d(TAG, "onClick: view");
                    if (isCheck) {
                        tvDesc.setText(list.get(pos).get(Sub_Service.DESCRIPTION));
                        tvDesc.setMaxLines(200);
                        isCheck = false;
                    } else {
                        tvDesc.setMaxLines(3);
                        see_More_Text(tvDesc,list.get(pos).get(Sub_Service.DESCRIPTION));
                        isCheck = true;
                    }
                    break;
                default:
                    break;
            }
        }

    }

}










