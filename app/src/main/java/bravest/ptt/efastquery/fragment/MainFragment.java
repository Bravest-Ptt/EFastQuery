package bravest.ptt.efastquery.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.adapter.decoration.ArticleItemDecoration;
import bravest.ptt.efastquery.adapter.recycler.AbsAdapter;
import bravest.ptt.efastquery.adapter.recycler.ArticleAdapter;
import bravest.ptt.efastquery.entity.Article;
import bravest.ptt.efastquery.listeners.OnItemClickListener;
import bravest.ptt.efastquery.msciat.settings.IatSettings;
import bravest.ptt.efastquery.msciat.utils.JsonParser;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.utils.Utils;


/**
 * Created by root on 2/13/17.
 */

public class MainFragment extends BaseFragment implements OnItemClickListener{

    private static final String[] testUrl = {
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498504964778&di=efa569911fe92246164314395a9d8a0d&imgtype=0&src=http%3A%2F%2Fdynamic-image.yesky.com%2F1080x-%2FuploadImages%2F2013%2F217%2FW337J1423Z04.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498503937116&di=630ee2d9e43d1e393d2979d6fbb8a817&imgtype=0&src=http%3A%2F%2Fh5.86.cc%2Fwalls%2F20141224%2F1024x768_7a235953ae86b92.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498505019684&di=c7e31cce6ad744413b2f30f121742cc3&imgtype=0&src=http%3A%2F%2Fimg.meimi.cc%2Fmeinv%2F20170607%2Fd1r4kzxk3tp399.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498504181045&di=1c9bbf73b5da0d29d9e6d01b50c63172&imgtype=0&src=http%3A%2F%2Fimg.meimi.cc%2Fmeinv%2F20170607%2Ffsvvcerg21g405.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498504198966&di=d5ec98c8bcd1be37e28359351ec89bfb&imgtype=0&src=http%3A%2F%2F4493bz.1985t.com%2Fuploads%2Fallimg%2F140822%2F4-140R2114A0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498504220080&di=9a52804e20b37c4c1b9bcc1d418ede89&imgtype=0&src=http%3A%2F%2Fdynamic-image.yesky.com%2F1080x-%2FuploadImages%2F2013%2F319%2F41M3NBU0D319.jpg",
            "https://ss2.baidu.com/-vo3dSag_xI4khGko9WTAnF6hhy/image/h%3D220/sign=56dcc81d63600c33ef79d9ca2a4d5134/55e736d12f2eb938a6ef71fbdd628535e5dd6f3c.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498504249661&di=149d0770fc1161d48eec4925b4d28b67&imgtype=jpg&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2Fd1160924ab18972b399ddec2eecd7b899e510a17.jpg"
    };

    private Context mContext;

    private View mRootView;
    private RecyclerView mRecyclerView;

    private AbsAdapter mAdapter;
    private ArrayList<Article> mData = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mAdapter = new ArticleAdapter()
                .setContext(context)
                .setData(mData).setOnItemClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.article_recycler_view);
        mRefresher = (SwipeRefreshLayout) mRootView.findViewById(R.id.article_refresher);

        initRecyclerView();
        View test = mRootView.findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().sendBroadcast(new Intent("test"));
            }
        });
        return mRootView;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new ArticleItemDecoration(getResources().getColor(R.color.transparent)));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void refreshData() {
        Random random = new Random();
        int i = 0;
        for (String url : testUrl) {
            Article article = new Article();
            article.setImageUrl(url);
            article.setTitle("勇敢跳出自己的舒适区！7种方式挑战自己" + ++i);
            article.setTag("美女");
            article.setType(Article.TYPE_NORMAL);
            article.setCreatedAt(new Date(System.currentTimeMillis()));
            if (i == 1) {
                article.setType(Article.TYPE_BIG_IMAGE);
            } else {
                article.setType(Article.TYPE_NORMAL);
            }

            article.setReadCount(random.nextInt(100000));
            mData.add(article);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(View view, int position) {

    }
}
