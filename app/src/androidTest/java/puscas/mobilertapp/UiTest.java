package puscas.mobilertapp;

import static puscas.mobilertapp.ConstantsAndroidTests.BUTTON_MESSAGE;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.common.collect.ImmutableList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runners.MethodSorters;

import java.util.List;

import java8.util.stream.IntStreams;
import lombok.extern.java.Log;
import puscas.mobilertapp.constants.Accelerator;
import puscas.mobilertapp.constants.Constants;
import puscas.mobilertapp.constants.ConstantsUI;
import puscas.mobilertapp.constants.Scene;
import puscas.mobilertapp.constants.Shader;
import puscas.mobilertapp.utils.UtilsContext;
import puscas.mobilertapp.utils.UtilsContextT;
import puscas.mobilertapp.utils.UtilsPickerT;
import puscas.mobilertapp.utils.UtilsT;

/**
 * The test suite for the User Interface.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Log
public final class UiTest extends AbstractTest {

    /**
     * The current index of the scene in the {@link NumberPicker}.
     */
    private int counterScene = 0;

    /**
     * The current index of the accelerator in the {@link NumberPicker}.
     */
    private int counterAccelerator = 0;

    /**
     * The current index of the shader in the {@link NumberPicker}.
     */
    private int counterShader = 0;

    /**
     * The current index of the resolution in the {@link NumberPicker}.
     */
    private int counterResolution = 0;

    /**
     * The current index of the number of samples per light in the {@link NumberPicker}.
     */
    private int counterSpl = 0;

    /**
     * The current index of the number of samples per pixel in the {@link NumberPicker}.
     */
    private int counterSpp = 0;

    /**
     * The current index of the number of threads in the {@link NumberPicker}.
     */
    private int counterThreads = 0;

    /**
     * Helper method which tests clicking the preview {@link CheckBox}.
     *
     * @param expectedValue The expected value for the {@link CheckBox}.
     */
    private static void clickPreviewCheckBox(final boolean expectedValue) {
        Espresso.onView(ViewMatchers.withId(R.id.preview))
            .check((view, exception) ->
                assertPreviewCheckBox(view, !expectedValue)
            )
            .perform(ViewActions.click())
            .check((view, exception) ->
                assertPreviewCheckBox(view, expectedValue)
            );
    }

    /**
     * Asserts the {@link CheckBox} expected value.
     *
     * @param view          The {@link View}.
     * @param expectedValue The expected value for the {@link CheckBox}.
     */
    private static void assertPreviewCheckBox(@NonNull final View view,
                                              final boolean expectedValue) {
        final CheckBox checkbox = view.findViewById(R.id.preview);
        Assertions.assertEquals(Constants.PREVIEW, checkbox.getText().toString(),
            Constants.CHECK_BOX_MESSAGE);
        Assertions.assertEquals(expectedValue, checkbox.isChecked(),
            "Check box has not the expected value");
    }

    /**
     * Helper method which tests the range of the {@link NumberPicker} in the UI.
     *
     * @param numCores The number of CPU cores in the system.
     */
    private static void assertPickerNumbers(final int numCores) {
        IntStreams.rangeClosed(0, 2).forEach(value ->
            UtilsPickerT.changePickerValue(ConstantsUI.PICKER_ACCELERATOR, R.id.pickerAccelerator, value)
        );
        IntStreams.rangeClosed(1, 100).forEach(value ->
            UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SAMPLES_LIGHT, R.id.pickerSamplesLight, value)
        );
        IntStreams.rangeClosed(0, 6).forEach(value ->
            UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SCENE, R.id.pickerScene, value)
        );
        IntStreams.rangeClosed(0, 4).forEach(value ->
            UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SHADER, R.id.pickerShader, value)
        );
        IntStreams.rangeClosed(1, numCores).forEach(value ->
            UtilsPickerT.changePickerValue(ConstantsUI.PICKER_THREADS, R.id.pickerThreads, value)
        );
        IntStreams.rangeClosed(1, 99).forEach(value ->
            UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SAMPLES_PIXEL, R.id.pickerSamplesPixel, value)
        );
        IntStreams.rangeClosed(1, 8).forEach(value ->
            UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SIZE, R.id.pickerSize, value)
        );
    }

    /**
     * Tests changing all the {@link NumberPicker} and clicking the render
     * {@link Button} few times.
     */
    @Test(timeout = 20L * 60L * 1000L)
    public void testUI() {
        UtilsT.assertRenderButtonText(Constants.RENDER);

        final int numCores = UtilsContext.getNumOfCores(this.activity);
        assertClickRenderButton(1, numCores);
        assertPickerNumbers(numCores);
        clickPreviewCheckBox(false);
    }

    /**
     * Tests clicking the render {@link Button} many times without preview.
     */
    @Test(timeout = 20L * 60L * 1000L)
    public void testClickRenderButtonManyTimesWithoutPreview() {
        clickPreviewCheckBox(false);

        final int numCores = UtilsContext.getNumOfCores(this.activity);
        assertClickRenderButton(5, numCores);
    }

    /**
     * Tests clicking the render {@link Button} many times with preview.
     */
    @Test(timeout = 30L * 60L * 1000L)
    public void testClickRenderButtonManyTimesWithPreview() {
        clickPreviewCheckBox(false);
        clickPreviewCheckBox(true);

        final int numCores = UtilsContext.getNumOfCores(this.activity);
        assertClickRenderButton(5, numCores);
    }

    /**
     * Tests clicking the render {@link Button} with a long press.
     * It is expected for the {@link android.app.Activity} to restart.
     */
    @Test(timeout = 60L * 1000L)
    public void testClickRenderButtonLongPress() {
        Espresso.onView(ViewMatchers.withId(R.id.renderButton))
            .perform(new ViewActionButton(Constants.RENDER, true))
            .check((view, exception) -> {
                final Button button = (Button) view;
                Assertions.assertEquals(Constants.RENDER, button.getText().toString(), BUTTON_MESSAGE);
            });
        UtilsT.testStateAndBitmap(true);
    }

    /**
     * Helper method which tests clicking the render {@link Button}.
     *
     * @param repetitions The number of repetitions.
     * @param numCores    The number of CPU cores in the system.
     */
    private void assertClickRenderButton(final int repetitions, final int numCores) {
        UtilsContextT.resetPickerValues(this.activity, Scene.TEST_INTERNAL_STORAGE.ordinal());

        final List<String> buttonTextList = ImmutableList.of(Constants.STOP, Constants.RENDER);
        IntStreams.range(0, buttonTextList.size() * repetitions).forEach(currentIndex -> {

            final String message = "currentIndex = " + currentIndex;
            log.info(message);

            incrementCountersAndUpdatePickers(numCores);

            final int expectedIndexOld = currentIndex > 0? (currentIndex - 1) % buttonTextList.size() : 1;
            final String expectedButtonTextOld = buttonTextList.get(expectedIndexOld);
            final ViewInteraction viewInteraction = Espresso.onView(ViewMatchers.withId(R.id.renderButton));
            final int expectedIndex = currentIndex % buttonTextList.size();
            final String expectedButtonText = buttonTextList.get(expectedIndex);

            UtilsT.assertRenderButtonText(expectedButtonTextOld);

            viewInteraction.perform(new ViewActionButton(expectedButtonText, false));

            UtilsT.assertRenderButtonText(expectedButtonText);
        });
    }

    /**
     * Helper method that increments all the fields' counters and updates all
     * the {@link NumberPicker}s in the UI with the current values.
     *
     * @param numCores The number of CPU cores in the system.
     */
    private void incrementCountersAndUpdatePickers(final int numCores) {
        final int finalCounterScene = Math.min(this.counterScene % Scene.values().length, 3);
        final int finalCounterAccelerator = Math.max(this.counterAccelerator % Accelerator.values().length, 1);
        final int finalCounterShader = Math.max(this.counterShader % Shader.values().length, 0);
        final int finalCounterSpp = Math.max(this.counterSpp % 99, 90);
        final int finalCounterSpl = Math.max(this.counterSpl % 100, 1);
        final int finalCounterThreads = Math.max(this.counterThreads % numCores, 1);
        final int finalCounterSize = Math.max(this.counterResolution % 9, 1);

        incrementCounters();

        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SCENE, R.id.pickerScene, finalCounterScene);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_ACCELERATOR, R.id.pickerAccelerator, finalCounterAccelerator);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SHADER, R.id.pickerShader, finalCounterShader);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SAMPLES_PIXEL, R.id.pickerSamplesPixel, finalCounterSpp);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SAMPLES_LIGHT, R.id.pickerSamplesLight, finalCounterSpl);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_THREADS, R.id.pickerThreads, finalCounterThreads);
        UtilsPickerT.changePickerValue(ConstantsUI.PICKER_SIZE, R.id.pickerSize, finalCounterSize);
    }

    /**
     * Helper method that increments all the fields' counters.
     */
    private void incrementCounters() {
        this.counterScene++;
        this.counterAccelerator++;
        this.counterShader++;
        this.counterSpp++;
        this.counterSpl++;
        this.counterResolution++;
        this.counterThreads++;
    }

}
