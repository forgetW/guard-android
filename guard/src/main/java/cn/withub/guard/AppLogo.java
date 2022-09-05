package cn.withub.guard;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

<<<<<<< HEAD:guard/src/main/java/cn/withub/guard/AppLogo.java
import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.data.Config;
import cn.withub.guard.data.ImageLoader;
=======
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.ImageLoader;
>>>>>>> authing/master:guard/src/main/java/cn/authing/guard/AppLogo.java

public class AppLogo extends androidx.appcompat.widget.AppCompatImageView {

    public AppLogo(Context context) {
        this(context, null);
    }

    public AppLogo(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppLogo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("AppLogo");

        Authing.getPublicConfig((config)->{
            if (config == null) {
                return;
            }

            String url = config.getLogo();
            if (url != null) {
                ImageLoader.with(context).load(url).into(this);
            }
        });
    }
}
