function [oneCycleHRV_final, oneCycleHRV2_time_resampled, motionErrorTimePairs] = calcHRFromPeaks(peakIndex,timeArray,fs, thrHRV, plotBool)
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

% Resample simple HRV 
rFs = 8;
[oneCycleHRV_secondStage_resampled,oneCycleHRV2_time_resampled] = resample(oneCycleHRV_secondStage,oneCycleHRV2_time,rFs,1,1,'spline');
oneCycleHRV_secondStage_resampled(oneCycleHRV_secondStage_resampled <= 0) = 0;

% Determine the sections of interest that needs removal from resampled HRV based on IBI from oneCycleHRV2_time
ibiStage3 = diff(oneCycleHRV2_time); %ms
ibiStage3_time = oneCycleHRV2_time(2:end);

missingDataTimeThr = 60/20;
boiStage4 = ibiStage3 >= seconds(missingDataTimeThr);

boiStage4_time_end = ibiStage3_time(boiStage4);
boiStage4_time_start = ibiStage3_time([boiStage4(2:end);false]);

motionErrorTimePairs = [boiStage4_time_start, boiStage4_time_end];

oneCycleHRV_final = oneCycleHRV_secondStage_resampled;

% Set missing data to NaN in resampled HRV 
for errorIdx = 1:size(motionErrorTimePairs,1)
    oneCycleHRV_final(isbetween(oneCycleHRV2_time_resampled, motionErrorTimePairs(errorIdx,1) ,motionErrorTimePairs(errorIdx,2))) = NaN;
end



%% Plotting
if plotBool(1)
    figure()
    tiledlayout(2,1)
    ax1 = nexttile;
    hold on
    plot(oneCycleHRV2_time_resampled, oneCycleHRV_secondStage_resampled,'--.')
    stem(boiStage4_time_start,repmat(150,length(boiStage4_time_start),1),'g')
    stem(boiStage4_time_end,repmat(150,length(boiStage4_time_end),1),'r')
    hold off
    ylabel("HRV [ms]")
    title("HRV threshold filtered resampled 8Hz")
    
    ax2 = nexttile;
    hold on
    plot(oneCycleHRV2_time_resampled, oneCycleHRV_final,'--.')
    stem(boiStage4_time_start,repmat(150,length(boiStage4_time_start),1),'g')
    stem(boiStage4_time_end,repmat(150,length(boiStage4_time_end),1),'r')
    hold off
    ylabel("HRV [ms]")
    title("HRV threshold filtered resampled 8Hz - Missing HRV set to NaN")

    linkaxes([ax1 ax2],'x')
end

if plotBool(2)
    figure()
    tiledlayout(2,1)
    ax1 = nexttile;
    hold on
    plot(ibiStage3_time,ibiStage3,'o')
    plot(ibiStage3_time,seconds(boiStage4*150),'-r')
    hold off
    title("HRV from filtered including missing data")
    
    ax2 = nexttile;
    plot(ibiStage3_time(2:end),diff(ibiStage3),'o')
    title("Diff of HRV from filtered including missing data")
    
    linkaxes([ax1 ax2],'x')
end


end

