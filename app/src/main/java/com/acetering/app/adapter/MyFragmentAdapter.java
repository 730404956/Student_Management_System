package com.acetering.app.adapter;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Create by Acetering(Xiangrui Li)
 * On 2020/3/23
 */
public class MyFragmentAdapter extends FragmentPagerAdapter {
    //TODO: use this class to implements fragment slide change

    List<Fragment> list;

    public MyFragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.list = list;
    }//写构造方法，方便赋值调用

    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }//根据Item的位置返回对应位置的Fragment，绑定item和Fragment

    @Override
    public int getCount() {
        return list.size();
    }//设置Item的数量


}
