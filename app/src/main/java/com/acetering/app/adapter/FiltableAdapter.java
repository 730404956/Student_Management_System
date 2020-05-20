package com.acetering.app.adapter;

/*
  Author:Xiangrui Li
  Date:2020/3/18
  Last_Modify:2020/3/18
 */

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import com.acetering.app.event.CallbackEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FiltableAdapter<T extends IFilterableData> extends BasicAdapter<T> implements Filterable {
    private String TAG = "FiltableAdapter";
    protected List<T> data;
    protected Filter mfilter;
    protected CallbackEvent onResult, onDataSetInvalid;

    public FiltableAdapter(Context context, List<T> dataResource, int itemLayoutResourceId, ViewBinder<T> binder) {
        super(context, dataResource, itemLayoutResourceId, binder);
        data = dataResource;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        //synchronized data
        data.removeIf(item -> {
            int index = dataResource.indexOf(item);
            if (index >= 0) {
                int data_index = data.indexOf(item);
                data.set(data_index, dataResource.get(index));
                return false;
            } else {
                return true;
            }
        });
        //call back
        if (onResult != null) {
            onResult.doJob(context, null);
        }
        //update view
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        //call back
        if (onDataSetInvalid != null) {
            onDataSetInvalid.doJob(context, null);
        }
        super.notifyDataSetInvalidated();
    }

    public Filter getFilter(Class cls) {
        if (mfilter == null) {
            mfilter = new MyFilter<>(cls);
        }
        return mfilter;
    }

    public void setOnResult(CallbackEvent onResult) {
        this.onResult = onResult;
    }

    public void setOnDataSetInvalid(CallbackEvent onNoResult) {
        this.onDataSetInvalid = onNoResult;
    }

    @Override
    public Filter getFilter() {
        return mfilter;
    }


    //定义一个过滤器的类来定义过滤规则
    class MyFilter<A extends T> extends Filter {
        Method getData;

        public MyFilter(Class cls) {
            try {
                getData = cls.getMethod("getFiltData");
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "MyFilter: class doesn't have method getFiltData");
            }
        }

        //定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            List<T> list;
            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，显示所有的数据
                list = dataResource;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (T sth : dataResource) {
                    try {
                        Map<String, String> item_data = (Map<String, String>) getData.invoke(sth);
                        for (Map.Entry<String, String> entry : item_data.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            if (value.contains(charSequence)) {
                                list.add(sth);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中
            return result;
        }

        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            data = (List<T>) filterResults.values;
            Log.i(TAG, "publishResults:" + filterResults.count);
            if (filterResults.count > 0) {
                notifyDataSetChanged();//通知数据发生了改变
                Log.i(TAG, "publishResults:notifyDataSetChanged");
            } else {
                notifyDataSetInvalidated();//通知数据失效
                Log.i(TAG, "publishResults:notifyDataSetInvalidated");
            }
        }
    }
}
