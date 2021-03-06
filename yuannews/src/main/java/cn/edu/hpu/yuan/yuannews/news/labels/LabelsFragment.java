package cn.edu.hpu.yuan.yuannews.news.labels;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.List;

import javax.inject.Inject;

import cn.edu.hpu.yuan.yuancore.util.LogUtil;
import cn.edu.hpu.yuan.yuannews.R;
import cn.edu.hpu.yuan.yuannews.databinding.LabelFragmentBinding;
import cn.edu.hpu.yuan.yuannews.databinding.LabelsFragmentBinding;
import cn.edu.hpu.yuan.yuannews.main.app.BaseApplication;
import cn.edu.hpu.yuan.yuannews.main.base.NorbalBackFragment;
import cn.edu.hpu.yuan.yuannews.main.data.model.basevo.TasteVo;
import cn.edu.hpu.yuan.yuannews.news.labels.adapter.LabelsAdapter;
import cn.edu.hpu.yuan.yuannews.user.label.adapter.LabelIfoAdapter;
import cn.edu.hpu.yuan.yuannews.user.login.LoginActivity;

/**
 * Created by yuan on 16-5-13.
 */
public class LabelsFragment extends NorbalBackFragment implements LabelsContancts.LabelsContanctsView,LabelsAdapter.OnDeleteItemClick{


    @Inject
    protected LabelsContancts.LabelsContanctsPresenter labelsContanctsPresenter;

    @Inject
    protected LabelsAdapter labelsAdapter;

    private LabelsFragmentBinding binding;

    private SwipeRefreshLayout.OnRefreshListener swipeOnRefresh;

    private String label;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.labels_fragment,container,false);
        return binding.getRoot();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        labelsAdapter.setOnDeleteItemClick(this);
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    private void initSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary
                ,R.color.colorPrimaryDark
                ,R.color.colorAccent);
        binding.swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        binding.swipeRefreshLayout.setProgressViewEndTarget(true,500);//进度条位置
        binding.swipeRefreshLayout.setOnRefreshListener(swipeOnRefresh=new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onloadInitData();
            }
        });

        binding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                binding.swipeRefreshLayout.setRefreshing(true);
            }
        });
        swipeOnRefresh.onRefresh();
    }

    /**
     *  初始化RecyclerView
     */
    private void initRecyclerView() {
        binding.recyclerView.setAdapter(labelsAdapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                int lastVisibleItem = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
                int totalItemCount = lm.getItemCount();
                //最后一项
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem == totalItemCount - 1) {
                    labelsContanctsPresenter.nextgetTasteData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    protected void onloadInitData() {
        labelsAdapter.initTasteVo();
        labelsContanctsPresenter.initgetTasteData();
    }

    @Override
    protected void initComponent() {
       DaggerLabelsComponent.builder()
               .labelsModule(new LabelsModule(this,getContext()))
               .build()
               .injectLabelsFragment(this);
    }

    @Override
    public void showDialog() {
        showSnack("加载中...");
    }

    @Override
    public void showLabelsData(List<String> tastes) {

        if(binding.swipeRefreshLayout.isRefreshing()){
            binding.swipeRefreshLayout.setRefreshing(false);
        }
        if(tastes.size()==0){
            showSnack("没有更多数据了");
            return;
        }
        labelsAdapter.addTasteVo(tastes);
        labelsAdapter.notifyDataSetChanged();
        initNodataView();
    }

    private void initNodataView() {
        if(labelsAdapter.getItemCount()==0){
            binding.noData.setVisibility(View.VISIBLE);
        }else {
            binding.noData.setVisibility(View.GONE);
        }
    }

    @Override
    public void showErrorMsg(String msg) {
        showSnack(msg);
    }

    @Override
    public void addTasteSuccess() {
        labelsAdapter.removeTasteVo(label);
        labelsAdapter.notifyDataSetChanged();
        showSnack("关注成功");
    }

    private void showSnack(String msg){
        Snackbar.make(binding.labelsFragmentPage,msg,Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackAction(String msg){
        Snackbar.make(binding.labelsFragmentPage,msg,Snackbar.LENGTH_SHORT)
                .setAction("点我登陆", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //登陆
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                }).setActionTextColor(Color.YELLOW).show();
    }

    @Override
    public void onDelete(String tasteVo, int position) {
        //这个是点击关注后，移除的详情标签
        if(BaseApplication.newsAPIShared.getSharedUserID()>0){
            //可以关注
            labelsContanctsPresenter.userAddTaste(tasteVo);
            this.label=tasteVo;
        }else{
            showSnackAction("你还未登陆");
        }
    }
}
