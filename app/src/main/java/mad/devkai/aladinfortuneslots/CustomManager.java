package mad.devkai.aladinfortuneslots;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class CustomManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public CustomManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {

        isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {

        return isScrollEnabled && super.canScrollVertically();
    }
}
