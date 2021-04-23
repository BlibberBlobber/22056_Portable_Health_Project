function [oneCycleHRV_secondStage, oneCycleHRV2_time] = calcHRFromPeaks(peakIndex,timeArray,fs, thrHRV, plotBool)
%CALCHRFROMPEAKS Summary of this function goes here
%   Detailed explanation goes here
interBeatInterval = diff(peakIndex)/fs;
ibiTime = timeArray(2:end);

% Filter IBI based on HR interval from 20 to 240
beatsOfInterestAfterFirstStage = interBeatInterval >= 60/240 & interBeatInterval <= 60/20;
boiTime = ibiTime(beatsOfInterestAfterFirstStage);

% Calculate first unfiltered HRV from abs of the 1st order peak diff
oneCycleHRV = abs(diff(interBeatInterval(beatsOfInterestAfterFirstStage))).*10^3; % ms
oneCycleHRV_time = boiTime(2:end);

% Filter simple HRV based on realistic upper value
beatsOfInterestAfterSecondStage = oneCycleHRV <= thrHRV;
oneCycleHRV_secondStage = oneCycleHRV(beatsOfInterestAfterSecondStage);
oneCycleHRV2_time = oneCycleHRV_time(beatsOfInterestAfterSecondStage);

% Determine the sections of interest that needs repair based on IBI from oneCycleHRV2_time
ibiStage3 = diff(oneCycleHRV2_time); %ms
ibiStage3_time = oneCycleHRV2_time(2:end);


missingDataTimeThr = 60/20;
boiStage4 = ibiStage3 >= seconds(missingDataTimeThr);


boiStage4(1) = false;

boiStage4_time_end = ibiStage3_time(boiStage4);
boiStage4_time_start = ibiStage3_time([boiStage4(2:end);false]);


motionErrorTimePairs = [boiStage4_time_start, boiStage4_time_end];

oneCycleHRV_final = oneCycleHRV_secondStage_resampled;

oneCycleHRV_final(oneCycleHRV_final>250) = 250;

oneCycleHRV_final = movmean(oneCycleHRV_final,48);

% Calculate HR
stage4HR = seconds(60)./ibiStage3(~boiStage4); 
stage4HR_time = ibiStage3_time(~boiStage4); 

% Resample HR
[stage4HR_resampled,stage4HR_time_resampled] = resample(stage4HR,stage4HR_time,rFs,1,1);

stage4HR_resampled = movmean(stage4HR_resampled,48);



% Set missing data to NaN in resampled HRV and HR
for errorIdx = 1:size(motionErrorTimePairs,1)
    oneCycleHRV_final(isbetween(oneCycleHRV2_time_resampled, motionErrorTimePairs(errorIdx,1) ,motionErrorTimePairs(errorIdx,2))) = NaN;
    stage4HR_resampled(isbetween(stage4HR_time_resampled, motionErrorTimePairs(errorIdx,1) ,motionErrorTimePairs(errorIdx,2))) = NaN;
end

oneCycleHRV_fillgaps = fillgaps(oneCycleHRV_final,200,150);
oneCycleHRV_fillgaps(oneCycleHRV_fillgaps>250) = 250;
oneCycleHRV_fillgaps(oneCycleHRV_fillgaps<0) = 0;

stage4HR_resampled_fillgaps = fillgaps(stage4HR_resampled,200,150);



%% Plotting
if plotBool(1)
    figure()
    tiledlayout(2,1)
    ax1 = nexttile;
    plot(oneCycleHRV_time, oneCycleHRV,'--.')
    ylabel("HRV [ms]")
    title("HR Interval filtered")
    ax2 = nexttile;
    plot(oneCycleHRV2_time, oneCycleHRV_secondStage,'--.')
    ylabel("HRV [ms]")
    title("HRV threshold filtered")
    hold off

    linkaxes([ax1 ax2],'x')
end

if plotBool(2)
    figure()
    plot(ibiStage3_time,ibiStage3)
    ylabel("HRV [ms]")
    title("HRV from filtered including missing data")
end


end

