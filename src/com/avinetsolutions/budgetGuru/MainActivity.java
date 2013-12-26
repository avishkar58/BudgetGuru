package com.avinetsolutions.budgetGuru;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        initialisePaging();
    }

    private void initialisePaging() {
        List<Pair<String, Fragment>> fragments = new Vector<Pair<String, Fragment>>();
        fragments.add(Pair.create("Cash Flows", Fragment.instantiate(this, CashflowList.class.getName())));
        fragments.add(Pair.create("Categories", Fragment.instantiate(this, CategoryList.class.getName())));

        ViewPager pager = (ViewPager)super.findViewById(R.id.pager);
        pager.setAdapter(new PageAdapter(super.getSupportFragmentManager(), fragments));
    }

    public static class PageAdapter extends FragmentPagerAdapter {
        List<Pair<String, Fragment>> fragments;

        public PageAdapter(FragmentManager fm, List<Pair<String, Fragment>> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position).second;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).first;
        }
    }
}
