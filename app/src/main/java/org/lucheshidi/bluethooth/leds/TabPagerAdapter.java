package org.lucheshidi.bluethooth.leds;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabPagerAdapter extends FragmentStateAdapter {
    private static final int TAB_COUNT = 6;

    public TabPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 每个选项卡对应的Fragment
        switch (position) {
            case 0:
                return new CCTFragment();
            case 1:
                return new RGBFragment();
            case 2:
                return new HSIFragment();
            case 3:
                return new PaperFragment();
            case 4:
                return new SceneFragment();
            case 5:
                return new AnimationFragment();
            default:
                return new CCTFragment();
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}