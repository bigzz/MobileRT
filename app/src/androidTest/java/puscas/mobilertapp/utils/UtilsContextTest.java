package puscas.mobilertapp.utils;

import android.content.Context;
import android.widget.Button;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import com.google.common.util.concurrent.Uninterruptibles;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import puscas.mobilertapp.DrawView;
import puscas.mobilertapp.MainActivity;
import puscas.mobilertapp.MainRenderer;
import puscas.mobilertapp.R;

/**
 * Helper class which contains helper methods that need the {@link Context} for the tests.
 */
public final class UtilsContextTest {

    /**
     * The {@link Logger} for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(UtilsContextTest.class.getName());

    /**
     * Private method to avoid instantiating this helper class.
     */
    private UtilsContextTest() {
        final String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        LOGGER.info(methodName);
    }

    /**
     * Helper method that waits until the Ray Tracing engine stops rendering
     * the scene.
     *
     * @param activity The {@link MainActivity} of MobileRT.
     */
    public static void waitUntilRenderingDone(final MainActivity activity) throws TimeoutException {
        final AtomicBoolean done = new AtomicBoolean(false);
        final long advanceSecs = 3L;

        final DrawView drawView = UtilsTest.getPrivateField(activity, "drawView");
        final MainRenderer renderer = drawView.getRenderer();
        final ViewInteraction renderButtonView =
            Espresso.onView(ViewMatchers.withId(R.id.renderButton));

        for (long currentTimeSecs = 0L; currentTimeSecs < 120L && !done.get();
             currentTimeSecs += advanceSecs) {
            Uninterruptibles.sleepUninterruptibly(advanceSecs, TimeUnit.SECONDS);

            renderButtonView.check((view, exception) -> {
                final Button renderButton = view.findViewById(R.id.renderButton);
                LOGGER.info("Checking if rendering done.");
                final String message = "Render button: " + renderButton.getText().toString();
                LOGGER.info(message);
                final String messageState = "State: " + renderer.getState().name();
                LOGGER.info(messageState);
                if (renderButton.getText().toString().equals(Constants.RENDER)
                    && renderer.getState() == State.IDLE) {
                    done.set(true);
                    LOGGER.info("Rendering done.");
                }
            });
        }

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
    public static void resetPickerValues(final Context context, final int scene) {
        LOGGER.info("resetPickerValues");

        final int numCores = puscas.mobilertapp.utils.UtilsContext.getNumOfCores(context);

        UtilsPickerTest.changePickerValue(ConstantsUI.PICKER_SCENE, R.id.pickerScene, scene);
        UtilsPickerTest.changePickerValue(ConstantsUI.PICKER_THREADS, R.id.pickerThreads, numCores);
        UtilsPickerTest.changePickerValue(ConstantsUI.PICKER_SIZE, R.id.pickerSize, 8);
        UtilsPickerTest
            .changePickerValue(ConstantsUI.PICKER_SAMPLES_PIXEL, R.id.pickerSamplesPixel, 1);
        UtilsPickerTest
            .changePickerValue(ConstantsUI.PICKER_SAMPLES_LIGHT, R.id.pickerSamplesLight, 1);
        UtilsPickerTest
            .changePickerValue(ConstantsUI.PICKER_ACCELERATOR, R.id.pickerAccelerator, 3);
        UtilsPickerTest.changePickerValue(ConstantsUI.PICKER_SHADER, R.id.pickerShader, 2);
    }

}
