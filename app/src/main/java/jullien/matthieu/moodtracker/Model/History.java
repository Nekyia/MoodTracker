package jullien.matthieu.moodtracker.Model;

import java.util.Date;

// This class represents an entry in the history
public class History {
    private long mId;
    private int mMoodIndex;
    private Date mDate;
    private String mNote;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public int getMoodIndex() {
        return mMoodIndex;
    }

    public void setMoodIndex(int moodIndex) {
        mMoodIndex = moodIndex;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }
}
