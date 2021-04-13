clc; close all; clear all;
%% Import data from specific data folder
% Locate the folder containing the -csv files of interest
% dataFolderPath = "C:\Users\olive\Google Drive\sevenWeekProject\dataFromEmpaticaE4\data_malin_test";
% Note: If error occurs switch "/" (mac)  to "\" (windows) in mainDSP and readALlCsvFromFolder

root = fileparts(which(mfilename));
addpath(genpath(fullfile(root,'Data')))
addpath(genpath(fullfile(root,'Features')))
clear root

dataFolderList=dir("Data");
if ispc()
    dataFolderPath = pwd + "\Data\" + dataFolderList(4).name; % The number should be 3:end and notes the folders containing data
    [fileDataCell, modalityFieldNames] = readAllCsvFromFolder(dataFolderPath); % Optional input of folder path
elseif ismac()
    dataFolderPath = pwd + "/Data/" + dataFolderList(4).name; % The number should be 3:end and notes the folders containing data
    [fileDataCell, modalityFieldNames] = readAllCsvFromFolder(dataFolderPath); % Optional input of folder path
else
    [fileDataCell, modalityFieldNames] = readAllCsvFromFolder(); % Optional input of folder path
end


%% Plot E4 data
figure; hold on; grid on;
for j = 2:6 % modalities
    
    % Plot Accelerometer
    subplot(2,3,1)
    plot(fileDataCell{1}.time, fileDataCell{1}.x); hold on;
    plot(fileDataCell{1}.time, fileDataCell{1}.y); hold on;
    plot(fileDataCell{1}.time, fileDataCell{1}.z); hold on;
    title("Accelerometer"); xlabel("Time"); ylabel("Amplitude");
    % Plot everything else
    subplot(2,3,j)
    plot(fileDataCell{j}.time, fileDataCell{j}.amplitude)
    title(modalityFieldNames(j)); xlabel("Time"); ylabel("Amplitude");
    
end
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0 0 1 1]); % Create full size figure



