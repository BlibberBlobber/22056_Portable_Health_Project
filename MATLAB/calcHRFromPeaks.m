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

