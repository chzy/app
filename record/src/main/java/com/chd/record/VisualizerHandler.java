package com.chd.record;

import com.cleveroad.audiovisualization.DbmHandler;

/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-03-03
 * Time: 14:51
 * describe:
 */
public class VisualizerHandler extends DbmHandler<Float> {

	@Override
	protected void onDataReceivedImpl(Float amplitude, int layersCount, float[] dBmArray, float[] ampsArray) {
		amplitude = amplitude / 100;
		if(amplitude <= 0.5){
			amplitude = 0.0f;
		} else if(amplitude > 0.5 && amplitude <= 0.6){
			amplitude = 0.2f;
		} else if(amplitude > 0.6 && amplitude <= 0.7){
			amplitude = 0.6f;
		} else if(amplitude > 0.7){
			amplitude = 1f;
		}
		try {
			dBmArray[0] = amplitude;
			ampsArray[0] = amplitude;
		} catch (Exception e){ }
	}

	public void stop() {
		try {
			calmDownAndStopRendering();
		} catch (Exception e){ }
	}
}
