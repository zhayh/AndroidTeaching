package edu.niit.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    @BindView(R.id.true_button) Button mTrueBtn;

    private Question[] mQuestions = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Log.d(TAG, "OnCreate called");

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }
//        Button mTrueBtn = (Button) findViewById(R.id.true_button);
//        Button mFalseBtn = null;// = (Button) findViewById(R.id.false_button);
        Button mNextBtn = (Button) findViewById(R.id.next_button);
        Button mPrevBtn = (Button) findViewById(R.id.prev_button);
        Button mFalseBtn = (Button) findViewById(R.id.false_button);

        final TextView mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setText(mQuestions[mCurrentIndex].getTextResId());

        mTrueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });

        mFalseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsCheater = false;
                mCurrentIndex = (mCurrentIndex + 1) % mQuestions.length;
                mQuestionTextView.setText(mQuestions[mCurrentIndex].getTextResId());
            }
        });

        mPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsCheater = false;
                mCurrentIndex = (mCurrentIndex - 1) % mQuestions.length;
                mQuestionTextView.setText(mQuestions[mCurrentIndex].getTextResId());
            }
        });

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsCheater = false;
                mCurrentIndex = (mCurrentIndex + 1) % mQuestions.length;
                mQuestionTextView.setText(mQuestions[mCurrentIndex].getTextResId());
            }
        });

        Button mCheatBtn = (Button) findViewById(R.id.cheat_button);
        mCheatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean answerIsTrue = mQuestions[mCurrentIndex].isAnswerTrue();
                startActivityForResult(CheatActivity.newIntent(QuizActivity.this, answerIsTrue), REQUEST_CODE_CHEAT);
            }
        });
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestions[mCurrentIndex].isAnswerTrue();

        int msgResId = 0;
        if(mIsCheater) {
            msgResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                msgResId = R.string.correct_toast;
            } else {
                msgResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(QuizActivity.this, msgResId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT) {
            if(data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

}
