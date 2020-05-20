package com.acetering.app.views;

import android.app.Dialog;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.acetering.app.R;
import com.acetering.app.adapter.BasicAdapter;
import com.acetering.app.util.BitmapUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/5/6
 */
public class ImagePickerDialog extends Dialog {
    BasicAdapter<Bitmap> adapter;
    List<Bitmap> images;
    Context context;
    Bitmap choosen_img;

    private ImagePickerDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        loadImages();
        images = new ArrayList<>();
        adapter = new BasicAdapter<>(context, images, R.layout.grid_image_item, new BasicAdapter.ViewBinder<Bitmap>() {
            BasicAdapter.ViewHolder last_choosen;

            @Override
            public void bindView(BasicAdapter.ViewHolder holder, Bitmap item) {
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(), item);
                holder.setImage(R.id.image, drawable);
                holder.setOnClickListener(v -> {
                    if (last_choosen != null) {
                        last_choosen.setBackground(Color.TRANSPARENT);
                    }
                    choosen_img = item;
                    holder.setBackground(Color.YELLOW);
                    last_choosen = holder;
                });
            }
        });

    }

    public Bitmap getChoosen_img() {
        return choosen_img;
    }

    private void loadImages() {
        //显示进度条
        //DialogFactory.createProgressDialog(getContext(),"加载中，请稍后……").show();

        new Thread(() -> {
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = context.getContentResolver();

            //只查询jpeg和png的图片
            Cursor mCursor = mContentResolver.query(mImageUri, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

            if (mCursor == null) {
                return;
            }

            while (mCursor.moveToNext()) {
                //获取图片的路径
                String path = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                //获取该图片的父路径名
                String parentName = new File(path).getParentFile().getName();

                try {
                    FileInputStream fis = new FileInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    images.add(BitmapUtil.zoomImg(bitmap, 96, 96));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //通知Handler扫描图片完成
            //mHandler.sendEmptyMessage(0);
            mCursor.close();
        }).start();
    }


    public static class Builder {

        private View mLayout;

        private ImageView mIcon;
        private TextView mTitle;
        private TextView mMessage;
        private Button mButton;
        private Button mCancelBtn;

        private View.OnClickListener mButtonClickListener;

        public ImagePickerDialog mDialog;

        public Builder(Context context) {
            mDialog = new ImagePickerDialog(context, R.style.Theme_AppCompat_Dialog);
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //加载布局文件
            mLayout = inflater.inflate(R.layout.image_picker_dialog, null, false);
            //添加布局文件到 Dialog
            mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            GridView v = mLayout.findViewById(R.id.img_container);
            v.setAdapter(mDialog.adapter);
            mIcon = mLayout.findViewById(R.id.icon);
            mTitle = mLayout.findViewById(R.id.title);
            //mMessage = mLayout.findViewById(R.id.dialog_message);
            mButton = mLayout.findViewById(R.id.btn_confirm);
            mCancelBtn = mLayout.findViewById(R.id.btn_cancel);
        }

        /**
         * 通过 ID 设置 Dialog 图标
         */
        public Builder setIcon(int resId) {
            mIcon.setImageResource(resId);
            return this;
        }

        /**
         * 用 Bitmap 作为 Dialog 图标
         */
        public Builder setIcon(Bitmap bitmap) {
            mIcon.setImageBitmap(bitmap);
            return this;
        }

        /**
         * 设置 Dialog 标题
         */
        public Builder setTitle(@NonNull String title) {
            mTitle.setText(title);
            mTitle.setVisibility(View.VISIBLE);
            return this;
        }

        /**
         * 设置 Message
         */
        public Builder setMessage(@NonNull String message) {
            mMessage.setText(message);
            return this;
        }

        /**
         * 设置按钮文字和监听
         */
        public Builder setPositiveButton(@NonNull String text, View.OnClickListener listener) {
            mButton.setText(text);
            mButtonClickListener = listener;
            return this;
        }

        public ImagePickerDialog create() {
            mButton.setOnClickListener(view -> {
                mDialog.dismiss();
                mButtonClickListener.onClick(view);
            });
            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);                //用户可以点击后退键关闭 Dialog
            mDialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
            return mDialog;
        }
    }
}
