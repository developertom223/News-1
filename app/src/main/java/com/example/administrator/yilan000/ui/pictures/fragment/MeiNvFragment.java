package com.example.administrator.yilan000.ui.pictures.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.yilan000.R;
import com.example.administrator.yilan000.bean.MeiNvGson;
import com.example.administrator.yilan000.ui.base.BaseFragent;
import com.example.administrator.yilan000.ui.pictures.PictureDescribeActivity;
import com.example.administrator.yilan000.ui.pictures.adapter.ImageAdapter;
import com.example.administrator.yilan000.ui.pictures.contrant.MeiNvContract;
import com.example.administrator.yilan000.ui.pictures.presenter.MeiNvPresenter;
import com.example.administrator.yilan000.util.PixUtil;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/24.
 */

public class MeiNvFragment extends BaseFragent implements MeiNvContract.View {
    private ImageAdapter adapter;
    private int page = 1;
    private MeiNvPresenter mMeiNvPresenter;
    private boolean isViewPrepared; // 标识fragment视图已经初始化完毕
    private boolean hasFetchData; // 标识已经触发过懒加载数据

    public static MeiNvFragment newInstance() {
        MeiNvFragment fragment = new MeiNvFragment();
        return fragment;
    }
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meizi_fragment, container, false);

        ButterKnife.bind(this, view);
        mMeiNvPresenter=new MeiNvPresenter(this,getContext());
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new ImageAdapter(getActivity()));


        //添加边框
        SpaceDecoration itemDecoration = new SpaceDecoration((int) PixUtil.convertDpToPixel(10, getContext()));
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        itemDecoration.setPaddingHeaderFooter(false);
        recyclerView.addItemDecoration(itemDecoration);



        //更多加载
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                //
                Log.e("更多","更多");
                mMeiNvPresenter.loadData(page);
                page=page+1;
            }

            @Override
            public void onMoreClick() {

            }
        });

        adapter.setNoMore(R.layout.view_nomore);

        //写刷新事件
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page=1;
                        adapter.clear();
                        Log.e("刷新", "刷新");
                        mMeiNvPresenter.loadData(page);
                    }
                }, 1000);
            }
        });

        //点击事件
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ArrayList<String> data = new ArrayList<String>();
                data.add(adapter.getAllData().get(position).getPicUrl());
                data.add(adapter.getAllData().get(position).getUrl());
                Intent intent = new Intent(getActivity(), PictureDescribeActivity.class);
                //用Bundle携带数据
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("data", data);
                intent.putExtras(bundle);
                startActivity(intent);
            }


        });


        isViewPrepared = true;


        return view;
    }


    @Override
    public void returnData(List<MeiNvGson.NewslistBean> datas) {

        adapter.addAll(datas);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void lazyFetchDataIfPrepared() {
        if (isViewPrepared && getUserVisibleHint() && !hasFetchData) {
            lazyFetchData();
            hasFetchData = true;
        }
    }

    protected void lazyFetchData() {
        mMeiNvPresenter.loadData(page);
        page=page+1;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //视图销毁 数据设置为空
        hasFetchData=false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        lazyFetchDataIfPrepared();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hasFetchData = false;
        isViewPrepared = false;
    }
}
