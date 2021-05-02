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
movMeanLen = 51;
eda_scl = movmean(eda,[movMeanLen 0]); % EDA is sampled at 4 Hz; X samples backward
eda_scl = [eda_scl(ceil(movMeanLen/2):end);ones(floor(movMeanLen/2),1)*eda_scl(end)];
% disp([size(eda), size(eda_scl)])
eda_scr = eda - eda_scl;

if plotBool(1)
    minMax = [min(eda), max(eda)];
    figure()
    tiledlayout(2,1)
    ax1 = nexttile;
    hold on
    plot(time, eda,':','LineWidth',0.8)
    plot(time, eda_scl)
    for idx = 1:length(motionErrorTimePairs)
        area([motionErrorTimePairs(idx,1),motionErrorTimePairs(idx,2)],...
                [minMax(2)*1.1,minMax(2)*1.1],...
                minMax(1)*0.9, 'FaceAlpha',0.3,'EdgeColor', 'none','FaceColor',[0.8500, 0.3250, 0.0980])
    end
    hold off
     ylabel("Electric conductance [$\mu S$]"); hold on;

    legend(["EDA","SCL"])
    

    minMax = [min(eda_scr), max(eda_scr)];
    ax2 = nexttile;
    hold on
    plot(time, eda_scr)
    for idx = 1:length(motionErrorTimePairs)
        area([motionErrorTimePairs(idx,1),motionErrorTimePairs(idx,2)],...
                [minMax(2)*1.1,minMax(2)*1.1],...
                minMax(1)*0.9, 'FaceAlpha',0.3,'EdgeColor', 'none','FaceColor',[0.8500, 0.3250, 0.0980])
    end
    hold off
    ylabel("Electric conductance [$\mu S$]");

    legend("SCR")
    linkaxes([ax1 ax2],'x')
end
end

