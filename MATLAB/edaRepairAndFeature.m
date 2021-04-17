function [eda_scl,eda_scr] = edaRepairAndFeature(eda, time, motionErrorTimePairs, plotBool)
%EDAREPAIRANDFEATURE Summary of this function goes here
%   Detailed explanation goes here

% Remove motion errors
for errorIdx = 1:size(motionErrorTimePairs,1)
    eda(isbetween(time, motionErrorTimePairs(errorIdx,1) ,motionErrorTimePairs(errorIdx,2))) = NaN;
end

% Repair motion errors
eda = fillgaps(eda,200,150);

% Calculate EDA features
eda_scl = movmean(eda,[51 0]); % EDA is sampled at 4 Hz; X samples backward
eda_scr = eda - eda_scl;

if plotBool(1)
    figure()
    tiledlayout(2,1)
    ax1 = nexttile;
    hold on
    plot(time, eda,':','LineWidth',0.8)
    plot(time, eda_scl)
    stem(motionErrorTimePairs(:,1),repmat(2,length(motionErrorTimePairs),1),'g')
    stem(motionErrorTimePairs(:,2),repmat(2,length(motionErrorTimePairs),1),'r')
    hold off
    title('SCL'); xlabel("Time"); ylabel("Amplitude (\muS)"); hold on;

    legend(["EDA","SCL","Error start","Error end"])


    ax2 = nexttile;
    hold on
    plot(time, eda_scr)
    stem(motionErrorTimePairs(:,1),repmat(0.5,length(motionErrorTimePairs),1),'g')
    stem(motionErrorTimePairs(:,2),repmat(0.5,length(motionErrorTimePairs),1),'r')
    hold off
    title('SCR'); xlabel("Time"); ylabel("Amplitude");

    legend("SCR","Error start","Error end")
    linkaxes([ax1 ax2],'x')
end
end

