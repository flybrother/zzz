package com.aeviou.back;

import java.util.ArrayList;

import android.util.Log;

/*
 * this class offers varies of frequency calibration algorithms
 */
public class FreqCalibrator {
	private static FreqCalibrator instance;
	
	public static FreqCalibrator getInstance() {
		return instance = (instance == null ? new FreqCalibrator() : instance);
	}
	
	public FreqCalibrator() {
	}

	public void calibrate(ArrayList<CandidateType> candidates, int index, PinyinHanzi hanzi) {
		if (index == 0)
			return;
		
		CandidateType ref;
		if (index == 1)
			ref = candidates.get(0);
		else if (index < 6)
			ref = candidates.get(index-2);
		else
			ref = candidates.get(2);
		
		int freqRef = hanzi.getFrequency(ref.getFreqOffset());
		int offsetCurFreq = candidates.get(index).getFreqOffset();
		Log.d("yzy", "freq "+offsetCurFreq);
		int freqCur = hanzi.getFrequency(offsetCurFreq);
		Log.d("yzy", ""+freqCur);
		freqCur = freqCur + (freqRef-freqCur)/2 + 1;
		Log.d("yzy", ""+freqCur);
		hanzi.setFrequency(offsetCurFreq, freqCur);
	}
}
