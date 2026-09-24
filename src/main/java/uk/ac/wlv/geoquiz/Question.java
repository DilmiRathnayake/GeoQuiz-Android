package uk.ac.wlv.geoquiz;

public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;

    // NEW: tracks whether this question has already been answered once.
    // Used to stop the user from tapping True/False repeatedly on the
    // same question just to "farm" a correct answer (anti-cheat).
    private boolean mIsAnswered;

    public Question(int textResId, boolean answerTrue) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public boolean isAnswered() {
        return mIsAnswered;
    }

    public void setAnswered(boolean answered) {
        mIsAnswered = answered;
    }
}