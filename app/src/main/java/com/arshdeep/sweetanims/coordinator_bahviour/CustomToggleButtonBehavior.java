package com.arshdeep.sweetanims.coordinator_bahviour;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import android.util.AttributeSet;
import android.view.View;

public class CustomToggleButtonBehavior extends CoordinatorLayout.Behavior<CustomToggleButton> {

    public CustomToggleButtonBehavior() {
        super();
        //Used when the layout has a behavior attached via the Annotation;
    }

    public CustomToggleButtonBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Used when the layout has a behavior attached via xml (Within the xml file e.g.
        //<app:layout_behavior=".link.to.your.behavior">
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull CustomToggleButton child, @NonNull View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull CustomToggleButton child, @NonNull View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout){
            child.setChecked(true);
            child.setText("I see a Snackbar");
            return true;
        }
        return false;
    }

    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull CustomToggleButton child, @NonNull View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        if(dependency instanceof  Snackbar.SnackbarLayout) {
            child.setChecked(false);
            child.setText("Snackbar Vanished :(");
        }
    }
}
