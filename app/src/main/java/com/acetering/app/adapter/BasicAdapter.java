package com.acetering.app.adapter;
/**
 * Author: Xiangrui Li
 * Date: 2020-3-2
 * Description: Abstract basic adapter, override method bindView to customize your view
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BasicAdapter<T> extends BaseAdapter {
    protected List<T> dataResource;
    protected int itemLayoutResourceId;
    protected Context context;
    protected ViewBinder<T> binder;

    public BasicAdapter(Context context, List<T> dataResource, int itemLayoutResourceId, ViewBinder<T> binder) {
        this.binder = binder;
        this.context = context;
        this.dataResource = dataResource;
        this.itemLayoutResourceId = itemLayoutResourceId;
    }

    @Override
    public int getCount() {
        return dataResource == null ? 0 : dataResource.size();
    }

    @Override
    public T getItem(int position) {
        return dataResource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.bindLayout(context, convertView, parent, itemLayoutResourceId, position);
        binder.bindView(holder, getItem(position));
        return holder.getView();
    }


    /**
     * only work once you modify "equals" method
     */
    public boolean addOrModify(T item) {
        int index = dataResource.indexOf(item);
        if (index >= 0) {
            modifyItem(index, item);
            return false;
        } else {
            addItem(item);
            return true;
        }
    }

    public void modifyItem(int index, T item) {
        dataResource.set(index, item);
        notifyDataSetChanged();
    }
    public void addItem(T item) {
        dataResource.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(T item) {
        dataResource.remove(item);
        notifyDataSetChanged();
    }

    public List<T> getDataResource() {
        return dataResource;
    }

    /**
     * remember to call this before exit app
     */
    public void close() {
        //do some release job
    }
    /**
     * bind data and view, use holder.setXXX
     */
    public interface ViewBinder<T> {
        void bindView(ViewHolder holder, T item);
    }

    /**
     * A class to store inner views of view item, with this all inner views will only be load once;
     */
    public static class ViewHolder {
        //Activity context, use for get resources
        private Context context;
        //itemView's inner views
        private SparseArray<View> innerViews;
        //the position of the itemView in the ListView
        private int position;
        //view item in the ListView or GridView
        private View itemView;

        private ViewHolder(Context context, ViewGroup parent, int itemLayoutResourceId, int position) {
            this.innerViews = new SparseArray<>();
            this.context = context;
            this.itemView = LayoutInflater.from(context).inflate(itemLayoutResourceId, parent, false);
            this.position = position;
            itemView.setTag(this);
        }

        public static ViewHolder bindLayout(Context context, View convertView, ViewGroup parent, int itemLayoutResourceId, int position) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(context, parent, itemLayoutResourceId, position);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.itemView = convertView;
            }
            return holder;
        }

        public View getView() {
            return itemView;
        }

        @SuppressWarnings("unchecked")
        public <V extends View> V getInnerView(int id) {
            V view = (V) innerViews.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                innerViews.put(id, view);
            }
            return view;
        }

        /**
         * set onclick listener for item;
         */
        public ViewHolder setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
            return this;
        }

        /**
         * set onclick listener for inner view, such as a button inside the view item;
         */
        public ViewHolder setOnclickListenerForInnerView(int id, View.OnClickListener listener) {
            View view = getInnerView(id);
            if (view != null) {
                view.setOnClickListener(listener);
            }
            return this;
        }

        public ViewHolder setText(int id, CharSequence text) {
            View view = getInnerView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public ViewHolder setImage(int id, Drawable drawable) {
            View view = getInnerView(id);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(drawable);
            }
            return this;
        }

        public ViewHolder setBackground(int color) {
            itemView.setBackgroundColor(color);
            return this;
        }
    }

}
