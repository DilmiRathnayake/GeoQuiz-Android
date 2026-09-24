package uk.ac.wlv.geoquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Bundle keys used to survive rotation / recreation
    private static final String KEY_INDEX = "key_index";
    private static final String KEY_ANSWERED_COUNT = "key_answered_count";
    private static final String KEY_CORRECT_COUNT = "key_correct_count";
    private static final String KEY_QUESTION_ANSWERED_STATES = "key_question_answered_states";

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private Button mLanguageButton;
    private TextView mQuestionTextView;
    private TextView mProgressTextView;
    private TextView mSuccessRateTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
            new Question(R.string.question_space_1, true),
            new Question(R.string.question_space_2, false),
            new Question(R.string.question_space_3, true),
            new Question(R.string.question_space_4, false),
            new Question(R.string.question_space_5, false),
    };

    private int mCurrentIndex = 0;

    private int mQuestionsAnswered = 0;
    private int mQuestionsCorrect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQuestionTextView = findViewById(R.id.question_text_view);
        mProgressTextView = findViewById(R.id.progress_text_view);
        mSuccessRateTextView = findViewById(R.id.success_rate_text_view);
        mLanguageButton = findViewById(R.id.language_button);

        // Restore everything that would otherwise be lost when the
        // Activity is recreated (e.g. on a portrait <-> landscape rotation).
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mQuestionsAnswered = savedInstanceState.getInt(KEY_ANSWERED_COUNT, 0);
            mQuestionsCorrect = savedInstanceState.getInt(KEY_CORRECT_COUNT, 0);

            boolean[] answeredStates = savedInstanceState.getBooleanArray(KEY_QUESTION_ANSWERED_STATES);
            if (answeredStates != null) {
                for (int i = 0; i < mQuestionBank.length && i < answeredStates.length; i++) {
                    mQuestionBank[i].setAnswered(answeredStates[i]);
                }
            }
        }

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPreviousButton = findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLanguage();
            }
        });

        updateLanguageButtonLabel();
        updateQuestion();
        updateSuccessRate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putInt(KEY_ANSWERED_COUNT, mQuestionsAnswered);
        outState.putInt(KEY_CORRECT_COUNT, mQuestionsCorrect);

        boolean[] answeredStates = new boolean[mQuestionBank.length];
        for (int i = 0; i < mQuestionBank.length; i++) {
            answeredStates[i] = mQuestionBank[i].isAnswered();
        }
        outState.putBooleanArray(KEY_QUESTION_ANSWERED_STATES, answeredStates);
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        mProgressTextView.setText(getString(R.string.question_progress_format,
                mCurrentIndex + 1, mQuestionBank.length));

        // Anti-cheat: if this question was already answered, keep the
        // True/False buttons disabled so the user can't re-answer it to
        // inflate the success rate. They can still move Prev/Next freely.
        boolean alreadyAnswered = mQuestionBank[mCurrentIndex].isAnswered();
        setAnswerButtonsEnabled(!alreadyAnswered);
    }

    private void setAnswerButtonsEnabled(boolean enabled) {
        mTrueButton.setEnabled(enabled);
        mFalseButton.setEnabled(enabled);
    }

    private void checkAnswer(boolean userPressedTrue) {
        Question currentQuestion = mQuestionBank[mCurrentIndex];

        // Anti-cheat guard: a disabled button shouldn't fire this listener,
        // but this is a second line of defense against double counting.
        if (currentQuestion.isAnswered()) {
            Toast.makeText(this, R.string.already_answered_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean answerIsTrue = currentQuestion.isAnswerTrue();

        int messageResId;

        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
            mQuestionsCorrect++;
        } else {
            messageResId = R.string.incorrect_toast;
        }

        currentQuestion.setAnswered(true);
        mQuestionsAnswered++;

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();

        setAnswerButtonsEnabled(false);
        updateSuccessRate();
    }

    private void updateSuccessRate() {
        int successRate = 0;
        if (mQuestionsAnswered > 0) {
            successRate = (int) (((double) mQuestionsCorrect / mQuestionsAnswered) * 100);
        }
        mSuccessRateTextView.setText(getString(R.string.success_rate_format, successRate));
    }

    // ---- Language switching -------------------------------------------------

    /**
     * Flips the app's in-app language between English and Sinhala.
     * AppCompatDelegate.setApplicationLocales() recreates the Activity for
     * us (the same way a rotation does), and onSaveInstanceState /
     * onCreate above make sure the score and current question survive it.
     */
    private void toggleLanguage() {
        String currentLang = getCurrentLanguageTag();
        String newLang = "si".equals(currentLang) ? "en" : "si";
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newLang));
    }

    private String getCurrentLanguageTag() {
        LocaleListCompat appLocales = AppCompatDelegate.getApplicationLocales();
        if (!appLocales.isEmpty()) {
            return appLocales.get(0).getLanguage();
        }
        return Locale.getDefault().getLanguage();
    }

    private void updateLanguageButtonLabel() {
        // Button always offers to switch TO the other language.
        boolean isSinhala = "si".equals(getCurrentLanguageTag());
        mLanguageButton.setText(isSinhala ? R.string.language_button_en : R.string.language_button_si);
    }
}