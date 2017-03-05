package com.example.administrator.travel;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.travel.offlinemap.MainIntoActivity;

import java.util.ArrayList;

import static com.example.administrator.travel.R.styleable.RecyclerView;

public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.BaseViewHolder>
{
    private ArrayList<String> dataList = new ArrayList<>();
    private Resources res;
    private static int i = 0;

    public void replaceAll(ArrayList<String> list)
    {
        dataList.clear();
        if (list != null && list.size() > 0)
        {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public DemoAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new OneViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.one, parent, false));
    }

    @Override
    public void onBindViewHolder(DemoAdapter.BaseViewHolder holder, int position)
    {

        holder.setData(dataList.get(position));
    }


    @Override
    public int getItemCount()
    {
        return dataList != null ? dataList.size() : 0;
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder
    {

        public BaseViewHolder(View itemView)
        {
            super(itemView);
        }

        void setData(Object data)
        {
        }
    }

    private class OneViewHolder extends BaseViewHolder
    {
        private ImageView ivImage;
        private TextView ivText;

        public OneViewHolder(View view)
        {
            super(view);
            ivImage = (ImageView) view.findViewById(R.id.ivImage);
            ivText = (TextView) view.findViewById(R.id.ivText);
//            int width = ((Activity) ivImage.getContext()).getWindowManager().getDefaultDisplay().getWidth();
            int width = 1440;//TODO 修改
            ViewGroup.LayoutParams params = ivImage.getLayoutParams();
            //设置图片的相对于屏幕的宽高比
            params.width = width / 2;
            params.height = (int) (200 + Math.random() * 400);
            ivImage.setLayoutParams(params);
            res = itemView.getContext().getResources();
        }

        @Override
        void setData(Object data)
        {
            if (data != null)
            {
                String text = (String) data;
//                System.out.println(text);
                Glide.with(itemView.getContext()).load(text).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_launcher).crossFade().into(ivImage);
                System.out.println(text);
//                ivText.setOnClickListener(new clikListener(text));
                ivText.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        MainActivity.mainActivity.setContentView(R.layout.activity_remark);
                    }
                });
                ivImage.setOnClickListener(new clikListener(text));
                Bitmap bitmap = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
                //异步获得bitmap图片颜色值
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener()
                {
                    @Override
                    public void onGenerated(Palette palette)
                    {
                        Palette.Swatch vibrant = palette.getVibrantSwatch();//有活力
                        Palette.Swatch c = palette.getDarkVibrantSwatch();//有活力 暗色
                        Palette.Swatch d = palette.getLightVibrantSwatch();//有活力 亮色
                        Palette.Swatch f = palette.getMutedSwatch();//柔和
                        Palette.Swatch a = palette.getDarkMutedSwatch();//柔和 暗色
                        Palette.Swatch b = palette.getLightMutedSwatch();//柔和 亮色

                        if (vibrant != null)
                        {
                            int color1 = vibrant.getBodyTextColor();//内容颜色
                            int color2 = vibrant.getTitleTextColor();//标题颜色
                            int color3 = vibrant.getRgb();//rgb颜色

                            ivImage.setBackgroundColor(
                                    vibrant.getRgb());

                        }
                    }
                });
            }
        }
    }

    public class clikListener implements View.OnClickListener
    {
        String url_Autumn = "";
        String url_Summer = "";
        String url_Spring = "";
        String url_Winter = "";

        public clikListener(String text)
        {
            url_Winter=text;
            url_Autumn = text.replace("win","aut");
            url_Summer = text.replace("win","sum");
            url_Spring = text.replace("win","spr");

        }

        @Override
        public void onClick(View view)
        {
            Intent mintent = new Intent(MainActivity.mainActivity, MainIntoActivity.class);
            mintent.putExtra("Summer",url_Summer);
            mintent.putExtra("Autumn",url_Autumn);
            mintent.putExtra("Spring",url_Spring);
            mintent.putExtra("Winter",url_Winter);
            MainActivity.mainActivity.startActivity(mintent);
        }
    }
}
