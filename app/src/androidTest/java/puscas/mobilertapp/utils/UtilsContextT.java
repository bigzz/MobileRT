package puscas.mobilertapp.utils;

import android.content.Context;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.common.util.concurrent.Uninterruptibles;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import puscas.mobilertapp.DrawView;
import puscas.mobilertapp.MainActivity;
import puscas.mobilertapp.MainRenderer;
import puscas.mobilertapp.R;
import puscas.mobilertapp.constants.Constants;
import puscas.mobilertapp.constants.ConstantsUI;
import puscas.mobilertapp.constants.State;

/**
 * Helper class which contains helper methods that need the {@link Context} for the tests.
 */
@UtilityClass
@Log
public final class UtilsContextT {

    /**
     * Helper method that waits until the Ray Tracing engine stops rendering
     * the scene.
     *
     * @param activity The {@link MainActivity} of MobileRT.
     * @throws TimeoutException If the Ray Tracing engine didn't stop rendering the scene.
     */
    public static void waitUntilRenderingDone(@NonNull final MainActivity activity)
        throws TimeoutException {
        log.info("waitUntilRenderingDone start");
        final AtomicBoolean done = new AtomicBoolean(false);
        final long advanceSecs = 3L;

        final DrawView drawView = UtilsT.getPrivateField(activity, "drawView");
        final MainRenderer renderer = drawView.getRenderer();
        final ViewInteraction renderButtonView =
            Espresso.onView(ViewMatchers.withId(R.id.renderButton));

        for (long currentTimeSecs = 0L; currentTimeSecs < 300L && !done.get();
             currentTimeSecs += advanceSecs) {
            Uninterruptibles.sleepUninterruptibly(advanceSecs, TimeUnit.SECONDS);

            renderButtonView.check((view, exception) -> {
                final Button renderButton = view.findViewById(R.id.renderButton);
                log.info("Checking if rendering done.");
                if (renderButton.getText().toString().equals(Constants.RENDER)
                    && renderer.getState() == State.IDLE) {
                    done.set(true);
                    log.info("Rendering done.");
                }
            });
        }

        log.info("waitUntilRenderingDone finished");
        if (!done.get()) {
            throw new TimeoutException("The Ray Tracing engine didn't stop rendering the scene.");
        }
    }

    /**
     * Helper method that resets the {@link android.widget.NumberPicker}s values
     * in the UI to some predefined values.
     *
     * @param context The {@link Context} of the application.
     * @param scene   The id of the scene to set.
     */
    public static void resetPickerValues(@NonNull final Context context, final int scene) {
        log.info("resetPickerValues");

        final int numCores = UtilsContext.getNumOfCores(context);

        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SCENE, R.id.pickerScene, scene);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_THREADS, R.id.pickerThreads, numCores);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SIZE, R.id.pickerSize, 8);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SAMPLES_PIXEL, R.id.pickerSamplesPixel, 1);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SAMPLES_LIGHT, R.id.pickerSamplesLight, 1);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_ACCELERATOR, R.id.pickerAccelerator, 3);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SHADER, R.id.pickerShader, 2);
    }

}
