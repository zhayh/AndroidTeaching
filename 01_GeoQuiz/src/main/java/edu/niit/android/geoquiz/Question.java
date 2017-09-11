package edu.niit.android.geoquiz;

/**
 * Created by zhayh on 2017-2-28.
 */

public class Question {
    private int mTextResId;
    private boolean mAnswerTrue;

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public Question() {
    }

    public Question(int textResId, boolean answerTrue) {
        mAnswerTrue = answerTrue;
        mTextResId = textResId;
    }
}