%% Calculate HR from BVP
% [peakIndex, filtOut_BVP] = bvpPeakDetection(fileDataCell{2}.amplitude', 64);
% 
% figure()
% hold on
% plot(fileDataCell{2}.time, filtOut_BVP,'b')
% plot(fileDataCell{2}.time(peakIndex), filtOut_BVP(peakIndex),'bo')
% hold off

%% Noise removal stategy 1
% accFs = 32;
% secsToSmooth = 15;
% smoothingNbSamples = secsToSmooth*accFs;
% movMeanEnvDiffAcc = movmean(envelope(sqrt(fileDataCell{1}.x.^2 + fileDataCell{1}.y.^2 + fileDataCell{1}.z.^2)),smoothingNbSamples);
% 
% accThresh = 66.5;
% accBoolBelowThresh = movMeanEnvDiffAcc < accThresh;
% 
% 
% xlimits = [fileDataCell{1}.time(25*60*accFs+1),fileDataCell{1}.time(36*60*accFs+1)];
% 
% figure
% subplot(3,2,1)
% hold on
% plot(fileDataCell{1}.time + datenum(0,0,0,0,0,4), movMeanEnvDiffAcc)
% yline(accThresh)
% hold off
% ylabel('movMean(Env(length(acc)))')
% xlim(xlimits)
% 
% subplot(3,2,2)
% plot(fileDataCell{1}.time,fileDataCell{1}.x)
% ylabel('Acc x')
% xlim(xlimits)
% 
% subplot(3,2,3)
% hold on
% plot(fileDataCell{2}.time,fileDataCell{2}.amplitude)
% plot(fileDataCell{1}.time, accBoolBelowThresh*200)
% hold off
% ylabel('BVP')
% xlim(xlimits)
% 
% subplot(3,2,4)
% plot(fileDataCell{1}.time,fileDataCell{1}.y)
% ylabel('Acc y')
% xlim(xlimits)
% 
% subplot(3,2,5)
% plot(fileDataCell{2}.time(4:end),fileDataCell{2}.amplitude(4:end) .* interp(double(accBoolBelowThresh),2)')
% ylabel('Acc thresh filtered BVP')
% xlim(xlimits)
% 
% subplot(3,2,6)
% plot(fileDataCell{1}.time,fileDataCell{1}.z)
% ylabel('Acc z')
% xlim(xlimits)
% 
% 
% fileDataCell{2}.amplitude(4:end) = fileDataCell{2}.amplitude(4:end) .* interp(double(accBoolBelowThresh),2)';

%% Noise removal stategy 2
% filtObj_iir = designfilt('bandpassiir','FilterOrder',16, ...
%          'HalfPowerFrequency1',0.4,'HalfPowerFrequency2',4, ...
%          'SampleRate',64);
%      
% xStart = 3*10^3;
% xlimits = [xStart xStart+5*10^2];
% figure()
% subplot(3,1,1)
% hold on
% plot(fileDataCell{2}.amplitude)
% plot(envelope(fileDataCell{2}.amplitude),'--')
% ylabel("Raw BVP")
% xlim(xlimits)
% hold off
% 
% subplot(3,1,2)
% plot(filter(filtObj_iir,fileDataCell{2}.amplitude./ (envelope(fileDataCell{2}.amplitude))))
% ylabel("BVP/env(BVP)")
% xlim(xlimits)
% 
% 
% subplot(3,1,3)
% plot(filter(filtObj_iir,fileDataCell{2}.amplitude./ (envelope(fileDataCell{2}.amplitude.^2))))
% ylabel("BVP/env(BVP)^2")
% xlim(xlimits)

%% Noise removal stategy 3
% method = "LMS";
% stepSize = 0.6;
% forgettingFactor = 0.4;
% nbOfCoeff = 10;
% 
% xStart = 1*10^3+1;
% xlimits = [xStart xStart+5*10^2];
% 
% dataOffset = 13;%samples
% 
% BVPSnip = fileDataCell{2}.amplitude(xlimits(1):xlimits(2));
% interpolAcc = interp(sqrt(fileDataCell{1}.x.^2 + fileDataCell{1}.y.^2 + fileDataCell{1}.z.^2),2);
% accSnip = interpolAcc(xlimits(1)+dataOffset:xlimits(2)+dataOffset)';
% 
% Create filter obj
% lms = dsp.LMSFilter('Length',nbOfCoeff, 'Method','Normalized LMS', 'StepSize',stepSize,'LeakageFactor',forgettingFactor);%,'InitialConditions',coeffInitCondition);
% 
% Filtering
% [y,err,wtslms] = lms(accSnip'./mean(accSnip), BVPSnip');
% xPlotLim = [0,510];
% 
% figure();
% subplot(3,1,1)
% plot(BVPSnip)
% ylabel("BVP")
% xlim(xPlotLim)
% 
% subplot(3,1,2)
% plot(accSnip'./mean(accSnip))
% ylabel("ACC")
% xlim(xPlotLim)
% 
% subplot(3,1,3)
% hold on
% plot(BVPSnip)
% plot(err)
% hold off
% legend(["BVP orig","BVP Filt"])
% ylabel("BVP and err")
% xlim(xPlotLim)

%% Noise removal strategy 4
threshBVPEnv = 30; sampleMean = 64*4;
tempBVP = fileDataCell{2}.amplitude;
[fileDataCell{2}.amplitude, smoothedBvpEnvelope] = bvpMotionArtifactRemoval(threshBVPEnv,fileDataCell{2}.amplitude,sampleMean);



%% Plot of BVP motion artifact removal
figure()
tiledlayout(3,1)
ax1 = nexttile;
plot(fileDataCell{1}.time, sqrt(fileDataCell{1}.x.^2 + fileDataCell{1}.y.^2 + fileDataCell{1}.z.^2))
grid on
title("Accelerometer xyz-length - Unfiltered")
legend(["ACC length"])


ax2 = nexttile;
hold on; 
plot(fileDataCell{2}.time,tempBVP,'Color','#0072BD');
plot(fileDataCell{2}.time,smoothedBvpEnvelope,'--','Color','#FF0000');
yline(threshBVPEnv,'-.','Color','#D95319','LineWidth',1)
hold off
grid on
title("Blood volume pulse - Unfiltered")
legend(["BVP","Smoothed Envelope","Filter threshold"])


ax3 = nexttile;
hold on; 
plot(fileDataCell{2}.time,fileDataCell{2}.amplitude,'Color','#0072BD');
hold off
grid on
title("Blood volume pulse - Motion artifacts removed")
legend(["BVP"])

linkaxes([ax1 ax2],'x')
linkaxes([ax2 ax3],'xy')

%% Calculate HR from BVP
[peakIndex, filtOut_BVP] = bvpPeakDetection(fileDataCell{2}.amplitude', 64);
% 
% figure()
% hold on
% plot(fileDataCell{2}.time, filtOut_BVP,'b')
% plot(fileDataCell{2}.time(peakIndex), filtOut_BVP(peakIndex),'bo')
% hold off



%% Define features with sliding window

% Define length of sliding window
% Note! Remember to define window size according to sampling frequency
WINDOW_SIZE_ACC = 4; % 5 seconds
WINDOW_SIZE = 59; % 60 seconds

% Define features to calculate
% Note! Add Feature folder to path
acc_features = {@mean, @std};
bvp_features = {@mean, @std};
eda_features = {@mean, @std, @min, @max, @dynamic_range};
hr_features = {@mean, @std};
ibi_features = {@mean, @std};
temp_features = {@mean, @std, @min, @max, @dynamic_range};

% Calculate ACC features
features_acc_x = calc_features(fileDataCell{1}.x, WINDOW_SIZE_ACC, acc_features);
features_acc_y = calc_features(fileDataCell{1}.y, WINDOW_SIZE_ACC, acc_features);
features_acc_z = calc_features(fileDataCell{1}.z, WINDOW_SIZE_ACC, acc_features);

% Calculate BVP, EDA, HR, IBI and TEMP features
features_bvp = calc_features(fileDataCell{2}.amplitude, WINDOW_SIZE, bvp_features);
features_eda = calc_features(fileDataCell{3}.amplitude, WINDOW_SIZE, eda_features);
features_hr = calc_features(fileDataCell{4}.amplitude, WINDOW_SIZE, hr_features);
features_ibi = calc_features(fileDataCell{5}.amplitude, WINDOW_SIZE, ibi_features);
features_temp = calc_features(fileDataCell{6}.amplitude, WINDOW_SIZE, temp_features);




















